package code.messy.net.ip;

import java.net.InetAddress;

import code.messy.Receiver;
import code.messy.util.IpAddressHelper;

public class IpBroadcastHandler implements Receiver<IpPacket> {
	Receiver<IpPacket> receiver, other;
    public IpBroadcastHandler(Receiver<IpPacket> receiver, Receiver<IpPacket> other) {
        this.receiver = receiver;
        this.other = other;
    }

    @Override
    public void receive(IpPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        if (IpAddressHelper.isBroadcast(dst)) {
        	receiver.receive(ip);
        }
        else {
            other.receive(ip);
        }
    }

}
