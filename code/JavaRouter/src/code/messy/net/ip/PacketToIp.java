/*
 * Created on Aug 6, 2008
 */
package code.messy.net.ip;

import code.messy.Handler;
import code.messy.net.Packet;

public class PacketToIp implements Handler<Packet> {
    Handler<IpPacket> ipHandler;

    public PacketToIp(Handler<IpPacket> ipHandler) {
        this.ipHandler = ipHandler;
    }

    @Override
    public void handle(Packet packet) {
        IpPacket ipPacket = new IpPacket(packet);
        ipHandler.handle(ipPacket);
    }
}
