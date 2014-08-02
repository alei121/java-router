/*
 * Created on Aug 26, 2008
 */
package code.messy.sample;

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
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.route.RouteHandler;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.udp.UdpHandler;

public class StaticRouting {

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
        IcmpHandler icmp = new IcmpHandler();
        EthernetPort eths[] = new EthernetPort[2];
        IpProtocolHandler protocol = new IpProtocolHandler();
        protocol.register(IpPacket.Protocol.ICMP, icmp);
    	
        for (int i = 0; i < 2; i++) {
        	eths[i] = new EthernetPort(args[i * 3]);
        	InetAddress ip = InetAddress.getByName(args[i * 3 + 1]);
            short prefix = Short.parseShort(args[i * 3 + 2]);
            NetworkNumber network = new NetworkNumber(ip, prefix);
            
            EthernetIpSupport ethip = new EthernetIpSupport(eths[i]);
            LocalSubnet subnet = LocalSubnet.create(network, ip, ethip, protocol);

            UdpHandler udp = new UdpHandler();
            DhcpHandler dhcp = new DhcpHandler(subnet);
            udp.add(null, 67, dhcp);
            IpBroadcastHandler broadcast = new IpBroadcastHandler(udp, route);
            
            ethip.register(broadcast);
            RoutingTable.getInstance().add(subnet);

            eths[i].register(Ethertype.ARP, new ArpHandler());
        }
        
        for (int i = 0; i < 2; i++) {
        	eths[i].start();
        }
        
        for (int i = 0; i < 2; i++) {
        	eths[i].join();
        }
    }
}
