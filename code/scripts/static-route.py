from code.messy.net.ethernet import Ethertype, ArpHandler, EthernetIpSupport

from code.messy.net.ip import IpProtocolHandler, IpPacket
from code.messy.net.ip.udp import UdpHandler
from code.messy.net.ip.dhcp import DhcpHandler
from code.messy.net.ip.route import LocalSubnet
from java.net import InetAddress

udp = UdpHandler()
protocol = IpProtocolHandler()
protocol.register(IpPacket.Protocol.UDP, udp)

dhcp = DhcpHandler()
udp.add(None, 67, dhcp)

eth1ip = EthernetIpSupport(interface['eth1'])
eth2ip = EthernetIpSupport(interface['eth2'])

eth1ip.register(protocol);
eth2ip.register(protocol);

# arp = ArpHandler()

#address = InetAddress.getByName('10.0.0.2')
#LocalSubnet.create(address, 24, interface['eth1'])
#address = InetAddress.getByName('10.1.0.2');
#LocalSubnet.create(address, 24, interface['eth2'])

#interface['eth1'].register(Ethertype.ARP, arp)
#interface['eth2'].register(Ethertype.ARP, arp)

interface['eth1'].start()
interface['eth2'].start()
