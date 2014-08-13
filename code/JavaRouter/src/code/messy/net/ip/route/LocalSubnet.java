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
import code.messy.net.InputPacket;
import code.messy.net.Payload;
import code.messy.net.ip.IpPort;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.util.Flow;

public class LocalSubnet implements Subnet {
    static List<LocalSubnet> directs = new ArrayList<LocalSubnet>();
    static HashMap<InetAddress, LocalSubnet> addressSubnetMap = new HashMap<InetAddress, LocalSubnet>();
    
    NetworkNumber network;
    InetAddress src;
    IpPort ipPort;
    Receiver<IpInputPacket> localHandler = null;
    List<RemoteSubnet> remotes = new ArrayList<RemoteSubnet>();
    
    // TODO Use loopback() on port instead of localHandler
    // TODO Not sure why protected
    protected LocalSubnet(NetworkNumber network, InetAddress src,
            IpPort ipPort, Receiver<IpInputPacket> localHandler) {
        this.network = network;
        this.src = src;
        this.ipPort = ipPort;
        this.localHandler = localHandler;
    }

    static public LocalSubnet create(NetworkNumber network, InetAddress src,
            IpPort ipPort, Receiver<IpInputPacket> localHandler) {
        LocalSubnet subnet = new LocalSubnet(network, src, ipPort, localHandler);
        directs.add(subnet);
        addressSubnetMap.put(src, subnet);
        return subnet;
    }
    
    static public LocalSubnet create(InetAddress src, int prefix, IpPort ipPort) {
    	LocalSubnet subnet = new LocalSubnet(new NetworkNumber(src, prefix), src, ipPort, null);
    	directs.add(subnet);
        addressSubnetMap.put(src, subnet);
    	return subnet;
    }
    
    @Override
    public void forward(IpInputPacket ip) throws IOException {
        InetAddress dst = ip.getDestinationAddress();

        // TODO why check all subnets? should it be this subnet?
        if (addressSubnetMap.containsKey(dst)) {
            Flow.trace("LocalSubnet: locally addressed. packet=" + ip);
            if (localHandler != null) {
                localHandler.receive(ip);
            } else {
                Flow.trace("LocalSubnet: No local handle");
            }
        } else {
            Flow.trace("LocalSubnet.forward dst=" + dst + " packet=" + ip);

            InputPacket packet = ip.getPacket();
            packet.getByteBuffer().position(packet.getDataOffset());

            ByteBuffer bbs[] = new ByteBuffer[1];
            bbs[0] = packet.getByteBuffer();
            ipPort.send(src, dst, ip);
        }
    }

    @Override
    public InetAddress getSrcAddress() {
        return src;
    }

    @Override
    public void send(InetAddress dst, Payload payload) throws IOException {
        Flow.trace("LocalSubnet: send dst=" + dst);
        ipPort.send(src, dst, payload);
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
    	return "LocalSubnet(network=" + network + ", src="+ src + ", link=" + ipPort + ")";
    }

    public IpPort getIpPort() {
        return ipPort;
    }
}
