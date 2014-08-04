package code.messy.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import code.messy.net.OutputPacket;

public class PayloadHelper {
	public static int getLength(OutputPacket payload) {
		ArrayList<ByteBuffer> bbs = new ArrayList<>();
		payload.getOutput(bbs);

		int remain = 0;
		for (ByteBuffer bb : bbs) {
			if (bb != null)
				remain += bb.remaining();
		}
		return remain;
	}

}
