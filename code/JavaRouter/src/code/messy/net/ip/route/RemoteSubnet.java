/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.net.Dump;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;

public class RemoteSubnet implements Subnet {
    private NetworkNumber network;
    private InetAddress nextHop;
    private DirectSubnet direct;

    public RemoteSubnet(NetworkNumber network, InetAddress nextHop) {
        this.network = network;
        this.nextHop = nextHop;
        direct = (DirectSubnet) RoutingTable.getInstance()
                .getSubnetByMasking(nextHop);
        direct.add(this);
    }

    @Override
    public NetworkNumber getNetwork() {
        return network;
    }

    @Override
    public void forward(IpPacket ip) throws IOException {
        Dump.dumpIndent();
        Dump.dump("RemoteSubnet: forward dst=" + ip.getDestinationAddress());
        direct.forward(nextHop, ip);
        Dump.dumpDedent();
    }

    @Override
    public InetAddress getSrcAddress() {
        return direct.getSrcAddress();
    }

    @Override
    public void send(InetAddress dst, ByteBuffer[] bbs) throws IOException {
        // Ignoring dst and using nexthop
        direct.send(nextHop, bbs);
    }

    @Override
    public String toString() {
        return "[" + network + " " + nextHop + "]";
    }

    public InetAddress getNextHop() {
        return nextHop;
    }
}
