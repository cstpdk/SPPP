// For week 6
// sestoft@itu.dk * 2014-09-29

// Main points: When two objects are involved in a transaction,
// locking on them individually does not work.  On the other hand,
// locking too aggressively leads to deadlock.  Always locking in a
// consistent order works.

// TransferC: Locks both accounts to make the transfer atomic, but
// does not take locks in consistent order and so may deadlock.

import java.util.Random;

public class TestAccountDeadlock {
  public static void main(String[] args) {
    final Account account1 = new Account(), account2 = new Account();
    final Random rnd = new Random();
    final int transfers = 2_000_000;
    account1.deposit(3000); account2.deposit(2000);
    Thread clerk1 = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<transfers; i++) 
	    account1.transferC(account2, rnd.nextInt(10000));
	}
      });
    Thread clerk2 = new Thread(new Runnable() {
	public void run() { 
	  for (int i=0; i<transfers; i++) 
	    account2.transferC(account1, rnd.nextInt(10000));
	}
      });
    clerk1.start(); clerk2.start();
    // We occasionally print the account balances during the transfer:
    for (int i=0; i<40; i++) {
      try { Thread.sleep(10); } catch (InterruptedException exn) { }
      // Locking both accounts is necessary to avoid reading the
      // balance in the middle of a transfer.
      System.out.println(account1.get() + account2.get());
    }
    // The auditor prints the account balance sum when the clerks are finished: 
    try { clerk1.join(); clerk2.join(); } catch (InterruptedException exn) { }
    System.out.printf("%nFinal sum is %d%n", account1.get() + account2.get());
  }
}


class Account {
  private long balance = 0;

  public synchronized void deposit(long amount) {
    balance += amount;
  }

  public synchronized long get() {
    return balance;
  }

  // This may deadlock
  public void transferC(Account that, long amount) {
    synchronized (this) { synchronized(that) { 
	this.balance = this.balance - amount;
	that.balance = that.balance + amount;
      } }
  }
}
