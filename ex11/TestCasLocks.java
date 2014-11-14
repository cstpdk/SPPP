// For week 11 and 12
// sestoft@itu.dk * 2014-10-30

// Four lock implementations in terms of compare-and-swap:
// SimpleTryLock    -- non-reentrant tryLock and unlock
// ReentrantTryLock -- reentrant tryLock and unlock
// SimpleLock       -- non-reentrant blocking lock and unlock
// MyReentrantLock  -- reentrant blocking lock and unlock

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

// Timing a standard Java Class Library ReentrantLock
import java.util.concurrent.locks.ReentrantLock;

public class TestCasLocks {
  public static void main(String[] args) {
    // timeLocks();
    // testLocks();
    // TryLockPhilosopher.runPhilosophers();
    // BadLockPhilosopher.runPhilosophers();
    GoodLockPhilosopher.runPhilosophers();
  }

  // Simple illustration of our CAS-based lock implementations:

  // The Dining Philosophers problem, due to E.W. Dijkstra 1965.  Five
  // philosophers (threads) sit at a round table on which there are five
  // forks (shared resources), placed between the philosophers.  A
  // philosopher alternatingly thinks and eats spaghetti.  To eat, the
  // philosopher needs exclusive use of the two forks placed to his left
  // and right, so he tries to lock them.  

  // Both the places and the forks are numbered 0 to 5.  The fork to the
  // left of place p has number p, and the fork to the right has number
  // (p+1)%5.

  // Deadlock-free but perhaps livelocking version

  static class TryLockPhilosopher implements Runnable {
    private final Fork[] forks;
    private final int place;
    
    public TryLockPhilosopher(Fork[] forks, int place) {
      this.forks = forks;
      this.place = place;
    }

    public static void runPhilosophers() {
      Fork[] forks = { new Fork(), new Fork(), new Fork(), new Fork(), new Fork() };
      for (int place=0; place<forks.length; place++) {
        Thread phil = new Thread(new TryLockPhilosopher(forks, place));
        phil.start();
      }
    }
      
    public void run() {
      while (true) {
        // Take the two forks to the left and the right
        int left = place, right = (place+1) % forks.length;
        if (forks[left].tryLock()) {
          try { 
            if (forks[right].tryLock()) {
              try { 
                System.out.print(place + " "); 
              } finally { forks[right].unlock(); }
            }
          } finally { forks[left].unlock(); }
        }
        // Think
        try { Thread.sleep(10); }
        catch (InterruptedException exn) { }
      }
    }

    static class Fork extends SimpleTryLock { }
  }

  // This solution is wrong; it will deadlock after a while -- also
  // when using SimpleLock or MyReentrantLock.

  static class BadLockPhilosopher implements Runnable {
    private final Fork[] forks;
    private final int place;
    
    public BadLockPhilosopher(Fork[] forks, int place) {
      this.forks = forks;
      this.place = place;
    }

    public static void runPhilosophers() {
      Fork[] forks = { new Fork(), new Fork(), new Fork(), new Fork(), new Fork() };
      for (int place=0; place<forks.length; place++) {
        Thread phil = new Thread(new BadLockPhilosopher(forks, place));
        phil.start();
      }
    }
      
    // Deadlock-prone version
    public void run() {
      while (true) {
        // Take the two forks to the left and the right
        int left = place, right = (place+1) % forks.length;
        try {
          forks[left].lock();
          forks[right].lock();
          // Eat
          System.out.print(place + " ");
        } finally {
          forks[left].unlock();
          forks[right].unlock();          
        }
        // Think
        try { Thread.sleep(10); }
        catch (InterruptedException exn) { }
      }
    }
    
    static class Fork extends SimpleLock { }
  }

  // This solution is correct, takes the locks in the right order.

  static class GoodLockPhilosopher implements Runnable {
    private final Fork[] forks;
    private final int place;
    
    public GoodLockPhilosopher(Fork[] forks, int place) {
      this.forks = forks;
      this.place = place;
    }

    public static void runPhilosophers() {
      Fork[] forks = { new Fork(), new Fork(), new Fork(), new Fork(), new Fork() };
      for (int place=0; place<forks.length; place++) {
        Thread phil = new Thread(new GoodLockPhilosopher(forks, place));
        phil.start();
      }
    }
      
