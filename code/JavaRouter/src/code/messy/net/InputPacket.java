/*
 * Created on Apr 30, 2008
 */
package code.messy.net;

import java.nio.ByteBuffer;

public interface InputPacket extends Payload {
    public int getDataOffset();
    public ByteBuffer getByteBuffer();
    public int getDataLength();
    public Port getPort();
    public long getTimestamp();
    public int getHeaderOffset();
}
