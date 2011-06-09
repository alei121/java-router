/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.Packet;
import code.messy.net.ip.IpLinkSupport;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;

public class DirectSubnet implements Subnet {
    static List<DirectSubnet> directs = new ArrayList<DirectSubnet>();
    static HashMap<InetAddress, DirectSubnet> addressSubnetMap = new HashMap<InetAddress, DirectSubnet>();
    
    NetworkNumber network;
    InetAddress src;
    IpLinkSupport link;
    Handler<IpPacket> localHandler = null;
    List<RemoteSubnet> remotes = new ArrayList<RemoteSubnet>();


    
    // TODO Use loopback() on port instead of localHandler
    // TODO Not sure why protected
    protected DirectSubnet(NetworkNumber network, InetAddress src,
            IpLinkSupport link, Handler<IpPacket> localHandler) {
        this.network = network;
        this.src = src;
        this.link = link;
        this.localHandler = localHandler;
    }

    static public DirectSubnet create(NetworkNumber network, InetAddress src,
            IpLinkSupport link, Handler<IpPacket> localHandler) {
        DirectSubnet subnet = new DirectSubnet(network, src, link, localHandler);
        directs.add(subnet);
        addressSubnetMap.put(src, subnet);
        return subnet;
    }
    
    static public DirectSubnet create(InetAddress src, int prefix, IpLinkSupport link) {
    	DirectSubnet subnet = new DirectSubnet(new NetworkNumber(src, prefix), src, link, null);
    	directs.add(subnet);
        addressSubnetMap.put(src, subnet);
    	return subnet;
    }
    
    @Override
    public void forward(IpPacket ip) throws IOException {
        Dump.dumpIndent();
        InetAddress dst = ip.getDestinationAddress();

        if (addressSubnetMap.containsKey(dst)) {
            Dump.dump("DirectSubnet: locally addressed. packet=" + ip);
            if (localHandler != null) {
                localHandler.handle(ip);
            } else {
                Dump.dump("SubnetLocal: No local handle");
            }
        } else {
            Dump.dump("DirectSubnet: forward " + dst + " packet=" + ip);

            Packet packet = ip.getPacket();
            packet.getByteBuffer().position(packet.getDataOffset());

            ByteBuffer bbs[] = new ByteBuffer[1];
            bbs[0] = packet.getByteBuffer();
            link.send(src, dst, bbs);
        }
        Dump.dumpDedent();
    }

    public void forward(InetAddress gw, IpPacket ip) throws IOException {
        Dump.dumpIndent();

        // TODO need to handle case where locally addressed. like ping its own
        // address.
        Dump.dump("DirectSubnet: forward gw=" + gw);
        Packet packet = ip.getPacket();
        packet.getByteBuffer().position(packet.getDataOffset());

        ByteBuffer bbs[] = new ByteBuffer[1];
        bbs[0] = packet.getByteBuffer();
        link.send(src, gw, bbs);
        
        Dump.dumpDedent();

    }

    @Override
    public InetAddress getSrcAddress() {
        return src;
    }

    @Override
    public void send(InetAddress dst, ByteBuffer[] bbs) throws IOException {
        Dump.dumpIndent();
        Dump.dump("DirectSubnet: send dst=" + dst);
        link.send(src, dst, bbs);
        Dump.dumpDedent();
    }

    protected void add(RemoteSubnet remote) {
        remotes.add(remote);
    }

    public static List<DirectSubnet> getSubnets() {
        return directs;
    }

    @Override
    public NetworkNumber getNetwork() {
        return network;
    }

    public List<RemoteSubnet> getRemotes() {
        return remotes;
    }
    
    static public DirectSubnet getSubnet(InetAddress address) {
        return addressSubnetMap.get(address);
    }

    @Override
    public String toString() {
        return "[" + network + ":" + src + ":" + link + "]";
    }

    public IpLinkSupport getLink() {
        return link;
    }
}
