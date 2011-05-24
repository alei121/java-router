package code.messy.net.ip.tcp;

import java.nio.ByteBuffer;

public class TcpHeader {
    int srcPort;
    int dstPort;
    int seqNumber;
    int ackNumber;
    int window;
    int checksum;
    int urgentPointer;
    
    // TODO with options in future
    int dataOffset = 4;
    
    boolean CWR, ECE;
    boolean URG, ACK, PSH, RST, SYN, FIN;
    
    public TcpHeader(int srcPort, int dstPort) {
    	this.srcPort = srcPort;
    	this.dstPort = dstPort;
	}
    
    public ByteBuffer getByteBuffer() {
    	ByteBuffer bb = ByteBuffer.allocate(20);
    	bb.putShort((short)srcPort);
    	bb.putShort((short)dstPort);
    	bb.putInt(seqNumber);
    	bb.putInt(ackNumber);
    	int bits;
    	bits = dataOffset << 12;
    	bits |= (CWR ? 0x80 : 0);
    	bits |= (ECE ? 0x40 : 0);
    	bits |= (URG ? 0x20 : 0);
    	bits |= (ACK ? 0x10 : 0);
    	bits |= (PSH ? 0x08 : 0);
    	bits |= (RST ? 0x04 : 0);
    	bits |= (SYN ? 0x02 : 0);
    	bits |= (FIN ? 0x01 : 0);
    	bb.putShort((short)bits);
    	bb.putShort((short)window);
    	
    	// TODO checksum later on
    	bb.putShort((short)0);
    	bb.putShort((short)urgentPointer);
    	
    	bb.flip();
    	return bb;
    }

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}

	public void setAckNumber(int ackNumber) {
		this.ackNumber = ackNumber;
	}

	public void setWindow(int window) {
		this.window = window;
	}

	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

	public void setUrgentPointer(int urgentPointer) {
		this.urgentPointer = urgentPointer;
	}

	public void setCWR(boolean cWR) {
		CWR = cWR;
	}

	public void setECE(boolean eCE) {
		ECE = eCE;
	}

	public void setURG(boolean uRG) {
		URG = uRG;
	}

	public void setACK(boolean aCK) {
		ACK = aCK;
	}

	public void setPSH(boolean pSH) {
		PSH = pSH;
	}

	public void setRST(boolean rST) {
		RST = rST;
	}

	public void setSYN(boolean sYN) {
		SYN = sYN;
	}

	public void setFIN(boolean fIN) {
		FIN = fIN;
	}
	
	public void clearFlags() {
		CWR = ECE = URG = ACK = PSH = RST = SYN = FIN = false;
	}
}
