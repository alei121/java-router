/*
 * Created on Aug 15, 2008
 */
package code.messy.net.ip;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.Port;

public interface IpLinkSupport {
	// void loopback(ByteBuffer[] payload) throws IOException;
//    void forward(InetAddress src, InetAddress gw, IpPacket packet) throws IOException;
	
	// TODO may remove in future
    void send(InetAddress src, InetAddress gw, ByteBuffer[] payload) throws IOException;
    
    public Port getPort();
	public void send(InetAddress dst, IpPacket ip) throws IOException;
	public void register(Receiver<IpPacket> receiver);
}
