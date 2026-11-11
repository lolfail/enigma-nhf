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
    // this(true);
  }
  public SelfMap(boolean identityMappings) {
    this.identityMappings = identityMappings;
    this.pairings = new PairMap<>();
  }

  /**
   * Get whether {@link #identityMappings} are allowed.
   *
   * @implNote This acts as a getter. There is no setter, because this is too much of a core property.
   */
  public boolean areIdentityMappingsAllowed() {
    return identityMappings;
  }

  /**
   * Get whether there are no mappings.
   */
  public boolean isEmpty() {
    return pairings.isEmpty();
  }

  /**
   * Erase every mapping.
   */
  public void clear() {
    pairings.clear();
  }

  /**
   * Get the number of mappings.
   */
  public int size() {
    return pairings.size();
  }

  /**
   * Add a new mapping between the two parameters.
   * @return the second parameter's overwritten value, otherwise null
   *
   * If {@link #identityMappings} is set, it will allow (a - a), self mappings.
   * Otherwise for such, it will shortcircuit and return null.
   */
  public T put(T a, T b) {
    if (!identityMappings && a.equals(b))
      return null; // shortcircuit -- exception should be preferred...
    else
      return pairings.put(a, b);
  }

  /**
   * Gets the pair of whatever is mapped to the parameter, or null.
   */
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

  /**
   * Deletes the pair, who contains the parameter.
   */
  public T remove(Object t) {
    T forward = pairings.remove(t);
    T reverse = pairings.removeReverse(t);

    if      (forward == null && reverse == null) return null;
    else if (forward == null && reverse != null) return reverse;
    else if (forward != null && reverse == null) return forward;
    else throw new RuntimeException(); // removing one should have removed the other
  }

  /**
   * Returns whether the parameter is mapped to anything.
   */
  public boolean containsKey(Object key) {
    T got = this.get(key);
    return got != null;
  }

  /**
   * Same as {@link #containsKey(Object)}.
   */
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

  /**
   * Returns a set of all mapped things.
   */
  public Set<T> keySet() {
    Set<T> set = new HashSet<>();
    set.addAll(pairings.keySet());
    set.addAll(pairings.keySetReverse());
    return set;
  }

  /**
   * Same as {@link #keySet()}.
   */
  public Set<T> values() {
    return this.keySet();
  }

  /**
   * Put everything from the parameter into self.
   */
  public void putAll(Map<? extends T, ? extends T> map) {
    pairings.putAll(map);
  }
}
