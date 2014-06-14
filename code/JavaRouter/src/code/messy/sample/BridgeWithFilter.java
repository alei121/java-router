package code.messy.sample;

import java.util.ArrayList;
import java.util.List;

import code.messy.Filter;
import code.messy.Matcher;
import code.messy.Receiver;
import code.messy.net.ethernet.EthernetPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;
import code.messy.net.ethernet.bridge.Bridge;
import code.messy.util.Flow;

public class BridgeWithFilter {
	static class BroadcastMatcher implements Matcher<EthernetPacket> {
		@Override
		public boolean match(EthernetPacket packet) {
			if (MacAddress.BROADCAST.equals(packet.getDestinationAddress())) {
				return true;
			}
			return false;
		}
	}
	
	static class Printer implements Receiver<EthernetPacket> {
		@Override
		public void receive(EthernetPacket packet) {
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
        ports.add(new EthernetPort(args[0]));
        ports.add(new EthernetPort(args[1]));
    	Bridge bridge = new Bridge("MyBridge", ports);

    	Filter<EthernetPacket> filter = new Filter<>(matcher, printer, bridge);

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
