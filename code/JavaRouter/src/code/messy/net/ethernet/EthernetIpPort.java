package code.messy.net.ethernet;

import java.io.IOException;
import java.net.InetAddress;

import code.messy.Receiver;
import code.messy.net.Payload;
import code.messy.net.ip.IpPort;
import code.messy.net.ip.IpInputPacket;
import code.messy.util.Flow;
import code.messy.util.IpAddressHelper;

public class EthernetIpPort implements IpPort {
    EthernetPort port;

    public EthernetIpPort(EthernetPort port) throws IOException {
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
    public void register(Receiver<IpInputPacket> receiver) {
    	port.register(Ethertype.IP, new PacketToIp(receiver));
    }

	@Override
	public void send(InetAddress src, InetAddress dst, Payload payload)
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

    class PacketToIp implements Receiver<EthernetInputPacket> {
    	Receiver<IpInputPacket> receiver;
    	
    	public PacketToIp(Receiver<IpInputPacket> receiver) {
    		this.receiver = receiver;
		}
    	
		@Override
		public void receive(EthernetInputPacket packet) {
			IpInputPacket ip = new IpInputPacket(packet, EthernetIpPort.this);
			Flow.trace("EthernetIpSupport.receive: src="
					+ ip.getSourceAddress() + " dst="
					+ ip.getDestinationAddress());
			receiver.receive(ip);
		}
    	
    }

}
