package code.messy.net.ip.spi;

import java.util.HashMap;
import java.util.Set;

import code.messy.Receiver;
import code.messy.net.ip.Tuple;
import code.messy.net.ip.tcp.Tcb;
import code.messy.net.ip.tcp.TcpPacket;
import code.messy.net.ip.tcp.TupleMatcher;

public class TcpEntryHandler implements Receiver<TcpPacket> {
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
    private HashMap<TupleMatcher, Receiver<TcpPacket>> map = new HashMap<TupleMatcher, Receiver<TcpPacket>>();
    private Receiver<TcpPacket> defaultHandler = null;
    
	@Override
	public void receive(TcpPacket packet) {
		SocketHandler handler = mapOfTupleToSocketHandler.get(packet.getTuple());
		if (handler != null) {
			handler.receive(packet);
		}
		else {
			// TODO revise this for ServerSocketHandler
			Set<TupleMatcher> matchers = map.keySet();
			for (TupleMatcher matcher : matchers) {
				if (matcher.match(packet.getTuple())) {
					Receiver<TcpPacket> tcpHandler = map.get(matcher);
					tcpHandler.receive(packet);
					return;
				}
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
