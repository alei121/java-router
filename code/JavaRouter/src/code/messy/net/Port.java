/*
 * Created on Aug 11, 2008
 */
package code.messy.net;

import java.io.IOException;

public interface Port {
    // Functionality
    public void send(Packet packet) throws IOException;
}
