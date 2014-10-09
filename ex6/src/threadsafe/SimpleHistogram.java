// For week 3
// sestoft@itu.dk * 2014-09-04
import javax.annotation.concurrent.*;
class SimpleHistogram {
  public static void main(String[] args) {
    final Histogram histogram = new Histogram2(30);
    histogram.increment(7);
    histogram.increment(13);
    histogram.increment(7);
    dump(histogram);
  }

  public static void dump(Histogram histogram) {
    int totalCount = 0;
    for (int item=0; item<histogram.getSpan(); item++) {
      System.out.printf("%4d: %9d%n", item, histogram.getCount(item));
      totalCount += histogram.getCount(item);
    }
    System.out.printf("      %9d%n", totalCount);
  }
}

interface Histogram {
  public void increment(int item);
  public int getCount(int item);
  public int getSpan();
  public void addAll(Histogram hist);
}


/**
 * Exercise 3.1
 */
class Histogram2 implements Histogram {
	@GuardedBy("this")  
	private final int[] counts;
	  
	  public Histogram2(int span) {
	    this.counts = new int[span];
	  }
	  public synchronized void increment(int item) {
	    counts[item] = counts[item] + 1;
	  }
	  public synchronized int getCount(int item) {
	    return counts[item];
	  }
	  public int getSpan() {
	    return counts.length;
	  }

    public void addAll(Histogram that) throws RuntimeException{
      if(this.getSpan() != that.getSpan())
        throw new RuntimeException();
      else {

        synchronized(this){
          synchronized(that){
            for(int i = 0; i < counts.length; i++){
              for(int j = 0; j<that.getCount(i); j++)
                this.increment(i);
            }
          }
        }
        
      }
    }
    public void addAll2(Histogram2 that) throws RuntimeException{
      if(this.getSpan() != that.getSpan())
        throw new RuntimeException();
      else {
        
        
            for(int i = 0; i < counts.length; i++){
              for(int j = 0; j<that.counts[i]; j++)
                this.counts[i]++;
            }
   
        
      }
    }
}

