/*
 * Created on Apr 29, 2008
 */
package code.messy.net.ethernet;

import java.util.HashMap;


public enum Ethertype {
    VLAN ((short)0x8100),
    IP ((short)0x800),
    ARP ((short)0x806);
    
    static HashMap<Short, Ethertype> map = new HashMap<Short, Ethertype>();
    static {
    	for (Ethertype type : Ethertype.values()) {
			map.put(type.getValue(), type);
		}
    }
    
    private short value;
    
    Ethertype(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
    
    static public Ethertype get(short value) {
    	return map.get(value);
    }
}
