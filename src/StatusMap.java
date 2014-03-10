import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;	
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* What you have to build... */
public class StatusMap {	
	private static long clock;
	private static Object clockLock = new Object();
	private static HashMap<Object, Trajectory> map = new HashMap<Object, Trajectory>();
	
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
				  return new OrderedPair<Arrow, Long>(map.get(o).getPosition().clone(), clock);
			  } else {
				  return new OrderedPair<Arrow, Long>(null, clock);
			  }
		  }
	  }
	  
	  OrderedPair<Arrow, Long> getVelocity(Object o) {
		  synchronized (clockLock) {
			  if (map.containsKey(o)) {
				  return new OrderedPair<Arrow, Long>(map.get(o).getVelocity().clone(), clock);
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
			  @Override
			  public int compare(Trajectory t0, Trajectory t1) {
				  Arrow origin = new Arrow(0L, 0L, 0L);
				  
				  if (Arrow.distance(t0.getPosition(), origin) > Arrow.distance(t1.getPosition(), origin)) {
					  return 1;
				  } else if (Arrow.distance(t0.getPosition(), origin) < Arrow.distance(t1.getPosition(), origin)) {
					  return -1;
				  } else {
					  return 0;
				  }
			  }
		  });

		  return deepCopyList;
	  }
	  
	  @Override public String toString() {
		  String toReturn = "";
		  
		  synchronized (clockLock) {
			  Set<Object> keys = map.keySet();
			  for (Object o : keys) {
				  Trajectory objTraj = map.get(o);				  
				  toReturn = toReturn + "\nObject: " + o + "    Trajectory: "+ objTraj;
			  }
			  
			  return toReturn;
		  }
	  }
}
