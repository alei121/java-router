package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class DNSOption implements OptionIF {
	// IPv4 only
	byte[] dnss;
	
	public DNSOption(byte[] dnss) {
		this.dnss = dnss;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(dnss.length + 2);
		bb.put((byte)6);
		bb.put((byte)dnss.length);
		bb.put(dnss);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DNSOption dnss=");
		for (int i = 0; i < dnss.length; i += 4) {
			sb.append(IPAddressHelper.toString(dnss, i));
			sb.append(" ");
		}
		return sb.toString();
	}
}
