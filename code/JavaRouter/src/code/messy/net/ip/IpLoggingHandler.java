/*
 * Created on Aug 8, 2008
 */
package code.messy.net.ip;

import code.messy.Handler;

public class IpLoggingHandler implements Handler<IpPacket> {
    public IpLoggingHandler() {
    }

    @Override
    public void handle(IpPacket packet) {
        System.out.println("IpLogging " + packet.getDestinationAddress());
    }
}
