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
    final IO io = getIO(args[0]);
    System.out.printf("Running %s test\n", args[0]);

    final ByteBuffer buffer = ByteBuffer.wrap(logEntry);
    buffer.rewind();

    long bytes = 0;
    int count = 0;
    while (bytes < ONE_GB) {
      io.write(buffer);
      buffer.rewind();

      count++;
      bytes += logEntry.length;

      if (count == SYNC_INTERVAL) {
        io.sync();
        count = 0;
      }
    }

    System.out.printf("Wrote %d bytes total\n", bytes);
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
