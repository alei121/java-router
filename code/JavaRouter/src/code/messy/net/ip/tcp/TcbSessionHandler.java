package code.messy.net.ip.tcp;

import java.io.IOException;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.ip.TupleMap;

public class TcbSessionHandler implements Handler<TcpPacket> {
	TupleMap<Handler<TcpPacket>> map = new TupleMap<Handler<TcpPacket>>();

	@Override
	public void handle(TcpPacket packet) {
		try {
			Handler<TcpPacket> handler = map.get(packet.getTuple());
			if (handler == null) {
				Dump.dump("null handler. tuple=" + packet.getTuple());
				Tcb tcb;
				tcb = new Tcb(packet);
				handler = new TcbHandler(tcb);
				map.add(packet.getTuple(), handler);
			}
			handler.handle(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TcbHandler implements Handler<TcpPacket> {
		Tcb tcb;
		
		public TcbHandler(Tcb tcb) {
			this.tcb = tcb;
		}
		
		@Override
		public void handle(TcpPacket packet) {
			try {
				tcb.process(Tcb.Event.SEGMENT_ARRIVES, packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
