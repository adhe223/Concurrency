/** Thread-safe implementation of an unbounded FIFO Queue.
 *  For producer-consumer applications.
 *  Uses a singly-linked list to store contents.
 *  Dequeue operation blocks if empty.
 *  @THREADSAFE
 */
class PCQueue<E> {

  private class Elem { // this can't be static!
    /* this is really just a struct */
    E item;
    Elem next;
    Elem(E i) {
      this.item = i;
      this.next = null;
    }
  }

  private Elem first;
  private Elem last;
  private int len;

  /* Invariants:
   *   - (first==null) == (last==null)
   *   - len is the number of elements in the list
   *   - list contains all values passed to enq() minus all non-null
   *         values returned by deq()
   *   - first points to the first Item enqueued, not yet dequeued.
   *   - len==1 => first==last
   *   - the order of elements in the list is the order they were enqueued
  */

  public PCQueue() {
    this.first = null;
    this.last = null;
    this.len = 0;
  }

  public synchronized void enq(E item) {    // note: never fails
    Elem e = new Elem(item);
    if (this.last==null) { // if empty
      this.first = e;
      this.last = e;
      this.notifyAll();
    } else { // nonempty - nobody should be waiting
      this.last.next = e;
      /* e.next == null */
      this.last = e;
      // XXX notify() here?
    }
    this.len += 1;
  }
 
  public synchronized E deq() {   // note: never fails
    E dequeuedValue;
    while (this.first==null)
      try {
	this.wait();
      } catch (InterruptedException fooey) { }
    /* this.first != null */
    dequeuedValue = this.first.item;
    this.first = this.first.next;
    if (this.first==null) {// now empty
	this.last = null;
    }
    this.len -= 1;
    return dequeuedValue;
  }
  
  public synchronized boolean isEmpty() { //true if nothing in queue
    return (this.first==null);
  }

  public synchronized int getQueueLength() { // get how many in queue
    return this.len;
  }

}  

    