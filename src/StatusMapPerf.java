import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;	
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * Using a lazy implementation would offer improved performance because Of the dominance of tick() and accelerate() over
 * calls to getSnapshot. However, I did not have time this week to make a heavy adjustment to my eager implementation.
 */

public class StatusMapPerf {	
	private long clock;
	private Object clockLock = new Object();
	private Object mapLock = new Object();
	private HashMap<Object, Trajectory> map = new HashMap<Object, Trajectory>();
	
	  StatusMapPerf() {
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
				  
				  synchronized (mapLock) {
					  //Now update everything
					  for (Object o : map.keySet()) {
						  synchronized(o) {
							  Trajectory t = map.get(o);
							  t.update(1);	//One because this tick increments the time by one
						  }
					  }
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
				  
				  synchronized(mapLock) {
					  //Now update everything
					  for (Object o : map.keySet()) {
						  synchronized(o) {
							  Trajectory t = map.get(o);
							  t.update(elapsedTime);
						  }
					  }
				  }
				  
			  } else {
				  throw new OverflowException("The clock overflowed!");
			  }
		  }
	  }
	
	  OrderedPair<Arrow, Long> getPosition(Object o) {
		  synchronized (o) {		  
			  if (map.containsKey(o)) {
				  return new OrderedPair<Arrow, Long>(map.get(o).getPosition(), clock);
			  } else {
				  return new OrderedPair<Arrow, Long>(null, clock);
			  }
		  }
	  }
	  
	  OrderedPair<Arrow, Long> getVelocity(Object o) {
		  synchronized (o) {
			  if (map.containsKey(o)) {
				  return new OrderedPair<Arrow, Long>(map.get(o).getVelocity(), clock);
			  } else {
				  return new OrderedPair<Arrow, Long>(null, clock);
			  }
		  }
	  }
	  
	  boolean accelerate(Object o, Arrow a) {
		  synchronized (o) {
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
			  clock = getGlobalTime();
			  
			  synchronized(mapLock) {
			  Set<Object> keys = map.keySet();
				  for (Object o : keys) {
					  synchronized(o) {
						  if (Arrow.distance(position, map.get(o).getPosition()) <= radius) {
							  objectsWithinRadius.add(o);
						  }
					  }
				  }
			  }
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
			  synchronized(mapLock) {
				  for (Object o : map.keySet()) {	
					  synchronized(o) {
						  deepCopyList.add(map.get(o).clone());
					  }
				  }
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
			  synchronized(mapLock) {
				  Set<Object> keys = map.keySet();
				  for (Object o : keys) {
					  Trajectory objTraj;
					  synchronized(o) {
						  objTraj = map.get(o);	
					  }
					  toReturn.append("\nObject: " + o + "    Trajectory: "+ objTraj);
				  }
			  }
		  }
		  
		  return toReturn.toString();
	  }
}
