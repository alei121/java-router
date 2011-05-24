package code.messy;

public interface Publisher<T, H> {
	void subscribe(T type, H handler);
	
	// Default handler
	void subscribe(H handler);
}
