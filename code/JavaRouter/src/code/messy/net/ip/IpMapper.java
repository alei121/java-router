/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.ip.IpInputPacket.Protocol;

public class IpMapper implements Registrable<IpInputPacket.Protocol, Receiver<IpInputPacket>>, Receiver<IpInputPacket> {
    Receiver<IpInputPacket> defaultReceiver = null;
    
    HashMap<Byte, TupleMap<Receiver<IpInputPacket>>> protocolMap = new HashMap<>();

    @Override
    public void receive(IpInputPacket ip) {
        Byte protocol = ip.getProtocol();
		TupleMap<Receiver<IpInputPacket>> tupleMap = protocolMap.get(protocol);
		if (tupleMap != null) {
	        Receiver<IpInputPacket> ph = tupleMap.get(ip.getSourceAddress(), ip
	                .getDestinationAddress(), 0, 0);
	        if (ph != null) {
	        	ph.receive(ip);
	        	return;
	        }
		}
        if (defaultReceiver != null) {
        	defaultReceiver.receive(ip);
        }
    }

	public void register(InetAddress dst, Protocol type, Receiver<IpInputPacket> handler) {
		TupleMap<Receiver<IpInputPacket>> tupleMap = protocolMap.get(type);
		if (tupleMap == null) {
			tupleMap = new TupleMap<>();
			protocolMap.put(type.getValue(), tupleMap);
		}
		tupleMap.add(null, dst, 0, 0, handler);
	}
	
	@Override
	public void register(Protocol type, Receiver<IpInputPacket> handler) {
		register(null, type, handler);
	}

	@Override
	public void register(Receiver<IpInputPacket> handler) {
		defaultReceiver = handler;
	}
}
