/*
 * Created on Aug 1, 2008
 */
package code.messy.net.ethernet.bridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import code.messy.Receiver;
import code.messy.net.Port;
import code.messy.net.ethernet.EthernetInputPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;
import code.messy.util.Flow;

public class Bridge implements Receiver<EthernetInputPacket> {
    List<EthernetPort> ports = new ArrayList<EthernetPort>();
    LearnedMac learnedMac = new LearnedMac();
    String name;
    
    public Bridge(String name, List<EthernetPort> ports) {
        this.name = name;
        this.ports = ports;
    }

    void sendToOthers(EthernetInputPacket ep) throws IOException {
    	Port port = ep.getPort();
        for (EthernetPort targetPort : ports) {
            if (targetPort != port) {
            	Flow.trace("Bridge: sendToOthers port=" + targetPort);
                targetPort.send(ep);
            }
        }
    }

	@Override
	public void receive(EthernetInputPacket packet) {
        try {
        	EthernetInputPacket ep = (EthernetInputPacket)packet;
            MacAddress dstMac = ep.getDestinationAddress();
            MacAddress srcMac = ep.getSourceAddress();
            Port port = ep.getPort();
            
            learnedMac.learn(srcMac, port);

            if (dstMac.isBroadcast()) {
                Flow.trace("Bridge: dst=broadcast");
                sendToOthers(ep);
            } else {
                Port targetPort = learnedMac.get(dstMac);
                if (targetPort == null) {
                	Flow.trace("Bridge: Unknown mac. Send to others");
                    sendToOthers(ep);
                } else if (targetPort != port) {
                    // TODO this check is preventing the
                    // loop because output packets get captured also
                	Flow.trace("Bridge: Send target=" + targetPort);
                    targetPort.send(ep);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
