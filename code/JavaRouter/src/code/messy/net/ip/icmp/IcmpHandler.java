/*
 * Created on Aug 12, 2008
 */
package code.messy.net.ip.icmp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.Dump;
import code.messy.net.ip.IpHeader;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.route.LocalSubnet;

public class IcmpHandler implements Receiver<IpPacket> {
    @Override
    public void receive(IpPacket ip) {
        Dump.dumpIndent();
        
        ByteBuffer bb = ip.getByteBuffer();
        int offset = ip.getDataOffset();
        int length = ip.getDataLength();
        bb.position(offset);
        byte type = bb.get(offset);

        if (type != 8) {
            Dump.dump("Unsupported operation");
            Dump.dumpDedent();
            return;
        }
        if (IpPacket.getChecksum(bb, offset, length) != 0) {
            Dump.dump("Invalid ICMP checksum");
            Dump.dumpDedent();
            return;
        }

        Dump.dump("IcmpHandler: echo request. Length=" + length);
        // Echo reply
        bb.rewind();
        try {
            ByteBuffer icmp = ByteBuffer.allocateDirect(length);
            InetAddress dst = ip.getSourceAddress();
            LocalSubnet subnet = LocalSubnet.getSubnet(ip.getDestinationAddress());
            
            // copy original
            bb.position(offset);
            icmp.put(bb);

            // echo reply
            icmp.put(0, (byte) 0);

            // set checksum zero
            icmp.putShort(2, (short) 0);
            // recalculate checksum
            icmp.putShort(2, IpPacket.getChecksum(icmp, 0, length));

            icmp.flip();
            Dump.dump("IcmpHandler: echo response");
            
            
            ByteBuffer[] bbs = new ByteBuffer[2];
            bbs[1] = icmp;
            bbs[0] = IpHeader.create(subnet.getSrcAddress(),
                    dst, IpPacket.Protocol.ICMP, 1, bbs);
            subnet.send(dst, bbs);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Dump.dumpDedent();
    }
}
