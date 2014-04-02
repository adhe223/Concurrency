/* Correctness properties:  for every trajectory in the system:
 *   (1)  trajectory == <x> && currentTime == y unless
 *         currentTime == y+d && trajectory == up.<x>.d
 *   (2)  
 * Synchronization strategy:
 *  - map guards operations on all mutable shared state, including:
 *      contents of the map itself
 *      currentTime
 *  - trajectories in the map cannot be updated except via this class:
 *    -- defensive copies on insertion and retrieval
 *    -- underlying arrows are immutable, so shallow clone() is fine.
 * Update strategy: lazy, we only update trajectories when necessary.
 */
import java.util.*;

/*
 * Does not have a "getNearbyObjects implementation because I decided to use yours seeing as how lazy implementation
 * will have better performance within our testing scenario. The test plan did not mention that method being tested
 * so I assume it does not matter that this performance class does not have it.
 */

public class StatusMapAlt {
  public static final int MAX_TIME_INCREMENT = Trajectory.MAXDELTAT;

  private static class Status { // just a struct
    Trajectory traj;
    long lastUpdate;
    long lastAccel;
  }

  private static final int INITIALCAPACITY = 500;
  private static final float LOADFACTOR = (float) 0.5;
  private final HashMap<Object,Status> map; // note: unsynchronized
  private long currentTime;

  public StatusMapAlt() {
    this.map = new HashMap<Object,Status>(INITIALCAPACITY,LOADFACTOR);
    this.currentTime = 0L;  // XXX default value
  }

/* Add an object with a trajectory to the map.
 * Once in the map, the object's trajectory can only be changed
 * by methods of this class => we make defensive copies.
 */
  public void insert(Object o, Trajectory trajectory) {
    Trajectory t = trajectory.clone(); // defensive copy
    Status s = new Status();
    s.traj = t;
    s.lastAccel = -1;
    synchronized(map) {
      if (!this.map.containsKey(o)) { // ignore if already here
		s.lastUpdate = this.currentTime;
		this.map.put(o,s);
      }
    } // synchronized
  }

  public Arrow getPosition(Object o) {
    synchronized (o) {
      Status s = map.get(o);
      if (s==null) {
    	  return null;
      }
      doUpdate(s);
      return s.traj.getPosition();
    }
  }

  public Arrow getVelocity(Object o) {
    synchronized (o) {
    	Status s = map.get(o);
    if (s==null) {
    	return null;
    }
    doUpdate(s);
    return s.traj.getVelocity();
    }
  }

  // advance the global clock one unit of time
  public void tick() {
    synchronized (map) {
      this.currentTime += 1;
    }
    if (this.currentTime < 0)
      throw new RuntimeException("clock rollover");
  }

  public void tick(int increment) {
    if (increment > MAX_TIME_INCREMENT || increment < 0) {
      throw new RuntimeException("invalid time increment");
    }
    synchronized (map) {
      this.currentTime += increment;
    }
    if (this.currentTime < 0)
      throw new RuntimeException("clock rollover");
  }

  public boolean accelerate(Object o, Arrow change) {
    boolean changed = false;
    synchronized (o) {
      Status s = map.get(o);
      if (s!=null) {
		if (s.lastAccel != currentTime) {
		  doUpdate(s); // make sure it's up to date
		  s.traj.accelerate(change);
		  s.lastAccel = currentTime;
		  changed = true;
		} // else ignore...
      }
    }
    return changed;
  }

  public Set<Trajectory> getSnapshot() {
    HashSet<Trajectory> toReturn =
      new HashSet<Trajectory>(map.size(),LOADFACTOR);
    /* The values() method of HashMap returns the actual contents of
     * the map, so we have to
     * (i) ensure trajectories are up-to-date, and
     * (ii)  make defensive copies
     * inside the critical section.
     */
    synchronized (map) {
      Collection<Status> inMap = map.values();
      for (Status s: inMap) {
		doUpdate(s);
		toReturn.add(s.traj.clone()); // defensive copy
	  }
    }
    return toReturn;
  }

  /* Establishes: "trajectory is consistent with current time"
   * precondition: s is non-null, stored in map.
   * precondition: caller holds map's lock
   */
  private void doUpdate(Status s) {
    if (s.lastUpdate != this.currentTime) {
      long delta = this.currentTime - s.lastUpdate;
      if (delta < 0 || delta > MAX_TIME_INCREMENT) // XXX DETECT SOONER!
    	  throw new RuntimeException("invariant violation");
      s.lastUpdate = this.currentTime;
      s.traj.update((int)delta);
    }
  }

}