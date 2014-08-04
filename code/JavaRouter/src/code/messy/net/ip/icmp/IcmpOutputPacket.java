package code.messy.net.ip.icmp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;
import code.messy.net.ip.IpInputPacket;
import code.messy.util.ByteHelper;

public class IcmpOutputPacket implements OutputPacket {
	private ByteBuffer icmp;

	public IcmpOutputPacket(ByteBuffer request) {
		int length = request.remaining();
        icmp = ByteBuffer.allocateDirect(length);
        
        // copy original
        icmp.put(request);

        // echo reply
        icmp.put(0, (byte) 0);

        // set checksum zero
        icmp.putShort(2, (short) 0);
        // recalculate checksum
        icmp.putShort(2, IpInputPacket.getChecksum(icmp, 0, length));

        icmp.flip();
	}
	
	@Override
	public String toString() {
		return "IcmpOutputPayload " + ByteHelper.toStringTrimmed(icmp);
	}
	
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(icmp);
	}
}
