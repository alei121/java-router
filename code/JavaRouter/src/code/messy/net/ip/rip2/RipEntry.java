/*
 * Created on Sep 5, 2008
 */
package code.messy.net.ip.rip2;

import java.net.InetAddress;

import code.messy.net.ip.IpPort;
import code.messy.net.ip.NetworkNumber;
import code.messy.net.ip.route.RemoteSubnet;

public class RipEntry {
    static long ENTRY_TIMEOUT = 180000;

    NetworkNumber network;
    InetAddress nextHop;
    int metric;
    long timestamp;
    boolean isActive;
    IpPort learnedPort;

    RemoteSubnet remoteSubnet;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public RipEntry(NetworkNumber network, InetAddress nextHop, int metric,
            IpPort learnedPort) {
        this.network = network;
        this.nextHop = nextHop;
        this.metric = metric;
        this.learnedPort = learnedPort;
        this.isActive = false;
        timestamp = System.currentTimeMillis();
    }

    boolean isExpired() {
        return ((System.currentTimeMillis() - timestamp) > ENTRY_TIMEOUT);
    }

    void updateTimestamp() {
        timestamp = System.currentTimeMillis();
    }

    public int getMetric() {
        return metric;
    }

    public RemoteSubnet getRemoteSubnet() {
        if (remoteSubnet == null) {
            remoteSubnet = new RemoteSubnet(network, nextHop);
        }
        return remoteSubnet;
    }

    public NetworkNumber getNetwork() {
        return network;
    }

    public InetAddress getNextHop() {
        return nextHop;
    }

    public IpPort getIpPort() {
        return learnedPort;
    }
    
    @Override
    public String toString() {
        return network + ":" + nextHop + ":" + metric + ":" + learnedPort;
    }
}
