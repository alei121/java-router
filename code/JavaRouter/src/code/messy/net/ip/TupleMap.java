/*
 * Created on Sep 4, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * 4-tuple matching with wild-cards available Use null for "any" address Use 0
 * for "any" port
 * 
 * @author alei
 */
public class TupleMap<E> {

    class DstPortMap extends HashMap<Integer, E> {
        private static final long serialVersionUID = 1L;
    }

    class SrcPortMap extends HashMap<Integer, DstPortMap> {
        private static final long serialVersionUID = 1L;
    }

    class DstAddressMap extends HashMap<InetAddress, SrcPortMap> {
        private static final long serialVersionUID = 1L;
    }

    class SrcAddressMap extends HashMap<InetAddress, DstAddressMap> {
        private static final long serialVersionUID = 1L;
    }

    SrcAddressMap srcAddressMap = new SrcAddressMap();

    public void add(InetAddress srcAddress, InetAddress dstAddress,
            int srcPort, int dstPort, E e) {
        DstAddressMap dstAddressMap = srcAddressMap.get(srcAddress);
        if (dstAddressMap == null) {
            dstAddressMap = new DstAddressMap();
            srcAddressMap.put(srcAddress, dstAddressMap);
        }

        SrcPortMap srcPortMap = dstAddressMap.get(dstAddress);
        if (srcPortMap == null) {
            srcPortMap = new SrcPortMap();
            dstAddressMap.put(dstAddress, srcPortMap);
        }

        DstPortMap dstPortMap = srcPortMap.get(srcPort);
        if (dstPortMap == null) {
            dstPortMap = new DstPortMap();
            srcPortMap.put(srcPort, dstPortMap);
        }

        dstPortMap.put(dstPort, e);
    }

    public void add(Tuple tuple, E e) {
        add(tuple.getSrcAddress(), tuple.getDstAddress(), tuple.getSrcPort(), tuple.getDstPort(), e);
    }

    public E get(Tuple tuple) {
        return get(tuple.getSrcAddress(), tuple.getDstAddress(), tuple.getSrcPort(), tuple.getDstPort());
    }
    
    public E get(InetAddress srcAddress, InetAddress dstAddress, int srcPort,
            int dstPort) {
        DstAddressMap dstAddressMap = srcAddressMap.get(srcAddress);
        if (dstAddressMap == null) {
            dstAddressMap = srcAddressMap.get(null);
            if (dstAddressMap == null) {
                return null;
            }
        }

        SrcPortMap srcPortMap = dstAddressMap.get(dstAddress);
        if (srcPortMap == null) {
            srcPortMap = dstAddressMap.get(null);
            if (srcPortMap == null) {
                return null;
            }
        }

        DstPortMap dstPortMap = srcPortMap.get(srcPort);
        if (dstPortMap == null) {
            dstPortMap = srcPortMap.get(0);
            if (dstPortMap == null) {
                return null;
            }
        }

        E e = dstPortMap.get(dstPort);
        if (e == null) {
            e = dstPortMap.get(0);
        }
        return e;
    }
}
