/*
 * Created on Apr 28, 2008
 */
package code.messy.net.bridge;

import java.io.IOException;
import java.util.List;

import code.messy.Handler;
import code.messy.net.Packet;
import code.messy.net.Port;
import code.messy.net.ethernet.EthernetPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;

/**
 * Possible improvement with per port cached entries to avoid using synchronized
 * centralized mac/port map
 * 
 * @author alei
 */
public class BridgePacketHandler implements Handler<Packet> {

    Bridge bridge;
    EthernetPort port;

    public BridgePacketHandler(Bridge bridge, EthernetPort port) {
        this.bridge = bridge;
        this.port = port;
    }

    void sendToOthers(Packet p) throws IOException {
        List<EthernetPort> ports = bridge.getPorts();
        for (EthernetPort targetPort : ports) {
            if (targetPort != port) {
                targetPort.send(p);
            }
        }
    }

    public void handle(Packet p) {
        try {
        	EthernetPacket ep = (EthernetPacket)p;
            MacAddress dstMac = ep.getDestinationAddress();
            MacAddress srcMac = ep.getSourceAddress();

            bridge.learnMac(srcMac, port);

            System.out.println("after learn");
            if (dstMac.isBroadcast()) {
                System.out.println("broadcast");
                sendToOthers(p);
            } else {
                Port targetPort = bridge.getPort(dstMac);
                System.out.println("after getPort");
                if (targetPort == null) {
                    System.out.println("sending to others");
                    sendToOthers(p);
                } else if (targetPort != port) {
                    // TODO this check is preventing the
                    // loop because output packets get captured also
                    System.out.println("sending to target");
                    targetPort.send(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "BridgePacketHandler " + port.toString();
    }
}
