/*
 * Created on Aug 6, 2008
 */
package code.messy.net.filter;

import java.net.InetAddress;

import code.messy.Handler;
import code.messy.net.ip.IpPacket;

public class IpPacketFilter implements Handler<IpPacket> {

	Handler<IpPacket> match;
	Handler<IpPacket> unmatch;
    InetAddress address;

    public IpPacketFilter(InetAddress address, Handler<IpPacket> matchHandler,
    		Handler<IpPacket> unmatchHandler) {
        this.address = address;
        this.match = matchHandler;
        this.unmatch = unmatchHandler;
    }

    @Override
    public void handle(IpPacket ip) {
        if (address.equals(ip.getDestinationAddress())
                || address.equals(ip.getSourceAddress())) {
            match.handle(ip);
        } else {
            unmatch.handle(ip);
        }
    }
}
