/*
 * Created on Apr 28, 2008
 */
package code.messy.net.bridge;

import code.messy.net.Port;
import code.messy.net.ethernet.MacAddress;

public class LearnedEntry {
    static long LEARNED_ENTRY_TIMEOUT = 5000;

    MacAddress mac;
    Port port;
    long timestamp;

    public LearnedEntry(MacAddress mac, Port port) {
        this.mac = mac;
        this.port = port;
        timestamp = System.currentTimeMillis();
    }

    Port getPort() {
        return port;
    }
    
    boolean isExpired() {
        return ((System.currentTimeMillis() - timestamp) > LEARNED_ENTRY_TIMEOUT);
    }
    
    void updateTimestamp() {
        timestamp = System.currentTimeMillis();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        LearnedEntry entry = (LearnedEntry)obj;
        if (mac.equals(entry.mac)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return mac.hashCode();
    }

    @Override
    public String toString() {
        return "[" + mac.toString() + "," + port.toString() + "]";
    }
}
