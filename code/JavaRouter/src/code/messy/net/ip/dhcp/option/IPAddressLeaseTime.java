package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class IPAddressLeaseTime implements OptionIF {
	int seconds;
	
	public IPAddressLeaseTime(byte[] value) {
		seconds = ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) | ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
	}
	
	public IPAddressLeaseTime(int seconds) {
		this.seconds = seconds;
	}

	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.put((byte)51);
		bb.put((byte)4);
		bb.putInt(seconds);
		bb.flip();
		return bb;
	}
	
	@Override
	public String toString() {
		return "IPAddressLeaseTime seconds=" + seconds;
	}
}
