import java.util.ArrayList;
/*  Utility class to encapsulate one factoring method.
 *  Throws an Exception if given a negative number.
 */
public class Factorer {
/* Get factors.  Start with two, then go up, checking all odd numbers.
 * (Yes, this is brute force!  But our goal is to use cycles!)
 * Add each factor to the ArrayList; stop when the square root is exceeded.
 * invariant: product of all values in ArrayList, times remaining,
 * equals numberToFactor.
 */
  public static void bruteForceFactor(ArrayList<Long> factors, long number) {
    long remaining = number;
    if (remaining < 0)
      return;  // empty list of factors => error

    // Don't need to try anything larger than the square root of the number
    long bound = (long)(Math.sqrt((double) number)) + 1;
    while ((remaining & 1L)==0L) {  // first remove all the factors of 2
      factors.add(2L);
      remaining >>= 1;     // divides by two
    }
    long fact = 3L;
    while (fact <= bound && remaining != 1) {
      long quot = remaining/fact;     // Note: integer division!
      if (fact * quot == remaining) { // found a factor
	factors.add(fact);
	remaining = quot;
      } else { // not a divisor
	fact += 2;   // try the next odd number
      }
    }
    /* All integers <= bound have been tried. */
    /* What's left is the other factor (which may be the number) or is 1. */
    if (remaining != 1)
      factors.add(remaining);
  }

/* print a list of factors */
  public static void printFactors(long number, ArrayList<Long> factors) {
    // produce output
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    sb.append(" has ");
    sb.append(factors.size());
    sb.append(" factors: ");
    for (long fac : factors ) { // auto-unboxing
      sb.append(fac);
      sb.append(" ");
    }
    // print the result
    System.out.println(sb);
    System.out.flush();
  }
}