from code.messy.net.ethernet.bridge import Bridge
from code.messy import Receiver

class Inspector(Receiver):
    def __init__(self, bypass):
        self.bypass = bypass

    def receive(self, packet):
        print "Inspect: " + packet.toString()
        self.bypass.receive(packet)

bridge = Bridge("MyBridge", [interface['eth1'], interface['eth2']]);

inspector = Inspector(bridge)

interface['eth1'].register(inspector)
interface['eth2'].register(inspector)

interface['eth1'].start()
interface['eth2'].start()
