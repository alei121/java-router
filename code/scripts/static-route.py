from code.messy.net.ethernet import Ethertype, ArpHandler, EthernetIpSupport

from code.messy.net.ip import IpProtocolHandler, IpPacket
from code.messy.net.ip.udp import UdpHandler
from code.messy.net.ip.dhcp import DhcpHandler
from code.messy.net.ip.route import LocalSubnet, RouteHandler, RoutingTable
from java.net import InetAddress

route = RouteHandler()
eth1ip = EthernetIpSupport(interface['eth1'])
eth2ip = EthernetIpSupport(interface['eth2'])

address = InetAddress.getByName('10.1.0.1');
eth1network = LocalSubnet.create(address, 24, eth1ip)
address = InetAddress.getByName('10.2.0.1');
eth2network = LocalSubnet.create(address, 24, eth2ip)

udp1 = UdpHandler()
protocol1 = IpProtocolHandler()
protocol1.register(IpPacket.Protocol.UDP, udp1)
protocol1.register(route)
dhcp1 = DhcpHandler(eth1network)
udp1.add(None, 67, dhcp1)

udp2 = UdpHandler()
protocol2 = IpProtocolHandler()
protocol2.register(IpPacket.Protocol.UDP, udp2)
protocol2.register(route)
dhcp2 = DhcpHandler(eth2network)
udp2.add(None, 67, dhcp2)

eth1ip.register(protocol1);
eth2ip.register(protocol2);

routing_table = RoutingTable.getInstance()
routing_table.add(eth1network)
routing_table.add(eth2network)

arp = ArpHandler()

interface['eth1'].register(Ethertype.ARP, arp)
interface['eth2'].register(Ethertype.ARP, arp)

interface['eth1'].start()
interface['eth2'].start()

