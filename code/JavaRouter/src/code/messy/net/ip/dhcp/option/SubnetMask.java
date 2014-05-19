package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class SubnetMask implements OptionIF {
	// IPv4 only
	byte[] mask;
	
	public SubnetMask(byte[] value) {
		this.mask = value;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.put((byte)1);
		bb.put((byte)4);
		bb.put(mask);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		return "SubnetMask mask=" + IPAddressHelper.toString(mask);
	}
}
