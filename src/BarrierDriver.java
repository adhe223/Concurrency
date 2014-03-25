import java.util.concurrent.BrokenBarrierException;


public class BarrierDriver {
	private static final int COUNT = 5;
	private static Barrier barrier = null;
	private static final int ZERO = 0;
	private static final int ONE = 1;
	
	/*
	Test Cases
	1. Normal behavior. Have count threads call await(). Count of one. I expect to see all of the await() calls to return and the threads to execute.
	2. Partially fill the barrier, then interrupt one of the threads. All threads that had called await() should throw a BrokenBarrierException.
	3. Call await() in less than count threads. Should not terminate.
	4. Call await() in 0 zero threads. Should not terminate because the barrier expects more.
	5. Over capacity threads call await. Calls past the capacity hould continue to return since the barrier has released
	6. Call await() equal to the number of the capacity (>1). Should block until capacity then return all.
	7. Barrier with negative count. Shouldn't terminate unless wrapped around because otherwise the number of calls will never be -1
	*/
	
	public static void main(String[] args) {
		BarrierDriver bd = new BarrierDriver();
		System.out.println("Test 1:");
		bd.case1();
		System.out.println("Test 2:");
		bd.case2();		
		System.out.println("Test 3:");
		bd.case3();
		System.out.println("Test 4:");
		bd.case4();
		System.out.println("Test 5:");
		bd.case5();
		System.out.println("Test 6:");
		bd.case6();		
		System.out.println("Test 7:");
		bd.case7();		
	}
	
	//1. Normal behavior. Have count threads call await(). Count of one. I expect to see all of the await() calls to return and the threads to execute.
	public void case1() {
		barrier = new Barrier(ONE);
		
		Thread waitingThread = null;
		for (int i = 0; i < ONE; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 1.");
		}
	}
	
	//2. Partially fill the barrier, then interrupt one of the threads. All threads that had called await() should throw a BrokenBarrierException.	
	public void case2() {		
		barrier = new Barrier(COUNT);
		Thread waitingThread = null;
		Thread threadToInterrupt = null;
		
		for (int i = 0; i < COUNT - 1; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
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
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 2.");
		}
	}
	
	//3. Call await() in less than count threads. Should not terminate.
	public void case3() {	
		int counter = 0;
		barrier = new Barrier(COUNT);
		Thread waitingThread = null;
		
		for (int i = 0; i < COUNT - 1; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		    counter++;
		}
		
		try {
			Thread.sleep(3000L);
			if (counter < COUNT) {
				System.out.println("The test took over 3 seconds, likely does not terminate.");
				return;
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 3.");
		}
	}	
	
	//4. Call await() in 0 zero threads. Should not terminate because the barrier expects more. 
	public void case4() {
		int counter = 0;
		barrier = new Barrier(COUNT);
		Thread waitingThread = null;
		
		for (int i = 0; i < ZERO; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		    counter++;
		}
		
		try {
			Thread.sleep(3000L);
			if (counter < COUNT) {
				System.out.println("The test took over 3 seconds, likely does not terminate.");
				return;
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 4.");
		}
	}
	
	//5. Over capacity threads call await. Calls past the capacity should continue to return since the barrier has released
	public void case5() {
		barrier = new Barrier(COUNT);
		
		Thread waitingThread = null;
		for (int i = 0; i < COUNT; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e1) {
			System.out.println("Interrupted exception thrown in test 5.");
		}
		
		for (int i = 0; i < COUNT; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 1.");
		}
	}
	
	//6. Call await() equal to the number of the capacity (>1). Should block until capacity then return all.
	public void case6() {
		barrier = new Barrier(COUNT);
		
		Thread waitingThread = null;
		for (int i = 0; i < COUNT; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 6.");
		}
	}
	
	//7. Barrier with negative count. Shouldn't terminate unless wrapped around because otherwise the number of calls will never be -1
	public void case7() {
		int neg = -1;
		barrier = new Barrier(neg);
		
		Thread waitingThread = null;
		
		for (int i = 0; i < COUNT; i++) {
		    waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		barrier.await();
		    		System.out.println("Await returned.");
		    	  } catch (BrokenBarrierException e) {
		    		System.out.println("BrokenBarrierException thrown.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
		
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception thrown in test 6.");
		}
	}
}
