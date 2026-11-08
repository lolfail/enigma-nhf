package utils;

import java.util.Map;
import java.util.Set;

import java.util.HashMap;

public class PairMap<A, B> implements Map<A,B> {
  private Map<A, B> forwardStore;
  private Map<B, A> reverseStore;

  public PairMap() {
    this.forwardStore = new HashMap<>();
    this.reverseStore = new HashMap<>();
  }

  public boolean isEmpty() {
    boolean forwardIsEmpty = forwardStore.isEmpty();
    boolean reverseIsEmpty = reverseStore.isEmpty();
    if (forwardIsEmpty != reverseIsEmpty)
      throw new RuntimeException(); // this should not be possible
    return forwardIsEmpty && reverseIsEmpty;
  }

  public void clear() {
    forwardStore.clear();
    reverseStore.clear();
  }

  public int size() {
    int forwardSize = forwardStore.size();
    int reverseSize = reverseStore.size();
    if (forwardSize != reverseSize)
      throw new RuntimeException(); // should not be possible
    return forwardSize;
  }

  // essential feature: double-keyed - both a and b must be unique
  public B put(A a, B b) {
    B overwritten = forwardStore.put(a, b);

    forwardStore.remove(reverseStore.get(b));
    if (overwritten != null)
      reverseStore.remove(overwritten);

    reverseStore.put(b, a);
    return overwritten;
  }
  public A putReverse(B b, A a) {
    A overwritten = reverseStore.put(b, a);

    reverseStore.remove(forwardStore.get(a));
    if (overwritten != null)
      forwardStore.remove(overwritten);

    forwardStore.put(a, b);
    return overwritten;
  }

  // precisely the reason we use a second map
  public B get(Object a) {
    return forwardStore.get(a);
  }
  public A getReverse(Object b) {
    return reverseStore.get(b);
  }

  public B remove(Object a) {
    B removed = forwardStore.remove(a);
    boolean hadEntry = reverseStore.remove(removed, a);
    if (removed != null && !hadEntry)
      throw new RuntimeException(); // should have had the same thing but reversed
    return removed;
  }
  public A removeReverse(Object b) {
    A removed = reverseStore.remove(b);
    boolean hadEntry = forwardStore.remove(removed, b);
    if (removed != null && !hadEntry)
      throw new RuntimeException(); // should have had the same thing but reversed
    return removed;
  }

  // containsValue is the same thing as the reverse's containsKey
  public boolean containsKey(Object key) {
    return forwardStore.containsKey(key);
  }
  public boolean containsValue(Object value) {
    return reverseStore.containsKey(value);
  }
  public boolean containsKeyReverse(Object key) {
    return reverseStore.containsKey(key);
  }
  public boolean containsValueReverse(Object value) {
    return forwardStore.containsKey(value);
  }

  public Set<Map.Entry<A,B>> entrySet() {
    return forwardStore.entrySet();
  }
  public Set<Map.Entry<B,A>> entrySetReverse() {
    return reverseStore.entrySet();
  }

  // values is the same thing as the reverse's keySet
  public Set<A> keySet() {
    return forwardStore.keySet();
  }
  public Set<B> keySetReverse() {
    return reverseStore.keySet();
  }
  public Set<B> values() {
    return reverseStore.keySet();
  }
  public Set<A> valuesReverse() {
    return forwardStore.keySet();
  }

  public void putAll(Map<? extends A, ? extends B> map) {
    for (Map.Entry<? extends A, ? extends B> entry : map.entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
  }
  public void putAllReverse(Map<? extends B, ? extends A> map) {
    for (Map.Entry<? extends B, ? extends A> entry : map.entrySet()) {
      this.putReverse(entry.getKey(), entry.getValue());
    }
  }
}
