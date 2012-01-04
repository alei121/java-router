/*
 * Created on Aug 27, 2008
 */
package code.messy.net.ip.spi;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;

import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.route.Subnet;

public class UdpChannel extends DatagramChannel {
    static HashSet<Integer> srcPortSet = new HashSet<Integer>();
    static int srcPortCounter = 10000;

    InetSocketAddress isa;
    Subnet subnet;
    int srcPort;

    protected UdpChannel(SelectorProvider provider) {
        super(provider);
        System.out.println("UdpChannel: created");
    }

    @Override
    public DatagramChannel connect(SocketAddress sa) throws IOException {
        isa = (InetSocketAddress)sa;
        RoutingTable.getInstance().getSubnetByMasking(isa.getAddress());
        srcPort = srcPortCounter;
        srcPortSet.add(srcPort);
        srcPortCounter++;
        return this;
    }

    @Override
    public DatagramChannel disconnect() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int read(ByteBuffer arg0) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long read(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SocketAddress receive(ByteBuffer arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int send(ByteBuffer arg0, SocketAddress arg1) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DatagramSocket socket() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int write(ByteBuffer payload) throws IOException {
        int length = payload.remaining();
        
        ByteBuffer header = ByteBuffer.allocateDirect(8);
        header.putShort((short)srcPort);
        header.putShort((short)isa.getPort());
        header.putShort((short)(length + 8));
        
        // TODO put checksum in future
        header.putShort((short)0);

        ByteBuffer[] bbs = new ByteBuffer[2];
        bbs[0] = header;
        bbs[1] = payload;
        
        subnet.send(isa.getAddress(), bbs);
        return length;
    }

    @Override
    public long write(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void implCloseSelectableChannel() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void implConfigureBlocking(boolean arg0) throws IOException {
        // TODO Auto-generated method stub

    }

	@Override
	public MembershipKey join(InetAddress group, NetworkInterface interf)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MembershipKey join(InetAddress group, NetworkInterface interf,
			InetAddress source) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
	public DatagramChannel bind(SocketAddress local) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> DatagramChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
