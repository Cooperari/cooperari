package org.cooperari.sanity.aspectj;

@SuppressWarnings("javadoc")
public class Counter {
  private int value;
  public Counter(int value) {
    this.value = value;
  }
  public void inc() {
    value++;
  }
  public void dec() {
    value--;
  }
  public int get() {
    return value;
  }
}
