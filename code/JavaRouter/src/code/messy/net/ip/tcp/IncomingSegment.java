/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip.tcp;

import java.nio.ByteBuffer;

import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.Tuple;

// TODO seems only for incoming packet
public class IncomingSegment implements Packet {
    IpPacket ip;
    int srcPort;
    int dstPort;
    
    // RFC Section 3.2 Current segment variable
    int SEG_SEQ;
    int SEG_ACK;
    int SEG_LEN;
    int SEG_WND;
    int SEG_UP;
    int SEG_PRC;
    
    int checksum;
    int urgentPointer;
    
	boolean CWR, ECE;
	boolean URG, ACK, PSH, RST, SYN, FIN;
    
    int headerOffset;
    int dataOffset;
    
    final static int maxDataLength = 2048;
    
    public IncomingSegment() {
    	// TODO Try build or extract directly from BB
    }
    
    public IncomingSegment(IpPacket ip) {
        this.ip = ip;
        
        ByteBuffer bb = ip.getByteBuffer();
        headerOffset = ip.getDataOffset();
        srcPort = bb.getShort(headerOffset);
        dstPort = bb.getShort(headerOffset + 2);
        SEG_SEQ = bb.getInt(headerOffset + 4);
        SEG_ACK = bb.getInt(headerOffset + 8);
        short b = bb.getShort(headerOffset + 12);
        dataOffset = (b >> 10) & 0x3C;
        CWR = (b & 0x80) != 0;
        ECE = (b & 0x40) != 0;
        URG = (b & 0x20) != 0;
        ACK = (b & 0x10) != 0;
        PSH = (b & 0x8) != 0;
        RST = (b & 0x4) != 0;
        SYN = (b & 0x2) != 0;
        FIN = (b & 0x1) != 0;
        
        SEG_WND = bb.getShort(headerOffset + 14);
        checksum = bb.getShort(headerOffset + 16);
        urgentPointer = bb.getShort(headerOffset + 18);
        
        SEG_LEN = ip.getDataLength() - dataOffset;
        dataOffset += headerOffset;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return ip.getByteBuffer();
    }

    public IpPacket getIp() {
        return ip;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    @Override
    public Port getPort() {
        return ip.getPort();
    }

    @Override
    public int getHeaderOffset() {
        return headerOffset;
    }

    @Override
    public int getDataLength() {
        return SEG_LEN;
    }

    @Override
    public int getDataOffset() {
        return dataOffset;
    }

    @Override
    public long getTimestamp() {
        return ip.getTimestamp();
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("TcpPacket srcPort=" + srcPort);
    	sb.append(",dstPort=" + dstPort);
    	sb.append(",seqNumber=" + SEG_SEQ);
    	sb.append(",ackNumber=" + SEG_ACK);
    	sb.append(",CWR=" + CWR);
    	sb.append(",ECE=" + ECE);
    	sb.append(",URG=" + URG);
    	sb.append(",ACK=" + ACK);
    	sb.append(",PSH=" + PSH);
    	sb.append(",RST=" + RST);
    	sb.append(",SYN=" + SYN);
    	sb.append(",FIN=" + FIN);
    	sb.append(",window=" + SEG_WND);
    	sb.append(",checksum=" + checksum);
    	sb.append(",urgentPointer=" + urgentPointer);
    	sb.append(",headerOffset=" + headerOffset);
    	sb.append(",dataOffset=" + dataOffset);
    	sb.append(",dataLength=" + SEG_LEN);
    	return sb.toString();
    }

	public int getSeqNumber() {
		return SEG_SEQ;
	}

	public void setSeqNumber(int seqNumber) {
		this.SEG_SEQ = seqNumber;
	}

	public int getAckNumber() {
		return SEG_ACK;
	}

	public void setAckNumber(int ackNumber) {
		this.SEG_ACK = ackNumber;
	}
	
	public Tuple getTuple() {
		return new Tuple(ip.getSourceAddress(), ip.getDestinationAddress(), srcPort, dstPort);
	}
}
