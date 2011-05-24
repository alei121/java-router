/*
 * Created on Aug 1, 2008
 */
package code.messy.net.bridge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import code.messy.net.Port;
import code.messy.net.ethernet.EthernetPort;
import code.messy.net.ethernet.MacAddress;

public class Bridge {
    List<EthernetPort> Ports = new ArrayList<EthernetPort>();
    Hashtable<MacAddress, LearnedEntry> map = new Hashtable<MacAddress, LearnedEntry>();
    String name;
    
    
    public Bridge(String name, List<EthernetPort> ports) {
        this.name = name;
        this.Ports = ports;
    }

    void learnMac(MacAddress mac, Port port) {
        LearnedEntry entry = map.get(mac);
        if (entry == null) {
            entry = new LearnedEntry(mac, port);
            map.put(mac, entry);
            System.out.println("Learned " + entry);
        }
        else {
            entry.updateTimestamp();
        }
    }
    
    Port getPort(MacAddress mac) {
        Port bp = null;
        synchronized (map) {
            LearnedEntry entry = map.get(mac);
            if (entry != null) {
                if (entry.isExpired()) {
                    System.out.println("Expired " + entry);
                    map.remove(mac);
                }
                else {
                    bp = entry.getPort();
                }
            }
        }
        return bp;
    }
    
    List<EthernetPort> getPorts() {
        return Ports;
    }
    

}
