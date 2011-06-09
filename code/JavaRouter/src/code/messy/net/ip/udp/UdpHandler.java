/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.udp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.TupleMap;

public class UdpHandler implements Handler<IpPacket> {
    static public void send(InetAddress dstAddress, int dstPort,
            ByteBuffer[] bbs) {

    }

    TupleMap<UdpPacketHandler> tupleMap = new TupleMap<UdpPacketHandler>();

    public void add(InetAddress dstAddress, int dstPort, UdpPacketHandler ph) {
        tupleMap.add(null, dstAddress, 0, dstPort, ph);
    }

    @Override
    public void handle(IpPacket ip) {
        Dump.dumpIndent();
        UdpPacket udp = new UdpPacket(ip);

        Dump.dump("UdpHandler: src=" + udp.getSrcPort() + " dst=" + udp.getDstPort());

        // TODO check checksum

        UdpPacketHandler ph = tupleMap.get(ip.getSourceAddress(), ip
                .getDestinationAddress(), udp.getSrcPort(), udp.getDstPort());
        if (ph != null) {
            ph.handle(udp);
        }
        else {
            Dump.dump("UdpHandler: no handler");
        }
        Dump.dumpDedent();
    }

}
