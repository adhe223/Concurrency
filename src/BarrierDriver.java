
public class BarrierDriver {
	private static final int COUNT = 5;
	private static final Gateway gateway = new Gateway(COUNT);
	
	/*
	Test Cases
	1. Normal behavior. Have count threads call await(). I expect to see all of the await() calls to return and the threads to execute.
	2. Call await() in less than count threads. Should not terminate.
	3. Call await() in count threads. Reset the barrier, then repeat. Expect to see the threads wait until count number call await(), then they execute, then repeat.
	4. Call await() in count/2 threads, then reset(), then call await() in (count - floor(count/2)) threads. Expect to see it not terminate.
	5. Partially fill the barrier, then interrupt one of the threads. The thread should throw an InterruptedException and then all other threads that had called await() should throw a BrokenBarrierException.
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
	}
	
	//1. Normal behavior. Have count threads call await(). I expect to see all of the await() calls to return and the threads to execute.
	public void case1() {
		gateway.reset();
		
		for (int i = 0; i < COUNT; i++) {
		    Thread waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway.await();
		    		System.out.println("Await returned.");
		    	  } catch (InterruptedException e) {
		    		System.out.println("InterruptedException occured.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
	}
	
	//2. Call await() in less than count threads. No await() calls should be returned and will not terminate.
	public void case2() {
		gateway.reset();
		
		for (int i = 0; i < COUNT - 1; i++) {
		    Thread waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway.await();
		    		System.out.println("Await returned.");
		    	  } catch (InterruptedException e) {
		    		System.out.println("InterruptedException occured.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		}
	}
	
	//3. Call await() in count threads. Reset the barrier, then repeat. Expect to see the threads wait until count number call await(), then they execute, then repeat.
	public void case3() {
		final int REPEAT_COUNT = 1;
		for (int i = 0; i < REPEAT_COUNT; i++) {
			gateway.reset();
			if (i > 0) {
				System.out.println("Reset the barrier.");
			}
			
			for (int j = 0; j < COUNT; j++) {
			    Thread waitingThread = new Thread() {
			    	public void run() {
			    	  try {
			    		gateway.await();
			    		System.out.println("Await returned.");
			    	  } catch (InterruptedException e) {
			    		System.out.println("InterruptedException occured.");
			    	  }
			    	}
			          };
			          
			    waitingThread.start();
			}
		}
	}
	
	//4. Call await() in count/2 threads, then reset(), then call await() in (count - floor(count/2)) threads. Expect to see it not terminate.
	public void case4() {
		final int REPEAT_COUNT = 1;
		for (int i = 0; i < REPEAT_COUNT; i++) {
			gateway.reset();
			if (i > 0) {
				System.out.println("Reset the barrier.");
			}
			
			int numAwaitCalls;
			if (i == 0) {
				numAwaitCalls = COUNT/2;
			} else {
				numAwaitCalls = COUNT - (COUNT/2);
			}
			
			for (int j = 0; j < numAwaitCalls; j++) {
			    Thread waitingThread = new Thread() {
			    	public void run() {
			    	  try {
			    		gateway.await();
			    		System.out.println("Await returned.");
			    	  } catch (InterruptedException e) {
			    		System.out.println("InterruptedException occured.");
			    	  }
			    	}
			          };
			          
			    waitingThread.start();
			}
		}
	}
	
	//5. Partially fill the barrier, then interrupt one of the threads. The thread should throw an InterruptedException and then all other threads that had called await() should throw a BrokenBarrierException.
	public void case5() {
		gateway.reset();
		
		Thread threadToInterrupt;
		for (int i = 0; i < COUNT - 1; i++) {
		    Thread waitingThread = new Thread() {
		    	public void run() {
		    	  try {
		    		gateway.await();
		    		System.out.println("Await returned.");
		    	  } catch (InterruptedException e) {
		    		System.out.println("InterruptedException occured.");
		    	  }
		    	}
		          };
		          
		    waitingThread.start();
		    
		    if (i == 0) {
		    	threadToInterrupt = waitingThread;
		    }
		}
		
		threadToInterrupt.interrupt();	//Save the address of the first caller to await and wait until the remaining threads are added before calling this. Ensures with almost complete certainty that await() will be called before this line.
	}
	
}
