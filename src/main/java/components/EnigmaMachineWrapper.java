package components;

import java.util.Map;

import components.EnigmaMachine;
import utils.PairMap;
import utils.SelfMap;

public class EnigmaMachineWrapper {
  static private PairMap<Character, Integer> defaultTranslation = new PairMap<>();
  static {
    int letterCount = 'z' - 'a';
    for (int i = 0; i < letterCount; i++) {
      char letter = (char)('a' + i);
      defaultTranslation.put(letter, i);
    }
  }

  private EnigmaMachine machine;
  private PairMap<Character, Integer> translator;

  public EnigmaMachineWrapper(int numLetters, PairMap<Character, Integer> translation) {
    this.machine = new EnigmaMachine(numLetters);
    this.translator = translation;
  }

  public EnigmaMachineWrapper() {
    this.machine = new EnigmaMachine(defaultTranslation.size());
    this.translator = defaultTranslation;
  }

  public int getNumContactPoints() {
    return machine.getNumContactPoints();
  }

  public char passThrough(char input) {
    // TODO! what if input is not mapped in translator or underlying machine?

    int in = translator.get(input);
    int out = machine.passThrough(in);
    return translator.getReverse(out);
  }

  public void setNumRotors(int num) {
    machine.setNumRotors(num);
  }
  public int getNumRotors() {
    return machine.getNumRotors();
  }

  public PairMap<Character, Character> getWiring(int rotorIndex) {
    PairMap<Integer, Integer> rawWiring = machine.getWiring(rotorIndex);
    PairMap<Character, Character> charWiring = new PairMap<>();

    for (int rawWireStart : rawWiring.keySet()) {
      int rawWireEnd = rawWiring.get(rawWireStart);

      char charWireStart = translator.getReverse(rawWireStart);
      char charWireEnd = translator.getReverse(rawWireEnd);

      charWiring.put(charWireStart, charWireEnd);
    }

    return charWiring;
  }
  public void setWiring(int rotorIndex, PairMap<Character, Character> charWiring) {
    PairMap<Integer, Integer> rawWiring = new PairMap<>();

    for (char charWireStart : charWiring.keySet()) {
      char charWireEnd = charWiring.get(charWireStart);

      int rawWireStart = translator.get(charWireStart);
      int rawWireEnd = translator.get(charWireEnd);

      rawWiring.put(rawWireStart, rawWireEnd);
    }

    machine.setWiring(rotorIndex, rawWiring);
  }

  public char getStartingPosition(int rotorIndex) {
    int startingPositionPinNum = machine.getStartingPosition(rotorIndex);
    return translator.getReverse(startingPositionPinNum);
  }

  public void setStartingPosition(int rotorIndex, char character) {
    int startingPositionPinNum = translator.get(character);
    machine.setStartingPosition(rotorIndex, startingPositionPinNum);
  }

  private SelfMap<Character> translateIntSelfMapToChar(SelfMap<Integer> intMap) {
    boolean identityMappings = intMap.areIdentityMappingsAllowed();
    SelfMap<Character> charMap = new SelfMap<>(identityMappings);

    // this will unnecessarily go through the second half
    // since a SelfMap is double-keyed, and maps to self
    for (int mapKey : intMap.keySet()) {
      int mapValue = intMap.get(mapKey);

      char charMapKey = translator.getReverse(mapKey);
      char charMapValue = translator.getReverse(mapValue);

      charMap.put(charMapKey, charMapValue);
    }

    return charMap;
  }

  private SelfMap<Integer> translateCharSelfMapToInt(SelfMap<Character> charMap) {
    boolean identityMappings = charMap.areIdentityMappingsAllowed();
    SelfMap<Integer> intMap = new SelfMap<>(identityMappings);

    // this will unnecessarily go through the second half
    // since a SelfMap is double-keyed, and maps to self
    for (char mapKey : charMap.keySet()) {
      char mapValue = charMap.get(mapKey);

      int intMapKey = translator.getReverse(mapKey);
      int intMapValue = translator.getReverse(mapValue);

      intMap.put(intMapKey, intMapValue);
    }

    return intMap;
  }

  public SelfMap<Character> getReflector() {
    return translateIntSelfMapToChar(machine.getReflector());
  }
  public void setReflector(SelfMap<Character> reflector) {
    machine.setReflector(translateCharSelfMapToInt(reflector));
  }

  public SelfMap<Character> getPlugboard() {
    return translateIntSelfMapToChar(machine.getPlugboard());
  }
  public void setPlugboard(SelfMap<Character> plugboard) {
    machine.setPlugboard(translateCharSelfMapToInt(plugboard));
  }
}
