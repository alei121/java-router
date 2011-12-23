package code.messy.net.ip.tcp;

import code.messy.net.ip.Tuple;

public interface TupleMatcher {
	public boolean match(Tuple tuple);
}
