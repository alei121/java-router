/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.udp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.TupleMap;
import code.messy.util.Flow;

public class UdpHandler implements Receiver<IpPacket> {
    static public void send(InetAddress dstAddress, int dstPort,
            ByteBuffer[] bbs) {

    }

    TupleMap<Receiver<UdpPacket>> tupleMap = new TupleMap<Receiver<UdpPacket>>();

    public void add(InetAddress dstAddress, int dstPort, Receiver<UdpPacket> ph) {
        tupleMap.add(null, dstAddress, 0, dstPort, ph);
    }

    @Override
    public void receive(IpPacket ip) {
        UdpPacket udp = new UdpPacket(ip);

        Flow.trace("UdpHandler: src=" + udp.getSrcPort() + " dst=" + udp.getDstPort());

        // TODO check checksum

        Receiver<UdpPacket> ph = tupleMap.get(ip.getSourceAddress(), ip
                .getDestinationAddress(), udp.getSrcPort(), udp.getDstPort());
        if (ph != null) {
            ph.receive(udp);
        }
        else {
            Flow.trace("UdpHandler: no handler");
        }
    }

}
