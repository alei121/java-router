package code.messy;

public interface Handler<P> {
	void handle(P packet);
}
