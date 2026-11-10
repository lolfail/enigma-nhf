package components;

import components.Rotor;

import utils.SelfMap;

import java.util.List;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;

import java.util.ArrayList;

public class RotorCompartment implements List<Rotor> {
  private List<Rotor> rotors;
  private SelfMap<Integer> reflector;
  private int numContactPoints;

  public RotorCompartment(int numContactPoints) {
    this.numContactPoints = numContactPoints;
    this.rotors = new ArrayList<>();
  }

  public void setReflector(SelfMap<Integer> reflector) {
    this.reflector = reflector;
  }
  public SelfMap<Integer> getReflector() {
    return this.reflector;
  }

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

  public void reset() {
    for (Rotor rotor : rotors)
      rotor.reset();
  }

  // {{{ ATOMIC LIST OPERATIONS

  public void add(int index, Rotor rotor) {
    // TODO! check for rotor numContactPoints != the one specified

    if (index > 0)
      rotors.get(index - 1).setNextRotor(rotor);
    rotors.add(index, rotor);
    if ((index + 1) < rotors.size())
      rotor.setNextRotor(rotors.get(index + 1));
  }

  public Rotor set(int index, Rotor rotor) {
    // TODO! check for rotor numContactPoints != the one specified

    if (index > 0)
      rotors.get(index - 1).setNextRotor(rotor);
    Rotor previousRotor = rotors.set(index, rotor);
    rotor.setNextRotor(rotors.get(index + 1));
    return previousRotor;
  }

  public Rotor remove(int index) {
    if (0 < index && (index + 1) < rotors.size())
      rotors.get(index - 1).setNextRotor(rotors.get(index + 1));
    return rotors.remove(index);
  }

  public boolean retainAll(Collection<?> c) {
    boolean modified = rotors.retainAll(c);
    // we have to rechain the retained rotors because elements could be anywhere
    for (int i = 0; i < rotors.size() - 1; i++)
      rotors.get(i).setNextRotor(rotors.get(i+1));
    return modified;
  }

  /// }}}
  // {{{ RECURSIVELY DEFINED INTERFACE METHODS

  public int size() {
    return rotors.size();
  }

  public boolean isEmpty() {
    return rotors.isEmpty();
  }

  public void clear() {
    rotors.clear();
  }

  public Rotor get(int index) {
    return rotors.get(index);
  }

  public boolean contains(Object o) {
    return rotors.contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    for (Object e : c)
      if (!rotors.contains(e))
        return false;
    return true;
  }

  public int indexOf(Object o) {
    return rotors.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return rotors.lastIndexOf(o);
  }

  public boolean add(Rotor rotor) {
    this.add(rotors.size(), rotor);
    return true;
  }

  public boolean addAll(int index, Collection <? extends Rotor> c) {
    for (Rotor e : c)
      this.add(index++, e);
    return true;
  }

  public boolean addAll(Collection<? extends Rotor> c) {
    return this.addAll(this.size(), c);
  }

  public boolean remove(Object rotor) {
    int index = this.indexOf(rotor);
    if (index == -1)
      return false;
    this.remove(index);
    return true;
  }

  public boolean removeAll(Collection<?> c) {
    boolean modified = false;
    for (Object e : c)
      modified |= this.remove(e);
    return modified;
  }

  public Iterator<Rotor> iterator() {
    return rotors.iterator();
  }

  public ListIterator<Rotor> listIterator() {
    return rotors.listIterator();
  }

  public ListIterator<Rotor> listIterator(int index) {
    return rotors.listIterator(index);
  }

  public Object[] toArray() {
    return rotors.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return rotors.toArray(a);
  }

  public List<Rotor> subList(int fromIndex, int toIndex) {
    return rotors.subList(fromIndex, toIndex);
  }

  // }}}
}
