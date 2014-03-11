import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*  Factor positive longs.
 *  Throws an Exception if given a negative number.
 *  This version returns the value factored as the first element
 *  in the list.
 */
public class FactorProducer implements Runnable {
private final long numberToFactor;
  private final ArrayList<Long> factors;
  private ExecutorService factorPrinter;
  private ExecutorService serverGetter;
  private static int countAdded;
  private static int countProcessed = 0;
  private static Object lock = new Object();
  
  public FactorProducer(long n, ExecutorService inFactorPrinter, ExecutorService inServerGetter, int count) {
    this.numberToFactor = n;
    this.factors = new ArrayList<Long>();
    factorPrinter = inFactorPrinter;
    serverGetter = inServerGetter;
    countAdded = count;
  }

  public void run() {
    Factorer.bruteForceFactor(factors,numberToFactor);
    this.factors.add(0,numberToFactor); // factored # is first in list
    //this.queue.offer(factors);
	factorPrinter.execute(new Runnable() {
	    public void run() {
	    	System.out.println("The Factors of " + factors.get(0) + " are:");
	    	for (int i = 1; i < factors.size(); i++) {
	    		System.out.println("Factor " + i + ": " + factors.get(i));
	    	}
	    }
	});
	
	synchronized(lock) {
		countProcessed++;
		
		if (serverGetter.isShutdown() && countProcessed == countAdded) {
			factorPrinter.shutdown();
		}
	}
  }
  
}
