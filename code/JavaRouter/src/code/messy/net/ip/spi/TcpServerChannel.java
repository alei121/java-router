package code.messy.net.ip.spi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class TcpServerChannel extends ServerSocketChannel {
	ServerSocketHandler handler;
	
	public TcpServerChannel(SelectorProvider provider) {
		super(provider);
		handler = new ServerSocketHandler(provider);
	}
	
	@Override
	public SocketAddress getLocalAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSocketChannel bind(SocketAddress local, int backlog)
			throws IOException {
		// TODO use AnyMatcher for now
		return null;
	}

	@Override
	public <T> ServerSocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSocket socket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel accept() throws IOException {
		try {
			return handler.getChannel();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void implCloseSelectableChannel() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void implConfigureBlocking(boolean block) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
