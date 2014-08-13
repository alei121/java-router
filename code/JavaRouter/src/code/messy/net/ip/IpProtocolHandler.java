/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.util.HashMap;
import java.util.Map;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.ip.IpInputPacket.Protocol;

public class IpProtocolHandler implements Registrable<IpInputPacket.Protocol, Receiver<IpInputPacket>>, Receiver<IpInputPacket> {
    Map<Protocol, Receiver<IpInputPacket>> map = new HashMap<>();
    Receiver<IpInputPacket> defaultReceiver = null;

    @Override
    public void receive(IpInputPacket packet) {
        Receiver<IpInputPacket> ph = map.get(packet.getProtocol());
        if (ph != null) {
            ph.receive(packet);
        }
        else if (defaultReceiver != null) {
        	defaultReceiver.receive(packet);
        }
    }

	@Override
	public void register(Protocol type, Receiver<IpInputPacket> handler) {
        map.put(type, handler);
	}

	@Override
	public void register(Receiver<IpInputPacket> handler) {
		defaultReceiver = handler;
	}
}
