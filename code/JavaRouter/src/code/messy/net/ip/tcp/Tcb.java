package code.messy.net.ip.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.python.modules.synchronize;

import code.messy.net.Dump;
import code.messy.net.ip.IpHeader;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.Tuple;
import code.messy.net.ip.route.LocalSubnet;

public class Tcb {
	// TCP RFC 3.2
	int SND_UNA, SND_NXT, SND_WND, SND_UP, SND_WL1, SND_WL2, ISS;
	int RCV_NXT, RCV_WND, RCV_UP, IRS;
	
	// TODO may not need here TCP RFC 3.2
	int SEG_SEQ, SEG_ACK, SEG_LEN, SEG_WND, SEG_UP, SEG_PRC;
	
	// TCP RFC 3.2
	public enum State { LISTEN, SYN_SENT, SYN_RCVD, ESTABLISHED, FIN_WAIT1, FIN_WAIT2, CLOSE_WAIT, CLOSING, 
		LAST_ACK, TIME_WAIT, CLOSED };

	// TCP RFC 3.9 Event Processing
	public enum Event { OPEN, SEND, RECEIVE, CLOSE, ABORT, STATUS, SEGMENT_ARRIVES, USER_TIMEOUT, RETRANSMISSION_TIMEOUT, TIME_WAIT_TIMEOUT };
		
	OutgoingSegment out;
	LocalSubnet subnet;
    InetAddress srcAddress, dstAddress;
    int srcPort, dstPort;
    
    RfcSocket foreignSocket, localSocket;
    
    State currentState;
    ByteBufferQueue outgoingQueue = new ByteBufferQueue();
    
    // TODO replace ring buffer??
    ByteBufferQueue incomingQueue = new ByteBufferQueue();
    
    // TODO window size configurable
    private RingBuffer buffer = new RingBuffer(1024);
    public RingBuffer getBuffer() {
    	return buffer;
    }

	public Tcb(InetAddress srcAddress, int srcPort, InetAddress dstAddress, int dstPort, boolean active) throws IOException {
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.srcAddress = srcAddress;
		
		if (active) {
			
		}
		else {
			// passive
			currentState = State.LISTEN;
		}
	}

	private void init() {
	}
	
	// TODO passive
	// TODO foreign sock unspecified
	public Tcb(Tuple tuple) throws IOException {
		// Open call at CLOSED STATE
		init();
		
		srcAddress = tuple.getSrcAddress();
		srcPort = tuple.getSrcPort();
		dstAddress = tuple.getDstAddress();
		dstPort = tuple.getDstPort();

		out = new OutgoingSegment(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);

        ISS = generateISS();
        RCV_WND = buffer.remaining();

        // RFC Open call in CLOSED state. active and valid socket
        out.setSYN(true);
        out.SEG_SEQ = ISS;
        send();
        SND_UNA = ISS;
        SND_NXT = ISS + 1;
        currentState = State.SYN_SENT;

	}

    // older code
	public Tcb(TcpPacket tcp) throws IOException {
		// From the packet, populate the addresses and ports
		srcAddress = tcp.getIp().getDestinationAddress();
		srcPort = tcp.getDstPort();
		dstAddress = tcp.getIp().getSourceAddress();
		dstPort = tcp.getSrcPort();
		
		out = new OutgoingSegment(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);

        ISS = generateISS();
        
        RCV_WND = buffer.remaining();

        // TODO tcp without SYN should not have created this Tcb!!!
        // Passive OPEN
        currentState = State.LISTEN;
        stateListen(Event.OPEN, tcp);
	}
	
	// older code
	public Tcb(InetAddress srcAddress, int srcPort, InetAddress dstAddress, int dstPort) throws IOException {
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.srcAddress = srcAddress;
		
		out = new OutgoingSegment(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);
        
        ISS = generateISS();

        // Active OPEN
        out.setSYN(true);
        out.SEG_SEQ = ISS;
        send();
        SND_UNA = ISS;
        SND_NXT = ISS + 1;
        currentState = State.SYN_SENT;
	}
	
