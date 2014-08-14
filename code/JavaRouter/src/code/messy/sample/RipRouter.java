/*
 * Created on Sep 2, 2008
 */
package code.messy.sample;

import java.net.InetAddress;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetIpPort;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpMapper;
import code.messy.net.ip.Protocol;
import code.messy.net.ip.dhcp.DhcpProcessor;
import code.messy.net.ip.icmp.IcmpHandler;
import code.messy.net.ip.rip2.RipProcessor;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.route.RouteHandler;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.udp.UdpMapper;

public class RipRouter {
    /*
     * Syntax: [<portname> <ip> <prefix>]...
     * e.g: java RipRouter eth1 10.0.0.2 24 eth2 11.0.0.2 24
     * 
     * This would be pure static routing if RipProcessor is removed.
     * 
     */
    public static void main(String[] args) throws Exception {
    	int portCount = args.length / 3;
    	
        RouteHandler route = new RouteHandler();

        UdpMapper udp = new UdpMapper();
        udp.registerFallback(route);
        RipProcessor rip = new RipProcessor(udp);
        DhcpProcessor dhcp = new DhcpProcessor(udp);

        IpMapper ipMapper = new IpMapper();
        ipMapper.register(Protocol.UDP, udp);
        ipMapper.register(route);

        IcmpHandler icmp = new IcmpHandler();
        
        EthernetPort eths[] = new EthernetPort[portCount];
        
        for (int i = 0; i < portCount; i++) {
        	eths[i] = new EthernetPort(args[i * 3]);
        	InetAddress ip = InetAddress.getByName(args[i * 3 + 1]);
            int prefix = Integer.parseInt(args[i * 3 + 2]);

            ipMapper.register(ip, Protocol.ICMP, icmp);

            EthernetIpPort ethip = new EthernetIpPort(eths[i]);
            ethip.register(ipMapper);

            LocalSubnet subnet = new LocalSubnet(ip, prefix, ethip);
            
            RoutingTable.getInstance().add(subnet);
            rip.addStaticRoute(subnet);
            
            dhcp.register(subnet);
            
            RoutingTable.getInstance().add(subnet);

            eths[i].register(Ethertype.ARP, new ArpHandler());
        }
        
        rip.start();

        for (int i = 0; i < portCount; i++) {
        	eths[i].start();
        }
        
        for (int i = 0; i < portCount; i++) {
        	eths[i].join();
        }
        rip.stop();
    }
}
