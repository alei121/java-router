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
import code.messy.net.ip.IpHeader;
import code.messy.net.ip.IpPacket;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.dhcp.option.DHCPMessageType;
import code.messy.net.ip.dhcp.option.DomainNameServerOption;
import code.messy.net.ip.dhcp.option.IPAddressLeaseTime;
import code.messy.net.ip.dhcp.option.OptionIF;
import code.messy.net.ip.dhcp.option.RouterOption;
import code.messy.net.ip.dhcp.option.ServerIdentifier;
import code.messy.net.ip.dhcp.option.SubnetMask;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.udp.UdpHeader;
import code.messy.net.ip.udp.UdpPacket;
import code.messy.util.ByteHelper;
import code.messy.util.Flow;
import code.messy.util.IpAddressHelper;

public class DhcpHandler implements Receiver<UdpPacket> {
	private InetAddress gateway;
	private NetworkNumber network;
	private LocalSubnet subnet;
	private Map<ByteHelper.ByteArray, Integer> mapOfHardwareToIP = new HashMap<>();
	private int nextIP;

	public DhcpHandler(LocalSubnet subnet) {
		this.network = subnet.getNetwork();
		this.gateway = subnet.getSrcAddress();
		this.subnet = subnet;
		// TODO need to be configurable and able to release and timeout
		this.nextIP = IpAddressHelper.getInt(gateway) + 1;
	}

	@Override
	public void receive(UdpPacket udp) {
		ByteBuffer bb = udp.getByteBuffer();
		bb.position(udp.getDataOffset());
		DhcpMessage message = new DhcpMessage(bb);

		Flow.trace("DhcpHandler: message=" + message);
		if (message.getMessageType() == DHCPMessageType.DHCPDISCOVER) {
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
			options.add(new SubnetMask(network));
			options.add(new RouterOption(gateway));
			// 10 minutes
			options.add(new IPAddressLeaseTime(600));
			options.add(new ServerIdentifier(gateway));
			options.add(new DomainNameServerOption(gateway));
			message.setOptions(options);

			try {
				ByteBuffer bbs[] = new ByteBuffer[3];
				bbs[2] = message.getPayload();
				bbs[1] = UdpHeader.create(67, 68, bbs);
				bbs[0] = IpHeader.create(gateway, IpAddressHelper.BROADCAST_ADDRESS,
						IpPacket.Protocol.UDP, 1, bbs);
				subnet.send(IpAddressHelper.BROADCAST_ADDRESS, bbs);
				
				// TODO should be unicast!!
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPREQUEST) {
		}
	}
}
