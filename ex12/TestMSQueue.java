// For week 12
// sestoft@itu.dk * 2014-11-16

// Unbounded list-based lock-free queue by Michael and Scott 1996 (who
// call it non-blocking).

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMSQueue {
  public static void main(String[] args) throws Exception {
    MSQueue<Integer> q = new MSQueue<Integer>();
    sequentialTest(q);
    parallelTest(q);
  }

  public static void assertEquals(int x, int y) throws Exception {
    if (x != y)
      throw new Exception(String.format("ERROR: %d not equal to %d%n", x, y));
  }

  public static void assertTrue(boolean b) throws Exception {
    if (!b)
      throw new Exception(String.format("ERROR: assertTrue"));
  }

  @SuppressWarnings("unchecked")
  private static void sequentialTest(UnboundedQueue<Integer> bq) throws Exception {
    System.out.printf("%nSequential test: %s", bq.getClass());
    for(int i = 0; i < 1000; i++){
      bq.enqueue(i);
    }
    for(int i = 0; i < 1000; i++){
      assertEquals(bq.dequeue(), i);
    }


    System.out.println("... passed Sequential");
  }

  private static void parallelTest(MSQueue<Integer> bq) throws Exception {
    System.out.printf("%nParallel test: %s", bq.getClass());
    final ExecutorService pool = Executors.newCachedThreadPool();
    new PutTakeTest(bq, 17, 100000).test(pool);
    pool.shutdown();
    System.out.println("... passed parallel");
  }

  /**
   * Producer-consumer test program for BoundedQueue
   *
   * @author Brian Goetz and Tim Peierls; some modifications by
   * sestoft@itu.dk
   */

  static class PutTakeTest extends Tests {
    protected CyclicBarrier barrier;
    protected final UnboundedQueue<Integer> bq;
    protected final int nTrials, nPairs;
    protected final AtomicInteger putSum = new AtomicInteger(0);
    protected final AtomicInteger takeSum = new AtomicInteger(0);

    public PutTakeTest(UnboundedQueue<Integer> bq, int npairs, int ntrials) {
      this.bq = bq;
      this.nTrials = ntrials;
      this.nPairs = npairs;
      this.barrier = new CyclicBarrier(npairs * 2 + 1);
    }

    void test(ExecutorService pool) {
      try {
        for (int i = 0; i < nPairs; i++) {
          pool.execute(new Producer());
          pool.execute(new Consumer());
        }
        barrier.await(); // wait for all threads to be ready
        barrier.await(); // wait for all threads to finish

        assertEquals(putSum.get(), takeSum.get());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    class Producer implements Runnable {
      public void run() {
        try {
          Random random = new Random();
          int sum = 0;
          barrier.await();
          for (int i = nTrials; i > 0; --i) {
            int item = random.nextInt();
            bq.enqueue(item);
            sum += item;
          }
          putSum.getAndAdd(sum);
          barrier.await();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    class Consumer implements Runnable {
      public void run() {
        try {
          barrier.await();
          int sum = 0;
          for (int i = nTrials; i > 0; --i) {
            Integer dequed = bq.dequeue();
            while(dequed == null)
              dequed = bq.dequeue();
            sum += dequed;
          }
          takeSum.getAndAdd(sum);
          barrier.await();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}



interface UnboundedQueue<T> {
  void enqueue(T item);
  T dequeue();
}

// ------------------------------------------------------------
// Unbounded lock-based queue with sentinel (dummy) node

class LockingQueue<T> implements UnboundedQueue<T> {
  // Invariants:
  // The node referred by tail is reachable from head.
  // If non-empty then head != tail,
  //    and tail points to last item, and head.next to first item.
  // If empty then head == tail.

  private static class Node<T> {
    final T item;
    Node<T> next;

    public Node(T item, Node<T> next) {
      this.item = item;
      this.next = next;
    }
  }

  private Node<T> head, tail;

  public LockingQueue() {
    head = tail = new Node<T>(null, null);
  }

  public synchronized void enqueue(T item) { // at tail
    Node<T> node = new Node<T>(item, null);
    tail.next = node;
    tail = node;
  }

  public synchronized T dequeue() {     // from head
    if (head.next == null)
      return null;
    Node<T> first = head;
    head = first.next;
    return head.item;
  }
}


// ------------------------------------------------------------
// Unbounded lock-free queue (non-blocking in M&S terminology),
// using CAS and AtomicReference

// This creates one AtomicReference object for each Node object.  The
// next MSQueueRefl class further below uses one-time reflection to
// create an AtomicReferenceFieldUpdater, thereby avoiding this extra
// object.  In practice the overhead of the extra object apparently
// does not matter much.

class MSQueue<T> implements UnboundedQueue<T> {
  private final AtomicReference<Node<T>> head, tail;

  public MSQueue() {
    Node<T> dummy = new Node<T>(null, null);
    head = new AtomicReference<Node<T>>(dummy);
    tail = new AtomicReference<Node<T>>(dummy);
  }

  public void enqueue(T item) { // at tail
    Node<T> node = new Node<T>(item, null);
    while (true) {
      Node<T> last = tail.get(), next = last.next.get();
      if (last == tail.get()) {         // E7
        if (next == null)  {
          // In quiescent state, try inserting new node
          if (last.next.compareAndSet(next, node)) { // E9
            // Insertion succeeded, try advancing tail
            tail.compareAndSet(last, node);
            return;
          }
        } else
          // Queue in intermediate state, advance tail
          tail.compareAndSet(last, next);
      }
    }
  }

  public T dequeue() { // from head
    while (true) {
      Node<T> first = head.get(), last = tail.get(), next = first.next.get(); // D3
      if (first == head.get()) {        // D5
        if (first == last) {
          if (next == null)
            return null;
          else
            tail.compareAndSet(last, next);
        } else {
          T result = next.item;
          if (head.compareAndSet(first, next)) // D13
            return result;
        }
      }
    }
  }

  private static class Node<T> {
    final T item;
    final AtomicReference<Node<T>> next;

    public Node(T item, Node<T> next) {
      this.item = item;
      this.next = new AtomicReference<Node<T>>(next);
    }
  }
}


// --------------------------------------------------
// Lock-free queue, using CAS and reflection on field Node.next

class MSQueueRefl<T> implements UnboundedQueue<T> {
  private final AtomicReference<Node<T>> head, tail;

  public MSQueueRefl() {
    // Essential to NOT make dummy a field as in Goetz p. 334, that
    // would cause a memory management disaster, huge space leak:
    Node<T> dummy = new Node<T>(null, null);
    head = new AtomicReference<Node<T>>(dummy);
    tail = new AtomicReference<Node<T>>(dummy);
  }

  @SuppressWarnings("unchecked")
  // Java's @$#@?!! generics type system: abominable unsafe double type cast
  private final AtomicReferenceFieldUpdater<Node<T>, Node<T>> nextUpdater
    = AtomicReferenceFieldUpdater.newUpdater((Class<Node<T>>)(Class<?>)(Node.class),
                                             (Class<Node<T>>)(Class<?>)(Node.class),
                                             "next");

  public void enqueue(T item) { // at tail
    Node<T> node = new Node<T>(item, null);
    while (true) {
      Node<T> last = tail.get(), next = last.next;

      //room for interleaving!

      if (last == tail.get()) {         // E7
        if (next == null)  {
          // In quiescent state, try inserting new node
          if (nextUpdater.compareAndSet(last, next, node)) {
            // Insertion succeeded, try advancing tail
            tail.compareAndSet(last, node);
            return;
          }
        } else {
          // Queue in intermediate state, advance tail
          tail.compareAndSet(last, next);
        }
      }
    }
  }

  public T dequeue() { // from head
    while (true) {
      Node<T> first = head.get(), last = tail.get(), next = first.next;
      if (first == head.get()) {        // D5
        if (first == last) {
          if (next == null)
            return null;
          else
            tail.compareAndSet(last, next);
        } else {
          T result = next.item;
          if (head.compareAndSet(first, next)) {
            return result;
          }
        }
      }
    }
  }

  private static class Node<T> {
    final T item;
    volatile Node<T> next;

    public Node(T item, Node<T> next) {
      this.item = item;
      this.next = next;
    }
  }



}
