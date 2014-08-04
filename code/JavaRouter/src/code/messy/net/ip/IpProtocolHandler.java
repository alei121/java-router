/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.ip.IpInputPacket.Protocol;

public class IpProtocolHandler implements Registrable<IpInputPacket.Protocol, Receiver<IpInputPacket>>, Receiver<IpInputPacket> {
    HashMap<Byte, Receiver<IpInputPacket>> map = new HashMap<Byte, Receiver<IpInputPacket>>();
    Receiver<IpInputPacket> defaultReceiver = null;

    @Override
    public void receive(IpInputPacket packet) {
        Byte protocol = packet.getProtocol();
        Receiver<IpInputPacket> ph = map.get(protocol);
        if (ph != null) {
            ph.receive(packet);
        }
        else if (defaultReceiver != null) {
        	defaultReceiver.receive(packet);
        }
    }

	@Override
	public void register(Protocol type, Receiver<IpInputPacket> handler) {
        map.put(type.getValue(), handler);
	}

	@Override
	public void register(Receiver<IpInputPacket> handler) {
		defaultReceiver = handler;
	}
}
