package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class RequestedIPAddress implements OptionIF {
	// IPv4 only
	byte[] ip;
	
	public RequestedIPAddress(byte[] value) {
		this.ip = value;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.put((byte)50);
		bb.put((byte)4);
		bb.put(ip);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		return "RequestedIPAddress ip=" + IPAddressHelper.toString(ip);
	}
}
