/*
 * Created on Aug 8, 2008
 */
package code.messy.net.ip.util;

import code.messy.Receiver;
import code.messy.net.ip.IpPacket;

public class IpPacketPrinter implements Receiver<IpPacket> {
    public IpPacketPrinter() {
    }

    @Override
    public void receive(IpPacket packet) {
        System.out.println("IpLogging " + packet.getDestinationAddress());
    }
}
