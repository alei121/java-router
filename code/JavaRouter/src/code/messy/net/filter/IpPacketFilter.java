/*
 * Created on Aug 6, 2008
 */
package code.messy.net.filter;

import java.net.InetAddress;

import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpPacketHandler;

public class IpPacketFilter implements IpPacketHandler {

    IpPacketHandler match;
    IpPacketHandler unmatch;
    InetAddress address;

    public IpPacketFilter(InetAddress address, IpPacketHandler matchHandler,
            IpPacketHandler unmatchHandler) {
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
