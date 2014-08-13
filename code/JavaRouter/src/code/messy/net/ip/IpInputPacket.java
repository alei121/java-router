/*
 * Created on Aug 6, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.InputPacket;
import code.messy.net.Port;

public class IpInputPacket implements InputPacket {
    private InputPacket packet;
    private IpPort ipSupport;

    private InetAddress sourceAddress;
    private InetAddress destinationAddress;
    private int version;
    private int headerOffset;
    private int headerLength;
    private int dataOffset;
    private int dataLength;
    private int totalLength;
    private byte protocol;
    

    public IpInputPacket(InputPacket packet, IpPort ipSupport) {
        this.packet = packet;
        this.ipSupport = ipSupport;
        
        headerOffset = packet.getDataOffset();
        ByteBuffer bb = packet.getByteBuffer();
        bb.position(headerOffset);

        byte b = bb.get(headerOffset);
        version = (b & 0xF0) >> 4;
        headerLength = (b & 0x0F) * 4;

        // TODO is this an exception?
        if (version != 4) {
            System.out.println("Unsupported ip version " + version);
            return;
        }

        dataOffset = headerOffset + headerLength;

        totalLength = bb.getShort(headerOffset + 2);
        dataLength = totalLength - headerLength;

        protocol = bb.get(headerOffset + 9);

        byte[] srcIp = new byte[4];
        byte[] dstIp = new byte[4];
        bb.position(headerOffset + 12);
        bb.get(srcIp);
        bb.get(dstIp);

        try {
            destinationAddress = InetAddress.getByAddress(dstIp);
            sourceAddress = InetAddress.getByAddress(srcIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    
    public InetAddress getSourceAddress() {
        return sourceAddress;
    }
    public void setSourceAddress(InetAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }
    public void setDestinationAddress(InetAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public InputPacket getPacket() {
        return packet;
    }
    public byte getProtocol() {
        return protocol;
    }
    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }
    
    public static short getChecksum(ByteBuffer bb, int offset, int length) {
        bb.position(offset);
        
        int sum = 0;
        while(length > 1) {
            sum += (bb.getShort() & 0xFFFF);
            length -= 2;
        }
        
        if(length > 0) sum += bb.get();

        while ((sum >> 16) > 0) {
           sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (short)(~sum);
    }

    public enum Protocol {
        ICMP ((byte)1),
        TCP ((byte)6),
        UDP ((byte)17);
        
        private byte value;
        
        Protocol(byte value) {
            this.value = value;
        }
        
        public byte getValue() {
            return value;
        }
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return packet.getByteBuffer();
    }

    @Override
    public Port getPort() {
        return packet.getPort();
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
        return packet.getTimestamp();
    }

    public IpPort getIpPort() {
		return ipSupport;
	}
    
    @Override
    public String toString() {
    	return "IpPacket(src=" + sourceAddress + ", dst=" + destinationAddress + ")";
    }

	@Override
	public void getOutput(ArrayList<ByteBuffer> bbs) {
		// TODO maybe the same bb used by eth
        ByteBuffer bb = packet.getByteBuffer();
        bb.position(headerOffset);
        bbs.add(bb);
	}
}
