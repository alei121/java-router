package code.messy.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import code.messy.net.ip.NetworkNumber;

public class IpAddressHelper {
	public static InetAddress BROADCAST_ADDRESS = null;
	public static InetAddress ANY_ADDRESS = null;
	public static NetworkNumber ANY_NETWORK = null;
	
	static {
		byte[] addr = new byte[4];
		try {
			addr[0] = addr[1] = addr[2] = addr[3] = (byte)0xFF;
			BROADCAST_ADDRESS = InetAddress.getByAddress(addr);
			addr[0] = addr[1] = addr[2] = addr[3] = (byte)0x00;
			ANY_ADDRESS = InetAddress.getByAddress(addr);
		} catch (UnknownHostException e) {
			// Should not happen because there is no lookup
			throw new AssertionError("Cannot get broadcast address", e);
		}
		ANY_NETWORK = new NetworkNumber(ANY_ADDRESS, 0);
	}
	
	public static int getInt(InetAddress address) {
		// TODO assuming ipv4 now
		byte[] b = address.getAddress();
		return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);
	}
	
	public static InetAddress getInetAddress(int address) throws UnknownHostException {
		// TODO assuming ipv4 now
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i++) {
			b[i] = (byte)(address & 0xFF);
			address = address >> 8;
		}
		return InetAddress.getByAddress(b);
	}

	public static boolean isBroadcast(InetAddress ip) {
		return BROADCAST_ADDRESS.equals(ip);
	}
}
