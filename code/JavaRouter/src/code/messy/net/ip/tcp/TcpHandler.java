/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.tcp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Handler;
import code.messy.net.Dump;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.TupleMap;

public class TcpHandler implements Handler<IpPacket> {
    static public void send(InetAddress dstAddress, int dstPort,
            ByteBuffer[] bbs) {

    }

    TupleMap<TcpPacketHandler> tupleMap = new TupleMap<TcpPacketHandler>();

    public void add(InetAddress dstAddress, int dstPort, TcpPacketHandler ph) {
        tupleMap.add(null, dstAddress, 0, dstPort, ph);
    }

    @Override
    public void handle(IpPacket ip) {
        Dump.dumpIndent();
        TcpPacket tcp = new TcpPacket(ip);

        Dump.dump("TcpHandler: src=" + tcp.getSrcPort() + " dst=" + tcp.getDstPort());

        // TODO check checksum

        TcpPacketHandler ph = tupleMap.get(ip.getSourceAddress(), ip
                .getDestinationAddress(), tcp.getSrcPort(), tcp.getDstPort());
        if (ph != null) {
            ph.handle(tcp);
        }
        else {
            Dump.dump("TcpHandler: no handler");
        }
        Dump.dumpDedent();
    }

}
