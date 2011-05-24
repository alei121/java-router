/*
 * Created on Sep 26, 2008
 */
package code.messy.net.ip.ospf2;

import java.nio.ByteBuffer;
import java.util.HashMap;

import code.messy.net.Dump;
import code.messy.net.ip.IpPacket;

/**
 * RFC 2328 OSPF v2
 * Section A.3.1 The OSPF packet header
 * 
 * @author alei
 */
public class OspfHeader {
    static private HashMap<Byte, Type> typeMap = new HashMap<Byte, Type>();
    static {
        for (Type type : Type.values()) {
            typeMap.put(type.getValue(), type);
        }
    }
    
    static public enum Type {
        Hello (1),
        DatabaseDescription (2),
        LinkStateRequst (3),
        LinkStateUpate (4),
        LinkStateAcknowledgment (5);
        
        private byte value;

        Type(int value) {
            this.value = (byte)value;
        }

        public byte getValue() {
            return value;
        }
        
        static public Type getType(byte value) {
            return typeMap.get(value);
        }
    }
    
    private int routeId;
    private int areaId;

    private short auType;
    private long authentication;
    
    static public ByteBuffer create(Type type, ByteBuffer[] payload) {
        Dump.dumpIndent();
        ByteBuffer header = ByteBuffer.allocateDirect(24);
        
        Dump.dump("OspfHeader: create type=" + type);
        
        int remain = 0;
        for (ByteBuffer bb : payload) {
            if (bb != null) remain += bb.remaining();
        }
        
        // version 2
        header.put((byte)2);
        // type
        header.put(type.getValue());
        // total length
        header.putShort((short)(remain + 24));
        
        // router id
        header.putInt(0);
        
        // area id
        header.putInt(0);

        // checksum zero for now
        header.putShort((short)0);
        // auType
        header.putShort((short)0);
        // authentication for now
        header.putLong(0);

        header.putShort(12, IpPacket.getChecksum(header, 0, 24));
        
        // TODO set auth
        
        header.flip();

        Dump.dumpDedent();
        return header;
    }
}
