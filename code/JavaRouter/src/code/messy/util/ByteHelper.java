package code.messy.util;

import java.nio.ByteBuffer;
import java.util.Formatter;

public class ByteHelper {
	private static final int LIMIT = 80;
	
	private static StringBuilder getStringBuilder(ByteBuffer bb, String divider, int count) {
    	StringBuilder sb = new StringBuilder();
    	Formatter formatter = new Formatter(sb);
    	int pos = bb.position();
        for (int i = 0; i < count; i++) {
        	if (i < (count - 1)) {
        		formatter.format("%02X" + divider, bb.get() & 0xFF);
        	}
        	else {
        		formatter.format("%02X", bb.get() & 0xFF);
        	}
        }
        bb.position(pos);
        formatter.close();
        return sb;
	}

	private static StringBuilder getStringBuilder(byte[] b, String divider, int count) {
    	StringBuilder sb = new StringBuilder();
    	Formatter formatter = new Formatter(sb);
        for (int i = 0; i < count; i++) {
        	if (i < (count - 1)) {
        		formatter.format("%02X" + divider, b[i] & 0xFF);
        	}
        	else {
        		formatter.format("%02X", b[i] & 0xFF);
        	}
        }
        formatter.close();
        return sb;
	}

	public static String toStringTrimmed(ByteBuffer bb, String divider, String trail, int count) {
    	StringBuilder sb;
    	int pos = bb.position();
        int len = bb.limit() - pos;
        if (count >= len) {
            sb = getStringBuilder(bb, divider, len);
        }
        else {
            sb = getStringBuilder(bb, divider, count);
            sb.append(trail);
        }
        return sb.toString();
    }
    
    public static String toStringTrimmed(byte[] b, String divider, String trail, int count) {
    	StringBuilder sb;
        int len = b.length;
        if (count >= len) {
            sb = getStringBuilder(b, divider, len);
        }
        else {
            sb = getStringBuilder(b, divider, count);
            sb.append(trail);
        }
        return sb.toString();
    }
    
    public static String toStringTrimmed(ByteBuffer bb, int limit) {
    	return toStringTrimmed(bb, " ", "...", limit);
    }

    public static String toStringTrimmed(ByteBuffer bb, String divider) {
    	return toStringTrimmed(bb, divider, "...", LIMIT);
    }

    public static String toStringTrimmed(ByteBuffer bb) {
    	return toStringTrimmed(bb, " ", "...", LIMIT);
    }
    

    public static String toStringTrimmed(byte[] b, int limit) {
    	return toStringTrimmed(b, " ", "...", limit);
    }

    public static String toStringTrimmed(byte[] b) {
    	return toStringTrimmed(b, " ", "...", LIMIT);
    }
    
    public static String toStringTrimmed(byte[] b, String divider) {
    	return toStringTrimmed(b, divider, "...", LIMIT);
    }
    

    public static String toString(ByteBuffer bb, String divider, int count) {
    	int pos = bb.position();
        int len = bb.limit() - pos;
        if (count < len) {
        	len = count;
        }
        StringBuilder sb = getStringBuilder(bb, divider, len);
        return sb.toString();
    }

    public static String toString(byte[] b, String divider, int count) {
        int len = b.length;
        if (count < len) {
        	len = count;
        }
        StringBuilder sb = getStringBuilder(b, divider, len);
        return sb.toString();
    }
    
    public static String toString(byte[] b, String divider) {
    	return toString(b, divider, b.length);
    }
}
