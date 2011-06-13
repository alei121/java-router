/*
 * Created on Sep 2, 2008
 */
package code.messy.net.ip.rip2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpMulticastHandler;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpProtocolHandler;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.PacketToIp;
import code.messy.net.ip.icmp.IcmpHandler;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.route.RouteHandler;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.udp.UdpHandler;

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
        
        UdpHandler udp = new UdpHandler();

        IcmpHandler icmp = new IcmpHandler();
        IpProtocolHandler protocol = new IpProtocolHandler();
        protocol.register(IpPacket.Protocol.ICMP, icmp);
        protocol.register(IpPacket.Protocol.UDP, udp);

        RipProcessor rip = new RipProcessor(udp);
        
        
        EthernetPort p = new EthernetPort(args[0]);
        InetAddress address = InetAddress.getByName(args[1]);
        short prefix = Short.parseShort(args[2]);
        ports.add(p);
        NetworkNumber network = new NetworkNumber(address, prefix);
        LocalSubnet direct = LocalSubnet.create(network, address, p, protocol);
        RoutingTable.getInstance().add(direct);
        rip.addStaticRoute(direct);


        p = new EthernetPort(args[3]);
        address = InetAddress.getByName(args[4]);
        prefix = Short.parseShort(args[5]);
        ports.add(p);
        network = new NetworkNumber(address, prefix);
        direct = LocalSubnet.create(network, address, p, protocol);
        RoutingTable.getInstance().add(direct);
        rip.addStaticRoute(direct);

        // skip routing if multicast
        IpMulticastHandler multicast = new IpMulticastHandler(protocol, route);
        
        PacketToIp p2ip = new PacketToIp(multicast);

        rip.start();
        
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
        
        rip.stop();
    }
}
