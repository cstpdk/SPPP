// For week 6
// sestoft@itu.dk * 2014-10-02

// Main points: When two objects are involved in a transaction,
// locking on them individually does not work.  On the other hand,
// locking too aggressively leads to deadlock.  Always locking in a
// consistent order works.  May also used a timed lock to avoid
// deadlock, may still suffer from livelock.

// TransferG: Locks both accounts using tryLock, retrying after a
// random-length sleep.  

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class TestAccountTryLock {
  public static void main(String[] args) {
    final Account account1 = new Account(), account2 = new Account();
    final Random rnd = new Random();
    final int transfers = 2_000_000;
    account1.deposit(3000); account2.deposit(2000);
    Thread clerk1 = new Thread(new Runnable() {
        public void run() { 
          for (int i=0; i<transfers; i++) 
            account1.transferG(account2, rnd.nextInt(10000));
        }
      });
    Thread clerk2 = new Thread(new Runnable() {
        public void run() { 
          for (int i=0; i<transfers; i++) 
            account2.transferG(account1, rnd.nextInt(10000));
        }
      });
    clerk1.start(); clerk2.start();
    // We occasionally print the account balances during the transfer:
    for (int i=0; i<40; i++) {
      try { Thread.sleep(10); } catch (InterruptedException exn) { }
      // Locking both accounts is necessary to avoid reading the
      // balance in the middle of a transfer.
      System.out.println(Account.balanceSumG(account1, account2));
    }
    // The auditor prints the account balance sum when the clerks are finished: 
    try { clerk1.join(); clerk2.join(); } catch (InterruptedException exn) { }
    System.out.println("\nFinal sum is " + Account.balanceSumG(account1, account2));
  }
}


class Account {
  private final Lock lock = new ReentrantLock();
  
  private long balance = 0;

  public void deposit(long amount) {
    lock.lock();
    try { 
      balance += amount;
    } finally {
      lock.unlock();
    }
  }

  public long get() {
    lock.lock();
    try { 
      return balance;
    } finally {
      lock.unlock();
    }
  }

  // This is thread-safe and cannot deadlock; but may in theory livelock
  public void transferG(Account that, final long amount) {
    Account ac1 = this, ac2 = that;
    while (true) {
      if (ac1.lock.tryLock()) {
	try {
	  if (ac2.lock.tryLock()) {
	    try {
	      ac1.balance = ac1.balance - amount;
	      ac2.balance = ac2.balance + amount;
	      return;
	    } finally {
	      ac2.lock.unlock();
	    }
	  }
	} finally {
	  ac1.lock.unlock();
	}
      }
      try { Thread.sleep(0, (int)(500 * Math.random())); }
      catch (InterruptedException exn) { }
    }
  }

  public static long balanceSumG(Account ac1, Account ac2) {
    while (true) {
      if (ac1.lock.tryLock()) {
	try {
	  if (ac2.lock.tryLock()) {
	    try {
	      return ac1.balance + ac2.balance;
	    } finally {
	      ac2.lock.unlock();
	    }
	  }
	} finally {
	  ac1.lock.unlock();
	}
      }
      try { Thread.sleep(0, (int)(500 * Math.random())); }
      catch (InterruptedException exn) { }
    }
  }
}
