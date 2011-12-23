/*
 * Created on Apr 24, 2008
 */
package code.messy.net;

import java.nio.ByteBuffer;

public class Dump {
    public static void dump(ByteBuffer bb) {
        bb.rewind();
        int len = bb.limit();
        if (len > 40)
            len = 40;
        for (int i = 0; i < len; i++) {
            System.out.print(Integer.toHexString(bb.get() & 0xFF) + " ");
        }
        System.out.println();
    }
    
    public static void dump(ByteBuffer bb, int offset, int length) {
    	int pos = bb.position();
    	bb.position(offset);
        for (int i = 0; i < length; i++) {
            System.out.print(Integer.toHexString(bb.get() & 0xFF) + " ");
        }
        System.out.println();
        bb.position(pos);
    }

    public static void dump(byte[] b) {
        int len = b.length;
        if (len > 40) len = 40;
        for (int i = 0; i < len; i++) {
            System.out.print(Integer.toHexString(b[i] & 0xFF) + " ");
        }
        System.out.println();
    }

    public static void dump(String message, ByteBuffer bb) {
        System.out.println(message + " " + bb.remaining());
    }

    public static void dump(String message, Packet p) {
        dump(message, p.getByteBuffer());
    }

    static ThreadLocal<Integer> currentCount = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return new Integer(0);
        }
    };

    public static void dumpEntry(String message) {
        int count = currentCount.get();
        if (message != null) {
            for (int i = 0; i < count; i++) {
                System.out.print("  ");
            }
            System.out.println(message);
        }
        count++;
        currentCount.set(count);
    }

    public static void dump(String message) {
        int count = currentCount.get();
        for (int i = 0; i < count; i++) {
            System.out.print("  ");
        }
        System.out.println(message);
    }

    public static void dumpExit(String message) {
        int count = currentCount.get();
        count--;
        currentCount.set(count);
        if (message != null) {
            for (int i = 0; i < count; i++) {
                System.out.print("  ");
            }
            System.out.println(message);
        }
    }
    public static void dumpIndent() {
        int count = currentCount.get();
        count++;
        currentCount.set(count);
    }
    public static void dumpDedent() {
        int count = currentCount.get();
        count--;
        currentCount.set(count);
    }
}
