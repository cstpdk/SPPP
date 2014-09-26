// Simple microbenchmark setups
// sestoft@itu.dk * 2013-06-02, 2014-09-10

class Benchmark {
  public static void main(String[] args) {
    SystemInfo();
    if(args[0].equals("Mark0")) { Mark0(); }
    if(args[0].equals("Mark1")) { Mark1(); }
    if(args[0].equals("Mark2")) { Mark2(); }
    if(args[0].equals("Mark3")) { Mark3(); }
    if(args[0].equals("Mark4")) { Mark4(); }
    if(args[0].equals("Mark5")) { Mark5(); }
    if(args[0].equals("Mark6")) { Mark6("multiply", new IntToDouble() { 
        public double call(int i) { return multiply(i); } }); }
    if(args[0].equals("Mark7")) { Mark7("multiply", new IntToDouble() { 
        public double call(int i) { return multiply(i); } }); }
    if(args[0].equals("MathFunctionBenchmarks")) { MathFunctionBenchmarks(); }
    final java.util.Random rnd = new java.util.Random();
    final int n = 1638400;
    if(args[0].equals("Mark8")) { Mark8("random_index", new IntToDouble() { 
     public double call(int i) { return rnd.nextInt(n); } }); }
    if(args[0].equals("SearchBenchmarks")) { SearchBenchmarks(); }
    if(args[0].equals("SearchScalabilityBenchmarks1")) { SearchScalabilityBenchmarks1(); }
    if(args[0].equals("SearchScalabilityBenchmarks2")) { SearchScalabilityBenchmarks2(); }
    if(args[0].equals("GetPseudorandomItems")) { GetPseudorandomItems(); }
    if(args[0].equals("SortingBenchmarks")) { SortingBenchmarks(); }
    if(args[0].equals("SortingScalabilityBenchmarks")) { SortingScalabilityBenchmarks(); }
  }

  // ========== Example functions and benchmarks ==========

  private static double multiply(int i) {
    double x = 1.1 * (double)(i & 0xFF);
     return x * x * x * x * x * x * x * x * x * x 
          * x * x * x * x * x * x * x * x * x * x;
  }

  private static void MathFunctionBenchmarks() {
    Mark7("pow", new IntToDouble() { 
      public double call(int i) { return Math.pow(10.0, 0.1 * (i & 0xFF)); } });
    Mark7("exp", new IntToDouble() { 
      public double call(int i) { return Math.exp(0.1 * (i & 0xFF)); } });
    Mark7("log", new IntToDouble() { 
      public double call(int i) { return Math.log(0.1 + 0.1 * (i & 0xFF)); } });
    Mark7("sin", new IntToDouble() { 
      public double call(int i) { return Math.sin(0.1 * (i & 0xFF)); } });
    Mark7("cos", new IntToDouble() { 
      public double call(int i) { return Math.cos(0.1 * (i & 0xFF)); } });
    Mark7("tan", new IntToDouble() { 
      public double call(int i) { return Math.tan(0.1 * (i & 0xFF)); } });
    Mark7("asin", new IntToDouble() { 
      public double call(int i) { return Math.asin(1.0/256.0 * (i & 0xFF)); } });
    Mark7("acos", new IntToDouble() { 
      public double call(int i) { return Math.acos(1.0/256.0 * (i & 0xFF)); } });
    Mark7("atan", new IntToDouble() { 
      public double call(int i) { return Math.atan(1.0/256.0 * (i & 0xFF)); } });
  }

  private static void SearchBenchmarks() {
    final int[] intArray = SearchAndSort.fillIntArray(10_000);  // sorted [0,1,...]
    final int successItem = 4900, failureItem = 14000;
    Mark7("linear_search_success", 
       new IntToDouble() { 
         public double call(int i) { 
           return SearchAndSort.linearSearch(successItem, intArray); } });
    Mark7("binary_search_success", 
       new IntToDouble() { 
         public double call(int i) { 
           return SearchAndSort.binarySearch(successItem, intArray); } });
  }

