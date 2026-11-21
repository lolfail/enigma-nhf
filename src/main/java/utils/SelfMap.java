package utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.PairMap;

/**
 * A generic utility class to implement a mapping, in which values map to themselves.
 * This is useful to implement plugboard-like logic, where a number of elements may be associated together exclusively.
 * The exclusive part is important, this means that: if a is mapped to b, b can not be mapped to c.
 *
 * The class supports (during construction only) "identity" mappings, where the user can map values to themselves.
 * If disabled, no value can map to itself.
 */
public class SelfMap<T> implements Map<T, T> {
  private PairMap<T, T> pairings;

  // mappings such as (a, a) or (b, b)
  private boolean identityMappings = true;

  public SelfMap() {
    this.pairings = new PairMap<>();
    // this(true);
  }
  public SelfMap(Map<T,T> map) {
    this.pairings = new PairMap<>(map);
  }
  public SelfMap(boolean identityMappings, Map<T,T> map) {
    this.identityMappings = identityMappings;
    this.pairings = new PairMap<>(map);
  }
  public SelfMap(boolean identityMappings) {
    this.identityMappings = identityMappings;
    this.pairings = new PairMap<>();
  }

  /**
   * Get whether {@link #identityMappings} are allowed.
   * @return Whether the object was constructed with allowing identity mappings.
   *
   * @implNote This acts as a getter. There is no setter, because this is too much of a core property to change on the fly.
   */
  public boolean areIdentityMappingsAllowed() {
    return identityMappings;
  }

  /**
   * Get whether there are no mappings.
   * @return True if there are no mappings.
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
   * @return The number (count) of mappings / associations.
   *
   * Will count mappings to self <=> identity mappings.
   */
  public int size() {
    return pairings.size();
  }

  /**
   * Add a new mapping between the two parameters.
   * @param a Parameter of the first (as in genericity order) type.
   * @param b Parameter of the second (as in genericity order) type.
   * @return the second parameter's overwritten value, otherwise null
   *
   * If {@link #identityMappings} is set, it will allow (a - a), self mappings.
   * Otherwise for such, it will shortcircuit and return null.
   */
  public T put(T a, T b) {
    if (!identityMappings && a.equals(b))
      return null; // shortcircuit
    else
      return pairings.put(a, b);
  }

  /**
   * Gets the pair of whatever is mapped to the parameter, or null.
   * @param t An Object who may or may not be in the store.
   * @return The value associated with the parameter, or null if not found.
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
   * Deletes the pair, who contains the parameter, and returns the value mapped to it.
   * @param t Who to search for in the store.
   * @return The pair of whoever the parameter was mapped to, or null if not mapped.
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
   * @return True if there exists a mapping to the parameter.
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

  /**
   * Calls the underlying {@link PairMap}'s entrySet method.
   */
  public Set<Map.Entry<T,T>> entrySet() {
    return pairings.entrySet();
  }

  /**
   * Returns a set of all mapped things.
   * @return A set of all keys, that are mapped.
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
   * @param map The map to use as the source.
   */
  public void putAll(Map<? extends T, ? extends T> map) {
    pairings.putAll(map);
  }
}
