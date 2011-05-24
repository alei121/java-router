/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;

public class IpMulticastHandler implements IpPacketHandler {
    IpPacketHandler multicast, other;
    public IpMulticastHandler(IpPacketHandler multicast, IpPacketHandler other) {
        this.multicast = multicast;
        this.other = other;
    }

    @Override
    public void handle(IpPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        if (dst.isMulticastAddress()) {
            multicast.handle(ip);
        }
        else {
            other.handle(ip);
        }
    }

}
