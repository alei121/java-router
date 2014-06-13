package code.messy.net.ethernet.bridge;

import java.util.Hashtable;

import code.messy.net.Port;
import code.messy.net.ethernet.MacAddress;
import code.messy.util.Flow;

public class LearnedMac {
    private static final long LEARNED_ENTRY_TIMEOUT = 5000;
    private Hashtable<MacAddress, Entry> map = new Hashtable<MacAddress, Entry>();

    public void learn(MacAddress mac, Port port) {
        Entry entry = map.get(mac);
        if (entry == null) {
            entry = new Entry(mac, port);
            map.put(mac, entry);
            Flow.trace("LearnedMac learned: entry=" + entry);
        }
        else {
            entry.updateTimestamp();
        }
    }
    
    public Port get(MacAddress mac) {
        Entry entry = map.get(mac);
        if (entry != null) {
            if (entry.isExpired()) {
            	Flow.trace("LearnedMac expired: entry=" + entry);
                map.remove(mac);
            }
            else {
                return entry.getPort();
            }
        }
        return null;
    }
    
    static private class Entry {
        MacAddress mac;
        Port port;
        long timestamp;

        public Entry(MacAddress mac, Port port) {
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
            
            Entry entry = (Entry)obj;
            if (mac.equals(entry.mac)) return true;
            return false;
        }

        @Override
        public int hashCode() {
            return mac.hashCode();
        }

        @Override
        public String toString() {
            return "LearnedMac.Entry(" + mac.toString() + ", " + port.toString() + ")";
        }
    }

}
