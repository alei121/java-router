package code.messy;

public class Filter<T> implements Receiver<T> {
	private Matcher<T> matcher;
	private Receiver<T> match;
	private Receiver<T> unmatch;

    public Filter(Matcher<T> matcher, Receiver<T> matchHandler,	Receiver<T> unmatchHandler) {
    	this.matcher = matcher;
        this.match = matchHandler;
        this.unmatch = unmatchHandler;
    }

    @Override
    public void receive(T item) {
    	if (matcher.match(item)) {
    		match.receive(item);
    	}
    	else {
    		unmatch.receive(item);
    	}
    }
}
