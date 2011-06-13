/*
 * Created on Aug 8, 2008
 */
package code.messy.net.bridge;

import java.util.ArrayList;
import java.util.List;

import code.messy.net.ethernet.EthernetPort;

/**
 * java Main eth1 eth2...
 * 
 * @author alei
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        List<EthernetPort> ports = new ArrayList<EthernetPort>();
        for (int i = 0; i < args.length; i++) {
        	System.out.println("port=" + args[i]);
        	EthernetPort ep = new EthernetPort(args[i]);
        	ports.add(ep);
        }
        
        Bridge bridge = new Bridge("MyBridge", ports);

        for (EthernetPort port : ports) {
            BridgePacketHandler bph = new BridgePacketHandler(bridge, port);
            port.register(bph);
        }

        for (EthernetPort port : ports) {
			port.start();
		}
        for (EthernetPort port : ports) {
            try {
                port.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
