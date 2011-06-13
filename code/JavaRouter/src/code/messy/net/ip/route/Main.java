/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpProtocolHandler;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.PacketToIp;
import code.messy.net.ip.icmp.IcmpHandler;

public class Main {

    /**
     * Syntax: java Main <portname> <ip> <prefix>
     * java Main eth1 10.0.0.2 24 eth2 11.0.0.2 24
     * 
     * Routing test
     * 
     * TODO need to re-org ip address, network address, prefix, mask, port/network 
     * 
     * 
     * @param args
     * @throws IOException 
     * @throws SocketException 
     */
    public static void main(String[] args) throws SocketException, IOException {
        List<EthernetPort> ports = new ArrayList<EthernetPort>();
        
        RouteHandler route = new RouteHandler();

        
        IcmpHandler icmp = new IcmpHandler();
        IpProtocolHandler ip = new IpProtocolHandler();
        ip.register(IpPacket.Protocol.ICMP, icmp);

        
        
        EthernetPort p = new EthernetPort(args[0]);
        InetAddress address = InetAddress.getByName(args[1]);
        short prefix = Short.parseShort(args[2]);
        ports.add(p);
        NetworkNumber network = new NetworkNumber(address, prefix);
        LocalSubnet subnet = LocalSubnet.create(network, address, p, ip);
        RoutingTable.getInstance().add(subnet);

        p = new EthernetPort(args[3]);
        address = InetAddress.getByName(args[4]);
        prefix = Short.parseShort(args[5]);
        ports.add(p);
        network = new NetworkNumber(address, prefix);
        subnet = LocalSubnet.create(network, address, p, ip);
        RoutingTable.getInstance().add(subnet);

        PacketToIp p2ip = new PacketToIp(route);
        
        for (EthernetPort port : ports) {
            ArpHandler arp = new ArpHandler();
            
            port.register(Ethertype.ARP, arp);
            port.register(Ethertype.IP, p2ip);
        }
        
        for (EthernetPort ep : ports) {
            ep.start();
        }
        for (EthernetPort ep : ports) {
            try {
                ep.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
