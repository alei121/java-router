package code.messy.net.ip.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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
		
	OutgoingSegment outgoingSegment;
	LocalSubnet subnet;
    InetAddress srcAddress, dstAddress;
    int srcPort, dstPort;
    
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

		outgoingSegment = new OutgoingSegment(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);

        ISS = generateISS();
        RCV_WND = buffer.remaining();

        // RFC Open call in CLOSED state. active and valid socket
        outgoingSegment.setSYN(true);
        outgoingSegment.SEG_SEQ = ISS;
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
		
		outgoingSegment = new OutgoingSegment(srcPort, dstPort);
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
		
		outgoingSegment = new OutgoingSegment(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);
        
        ISS = generateISS();

        // Active OPEN
        outgoingSegment.setSYN(true);
        outgoingSegment.SEG_SEQ = ISS;
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
        outgoingSegment.SEG_WND = buffer.remaining();
        
        // TODO put here???
        outgoingSegment.SEG_ACK = RCV_NXT;

		InetAddress src = subnet.getSrcAddress();
		InetAddress dst = dstAddress;
        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[1] = outgoingSegment.getByteBuffer(src, dst, null);
        bbs[0] = IpHeader.create(src, dst, IpPacket.Protocol.TCP, bbs);
        subnet.send(dstAddress, bbs);
        
        outgoingSegment.clearFlags();
	}
	
	void sendPayload() throws IOException, InterruptedException {
		// TODO put here??? use RCV_WND???
        outgoingSegment.SEG_WND = buffer.remaining();
        
        // TODO put here???
        outgoingSegment.SEG_ACK = RCV_NXT;
        
        // TODO reuse payload. setup size constant
        ByteBuffer payload = ByteBuffer.allocate(1024);
        payload.clear();
        outgoingQueue.get(payload);
        payload.flip();
        
		InetAddress src = subnet.getSrcAddress();
		InetAddress dst = dstAddress;
        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[1] = outgoingSegment.getByteBuffer(src, dst, payload);
        bbs[0] = IpHeader.create(src, dst, IpPacket.Protocol.TCP, bbs);
        subnet.send(dstAddress, bbs);
        
        outgoingSegment.clearFlags();
	}
	
	State stateListen(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.CLOSE) {
			// delete TCB
			return State.CLOSED;
		}
		else if (event == Event.SEND) {
	        outgoingSegment.SEG_SEQ = ISS;
			outgoingSegment.setSYN(true);
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
				
		        outgoingSegment.SEG_SEQ = ISS;
		        outgoingSegment.SEG_ACK = RCV_NXT;
		        outgoingSegment.SEG_WND = RCV_WND;
				outgoingSegment.setSYN(true);
				outgoingSegment.setACK(true);
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
			outgoingSegment.setFIN(true);
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
					outgoingSegment.setRST(true);
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
				outgoingSegment.setACK(true);
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
			outgoingSegment.setFIN(true);
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
				outgoingSegment.setACK(true);
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
				outgoingSegment.setACK(true);
				send();
				return State.CLOSING;
			}
		}
		return State.FIN_WAIT1;
	}
	
	State stateFinWait2(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.SEGMENT_ARRIVES) {
			if (tcp.FIN) {
				outgoingSegment.setACK(true);
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
			outgoingSegment.setFIN(true);
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
				outgoingSegment.SEG_SEQ = ISS;
				outgoingSegment.setSYN(true);
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
				outgoingSegment.SEG_SEQ = ISS;
				outgoingSegment.setSYN(true);
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
			outgoingSegment.SEG_SEQ = ISS;
			outgoingSegment.setSYN(true);
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
			outgoingSegment.SEG_ACK = RCV_NXT;
			// TODO set ack here??
			outgoingSegment.setACK(true);
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
	
	private int recv(ByteBuffer bb) {
		return 0;
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
