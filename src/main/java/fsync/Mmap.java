package fsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Mmap implements App.IO {
  private MappedByteBuffer map;

  public Mmap(File file, long size) throws IOException { 
    RandomAccessFile raf = new RandomAccessFile(file, "rw");
    raf.setLength(size);
    FileChannel fileChannel = raf.getChannel();
    map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, (int)size);
    raf.close();
  }

  public void write(ByteBuffer buffer) throws IOException {
    map.put(buffer);
  }

  public void sync() throws IOException {
    map.force();
  }
}
