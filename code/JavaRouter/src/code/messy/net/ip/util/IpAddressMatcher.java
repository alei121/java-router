package code.messy.net.ip.util;

import java.net.InetAddress;

import code.messy.Matcher;
import code.messy.net.ip.IpPacket;

public class IpAddressMatcher implements Matcher<IpPacket> {
	private InetAddress address;
	
	public IpAddressMatcher(InetAddress address) {
		this.address = address;
	}
	
	@Override
	public boolean match(IpPacket item) {
		if (address.equals(item.getSourceAddress()) ||
			address.equals(item.getDestinationAddress())) {
			return true;
		}
		return false;
	}

}
