package code.messy.net.ip.util;

import java.nio.ByteBuffer;

public class Checksum {
    public static short getOnesCompliment(ByteBuffer bb, int offset, int length) {
    	int pos = bb.position();
    	
        bb.position(offset);
        
        int sum = 0;
        while(length > 1) {
            sum += (bb.getShort() & 0xFFFF);
            length -= 2;
        }
        
        if(length > 0) sum += ((bb.get() & 0xFF) << 8);

        while ((sum >> 16) > 0) {
           sum = (sum & 0xFFFF) + (sum >> 16);
        }
        
        bb.position(pos);
        return (short)(~sum);
    }
    
    public static short getOnesCompliment(ByteBuffer[] bbs) {
    	int count = bbs.length;
        int sum = 0;
        boolean isOdd = false;
    	for (int i = 0; i < (count - 1); i++) {
    		ByteBuffer bb = bbs[i];
			if (bb != null) {
				int pos = bb.position();

				while (bb.remaining() > 1) {
					if (isOdd) {
						sum += (bb.get() & 0xFF);
						isOdd = false;
					} else {
						sum += (bb.getShort() & 0xFFFF);
					}
				}
				if (bb.remaining() == 1) {
					if (isOdd) {
						sum += (bb.get() & 0xFF);
						isOdd = false;
					} else {
						sum += ((bb.get() & 0xFF) << 8);
						isOdd = true;
					}
				}
				bb.position(pos);
    		}
    	}
    	
        while ((sum >> 16) > 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
         
    	return (short)(~sum);
    }
    
    private static short getShort(ByteBuffer[] bbs) {
    	int count = bbs.length;
    	for (int i = 0; i < (count - 1); i++) {
    		ByteBuffer bb = bbs[i];
    		if (bb.remaining() >= 2) {
    			return bb.getShort();
    		}
    		else if (bb.remaining() == 1) {
    			return toShort(bb.get(), bbs[i + 1].get());
    		}
    	}
    	return bbs[count - 1].getShort();
    }
    
    private static int getRemaining(ByteBuffer[] bbs) {
    	int remain = 0;
    	for (ByteBuffer bb : bbs) {
			remain += bb.remaining();
		}
    	return remain;
    }
    
    private static short toShort(byte first, byte second) {
    	return (short)(first << 8 & (second & 0xFF));
    }
}
