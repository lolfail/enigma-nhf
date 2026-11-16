package components;

import java.util.Map;

import utils.PairMap;
import utils.SelfMap;

public class TranslationLayer {
  static private PairMap<Character, Integer> defaultTranslation = new PairMap<>();
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

  public PairMap<Character, Integer> getDictionary() {
    return translator;
  }

  public int charToInt(char ch) {
    return translator.get(ch);
  }
  public char intToChar(int i) {
    return translator.getReverse(i);
  }

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

  public SelfMap<Integer> charSelfMapToIntCharMap(SelfMap<Character> charMap) {
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
}
