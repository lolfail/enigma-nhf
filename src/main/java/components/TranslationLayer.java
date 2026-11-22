package components;

import utils.PairMap;
import utils.SelfMap;

/**
 * Helper class to handle translation between {@link EnigmaMachine} that operates on Integers,
 * and the graphical stuff that works based on Characters.
 *
 * @implNote Could be generic (for the container types), but for simplicity and because neither is {@link EnigmaMachine} (as well as graphical stuff), it is not.
 * @implNote Could operate on interfaces of Map, but some tricks would be needed to be able to use getReverse() of PairMap for example.
 */
public class TranslationLayer {
  static private PairMap<Character, Integer> defaultTranslation = new PairMap<>();
  // prefill with default translation: a - 0, b - 1, ... , z - 25
  static {
    int letterCount = 'z' - 'a';
    for (int i = 0; i <= letterCount; i++) {
      char letter = (char)('a' + i);
      defaultTranslation.put(letter, i);
    }
  }

  private PairMap<Character, Integer> translator;

  public TranslationLayer(PairMap<Character, Integer> translation) {
    this.translator = translation;
  }

  public TranslationLayer() {
    this.translator = defaultTranslation;
  }

  /**
   * Yield the mapping contained within the class.
   * @return The translation mapping used by this class as well.
   *
   * Note that there is no setter, it is set at construction time.
   * This is because the class does it's job currently, and there was no need for setting it dynamically.
   */
  public PairMap<Character, Integer> getDictionary() {
    return translator;
  }

  /**
   * Translate a character to an int.
   * @param ch The char to translate.
   * @return The translated, now Integer. Null if no mapping exists.
   *
   * @implNote Returns Integer not int: if it was not like so, upon trying to return, null conversion would raise an error.
   */
  public Integer charToInt(char ch) {
    return translator.get(ch);
  }

  /**
   * See {@link #charToInt(char)}. Reverse direction.
   */
  public Character intToChar(int i) {
    return translator.getReverse(i);
  }

  /**
   * Translate an entire {@link PairMap} of Character mappings to Integer mappings.
   * @param charMap The mapping of chars to translate.
   * @return A new PairMap of Integers, now translated.
   */
  public PairMap<Integer, Integer> charPairMapToIntPairMap(PairMap<Character, Character> charMap) {
    PairMap<Integer, Integer> intMap = new PairMap<>();

    for (char charKey : charMap.keySet()) {
      char charValue = charMap.get(charKey);

      int intKey = translator.get(charKey);
      int intValue = translator.get(charValue);

      intMap.put(intKey, intValue);
    }

    return intMap;
  }

  /**
   * See {@link #charPairMapToIntPairMap(PairMap)}. Reverse direction.
   */
  public PairMap<Character, Character> intPairMapToCharPairMap(PairMap<Integer, Integer> intMap) {
    PairMap<Character, Character> charMap = new PairMap<>();

    for (int intKey : intMap.keySet()) {
      int intValue = intMap.get(intKey);

      char charKey = translator.getReverse(intKey);
      char charValue = translator.getReverse(intValue);

      charMap.put(charKey, charValue);
    }

    return charMap;
  }

  /**
   * Same as {@link #charPairMapToIntPairMap(PairMap)} but for SelfMaps.
   * Toggles identity mappings flag to match that of the parameter.
   */
  public SelfMap<Integer> charSelfMapToIntSelfMap(SelfMap<Character> charMap) {
    boolean identityMappings = charMap.areIdentityMappingsAllowed();
    SelfMap<Integer> intMap = new SelfMap<>(identityMappings);

    // this will unnecessarily go through the second half
    // since a SelfMap is double-keyed, and maps to self
    for (char mapKey : charMap.keySet()) {
      char mapValue = charMap.get(mapKey);

      int intMapKey = translator.get(mapKey);
      int intMapValue = translator.get(mapValue);

      intMap.put(intMapKey, intMapValue);
    }

    return intMap;
  }

  /**
   * Same as {@link #intPairMapToCharPairMap(PairMap)} but for SelfMaps.
   * Toggles identity mappings flag to match that of the parameter.
   */
  public SelfMap<Character> intSelfMapToCharSelfMap(SelfMap<Integer> intMap) {
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
}
