package code.messy.net.ip.tcp;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Blocking/NonBlocking do not work with each other
 * 
 * @author alei
 *
 */
public class ByteBufferQueue {
	LinkedBlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<ByteBuffer>();
	private ByteBuffer head = null;
	private boolean isBlocking = true;
	
	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		if (!queue.isEmpty() || head != null) {
			throw new IllegalStateException("Cannot change blocking mode if queue not empty");
		}
		this.isBlocking = isBlocking;
	}

	public void clear() {
		queue.clear();
		head = null;
	}
	
	public boolean isEmpty() {
		if (!queue.isEmpty()) return false;
		if (head != null) {
			if (head.hasRemaining()) return false;
		}
		return true;
	}
	
	public void put(ByteBuffer bb) throws InterruptedException {
		queue.put(bb);
	}
	
	public int get(ByteBuffer bb) throws InterruptedException {
		if (isBlocking) return blockingGet(bb);
		else return nonBlockingGet(bb);
	}

	private int completelyBlockingGet(ByteBuffer bb) throws InterruptedException {
		int start = bb.position();
		if (head == null) head = queue.take();
		while (bb.hasRemaining()) {
			bb.put(head);
			if (!bb.hasRemaining()) break;
			if (!head.hasRemaining()) head = queue.take();
		}
		return bb.position() - start;
	}
	
	private int blockingGet(ByteBuffer bb) throws InterruptedException {
		int start = bb.position();
		if (head == null) {
			// wait if nothing to start with
			head = queue.take();
		}
		while (head != null) {
			bb.put(head);
			if (!head.hasRemaining()) head = queue.poll();
			if (!bb.hasRemaining()) break;
		}
		return bb.position() - start;
	}
	
	private int nonBlockingGet(ByteBuffer bb) throws InterruptedException {
		int start = bb.position();
		if (head == null) head = queue.poll();
		while (head != null) {
			bb.put(head);
			if (!head.hasRemaining()) head = queue.poll();
			if (!bb.hasRemaining()) break;
		}
		return bb.position() - start;
	}
}
