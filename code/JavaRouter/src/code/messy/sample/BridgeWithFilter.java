package code.messy.sample;

import java.util.ArrayList;
import java.util.List;

import code.messy.Filter;
import code.messy.Matcher;
import code.messy.Receiver;
import code.messy.net.ethernet.EthernetInputPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;
import code.messy.net.ethernet.bridge.Bridge;
import code.messy.util.Flow;

public class BridgeWithFilter {
	static class BroadcastMatcher implements Matcher<EthernetInputPacket> {
		@Override
		public boolean match(EthernetInputPacket packet) {
			if (MacAddress.BROADCAST.equals(packet.getDestinationAddress())) {
				return true;
			}
			return false;
		}
	}
	
	static class Printer implements Receiver<EthernetInputPacket> {
		@Override
		public void receive(EthernetInputPacket packet) {
			Flow.trace("Printer: packet=" + packet);
		}
	}

	/**
     * java Main eth1 eth2
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	BroadcastMatcher matcher = new BroadcastMatcher();
    	Printer printer = new Printer();
    	
        List<EthernetPort> ports = new ArrayList<>();
        for (String name : args) {
            ports.add(new EthernetPort(name));
		}
    	Bridge bridge = new Bridge("MyBridge", ports);

    	Filter<EthernetInputPacket> filter = new Filter<>(matcher, printer, bridge);

        for (EthernetPort port : ports) {
			port.register(filter);
		}
        for (EthernetPort port : ports) {
        	port.start();
        }
        for (EthernetPort port : ports) {
        	port.join();
        }
    }
}
