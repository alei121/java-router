/*
 * Created on Jun 13, 2008
 */
package code.messy.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class PcapWrapper  implements ByteChannel {
    public static native void nativeInit();
    public static native long nativeOpen(String ifName);
    public static native void nativeClose(long pcap);
    public static native int nativeRead(long pcap, ByteBuffer bb, int offset, int length);
    public static native int nativeWrite(long pcap, ByteBuffer bb, int offset, int length);
    
    long pcap = 0;
    String ifName;
    
    public PcapWrapper(String ifName) throws IOException {
        this.ifName = ifName;
        pcap = nativeOpen(ifName);
    }
    
    public void close() throws IOException {
        nativeClose(pcap);
        pcap = 0;
    }
    
    @Override
    public int read(ByteBuffer bb) throws IOException {
        bb.clear();
        int pos = bb.position();
        int len = nativeRead(pcap, bb, pos, bb.remaining());
        if (len == -1) throw new IOException("nativeRead error");
        bb.position(pos + len);
        return len;
    }

    @Override
    public int write(ByteBuffer bb) throws IOException {
        int pos = bb.position();
        int len = nativeWrite(pcap, bb, pos, bb.remaining());
        if (len == -1) throw new IOException("nativeWrite error");
        bb.position(pos + len);
        return len;
    }

    @Override
    public boolean isOpen() {
        return (pcap != 0);
    }

}
