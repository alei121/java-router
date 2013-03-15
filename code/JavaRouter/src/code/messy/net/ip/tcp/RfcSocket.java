package code.messy.net.ip.tcp;

import java.net.InetAddress;

public class RfcSocket {
	private InetAddress address;
	private int port = 0;

	public RfcSocket() {
	}

	public RfcSocket(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public boolean isFullySpecified() {
		if (address == null) return false;
		if (port == 0) return false;
		return true;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
