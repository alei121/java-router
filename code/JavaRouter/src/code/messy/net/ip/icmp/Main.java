/*
 * Created on Aug 11, 2008
 */
package code.messy.net.ip.icmp;

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
import code.messy.net.ip.PacketToIp;
import code.messy.net.ip.route.LocalSubnet;

public class Main {

    /**
     * Syntax: java Main <portname> <ip> <prefix>
     * 
     * java -cp bin:../../java-router/build/deploy/lib/RawSocket.jar code.messy.net.ip.icmp.Main eth1 10.0.0.2 24 eth2 10.1.0.2 24
     * 
     * @param args
     * @throws IOException 
     * @throws SocketException 
     */
    public static void main(String[] args) throws SocketException, IOException {
        List<EthernetPort> ports = new ArrayList<EthernetPort>();
        
        IcmpHandler icmp = new IcmpHandler();
        IpProtocolHandler protocol = new IpProtocolHandler();
        protocol.register(IpPacket.Protocol.ICMP, icmp);
        PacketToIp pak2Ip = new PacketToIp(protocol);

        ArpHandler arp = new ArpHandler();

        EthernetPort p = new EthernetPort(args[0]);
        InetAddress address = InetAddress.getByName(args[1]);
        int prefix = Integer.parseInt(args[2]);
        ports.add(p);
        LocalSubnet.create(address, prefix, p);

        p = new EthernetPort(args[3]);
        address = InetAddress.getByName(args[4]);
        prefix = Integer.parseInt(args[2]);
        ports.add(p);
        LocalSubnet.create(address, prefix, p);
        
        for (EthernetPort port : ports) {
            port.register(Ethertype.ARP, arp);
            port.register(Ethertype.IP, pak2Ip);
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
