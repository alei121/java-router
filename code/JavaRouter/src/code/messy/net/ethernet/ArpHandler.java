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

public class ArpHandler implements Receiver<EthernetPacket> {

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
        ByteBuffer arp = ByteBuffer.allocateDirect(60);

        // Ethernet
        arp.putShort((short) 0x1);
        // IP
        arp.putShort((short) 0x800);
        // size 6
        arp.put((byte) 6);
        // size 4
        arp.put((byte) 4);
        // ARP request
        arp.putShort((short) 0x1);

        arp.put(port.getMac().getAddress());
        arp.put(srcAddress.getAddress());
        arp.put(MacAddress.ZERO.getAddress());
        arp.put(dstAddress.getAddress());

        arp.flip();

        ByteBuffer bbs[] = new ByteBuffer[1];
        bbs[0] = arp;
        port.send(MacAddress.BROADCAST, Ethertype.ARP, bbs);
    }

    @Override
    public void receive(EthernetPacket packet) {
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
                    ByteBuffer arp = ByteBuffer.allocateDirect(60);

                    // Ethernet
                    arp.putShort((short) 0x1);
                    // IP
                    arp.putShort((short) 0x800);
                    // size 6
                    arp.put((byte) 6);
                    // size 4
                    arp.put((byte) 4);
                    // ARP reply
                    arp.putShort((short) 0x2);

                    arp.put(port.getMac().getAddress());
                    arp.put(targetAddress.getAddress());
                    arp.put(senderMac.getAddress());
                    arp.put(senderAddress.getAddress());

                    arp.flip();

                    Flow.trace("ArpHandler: response. ip=" + targetAddress
                            + " mac=" + port.getMac());
                    
                    ByteBuffer bbs[] = new ByteBuffer[1];
                    bbs[0] = arp;
                    port.send(senderMac, Ethertype.ARP, bbs);
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
