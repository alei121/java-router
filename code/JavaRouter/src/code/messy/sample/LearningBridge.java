/*
 * Created on Aug 26, 2008
 */
package code.messy.sample;

import java.util.ArrayList;
import java.util.List;

import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.bridge.Bridge;

public class LearningBridge {

    /**
     * java Main eth1 eth2
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        List<EthernetPort> ports = new ArrayList<>();
        ports.add(new EthernetPort(args[0]));
        ports.add(new EthernetPort(args[1]));
    	Bridge bridge = new Bridge("MyBridge", ports);

        for (EthernetPort port : ports) {
			port.register(bridge);
		}
        for (EthernetPort port : ports) {
        	port.start();
        }
        for (EthernetPort port : ports) {
        	port.join();
        }
    }
}
