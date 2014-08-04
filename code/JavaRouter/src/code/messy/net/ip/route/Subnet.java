/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.net.Payload;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.NetworkNumber;

public interface Subnet {
    public NetworkNumber getNetwork();
    public InetAddress getSrcAddress();
    public void forward(IpInputPacket ip) throws IOException;
    public void send(InetAddress dst, Payload payload) throws IOException;
}
