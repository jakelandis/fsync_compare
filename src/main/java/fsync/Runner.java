package fsync;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


class Runner {
  static final byte[] logEntry = "[Fri Sep 09 10:42:29.902022 2011] [core:error] [pid 35708:tid 4328636416] [client 72.15.99.187] File does not exist: /usr/local/apache2/htdocs/favicon.ico".getBytes(StandardCharsets.UTF_8);
  public final static long ONE_GB = 1024 * 1024 * 1024;
  public final static int SYNC_INTERVAL = 1024;

  private boolean noted;
  private String name;
  private int writeSize;

  public Runner(String name, int writeSize) {
    this.name = name;
    this.writeSize = writeSize;
  }

  private void note(String format, Object... args) {
    if (noted) {
      return;
    }

    System.out.printf(format, args);
  }

  public void run() throws Exception {
    try (final IO io = getIO(name)) {
      note("Running %s test\n", name);
      final ByteBuffer buffer = ByteBuffer.allocateDirect(writeSize);

      int eventCount = writeSize / logEntry.length;
      for (int i = 0; i < (writeSize / logEntry.length); i++) {
        buffer.put(logEntry);
      }
      buffer.rewind();
      note("buffer contains %d events and has byte size is %d\n", eventCount, buffer.limit());

      long bytes = 0;
      int interval = 0;
      int count = 0;
      while (bytes < ONE_GB) {
        io.write(buffer);
        buffer.rewind();

        count++;
        interval += eventCount; // fsync every n events.
        bytes += buffer.limit();

        if (interval >= SYNC_INTERVAL) {
          io.sync();
          interval = 0;
        }
      }

      note("Did %d writes. Wrote %d bytes total\n", count, bytes);
    }

    noted = true;
  }

  private static IO getIO(String name) throws IOException {
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
} // Runner
