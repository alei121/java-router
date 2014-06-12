package code.messy.net.ip.dhcp.option;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class RouterOption implements OptionIF {
	// IPv4 only
	byte[] routers;
	
	public RouterOption(byte[] routers) {
		this.routers = routers;
	}

	public RouterOption(InetAddress gateway) {
		routers = gateway.getAddress();
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(routers.length + 2);
		bb.put((byte)3);
		bb.put((byte)routers.length);
		bb.put(routers);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RouterOption routers=");
		for (int i = 0; i < routers.length; i += 4) {
			sb.append(IPAddressHelper.toString(routers, i));
			sb.append(" ");
		}
		return sb.toString();
	}
}
