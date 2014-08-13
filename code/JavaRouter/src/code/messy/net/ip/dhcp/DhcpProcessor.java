package code.messy.net.ip.dhcp;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import code.messy.Receiver;
import code.messy.net.ip.IpPort;
import code.messy.net.ip.dhcp.option.DHCPMessageType;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.udp.UdpInputPacket;
import code.messy.util.Flow;

/*
 * Simplified DHCP processing. Only handles discover and request
 * Assume Ethernet only
 */
public class DhcpProcessor implements Receiver<UdpInputPacket> {
	private Map<IpPort, DhcpEntry> mapOfIpPortToEntry = new HashMap<>();

	public void register(LocalSubnet subnet) {
		DhcpEntry entry = new DhcpEntry(subnet);
		mapOfIpPortToEntry.put(subnet.getIpPort(), entry);
	}
	
	@Override
	public void receive(UdpInputPacket udp) {
		ByteBuffer bb = udp.getByteBuffer();
		bb.position(udp.getDataOffset());
		DhcpMessage message = new DhcpMessage(bb);

		Flow.trace("DhcpProcessor: message=" + message);
		if (message.getMessageType() == DHCPMessageType.DHCPDISCOVER) {
			DhcpEntry entry = mapOfIpPortToEntry.get(udp.getIp().getIpPort());
			if (entry != null) {
				entry.fill(message, (byte) DHCPMessageType.DHCPOFFER);
				entry.reply(udp, message);
			}
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPREQUEST) {
			DhcpEntry entry = mapOfIpPortToEntry.get(udp.getIp().getIpPort());
			if (entry != null) {
				entry.fill(message, (byte) DHCPMessageType.DHCPACK);
				entry.reply(udp, message);
			}
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPRELEASE) {
		}
	}
}
