/*
 * Created on Aug 11, 2008
 */
package code.messy.net;

import java.io.IOException;

public interface Port {
    public void send(Payload payload) throws IOException;
}
