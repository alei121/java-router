/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.net.Payload;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.util.Flow;

public class RemoteSubnet implements Subnet {
    private NetworkNumber network;
    private InetAddress nextHop;
    private LocalSubnet direct;

    public RemoteSubnet(NetworkNumber network, InetAddress nextHop) {
        this.network = network;
        this.nextHop = nextHop;
        direct = (LocalSubnet) RoutingTable.getInstance()
                .getSubnetByMasking(nextHop);
    }

    @Override
    public NetworkNumber getNetwork() {
        return network;
    }

    @Override
    public void forward(IpInputPacket ip) throws IOException {
        Flow.trace("RemoteSubnet: forward dst=" + ip.getDestinationAddress());
        direct.send(nextHop, ip);
    }

    @Override
    public InetAddress getIpAddress() {
        return direct.getIpAddress();
    }

    @Override
    public void send(InetAddress dst, Payload payload) throws IOException {
        // Ignoring dst and using nexthop
        direct.send(nextHop, payload);
    }

    @Override
    public String toString() {
        return "[" + network + " " + nextHop + "]";
    }

    public InetAddress getNextHop() {
        return nextHop;
    }
}
