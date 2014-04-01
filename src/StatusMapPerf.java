import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;	
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * The changes made for HW6 have very little effect on the thread safety of my StatusMap class. Changing the getSnapshot to a sorted list had now
 * effect on thread safety. I did the sorting outside of the critical section and made sure that all of the objects in the returned list were deep copies. 
 * In returning the ordered pairs from getPosition and getVelocity I created the ordered pair objects within the critical section and cloned the arrow that
 * is encapsulated in them. Thus, changing those two methods will also not have a negative effect on the thread safety of this class.
 */


/* What you have to build... */
public class StatusMap {	
	private long clock;
	private Object clockLock = new Object();
	private HashMap<Object, Trajectory> map = new HashMap<Object, Trajectory>();
	
	  StatusMap() {
		  clock = 0;
	  }
	
	  void insert(Object o, Trajectory t) {
		  Trajectory defCopy = t.clone();
		  synchronized (clockLock) {
			  if (!map.containsKey(o)) {
				  map.put(o, defCopy);
			  }
		  }
	  }
  
	  void tick() throws OverflowException {
		  synchronized (clockLock) {
			  if (clock + 1 > 0) {
				  clock++;
				  
				  //Now update everything
				  for (Trajectory t : map.values()) {
					  t.update(1);	//One because this tick increments the time by one
				  }
				  
			  } else {
				  throw new OverflowException("The clock overflowed!");
			  }
		  }
	  }
	  
	  void tick(int elapsedTime) throws OverflowException {  // XXX note change
		  synchronized (clockLock) {
			  if (clock + elapsedTime > 0) {
				  clock = clock + elapsedTime;
				  
				  //Now update everything
				  for (Trajectory t : map.values()) {
					  t.update(elapsedTime);
				  }
				  
			  } else {
				  throw new OverflowException("The clock overflowed!");
			  }
		  }
	  }
	
	  OrderedPair<Arrow, Long> getPosition(Object o) {
		  synchronized (clockLock) {		  
			  if (map.containsKey(o)) {
				  return new OrderedPair<Arrow, Long>(map.get(o).getPosition(), clock);
			  } else {
				  return new OrderedPair<Arrow, Long>(null, clock);
			  }
		  }
	  }
	  
	  OrderedPair<Arrow, Long> getVelocity(Object o) {
		  synchronized (clockLock) {
			  if (map.containsKey(o)) {
				  return new OrderedPair<Arrow, Long>(map.get(o).getVelocity(), clock);
			  } else {
				  return new OrderedPair<Arrow, Long>(null, clock);
			  }
		  }
	  }
	  
	  boolean accelerate(Object o, Arrow a) {
		  synchronized (clockLock) {
			  if (map.containsKey(o)) {
				  map.get(o).accelerate(a);
				  return true;
			  } else {
				  return false;
			  }
		  }
	  }
	  
	  public OrderedPair<List<Object>,Long> getNearbyObjects(Arrow position, long radius) {
		  List<Object> objectsWithinRadius = new ArrayList<Object>();
		  long clock;
		  
		  synchronized(clockLock) {
			  Set<Object> keys = map.keySet();
			  for (Object o : keys) {
				  if (Arrow.distance(position, map.get(o).getPosition()) <= radius) {
					  objectsWithinRadius.add(o);
				  }
			  }
			  
			  clock = getGlobalTime();
		  }
		  
		  return new OrderedPair<List<Object>, Long> (objectsWithinRadius, clock);
	  }
	  
	  long getGlobalTime() {
		  synchronized(clockLock) {
			  return clock;
		  }
	  }
	  
	  List<Trajectory> getSnapshot() {
		  List<Trajectory> deepCopyList = new ArrayList<Trajectory>();
		  
		  synchronized (clockLock) {
			  Set<Object> mapKeySet = map.keySet();
			  
			 for (Object o : mapKeySet) {
				  deepCopyList.add(map.get(o).clone());
			  }
		  }
		  
		  //Sort the copy by distance
		  Collections.sort(deepCopyList, new Comparator<Trajectory>() {
			  public int compare(Trajectory t0, Trajectory t1) {				  
				  if (Arrow.distance(t0.getPosition(), Arrow.ORIGIN) > Arrow.distance(t1.getPosition(), Arrow.ORIGIN)) {
					  return 1;
				  } else if (Arrow.distance(t0.getPosition(), Arrow.ORIGIN) < Arrow.distance(t1.getPosition(), Arrow.ORIGIN)) {
					  return -1;
				  } else {
					  return 0;
				  }
			  }
		  });

		  return deepCopyList;
	  }
	  
	  @Override public String toString() {
		  StringBuilder toReturn = new StringBuilder("");
		  
		  synchronized (clockLock) {
			  Set<Object> keys = map.keySet();
			  for (Object o : keys) {
				  Trajectory objTraj = map.get(o);				  
				  toReturn.append("\nObject: " + o + "    Trajectory: "+ objTraj);
			  }
		  }
		  
		  return toReturn.toString();
	  }
}
