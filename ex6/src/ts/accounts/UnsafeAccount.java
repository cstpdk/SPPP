// For week 6
// sestoft@itu.dk * 2014-09-29

// Compile with
//   javac -g -cp ~/lib/jsr305-3.0.0.jar UnsafeAccount.java
// Run ThreadSafe with
//   java -jar ~/lib/ts/threadsafe.jar
// Open ThreadSafe's report threadsafe-html/index.html in a browser

// From JSR 305 jar file jsr305-3.0.0.jar:
import javax.annotation.concurrent.GuardedBy;

class Account {
  @GuardedBy("this")
  private long balance = 0;

  public synchronized void deposit(long amount) {
    balance += amount;
  }

  public synchronized long get() {
    return balance;
  }

  public synchronized void transferA(Account that, long amount) {
    this.balance = this.balance - amount;
    that.balance = that.balance + amount;
  }

  // This (wrongly) allows observation in the middle of a transfer
  public void transferB(Account that, long amount) {
    this.deposit(-amount);
    that.deposit(+amount);
  }
}
