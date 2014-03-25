package code.messy.net.ethernet;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.Dump;
import code.messy.net.ip.IpLinkSupport;
import code.messy.net.ip.IpPacket;

public class EthernetIpSupport implements IpLinkSupport {
    EthernetPort port;

    public EthernetIpSupport(EthernetPort port) throws IOException {
    	this.port = port;
    }
    
    public EthernetPort getPort() {
		return port;
	}

    @Override
    public String toString() {
        return "EthernetIpSupport[port=" + port + "]";
    }

    @Override
    public void send(InetAddress dst, IpPacket ip) throws IOException {
        Dump.dumpIndent();
        Dump.dump("EthernetIpSupport: send dst=" + dst);

        MacAddress dstMac;
        if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        } else {
            dstMac = ArpHandler.getAddress(ip.getSourceAddress(), dst, port);
            if (dstMac == null) {
                Dump.dump("Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                Dump.dumpDedent();
                return;
            }
        }
        port.send(dstMac, Ethertype.IP, ip.getPacket());
        Dump.dumpDedent();
    }

    @Override
    public void register(Receiver<IpPacket> receiver) {
    	port.register(Ethertype.IP, new PacketToIp(receiver));
    }

    @Override
    public void send(InetAddress src, InetAddress dst, ByteBuffer[] payload)
            throws IOException {
        Dump.dumpIndent();
        Dump.dump("EthernetPort: send src=" + src + " dst=" + dst);

        MacAddress dstMac;
        if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        } else {
            dstMac = ArpHandler.getAddress(src, dst, port);
            if (dstMac == null) {
                Dump.dump("Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                Dump.dumpDedent();
                return;
            }
        }
        port.send(dstMac, Ethertype.IP, payload);
        Dump.dumpDedent();
    }
    
    class PacketToIp implements Receiver<EthernetPacket> {
    	Receiver<IpPacket> receiver;
    	
    	public PacketToIp(Receiver<IpPacket> receiver) {
    		this.receiver = receiver;
		}
    	
		@Override
		public void receive(EthernetPacket packet) {
			IpPacket ip = new IpPacket(packet, EthernetIpSupport.this);
			receiver.receive(ip);
		}
    	
    }
}
