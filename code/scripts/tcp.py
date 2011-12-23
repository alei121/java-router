from code.messy.net.ethernet.bridge import Bridge
from code.messy import Handler
from code.messy.net.ethernet import Ethertype, ArpHandler
from code.messy.net.ip import IpProtocolHandler, PacketToIp, IpPacket
from code.messy.net.ip.icmp import IcmpHandler
from code.messy.net.ip.route import LocalSubnet
from code.messy.net.ip.tcp import TcpHandler
from code.messy.net.ip.tcp import SessionHandler
from code.messy.net.ip.tcp import AnyMatcher
from code.messy.net.ip.tcp import TcbSessionHandler
from code.messy.net.ip.tcp import UnknownSessionHandler
from java.net import InetAddress

address = InetAddress.getByName('10.1.0.2')

tcp = TcpHandler()
protocol = IpProtocolHandler()
session = SessionHandler()

unknown = UnknownSessionHandler()
session.register(unknown)

tcb = TcbSessionHandler()
matcher = AnyMatcher()
session.register(matcher, tcb)

tcp.add(address, 23, session)
protocol.register(IpPacket.Protocol.TCP, tcp)
pak2Ip = PacketToIp(protocol)

arp = ArpHandler()

LocalSubnet.create(address, 24, interface['eth1'])


interface['eth1'].register(Ethertype.ARP, arp)
interface['eth1'].register(Ethertype.IP, pak2Ip)
interface['eth1'].start()
