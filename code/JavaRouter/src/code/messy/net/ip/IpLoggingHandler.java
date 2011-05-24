/*
 * Created on Aug 8, 2008
 */
package code.messy.net.ip;

public class IpLoggingHandler implements IpPacketHandler {
    public IpLoggingHandler() {
    }

    @Override
    public void handle(IpPacket packet) {
        System.out.println("IpLogging " + packet.getDestinationAddress());
    }
}
