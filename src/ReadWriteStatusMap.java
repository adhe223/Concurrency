import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;	
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * The changes made for HW6 have very little effect on the thread safety of my StatusMap class. Changing the getSnapshot to a sorted list had now
 * effect on thread safety. I did the sorting outside of the critical section and made sure that all of the objects in the returned list were deep copies. 
 * In returning the ordered pairs from getPosition and getVelocity I created the ordered pair objects within the critical section and cloned the arrow that
 * is encapsulated in them. Thus, changing those two methods will also not have a negative effect on the thread safety of this class.
 */


/* What you have to build... */
public class ReadWriteStatusMap {	
	private long clock;
	private ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private HashMap<Object, Trajectory> map = new HashMap<Object, Trajectory>();
	
	  ReadWriteStatusMap() {
		  clock = 0;
	  }
	
	  void insert(Object o, Trajectory t) {
		  Trajectory defCopy = t.clone();
		  rwLock.writeLock().lock();
			  if (!map.containsKey(o)) {
				  map.put(o, defCopy);
			  }
		  rwLock.writeLock().unlock();
	  }
  
	  void tick() throws OverflowException {
		  rwLock.writeLock().lock();
			  if (clock + 1 > 0) {
				  clock++;
				  
				  //Now update everything
				  for (Trajectory t : map.values()) {
					  t.update(1);	//One because this tick increments the time by one
				  }
				  
			  } else {
				  throw new OverflowException("The clock overflowed!");
			  }
		  rwLock.writeLock().unlock();
	  }
	  
	  void tick(int elapsedTime) throws OverflowException {  // XXX note change
		  rwLock.writeLock().lock();
			  if (clock + elapsedTime > 0) {
				  clock = clock + elapsedTime;
				  
				  //Now update everything
				  for (Trajectory t : map.values()) {
					  t.update(elapsedTime);
				  }
				  
			  } else {
				  throw new OverflowException("The clock overflowed!");
			  }
		  rwLock.writeLock().unlock();
	  }
	
	  OrderedPair<Arrow, Long> getPosition(Object o) {
		  rwLock.readLock().lock();	  
			  if (map.containsKey(o)) {
				  OrderedPair<Arrow, Long> toReturn = new OrderedPair<Arrow, Long>(map.get(o).getPosition(), clock);
				  rwLock.readLock().unlock();
				  return toReturn;
			  } else {
				  OrderedPair<Arrow, Long> toReturn = new OrderedPair<Arrow, Long>(null, clock);
				  rwLock.readLock().unlock();
				  return toReturn;
			  }
	  }
	  
	  OrderedPair<Arrow, Long> getVelocity(Object o) {
		  rwLock.readLock().lock();	  
		  if (map.containsKey(o)) {
			  OrderedPair<Arrow, Long> toReturn = new OrderedPair<Arrow, Long>(map.get(o).getVelocity(), clock);
			  rwLock.readLock().unlock();
			  return toReturn;
		  } else {
			  OrderedPair<Arrow, Long> toReturn = new OrderedPair<Arrow, Long>(null, clock);
			  rwLock.readLock().unlock();
			  return toReturn;
		  }
	  }
	  
	  boolean accelerate(Object o, Arrow a) {
		  rwLock.writeLock().lock();
			  if (map.containsKey(o)) {
				  map.get(o).accelerate(a);
				  rwLock.writeLock().unlock();
				  return true;
			  } else {
				  rwLock.writeLock().unlock();
				  return false;
			  }
	  }
	  
	  public OrderedPair<List<Object>,Long> getNearbyObjects(Arrow position, long radius) {
		  List<Object> objectsWithinRadius = new ArrayList<Object>();
		  long clock;
		  
		  rwLock.readLock().lock();
			  for (Object o : map.keySet()) {
				  if (Arrow.distance(position, map.get(o).getPosition()) <= radius) {
					  objectsWithinRadius.add(o);
				  }
			  }
			  
			  clock = getGlobalTime();
		  rwLock.readLock().unlock();
		  
		  return new OrderedPair<List<Object>, Long> (objectsWithinRadius, clock);
	  }
	  
	  long getGlobalTime() {
		  rwLock.readLock().lock();
			  long toReturn = clock;
		  rwLock.readLock().unlock();
		  
		  return toReturn;
	  }
	  
	  List<Trajectory> getSnapshot() {
		  List<Trajectory> deepCopyList = new ArrayList<Trajectory>();
		  
		  rwLock.readLock().lock();
			  Set<Object> mapKeySet = map.keySet();
			  
			 for (Object o : mapKeySet) {
				  deepCopyList.add(map.get(o).clone());
			  }
		  rwLock.readLock().unlock();
		  
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
		  
		  rwLock.readLock().lock();
			  Set<Object> keys = map.keySet();
			  for (Object o : keys) {
				  Trajectory objTraj = map.get(o);				  
				  toReturn.append("\nObject: " + o + "    Trajectory: "+ objTraj);
			  }
		  rwLock.readLock().unlock();
		  
		  return toReturn.toString();
	  }
}
