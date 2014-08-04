/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.rip2;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import code.messy.net.ip.IpOutputPacket;
import code.messy.net.ip.IpInputPacket;
import code.messy.net.ip.route.LocalSubnet;
import code.messy.net.ip.udp.UdpMapper;
import code.messy.net.ip.udp.UdpOutputPacket;
import code.messy.util.Flow;

/**
 * 
 * About routing protocol.
 * 
 * Different subnet can run on the same interface.
 * 
 * When configuring routing protocol like RIP or OSPF, it is possible to
 * associate a subnet rather than an interface with the protocol. Thus, a single
 * interface can run both RIP and OSPF
 * 
 * In the case of RIP, it will only broadcast route info out of that subnet for
 * that interface. For receiving RIP messages, the subnet can also be determined
 * by the source address.
 * 
 * However, when a packet is forwarded to the router, the packet may be in the
 * middle of the path where the source ip address doesn't correspond to a direct
 * subnet. Even with reverse ARP on the source mac doesn't tell the subnet
 * because that mac can also contains multiple ip addresses of different subnet.
 * Thus, there is no way to tell if the packet should be routed through RIP or
 * OSFP.
 * 
 * In other words, the routing table can only be port based instead of subnet
 * based. If the routing table ever needed to be split into different routing
 * zone, it can be done. But port based only. RIP and OSPF will have to split
 * too.
 * 
 * As a result, there can be multiple routing tables. Each routing table has its
 * own RIP, OSPF... etc, where these routing protocols only updates its own
 * routing table.
 * 
 * 
 * ****************
 * 
 * Conflicts of route info
 * 
 * If OSPF and RIP is running together, there is a possibility that the same
 * network appears on both RIP and OSPF, causing a conflict. In practice, this
 * should be a configuration error, but it may very likely happen when
 * configuration has just been updated and route info is still being lingering
 * around.
 * 
 * RIP/OSPF is responsible to update the same routing table. If one puts an
 * entry in, it is also responsible for removing it. Conflicts can be detected
 * when one tries to put a new entry and an entry already exist. In this case,
 * the routing table will not be changed.
 * 
 * The methods will be: bool add(entry); where return is true if it is
 * successful, false if unsuccessful. void remove(entry);
 * 
 * In the case of RIP, if there is a conflict, the routing table will not be
 * updated. But it will still keep the entry in its own table for the 30 sec
 * route info broadcast. At each 30 sec, it will try "add" again to the routing
 * table.
 * 
 * As for OSPF, periodic broadcast is half an hour. Thus, retrying "add" at that
 * time is too slow. The hello timer by default is 10 seconds, maybe that could
 * be a place to retry.
 * 
 * Conflict routes The conflict routes that could not be added should be safe to
 * reside with the protocols' table. There are really routes for those networks,
 * but just may not be the same nexthop. The ultimate goal is for the network to
 * correct itself and resolve these conflict routes.
 * 
 * @author alei
 */
public class RipProcessor {
    Timer timer = new Timer();
    List<LocalSubnet> ripSubnets = new ArrayList<LocalSubnet>();
    List<Loop> loops = new ArrayList<Loop>();
    InetAddress multicastAddress;
    RipTable ripTable = new RipTable();

    public RipProcessor(UdpMapper handler) {
        try {
            RipHandler riph = new RipHandler(this);
            multicastAddress = InetAddress.getByName("224.0.0.9");
            handler.add(multicastAddress, 520, riph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStaticRoute(LocalSubnet localSubnet) {
        ripSubnets.add(localSubnet);
        Loop loop = new Loop(localSubnet);
        loops.add(loop);
        
        // adding to rip table
        ripTable.add(localSubnet.getNetwork(), null, 0, localSubnet.getLink().getPort());
    }

    public void start() {
        for (Loop loop : loops) {
            timer.scheduleAtFixedRate(loop, 30000, 30000);
        }
    }

    public void stop() {
        timer.cancel();
    }

    class Loop extends TimerTask {
        LocalSubnet direct;

        public Loop(LocalSubnet direct) {
            this.direct = direct;
        }

        @Override
        public void run() {

            // TODO recheck not added entries
            
            // TODO check expired entries
            
            try {
            	Flow.traceStart();
                Flow.trace("Rip2: 30 sec wakeup. network=" + direct);
                
                RipResponse message = new RipResponse(ripTable, direct);
    			UdpOutputPacket udp = new UdpOutputPacket(520, 520, message);
    			IpOutputPacket ip = new IpOutputPacket(direct.getSrcAddress(), multicastAddress, IpInputPacket.Protocol.UDP, 1, udp);
    			direct.send(multicastAddress, ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public RipTable getRipTable() {
        return ripTable;
    }
}
