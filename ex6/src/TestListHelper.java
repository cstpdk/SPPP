// For week 3 and 6

// Code from Goetz et al 4.4, written by Brian Goetz and Tim Peierls.
// Minor modifications by sestoft@itu.dk * 2014-09-03

import java.util.*;

public class TestListHelper {
  public static void main(String[] args) {


  }
}

/**
 * Examples of thread-safe and non-thread-safe implementations of
 * put-if-absent helper methods for List.
 * @author Brian Goetz and Tim Peierls
 */

// Not thread-safe because of non-atomic test-then-act: two threads
// may call fblh.putIfAbsent(7) at the same time.

class FirstBadListHelper<E> {
  private final List<E> list = Collections.synchronizedList(new ArrayList<E>());

  public boolean putIfAbsent(E x) {
    boolean absent = !list.contains(x);
    if (absent)
      list.add(x);
    return absent;
  }
}

// Not thread-safe because of locking on the wrong object: a
// SecondBadListHelper instance rather than the list, and one thread
// may perform sblh.list.add(7) while another thread performs
// sblh.putIfAbsent(7).

class SecondBadListHelper<E> {
  public final List<E> list = Collections.synchronizedList(new ArrayList<E>());

  public synchronized boolean putIfAbsent(E x) {
    boolean absent = !list.contains(x);
    if (absent)
      list.add(x);
    return absent;
  }

  public boolean add(E x) {
    return list.add(x);
  } 

  public boolean remove(E x) {
    return list.remove(x);
  } 
}

// Much better, locks on the right object, but clearly a mistake to
// make the field public (and non-final): another thread could make
// the field glh.list point to a new list while we hold the lock on
// the old one!  Seems it would be safe with public final list though.

class GoodListHelper<E> {
  public List<E> list = Collections.synchronizedList(new ArrayList<E>());

  public boolean putIfAbsent(E x) {
    synchronized (list) {
      boolean absent = !list.contains(x);
      if (absent)
        list.add(x);
      return absent;
    }
  }

  public boolean add(E x) {
    return list.add(x);
  } 

  public boolean remove(E x) {
    return list.remove(x);
  } 
}

class BetterArrayList<E> {
  private List<E> list = new ArrayList<E>();

  public synchronized boolean add(E item) {
    return list.add(item);
  }

  public synchronized boolean putIfAbsent(E x) {
    boolean absent = !list.contains(x);
    if (absent)
      list.add(x);
    return absent;
  }
}



