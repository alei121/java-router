package code.messy.net.ip.tcp;

import java.util.HashMap;
import java.util.Set;

import code.messy.Receiver;

public class SessionHandler implements TcpPacketHandler {
	Tcb tcb = null;
    private HashMap<TupleMatcher, Receiver<TcpPacket>> map = new HashMap<TupleMatcher, Receiver<TcpPacket>>();
    private Receiver<TcpPacket> defaultHandler = null;
	

	@Override
	public void handle(TcpPacket packet) {
		Set<TupleMatcher> matchers = map.keySet();
		for (TupleMatcher matcher : matchers) {
			if (matcher.match(packet.getTuple())) {
				Receiver<TcpPacket> handler = map.get(matcher);
				handler.receive(packet);
				return;
			}
		}
		if (defaultHandler != null) {
			defaultHandler.receive(packet);
		}
	}
	
	public void register(Receiver<TcpPacket> handler) {
		defaultHandler = handler;
	}
	
	public void register(TupleMatcher matcher, Receiver<TcpPacket> handler) {
		map.put(matcher, handler);
	}
}
