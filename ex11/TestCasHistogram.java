// For week 10
// sestoft@itu.dk * 2014-11-05

// NOT TO BE HANDED OUT, CONTAINS SOLUTIONS FOR WEEK 10

// Compile and run like this:
//   javac -cp ~/lib/multiverse-core-0.7.0.jar TestStmHistogram.java
//   java -cp ~/lib/multiverse-core-0.7.0.jar:. TestStmHistogram

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;

class TestCasHistogram {
  public static void main(String[] args) {
    countPrimeFactorsWithCasHistogram();
  }

  private static void countPrimeFactorsWithCasHistogram() {
    final Histogram histogram = new CasHistogram(30);
    final int range = 4_000_000;
    final int threadCount = 10, perThread = range / threadCount;
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1),
      stopBarrier = startBarrier;
    final Thread[] threads = new Thread[threadCount];
    for (int t=0; t<threadCount; t++) {
      final int from = perThread * t,
                  to = (t+1 == threadCount) ? range : perThread * (t+1);
        threads[t] =
          new Thread(new Runnable() {
              public void run() {
                try { startBarrier.await(); } catch (Exception exn) { }
                for (int p=from; p<to; p++)
                  histogram.increment(countFactors(p));
                System.out.print("*");
                try { stopBarrier.await(); } catch (Exception exn) { }
              }
            });
        threads[t].start();
    }

    try { startBarrier.await(); } catch (Exception exn) { }
    Timer t = new Timer();
    //We let the Main Thread here create a histogram and occasionally transfers all values to it.
    Histogram total = new CasHistogram(histogram.getSpan());
    for(int i = 0; i < 200; i++){
      total.transferBins(histogram);
      try{
        Thread.sleep(30);
      } catch(InterruptedException e){ System.out.println(e); return;}
    }
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.printf("Time spent: %f", t.check());
    total.transferBins(total);
    dump(total);
  }

  public static void dump(Histogram histogram) {
    int totalCount = 0;
    for (int bin=0; bin<histogram.getSpan(); bin++) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
      totalCount += histogram.getCount(bin);
    }
    System.out.printf("      %9d%n", totalCount);
  }

  public static int countFactors(int p) {
    if (p < 2)
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
        factorCount++;
        p /= k;
      } else
        k++;
    }
    return factorCount;
  }
}

interface Histogram {
  void increment(int bin);
  int getCount(int bin);
  int getSpan();
  int[] getBins();
  int getAndClear(int bin);
  void transferBins(Histogram hist);
}

class CasHistogram implements Histogram {
  private final AtomicInteger[] counts;

  public CasHistogram(int span) {
    counts = new AtomicInteger[span];
    for(int i = 0; i < counts.length; i++){
      counts[i] = new AtomicInteger(0);
    }
  }

  public void increment(int bin) {
    int old = counts[bin].get();
    do{
      old = counts[bin].get();
    } while(!counts[bin].compareAndSet(old,old+1));
  }

  public int getCount(int bin) {
    return counts[bin].get();
  }

  public int getSpan() {
    return counts.length;
  }

  public int[] getBins() {
    int[] arr = new int[counts.length];
    for(int i = 0; i < counts.length; i++)
      arr[i] = counts[i].get();
    return arr;
  }

  public int getAndClear(int bin) {
    int old = counts[bin].get();
    do{
      old = counts[bin].get();
    } while(!counts[bin].compareAndSet(old,0));
    return old;
  }

  public void transferBins(Histogram hist) {
    if(hist.getSpan() != counts.length){
        System.out.println("incompatible lengths of histograms");
    }
    for(int i = 0; i < counts.length; i++){
      final int value = i;
      int limit = hist.getAndClear(value);
      for(int j = 0; j < limit; j++){
        increment(value);
      }
    }
  }
}

// vim: et:st=2:ts=2:sts=2:sw=2
