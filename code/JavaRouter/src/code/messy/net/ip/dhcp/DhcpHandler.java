package code.messy.net.ip.dhcp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.messy.Receiver;
import code.messy.net.ethernet.EthernetPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ethernet.MacAddress;
import code.messy.net.ip.IpOutputPayload;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.dhcp.option.DHCPMessageType;
import code.messy.net.ip.dhcp.option.IPAddressLeaseTime;
import code.messy.net.ip.dhcp.option.OptionIF;
import code.messy.net.ip.dhcp.option.RouterOption;
import code.messy.net.ip.dhcp.option.ServerIdentifier;
import code.messy.net.ip.dhcp.option.SubnetMask;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.udp.UdpOutputPayload;
import code.messy.net.ip.udp.UdpPacket;
import code.messy.util.ByteHelper;
import code.messy.util.Flow;
import code.messy.util.IpAddressHelper;

/*
 * Assume Ethernet only
 */
public class DhcpHandler implements Receiver<UdpPacket> {
	private InetAddress gateway;
	private NetworkNumber network;
	private Map<ByteHelper.ByteArray, Integer> mapOfHardwareToIP = new HashMap<>();
	private int nextIP;

	public DhcpHandler(LocalSubnet subnet) {
		this.network = subnet.getNetwork();
		this.gateway = subnet.getSrcAddress();
		// TODO need to be configurable and able to release and timeout
		this.nextIP = IpAddressHelper.getInt(gateway) + 1;
	}

	private void fill(DhcpMessage message, byte type) {
		message.setOp((byte) 2);

		byte[] b = Arrays.copyOf(message.getChaddr(), message.getHlen());
		ByteHelper.ByteArray hardware = new ByteHelper.ByteArray(b);
		Integer ip = mapOfHardwareToIP.get(hardware);
		if (ip == null) {
			ip = nextIP;
			mapOfHardwareToIP.put(hardware, ip);
			nextIP++;
		}
		message.setYiaddr(ip);
		
		List<OptionIF> options = new ArrayList<>();
		options.add(new DHCPMessageType(type));
		options.add(new SubnetMask(network));
		options.add(new RouterOption(gateway));
		// 10 minutes
		options.add(new IPAddressLeaseTime(600));
		options.add(new ServerIdentifier(gateway));
		// options.add(new DomainNameServerOption(gateway));
		message.setOptions(options);		
	}

	private void reply(UdpPacket udpPacket, DhcpMessage message) {
		try {
			DhcpOutputPayload dhcp = new DhcpOutputPayload(message);
			UdpOutputPayload udp = new UdpOutputPayload(67, 68, dhcp);
			IpOutputPayload ip = new IpOutputPayload(gateway, IpAddressHelper.BROADCAST_ADDRESS, IpPacket.Protocol.UDP, 1, udp);
			
			EthernetPort port = (EthernetPort)udpPacket.getPort();
			EthernetPacket packet = (EthernetPacket)udpPacket.getIp().getPacket();
			MacAddress dstMac = packet.getSourceAddress();
			port.send(dstMac, Ethertype.IP, ip);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void receive(UdpPacket udp) {
		ByteBuffer bb = udp.getByteBuffer();
		bb.position(udp.getDataOffset());
		DhcpMessage message = new DhcpMessage(bb);

		Flow.trace("DhcpHandler: message=" + message);
		if (message.getMessageType() == DHCPMessageType.DHCPDISCOVER) {
			fill(message, (byte) DHCPMessageType.DHCPOFFER);
			reply(udp, message);
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPREQUEST) {
			fill(message, (byte) DHCPMessageType.DHCPACK);
			reply(udp, message);
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPRELEASE) {
		}
	}
}