  private static void SearchScalabilityBenchmarks1() {
    for (int size = 100; size <= 10_000_000; size *= 2) {
      final int[] intArray = SearchAndSort.fillIntArray(size);  // sorted [0,1,...]
      final int successItem = (int)(0.49 * size);
      Mark8("binary_search_success", 
            String.format("%8d", size),
            new IntToDouble() { 
              public double call(int i) { 
                return SearchAndSort.binarySearch(successItem, intArray); } });      
    }
  }

  private static void SearchScalabilityBenchmarks2() {
    for (int size = 100; size <= 10_000_000; size *= 2) {
      final int[] intArray = SearchAndSort.fillIntArray(size);  // sorted [0,1,...]
      final int[] items = SearchAndSort.fillIntArray(size); 
      final int n = size;
      SearchAndSort.shuffle(items);
      Mark8("binary_search_success", 
            String.format("%8d", size),
            new IntToDouble() { 
              public double call(int i) { 
                int successItem = items[i % n];
                return SearchAndSort.binarySearch(successItem, intArray); } });      
    }
  }

  private static void GetPseudorandomItems() {
    for (int size = 100; size <= 10_000_000; size *= 2) {
      final int[] items = SearchAndSort.fillIntArray(size); 
      final int n = size;
      SearchAndSort.shuffle(items);
      Mark8("get_pseudorandom_items", 
            String.format("%8d", size),
            new IntToDouble() { 
              public double call(int i) { 
                int successItem = items[i % n];
                return successItem; } });      
    }
  }

  private static void SortingBenchmarks() {
    final int[] intArray = SearchAndSort.fillIntArray(10_000);
    Mark7("shuffle int", 
       new IntToDouble() { 
         public double call(int i) { SearchAndSort.shuffle(intArray); 
                                     return 0.0; } });
    Mark8Setup("shuffle", 
       new Benchmarkable() { 
         public double call(int i) { SearchAndSort.shuffle(intArray); 
                                     return 0.0; } });
    Mark8Setup("selection_sort", 
       new Benchmarkable() { 
         public void setup() { SearchAndSort.shuffle(intArray); }
         public double call(int i) { SearchAndSort.selsort(intArray); 
                                     return 0.0; } });
    Mark8Setup("quicksort", 
       new Benchmarkable() { 
         public void setup() { SearchAndSort.shuffle(intArray); }
         public double call(int i) { SearchAndSort.quicksort(intArray); 
                                     return 0.0; } });
    Mark8Setup("heapsort", 
       new Benchmarkable() { 
         public void setup() { SearchAndSort.shuffle(intArray); }
         public double call(int i) { SearchAndSort.heapsort(intArray); 
                                     return 0.0; } });
  }

  private static void SortingScalabilityBenchmarks() {
    for (int size = 100; size <= 50000; size *= 2) {
      final int[] intArray = SearchAndSort.fillIntArray(size);
      Mark8Setup("selection_sort", 
                 String.format("%8d", size),
                 new Benchmarkable() { 
                   public void setup() { SearchAndSort.shuffle(intArray); }
                   public double call(int i) { SearchAndSort.selsort(intArray); 
                                               return 0.0; } });
    }
    System.out.printf("%n%n"); // data set divider
    for (int size = 100; size <= 2000000; size *= 2) {
      final int[] intArray = SearchAndSort.fillIntArray(size);
      Mark8Setup("quicksort", 
                 String.format("%8d", size),
                 new Benchmarkable() { 
                   public void setup() { SearchAndSort.shuffle(intArray); }
                   public double call(int i) { SearchAndSort.quicksort(intArray); 
                                               return 0.0; } });
    }
    System.out.printf("%n%n"); // data set divider
    for (int size = 100; size <= 2000000; size *= 2) {
      final int[] intArray = SearchAndSort.fillIntArray(size);
      Mark8Setup("heapsort", 
                 String.format("%8d", size),
                 new Benchmarkable() { 
                   public void setup() { SearchAndSort.shuffle(intArray); }
                   public double call(int i) { SearchAndSort.heapsort(intArray); 
                                               return 0.0; } });
    }
  }

  // ========== Infrastructure code ==========

