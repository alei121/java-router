/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip.tcp;

import java.nio.ByteBuffer;

import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.ip.IpPacket;

public class TcpPacket implements Packet {
    IpPacket ip;
    int srcPort;
    int dstPort;
    int seqNumber;
    int ackNumber;
    int window;
    int checksum;
    int urgentPointer;
    
    boolean CWR, ECE;
    boolean URG, ACK, PSH, RST, SYN, FIN;
    
    int headerOffset;
    int dataOffset;
    int dataLength;
    
    final static int maxDataLength = 2048;
    
    public TcpPacket() {
    	// TODO Try build or extract directly from BB
    }
    
    public TcpPacket(IpPacket ip) {
        this.ip = ip;
        
        ByteBuffer bb = ip.getByteBuffer();
        headerOffset = ip.getDataOffset();
        srcPort = bb.getShort(headerOffset);
        dstPort = bb.getShort(headerOffset + 2);
        seqNumber = bb.getInt(headerOffset + 4);
        ackNumber = bb.getInt(headerOffset + 8);
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
        
        window = bb.getShort(headerOffset + 14);
        checksum = bb.getShort(headerOffset + 16);
        urgentPointer = bb.getShort(headerOffset + 18);
        
        dataLength = ip.getDataLength() - dataOffset;
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
        return dataLength;
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
    	sb.append(",seqNumber=" + seqNumber);
    	sb.append(",ackNumber=" + ackNumber);
    	sb.append(",CWR=" + CWR);
    	sb.append(",ECE=" + ECE);
    	sb.append(",URG=" + URG);
    	sb.append(",ACK=" + ACK);
    	sb.append(",PSH=" + PSH);
    	sb.append(",RST=" + RST);
    	sb.append(",SYN=" + SYN);
    	sb.append(",FIN=" + FIN);
    	sb.append(",window=" + window);
    	sb.append(",checksum=" + checksum);
    	sb.append(",urgentPointer=" + urgentPointer);
    	sb.append(",headerOffset=" + headerOffset);
    	sb.append(",dataOffset=" + dataOffset);
    	sb.append(",dataLength=" + dataLength);
    	return sb.toString();
    }
}
