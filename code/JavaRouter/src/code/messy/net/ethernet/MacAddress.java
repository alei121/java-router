/*
 * Created on Apr 28, 2008
 */
package code.messy.net.ethernet;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Formatter;

public class MacAddress {
    static MacAddress BROADCAST = new MacAddress((byte)0xFF, (byte)0xFF, (byte)0xFF,
            (byte)0xFF, (byte)0xFF, (byte)0xFF);
    static MacAddress ZERO = new MacAddress((byte)0, (byte)0, (byte)0,
            (byte)0, (byte)0, (byte)0);

    private byte[] address = new byte[6];
    
    public MacAddress(ByteBuffer bb) {
        bb.get(address);
    }

    public MacAddress(byte[] b) {
        System.arraycopy(b, 0, address, 0, 6);
    }

    public MacAddress(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5) {
        address[0] = b0;
        address[1] = b1;
        address[2] = b2;
        address[3] = b3;
        address[4] = b4;
        address[5] = b5;
    }

    public boolean isBroadcast() {
        return equals(BROADCAST);
    }
    
    public byte[] getAddress() {
        return address;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        MacAddress addr2 = (MacAddress)obj;
        if (Arrays.equals(address, addr2.address)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("%02X", address[0] & 0xFF);
        for (int i = 1; i < address.length; i++) {
            sb.append('.');
            f.format("%02X", address[i] & 0xFF);
        }
        return sb.toString();
    }
    
    static public MacAddress getMulticast(InetAddress multicast) {
        byte[] b = multicast.getAddress();
        return new MacAddress((byte)0x01, (byte)0x00, (byte)0x5E, (byte)(b[1] & 0x7F), b[2], b[3]);
    }
}
