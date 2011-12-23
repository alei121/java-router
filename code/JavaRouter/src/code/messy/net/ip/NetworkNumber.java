/*
 * Created on Sep 5, 2008
 */
package code.messy.net.ip;

import java.net.Inet4Address;
import java.net.InetAddress;

import code.messy.net.ip.util.Mask;

public class NetworkNumber {
    InetAddress address;
    InetAddress mask;
    int prefix;

    public NetworkNumber(InetAddress address, InetAddress mask) {
        this.address = Mask.applyMask(address, mask);
        this.mask = mask;
        prefix = Mask.maskToPrefix(mask); 
    }

    public NetworkNumber(InetAddress address, int prefix) {
        if (address instanceof Inet4Address) {
            mask = Mask.prefixToMask(4, prefix);
        }
        else {
            mask = Mask.prefixToMask(16, prefix);
        }
        this.address = Mask.applyMask(address, mask);
        this.prefix = prefix;
    }

    @Override
    public int hashCode() {
        return address.hashCode() ^ prefix;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        NetworkNumber other = (NetworkNumber)obj;
        if (other.prefix != prefix) return false;
        if (!other.address.equals(address)) return false;
        return true;
    }

    public InetAddress getAddress() {
        return address;
    }

    public InetAddress getMask() {
        return mask;
    }

    public int getPrefix() {
        return prefix;
    }
    
    @Override
    public String toString() {
        return address + "/" + prefix;
    }
}
