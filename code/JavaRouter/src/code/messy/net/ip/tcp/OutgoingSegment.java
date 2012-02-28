package code.messy.net.ip.tcp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.net.ip.IpPacket;
import code.messy.net.ip.util.Checksum;

public class OutgoingSegment {
    // RFC Section 3.2 Current segment variable
    int SEG_SEQ;
    int SEG_ACK;
    int SEG_LEN;
    int SEG_WND;
    int SEG_UP;
    int SEG_PRC;
    
    int srcPort;
    int dstPort;
    int checksum;
    
    // TODO with options in future
    int dataOffset = 5;
    
    boolean CWR, ECE;
    boolean URG, ACK, PSH, RST, SYN, FIN;
    
    public OutgoingSegment(int srcPort, int dstPort) {
    	this.srcPort = srcPort;
    	this.dstPort = dstPort;
	}
    
    public ByteBuffer getByteBuffer(InetAddress srcAddress, InetAddress dstAddress, ByteBuffer content) {
    	int posChecksum = 0;
    	int posRealHeader = 0;
    	
    	ByteBuffer bb = ByteBuffer.allocate(12 + 20);
    	
    	// Pseudo header
        bb.put(srcAddress.getAddress());
        bb.put(dstAddress.getAddress());
        bb.put((byte)0);
        bb.put(IpPacket.Protocol.TCP.getValue());
        if (content != null) {
        	bb.putShort((short)(20 + content.remaining()));
        }
        else {
        	bb.putShort((short)20);
        }
    	
        // Real header
        posRealHeader = bb.position();
    	bb.putShort((short)srcPort);
    	bb.putShort((short)dstPort);
    	bb.putInt(SEG_SEQ);
    	bb.putInt(SEG_ACK);
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
    	bb.putShort((short)SEG_WND);
    	
    	// TODO checksum later on
    	posChecksum = bb.position();
    	bb.putShort((short)0);
    	bb.putShort((short)SEG_UP);
    	
    	bb.flip();
    	
    	ByteBuffer[] bbs = new ByteBuffer[2];
    	bbs[0] = bb;
    	bbs[1] = content;
    	short checksum = Checksum.getOnesCompliment(bbs);
    	bb.putShort(posChecksum, checksum);
    	
    	// Start from real header
    	bb.position(posRealHeader);
    	
    	return bb;
    }

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public void setChecksum(int checksum) {
		this.checksum = checksum;
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
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("TcpPacket srcPort=" + srcPort);
    	sb.append(",dstPort=" + dstPort);
    	sb.append(",SEG_SEQ=" + SEG_SEQ);
    	sb.append(",SEG_ACK=" + SEG_ACK);
    	sb.append(",CWR=" + CWR);
    	sb.append(",ECE=" + ECE);
    	sb.append(",URG=" + URG);
    	sb.append(",ACK=" + ACK);
    	sb.append(",PSH=" + PSH);
    	sb.append(",RST=" + RST);
    	sb.append(",SYN=" + SYN);
    	sb.append(",FIN=" + FIN);
    	sb.append(",SEG_WND=" + SEG_WND);
    	sb.append(",checksum=" + checksum);
    	sb.append(",SEG_UP=" + SEG_UP);
    	sb.append(",dataOffset=" + dataOffset);
    	return sb.toString();
    }

}
