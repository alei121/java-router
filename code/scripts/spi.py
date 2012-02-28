# Testing Java SPI socket stuff

from code.messy.net.ethernet.bridge import Bridge
from code.messy import Handler
from code.messy.net.ethernet import Ethertype, ArpHandler
from code.messy.net.ip import IpProtocolHandler, PacketToIp, IpPacket
from code.messy.net.ip.icmp import IcmpHandler
from code.messy.net.ip.route import LocalSubnet
from code.messy.net.ip.tcp import IpToTcp
from code.messy.net.ip.spi import TcpEntryHandler

from java.net import InetAddress
from java.nio.channels.spi import SelectorProvider
from java.nio.channels import ServerSocketChannel
from java.nio.channels import SocketChannel
from java.nio import ByteBuffer

import time

address = InetAddress.getByName('10.1.0.2')

ip2Tcp = IpToTcp(TcpEntryHandler.getInstance())
protocol = IpProtocolHandler()

protocol.register(IpPacket.Protocol.TCP, ip2Tcp)
pak2Ip = PacketToIp(protocol)

arp = ArpHandler()

LocalSubnet.create(address, 24, interface['eth1'])


interface['eth1'].register(Ethertype.ARP, arp)
interface['eth1'].register(Ethertype.IP, pak2Ip)
interface['eth1'].start()

# Java Channel
print("opening")
ssc = ServerSocketChannel.open()
sc = ssc.accept()
print("accepted. and sleeping");
time.sleep(10)
bb = ByteBuffer.allocate(1024)
len = sc.read(bb)
bb.flip()
print("len=" + str(len) + " remain=" + str(bb.remaining()) + " limit=" + str(bb.limit()))
print(bb.get())
