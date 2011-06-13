/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.util.HashMap;

import code.messy.Handler;
import code.messy.Registrable;
import code.messy.net.ip.IpPacket.Protocol;

public class IpProtocolHandler implements Registrable<IpPacket.Protocol, Handler<IpPacket>>, Handler<IpPacket> {
    HashMap<Byte, Handler<IpPacket>> map = new HashMap<Byte, Handler<IpPacket>>();

    @Override
    public void handle(IpPacket packet) {
        Byte protocol = packet.getProtocol();
        Handler<IpPacket> ph = map.get(protocol);
        if (ph != null) {
            ph.handle(packet);
        }
    }

	@Override
	public void register(Protocol type, Handler<IpPacket> handler) {
        map.put(type.getValue(), handler);
	}

	@Override
	public void register(Handler<IpPacket> handler) {
		// TODO Auto-generated method stub
		
	}

}
