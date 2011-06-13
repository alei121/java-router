/*
 * Created on Aug 8, 2008
 */
package code.messy.net.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import code.messy.Handler;
import code.messy.net.Packet;
import code.messy.net.bridge.Bridge;
import code.messy.net.bridge.BridgePacketHandler;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ip.IpLoggingHandler;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.IpToPacket;
import code.messy.net.ip.PacketToIp;

public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        List<EthernetPort> ports = new ArrayList<EthernetPort>();
        
        EthernetPort rs = new EthernetPort(args[0]);
        ports.add(rs);
        rs = new EthernetPort(args[1]);
        ports.add(rs);
        
        Bridge bridge = new Bridge("MyBridge", ports);

        for (EthernetPort port : ports) {
            InetAddress address = InetAddress.getByName("10.1.0.2");
            BridgePacketHandler bph = new BridgePacketHandler(bridge, port);
            IpToPacket ip2pak = new IpToPacket(bph);
            IpPacketRepeater ipr = new IpPacketRepeater();
            IpLoggingHandler my = new IpLoggingHandler();
            ipr.add(ip2pak);
            ipr.add(my);
            Handler<IpPacket> ipf = new IpPacketFilter(address, ipr, ip2pak);
            Handler<Packet> ph = new PacketToIp(ipf);
            port.register(Ethertype.IP, ph);
            port.register(bph);
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
