package fsync;

import java.io.IOException;

public class App {

    public final static long ONE_GB = 1024 * 1024 * 1024;

    public static void main(String... args) throws IOException {
        Channel.doIt();
        Mmap.doIt();
    }
}
