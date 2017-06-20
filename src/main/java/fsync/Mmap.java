package fsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Mmap implements IO {
  private MappedByteBuffer map;
  private RandomAccessFile raf;
  private FileChannel channel;

  public Mmap(File file, long size) throws IOException { 
    raf = new RandomAccessFile(file, "rw");
    channel = raf.getChannel();
    map = channel.map(FileChannel.MapMode.READ_WRITE, 0, (int)size);
  }

  public void write(ByteBuffer buffer) throws IOException {
    map.put(buffer);
  }

  public void sync() throws IOException {
    map.force();
  }

  public void close() throws Exception {
    raf.close();
    channel.close();
  }
}
