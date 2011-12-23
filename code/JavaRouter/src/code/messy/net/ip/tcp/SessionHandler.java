package code.messy.net.ip.tcp;

import java.util.HashMap;
import java.util.Set;

import code.messy.Handler;

public class SessionHandler implements TcpPacketHandler {
	Tcb tcb = null;
    private HashMap<TupleMatcher, Handler<TcpPacket>> map = new HashMap<TupleMatcher, Handler<TcpPacket>>();
    private Handler<TcpPacket> defaultHandler = null;
	

	@Override
	public void handle(TcpPacket packet) {
		Set<TupleMatcher> matchers = map.keySet();
		for (TupleMatcher matcher : matchers) {
			if (matcher.match(packet.getTuple())) {
				Handler<TcpPacket> handler = map.get(matcher);
				handler.handle(packet);
				return;
			}
		}
		if (defaultHandler != null) {
			defaultHandler.handle(packet);
		}
	}
	
	public void register(Handler<TcpPacket> handler) {
		defaultHandler = handler;
	}
	
	public void register(TupleMatcher matcher, Handler<TcpPacket> handler) {
		map.put(matcher, handler);
	}
}