	static private byte[] issRandomBytes;
	static private MessageDigest issMd5;
	static {
		Random r = new Random();
		issRandomBytes = new byte[64];
		r.nextBytes(issRandomBytes);

		// TODO should be placed elsewhere as init() 
		try {
			issMd5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public int generateISS() {
		int iss = (int)System.nanoTime() / 1024;

		// TODO rfc1948
		issMd5.reset();
		issMd5.update(srcAddress.getAddress());
		issMd5.update(dstAddress.getAddress());
		issMd5.update((byte)(dstPort / 256));
		issMd5.update((byte)dstPort);
		issMd5.update((byte)(srcPort / 256));
		issMd5.update((byte)srcPort);
		issMd5.update(issRandomBytes);
		byte[] digest = issMd5.digest();
		
		ByteBuffer bb = ByteBuffer.wrap(digest);
		for (int i = 0; i < 4; i++) {
			iss += bb.getInt();
		}
		
		return iss;
	}
	
	void send() throws IOException {
		// TODO put here??? use RCV_WND???
        out.SEG_WND = buffer.remaining();
        
        // TODO put here???
        out.SEG_ACK = RCV_NXT;

		InetAddress src = subnet.getSrcAddress();
		InetAddress dst = dstAddress;
        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[1] = out.getByteBuffer(src, dst, null);
        bbs[0] = IpHeader.create(src, dst, IpPacket.Protocol.TCP, bbs);
        subnet.send(dstAddress, bbs);
        
        out.clearFlags();
	}
	
	void sendPayload() throws IOException, InterruptedException {
		// TODO put here??? use RCV_WND???
        out.SEG_WND = buffer.remaining();
        
        // TODO put here???
        out.SEG_ACK = RCV_NXT;
        
        // TODO reuse payload. setup size constant
        ByteBuffer payload = ByteBuffer.allocate(1024);
        payload.clear();
        outgoingQueue.get(payload);
        payload.flip();
        
		InetAddress src = subnet.getSrcAddress();
		InetAddress dst = dstAddress;
        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[1] = out.getByteBuffer(src, dst, payload);
        bbs[0] = IpHeader.create(src, dst, IpPacket.Protocol.TCP, bbs);
        subnet.send(dstAddress, bbs);
        
        out.clearFlags();
	}
	
	State stateListen(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.CLOSE) {
			// delete TCB
			return State.CLOSED;
		}
		else if (event == Event.SEND) {
	        out.SEG_SEQ = ISS;
			out.setSYN(true);
			send();
			SND_UNA = ISS;
			SND_NXT = ISS + 1;
			return State.SYN_SENT;
		}
		else if (event == Event.SEGMENT_ARRIVES) {
			if (tcp.RST) {
				// ignore
				return State.LISTEN;
			}
			if (tcp.ACK) {
				// TODO
			}
			if (tcp.SYN) {
				// TODO security/compartment check
				
				// TODO SEG.PRC TCB.PRC check
				
				// Respond now
				RCV_NXT = SEG_SEQ + 1;
				IRS = SEG_SEQ;
				
		        out.SEG_SEQ = ISS;
		        out.SEG_ACK = RCV_NXT;
		        out.SEG_WND = RCV_WND;
				out.setSYN(true);
				out.setACK(true);
				send();
				SND_NXT = ISS + 1;
				SND_UNA = ISS;
				return State.SYN_RCVD;
			}
		}
		return State.LISTEN;
	}

	State stateSynRcvd(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.CLOSE) {
			out.setFIN(true);
			send();
			return State.FIN_WAIT1;
		}
		else if (event == Event.SEGMENT_ARRIVES) {
			// TODO Ack of SYN
			if (tcp.ACK) {
				if (SND_UNA <= SEG_ACK && SEG_ACK <= SND_NXT) {
					// TODO continue processing here or after return???
					return State.ESTABLISHED;
				}
				else {
					out.setRST(true);
					send();
					// TODO close???
					return State.CLOSED;
				}
			}
		}
		return State.SYN_RCVD;
	}
	
