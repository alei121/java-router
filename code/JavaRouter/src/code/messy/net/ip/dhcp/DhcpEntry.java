package code.messy.net.ip.dhcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.messy.net.ethernet.EthernetInputPacket;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.Ethertype;
import code.messy.net.ethernet.MacAddress;
import code.messy.net.ip.IpOutputPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.Protocol;
import code.messy.net.ip.dhcp.option.DHCPMessageType;
import code.messy.net.ip.dhcp.option.IPAddressLeaseTime;
import code.messy.net.ip.dhcp.option.OptionIF;
import code.messy.net.ip.dhcp.option.RouterOption;
import code.messy.net.ip.dhcp.option.ServerIdentifier;
import code.messy.net.ip.dhcp.option.SubnetMask;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.udp.UdpInputPacket;
import code.messy.net.ip.udp.UdpOutputPacket;
import code.messy.util.ByteHelper;
import code.messy.util.IpAddressHelper;

public class DhcpEntry {
	private InetAddress gateway;
	private NetworkNumber network;
	private Map<ByteHelper.ByteArray, Integer> mapOfHardwareToIP = new HashMap<>();
	private int nextIP;
	
	protected DhcpEntry(LocalSubnet subnet) {
		this.network = subnet.getNetwork();
		this.gateway = subnet.getIpAddress();
		// TODO need to be configurable and able to release and timeout
		this.nextIP = IpAddressHelper.getInt(gateway) + 1;
	}
	
	protected void fill(DhcpMessage message, byte type) {
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

	protected void reply(UdpInputPacket udpPacket, DhcpMessage message) {
		try {
			DhcpOutputPacket dhcp = new DhcpOutputPacket(message);
			UdpOutputPacket udp = new UdpOutputPacket(67, 68, dhcp);
			IpOutputPacket ip = new IpOutputPacket(gateway, IpAddressHelper.BROADCAST_ADDRESS, Protocol.UDP, 1, udp);
			
			EthernetPort port = (EthernetPort)udpPacket.getPort();
			EthernetInputPacket packet = (EthernetInputPacket)udpPacket.getIp().getPacket();
			MacAddress dstMac = packet.getSourceAddress();
			port.send(dstMac, Ethertype.IP, ip);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