    // Deadlock-prone version
    public void run() {
      while (true) {
        // Take the two forks to the left and the right
        int left = place, right = (place+1) % forks.length;
        int first = Math.min(left, right), 
          second =  Math.max(left, right);
        try {
          forks[first].lock();
          forks[second].lock();
          // Eat
          System.out.print(place + " ");
        } finally {
          forks[first].unlock();
          forks[second].unlock();          
        }
        // Think
        try { Thread.sleep(10); }
        catch (InterruptedException exn) { }
      }
    }
    
    static class Fork extends SimpleLock { }
  }

  private static void testLocks() {
    {
      final SimpleTryLock lock = new SimpleTryLock();
      assert lock.tryLock();
      assert !lock.tryLock();
      lock.unlock();
      try { lock.unlock();  assert false; } catch (RuntimeException exn) { }
    }
    {
      final ReentrantTryLock lock = new ReentrantTryLock();
      assert lock.tryLock();
      assert lock.tryLock();
      lock.unlock();
      lock.unlock();
      try { lock.unlock();  assert false; } catch (RuntimeException exn) { }
    }
    {
      final SimpleLock lock = new SimpleLock();
      lock.lock();
      // lock.lock(); // Should block
      lock.unlock();
      try { lock.unlock();  assert false; } catch (RuntimeException exn) { }
    }
    {
      final MyReentrantLock lock = new MyReentrantLock();
      lock.lock();
      lock.lock();
      lock.unlock();
      lock.unlock();
      try { lock.unlock();  assert false; } catch (RuntimeException exn) { }
    }
  }

  private static void timeLocks() {
    SystemInfo();
    {
      final SimpleTryLock lock = new SimpleTryLock();
      Mark6("Untaken SimpleTryLock", 
            new IntToDouble() { public double call(int i) {
              try { 
                lock.tryLock();
                return i;
              } finally {
                lock.unlock();
              }
          }});
    }
    {
      final SimpleLock lock = new SimpleLock();
      Mark6("Untaken SimpleLock", 
            new IntToDouble() { public double call(int i) {
              try { 
                lock.lock();
                return i;
              } finally {
                lock.unlock();
              }
          }});
    }
    {
      final ReentrantTryLock lock = new ReentrantTryLock();
      Mark6("Untaken ReentrantTryLock", 
            new IntToDouble() { public double call(int i) {
              try { 
                lock.tryLock();
                return i;
              } finally {
                lock.unlock();
              }
          }});
    }
    {
      final MyReentrantLock lock = new MyReentrantLock();
      Mark6("Untaken MyReentrantLock", 
            new IntToDouble() { public double call(int i) {
              try { 
                lock.lock();
                return i;
              } finally {
                lock.unlock();
              }
          }});
    }
    {
      final ReentrantLock lock = new ReentrantLock();
      Mark6("Untaken ReentrantLock", 
            new IntToDouble() { public double call(int i) {
              try { 
                lock.lock();
                return i;
              } finally {
                lock.unlock();
              }
          }});
    }
  }
  
  // --- Benchmarking infrastructure ---

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
        double time = runningTime * 1e9 / count; // nanoseconds
        st += time; 
        sst += time * time;
        totalCount += count;
      }
      double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
      System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
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
        double time = runningTime * 1e9 / count; // nanoseconds
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
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

// ======================================================================

// The lock implementations

// The correctness of everything below relies on
// Thread.currentThread() != null, but that must be taken as given.

// ----------------------------------------------------------------------

// Non-reentrant lock supporting only tryLock and unlock.  In all
// cases the owner of the lock is the thread that equals holder.get().

class SimpleTryLock {
  // Refers to holding thread, null iff unheld
  private final AtomicReference<Thread> holder = new AtomicReference<Thread>();

  public boolean tryLock() {
    final Thread current = Thread.currentThread();
    return holder.compareAndSet(null, current);
  }

  public void unlock() {
    final Thread current = Thread.currentThread();
    if (!holder.compareAndSet(current, null))
      throw new RuntimeException("Not lock holder");
  }

  // This would work too, because holder can change (in tryLock) only
  // if null, and current is never null.  But the above version is
  // better because of the symmetry with lock() and because it makes
  // it evident that holder only ever gets changed from null to
  // current, or back, where current is the current thread's object.
  public void unlockVariant() {
    final Thread current = Thread.currentThread();
    if (holder.get() == current) 
      holder.set(null);
    else
      throw new RuntimeException("Not lock holder");
  }
}

