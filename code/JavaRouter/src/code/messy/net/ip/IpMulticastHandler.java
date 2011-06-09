/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;

import code.messy.Handler;

public class IpMulticastHandler implements Handler<IpPacket> {
	Handler<IpPacket> multicast, other;
    public IpMulticastHandler(Handler<IpPacket> multicast, Handler<IpPacket> other) {
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
