package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class Unknown implements OptionIF {
	int type;
	byte[] value;
	
	public Unknown(int type, byte[] value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(value.length + 2);
		bb.put((byte)type);
		bb.put((byte)value.length);
		bb.put(value);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		return "Unknown type=" + type + " length=" + value.length;
	}
}
