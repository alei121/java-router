package code.messy.net.ethernet;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import code.messy.Receiver;
import code.messy.net.OutputPayload;
import code.messy.net.ip.IpLinkSupport;
import code.messy.net.ip.IpPacket;
import code.messy.util.Flow;
import code.messy.util.IpAddressHelper;

public class EthernetIpSupport implements IpLinkSupport {
    EthernetPort port;

    public EthernetIpSupport(EthernetPort port) throws IOException {
    	this.port = port;
    }
    
    @Override
    public EthernetPort getPort() {
		return port;
	}

    @Override
    public String toString() {
        return "EthernetIpSupport(port=" + port + ")";
    }

	@Override
	public void send(InetAddress src, InetAddress dst, IpPacket ip)
			throws IOException {
        Flow.trace("EthernetIpSupport.send: dst=" + dst);

        MacAddress dstMac;
        if (IpAddressHelper.isBroadcast(dst)) {
        	dstMac = MacAddress.BROADCAST;
        }
        else if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        }
        else {
            dstMac = ArpHandler.getAddress(src, dst, port);
            if (dstMac == null) {
                Flow.trace("EthernetIpSupport.send: Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                return;
            }
        }
        port.send(dstMac, Ethertype.IP, ip.getPacket());
	}
	
	
    @Override
    public void send(InetAddress dst, IpPacket ip) throws IOException {
    	send(ip.getSourceAddress(), dst, ip);
    }

    @Override
    public void register(Receiver<IpPacket> receiver) {
    	port.register(Ethertype.IP, new PacketToIp(receiver));
    }

    @Override
    public void send(InetAddress src, InetAddress dst, ByteBuffer[] payload)
            throws IOException {
        Flow.trace("EthernetIpSupport.send: src=" + src + " dst=" + dst);

        MacAddress dstMac;
        if (IpAddressHelper.isBroadcast(dst)) {
        	dstMac = MacAddress.BROADCAST;
        }
        else if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        } else {
            dstMac = ArpHandler.getAddress(src, dst, port);
            if (dstMac == null) {
                Flow.trace("Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                return;
            }
        }
        port.send(dstMac, Ethertype.IP, payload);
    }


	@Override
	public void send(InetAddress src, InetAddress dst, OutputPayload payload)
			throws IOException {
        Flow.trace("EthernetIpSupport.send: src=" + src + " dst=" + dst);

        MacAddress dstMac;
        if (IpAddressHelper.isBroadcast(dst)) {
        	dstMac = MacAddress.BROADCAST;
        }
        else if (dst.isMulticastAddress()) {
            dstMac = MacAddress.getMulticast(dst);
        } else {
            dstMac = ArpHandler.getAddress(src, dst, port);
            if (dstMac == null) {
                Flow.trace("Unknown mac for " + dst
                        + ". Maybe ARP requesting.");
                return;
            }
        }
        port.send(dstMac, Ethertype.IP, payload);
	}

    class PacketToIp implements Receiver<EthernetPacket> {
    	Receiver<IpPacket> receiver;
    	
    	public PacketToIp(Receiver<IpPacket> receiver) {
    		this.receiver = receiver;
		}
    	
		@Override
		public void receive(EthernetPacket packet) {
			IpPacket ip = new IpPacket(packet, EthernetIpSupport.this);
			Flow.trace("EthernetIpSupport.receive: src="
					+ ip.getSourceAddress() + " dst="
					+ ip.getDestinationAddress());
			receiver.receive(ip);
		}
    	
    }

}
