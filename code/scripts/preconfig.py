from code.messy.net.ethernet import EthernetPort
#from code.messy.net import RawSocket

interface = {}
interface['eth1'] = EthernetPort('eth1')
interface['eth2'] = EthernetPort('eth2')
#interface['eth1'] = RawSocket("eth1")
#interface['eth2'] = RawSocket("eth2")
