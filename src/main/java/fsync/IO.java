package fsync;

import java.nio.ByteBuffer;
import java.io.IOException;

public interface IO extends AutoCloseable {
  void write(ByteBuffer buffer) throws IOException;
  void sync() throws IOException;
}

