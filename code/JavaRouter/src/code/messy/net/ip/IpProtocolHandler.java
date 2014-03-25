/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.ip.IpPacket.Protocol;

public class IpProtocolHandler implements Registrable<IpPacket.Protocol, Receiver<IpPacket>>, Receiver<IpPacket> {
    HashMap<Byte, Receiver<IpPacket>> map = new HashMap<Byte, Receiver<IpPacket>>();

    @Override
    public void receive(IpPacket packet) {
        Byte protocol = packet.getProtocol();
        Receiver<IpPacket> ph = map.get(protocol);
        if (ph != null) {
            ph.receive(packet);
        }
    }

	@Override
	public void register(Protocol type, Receiver<IpPacket> handler) {
        map.put(type.getValue(), handler);
	}

	@Override
	public void register(Receiver<IpPacket> handler) {
		// TODO Auto-generated method stub
		
	}

}
