/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.route;

import java.net.InetAddress;

import code.messy.net.ip.NetworkMap;
import code.messy.net.ip.NetworkNumber;

public class RoutingTable {
    // TODO need "host route".
    
    static RoutingTable instance = new RoutingTable();

    static public RoutingTable getInstance() {
        return instance;
    }

    NetworkMap<Subnet> networkMap = new NetworkMap<>();

    public void add(Subnet subnet) {
        NetworkNumber network = subnet.getNetwork();
        networkMap.put(network, subnet);
    }
    
    public boolean contains(Subnet subnet) {
    	return networkMap.contains(subnet.getNetwork());
    }

    public Subnet getSubnetByMasking(InetAddress address) {
    	return networkMap.getByMasking(address);
    }

    @Override
    public String toString() {
        return "RoutingTable: " + networkMap.toString();
    }
}
