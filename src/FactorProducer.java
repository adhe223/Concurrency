import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/*  Factor positive longs.
 *  Throws an Exception if given a negative number.
 *  This version returns the value factored as the first element
 *  in the list.
 */
public class FactorProducer implements Runnable {
private final long numberToFactor;
  private final ArrayList<Long> factors;
  private ExecutorService factorPrinter;
  
  public FactorProducer(long n, ExecutorService inFactorPrinter) {
    this.numberToFactor = n;
    this.factors = new ArrayList<Long>();
    factorPrinter = inFactorPrinter;
  }

  public void run() {
    Factorer.bruteForceFactor(factors,numberToFactor);
    //this.factors.add(0,numberToFactor); // factored # is first in list
    //this.queue.offer(factors);
	factorPrinter.execute(new Runnable() {
	    public void run() {	    	
	    	Factorer.printFactors(numberToFactor, factors);
	    }
	});
  }
  
}
