/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.net.OutputPayload;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;

public interface Subnet {
    public NetworkNumber getNetwork();
    public InetAddress getSrcAddress();
    public void forward(IpPacket ip) throws IOException;
    public void send(InetAddress dst, ByteBuffer[] bbs) throws IOException;
    public void send(InetAddress dst, OutputPayload payload) throws IOException;
}
