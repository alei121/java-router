package code.messy.net.ip.spi;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.ip.tcp.AnyMatcher;
import code.messy.net.ip.tcp.Tcb;
import code.messy.net.ip.tcp.TcpPacket;

public class ServerSocketHandler implements Handler<TcpPacket> {
	private SelectorProvider provider;
	private LinkedBlockingQueue<TcpChannel> channelQueue = new LinkedBlockingQueue<TcpChannel>();
	
	public ServerSocketHandler(SelectorProvider provider) {
		this.provider = provider;
		TcpEntryHandler.getInstance().register(new AnyMatcher(), this);
	}

	@Override
	public void handle(TcpPacket packet) {
		Dump.dumpIndent();
		try {
			Dump.dump("ServerSocketHandler: tuple=" + packet.getTuple());
			Tcb tcb;
			tcb = new Tcb(packet);
			SocketHandler handler = new SocketHandler(tcb);

			TcpEntryHandler.getInstance().add(packet.getTuple(), handler);

			TcpChannel channel = new TcpChannel(provider, handler);
			channelQueue.add(channel);
			
			tcb.process(Tcb.Event.SEGMENT_ARRIVES, packet);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dump.dumpDedent();
	}
	
	public TcpChannel getChannel() throws InterruptedException {
		return channelQueue.take();
	}
}
