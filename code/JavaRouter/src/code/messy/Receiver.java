package code.messy;

public interface Receiver<T> {
	void receive(T item);
}
