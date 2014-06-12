package code.messy.net.ip.dhcp;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
import code.messy.net.ip.udp.UdpPacketHandler;
import code.messy.util.Flow;
import code.messy.util.IpAddressHelper;

public class DhcpHandler implements UdpPacketHandler {
	private InetAddress gateway;
	private NetworkNumber network;
	private LocalSubnet subnet;

	public DhcpHandler(LocalSubnet subnet) {
		this.network = subnet.getNetwork();
		this.gateway = subnet.getSrcAddress();
		this.subnet = subnet;
	}

	@Override
	public void handle(UdpPacket udp) {
		ByteBuffer bb = udp.getByteBuffer();
		bb.position(udp.getDataOffset());
		DhcpMessage message = new DhcpMessage(bb);

		Flow.trace("DhcpHandler: message=" + message);
		if (message.getMessageType() == DHCPMessageType.DHCPDISCOVER) {
			message.setOp((byte) 2);

			// TODO manage address maps later
			message.setYiaddr(IpAddressHelper.getInt(gateway) + 1);
			
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (message.getMessageType() == DHCPMessageType.DHCPREQUEST) {
		}
	}
}
