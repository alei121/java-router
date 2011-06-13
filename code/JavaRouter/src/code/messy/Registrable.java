package code.messy;

public interface Registrable<T, H> {
	void register(T type, H handler);
	
	// Default handler
	void register(H handler);
}
