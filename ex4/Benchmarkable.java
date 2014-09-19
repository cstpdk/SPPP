public abstract class Benchmarkable implements IntToDouble {
  public void setup() { }
  public abstract double call(int i);
}
