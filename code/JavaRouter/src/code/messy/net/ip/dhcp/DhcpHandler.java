package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;

import code.messy.net.ip.udp.UdpPacket;
import code.messy.net.ip.udp.UdpPacketHandler;

public class DhcpHandler implements UdpPacketHandler {

	@Override
	public void handle(UdpPacket udp) {
		ByteBuffer bb = udp.getByteBuffer();
		bb.position(udp.getDataOffset());
		DhcpMessage message = new DhcpMessage(bb);
		
		System.out.println("message=" + message.toString());
	}
}
