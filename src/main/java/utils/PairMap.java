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

  public B put(A a, B b) {
    B returned = forwardStore.put(a, b);
    reverseStore.put(b, a);
    return returned;
    // TODO! check if both can be placed otherwise fail
  }
  public A putReverse(B b, A a) {
    forwardStore.put(a, b);
    A returned = reverseStore.put(b, a);
    return returned;
    // TODO! check if both can be placed otherwise fail
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
    if (!hadEntry)
      throw new RuntimeException(); // should have had the same thing but reversed
    return removed;
  }
  public A removeReverse(Object b) {
    A removed = reverseStore.remove(b);
    boolean hadEntry = forwardStore.remove(removed, b);
    if (!hadEntry)
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
  public Set<A> valuesReversed() {
    return forwardStore.keySet();
  }

  public void putAll(Map<? extends A, ? extends B> map) {
    forwardStore.putAll(map);
    for (Map.Entry<? extends A, ? extends B> entry : map.entrySet()) {
      reverseStore.put(entry.getValue(), entry.getKey());
    }
  }
  public void putAllReverse(Map<? extends B, ? extends A> map) {
    reverseStore.putAll(map);
    for (Map.Entry<? extends B, ? extends A> entry : map.entrySet()) {
      forwardStore.put(entry.getValue(), entry.getKey());
    }
  }
}
