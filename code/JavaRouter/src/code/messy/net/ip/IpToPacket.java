/*
 * Created on Aug 6, 2008
 */
package code.messy.net.ip;

import code.messy.Handler;
import code.messy.net.Packet;

public class IpToPacket implements Handler<IpPacket> {
    Handler<Packet> ph;

    public IpToPacket(Handler<Packet> ph) {
        this.ph = ph;
    }

    @Override
    public void handle(IpPacket ip) {
        ph.handle(ip.getPacket());
    }

}
