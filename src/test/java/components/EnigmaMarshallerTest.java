package components;

import components.EnigmaMachine;
import components.EnigmaMarshaller;

import java.util.Map;
import utils.PairMap;
import utils.SelfMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

class EnigmaMarshallerTest {
  private EnigmaMarshaller marshaller;
  private EnigmaMachine enigmaMachine;
  private int numContactPoints;

  static private EnigmaMachine constructBasicEnigma(int numContactPoints) {
    EnigmaMachine enigma = new EnigmaMachine(numContactPoints);
    enigma = new EnigmaMachine(numContactPoints);

    enigma.setReflector(new SelfMap<>(false, Map.of(0,1)));
    enigma.setPlugboard(new SelfMap<>(true, Map.of(0,0, 1,1)));
    enigma.setNumRotors(1);
    enigma.setWiring(0, new PairMap<>(Map.of(0,1, 1,0)));

    return enigma;
  }
  
  @BeforeEach 
  void init() {
    this.marshaller = new EnigmaMarshaller(true);
    this.numContactPoints = 2;
    this.enigmaMachine = constructBasicEnigma(this.numContactPoints);
  }

  private void assertEqualsMachine(EnigmaMachine expected, EnigmaMachine actual) {
    assertEquals(expected.getNumContactPoints(), actual.getNumContactPoints());

    assertEquals(expected.getPlugboard().entrySet(), actual.getPlugboard().entrySet());
    assertEquals(expected.getReflector().entrySet(), actual.getReflector().entrySet());

    assertEquals(expected.getNumRotors(), actual.getNumRotors());
    for (int i = 0; i < expected.getNumRotors(); i++)
      assertEquals(expected.getWiring(i).entrySet(), actual.getWiring(i).entrySet());
  }

  // {{{ BASIC MARSHAL UNMARSHAL PERSISTANCE TESTS
  @Test 
  void preservedUntouchedOriginal() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    marshaller.write(os, enigmaMachine);
    assertEqualsMachine(constructBasicEnigma(this.numContactPoints), enigmaMachine);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    assertEqualsMachine(enigmaMachine, marshaller.read(is));
  }
  @Test 
  void preservedUntouchedOriginalZeroIndexed() throws Exception {
    this.marshaller = new EnigmaMarshaller(false);
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    marshaller.write(os, enigmaMachine);
    assertEqualsMachine(constructBasicEnigma(this.numContactPoints), enigmaMachine);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    assertEqualsMachine(enigmaMachine, marshaller.read(is));
  }
  @Test 
  void preservedUntouchedOriginalDefault() throws Exception {
    this.marshaller = new EnigmaMarshaller();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    marshaller.write(os, enigmaMachine);
    assertEqualsMachine(constructBasicEnigma(this.numContactPoints), enigmaMachine);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    assertEqualsMachine(enigmaMachine, marshaller.read(is));
  }
  // }}}

  // {{{ ERRORS
  @Test 
  void misnamedDocRoot() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><FakeEnigmaMachine alphabet=\"2\"/>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }

  @Test 
  void missingReflector() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"2\"><Plugboard/><Rotors/></EnigmaMachine>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }
  @Test 
  void missingRotors() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"2\"><Plugboard/><Reflector><Cable from=\"1\" to=\"2\"/></Reflector></EnigmaMachine>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }
  @Test 
  void missingPlugboard() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"2\"><Plugboard/><Rotors/></EnigmaMachine>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }

  @Test 
  void moreThanOnePlugboard() {
    // this hits the same path as rotors and reflector, so those are assumed to work as well if this works
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"2\"><Plugboard/><Plugboard/></EnigmaMachine>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }

  @Test 
  void numberParseError() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"2\"><Plugboard/><Reflector><Cable from=\"1k\" to=\"2c\"/></Reflector></EnigmaMachine>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }

  @Test 
  void missingAttribute() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine/>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }
  @Test 
  void blankAttribute() {
    String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EnigmaMachine alphabet=\"\"/>";
    ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8));
    assertThrows(EnigmaMarshaller.CorruptedBackupError.class, () -> {
      marshaller.read(is);
    });
  }
  // }}}
}
