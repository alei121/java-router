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
import code.messy.net.ethernet.EthernetIpSupport;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpProtocolHandler;
import code.messy.net.ip.NetworkNumber;
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
        List<EthernetIpSupport> ipPorts = new ArrayList<EthernetIpSupport>();
        
        RouteHandler route = new RouteHandler();

        IcmpHandler icmp = new IcmpHandler();
        IpProtocolHandler ip = new IpProtocolHandler();
        ip.register(IpPacket.Protocol.ICMP, icmp);
       
        
        EthernetIpSupport p = new EthernetIpSupport(new EthernetPort(args[0]));
        InetAddress address = InetAddress.getByName(args[1]);
        short prefix = Short.parseShort(args[2]);
        ipPorts.add(p);
        NetworkNumber network = new NetworkNumber(address, prefix);
        LocalSubnet subnet = LocalSubnet.create(network, address, p, ip);
        RoutingTable.getInstance().add(subnet);

        p = new EthernetIpSupport(new EthernetPort(args[3]));
        address = InetAddress.getByName(args[4]);
        prefix = Short.parseShort(args[5]);
        ipPorts.add(p);
        network = new NetworkNumber(address, prefix);
        subnet = LocalSubnet.create(network, address, p, ip);
        RoutingTable.getInstance().add(subnet);

        for (EthernetIpSupport port : ipPorts) {
            ArpHandler arp = new ArpHandler();
            port.getPort().register(Ethertype.ARP, arp);
            
            port.register(route);
        }
        for (EthernetIpSupport port : ipPorts) {
        	port.getPort().start();
        }
        for (EthernetIpSupport port : ipPorts) {
            try {
            	port.getPort().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
