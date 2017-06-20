package fsync;

public class App {
  public static void main(String... args) throws Exception {

    if (args.length != 3) {
      System.out.println("Usage: app [test] [writesize] [syncinterval]");
      System.out.println("Test can be 'channel' or 'mmap'");
      System.out.println("writesize is how many bytes you'd like each write to be");
      System.out.println("syncinterval is how many events to write before calling force() (msync / fdatasync)");
      return;
    }

    Runner runner = new Runner(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));

    for (int i = 0; i < 50; i++) {
      long start = System.currentTimeMillis();
      runner.run();
      long duration = System.currentTimeMillis() - start;
      System.out.printf("Iteration %d took %dms\n", i, duration);

    }
  }

}
