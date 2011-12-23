package code.messy.net.ip.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ip.IpHeader;
import code.messy.net.ip.IpPacket;

public class UnknownSessionHandler implements Handler<TcpPacket> {
	@Override
	public void handle(TcpPacket tcp) {
        Dump.dumpIndent();

		try {
			Dump.dump("Unknown session. tcp=" + tcp);

			// reverse address
			InetAddress src = tcp.getIp().getDestinationAddress();
			InetAddress dst = tcp.getIp().getSourceAddress();
			
			// no payload, just RST header
			TcpHeader header = new TcpHeader(tcp.getDstPort(), tcp.getSrcPort());
			header.setRST(true);
			header.setACK(true);
			header.setAckNumber(tcp.getSeqNumber() + 1);
			
            ByteBuffer[] bbs = new ByteBuffer[2];
            bbs[1] = header.getByteBuffer(src, dst, null);
            bbs[0] = IpHeader.create(src, dst, IpPacket.Protocol.TCP, bbs);
            
            // TODO not always ethernet
            EthernetPort port = (EthernetPort)tcp.getPort();
            
            port.send(src, dst, bbs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        Dump.dumpDedent();

	}
}
