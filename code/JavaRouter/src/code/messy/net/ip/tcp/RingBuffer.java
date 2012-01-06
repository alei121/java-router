package code.messy.net.ip.tcp;

import java.nio.BufferOverflowException;

/*
 * start and end are indexes
 * start is where data begins
 * end is where free space begins
 */
public class RingBuffer {
	private int max = 8192;
	private byte[] buffer;
	private int start = 0;
	private int end = 0;
	private int size = 0;
	
	public RingBuffer() {
		buffer = new byte[max];
	}
	
	public RingBuffer(int max) {
		this.max = max;
		buffer = new byte[max];
	}
	
	public int size() {
		return size;
	}
	
	public int remaining() {
		return max - size;
	}

	public void write(byte[] b) throws BufferOverflowException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int offset, int length) throws BufferOverflowException {
		int remain = remaining();
		
		if (length == 0) return;
		if (remain < length) throw new BufferOverflowException();

		if ((max - end) >= length) {
			// enough space at the end
			System.arraycopy(b, offset, buffer, end, length);
			end += length;
			if (end == max) end = 0;
		}
		else {
			int len1 = max - end;
			int len2 = length - len1;
			System.arraycopy(b, offset, buffer, end, len1);
			System.arraycopy(b, offset + len1, buffer, 0, len2);
			end = len2;
		}
		size += length;
	}

	public int read(byte[] b) {
		return read(b, 0, b.length);
	}
	
	public int read(byte[] b, int offset, int length) {
		int rlen, len1, len2;
		if (start < end) {
			len1 = end - start;
			len2 = 0;
		}
		else {
			len1 = max - start;
			len2 = end;
		}
		
		int remain = length;
		if (remain <= len1) {
			rlen = remain;
			System.arraycopy(buffer, start, b, offset, remain);
			start += rlen;
		}
		else {
			System.arraycopy(buffer, start, b, offset, len1);
			remain -= len1;
			if (remain < len2) len2 = remain;
			System.arraycopy(buffer, 0, b, offset + len1, len2);
			start = len2;
			rlen = len1 + len2;
		}
		size -= rlen;
		return rlen;
	}
}
