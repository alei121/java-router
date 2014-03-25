/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;

import code.messy.Receiver;

public class IpMulticastHandler implements Receiver<IpPacket> {
	Receiver<IpPacket> multicast, other;
    public IpMulticastHandler(Receiver<IpPacket> multicast, Receiver<IpPacket> other) {
        this.multicast = multicast;
        this.other = other;
    }

    @Override
    public void receive(IpPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        if (dst.isMulticastAddress()) {
            multicast.receive(ip);
        }
        else {
            other.receive(ip);
        }
    }

}
