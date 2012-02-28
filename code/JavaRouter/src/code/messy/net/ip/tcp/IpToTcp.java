package code.messy.net.ip.tcp;

import code.messy.Handler;
import code.messy.net.ip.IpPacket;

// TODO Try get rid of x-to-y
public class IpToTcp implements Handler<IpPacket> {
	private Handler<TcpPacket> handler;
	
	public IpToTcp(Handler<TcpPacket> handler) {
		this.handler = handler;
	}
	
	@Override
	public void handle(IpPacket ip) {
		TcpPacket tcp = new TcpPacket(ip);
		handler.handle(tcp);
	}

}
