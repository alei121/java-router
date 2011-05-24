/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip.rip2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

import code.messy.net.Dump;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.udp.UdpPacket;
import code.messy.net.ip.udp.UdpPacketHandler;

public class RipHandler implements UdpPacketHandler {
    HashSet<RipEntry> ripEntries = new HashSet<RipEntry>();
    
    InetAddress zeroAddress;

    private RipProcessor ripProcessor;
    
    public RipHandler(RipProcessor ripProcessor) {
        this.ripProcessor = ripProcessor;
        
        byte[] b = new byte[4];
        Arrays.fill(b, (byte)0);
        try {
            zeroAddress = InetAddress.getByAddress(b);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(UdpPacket udp) {
        Dump.dumpIndent();
        Dump.dump("RipHandler: handling...");

        try {
            ByteBuffer bb = udp.getByteBuffer();
            bb.position(udp.getDataOffset());

            // command
            int command = bb.get();
            // version
            bb.get();
            // zero
            bb.get();
            // zero
            bb.get();

            if (command == 2) {
                // rip response. learn it.
                int count = (udp.getDataLength() - 4) / 20;

                for (int i = 0; i < count; i++) {
                    // TODO check family
                    bb.getShort();
                    // TODO check tag
                    bb.getShort();

                    byte[] b = new byte[4];
                    bb.get(b);
                    InetAddress address = InetAddress.getByAddress(b);
                    bb.get(b);
                    InetAddress mask = InetAddress.getByAddress(b);
                    bb.get(b);

                    InetAddress nexthop = InetAddress.getByAddress(b);
                    if (nexthop.equals(zeroAddress)) {
                        nexthop = udp.getIp().getSourceAddress();
                    }
                    
                    int metric = bb.getInt();

                    NetworkNumber network = new NetworkNumber(address, mask);
                    
                    // See if update needs to be skipped
                    if (metric >= 16) continue;
                    
                    ripProcessor.getRipTable().add(network, nexthop, metric + 1, udp.getPort());
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Dump.dumpDedent();
    }

}
