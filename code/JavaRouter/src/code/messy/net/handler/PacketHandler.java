/*
 * Created on Jul 31, 2008
 */
package code.messy.net.handler;

import code.messy.net.Packet;

public interface PacketHandler {
    public void handle(Packet packet);
}
