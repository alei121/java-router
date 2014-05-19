package code.messy.net.ip.dhcp.option;

public class IPAddressHelper {
	public static String toString(byte[] value, int offset) {
		if (value == null) return null;
		if (offset + 4 >= value.length) return null;
		StringBuilder sb = new StringBuilder();
		sb.append(value[offset]);
		for (int i = 1; i < 4; i++) {
			sb.append(".");
			sb.append(value[offset + i]);
		}
		return sb.toString();
	}

	public static String toString(byte[] value) {
		return toString(value, 0);
	}
}
