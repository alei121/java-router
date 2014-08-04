package code.messy.net.ethernet;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;

public class ArpRequest implements OutputPacket {
    ByteBuffer arp = ByteBuffer.allocateDirect(60);

    public ArpRequest(InetAddress srcAddress, InetAddress dstAddress,
            MacAddress srcMac) {
        // Ethernet
        arp.putShort((short) 0x1);
        // IP
        arp.putShort((short) 0x800);
        // size 6
        arp.put((byte) 6);
        // size 4
        arp.put((byte) 4);
        // ARP request
        arp.putShort((short) 0x1);

        arp.put(srcMac.getAddress());
        arp.put(srcAddress.getAddress());
        arp.put(MacAddress.ZERO.getAddress());
        arp.put(dstAddress.getAddress());

        arp.flip();
    }
    
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(arp);
	}
}
