package fsync;

import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class App {
  public interface IO {
    void write(ByteBuffer buffer) throws IOException;
    void sync() throws IOException;
  }

  static final byte[] logEntry = "[Fri Sep 09 10:42:29.902022 2011] [core:error] [pid 35708:tid 4328636416] [client 72.15.99.187] File does not exist: /usr/local/apache2/htdocs/favicon.ico".getBytes(StandardCharsets.UTF_8);
  public final static long ONE_GB = 1024 * 1024 * 1024;
  public final static int SYNC_INTERVAL = 1024;

  public static void main(String... args) throws IOException {

    if (args.length != 2) {
      System.out.println("Usage: app [test] [writesize]");
      System.out.println("Test can be 'channel' or 'mmap'");
      System.out.println("writesize is how many bytes you'd like each write to be");
      return;
    }

    final IO io = getIO(args[0]);
    System.out.printf("Running %s test\n", args[0]);

    int writeSize = Integer.parseInt(args[1]);
    final ByteBuffer buffer = ByteBuffer.allocateDirect(writeSize);

    int eventCount = writeSize / logEntry.length;
    for (int i = 0; i < (writeSize / logEntry.length); i++) {
      buffer.put(logEntry);
    }
    buffer.rewind();
    System.out.printf("Buffer contains %d events and has byte size is %d\n", eventCount, buffer.limit());

    long bytes = 0;
    int interval = 0;
    int count = 0;
    while (bytes < ONE_GB) {
      io.write(buffer);
      buffer.rewind();

      count++;
      interval += eventCount; // fsync every N events.
      bytes += buffer.limit();

      if (interval >= SYNC_INTERVAL) {
        io.sync();
        interval = 0;
      }
    }

    System.out.printf("Did %d writes. Wrote %d bytes total\n", count, bytes);
  }

  public static IO getIO(String name) throws IOException {
    File file = File.createTempFile(name, ".tmp");
    file.deleteOnExit();

    switch (name) {
      case "channel": 
        return new Channel(file);
      case "mmap":
        return  new Mmap(file, ONE_GB);
      default:
        System.out.println("Invalid test name");
        return null;
    }
  }
}
