package code.messy.net.ip.rip2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import code.messy.net.OutputPayload;
import code.messy.net.ip.route.LocalSubnet;

public class RipMessage implements OutputPayload {
    ByteBuffer payload = ByteBuffer.allocateDirect(4 + 20 * 25);

    public RipMessage(RipTable ripTable, LocalSubnet direct) {
        payload.put((byte) 2);
        payload.put((byte) 2);
        payload.put((byte) 0);
        payload.put((byte) 0);

        Collection<RipEntry> entries = ripTable.getRipEntries();
        for (RipEntry ripEntry : entries) {
            // Don't send back to where it is learned
            if (direct.getLink() != ripEntry.getPort()) {
                // IP
                payload.putShort((short) 2);
                // tag 0
                payload.putShort((short) 0);

                payload.put(ripEntry.getNetwork().getAddress()
                        .getAddress());
                payload.put(ripEntry.getNetwork().getMask()
                        .getAddress());

                // TODO put nexthop
                
                payload.putInt(0);
                payload.putInt(ripEntry.getMetric());
            }
        }
        payload.flip();
	}
    
	@Override
	public void getByteBuffers(ArrayList<ByteBuffer> bbs) {
		bbs.add(payload);
	}
}