	State stateSynSent(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.SEGMENT_ARRIVES) {
			if (tcp.SYN) {
				out.setACK(true);
				send();
				if (tcp.ACK) {
					return State.ESTABLISHED;
				} else {
					return State.SYN_RCVD;
				}
			}
		}
		return State.SYN_SENT;
	}
	
	State stateEstab(Event event, TcpPacket tcp) throws IOException {
		Dump.dumpIndent();
		if (event == Event.CLOSE) {
			out.setFIN(true);
			send();
			return State.FIN_WAIT1;
		}
		else if (event == Event.SEGMENT_ARRIVES) {
			if (SND_UNA < SEG_ACK && SEG_ACK <= SND_NXT) {
				SND_UNA = SEG_ACK;
			}
			// TODO remove acked in retransmission queue

			Dump.dump("tcb data " + tcp.toString());
			RCV_NXT += tcp.getDataLength();
			buffer.write(tcp.getByteBuffer(), tcp.getDataOffset(), tcp.getDataLength());
			RCV_WND = buffer.remaining();
			
			// TODO transmit ack??
			
			// TODO what is this??
			if (tcp.FIN) {
				out.setACK(true);
				send();
				Dump.dumpDedent();
				return State.CLOSE_WAIT;
			}
		}
		Dump.dumpDedent();
		return State.ESTABLISHED;
	}
	
	State stateFinWait1(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.SEGMENT_ARRIVES) {
			// TODO ACK of FIN
			if (tcp.ACK) {
				return State.FIN_WAIT2;
			}
			else if (tcp.FIN) {
				out.setACK(true);
				send();
				return State.CLOSING;
			}
		}
		return State.FIN_WAIT1;
	}
	
	State stateFinWait2(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.SEGMENT_ARRIVES) {
			if (tcp.FIN) {
				out.setACK(true);
				send();
				return State.TIME_WAIT;
			}
		}
		return State.FIN_WAIT2;
	}
	
	State stateClosing(Event event, TcpPacket tcp) {
		if (event == Event.SEGMENT_ARRIVES) {
			// TODO ACK of FIN
			if (tcp.ACK) {
				return State.TIME_WAIT;
			}
		}
		return State.CLOSING;
	}
	
	State stateTimeWait(Event event, TcpPacket tcp) {
		if (event == Event.TIME_WAIT_TIMEOUT) {
			// delete TCB
			return State.CLOSED;
		}
		return State.TIME_WAIT;
	}
	
	State stateCloseWait(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.CLOSE) {
			out.setFIN(true);
			send();
			return State.LAST_ACK;
		}
		return State.CLOSE_WAIT;
	}
	
	State stateLastAck(Event event, TcpPacket tcp) {
		if (event == Event.SEGMENT_ARRIVES) {
			// TODO Ack of FIN
			if (tcp.ACK) {
				return State.CLOSED;
			}
		}
		return State.LAST_ACK;
	}
	
	State stateClosed(Event event, TcpPacket tcp) {
		return State.CLOSED;
	}

	void eventOpen(boolean active) throws IOException, TcbException {
		switch (currentState) {
		case CLOSED:
			// TODO init code here???
			// TODO checks
			if (active) {
				// TODO verify security precedence of user
				// TODO verify all sock info specified
				
				ISS = generateISS();
				out.SEG_SEQ = ISS;
				out.setSYN(true);
				send();
				SND_UNA = ISS;
				SND_NXT = ISS + 1;
				currentState = State.SYN_SENT;
			}
			else {
				currentState = State.LISTEN;
			}
			break;
		case LISTEN:
			// TODO check caller permission
			if (active) {
				// TODO verify all sock info specified
				// TODO need state for passive/active??
				ISS = generateISS();
				out.SEG_SEQ = ISS;
				out.setSYN(true);
				send();
				SND_UNA = ISS;
				SND_NXT = ISS + 1;
				currentState = State.SYN_SENT;
			}
			break;
		default:
			throw new TcbException("error: connection already exists");
		}
	}
	
	void eventSend(ByteBuffer payload) throws TcbException, IOException, InterruptedException {
		switch (currentState) {
		case CLOSED:
			// TODO check user permission
			throw new TcbException("error: connection does not exist");
		case LISTEN:
			// TODO passive to active again
			ISS = generateISS();
			out.SEG_SEQ = ISS;
			out.setSYN(true);
			send();
			SND_UNA = ISS;
			SND_NXT = ISS + 1;
			currentState = State.SYN_SENT;
			
			// TODO send data together with syn?
			
			// TODO check too much in queue?
			outgoingQueue.put(payload);
			break;
		case SYN_SENT:
		case SYN_RCVD:
			// TODO check too much in queue?
			outgoingQueue.put(payload);
			break;
		case ESTABLISHED:
		case CLOSE_WAIT:
			outgoingQueue.put(payload);
			// TODO segmentize
			// TODO piggy back ack???
			out.SEG_ACK = RCV_NXT;
			// TODO set ack here??
			out.setACK(true);
			break;
		case CLOSING:
		case FIN_WAIT1:
		case FIN_WAIT2:
		case LAST_ACK:
		case TIME_WAIT:
		default:
			throw new TcbException("connection closing");
		}
	}
	
	int eventReceive(ByteBuffer bb) throws TcbException, InterruptedException {
		switch (currentState) {
		case CLOSED:
			// TODO check user permission
			throw new TcbException("connection does not exist");
		case LISTEN:
		case SYN_SENT:
		case SYN_RCVD:
			// TODO insufficient resource???
			return incomingQueue.get(bb);
		case ESTABLISHED:
		case FIN_WAIT1:
		case FIN_WAIT2:
			// TODO insufficient resource???
			// TODO Urgent pointer???
			return incomingQueue.get(bb);
		case CLOSE_WAIT:
			if (incomingQueue.isEmpty()) {
				// TODO is this EOF??
				throw new TcbException("connection closing");
			}
			return incomingQueue.get(bb);
		case CLOSING:
		case LAST_ACK:
		case TIME_WAIT:
			throw new TcbException("connection closing");
		}
		return 0;
	}
	
	public void eventClose() throws TcbException, IOException {
		switch (currentState) {
		case CLOSED:
			// TODO check permission
			throw new TcbException("connection does not exist");
		case LISTEN:
			// TODO what is outstanding RECEIVEs
			// TODO delete tcb
			currentState = State.CLOSED;
			break;
		case SYN_SENT:
			// TODO delete tcb
			// TODO throw "closing" to queued send and receive??
			break;
		case SYN_RCVD:
			if (outgoingQueue.isEmpty()) {
				out.setFIN(true);
				send();
				currentState = State.FIN_WAIT1;
			}
			else {
				// TODO queue for processing after establish??
			}
			break;
		case ESTABLISHED:
			if (outgoingQueue.isEmpty()) {
				out.setFIN(true);
				send();
				currentState = State.FIN_WAIT1;
			}
			else {
				// TODO queue a fin
			}
			currentState = State.FIN_WAIT1;
			break;
		case FIN_WAIT1:
		case FIN_WAIT2:
			throw new TcbException("connection closing");
		case CLOSE_WAIT:
			if (outgoingQueue.isEmpty()) {
				out.setFIN(true);
				send();
				currentState = State.CLOSING;
			}
			else {
				// TODO queue a fin and then enter CLOSING
			}
			break;
		case CLOSING:
		case LAST_ACK:
		case TIME_WAIT:
			throw new TcbException("connection closing");
		}
	}

	public void eventAbort() throws TcbException, IOException {
		switch (currentState) {
		case CLOSED:
			// TODO check user permission
			throw new TcbException("connection does not exist");
		case LISTEN:
			// TODO delete tcb
			currentState = State.CLOSED;
			if (!incomingQueue.isEmpty()) {
				// TODO is this what RFC wanted?
				throw new TcbException("connection reset");
			}
			break;
		case SYN_SENT:
			// TODO delete tcb
			currentState = State.CLOSED;
			if (!incomingQueue.isEmpty() || !outgoingQueue.isEmpty()) {
				// TODO is this what RFC wanted?
				throw new TcbException("connection reset");
			}
			break;
		case SYN_RCVD:
		case ESTABLISHED:
		case FIN_WAIT1:
		case FIN_WAIT2:
		case CLOSE_WAIT:
			out.SEG_SEQ = SND_NXT;
			out.setRST(true);
			send();
			if (!incomingQueue.isEmpty() || !outgoingQueue.isEmpty()) {
				// TODO connection reset notification. Interrupt blocking calls
				// TODO flush queued transmission??
			}
			// TODO wait for flush before deleting???
			// TODO delete tcb
			currentState = State.CLOSED;
			break;
		case CLOSING:
		case LAST_ACK:
		case TIME_WAIT:
			// TODO respond with ok?!?!?
			// TODO delete tcb
			currentState = State.CLOSED;
			break;
		
		}
	}
	
	public String eventStatus() throws TcbException {
		if (currentState == State.CLOSED) {
			// TODO check permission. Since TCB goes under the same JVM. Maybe no need to check
			throw new TcbException("connection does not exist");
		}
		else {
			return currentState.toString();
		}
	}

	public void eventSegmentArrives(IncomingSegment segment) throws IOException, InterruptedException {
		if (currentState == State.CLOSED) {
			if (segment.RST) {
				// ignore
			}
			else {
				if (!segment.ACK) {
					out.SEG_SEQ = 0;
					out.SEG_ACK = segment.SEG_SEQ + segment.dataLength;
					out.setRST(true);
					out.setACK(true);
				}
				else {
					out.SEG_SEQ = segment.SEG_ACK;
					out.setRST(true);
				}
				send();
			}
		}
		else if (currentState == State.LISTEN) {
			if (segment.RST) {
				// ignore
			}
			else if (segment.ACK) {
				out.SEG_SEQ = segment.SEG_ACK;
				out.setRST(true);
				send();
			}
			else if (segment.SYN) {
				// TODO check security
				// TODO check PRC
				RCV_NXT = segment.SEG_SEQ + 1;
				IRS = segment.SEG_SEQ;
				ByteBuffer bb = segment.getByteBuffer();
				bb.position(segment.getDataOffset());
				incomingQueue.put(bb);
				ISS = generateISS();
				
				out.SEG_SEQ = ISS;
				out.SEG_ACK = RCV_NXT;
				out.setSYN(true);
				out.setACK(true);
				send();
				SND_NXT = ISS + 1;
				SND_UNA = ISS;
				currentState = State.SYN_RCVD;

				// TODO continue here...
				
				// TODO but the processing of syn/ack should not be repeated...
				
				// TODO if foreign socket not set
				foreignSocket.setAddress(segment.getIp().getSourceAddress());
				foreignSocket.setPort(segment.getSrcPort());
			}
			else {
				// Drop the segment
			}
		}
		else if (currentState == State.SYN_SENT) {
			boolean isAckOK = false;
			if (segment.ACK) {
				if (segment.SEG_ACK <= ISS || segment.SEG_ACK > SND_NXT) {
					if (!segment.RST) {
						out.SEG_SEQ = segment.SEG_ACK;
						out.setRST(true);
						send();
						return;
					}
				}
				else if (SND_UNA <= segment.SEG_ACK && segment.SEG_ACK <= SND_NXT) {
					isAckOK = true;
				}
			}
			if (segment.RST) {
				if (isAckOK) {
					// TODO signal connection reset
					currentState = State.CLOSED;
					// TODO delete TCB
				}
				return;
			}
			// TODO check security and precedence
			else if (segment.SYN) {
				RCV_NXT = segment.SEG_SEQ + 1;
				IRS = segment.SEG_SEQ;
				if (isAckOK) {
					SND_UNA = segment.SEG_ACK;
					// TODO clear retransmission queue
				}
				if (SND_UNA > ISS) {
					currentState = State.ESTABLISHED;
					out.SEG_SEQ = SND_NXT;
					out.SEG_ACK = RCV_NXT;
					out.setACK(true);
					send();
					
					// TODO may include data???
					
					if (segment.getDataLength() == 0) {
						return;
					}
					
					// TODO go to sixth step (check URG)
				}
				else {
					currentState = State.SYN_RCVD;
					out.SEG_SEQ = ISS;
					out.SEG_ACK = RCV_NXT;
					out.setSYN(true);
					out.setACK(true);
					send();
					
					if (segment.getDataLength() > 0) {
						// TODO queue for processing after ESTABLISH
					}
					return;
				}
			}
			else if (!segment.SYN && !segment.RST) {
				// fifth
				// Drop segment
				return;
			}
		}
		else {
			// first check seq
			switch (currentState) {
			case SYN_RCVD:
			case ESTABLISHED:
			case FIN_WAIT1:
			case FIN_WAIT2:
			case CLOSE_WAIT:
			case LAST_ACK:
			case TIME_WAIT:
				// TODO check parts straddle??
				
				// Test acceptance
				boolean acceptable = false;
				if (segment.getDataLength() == 0) {
					if (RCV_WND == 0) {
						if (segment.SEG_SEQ == RCV_NXT) {
							acceptable = true;
						}
					}
					else if (RCV_WND > 0){
						if (RCV_NXT <= segment.SEG_SEQ) {
							acceptable = true;
						}
					}
				}
				else if (segment.getDataLength() > 0){
					// RCV_WND 0 not acceptable
					
					if (RCV_WND > 0) {
						if ((RCV_NXT <= segment.SEG_SEQ && segment.SEG_SEQ < (RCV_NXT + RCV_WND)) ||
							(RCV_NXT <= (segment.SEG_SEQ + segment.getDataLength() - 1) && (segment.SEG_SEQ + segment.getDataLength() - 1) < (RCV_NXT + RCV_WND))
							) {
							acceptable = true;
						}
					}
				}
				
				// TODO RCV_WND 0 special allowance
				
				if (!acceptable) {
					if (!segment.RST) {
						out.SEG_SEQ = SND_NXT;
						out.SEG_ACK = RCV_NXT;
						out.setACK(true);
						send();
					}
					// drop and return
					return;
				}
				
				// TODO trimming off? higher seq for later processing??
				
			}
			
			// second check the RST bit
			switch (currentState) {
			case SYN_RCVD:
				if (segment.RST) {
					// TODO passive open should return to LISTEN state??? Was it not accepted???
					// TODO active open should CLOSED, delete tcb and return
					return;
				}
				break;
			case ESTABLISHED:
			case FIN_WAIT1:
			case FIN_WAIT2:
			case CLOSE_WAIT:
				if (segment.RST) {
					// TODO RECV/SEND reset
					// TODO notify user "connection reset"
					currentState = State.CLOSED;
					// TODO delete tcb
					return;
				}
				break;
			case CLOSING:
			case LAST_ACK:
			case TIME_WAIT:
				if (segment.RST) {
					currentState = State.CLOSED;
					// TODO delete tcb
					return;
				}
			}
			
			// third check security and precedence
			// TODO check security and precedence
			
			// fourth check the SYN bit
			switch (currentState) {
			case SYN_RCVD:
			case ESTABLISHED:
			case FIN_WAIT1:
			case FIN_WAIT2:
			case CLOSE_WAIT:
			case CLOSING:
			case LAST_ACK:
			case TIME_WAIT:
				// TODO SYN in window??
				// TODO SYN not in window??
			}
			
			// fifth check the ACK field
			if (!segment.ACK) {
				// drop
				return;
			}
			else {
				switch (currentState) {
				case SYN_RCVD:
					if (SND_UNA <= segment.SEG_ACK && segment.SEG_ACK <= SND_NXT) {
						currentState = State.ESTABLISHED;
						// TODO continue processing??
					}
					else {
						// TODO does this mean ACK not acceptable??
						out.SEG_SEQ = segment.SEG_ACK;
						out.setRST(true);
						send();
						
						// TODO continue?? it didn't say. But I think it should stop
						return;
					}
				case ESTABLISHED:
					if (SND_UNA < segment.SEG_ACK && segment.SEG_ACK <= SND_NXT) {
						SND_UNA = segment.SEG_ACK;
						// TODO clear ack segments in retx queue
						// TODO notify user??
						// TODO dup ACK??
						// TODO All seq check should use method to allow wrap or negative compare
						if (segment.SEG_ACK > SND_NXT) {
							// TODO not setting seq or ack???
							out.setACK(true);
							send();
							// drop
							return;
						}

						if (SND_UNA < segment.SEG_ACK && segment.SEG_ACK <= SND_NXT) {
							// TODO this is the same condition, maybe it should move to the block above
							// TODO update send window
							if (SND_WL1 < segment.SEG_SEQ ||
									(SND_WL1 == segment.SEG_SEQ && SND_WL2 <= segment.SEG_ACK)) {
								SND_WND = segment.SEG_WND;
								SND_WL1 = segment.SEG_SEQ;
								SND_WL2 = segment.SEG_ACK;
							}
						}
					}
					// TODO return or not? RFC didn't say!!!
					break;
				case FIN_WAIT1:
					// TODO if FIN is now acked, enter FIN_WAIT2???
					break;
				case FIN_WAIT2:
					// TODO not sure what the RFC says
					// TODO if retx queue empty, close can be acked ok???
					break;
				case CLOSE_WAIT:
					// TODO same as establish state
					break;
				case CLOSING:
					// TODO if FIN is acked??? enter TIME_WAIT
					break;
				case LAST_ACK:
					// TODO if FIN is acked, delete tcb, enter closed state, return
					break;
				case TIME_WAIT:
					// TODO only retx of remote FIN should come here. ack it and restart 2 MSL timeout
					break;
				}
			}
			
			// sixth, check the URG bit
			switch (currentState) {
			case ESTABLISHED:
			case FIN_WAIT1:
			case FIN_WAIT2:
				if (segment.URG) {
					// TODO what is SEG.UP??
					// TODO signal user
				}
				break;
			case CLOSE_WAIT:
			case CLOSING:
			case LAST_ACK:
			case TIME_WAIT:
				// TODO should not occur. Ignore. But why say it, what about the unmentioned states?
				break;
			}
			
			// seventh, process the segment text
			switch (currentState) {
			case ESTABLISHED:
			case FIN_WAIT1:
			case FIN_WAIT2:
				// TODO queue segment text
				// TODO signal user if PSH is set
				// TODO update RCV_NXT
				// TODO update RCV_WND
				// TODO window management, section 3.7
				
				// TODO send ack should piggyback data
				out.SEG_SEQ = SND_NXT;
				out.SEG_ACK = RCV_NXT;
				out.setACK(true);
				send();
				break;
			case CLOSE_WAIT:
			case CLOSING:
			case LAST_ACK:
			case TIME_WAIT:
				// TODO this should not occur, since a FIN has been received. What the?
				break;
			}
			
			// eighth, check the FIN bit
			if (segment.FIN) {
				switch (currentState) {
				case CLOSED:
				case LISTEN:
				case SYN_SENT:
					// drop and return
					return;
				}
				
				// TODO signal user "connection closing"
				// TODO reutnr pending RECEIVEs
				// TODO advance RCV_NXT over FIN
				// TODO send ack for FIN
				// TODO FIN implies PUSH
				switch (currentState) {
				case SYN_RCVD:
				case ESTABLISHED:
					currentState = State.CLOSE_WAIT;
					break;
				case FIN_WAIT1:
					// TODO if FIN is acked, enter TIME_WAIT
					// TODO otherwise CLOSING state
					break;
				case FIN_WAIT2:
					currentState = State.TIME_WAIT;
					// TODO start time-wait timer, off other timers
					break;
				case CLOSE_WAIT:
					// Remain CLOSE_WAIT
					break;
				case CLOSING:
					// Remain CLOSING
					break;
				case LAST_ACK:
					// Remain LAST_ACK
					break;
				case TIME_WAIT:
					// Remain TIME_WAIT
					// TODO restart 2 MSL time-wait timeout
					break;
				}
			}
		}
	}
	
	public void eventTimeout() {
		// TODO what is user timeout
		// TODO handle retx timeout
		// TODO handle time-wait timeout
	}

	// TODO testing events. any event
	private class Change {
		boolean read = false;
		boolean write = false;
	}
	
	private Change change = new Change();
	
	public void select() throws InterruptedException {
		synchronized (change) {
			while (!change.read && !change.write) {
				change.wait();
			}
		}
	}
	
	private void notifyRead() {
		synchronized (change) {
			change.read = true;
			change.notify();
		}
	}

	private void notifyWrite() {
		synchronized (change) {
			change.write = true;
			change.notify();
		}
	}

	public void process(Event event, TcpPacket tcp) throws IOException {
		Dump.dumpIndent();
		Dump.dump("Tcb processing: tcb=" + this + " currentState=" + currentState + " event=" + event);
		SEG_SEQ = tcp.getSeqNumber();
		SEG_ACK = tcp.getAckNumber();
		SEG_LEN = tcp.getDataLength();
		SEG_WND = tcp.window;
		SEG_UP = tcp.urgentPointer;
		// TODO precedence value
		SEG_PRC = 0;
		
		switch (currentState) {
		case LISTEN:
			currentState = stateListen(event, tcp);
			break;
		case SYN_RCVD:
			currentState = stateSynRcvd(event, tcp);
			if (currentState != State.ESTABLISHED) break;
			// continue processing
		case ESTABLISHED:
			currentState = stateEstab(event, tcp);
			break;
		}
		Dump.dumpDedent();
	}
}
