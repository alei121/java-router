/*
 * Created on Sep 2, 2008
 */
package code.messy.sample;

import java.net.InetAddress;

import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetIpSupport;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpMapper;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.dhcp.DhcpHandler;
import code.messy.net.ip.icmp.IcmpHandler;
import code.messy.net.ip.rip2.RipProcessor;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.route.RouteHandler;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.udp.UdpMapper;
import code.messy.util.IpAddressHelper;

public class RipRouting {
    /**
     * Syntax: <portname> <ip> <prefix>
     * e.g: java RipRouting eth1 10.0.0.2 24 eth2 11.0.0.2 24
     * 
     * Routing test
     * 
     * TODO need to re-org ip address, network address, prefix, mask, port/network 
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        RouteHandler route = new RouteHandler();
        
        UdpMapper udp = new UdpMapper();

        IpMapper ipCommonMapper = new IpMapper();
        ipCommonMapper.register(IpPacket.Protocol.UDP, udp);
        ipCommonMapper.register(route);

        IcmpHandler icmp = new IcmpHandler();
        IpMapper ipLocalMapper = new IpMapper();
        ipLocalMapper.register(IpPacket.Protocol.ICMP, icmp);

        RipProcessor rip = new RipProcessor(udp);
        
        EthernetPort eths[] = new EthernetPort[2];
        
        for (int i = 0; i < 2; i++) {
        	eths[i] = new EthernetPort(args[i * 3]);
        	InetAddress ip = InetAddress.getByName(args[i * 3 + 1]);
            short prefix = Short.parseShort(args[i * 3 + 2]);
            NetworkNumber network = new NetworkNumber(ip, prefix);
            
            EthernetIpSupport ethip = new EthernetIpSupport(eths[i]);
            LocalSubnet subnet = LocalSubnet.create(network, ip, ethip, ipLocalMapper);
            
            RoutingTable.getInstance().add(subnet);
            rip.addStaticRoute(subnet);
            
            UdpMapper udpForBroadcast = new UdpMapper();
            DhcpHandler dhcp = new DhcpHandler(subnet);
            udpForBroadcast.add(IpAddressHelper.BROADCAST_ADDRESS, 67, dhcp);
            udp.add(ip, 67, dhcp);
            IpMapper ipBroadcastMapper = new IpMapper();
            ipBroadcastMapper.register(IpAddressHelper.BROADCAST_ADDRESS, IpPacket.Protocol.UDP, udpForBroadcast);
            ipBroadcastMapper.register(ipCommonMapper);
            
            ethip.register(ipBroadcastMapper);
            RoutingTable.getInstance().add(subnet);

            eths[i].register(Ethertype.ARP, new ArpHandler());
        }
        
        
        
        
        
        /*
        List<EthernetIpSupport> ports = new ArrayList<EthernetIpSupport>();
        
        
        EthernetIpSupport p = new EthernetIpSupport(new EthernetPort(args[0]));
        InetAddress address = InetAddress.getByName(args[1]);
        short prefix = Short.parseShort(args[2]);
        ports.add(p);
        NetworkNumber network = new NetworkNumber(address, prefix);
        LocalSubnet direct = LocalSubnet.create(network, address, p, protocol);
        RoutingTable.getInstance().add(direct);
        rip.addStaticRoute(direct);

        p = new EthernetIpSupport(new EthernetPort(args[3]));
        address = InetAddress.getByName(args[4]);
        prefix = Short.parseShort(args[5]);
        ports.add(p);
        network = new NetworkNumber(address, prefix);
        direct = LocalSubnet.create(network, address, p, protocol);
        RoutingTable.getInstance().add(direct);
        rip.addStaticRoute(direct);

        // skip routing if multicast
        IpMulticastHandler multicast = new IpMulticastHandler(protocol, route);
        
        rip.start();
        
        for (EthernetIpSupport port : ports) {
            ArpHandler arp = new ArpHandler();
            port.getPort().register(Ethertype.ARP, arp);
            port.register(multicast);
        }

        for (EthernetIpSupport ep : ports) {
            ep.getPort().start();
        }
        for (EthernetIpSupport ep : ports) {
            try {
                ep.getPort().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */

        for (int i = 0; i < 2; i++) {
        	eths[i].start();
        }
        
        for (int i = 0; i < 2; i++) {
        	eths[i].join();
        }
        rip.stop();
    }
}
