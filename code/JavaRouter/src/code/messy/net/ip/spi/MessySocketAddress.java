package code.messy.net.ip.spi;

import java.net.SocketAddress;

public class MessySocketAddress extends SocketAddress {
	private static final long serialVersionUID = 1L;
	
	private int port;
	
	public MessySocketAddress(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}
}
