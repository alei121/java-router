from code.messy import Server
from code.messy.net.inspect import InspectIF, Forwarder

class Inspector(InspectIF):

    def __init__(self, name):
        self.name = name
        print "Inspector", name

    def inspect(self, bb):
        print "Inspector", self.name, " bb len=", bb.limit()

f1 = Forwarder(interface['eth1'], interface['eth2'])
f2 = Forwarder(interface['eth2'], interface['eth1'])

i1 = Inspector("i1")
i2 = Inspector("i2")

f1.setInspector(i1)
f2.setInspector(i2)

Server.add(f1)
Server.add(f2)

