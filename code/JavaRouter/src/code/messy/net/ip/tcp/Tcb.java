package code.messy.net.ip.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import code.messy.net.ip.IpHeader;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.route.LocalSubnet;

public class Tcb {
	int snd_una, snd_nxt, snd_wnd, snd_up, snd_wl1, snd_wl2, iss;
	int rcv_nxt, rcv_wnd, rcv_up, irs;
	int seg_seq, seg_ack, seg_len, seg_wnd, seg_up, seg_prc;
	
	enum State { Listen, SynSent, SynRcvd, Established, FinWait1, FinWait2, CloseWait, Closing, 
		LastAck, TimeWait, Closed };
		
	enum Event { Open, Send, Receive, Close, Abort, Status, Segment, UserTimeout, RetransmissionTimeout, TimeWaitTimeout };
		
	TcpHeader replyHeader;
	LocalSubnet subnet;
    InetAddress srcAddress, dstAddress;
    int srcPort, dstPort;
    
    State currentState;
    
    
	
	public Tcb(TcpPacket tcp) throws IOException {
		// From the packet, populate the addresses and ports
		srcAddress = tcp.getIp().getDestinationAddress();
		srcPort = tcp.getDstPort();
		dstAddress = tcp.getIp().getSourceAddress();
		dstPort = tcp.getSrcPort();
		
		replyHeader = new TcpHeader(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);

        iss = generateISS();

        // TODO tcp without SYN should not have created this Tcb!!!
        // Passive OPEN
        currentState = State.Listen;
        stateListen(Event.Open, tcp);
	}
	
	public Tcb(InetAddress srcAddress, int srcPort, InetAddress dstAddress, int dstPort) throws IOException {
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.srcAddress = srcAddress;
		
		replyHeader = new TcpHeader(srcPort, dstPort);
        subnet = LocalSubnet.getSubnet(srcAddress);
        
        iss = generateISS();

        // Active OPEN
        replyHeader.setSYN(true);
        replyHeader.setSeqNumber(iss);
        send();
        snd_una = iss;
        snd_nxt = iss + 1;
        currentState = State.SynSent;
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
        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[1] = replyHeader.getByteBuffer();
        bbs[0] = IpHeader.create(subnet.getSrcAddress(),
                dstAddress, IpPacket.Protocol.TCP, bbs);
        subnet.send(dstAddress, bbs);
        
        replyHeader.clearFlags();
	}
	
	State stateListen(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Close) {
			// delete TCB
			return State.Closed;
		}
		else if (event == Event.Send) {
	        replyHeader.setSeqNumber(iss);
			replyHeader.setSYN(true);
			send();
			snd_una = iss;
			snd_nxt = iss + 1;
			return State.SynSent;
		}
		else if (event == Event.Segment) {
			if (tcp.SYN) {
		        replyHeader.setSeqNumber(iss);
				replyHeader.setSYN(true);
				replyHeader.setACK(true);
				send();
				snd_una = iss;
				snd_nxt = iss + 1;
				return State.SynRcvd;
			}
		}
		return State.Listen;
	}

	State stateSynRcvd(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Close) {
			replyHeader.setFIN(true);
			send();
			return State.FinWait1;
		}
		else if (event == Event.Segment) {
			// TODO Ack of SYN
			if (tcp.ACK) {
				return State.Established;
			}
		}
		return State.SynRcvd;
	}
	
	State stateSynSent(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Segment) {
			if (tcp.SYN) {
				replyHeader.setACK(true);
				send();
				if (tcp.ACK) {
					return State.Established;
				} else {
					return State.SynRcvd;
				}
			}
		}
		return State.SynSent;
	}
	
	State stateEstab(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Close) {
			replyHeader.setFIN(true);
			send();
			return State.FinWait1;
		}
		else if (event == Event.Segment) {
			if (tcp.FIN) {
				replyHeader.setACK(true);
				send();
				return State.CloseWait;
			}
		}
		return State.Established;
	}
	
	State stateFinWait1(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Segment) {
			// TODO ACK of FIN
			if (tcp.ACK) {
				return State.FinWait2;
			}
			else if (tcp.FIN) {
				replyHeader.setACK(true);
				send();
				return State.Closing;
			}
		}
		return State.FinWait1;
	}
	
	State stateFinWait2(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Segment) {
			if (tcp.FIN) {
				replyHeader.setACK(true);
				send();
				return State.TimeWait;
			}
		}
		return State.FinWait2;
	}
	
	State stateClosing(Event event, TcpPacket tcp) {
		if (event == Event.Segment) {
			// TODO ACK of FIN
			if (tcp.ACK) {
				return State.TimeWait;
			}
		}
		return State.Closing;
	}
	
	State stateTimeWait(Event event, TcpPacket tcp) {
		if (event == Event.TimeWaitTimeout) {
			// delete TCB
			return State.Closed;
		}
		return State.TimeWait;
	}
	
	State stateCloseWait(Event event, TcpPacket tcp) throws IOException {
		if (event == Event.Close) {
			replyHeader.setFIN(true);
			send();
			return State.LastAck;
		}
		return State.CloseWait;
	}
	
	State stateLastAck(Event event, TcpPacket tcp) {
		if (event == Event.Segment) {
			// TODO Ack of FIN
			if (tcp.ACK) {
				return State.Closed;
			}
		}
		return State.LastAck;
	}
	
	State stateClosed(Event event, TcpPacket tcp) {
		return State.Closed;
	}
	
	public void process(Event event, TcpPacket tcp) throws IOException {
		switch (currentState) {
		case Listen:
			currentState = stateListen(event, tcp);
			break;
		}
	}
}
