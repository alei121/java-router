/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip.icmp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.ip.IpOutputPacket;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.route.RoutingTable;
import code.messy.net.ip.route.Subnet;
import code.messy.util.Flow;

public class IcmpHandler implements Receiver<IpInputPacket> {
    @Override
    public void receive(IpInputPacket ip) {
        ByteBuffer bb = ip.getByteBuffer();
        int offset = ip.getDataOffset();
        int length = ip.getDataLength();
        bb.position(offset);
        byte type = bb.get(offset);

        if (type != 8) {
            Flow.trace("IcmpHandler: Unsupported operation");
            return;
        }
        if (IpInputPacket.getChecksum(bb, offset, length) != 0) {
            Flow.trace("IcmpHandler: Invalid ICMP checksum");
            return;
        }

        Flow.trace("IcmpHandler: echo request. Length=" + length);
        try {
        	bb.position(offset);
        	IcmpOutputPacket icmp = new IcmpOutputPacket(bb);
            InetAddress dst = ip.getSourceAddress();
            Subnet subnet = RoutingTable.getInstance().getSubnetByMasking(dst);
            
            IpOutputPacket output = new IpOutputPacket(subnet.getSrcAddress(),
                    dst, IpInputPacket.Protocol.ICMP, icmp);
            subnet.send(dst, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
