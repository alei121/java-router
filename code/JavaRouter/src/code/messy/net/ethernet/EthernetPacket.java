/*
 * Created on Sep 11, 2008
 */
package code.messy.net.ethernet;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.Packet;
import code.messy.net.Port;

public class EthernetPacket implements Packet {
    EthernetPort port;
    ByteBuffer bb;
    int dataOffset;
    int dataLength;
    long timestamp;
	MacAddress destinationAddress;
    MacAddress sourceAddress;
    Ethertype ethertype;

    
    public EthernetPacket(ByteBuffer bb, EthernetPort port) {
    	this.port = port;
    	this.bb = bb;
    	bb.position(0);
    	destinationAddress = new MacAddress(bb);
    	sourceAddress = new MacAddress(bb);
    	ethertype = Ethertype.get(bb.getShort());

        dataOffset = 14;
        dataLength = bb.limit() - dataOffset;
	}

    @Override
    public Port getPort() {
        return port;
    }

    @Override
    public int getHeaderOffset() {
        return 0;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return bb;
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
        return timestamp;
    }
    
    public MacAddress getDestinationAddress() {
		return destinationAddress;
	}

	public MacAddress getSourceAddress() {
		return sourceAddress;
	}

	public Ethertype getEthertype() {
		return ethertype;
	}
	
    @Override
    public String toString() {
        return "[EthernetPacket port=" + port + " src=" + sourceAddress + " dst=" + destinationAddress + "]";
    }

	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bb.position(0);
		bbs.add(bb);
	}

}
