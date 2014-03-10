import java.util.concurrent.Callable;

//Thread Safe class
public class FutureTask<V> implements Runnable {
	private V returnObject;
	private Callable<V> c;
	private Object lock = new Object();

	public FutureTask(Callable<V> cIn) {
		returnObject = null;
		c = cIn;
	}
	
	@Override
	public void run() {
		synchronized(lock) {
			try {
				returnObject = c.call();
			} catch (Exception e) {
				System.out.println("Exception thrown in run()");
			}
			notifyAll();
		}
	}
	
	V get() {
		synchronized(lock) {
			while (returnObject == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Interruted exception");
				}
			}		
			return returnObject;
		}
	}
	
	boolean isDone() {
		synchronized(lock) {
			if (returnObject == null) {
				return false;
			}
			return true;
		}
	}
}
