/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip.udp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.InputPacket;
import code.messy.net.Port;
import code.messy.net.ip.IpInputPacket;

public class UdpInputPacket implements InputPacket {
    IpInputPacket ip;
    int srcPort;
    int dstPort;
    int headerOffset;
    int dataOffset;
    int dataLength;
    
    public UdpInputPacket(IpInputPacket ip) {
        this.ip = ip;
        
        ByteBuffer bb = ip.getByteBuffer();
        headerOffset = ip.getDataOffset();
        srcPort = bb.getShort(headerOffset);
        dstPort = bb.getShort(headerOffset + 2);
        dataLength = bb.getShort(headerOffset + 4) - 8;
        dataOffset = headerOffset + 8;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return ip.getByteBuffer();
    }

    public IpInputPacket getIp() {
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
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		// TODO maybe the same bb used by ip and eth
        ByteBuffer bb = ip.getByteBuffer();
        bb.position(headerOffset);
        bbs.add(bb);
	}
}
