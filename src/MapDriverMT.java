import java.util.Random;

public class MapDriverMT {
	private static StatusMap sm = new StatusMap();
	private static final int NUMOPS = 10;

	public static void main(String[] args) {		
		System.out.println("\nMulti threaded testing:\n");
	    // create threads
		Doer t0 = new Doer(sm);
		Doer t1 = new Doer(sm);
		Doer t2 = new Doer(sm);
		
	    //start them running
	    t0.start();
	    t1.start();
	    t2.start();
	    try {
	    t0.join();
	    t1.join();
	    t2.join();}
	    catch (Exception e) {System.out.println("Couldn't do it");}
	    
	    System.out.println("\nFinished testing.");
	}
	
	private static class Doer extends Thread {
	    private static StatusMap sm;

	    Doer(StatusMap inSM) {
	      sm = inSM;
	    }

	    public void run() {
	      // Do some ops
	      doSomething(sm);
	    }
	}
	
	private static void doSomething(StatusMap sm) {
		//Create object and add to map
	    Object toInsert = new Object();
	    
	    //Randomly create numbers for the trajectory
	    Random randomizer = new Random();
	    int RANGE = 11;	//Used with random to produce a number from -5 to 5
	    long[] posData = {randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2};
	    long[] velData = {randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2};
	    long[] accData = {randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2};
	    Arrow tempPos = new Arrow(posData);
	    Arrow tempVel = new Arrow(velData);
	    Arrow tempAcc = new Arrow(accData);
	    Trajectory tempTraj = new Trajectory(tempPos, tempVel, tempAcc);
	    sm.insert(toInsert, tempTraj);
	    
	    //Iteratively advance time and accelerate
	    for (int i = 0; i < NUMOPS; i++) {
			try {
				sm.tick();
				
				long[] accelData = {randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2, randomizer.nextInt(RANGE) - RANGE/2};
				Arrow accelerator = new Arrow(accelData);
				sm.accelerate(toInsert, accelerator);
				
				//Periodically print
				if (i == 4 || i == 9) {
					System.out.println(sm.getSnapshot());
				}
			} catch (Exception e) {
				System.out.println("Overflow, do something");
			}
	    }
	}
}
