/*
 * Created on Aug 11, 2008
 */
package code.messy.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Port {
    // Functionality
    public void send(Packet packet) throws IOException;
    public void send(ByteBuffer bb) throws IOException;
}
