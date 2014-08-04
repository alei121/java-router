/*
 * Created on Aug 15, 2008
 */
package code.messy.net.ip;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.Receiver;
import code.messy.net.OutputPayload;
import code.messy.net.Port;

public interface IpLinkSupport {
	public void send(InetAddress src, InetAddress dst, IpPacket ip) throws IOException;
    void send(InetAddress src, InetAddress gw, OutputPayload payload) throws IOException;

    public Port getPort();
	public void send(InetAddress dst, IpPacket ip) throws IOException;
	public void register(Receiver<IpPacket> receiver);
}
