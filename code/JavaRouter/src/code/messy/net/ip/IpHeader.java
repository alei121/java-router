/*
 * Created on Sep 2, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.net.Dump;

public class IpHeader {
    // special bytebuffer[] payload. it will skip null for "remain" calculation

    static public ByteBuffer create(InetAddress src, InetAddress dst, IpPacket.Protocol protocol, int ttl, ByteBuffer[] payload) {
        Dump.dumpIndent();
        
        ByteBuffer header = ByteBuffer.allocateDirect(20);
        
        Dump.dump("IpHeader: create src=" + src + " dst=" + dst + " protocol=" + protocol);
        
        int remain = 0;
        for (ByteBuffer bb : payload) {
            if (bb != null) remain += bb.remaining();
        }
        
        // start of ip header
        header.put((byte)0x45);
        header.put((byte)0x0);
        // total length
        header.putShort((short)(remain + 20));
        // id, flags and fragment offset
        header.putShort((short)0);
        header.putShort((short)0);
        
        // ttl
        header.put((byte)ttl);
        header.put(protocol.getValue());
        
        // header checksum. unknown now
        header.putShort((short)0);
        
        // source
        header.put(src.getAddress());
        // dst
        header.put(dst.getAddress());
        header.flip();

        header.putShort(10, IpPacket.getChecksum(header, 0, 20));
        header.flip();

        Dump.dumpDedent();
        return header;
    }

    static public ByteBuffer create(InetAddress src, InetAddress dst, IpPacket.Protocol protocol, ByteBuffer[] payload) {
        return create(src, dst, protocol, 64, payload);
    }
    
    static public void setDontFragment(ByteBuffer header, boolean df) {
        byte b = header.get(6);
        if (df) b |= 0x40;
        else b &= 0xBF;
        header.put(6, (byte)b);
    }
}
