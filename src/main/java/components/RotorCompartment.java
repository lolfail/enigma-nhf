package components;

import components.Rotor;

import utils.SelfMap;

import java.util.List;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;

import validators.Validator;
import validators.ReflectorValidator;
import validators.RotorValidator;

import java.util.ArrayList;

/**
 * This tries to model the Rotor Stack in the Enigma Machine.
 * Is not called Rotor Stack, because having something named Stack implement List and not function as a stack would be misleading.
 * Although documentation may refer to this as "stack".
 *
 * Provides methods on top to handle special stuff like resetting all rotors, and getting/setting the reflector.
 * However it implements List<Rotor>, through interfaces which {@link Rotor}s may be added.
 *
 * @implNote As each Rotor has a nextRotor property that essentially makes this a chained list, we could use that to traverse...
 * @implNote Will set each nextRotor appropriately, that is the reason not every (but most) methods called are delegated onto the underlying {@link #rotors} list.
 */
public class RotorCompartment implements List<Rotor> {
  private List<Rotor> rotors;
  private SelfMap<Integer> reflector;
  private int numContactPoints;

  private Validator<Rotor> rotorValidator;
  private Validator<SelfMap<Integer>> reflectorValidator;

  public RotorCompartment(int numContactPoints) {
    this.numContactPoints = numContactPoints;
    this.rotors = new ArrayList<>();

    this.rotorValidator = new RotorValidator(this.numContactPoints);
    this.reflectorValidator = new ReflectorValidator(this.numContactPoints);

    this.reflector = new SelfMap<>(false);
  }

  /**
   * Set the reflector.
   * @param The mapping to set it to.
   *
   * @implNote Will validate it, see {@link ReflectorValidator}.
   */
  public void setReflector(SelfMap<Integer> reflector) {
    reflectorValidator.validate(reflector);

    this.reflector = reflector;
  }

  /**
   * Get the reflector that was previously set.
   * @return The {@link SelfMap} that was previously set.
   */
  public SelfMap<Integer> getReflector() {
    return this.reflector;
  }

  /**
   * Give to the stack a pin number, and get what pin "lights up" as it passes through at the end.
   * @param pinNum The pin on the entrance where the signal is meant to pass in.
   * @return The pin on the entrance where the signal exits.
   */
  public int passThrough(int pinNum) {
    rotors.getFirst().rotate();

    int buffer = pinNum;
    // forward pass
    for (Rotor rotor : rotors)
      buffer = rotor.forwardPass(buffer);
    // reflector
    buffer = reflector.get(buffer);
    // reverse pass
    for (Rotor rotor : rotors.reversed())
      buffer = rotor.reversePass(buffer);

    return buffer;
  }

  /**
   * Resets every single rotor in the stack.
   */
  public void reset() {
    for (Rotor rotor : rotors)
      rotor.reset();
  }

  // {{{ ATOMIC LIST OPERATIONS

  /**
   * Add to the stack at a certain index the rotor.
   * @param index The index within the stack to add the rotor to.
   * @param rotor The rotor to add.
   *
   * Will validate the rotor, see {@link RotorValidator}.
   * Also will chain the rotors together around it properly.
   */
  public void add(int index, Rotor rotor) {
    rotorValidator.validate(rotor);

    if (index > 0)
      rotors.get(index - 1).setNextRotor(rotor);
    rotors.add(index, rotor);
    if ((index + 1) < rotors.size())
      rotor.setNextRotor(rotors.get(index + 1));
  }

  /**
   * Update a rotor at a certain index.
   * @param index The index of the rotor to update.
   * @param rotor The new rotor to set index to.
   * @return The rotor that was updated.
   * 
   * Will validate the rotor, see {@link RotorValidator}.
   * Also will chain the rotors together around it properly.
   */
  public Rotor set(int index, Rotor rotor) {
    rotorValidator.validate(rotor);

    if (index > 0)
      rotors.get(index - 1).setNextRotor(rotor);
    Rotor previousRotor = rotors.set(index, rotor);
    rotor.setNextRotor(rotors.get(index + 1));
    return previousRotor;
  }

  /**
   * Remove a rotor at an index from the compartment.
   * @param index The index at which to remove the rotor.
   * @return The rotor that was removed.
   *
   * Will chain the rotors together around it properly.
   */
  public Rotor remove(int index) {
    if (0 < index && (index + 1) < rotors.size())
      rotors.get(index - 1).setNextRotor(rotors.get(index + 1));
    return rotors.remove(index);
  }

