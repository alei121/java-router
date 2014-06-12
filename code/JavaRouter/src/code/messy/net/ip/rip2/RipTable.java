/*
 * Created on Sep 12, 2008
 */
package code.messy.net.ip.rip2;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;

import code.messy.net.Port;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.route.RoutingTable;
import code.messy.util.Flow;

public class RipTable {
    HashMap<NetworkNumber, RipEntry> map = new HashMap<NetworkNumber, RipEntry>();

    public void add(NetworkNumber network, InetAddress nexthop, int metric,
            Port port) {
        // TODO consult RFC for different rules

        if (nexthop == null) {
            // static route. zero nexthop and 1 for metric
            RipEntry newEntry = new RipEntry(network, null, 1, port);
            map.put(network, newEntry);
            // don't need to add to main routing table
            
        } else {
            RipEntry entry = map.get(network);
            if (entry != null) {
                if (metric < entry.getMetric()) {
                    map.remove(network);
                    entry = new RipEntry(network, nexthop, metric, port);
                    map.put(network, entry);
                    // no need to add to main routing table. should be already there
                } else if (nexthop.equals(entry.getNextHop())) {
                    entry.updateTimestamp();
                    Flow.trace("RipTable: Update timestamp " + entry);
                }
            } else {
                entry = new RipEntry(network, nexthop, metric, port);
                map.put(network, entry);
                addToRoutingTable(entry);
            }
        }
    }

    private void addToRoutingTable(RipEntry entry) {
        if (RoutingTable.getInstance().add(entry.getRemoteSubnet())) {
            entry.setActive(true);
            Flow.trace("RipTable: Added entry " + entry);
        } else {
            Flow.trace("RipTable: Route entry conflict " + entry);
        }
    }

    public Collection<RipEntry> getRipEntries() {
        return map.values();
    }
}
