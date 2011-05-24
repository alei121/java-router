/*
 * Created on Aug 6, 2008
 */
package code.messy.net.ip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.route.Subnet;

public class IpPacket implements Packet {
    Packet packet;

    private InetAddress sourceAddress;
    private InetAddress destinationAddress;
    private int version;
    private int headerOffset;
    private int headerLength;
    private int dataOffset;
    private int dataLength;
    private int totalLength;
    private byte protocol;
    

    public IpPacket(Packet packet) {
        this.packet = packet;
        
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
    public Packet getPacket() {
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

    static public void send(InetAddress dstAddress, Protocol protocol, ByteBuffer payload) throws IOException {
        System.out.println("IpPacket: send " + dstAddress + " protocol=" + protocol + " len=" + payload.remaining());
        Subnet subnet = RoutingTable.getInstance().getSubnetByMasking(dstAddress);
        
        ByteBuffer header = ByteBuffer.allocateDirect(20);
        
        // start of ip header
        header.put((byte)0x45);
        header.put((byte)0x0);
        // total length
        header.putShort((short)(payload.remaining() + 20));
        // id, flags and fragment offset
        header.putShort((short)0);
        header.putShort((short)0);
        
        // ttl
        header.put((byte)64);
        header.put(protocol.getValue());
        
        // header checksum. unknown now
        header.putShort((short)0);
        
        // source
        InetAddress srcAddress = subnet.getSrcAddress();
        header.put(srcAddress.getAddress());
        // dst
        header.put(dstAddress.getAddress());
        header.flip();

        header.putShort(10, getChecksum(header, 0, 20));
        header.rewind();

        
        ByteBuffer bbs[] = new ByteBuffer[2];
        bbs[0] = header;
        bbs[1] = payload;
        subnet.send(dstAddress, bbs);
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
}
