package code.messy.net.ip.tcp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import code.messy.net.Dump;
import code.messy.net.ethernet.ArpHandler;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpProtocolHandler;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.PacketToIp;
import code.messy.net.ip.icmp.IcmpHandler;
import code.messy.net.ip.route.DirectSubnet;

/**
 * Syntax:
 * java Main eth1 10.0.0.2 24 eth2 11.0.0.2 24 <tcp-listen-port>
 * 
 * @param args
 */
public class Main {
	static class MyTcpPacketHandler implements TcpPacketHandler {
		@Override
		public void handle(TcpPacket tcp) {
			Dump.dumpIndent();
			Dump.dump(tcp.toString());
			Dump.dumpDedent();
		}
	}
	
	public static void main(String[] args) throws Exception {
        List<EthernetPort> ports = new ArrayList<EthernetPort>();
        
        IcmpHandler icmp = new IcmpHandler();

        TcpHandler tcp = new TcpHandler();
        MyTcpPacketHandler myTcp = new MyTcpPacketHandler();
        tcp.add(null, Integer.parseInt(args[6]), myTcp);
        
        IpProtocolHandler protocol = new IpProtocolHandler();
        protocol.add(IpPacket.Protocol.ICMP, icmp);
        protocol.add(IpPacket.Protocol.TCP, tcp);
        PacketToIp pak2Ip = new PacketToIp(protocol);

        EthernetPort p = new EthernetPort(args[0]);
        InetAddress address = InetAddress.getByName(args[1]);
        int prefix = Integer.parseInt(args[2]);
        ports.add(p);
        NetworkNumber network = new NetworkNumber(address, prefix);
        DirectSubnet.create(network, address, p, null);

        p = new EthernetPort(args[3]);
        address = InetAddress.getByName(args[4]);
        prefix = Integer.parseInt(args[2]);
        ports.add(p);
        network = new NetworkNumber(address, prefix);
        DirectSubnet.create(network, address, p, null);
        
        
        for (EthernetPort port : ports) {
            ArpHandler arp = new ArpHandler();
            
            port.subscribe(Ethertype.ARP, arp);
            port.subscribe(Ethertype.IP, pak2Ip);
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
