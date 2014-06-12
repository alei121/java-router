/*
 * Created on Sep 2, 2008
 */
package code.messy.net.ip.udp;

import java.nio.ByteBuffer;

import code.messy.net.Flow;

public class UdpHeader {
    static public ByteBuffer create(int src, int dst, ByteBuffer[] payload) {
        ByteBuffer header = ByteBuffer.allocateDirect(8);

        Flow.trace("UdpHeader: create src=" + src + " dst=" + dst);

        int remain = 0;
        for (ByteBuffer bb : payload) {
            if (bb != null) remain += bb.remaining();
        }
   
        header.putShort((short)src);
        header.putShort((short)dst);
        header.putShort((short)(remain + 8));
        
        // TODO zero checksum for now
        header.putShort((short)0);
        
        header.flip();
        
        return header;
    }
}
