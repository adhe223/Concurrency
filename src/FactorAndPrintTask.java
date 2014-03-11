import java.util.ArrayList;
import java.util.concurrent.Callable;
/*  Factor positive longs.
 *  If created with a negative number, will fail silently.
 */
public class FactorAndPrintTask implements Runnable {
  private final long numberToFactor;
  private final ArrayList<Long> factors;
  
  public FactorAndPrintTask(long n) {
    this.numberToFactor = n;
    this.factors = new ArrayList<Long>();
  }

  public void run() {
    Factorer.bruteForceFactor(factors,numberToFactor);
    Factorer.printFactors(this.numberToFactor,this.factors);
  }

}
