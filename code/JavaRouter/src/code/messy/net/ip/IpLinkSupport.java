/*
 * Created on Aug 15, 2008
 */
package code.messy.net.ip;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public interface IpLinkSupport {
    void send(InetAddress src, InetAddress dst, ByteBuffer[] payload) throws IOException;
}
