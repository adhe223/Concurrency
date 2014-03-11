import java.net.Socket;
import java.util.Queue;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FactorTestProdCons {
  //  private static final String targetIP = "127.0.0.1";
  private static final String targetIP = "128.163.140.219";
  private static final int BYTESTOGET = 7;
  private static final int targetPort = 47105;
  private static final int NTHREADS = 7;

  public static void main(String[] args) throws Exception {
    ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);
    ExecutorService factorPrinter = Executors.newFixedThreadPool(NTHREADS);
    int countAdded = 0;
    //final PCQueue<ArrayList<Long>> results = new PCQueue<ArrayList<Long>>();

    /*
    // start a "consumer" thread to get/print the results as they come in
    Thread consumer = new Thread() {
    	
	public void run() {
	  // get, print results of tasks
	  for (;;) {
	    ArrayList<Long> list = results.remove();  // blocks
	    long x = list.remove(0);
	    if (x < 0L)
	      break;
	    Factorer.printFactors(x,list);
	  }
	}
	
      };
    consumer.start();
    */
    
    // get a source of work
    Socket connection = new Socket(targetIP, targetPort); // may throw
    InputStream source = connection.getInputStream();    

    // now get tasks to work on
    long value = getNextValueToFactor(source);
    while (value > 0) {
      countAdded++;
      System.out.println("Submitting task: " + value);
      exec.execute(new FactorProducer(value,factorPrinter, exec, countAdded)); // submit to pool
      value = getNextValueToFactor(source);
    }

    // Problem: need to let the consumer know there's no more work...
    // FIXME: replace PCQueue with another Executorservice.
    // Use the shutdown() capability of ExecutorService.
    
    
    exec.shutdown();
    //factorPrinter.shutdown();
  }

  private static long getNextValueToFactor(InputStream s) {
    byte[] buffer = new byte[BYTESTOGET];
    try {
      if (!readFully(s,buffer))
	return 0;
    } catch (IOException ioe) {
      return 0;
    }
    long number = ((int)buffer[0]) & 0xff;
    for (int i=1; i<BYTESTOGET; i+=1)
      number = number << 8 | (((int)buffer[i]) & 0xff); // XXX stupid signed
    return number;
  }

  private static boolean readFully(InputStream in, byte[] buf)
  throws IOException {
    int bytesToGo = buf.length;
    int bytesRead = 0;
    while (bytesToGo > 0) {
      bytesRead = in.read(buf,bytesRead,bytesToGo);
      if (bytesRead < 0) {
	break;
      } else {
	bytesToGo -= bytesRead;
      }
    }
    if (bytesToGo > 0)
      return false;
    else
      return true;
  }

}