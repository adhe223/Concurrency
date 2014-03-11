import java.util.ArrayList;
import java.util.concurrent.Callable;
/*  Factor positive longs.
 *  Throws an Exception if given a negative number.
 *  This version returns the value factored as the first element
 *  in the list.
 */
public class FactorTask implements Callable<ArrayList<Long>> {
  private final long numberToFactor;
  private final ArrayList<Long> factors;
  
  public FactorTask(long n) {
    this.numberToFactor = n;
    this.factors = new ArrayList<Long>();
  }

  public ArrayList<Long> call() {
    Factorer.bruteForceFactor(factors,numberToFactor);
    factors.add(0,numberToFactor); // factored # is first in list
    return this.factors;
  }

}
