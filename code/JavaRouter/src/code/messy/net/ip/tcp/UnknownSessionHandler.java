package code.messy.net.ip.tcp;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.net.ip.IpPacket;

public class UnknownSessionHandler implements TcpPacketHandler {
	@Override
	public void handle(TcpPacket tcp) {
		if (tcp.RST) {
			// ignore
			return;
		}

		TcpHeader header = new TcpHeader(tcp.getDstPort(), tcp.getSrcPort());
		header.setRST(true);
		if (tcp.ACK) {
			header.setSeqNumber(tcp.seqNumber);
		}
		else {
			header.setSeqNumber(0);
			header.setAckNumber(tcp.seqNumber + tcp.getDataLength());
			header.setACK(true);
		}
		
		InetAddress dstAddress = tcp.getIp().getSourceAddress();
		try {
			IpPacket.send(dstAddress, IpPacket.Protocol.TCP, header.getByteBuffer());
		} catch (IOException e) {
			// TODO log
			e.printStackTrace();
		}
	}
}
