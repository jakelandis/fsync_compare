package fsync;

import java.io.IOException;

public class App {

    public final static long ONE_GB = 1024 * 1024 * 1024;

    public static void main(String... args) throws IOException {

        if (args.length > 0 && "channel".equalsIgnoreCase(args[0])) {
            Channel.doIt();
        } else if (args.length > 0 && "mmap".equalsIgnoreCase(args[0])) {
            Mmap.doIt();
        } else {
            Channel.doIt();
            Mmap.doIt();
        }

    }
}
