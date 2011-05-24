/*
 * Created on Aug 8, 2008
 */
package code.messy.net.filter;

import java.util.ArrayList;
import java.util.List;

import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpPacketHandler;

public class IpPacketRepeater implements IpPacketHandler {
    List<IpPacketHandler> handlers = new ArrayList<IpPacketHandler>();

    public IpPacketRepeater() {
    }

    public void add(IpPacketHandler ph) {
        handlers.add(ph);
    }

    @Override
    public void handle(IpPacket packet) {
        for (IpPacketHandler handler : handlers) {
            handler.handle(packet);
        }
    }
}
