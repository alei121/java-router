package code.messy.net.ip.tcp;

import code.messy.net.ip.Tuple;

public class AnyMatcher implements TupleMatcher {

	@Override
	public boolean match(Tuple tuple) {
		return true;
	}

}