  public static void SystemInfo() {
    System.out.printf("# OS:   %s; %s; %s%n", 
                      System.getProperty("os.name"), 
                      System.getProperty("os.version"), 
                      System.getProperty("os.arch"));
    System.out.printf("# JVM:  %s; %s%n", 
                      System.getProperty("java.vendor"), 
                      System.getProperty("java.version"));
    // This line works only on MS Windows:
    // System.out.printf("# CPU:  %s%n", System.getenv("PROCESSOR_IDENTIFIER"));
    try {
	java.util.Scanner sc = new java.util.Scanner(new java.io.File("cpuinfo.txt"));
	while(sc.hasNextLine()){
		System.out.println("# " + sc.nextLine());
	}
    } catch(java.io.FileNotFoundException ignored) {  } 
    java.util.Date now = new java.util.Date();
    System.out.printf("# Date: %s%n", 
      new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
  }

  public static void Mark0() {         // USELESS
    Timer t = new Timer();
    double dummy = multiply(10);
    double time = t.check() * 1e9;
    System.out.printf("%6.1f ns%n", time);
  }

  public static void Mark1() {         // NEARLY USELESS
    Timer t = new Timer();
    int count = 1_000_000;
    for (int i=0; i<count; i++) {
      double dummy = multiply(i);
    }
    double time = t.check() * 1e9 / count;
    System.out.printf("%6.1f ns%n", time);
  }

  public static double Mark2() {
    Timer t = new Timer();
    int count = 100_000_000;
    double dummy = 0.0;
    for (int i=0; i<count; i++) 
      dummy += multiply(i);
    double time = t.check() * 1e9 / count;
    System.out.printf("%6.1f ns%n", time);
    return dummy;
  }

  public static double Mark3() {
    int n = 10;
    int count = 100_000_000;
    double dummy = 0.0;
    for (int j=0; j<n; j++) {
      Timer t = new Timer();
      for (int i=0; i<count; i++) 
        dummy += multiply(i);
      double time = t.check() * 1e9 / count;
      System.out.printf("%6.1f ns%n", time);
    }
    return dummy / n;
  }

  public static double Mark4() {
    int n = 10;
    int count = 100_000_000;
    double dummy = 0.0;
    double st = 0.0, sst = 0.0;
    for (int j=0; j<n; j++) {
      Timer t = new Timer();
      for (int i=0; i<count; i++) 
        dummy += multiply(i);
      double time = t.check() * 1e9 / count;
      st += time; 
      sst += time * time;
    }
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%6.1f ns +/- %6.3f%n", mean, sdev);
    return dummy / n;
  }

  public static double Mark5() {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do {
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += multiply(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
	totalCount += count;
      }
      double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
      System.out.printf("%6.1f ns +/- %8.2f %10d%n", mean, sdev, count);
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    return dummy / totalCount;
  }

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
        double time = runningTime * 1e9 / count;
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
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
	totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8(String msg, String info, IntToDouble f, 
                             int n, double minTime) {
    int count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.call(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
	totalCount += count;
      }
    } while (runningTime < minTime && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %s%15.1f ns %10.2f %10d%n", msg, info, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8(String msg, IntToDouble f) {
    return Mark8(msg, "", f, 10, 0.25);
  }

  public static double Mark8(String msg, String info, IntToDouble f) {
    return Mark8(msg, info, f, 10, 0.25);
  }

  public static double Mark8Setup(String msg, String info, Benchmarkable f, 
                                  int n, double minTime) {
    int count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) {
          t.pause();
          f.setup();
          t.play();
          dummy += f.call(i);
        }
        runningTime = t.check();
        double time = runningTime * 1e9 / count;
        st += time; 
        sst += time * time;
	totalCount += count;
      }
    } while (runningTime < minTime && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %s%15.1f ns %10.2f %10d%n", msg, info, mean, sdev, count);
    return dummy / totalCount;
  }

  public static double Mark8Setup(String msg, Benchmarkable f) {
    return Mark8Setup(msg, "", f, 10, 0.25);
  }

  public static double Mark8Setup(String msg, String info, Benchmarkable f) {
    return Mark8Setup(msg, info, f, 10, 0.25);
  }
}

// vim: noai:ts=4:sw=4:sts=4:et
