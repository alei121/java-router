package code.messy.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPayload;

public class PayloadHelper {
	public static int getLength(OutputPayload payload) {
		ArrayList<ByteBuffer> bbs = new ArrayList<>();
		payload.getByteBuffers(bbs);

		int remain = 0;
		for (ByteBuffer bb : bbs) {
			if (bb != null)
				remain += bb.remaining();
		}
		return remain;
	}

}
