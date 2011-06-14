from code.messy.net.ethernet.bridge import Bridge

bridge = Bridge("MyBridge", [interface['eth1'], interface['eth2']]);

interface['eth1'].register(bridge)
interface['eth2'].register(bridge)

interface['eth1'].start()
interface['eth2'].start()
