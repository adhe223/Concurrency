/** A simple ordered pair with "left" and "right" components.
 * @Immutable
 */
public class OrderedPair<L,R> {
  private final L left;
  private final R right;

  public OrderedPair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }

/* These are not needed for HW6, but might be handy in other contexts.*/
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o instanceof OrderedPair) {
      OrderedPair op = (OrderedPair) o;
      return this.left.equals(op.left) && this.right.equals(op.right);
    } else
      return false;
  }

  @Override
  public int hashCode() {
    return 31*this.left.hashCode() + this.right.hashCode();
  }

}