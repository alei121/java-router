package code.messy.net.ip.tcp;

import java.io.IOException;

import code.messy.Receiver;
import code.messy.net.Dump;
import code.messy.net.ip.TupleMap;

public class TcbSessionHandler implements Receiver<TcpPacket> {
	TupleMap<Receiver<TcpPacket>> map = new TupleMap<Receiver<TcpPacket>>();

	@Override
	public void receive(TcpPacket packet) {
		try {
			Receiver<TcpPacket> handler = map.get(packet.getTuple());
			if (handler == null) {
				Dump.dump("null handler. tuple=" + packet.getTuple());
				Tcb tcb;
				tcb = new Tcb(packet);
				handler = new TcbHandler(tcb);
				map.add(packet.getTuple(), handler);
			}
			handler.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TcbHandler implements Receiver<TcpPacket> {
		Tcb tcb;
		
		public TcbHandler(Tcb tcb) {
			this.tcb = tcb;
		}
		
		@Override
		public void receive(TcpPacket packet) {
			try {
				tcb.process(Tcb.Event.SEGMENT_ARRIVES, packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
