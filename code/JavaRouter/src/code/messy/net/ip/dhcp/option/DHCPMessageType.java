package code.messy.net.ip.dhcp.option;

import java.nio.ByteBuffer;

public class DHCPMessageType implements OptionIF {
	public static final int DHCPDISCOVER = 1;
	public static final int DHCPOFFER = 2;
	public static final int DHCPREQUEST = 3;
	public static final int DHCPDECLINE = 4;
	public static final int DHCPACK = 5;
	public static final int DHCPNAK = 6;
	public static final int DHCPRELEASE = 7;

	byte type;
	public DHCPMessageType(byte[] value) {
		type = value[0];
	}
	
	@Override
	public ByteBuffer getPayload() {
		ByteBuffer bb = ByteBuffer.allocate(3);
		bb.put((byte)53);
		bb.put((byte)1);
		bb.put((byte)type);
		bb.flip();
		return bb;
	}
	
	public byte getType() {
		return type;
	}
	
	@Override
	public String toString() {
		switch (type) {
		case DHCPDISCOVER: return "MessageType type=DHCPDISCOVER";
		case DHCPOFFER: return "MessageType type=DHCPOFFER";
		case DHCPREQUEST: return "MessageType type=DHCPREQUEST";
		case DHCPDECLINE: return "MessageType type=DHCPDECLINE";
		case DHCPACK: return "MessageType type=DHCPACK";
		case DHCPNAK: return "MessageType type=DHCPNAK";
		case DHCPRELEASE: return "MessageType type=DHCPRELEASE";
		}
		return "MessageType type=unknown";
	}
}
