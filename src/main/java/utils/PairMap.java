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

  /**
   * Get whether there are no mappings.
   */
  public boolean isEmpty() {
    boolean forwardIsEmpty = forwardStore.isEmpty();
    boolean reverseIsEmpty = reverseStore.isEmpty();
    if (forwardIsEmpty != reverseIsEmpty)
      throw new RuntimeException(); // this should not be possible
    return forwardIsEmpty && reverseIsEmpty;
  }

  /**
   * Erase every mapping.
   */
  public void clear() {
    forwardStore.clear();
    reverseStore.clear();
  }

  /**
   * Get the number of mappings.
   */
  public int size() {
    int forwardSize = forwardStore.size();
    int reverseSize = reverseStore.size();
    if (forwardSize != reverseSize)
      throw new RuntimeException(); // should not be possible
    return forwardSize;
  }

  /**
   * Add a new mapping between the two parameters.
   * @return the second parameter's overwritten value, otherwise null
   *
   * This is the defining feature of PairMap, both a and b shall be keys.
   * So it will overwrite the existing mapping where any of the parameters match.
   */
  public B put(A a, B b) {
    B overwritten = forwardStore.put(a, b);

    forwardStore.remove(reverseStore.get(b));
    if (overwritten != null)
      reverseStore.remove(overwritten);

    reverseStore.put(b, a);
    return overwritten;
  }

  /**
   * Same as {@link #put(Object, Object)} but the parameters are reversed.
   */
  public A putReverse(B b, A a) {
    A overwritten = reverseStore.put(b, a);

    reverseStore.remove(forwardStore.get(a));
    if (overwritten != null)
      forwardStore.remove(overwritten);

    forwardStore.put(a, b);
    return overwritten;
  }

  /**
   * Get what is mapped to the parameter of the first type.
   */
  public B get(Object a) {
    return forwardStore.get(a);
  }

  /**
   * Same as {@link #get(Object)} but with parameter of the second type.
   */
  public A getReverse(Object b) {
    return reverseStore.get(b);
  }

  /**
   * Remove an element and get it back.
   * @param a The element of the first type to search for.
   * @return The now removed element.
   */
  public B remove(Object a) {
    B removed = forwardStore.remove(a);
    boolean hadEntry = reverseStore.remove(removed, a);
    if (removed != null && !hadEntry)
      throw new RuntimeException(); // should have had the same thing but reversed
    return removed;
  }

  /**
   * Same as {@link #remove(Object)} but for parameter of second type.
   */
  public A removeReverse(Object b) {
    A removed = reverseStore.remove(b);
    boolean hadEntry = forwardStore.remove(removed, b);
    if (removed != null && !hadEntry)
      throw new RuntimeException(); // should have had the same thing but reversed
    return removed;
  }

  /**
   * Check whether it contains a key of first type.
   */
  public boolean containsKey(Object key) {
    return forwardStore.containsKey(key);
  }

  /**
   * Check whether it contains a key of second type.
   */
  public boolean containsValue(Object value) {
    return reverseStore.containsKey(value);
  }

  /**
   * Check whether it contains a key of second type.
   */
  public boolean containsKeyReverse(Object key) {
    return reverseStore.containsKey(key);
  }

  /**
   * Check whether it contains a key of first type.
   */
  public boolean containsValueReverse(Object value) {
    return forwardStore.containsKey(value);
  }

  /**
   * Yield a set of entries in the map.
   */
  public Set<Map.Entry<A,B>> entrySet() {
    return forwardStore.entrySet();
  }

  /**
   * Same as {@link #entrySet()} but elements of the set are reversed.
   */
  public Set<Map.Entry<B,A>> entrySetReverse() {
    return reverseStore.entrySet();
  }

  /**
   * Yield the set of keys of first type.
   */
  public Set<A> keySet() {
    return forwardStore.keySet();
  }

  /**
   * Yield the set of keys of the second type.
   */
  public Set<B> keySetReverse() {
    return reverseStore.keySet();
  }

  /**
   * Same as {@link #keySetReverse()}
   */
  public Set<B> values() {
    return reverseStore.keySet();
  }

  /**
   * Same as {@link #keySet()}
   */
  public Set<A> valuesReverse() {
    return forwardStore.keySet();
  }

  /**
   * Put all provided entries into self.
   * Utilizes {@link #put(Object, Object)}, will overwrite!
   */
  public void putAll(Map<? extends A, ? extends B> map) {
    for (Map.Entry<? extends A, ? extends B> entry : map.entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Same as {@link #putAll(Map)} but provided map's entries are reversed.
   */
  public void putAllReverse(Map<? extends B, ? extends A> map) {
    for (Map.Entry<? extends B, ? extends A> entry : map.entrySet()) {
      this.putReverse(entry.getKey(), entry.getValue());
    }
  }
}
