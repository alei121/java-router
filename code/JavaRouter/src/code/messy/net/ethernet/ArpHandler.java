/*
 * Created on Apr 29, 2008
 */
package code.messy.net.ethernet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import code.messy.Receiver;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.util.Flow;

public class ArpHandler implements Receiver<EthernetInputPacket> {

    static HashMap<InetAddress, MacAddress> map = new HashMap<InetAddress, MacAddress>();

    static MacAddress getAddress(InetAddress srcAddress,
            InetAddress dstAddress, EthernetPort port) throws IOException {
        MacAddress mac = null;
        synchronized (map) {
            mac = map.get(dstAddress);
            if (mac == null) {
                // send request out, but do not wait because this is a single
                // thread operation
                request(srcAddress, dstAddress, port);
            }
        }
        return mac;
    }

    static void request(InetAddress srcAddress, InetAddress dstAddress,
            EthernetPort port) throws IOException {
    	ArpRequest req = new ArpRequest(srcAddress, dstAddress, port.getMac());
        port.send(MacAddress.BROADCAST, Ethertype.ARP, req);
    }

    @Override
    public void receive(EthernetInputPacket packet) {
        ByteBuffer bb = packet.getByteBuffer();
        int offset = packet.getDataOffset();
        bb.position(offset);
        short hwType = bb.getShort();
        short protocolType = bb.getShort();
        byte hwSize = bb.get();
        byte protocolSize = bb.get();

        if (hwType != 0x1 || protocolType != 0x800 || hwSize != 6
                || protocolSize != 4) {
            Flow.trace("Unsupported ARP message " + offset + " "
                    + hwType + " " + protocolType + " " + hwSize + " "
                    + protocolSize);
            return;
        }

        short opcode = bb.getShort();
        if (opcode == 0x1) {
            try {
                // for storing in own ARP table
                MacAddress senderMac = new MacAddress(bb);
                byte[] ipBytes = new byte[4];
                bb.get(ipBytes);
                InetAddress senderAddress = InetAddress.getByAddress(ipBytes);

                if (map.get(senderAddress) == null) {
                    // TODO Need timer expire for ARP entries
                	map.put(senderAddress, senderMac);
                	Flow.trace("ArpHandler: Request. Learned ip=" + senderAddress
                			+ " mac=" + senderMac);
                }

                // skipping empty dst mac
                bb.position(bb.position() + 6);

                // trying to match dstIP to port address
                bb.get(ipBytes);
                InetAddress targetAddress = InetAddress.getByAddress(ipBytes);

                EthernetPort port = (EthernetPort) packet.getPort();

                LocalSubnet subnet = LocalSubnet.getSubnet(targetAddress);
                if (subnet != null && port == subnet.getLink().getPort()) {
                    Flow.trace("ArpHandler: Reply. ip=" + targetAddress
                            + " mac=" + port.getMac());

                    ArpReply reply = new ArpReply(senderAddress, targetAddress, senderMac, port.getMac());
                    port.send(senderMac, Ethertype.ARP, reply);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (opcode == 0x2) {
            try {
                // for storing in own ARP table
                MacAddress srcMac = new MacAddress(bb);
                byte[] ipBytes = new byte[4];
                bb.get(ipBytes);
                InetAddress srcAddress = InetAddress.getByAddress(ipBytes);

                if (map.get(srcAddress) == null) {
                	// TODO Need timer expire for ARP entries
                	map.put(srcAddress, srcMac);
                	Flow.trace("ARP response: Learned ip=" + srcAddress
                			+ " mac=" + srcMac);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
