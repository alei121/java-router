/*
 * Created on Aug 1, 2008
 */
package code.messy.net.ethernet.bridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import code.messy.Handler;
import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.ethernet.EthernetPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;

public class Bridge implements Handler<Packet> {
    List<EthernetPort> ports = new ArrayList<EthernetPort>();
    LearnedMac learnedMac = new LearnedMac();
    String name;
    
    public Bridge(String name, List<EthernetPort> ports) {
        this.name = name;
        this.ports = ports;
    }

    void sendToOthers(EthernetPacket ep) throws IOException {
    	Port port = ep.getPort();
        for (EthernetPort targetPort : ports) {
            if (targetPort != port) {
            	System.out.println("sendToOthers bridge=" + name + " send port=" + targetPort);
                targetPort.send(ep);
            }
        }
    }

	@Override
	public void handle(Packet packet) {
        try {
        	EthernetPacket ep = (EthernetPacket)packet;
            MacAddress dstMac = ep.getDestinationAddress();
            MacAddress srcMac = ep.getSourceAddress();
            Port port = ep.getPort();
            
            learnedMac.learn(srcMac, port);

            if (dstMac.isBroadcast()) {
                System.out.println("broadcast");
                sendToOthers(ep);
            } else {
                Port targetPort = learnedMac.get(dstMac);
                System.out.println("handle bridge=" + name + " targetPort=" + targetPort);
                if (targetPort == null) {
                    System.out.println("sending to others");
                    sendToOthers(ep);
                } else if (targetPort != port) {
                    // TODO this check is preventing the
                    // loop because output packets get captured also
                    System.out.println("sending to target");
                    targetPort.send(ep);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
