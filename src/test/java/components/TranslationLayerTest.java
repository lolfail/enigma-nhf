package components;

import components.TranslationLayer;
import utils.PairMap;
import utils.SelfMap;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class TranslationLayerTest {
  private TranslationLayer translationLayer;

  @BeforeEach 
  void init() {
    this.translationLayer = new TranslationLayer();
  }

  // {{{ CONSTRUCTOR TESTS
  @Test 
  void defaultsToEnumeratedAlphabet() {
    for (Map.Entry<Character, Integer> entry : translationLayer.getDictionary().entrySet()) {
      char ch = entry.getKey();
      int i = entry.getValue();

      assertEquals((char)('a')+i, ch);
    }
  }

  @Test 
  void takesCustomMapping() {
    PairMap<Character, Integer> mapping = new PairMap<>(Map.of('a',-1, 'b',-2));
    translationLayer = new TranslationLayer(mapping);
    assertEquals(mapping.entrySet(), translationLayer.getDictionary().entrySet());
  }
  // }}}

  // {{{ SINGULAR TRANSLATION
  @Test 
  void charToInt() {
    int i = 0;
    for (char letter = 'a'; letter <= 'z'; letter++) {
      assertEquals(i++, translationLayer.charToInt(letter));
    }
  }

  @Test 
  void intToChar() {
    char letter = 'a';
    for (int i = 0; i < 26; i++) {
      assertEquals(letter++, translationLayer.intToChar(i));
    }
  }
  // }}}

  private void fillExpectationIntToChar(Map<Integer, Integer> inMap, Map<Character, Character> outMap) {
    for (Integer key : inMap.keySet()) {
      Integer value = inMap.get(key);
      outMap.put(translationLayer.intToChar(key), translationLayer.intToChar(value));
    }
  }
  private void fillExpectationCharToInt(Map<Character, Character> inMap, Map<Integer, Integer> outMap) {
    for (Character key : inMap.keySet()) {
      Character value = inMap.get(key);
      outMap.put(translationLayer.charToInt(key), translationLayer.charToInt(value));
    }
  }

  // {{{ SELFMAP TRANSLATION
  @Test 
  void intSelfMapToCharSelfMap() {
    SelfMap<Integer> src = new SelfMap<>(false, Map.of(0,1, 2,3));
    SelfMap<Character> dst = new SelfMap<>(false);
    fillExpectationIntToChar(src, dst);
    assertEquals(dst.entrySet(), translationLayer.intSelfMapToCharSelfMap(src).entrySet());
  }

  @Test 
  void charSelfMapToIntCharMap() {
    SelfMap<Character> src = new SelfMap<>(false, Map.of('a','b', 'c','d'));
    SelfMap<Integer> dst = new SelfMap<>(false);
    fillExpectationCharToInt(src, dst);
    assertEquals(dst.entrySet(), translationLayer.charSelfMapToIntSelfMap(src).entrySet());
  }
  // }}}

  // {{{ PAIRMAP TRANSLATION
  @Test 
  void intPairMapToCharPairMap() {
    PairMap<Integer, Integer> src = new PairMap<>(Map.of(0,1, 2,3));
    PairMap<Character, Character> dst = new PairMap<>();
    fillExpectationIntToChar(src, dst);
    assertEquals(dst.entrySet(), translationLayer.intPairMapToCharPairMap(src).entrySet());
  }

  @Test 
  void charPairMapToIntCharMap() {
    PairMap<Character, Character> src = new PairMap<>(Map.of('a','b', 'c','d'));
    PairMap<Integer, Integer> dst = new PairMap<>();
    fillExpectationCharToInt(src, dst);
    assertEquals(dst.entrySet(), translationLayer.charPairMapToIntPairMap(src).entrySet());
  }
  // }}}
}
