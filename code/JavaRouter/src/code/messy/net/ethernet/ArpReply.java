package code.messy.net.ethernet;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;

public class ArpReply implements OutputPacket {
    ByteBuffer arp = ByteBuffer.allocateDirect(60);

    public ArpReply(InetAddress senderAddress, InetAddress targetAddress,
            MacAddress senderMac, MacAddress targetMac) {
        // Ethernet
        arp.putShort((short) 0x1);
        // IP
        arp.putShort((short) 0x800);
        // size 6
        arp.put((byte) 6);
        // size 4
        arp.put((byte) 4);
        // ARP reply
        arp.putShort((short) 0x2);

        arp.put(senderMac.getAddress());
        arp.put(targetAddress.getAddress());
        arp.put(senderMac.getAddress());
        arp.put(senderAddress.getAddress());

        arp.flip();
    }
    
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(arp);
	}
}
