from code.messy.net.ethernet.bridge import Bridge
from code.messy import Handler

class Inspector(Handler):
    def __init__(self, bypass):
        self.bypass = bypass

    def handle(self, packet):
        print "Packet len=" + str(packet.getByteBuffer().limit())
        self.bypass.handle(packet)

bridge = Bridge("MyBridge", [interface['eth1'], interface['eth2']]);
inspector = Inspector(bridge)

interface['eth1'].register(inspector)
interface['eth2'].register(inspector)

interface['eth1'].start()
interface['eth2'].start()