  /**
   * Only keep rotors that are in the parameter collection.
   * @param c The collection to use as a filter.
   * @return True if it was modified.
   *
   * Will chain the rotors together around it properly.
   * @implNote Because retained rotors may not be next to each other, can not eliminate the loop after it...
   */
  public boolean retainAll(Collection<?> c) {
    boolean modified = rotors.retainAll(c);
    // we have to rechain the retained rotors because elements could be anywhere
    for (int i = 0; i < rotors.size() - 1; i++)
      rotors.get(i).setNextRotor(rotors.get(i+1));
    return modified;
  }

  /// }}}
  // {{{ RECURSIVELY DEFINED INTERFACE METHODS

  /**
   * Get the number of rotors this stack contains.
   * @return The number of rotors contained.
   */
  public int size() {
    return rotors.size();
  }

  /**
   * Get whether the rotor stack is empty.
   * @return True if it is empty.
   */
  public boolean isEmpty() {
    return rotors.isEmpty();
  }

  /**
   * Remove every single rotor.
   */
  public void clear() {
    rotors.clear();
  }

  /**
   * Get a single rotor by index.
   * @param index The rotor by index to get.
   * @return The rotor that is at that index.
   */
  public Rotor get(int index) {
    return rotors.get(index);
  }

  /**
   * Get whether the compartment contains the parameter.
   * @param o An Object to check for.
   * @return True, if it contains it.
   */
  public boolean contains(Object o) {
    return rotors.contains(o);
  }

  /**
   * Check whether it contains every single item of the parameter.
   * @param c A collection to check through.
   * @return True if it contains every single item.
   */
  public boolean containsAll(Collection<?> c) {
    for (Object e : c)
      if (!this.contains(e))
        return false;
    return true;
  }

  /**
   * Get the index of the parameter of first occurrance.
   * @param o The Object to get the index of.
   * @return The index of it, if it contains it, otherwise -1.
   */
  public int indexOf(Object o) {
    return rotors.indexOf(o);
  }

  /**
   * Same as {@link #indexOf(Object)} but of the last occurrance.
   */
  public int lastIndexOf(Object o) {
    return rotors.lastIndexOf(o);
  }

  /**
   * Same as {@link #add(int, Rotor)} but the index as the size of the list.
   */
  public boolean add(Rotor rotor) {
    this.add(rotors.size(), rotor);
    return true;
  }

  /**
   * Same as {@link #add(int, Rotor)} looped through with increasing the index parameter on every iteration.
   */
  public boolean addAll(int index, Collection <? extends Rotor> c) {
    for (Rotor e : c)
      this.add(index++, e);
    return true;
  }

  /**
   * Same as {@link #addAll(int, Collection)} but with index the size of the list.
   */
  public boolean addAll(Collection<? extends Rotor> c) {
    return this.addAll(this.size(), c);
  }

  /**
   * Remove the rotor matching the parameter from the list.
   * @param rotor The rotor to look for.
   * @return True if anything was removed.
   */
  public boolean remove(Object rotor) {
    int index = this.indexOf(rotor);
    if (index == -1)
      return false;
    this.remove(index);
    return true;
  }

  /**
   * Same as {@link #remove(Object)} looped through the collection in the parameter.
   */
  public boolean removeAll(Collection<?> c) {
    boolean modified = false;
    for (Object e : c)
      modified |= this.remove(e);
    return modified;
  }

  /**
   * Yield an iterator on Rotors contained.
   * @return An iterator on the {@link Rotor}.
   */
  public Iterator<Rotor> iterator() {
    return rotors.iterator();
  }

  /**
   * Yield a ListIterator on Rotors contained.
   * @return A ListIterator on the {@link Rotor}.
   */
  public ListIterator<Rotor> listIterator() {
    return rotors.listIterator();
  }

  /**
   * Yield a ListIterator on Rotors contained.
   * @return A ListIterator on the {@link Rotor}.
   */
  public ListIterator<Rotor> listIterator(int index) {
    return rotors.listIterator(index);
  }

  /**
   * Yield a reference array.
   * @return The array.
   */
  public Object[] toArray() {
    return rotors.toArray();
  }

  /**
   * Yield a reference array but strongly typed with the parameter as the reference.
   * @return The array strongly typed.
   */
  public <T> T[] toArray(T[] a) {
    return rotors.toArray(a);
  }

  /**
   * Yield a list "view" of the Rotors between the arguments.
   * @return The sublist.
   */
  public List<Rotor> subList(int fromIndex, int toIndex) {
    return rotors.subList(fromIndex, toIndex);
  }

  // }}}
}
