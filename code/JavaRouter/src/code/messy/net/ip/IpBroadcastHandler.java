package code.messy.net.ip;

import java.net.InetAddress;

import code.messy.Receiver;
import code.messy.util.IpAddressHelper;

public class IpBroadcastHandler implements Receiver<IpInputPacket> {
	Receiver<IpInputPacket> receiver, other;
    public IpBroadcastHandler(Receiver<IpInputPacket> receiver, Receiver<IpInputPacket> other) {
        this.receiver = receiver;
        this.other = other;
    }

    @Override
    public void receive(IpInputPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        if (IpAddressHelper.isBroadcast(dst)) {
        	receiver.receive(ip);
        }
        else {
            other.receive(ip);
        }
    }

}
