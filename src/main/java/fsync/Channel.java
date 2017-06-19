package fsync;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class Channel implements App.IO {
  private FileChannel channel;

  public Channel(File file) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(file, "rw");
    channel = raf.getChannel();
  }

  public void write(ByteBuffer buffer) throws IOException {
    channel.write(buffer);
  }

  public void sync() throws IOException {
    channel.force(false);
  }
}
