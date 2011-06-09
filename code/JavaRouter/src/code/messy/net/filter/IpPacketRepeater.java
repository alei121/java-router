/*
 * Created on Aug 8, 2008
 */
package code.messy.net.filter;

import java.util.ArrayList;
import java.util.List;

import code.messy.Handler;
import code.messy.net.ip.IpPacket;

public class IpPacketRepeater implements Handler<IpPacket> {
    List<Handler<IpPacket>> handlers = new ArrayList<Handler<IpPacket>>();

    public IpPacketRepeater() {
    }

    public void add(Handler<IpPacket> ph) {
        handlers.add(ph);
    }

    @Override
    public void handle(IpPacket packet) {
        for (Handler<IpPacket> handler : handlers) {
            handler.handle(packet);
        }
    }
}
