/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip;

import java.util.HashMap;

public class IpProtocolHandler implements IpPacketHandler {
    public IpProtocolHandler() {
    }

    HashMap<Byte, IpPacketHandler> map = new HashMap<Byte, IpPacketHandler>();

    public void add(IpPacket.Protocol protocol, IpPacketHandler ph) {
        map.put(protocol.getValue(), ph);
    }

    @Override
    public void handle(IpPacket packet) {
        Byte protocol = packet.getProtocol();
        IpPacketHandler ph = map.get(protocol);
        if (ph != null) {
            ph.handle(packet);
        }
    }

}
