package code.messy.net.ip.tcp;

import code.messy.Receiver;
import code.messy.net.ip.IpPacket;

// TODO Try get rid of x-to-y
public class IpToTcp implements Receiver<IpPacket> {
	private Receiver<TcpPacket> handler;
	
	public IpToTcp(Receiver<TcpPacket> handler) {
		this.handler = handler;
	}
	
	@Override
	public void receive(IpPacket ip) {
		TcpPacket tcp = new TcpPacket(ip);
		handler.receive(tcp);
	}

}
