import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FactorTestFuture {
  //  private static final String targetIP = "127.0.0.1";
  private static final String targetIP = "128.163.140.219";
  private static final int BYTESTOGET = 7;
  private static final int targetPort = 47105;
  private static final int NTHREADS = 7;

  public static void main(String[] args) throws Exception {

    // get a source of work
    Socket connection = new Socket(targetIP, targetPort); // may throw
    InputStream source = connection.getInputStream();
    ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);
    ArrayList<Future<ArrayList<Long>>> results  // List of handles for results
      = new ArrayList<Future<ArrayList<Long>>>();

    // get some tasks to work on
    long value = getNextValueToFactor(source);
    while (value > 0) {
      System.out.println("Task: " + value);
      results.add(exec.submit(new FactorTask(value))); // submit to pool
      value = getNextValueToFactor(source);
    }

    // get their results, one at a time
    for (Future<ArrayList<Long>> f: results) {
      ArrayList<Long> list = f.get();  // blocks if not complete!
      value = list.remove(0);
      Factorer.printFactors(value,list);
    }

    System.out.println("That's all, folks.");
    System.exit(0);

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