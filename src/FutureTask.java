import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

//Thread Safe class
public class FutureTask<V> implements Runnable {
	private V returnObject;
	boolean run;
	private Callable<V> c;
	private Object lock = new Object();
	ExecutionException ex = null;

	public FutureTask(Callable<V> cIn) {
		returnObject = null;
		c = cIn;
		run = false;
	}
	
	@Override
	public void run() {
		synchronized(lock) {
			try {
				returnObject = c.call();
				run = true;
			} catch (Exception e) {
				System.out.println("Exception thrown in run()");
				ex = new ExecutionException("Exception thrown in run()", e);
				run = true;
			}
			notifyAll();
		}
	}
	
	V get() throws ExecutionException {
		synchronized(lock) {
			while (!run) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Interruted exception");
				}
			}		
			
			if (ex != null) {
				throw ex;	//On Piazza it said to return the exception, I believe this is the correct way to do that assuming the user is listening for it
			} else {
				return returnObject;
			}
		}
	}
	
	boolean isDone() {
		synchronized(lock) {
			if (!run) {
				return false;
			}
			return true;
		}
	}
}
