// For week 6
// sestoft@itu.dk * 2014-09-23

// You must download jsr305-3.0.0.jar and put it somewhere, let's say
// /Users/sestoft/lib/jsr305-3.0.0.jar alias ~/lib/jsr305-3.0.0.jar 

// Then compile this file with the classpath (cp) pointing to the jar:
//   javac -g -cp ~/lib/jsr305-3.0.0.jar TestGuardedBy.java 

// Note that the Java compiler (javac) performs NO checking
// of @GuardedBy annotations.

// To check annotation, you can use the ThreadSafe tool.  Install it
// (see lecture 6) in ~/lib/ts/ and run it AFTER compiling as above:
//   java -jar ~/lib/ts/threadsafe.jar
// Then read ThreadSafe's report in a browser:
//   open threadsafe-html/index.html

// Or do the whole thing in Eclipse, where it works more smoothly.

// From JSR 305 jar file jsr305-3.0.0.jar:
import javax.annotation.concurrent.GuardedBy;

import java.io.IOException;

public class TestGuardedBy {
  public static void main(String[] args) throws IOException {
    final LongCounter lc = new LongCounter();
    Thread t = new Thread(new Runnable() {
	public void run() {
	  while (true)		// Forever call increment
	    lc.increment();
	}
      });
    t.start();
    System.out.println("Press Enter to get the current value:");
    while (true) {
      System.in.read();         // Wait for enter key
      System.out.println(lc.get()); 
    }
  }
}

class LongCounter {
  @GuardedBy("this")
  private long count = 0;
  public synchronized void increment() { count++; }
  public synchronized long get() { return count; }
}
