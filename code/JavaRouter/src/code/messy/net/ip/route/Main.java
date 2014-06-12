/*
 * Created on Aug 26, 2008
 */
package code.messy.net.ip.route;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetIpSupport;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpBroadcastHandler;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpProtocolHandler;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.dhcp.DhcpHandler;
import code.messy.net.ip.icmp.IcmpHandler;
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
    public static void main(String[] args) throws Exception {
        RouteHandler route = new RouteHandler();

        IpProtocolHandler protocol1 = new IpProtocolHandler();
        IpProtocolHandler protocol2 = new IpProtocolHandler();
        IcmpHandler icmp = new IcmpHandler();
        protocol1.register(IpPacket.Protocol.ICMP, icmp);
        protocol2.register(IpPacket.Protocol.ICMP, icmp);
        
        EthernetPort eth1 = new EthernetPort(args[0]);
        EthernetPort eth2 = new EthernetPort(args[3]);
        InetAddress ip1 = InetAddress.getByName(args[1]);
        InetAddress ip2 = InetAddress.getByName(args[4]);
        short prefix = Short.parseShort(args[2]);
        NetworkNumber network1 = new NetworkNumber(ip1, prefix);
        prefix = Short.parseShort(args[5]);
        NetworkNumber network2 = new NetworkNumber(ip2, prefix);
        
        EthernetIpSupport eth1ip = new EthernetIpSupport(eth1);
        EthernetIpSupport eth2ip = new EthernetIpSupport(eth2);

        LocalSubnet subnet1 = LocalSubnet.create(network1, ip1, eth1ip, protocol1);
        LocalSubnet subnet2 = LocalSubnet.create(network2, ip2, eth2ip, protocol2);

        UdpHandler udp1 = new UdpHandler();
        UdpHandler udp2 = new UdpHandler();
        DhcpHandler dhcp1 = new DhcpHandler(subnet1);
        DhcpHandler dhcp2 = new DhcpHandler(subnet2);
        udp1.add(null, 67, dhcp1);
        udp2.add(null, 67, dhcp2);
        IpBroadcastHandler broadcast1 = new IpBroadcastHandler(udp1, route);
        IpBroadcastHandler broadcast2 = new IpBroadcastHandler(udp2, route);
        
        eth1ip.register(broadcast1);
        eth2ip.register(broadcast2);
        
        RoutingTable.getInstance().add(subnet1);
        RoutingTable.getInstance().add(subnet2);

        eth1.register(Ethertype.ARP, new ArpHandler());
        eth2.register(Ethertype.ARP, new ArpHandler());
        
        eth1.start();
        eth2.start();
        eth1.join();
        eth2.join();
    }
}
