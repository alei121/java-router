/*
 * Created on Aug 22, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.Receiver;
import code.messy.net.ip.IpPacket;
import code.messy.util.Flow;

public class RouteHandler implements Receiver<IpPacket> {
    @Override
    public void receive(IpPacket ip) {
        InetAddress dst = ip.getDestinationAddress();
        Subnet subnet = RoutingTable.getInstance().getSubnetByMasking(dst);
        Flow.trace("RouteHandler.receive dst=" + subnet);
        if (subnet != null) {
            try {
                subnet.forward(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
