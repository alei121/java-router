package code.messy;

public interface Service {
	public void init() throws Exception;
	public void shutdown() throws Exception;
	public void start() throws Exception;
	public void stop() throws Exception;
}
