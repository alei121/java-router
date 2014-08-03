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

import code.messy.Receiver;
import code.messy.net.OutputPayload;
import code.messy.net.Packet;
import code.messy.net.ip.IpLinkSupport;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.util.Flow;

public class LocalSubnet implements Subnet {
    static List<LocalSubnet> directs = new ArrayList<LocalSubnet>();
    static HashMap<InetAddress, LocalSubnet> addressSubnetMap = new HashMap<InetAddress, LocalSubnet>();
    
    NetworkNumber network;
    InetAddress src;
    IpLinkSupport link;
    Receiver<IpPacket> localHandler = null;
    List<RemoteSubnet> remotes = new ArrayList<RemoteSubnet>();


    
    // TODO Use loopback() on port instead of localHandler
    // TODO Not sure why protected
    protected LocalSubnet(NetworkNumber network, InetAddress src,
            IpLinkSupport link, Receiver<IpPacket> localHandler) {
        this.network = network;
        this.src = src;
        this.link = link;
        this.localHandler = localHandler;
    }

    static public LocalSubnet create(NetworkNumber network, InetAddress src,
            IpLinkSupport link, Receiver<IpPacket> localHandler) {
        LocalSubnet subnet = new LocalSubnet(network, src, link, localHandler);
        directs.add(subnet);
        addressSubnetMap.put(src, subnet);
        return subnet;
    }
    
    static public LocalSubnet create(InetAddress src, int prefix, IpLinkSupport link) {
    	LocalSubnet subnet = new LocalSubnet(new NetworkNumber(src, prefix), src, link, null);
    	directs.add(subnet);
        addressSubnetMap.put(src, subnet);
    	return subnet;
    }
    
    @Override
    public void forward(IpPacket ip) throws IOException {
        InetAddress dst = ip.getDestinationAddress();

        if (addressSubnetMap.containsKey(dst)) {
            Flow.trace("LocalSubnet: locally addressed. packet=" + ip);
            if (localHandler != null) {
                localHandler.receive(ip);
            } else {
                Flow.trace("LocalSubnet: No local handle");
            }
        } else {
            Flow.trace("LocalSubnet.forward dst=" + dst + " packet=" + ip);

            Packet packet = ip.getPacket();
            packet.getByteBuffer().position(packet.getDataOffset());

            ByteBuffer bbs[] = new ByteBuffer[1];
            bbs[0] = packet.getByteBuffer();
            link.send(src, dst, ip);
//            link.send(src, dst, bbs);
        }
    }

    public void forward(InetAddress gw, IpPacket ip) throws IOException {
        // TODO need to handle case where locally addressed. like ping its own address.
        link.send(gw, ip);
    }

    @Override
    public InetAddress getSrcAddress() {
        return src;
    }

    @Override
    public void send(InetAddress dst, ByteBuffer[] bbs) throws IOException {
        Flow.trace("LocalSubnet: send dst=" + dst);
        link.send(src, dst, bbs);
    }
    
    @Override
    public void send(InetAddress dst, OutputPayload payload) throws IOException {
        Flow.trace("LocalSubnet: send dst=" + dst);
        link.send(src, dst, payload);
    }

    protected void add(RemoteSubnet remote) {
        remotes.add(remote);
    }

    public static List<LocalSubnet> getSubnets() {
        return directs;
    }

    @Override
    public NetworkNumber getNetwork() {
        return network;
    }

    public List<RemoteSubnet> getRemotes() {
        return remotes;
    }
    
    static public LocalSubnet getSubnet(InetAddress address) {
        return addressSubnetMap.get(address);
    }

    @Override
    public String toString() {
    	return "LocalSubnet(network=" + network + ", src="+ src + ", link=" + link + ")";
    }

    public IpLinkSupport getLink() {
        return link;
    }
}
