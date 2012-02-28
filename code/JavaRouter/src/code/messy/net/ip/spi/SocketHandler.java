package code.messy.net.ip.spi;

import java.io.IOException;
import java.nio.ByteBuffer;

import code.messy.Handler;
import code.messy.net.ip.tcp.Tcb;
import code.messy.net.ip.tcp.TcpPacket;

public class SocketHandler implements Handler<TcpPacket> {
	Tcb tcb;
	
	public SocketHandler(Tcb tcb) {
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
	
	public int read(ByteBuffer bb) {
		return tcb.getBuffer().read(bb);
	}
}
