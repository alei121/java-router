package code.messy.net.ip.spi;

import java.util.HashMap;
import java.util.Set;

import code.messy.Handler;
import code.messy.net.ip.Tuple;
import code.messy.net.ip.tcp.Tcb;
import code.messy.net.ip.tcp.TcpPacket;
import code.messy.net.ip.tcp.TupleMatcher;

public class TcpEntryHandler implements Handler<TcpPacket> {
	private static TcpEntryHandler instance = new TcpEntryHandler();

	private HashMap<Tuple, SocketHandler> mapOfTupleToSocketHandler = new HashMap<Tuple, SocketHandler>();
	
	private TcpEntryHandler() {
	}
	
	public static TcpEntryHandler getInstance() {
		return instance;
	}
	
	public void add(Tuple tuple, SocketHandler handler) {
		mapOfTupleToSocketHandler.put(tuple, handler);
	}
	
	public void remove(Tuple tuple) {
		mapOfTupleToSocketHandler.remove(tuple);
	}
	
	Tcb tcb = null;
    private HashMap<TupleMatcher, Handler<TcpPacket>> map = new HashMap<TupleMatcher, Handler<TcpPacket>>();
    private Handler<TcpPacket> defaultHandler = null;
    
	@Override
	public void handle(TcpPacket packet) {
		SocketHandler handler = mapOfTupleToSocketHandler.get(packet.getTuple());
		if (handler != null) {
			handler.handle(packet);
		}
		else {
			// TODO revise this for ServerSocketHandler
			Set<TupleMatcher> matchers = map.keySet();
			for (TupleMatcher matcher : matchers) {
				if (matcher.match(packet.getTuple())) {
					Handler<TcpPacket> tcpHandler = map.get(matcher);
					tcpHandler.handle(packet);
					return;
				}
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
