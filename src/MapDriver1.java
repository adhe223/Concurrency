public class MapDriver1 {
	private static final int NUMOPS = 10;
	private static final int NUMTICKS = 10;
	private static final int NUM_TRAJECTS = 5;
	private static StatusMap sm = new StatusMap();

	public static void main(String args[]) {
		MapDriver1 driver = new MapDriver1();


		System.out.println("\nAssurance of behavior relies on manual checking of the output. Ensure that the ticking on an uninitialized map does not print any items or gibberish. Assure that the getPosition and getVelocity function get items, then return null for non existant objects. Ensure that the accel test changes the velocity valuesr. For the single threaded tick test just make sure that the objects have reasonable results and none disappear. For the multithreaded test, ensure that the final StatusMap that is printed contains 3 * NUM_TICKS + NUM_TRAJECTS). Also pick an object, pick an object from the beginning and make sure that it has correct values according to the formulas at the end. It was more efficient for me to do this to ensure correctness than to write code to do it.\n");
	
		System.out.println("Single threaded testing:\n");
		System.out.println("Test what happens if we run operations without any objects");
		driver.tickAndUpdateTest(NUMTICKS);
		driver.getSnapshotTest();
		
		System.out.println("\nInserting objects and testing getters:");
		driver.insertTest();
		System.out.println("\nPrinting initial map:");
		System.out.println(sm + "\n");
		
		System.out.println("\nTesting the tick, update, and the getGlobalTime Method:");
		driver.tickAndUpdateTest(NUMTICKS);
		
		System.out.println("\nTesting new getSnapshot. Adding two 'equal' items, make sure there are repeat values and that they are in order of decreasing distance from origin.");
		Arrow testArrow0 = new Arrow(0L, 0L, 0L);
		Arrow testArrow1 = new Arrow(0L, 0L, 0L);
		Arrow testArrow2 = new Arrow(0L, 0L, 0L);
		String name0 = new String("Duplicate0");
		String name1 = new String("Duplicate1");
		sm.insert(name0, new Trajectory(testArrow0, testArrow1, testArrow2));
		sm.insert(name1, new Trajectory(testArrow0, testArrow1, testArrow2));
		
		System.out.println("\nSnapshot:");
		driver.getSnapshotTest();
		
		
		System.out.println("\nMulti threaded testing:\n");
	    // create threads
		Doer t0 = new Doer(driver);
		Doer t1 = new Doer(driver);
		Doer t2 = new Doer(driver);
		
	    //start them running
	    t0.start();
	    t1.start();
	    t2.start();
	    try {
	    t0.join();
	    t1.join();
	    t2.join();}
	    catch (Exception e) {System.out.println("Couldn't do it");}
	    
	    System.out.println("\n" + sm.getGlobalTime());
	    System.out.println(sm);
	    driver.getSnapshotTest();
	}
	
	public void insertTest () {
		for (int i = 0; i < NUM_TRAJECTS; i++) {
			long[] longTriple0 = {i, i, i};
			long[] longTriple1 = {i, i, i};
			long[] longTriple2 = {i, i, i};
			
			Trajectory tempTraj = new Trajectory(new Arrow(longTriple0), new Arrow(longTriple1), new Arrow(longTriple2));
			String objectName = new String("");
			objectName = objectName + i;

			sm.insert(objectName, tempTraj);
			
			//Test the getters
			System.out.println("Test the getters: ");
			System.out.println(sm.getPosition(objectName).getLeft());
			System.out.println(sm.getVelocity(objectName).getLeft());
			
			//Accel test
			if (i == 0) {
				System.out.println("\nAccel Test:\nBefore: " + sm.getVelocity(objectName).getLeft());
		    	long[] arrowData = {1L, 1L, 1L};
		    	Arrow accelerator = new Arrow(arrowData);
		    	sm.accelerate(objectName, accelerator);
		    	try {sm.tick();}
		    	catch(Exception e) {}
		    	System.out.println("After: " + (sm.getVelocity(objectName).getLeft()) + "\n");
			}
			
			//Try with a non existant object
	    	System.out.println("Test getters with null objects:");
			objectName = new String("");
			objectName = objectName + "-1";
			System.out.println(sm.getPosition(objectName).getLeft());
			System.out.println(sm.getVelocity(objectName).getLeft());
			

		}
	}
	
	public void tickAndUpdateTest(int numTicks) {
		for (int i = 1; i <= numTicks; i++) {
			try {
				sm.tick(i);
			} catch (Exception e) {
				System.out.println("Overflow, do something");
			}
			System.out.println("After tick " + sm.getGlobalTime() + ":");
			System.out.println(sm);
		}		
	}
	
	public void getSnapshotTest() {
		System.out.println(sm.getSnapshot());
	}
	
	void insertEntry(Object o, Trajectory t) {
		sm.insert(o, t);
	}
	
	public StatusMap getStatusMap() {
		return sm;
	}
	
	private static void doSomething(MapDriver1 driver, StatusMap sm) {
		    for (int i=0; i<NUMOPS; i+=1) {
		    	Object toInsert = new Object();
		    	long[] arrowData = {1L, 1L, 1L};
		    	Arrow tempPos = new Arrow(arrowData);
		    	Arrow tempVel = new Arrow(arrowData);
		    	Arrow tempAcc = new Arrow(arrowData);
		    	Trajectory tempTraj = new Trajectory(tempPos, tempVel, tempAcc);
		    	sm.insert(toInsert, tempTraj);
				try {
					sm.tick();
				} catch (Exception e) {
					System.out.println("Overflow, do something");
				}
				System.out.println("After tick " + sm.getGlobalTime() + ":");
				System.out.println(sm);
		    }		
		    
		    driver.getSnapshotTest();
	}

	private static class Doer extends Thread {
		    private static MapDriver1 driver;
		    private static StatusMap sm;

		    Doer(MapDriver1 inDriver) {
		      driver = inDriver;
		      sm = driver.getStatusMap();
		    }

		    public void run() {
		      // Do some ops
		      doSomething(driver, sm);
		    }
	}
	
	
	

}
