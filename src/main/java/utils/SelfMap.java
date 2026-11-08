package utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.PairMap;

public class SelfMap<T> implements Map<T, T> {
  private PairMap<T, T> pairings;

  // mappings such as (a, a) or (b, b)
  private boolean identityMappings = true;

  public SelfMap() {
    this.pairings = new PairMap<>();
  }
  public SelfMap(boolean identityMappings) {
    this.identityMappings = identityMappings;
    this.pairings = new PairMap<>();
  }

  public boolean isEmpty() {
    return pairings.isEmpty();
  }

  public void clear() {
    pairings.clear();
  }

  public int size() {
    return pairings.size();
  }

  public T put(T a, T b) {
    if (!identityMappings && a.equals(b))
      return null; // shortcircuit -- exception should be preferred...
    else
      return pairings.put(a, b);
  }

  public T get(Object t) {
    T forward = pairings.get(t);
    T reverse = pairings.getReverse(t);

    if      (forward == null && reverse == null) return null;
    else if (forward == null && reverse != null) return reverse;
    else if (forward != null && reverse == null) return forward;
    else {
      if (identityMappings) return forward;
      else throw new RuntimeException(); // identityMappings should be off!
    }
  }

  public T remove(Object t) {
    T forward = pairings.remove(t);
    T reverse = pairings.removeReverse(t);

    if      (forward == null && reverse == null) return null;
    else if (forward == null && reverse != null) return reverse;
    else if (forward != null && reverse == null) return forward;
    else throw new RuntimeException(); // removing one should have removed the other
  }

  public boolean containsKey(Object key) {
    T got = this.get(key);
    return got != null;
  }
  public boolean containsValue(Object value) {
    T got = this.get(value);
    return got != null;
  }

  public Set<Map.Entry<T,T>> entrySet() {
    return null;
    // TODO!
    // this could do a couple of nice things:
    //   - call underlying pairings.entrySet() (utilizes forwards hashmap)
    //   - make a new set where every pair has some nice property (like a < b)
  }

  public Set<T> keySet() {
    Set<T> set = new HashSet<>();
    set.addAll(pairings.keySet());
    set.addAll(pairings.keySetReverse());
    return set;
  }

  public Set<T> values() {
    return this.keySet();
  }

  public void putAll(Map<? extends T, ? extends T> map) {
    pairings.putAll(map);
  }
}
