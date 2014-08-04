/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;

import code.messy.Receiver;

public class IpMulticastHandler implements Receiver<IpInputPacket> {
	Receiver<IpInputPacket> multicast, other;
    public IpMulticastHandler(Receiver<IpInputPacket> multicast, Receiver<IpInputPacket> other) {
        this.multicast = multicast;
        this.other = other;
    }

    @Override
    public void receive(IpInputPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        if (dst.isMulticastAddress()) {
            multicast.receive(ip);
        }
        else {
            other.receive(ip);
        }
    }

}
