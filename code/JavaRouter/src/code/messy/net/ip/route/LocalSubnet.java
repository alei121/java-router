/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import code.messy.net.InputPacket;
import code.messy.net.Payload;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.IpPort;
import code.messy.net.ip.NetworkNumber;
import code.messy.util.Flow;

public class LocalSubnet implements Subnet {
    static HashMap<InetAddress, LocalSubnet> addressSubnetMap = new HashMap<InetAddress, LocalSubnet>();
    
    NetworkNumber network;
    InetAddress address;
    IpPort ipPort;
    
    public LocalSubnet(NetworkNumber network, InetAddress address,
            IpPort ipPort) {
        this.network = network;
        this.address = address;
        this.ipPort = ipPort;
        addressSubnetMap.put(address, this);
    }
    
    public LocalSubnet(InetAddress address, int prefix, IpPort ipPort) {
    	this(new NetworkNumber(address, prefix), address, ipPort);
    }

    @Override
    public void forward(IpInputPacket ip) throws IOException {
        InetAddress dst = ip.getDestinationAddress();

        if (address.equals(dst)) {
            Flow.trace("LocalSubnet: Ignore locally addressed. packet=" + ip);
        } else {
            Flow.trace("LocalSubnet.forward dst=" + dst + " packet=" + ip);

            InputPacket packet = ip.getPacket();
            packet.getByteBuffer().position(packet.getDataOffset());

            ByteBuffer bbs[] = new ByteBuffer[1];
            bbs[0] = packet.getByteBuffer();
            ipPort.send(address, dst, ip);
        }
    }

    @Override
    public InetAddress getIpAddress() {
        return address;
    }

    @Override
    public void send(InetAddress dst, Payload payload) throws IOException {
        Flow.trace("LocalSubnet: send dst=" + dst);
        ipPort.send(address, dst, payload);
    }

    @Override
    public NetworkNumber getNetwork() {
        return network;
    }
    
    static public LocalSubnet getSubnet(InetAddress address) {
        return addressSubnetMap.get(address);
    }

    @Override
    public String toString() {
    	return "LocalSubnet(network=" + network + ", src="+ address + ", ipPort=" + ipPort + ")";
    }

    public IpPort getIpPort() {
        return ipPort;
    }
}