// ----------------------------------------------------------------------

// Reentrant lock supporting only tryLock and unlock

// Only the owner thread ever accesses the holdCount field (so it
// actually does not need to be volatile?!).  Only the owner thread
// ever changes the reference in holder, to null, or from null, and
// then only in the atomic act of becoming the owner.

class ReentrantTryLock {
  // Refers to holding thread, null iff unheld
  private final AtomicReference<Thread> holder = new AtomicReference<Thread>();
  // Valid only if holder != null
  private volatile int holdCount = 0;

  public boolean tryLock() {
    final Thread current = Thread.currentThread();
    if (holder.get() == current) {    // already held by this thread
      holdCount++;
      return true;
    } else if (holder.compareAndSet(null, current)) {        
      holdCount = 1;    // was unheld and we got it
      return true;
    } 
    return false;       // already held, or just grabbed, by other thread
  }

  public void unlock() {
    final Thread current = Thread.currentThread();
    if (holder.get() == current) {
      holdCount--;
      if (holdCount != 0 || holder.compareAndSet(current, null))
        return;
    }
    throw new RuntimeException("Not lock holder");
  }
}

// ----------------------------------------------------------------------

// Non-reentrant lock supporting blocking lock and unlock, modified
// from the FIFOMutex example in the Java LockSupport class
// documentation, but using AtomicReference<Thread> instead of
// AtomicBoolean, to check that an unlocking thread owns the lock.
// Based on example in java.util.concurrent.LockSupport documentation

class SimpleLock {
  // Refers to holding thread, null iff unheld
  private final AtomicReference<Thread> holder = new AtomicReference<Thread>();
  // The FIFO queue of threads waiting for this lock
  private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();
  
  public void lock() {
    final Thread current = Thread.currentThread();
    boolean wasInterrupted = false;
    waiters.add(current);
    // Block while not first in queue or cannot acquire lock
    while (waiters.peek() != current || !holder.compareAndSet(null, current)) {
      LockSupport.park(this);
      if (Thread.interrupted())        // also clears interrupted status
        wasInterrupted = true;
    }
    waiters.remove();
    if (wasInterrupted)                // reassert interrupt on exit
      current.interrupt();
  }
  
  public void unlock() {
    final Thread current = Thread.currentThread();
    if (holder.compareAndSet(current, null)) 
      LockSupport.unpark(waiters.peek()); // null arg has no effect
    else
      throw new RuntimeException("Not lock holder");
  }
}

// ----------------------------------------------------------------------

// Reentrant lock supporting blocking lock and unlock, modified from
// the FIFOMutex example in the Java LockSupport class documentation,
// but using AtomicReference<Thread> instead of AtomicBoolean, to
// check that an unlocking thread owns the lock.
// Based on example in java.util.concurrent.LockSupport documentation

class MyReentrantLock {
  // Refers to holding thread, null iff lock unheld
  private final AtomicReference<Thread> holder = new AtomicReference<Thread>();
  // The FIFO queue of threads waiting for this lock
  private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();
  // Valid only if holder != null
  private volatile int holdCount = 0;
  
  public void lock() {
    final Thread current = Thread.currentThread();
    if (holder.get() == current) 
      holdCount++;
    else {   
      boolean wasInterrupted = false;
      waiters.add(current);
      // Block while not first in queue or cannot acquire lock
      while (waiters.peek() != current || !holder.compareAndSet(null, current)) {
        LockSupport.park(this);
        if (Thread.interrupted())        // also clears interrupted status
          wasInterrupted = true;
      }
      holdCount = 1;
      waiters.remove();
      if (wasInterrupted)                // reassert interrupt on exit
        current.interrupt();
    }
  }
    
  public void unlock() {
    final Thread current = Thread.currentThread();
    if (holder.get() == current) {
      holdCount--;
      if (holdCount != 0) 
        return;
      else {
        if (holder.compareAndSet(current, null)) {
          LockSupport.unpark(waiters.peek()); // null arg has no effect
          return;
        }
      }
    }
    throw new RuntimeException("Not lock holder");
  }
}
