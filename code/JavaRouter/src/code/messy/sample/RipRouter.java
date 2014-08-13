/*
 * Created on Sep 2, 2008
 */
package code.messy.sample;

import java.net.InetAddress;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetIpPort;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.IpMapper;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.IpInputPacket.Protocol;
import code.messy.net.ip.dhcp.DhcpProcessor;
import code.messy.net.ip.icmp.IcmpHandler;
import code.messy.net.ip.rip2.RipProcessor;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.route.RouteHandler;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.udp.UdpMapper;
import code.messy.util.IpAddressHelper;

public class RipRouter {
    /*
     * Syntax: [<portname> <ip> <prefix>]...
     * e.g: java RipRouter eth1 10.0.0.2 24 eth2 11.0.0.2 24
     * 
     * This would be pure static routing if RipProcessor is removed.
     * 
     */
    public static void main(String[] args) throws Exception {
        RouteHandler route = new RouteHandler();
        DhcpProcessor dhcp = new DhcpProcessor();
        
        UdpMapper udp = new UdpMapper();
        udp.register(IpAddressHelper.BROADCAST_ADDRESS, 67, dhcp);

        IpMapper ipCommonMapper = new IpMapper();
        ipCommonMapper.register(IpInputPacket.Protocol.UDP, udp);
        ipCommonMapper.register(route);

        IcmpHandler icmp = new IcmpHandler();

        RipProcessor rip = new RipProcessor(udp);
        
        EthernetPort eths[] = new EthernetPort[2];
        
        for (int i = 0; i < 2; i++) {
        	eths[i] = new EthernetPort(args[i * 3]);
        	InetAddress ip = InetAddress.getByName(args[i * 3 + 1]);
            short prefix = Short.parseShort(args[i * 3 + 2]);
            NetworkNumber network = new NetworkNumber(ip, prefix);
            
            EthernetIpPort ethip = new EthernetIpPort(eths[i]);
            LocalSubnet subnet = LocalSubnet.create(network, ip, ethip, null);
            
            RoutingTable.getInstance().add(subnet);
            rip.addStaticRoute(subnet);
            
            dhcp.register(subnet);
            ipCommonMapper.register(ip, Protocol.ICMP, icmp);
            
            ethip.register(ipCommonMapper);
            RoutingTable.getInstance().add(subnet);

            eths[i].register(Ethertype.ARP, new ArpHandler());
        }
        
        rip.start();

        for (int i = 0; i < 2; i++) {
        	eths[i].start();
        }
        
        for (int i = 0; i < 2; i++) {
        	eths[i].join();
        }
        rip.stop();
    }
}
