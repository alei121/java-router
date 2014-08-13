/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.udp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.TupleMap;
import code.messy.util.Flow;

public class UdpMapper implements Receiver<IpInputPacket> {
    static public void send(InetAddress dstAddress, int dstPort,
            ByteBuffer[] bbs) {

    }

    Receiver<IpInputPacket> ipReceiver;
    TupleMap<Receiver<UdpInputPacket>> tupleMap = new TupleMap<Receiver<UdpInputPacket>>();

    public void register(InetAddress dstAddress, int dstPort, Receiver<UdpInputPacket> ph) {
        tupleMap.add(null, dstAddress, 0, dstPort, ph);
    }
    
    public void registerFallback(Receiver<IpInputPacket> ipReceiver) {
    	this.ipReceiver = ipReceiver;
    }

    @Override
    public void receive(IpInputPacket ip) {
        UdpInputPacket udp = new UdpInputPacket(ip);

        Flow.trace("UdpMapper: src=" + udp.getSrcPort() + " dst=" + udp.getDstPort());

        // TODO check checksum

        Receiver<UdpInputPacket> ph = tupleMap.get(ip.getSourceAddress(), ip
                .getDestinationAddress(), udp.getSrcPort(), udp.getDstPort());
        if (ph != null) {
            ph.receive(udp);
        }
        else if (ipReceiver != null) {
        	ipReceiver.receive(ip);
        }
        else {
            Flow.trace("UdpMapper: no handler");
        }
    }

}
