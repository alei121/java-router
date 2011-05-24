/*
 * Created on Aug 29, 2008
 */
package code.messy.net.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {

    static public InetAddress applyMask(InetAddress address, InetAddress mask) {
        byte[] b = address.getAddress();
        byte[] m = mask.getAddress();
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = (byte)((b[i] & m[i]) & 0xFF);
        }
        try {
            return InetAddress.getByAddress(result);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public InetAddress prefixToMask(int byteCount, int prefix) {
        byte[] b = new byte[byteCount];
        for (int i = 0; i < b.length; i++) {
            if (prefix >= 8)
                b[i] = (byte) 0xFF;
            else if (prefix <= 0)
                b[i] = 0;
            else {
                int shift = 8 - prefix;
                b[i] = (byte) 0xFF;
                b[i] <<= shift;
            }
            prefix -= 8;
        }
        try {
            return InetAddress.getByAddress(b);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public int maskToPrefix(InetAddress mask) {
        byte[] b = mask.getAddress();
        int prefix = 0;
    
        for (int i = 0; i < b.length; i++) {
            if (b[i] == (byte)0xFF) {
                prefix += 8;
            }
            else {
                switch (b[i]) {
                case (byte)0x80: prefix += 1; break;
                case (byte)0xC0: prefix += 2; break;
                case (byte)0xE0: prefix += 3; break;
                case (byte)0xF0: prefix += 4; break;
                case (byte)0xF8: prefix += 5; break;
                case (byte)0xFC: prefix += 6; break;
                case (byte)0xFE: prefix += 7; break;
                }
                break;
            }
        }
        return prefix;
    }
}
