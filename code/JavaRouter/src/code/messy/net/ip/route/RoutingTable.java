/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip.route;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import code.messy.net.ip.IpUtil;
import code.messy.net.ip.NetworkNumber;

public class RoutingTable {
    // TODO need "host route".
    
    static RoutingTable instance = new RoutingTable();

    static public RoutingTable getInstance() {
        return instance;
    }

    HashMap<InetAddress, HashMap<InetAddress, Subnet>> maskMap = new HashMap<InetAddress, HashMap<InetAddress, Subnet>>();
    HashSet<InetAddress> maskSet = new HashSet<InetAddress>();

    /**
     * Adding a new network route
     * 
     * Precondition: network is not in routing table
     * @param network
     * @param subnet
     * @return
     */
    public boolean add(Subnet subnet) {
        NetworkNumber network = subnet.getNetwork();
        HashMap<InetAddress, Subnet> networkMap = maskMap.get(network.getMask());

        if (networkMap == null) {
            networkMap = new HashMap<InetAddress, Subnet>();
            maskMap.put(network.getMask(), networkMap);
            maskSet.add(network.getMask());
        }
        if (networkMap.containsKey(network.getAddress())) {
            return false;
        }
        networkMap.put(network.getAddress(), subnet);
        return true;
    }

    /**
     * Precondition: network must exist in routing table
     * 
     * @param network
     * @param subnet
     */
    public void update(NetworkNumber network, Subnet subnet) {
        HashMap<InetAddress, Subnet> networkMap = maskMap.get(network.getMask());
        networkMap.put(network.getAddress(), subnet);
    }
    
    /**
     * Precondition: network must exist in routing table
     * 
     * @param network
     * @param subnet
     */
    public void remove(NetworkNumber network) {
        HashMap<InetAddress, Subnet> networkMap = maskMap.get(network.getMask());
        networkMap.remove(network.getAddress());
    }
    
    public Subnet getSubnetByMasking(InetAddress address) {
        for (InetAddress mask : maskSet) {
            InetAddress network = IpUtil.applyMask(address, mask);
            HashMap<InetAddress, Subnet> networkMap = maskMap.get(mask);
            Subnet subnet = networkMap.get(network);
            if (subnet != null)
                return subnet;
        }
        return null;
    }

    public Subnet getSubnet(NetworkNumber network) {
        HashMap<InetAddress, Subnet> networkMap = maskMap.get(network.getMask());
        if (networkMap == null)
            return null;
        return networkMap.get(network.getAddress());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("RoutingTable:");
        for (InetAddress mask : maskSet) {
            HashMap<InetAddress, Subnet> networkMap = maskMap.get(mask);
            Set<Entry<InetAddress, Subnet>> set = networkMap.entrySet();
            for (Entry<InetAddress, Subnet> entry : set) {
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("/");
                sb.append(mask);
                sb.append("/");
                sb.append(entry.getValue());
            }
        }
        return sb.toString();
    }
}
