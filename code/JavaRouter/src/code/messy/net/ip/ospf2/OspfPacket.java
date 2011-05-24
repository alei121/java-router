/*
 * Created on Oct 1, 2008
 */
package code.messy.net.ip.ospf2;

import java.nio.ByteBuffer;

import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.UnsupportedProtocolException;
import code.messy.net.ip.IpPacket;

public class OspfPacket implements Packet {
    IpPacket ip;
    int dataLength;
    int dataOffset;
    int headerOffset;
    
    public OspfHeader.Type getType() {
        return type;
    }

    public int getRouteId() {
        return routeId;
    }

    public int getAreaId() {
        return areaId;
    }

    OspfHeader.Type type;
    int routeId;
    int areaId;
    
    public OspfPacket(IpPacket ip) throws UnsupportedProtocolException {
        this.ip = ip;
        ByteBuffer bb = ip.getByteBuffer();
        headerOffset = ip.getDataOffset();
        dataOffset = headerOffset + 24;

        bb.position(headerOffset);
        
        byte version =  bb.get();
        if (version != 2) {
            throw new UnsupportedProtocolException("OSPF version unsupported: " + version);
        }
        
        type = OspfHeader.Type.getType(bb.get());
        
        short length = bb.getShort();
        dataLength = length - 24;
        
        routeId = bb.getInt();
        areaId = bb.getInt();
        
        // TODO validate checksum and auth
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        return ip.getByteBuffer();
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
    public int getHeaderOffset() {
        return headerOffset;
    }

    @Override
    public Port getPort() {
        return ip.getPort();
    }

    @Override
    public long getTimestamp() {
        return ip.getTimestamp();
    }

}
