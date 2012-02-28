package code.messy.net.ip.spi;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class TcpChannel extends SocketChannel {
	SocketHandler handler;
	
	public TcpChannel(SelectorProvider provider, SocketHandler handler) {
		super(provider);
		this.handler = handler;
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
	public SocketChannel bind(SocketAddress local) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> SocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownInput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownOutput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket socket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnectionPending() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect(SocketAddress remote) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finishConnect() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		return handler.read(dst);
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
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
