package code.messy.net;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.net.NetworkInterface;


/*
 * Created on 2008-04-08
 */

public class InetRawSocket implements ByteChannel {
    static {
        System.loadLibrary("RawSocket");
        nativeInit();
    }
    
    int sd = -1;
    NetworkInterface intf;
    
    public InetRawSocket(NetworkInterface intf) throws IOException {
        this.intf = intf;
        sd = nativeOpen(intf.getName());
    }
    
    public InetRawSocket(String ifName) throws IOException {
        this(NetworkInterface.getByName(ifName));
    }
    
    public void close() throws IOException {
        nativeClose(sd);
        sd = -1;
    }
    
    public int read(ByteBuffer bb) throws IOException {
        bb.clear();
        int pos = bb.position();
        int len = nativeRead(sd, bb, pos, bb.remaining());
        if (len == -1) throw new IOException("nativeRead error");
        bb.position(pos + len);
        return len;
    }

    public int write(ByteBuffer bb) throws IOException {
        int pos = bb.position();
        int len = nativeWrite(sd, bb, pos, bb.remaining());
        if (len == -1) throw new IOException("nativeWrite error");
        bb.position(pos + len);
        return len;
    }

    public static native void nativeInit();
    public static native int nativeOpen(String ifName);
    public static native void nativeClose(int sd);
    public static native int nativeRead(int sd, ByteBuffer bb, int offset, int length);
    public static native int nativeWrite(int sd, ByteBuffer bb, int offset, int length);

    @Override
    public boolean isOpen() {
        return (sd != -1);
    }
}
