package code.messy.net.ip;

import java.util.HashMap;
import java.util.Map;

public enum Protocol {
	UNKNOWN ((byte)-1),
    ICMP ((byte)1),
    TCP ((byte)6),
    UDP ((byte)17);
    
    private byte value;
    
    private static Map<Byte, Protocol> mapOfValueToProtocol = new HashMap<Byte, Protocol>();
    static {
    	for (Protocol protocol : Protocol.values()) {
    		mapOfValueToProtocol.put(protocol.getValue(), protocol);
    	}
    }
    
    Protocol(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static Protocol getProtocol(Byte b) {
    	Protocol protocol = mapOfValueToProtocol.get(b);
    	if (protocol != null) return protocol;
    	return UNKNOWN;
    }
}