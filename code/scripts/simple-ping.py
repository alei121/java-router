# Example to handle ARP and Ping
#
# java -cp bin:../../java-router/build/deploy/lib/RawSocket.jar:../../java-router/build/deploy/lib/JythonRouter.jar:../../java-router/ThirdParty/jython2.5.1/jython.jar Main ../../java-router/code/scripts/preconfig.py ../../java-router/code/scripts/simple-ping.py

from code.messy.net.ethernet import Ethertype, ArpHandler
from code.messy.net.ip import IpProtocolHandler, PacketToIp, IpPacket
from code.messy.net.ip.icmp import IcmpHandler
from code.messy.net.ip.route import LocalSubnet
from java.net import InetAddress

icmp = IcmpHandler()
protocol = IpProtocolHandler()
protocol.subscribe(IpPacket.Protocol.ICMP, icmp)
pak2Ip = PacketToIp(protocol)

arp = ArpHandler()

address = InetAddress.getByName('10.0.0.2')
LocalSubnet.create(address, 24, interface['eth1'])

address = InetAddress.getByName('10.1.0.2');
LocalSubnet.create(address, 24, interface['eth2'])

interface['eth1'].subscribe(Ethertype.ARP, arp)
interface['eth2'].subscribe(Ethertype.ARP, arp)

interface['eth1'].subscribe(Ethertype.IP, pak2Ip)
interface['eth2'].subscribe(Ethertype.IP, pak2Ip)

interface['eth1'].start()
interface['eth2'].start()
