package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class ServerIdentitier implements OptionIF {
	// IPv4 only
	byte[] ip;
	
	public ServerIdentitier(byte[] ip) {
		this.ip = ip;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.put((byte)54);
		bb.put((byte)4);
		bb.put(ip);
		bb.flip();
		return bb;
	}

	@Override
	public String toString() {
		return "ServerIdentitier ip=" + IPAddressHelper.toString(ip);
	}
}
