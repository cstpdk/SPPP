// For week 2
// sestoft@itu.dk * 2014-08-29
import java.util.concurrent.atomic.*;

class TestCountFactors {
  public static void main(String[] args) {
    final int range = 5_000_000;

    //System.out.printf("[Sequential] Total number of factors is %9d%n", countSequential(range));
    System.out.printf("[Par10] Total number of factors is %9d%n", countParallel10(range));
  }

  public static class MyAtomicInteger{
    int value = 0;

    synchronized int addAndGet(int amount){
      value += amount;
      return get();
    }

    int get(){
      return value;
    }
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

  public static int countSequential(int range){
    int count = 0;
    for (int p=0; p<range; p++)
      count += countFactors(p);
    return count;
  }

  private static Thread countInThread(int from, int to, AtomicInteger mai){
    Thread t = new Thread(new Runnable() {
      public void run(){
        for (int i = from; i < to; i++){
          mai.addAndGet(countFactors(i));
        }
      }
    });
    return t;
  }

  public static int countParallel10(int range){
    int steps = 10;
    int stepsize = range / steps;
    //final MyAtomicInteger ai = new MyAtomicInteger();
    final AtomicInteger ai = new AtomicInteger();
    Thread[] threads = new Thread[steps];

    for(int i = 0; i < steps; i++){

        final int from = stepsize*i;
        final int to = stepsize*(i+1)-1;

        Thread t = countInThread(from,to,ai);
        t.start();
        threads[i] = t;
    }

    for(int i = 0; i < steps; i++){
      try {
        threads[i].join();
      } catch (InterruptedException exn){ }
    }

    return ai.get();
  }
}

// vim: ts=2 sts=2 et:
