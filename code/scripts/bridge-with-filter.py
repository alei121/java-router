from code.messy.net.ethernet import MacAddress
from code.messy.net.ethernet.bridge import Bridge
from code.messy import Filter, Receiver, Matcher

class BroadcastMatcher(Matcher):
    def match(self, packet):
        if (MacAddress.BROADCAST.equals(packet.getDestinationAddress())):
            return True
        return False

class Printer(Receiver):
    def receive(self, packet):
        print "Printer: " + packet.toString()
        
bridge = Bridge("MyBridge", [interface['eth1'], interface['eth2']])

matcher = BroadcastMatcher()
printer = Printer()
filter = Filter(matcher, printer, bridge)

interface['eth1'].register(filter)
interface['eth2'].register(filter)

interface['eth1'].start()
interface['eth2'].start()

