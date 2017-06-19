package fsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Mmap {
    private static final byte[] logEntry = "[Fri Sep 09 10:42:29.902022 2011] [core:error] [pid 35708:tid 4328636416] [client 72.15.99.187] File does not exist: /usr/local/apache2/htdocs/favicon.ico".getBytes(StandardCharsets.UTF_8);

    static void doIt() throws IOException {

        System.out.println("MMap Test");
        System.out.println("==============");

        File file = File.createTempFile("mmap", ".tmp");
        System.out.println("Creating a 1GB tempfile : " + file.getAbsolutePath());
        file.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        FileChannel fileChannel = raf.getChannel();

        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, App.ONE_GB);
        raf.close();
        mappedByteBuffer.load();

        ByteBuffer preByteBuffer = ByteBuffer.allocateDirect(logEntry.length);
        preByteBuffer.wrap(logEntry);

        long allTime = 0;
        long fsycTotalTime = 0;

        int i = 0;
        long bytesWritten = 0;
        while (bytesWritten < App.ONE_GB - bytesWritten) {
            long astart = System.nanoTime();
            mappedByteBuffer.put(preByteBuffer);
            preByteBuffer.rewind();

            //fsync every 1024 writes
            if (++i % 1024 == 0) {
                if (i % 102400 == 0) {
                    System.out.println(".");
                } else {
                    System.out.print(".");
                }

                long fstart = System.nanoTime();
                mappedByteBuffer.force();
                long fstop = System.nanoTime();
                fsycTotalTime += fstop - fstart;
            }
            long astop = System.nanoTime();
            bytesWritten += logEntry.length;
            allTime += astop - astart;
        }
        System.out.println("");
        System.out.println("# of Writes : " + i);
        System.out.printf("%-26s : %s %s%n", "All total time", allTime, "ns");
        System.out.printf("%-26s : %s %s%n", "Fsync total time", fsycTotalTime, "ns");
        System.out.printf("%-26s : %s %s%n", "Fsync Percent of total time", Long.valueOf(fsycTotalTime).doubleValue() / Long.valueOf(allTime).doubleValue() * 100, "%");
    }

}
