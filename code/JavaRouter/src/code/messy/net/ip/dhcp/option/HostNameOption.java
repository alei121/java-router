package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class HostNameOption implements OptionIF {
	String name;
	
	public HostNameOption(byte[] value) {
		name = new String(value);
	}

	@Override
	public ByteBuffer getPayload() {
		byte[] value = name.getBytes();
		ByteBuffer bb = ByteBuffer.allocate(value.length + 2);
		bb.put((byte)12);
		bb.put((byte)value.length);
		bb.put(value);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		return "HostNameOption name=" + name;
	}
}
