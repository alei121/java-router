/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.util.HashMap;

import code.messy.Receiver;
import code.messy.Registrable;
import code.messy.net.ip.IpPacket.Protocol;

public class IpMapper implements Registrable<IpPacket.Protocol, Receiver<IpPacket>>, Receiver<IpPacket> {
    Receiver<IpPacket> defaultReceiver = null;
    
    HashMap<Byte, TupleMap<Receiver<IpPacket>>> protocolMap = new HashMap<>();

    @Override
    public void receive(IpPacket ip) {
        Byte protocol = ip.getProtocol();
		TupleMap<Receiver<IpPacket>> tupleMap = protocolMap.get(protocol);
		if (tupleMap != null) {
	        Receiver<IpPacket> ph = tupleMap.get(ip.getSourceAddress(), ip
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

	public void register(InetAddress dst, Protocol type, Receiver<IpPacket> handler) {
		TupleMap<Receiver<IpPacket>> tupleMap = protocolMap.get(type);
		if (tupleMap == null) {
			tupleMap = new TupleMap<>();
			protocolMap.put(type.getValue(), tupleMap);
		}
		tupleMap.add(null, dst, 0, 0, handler);
	}
	
	@Override
	public void register(Protocol type, Receiver<IpPacket> handler) {
		register(null, type, handler);
	}

	@Override
	public void register(Receiver<IpPacket> handler) {
		defaultReceiver = handler;
	}
}
