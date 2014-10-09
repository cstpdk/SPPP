// Week 6

// Counting primes using Java 8 streams. Simple and efficient on the
// Intel i7, much better than both threads and tasks on the AMD
// Opteron.

// sestoft@itu.dk * 2014-09-27

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestCountPrimesStream {
  public static void main(String[] args) {
    SystemInfo();
    final int range = 100_000;
    System.out.println(Mark6("countSequential", new IntToDouble() {
        public double call(int i) { 
          return countSequential(range, i);
        }}));
    System.out.println(Mark6("countStreamSequential", new IntToDouble() {
     public double call(int i) { 
       return countStreamSequential(range, i);
     }}));
    System.out.println(Mark6("countStreamParallel", new IntToDouble() {
     public double call(int i) { 
       return countStreamParallel(range, i);
     }}));
  }

  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }

  // Sequential solution (using int instead of long)
  private static long countSequential(int range, int j) {
    int count = 0;
    for (int i=0; i<range; i++)
      if (isPrime(i)) 
        count++;
    return count;
  }

  // Sequential stream solution
  private static long countStreamSequential(int range, int j) {
    return IntStream.range(0, range)
      .filter(i -> isPrime(i))
      .count();
  }

  // Parallel stream solution
  private static long countStreamParallel(int range, int j) {
    return IntStream.range(0, range)
      .parallel()
      .filter(i -> isPrime(i))
      .count();
  }

  // --- Benchmarking infrastructure ---

  // NB: Modified to show microseconds instead of nanoseconds

  public static double Mark6(String msg, IntToDouble f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.call(i);
        runningTime = t.check();
        double time = runningTime * 1e6 / count; // microseconds
        st += time; 
        sst += time * time;
        totalCount += count;
      }
      double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
      System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev, count);
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    return dummy / totalCount;
  }

  public static double Mark7(String msg, IntToDouble f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.call(i);
        runningTime = t.check();
        double time = runningTime * 1e6 / count; // microseconds
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }

  public static void SystemInfo() {
    System.out.printf("# OS:   %s; %s; %s%n", 
                      System.getProperty("os.name"), 
                      System.getProperty("os.version"), 
                      System.getProperty("os.arch"));
    System.out.printf("# JVM:  %s; %s%n", 
                      System.getProperty("java.vendor"), 
                      System.getProperty("java.version"));
    // This line works only on MS Windows:
    System.out.printf("# CPU:  %s%n", System.getenv("PROCESSOR_IDENTIFIER"));
    java.util.Date now = new java.util.Date();
    System.out.printf("# Date: %s%n", 
      new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
  }
}
