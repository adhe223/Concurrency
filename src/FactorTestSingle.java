import java.net.Socket;
import java.io.InputStream;

public class FactorTestSingle {
  //  private static final String targetIP = "127.0.0.1";
  private static final String targetIP = "128.163.140.219";
  private static final int BYTESTOGET = 7;
  private static final int targetPort = 47105;

  private static boolean readFully(InputStream in, byte[] buf)
  throws Exception {
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

  public static void main(String[] args) throws Exception {
    Socket connection = new Socket(targetIP, targetPort); // may throw
    InputStream source = connection.getInputStream();

    byte[] buffer = new byte[BYTESTOGET];

    boolean notDone = readFully(source,buffer);
    while (notDone) {
      long value = ((int)buffer[0]) & 0xff;
      for (int i=1; i<BYTESTOGET; i+=1)
	value = value << 8 | (((int)buffer[i]) & 0xff); // XXX stupid signed

      System.out.println("task: " + value);
      FactorAndPrintTask task = new FactorAndPrintTask(value);
      task.run();
      notDone = readFully(source,buffer);
    }
    System.out.println("Finished");
  }
}