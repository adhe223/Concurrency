import java.util.concurrent.BrokenBarrierException;


public class BarrierDriver {
	private static final int COUNT = 5;
	private static Barrier gateway1 = new Barrier(COUNT);
	private static Barrier gateway2 = new Barrier(COUNT);
	private static Barrier gateway3 = new Barrier(COUNT);
	
	/*
	Test Cases
	1. Normal behavior. Have count threads call await(). I expect to see all of the await() calls to return and the threads to execute.
	2. Partially fill the barrier, then interrupt one of the threads. The thread should throw an InterruptedException and then all other threads that had called await() should throw a BrokenBarrierException.
	3. Call await() in less than count threads. Should not terminate.
	*/
	
	public static void main(String[] args) {
		BarrierDriver bd = new BarrierDriver();
		System.out.println("Test 1:");
		bd.case1();
		System.out.println("Test 2:");
		bd.case2();
		System.out.println("Test 3:");
		bd.case3();
	}
	
	//1. Normal behavior. Have count threads call await(). I expect to see all of the await() calls to return and the threads to execute.
	public void case1() {
		Thread waitingThread = null;
		for (int i = 0; i < COUNT; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway1.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			waitingThread.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 1.");
		}
	}
	
	//3. Call await() in less than count threads. No await() calls should be returned and will not terminate.
	public void case3() {	
		Thread waitingThread = null;
		for (int i = 0; i < COUNT - 1; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway3.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			waitingThread.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 1.");
		}
	}	
	
	//2. Partially fill the barrier, then interrupt one of the threads. The thread should throw an InterruptedException and then all other threads that had called await() should throw a BrokenBarrierException.
	public void case2() {		
		Thread waitingThread = null;
		Thread threadToInterrupt = null;
		for (int i = 0; i < COUNT - 1; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway2.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		    
		    if (i == 0) {
		    	threadToInterrupt = waitingThread;
		    }
		}
		
		threadToInterrupt.interrupt();	//Save the address of the first caller to await and wait until the remaining threads are added before calling this. Ensures with almost complete certainty that await() will be called before this line.
		
		try {
			waitingThread.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 1.");
		}
	}
	
}
