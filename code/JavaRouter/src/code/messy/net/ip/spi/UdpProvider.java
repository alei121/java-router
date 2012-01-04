/*
 * Created on Aug 27, 2008
 */
package code.messy.net.ip.spi;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;

public class UdpProvider extends SelectorProvider {

    @Override
    public DatagramChannel openDatagramChannel() throws IOException {
        return new UdpChannel(this);
    }

    @Override
    public Pipe openPipe() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractSelector openSelector() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServerSocketChannel openServerSocketChannel() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SocketChannel openSocketChannel() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public DatagramChannel openDatagramChannel(ProtocolFamily family)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
