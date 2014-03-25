/*
 * Created on Aug 6, 2008
 */
package code.messy.net.ip;

import code.messy.Receiver;
import code.messy.net.Packet;

public class IpToPacket implements Receiver<IpPacket> {
    Receiver<Packet> ph;

    public IpToPacket(Receiver<Packet> ph) {
        this.ph = ph;
    }

    @Override
    public void receive(IpPacket ip) {
        ph.receive(ip.getPacket());
    }

}
