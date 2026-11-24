package components;

import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.OutputStream;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import utils.PairMap;
import utils.SelfMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Marshaller class for {@link EnigmaMachine} to XML.
 *
 * Construct once setting up 1 or 0 indexed, then pass as argument the object to be marshalled.
 * Returns the object if unmarshalled.
 */
public class EnigmaMarshaller {
  /**
   * Class signifying a corrupted XML state for various reasons.
   */
  static class CorruptedBackupError extends RuntimeException {
    public CorruptedBackupError(String message) {
      super(message);
    }

    public CorruptedBackupError(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private int indexDelta = 1; // via spec defaults to 1-indexed
  public EnigmaMarshaller(boolean isOneIndexed) {
    this.indexDelta = isOneIndexed ? 1 : 0;
  }
  public EnigmaMarshaller() {}

  // {{{ write XML
  /**
   * Helper to write to an element as children (with a specific name) entries in a map.
   * @param doc The document, used to create the child elements.
   * @param map The map to write out.
   * @param parent The parent element, whose children will be appended.
   * @param childName The element name to use for the child elements written.
   * @param skipSame Skip those values where the key is the same as the value.
   *
   * See {@link #putChildrenToMap(Map, Element, String)}, inverse of that.
   */
  private void appendMapChildren(Document doc, Map<Integer, Integer> map, Element parent, String childName, boolean skipSame) {
    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      int key = entry.getKey() + indexDelta;
      int value = entry.getValue() + indexDelta;
      if (skipSame && key == value)
        continue;

      Element child = doc.createElement(childName);
      child.setAttribute("from", Integer.toString(key, 10));
      child.setAttribute("to", Integer.toString(value, 10));
      parent.appendChild(child);
    }
  }

  /**
   * Marshal the machine to the stream provided.
   * @param ostream The stream to write the XML out to.
   * @param enigmaMachine The machine to marshal out to XML.
   */
  public void write(OutputStream ostream, EnigmaMachine enigmaMachine) throws Exception {
    Document doc = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .newDocument();

    Element machineRoot = doc.createElement("EnigmaMachine");
    String alphabetString = Integer.toString(enigmaMachine.getNumContactPoints());
    machineRoot.setAttribute("alphabet", alphabetString);
    doc.appendChild(machineRoot);

    Element plugboard = doc.createElement("Plugboard");
    appendMapChildren(doc, enigmaMachine.getPlugboard(), plugboard, "Cable", true);
    machineRoot.appendChild(plugboard);

    Element rotors = doc.createElement("Rotors");
    for (int i = 0; i < enigmaMachine.getNumRotors(); i++) {
      Element rotor = doc.createElement("Rotor");

      rotor.setAttribute("position", Integer.toString(i + indexDelta, 10));
      appendMapChildren(doc, enigmaMachine.getWiring(i), rotor, "Mapping", false);

      rotors.appendChild(rotor);
    }
    machineRoot.appendChild(rotors);

    Element reflector = doc.createElement("Reflector");
    appendMapChildren(doc, enigmaMachine.getReflector(), reflector, "Cable", false);
    machineRoot.appendChild(reflector);

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

    transformer.transform(new DOMSource(doc), new StreamResult(ostream));
  }
  // }}}

  // {{{ read XML
  /**
   * Parse an element's attribute name as an integer, and return the value, or throw.
   * @param element The element on whom the attribute is looked at.
   * @param attributeName The name of the attribute whose value to parse.
   * @return The parsed int.
   */
  private int requireAttributeParsed(Element element, String attributeName) {
    String attribute = element.getAttribute(attributeName);
    if (attribute.isBlank())
      throw new CorruptedBackupError("Missing " + attributeName + " on " + element.getTagName() + "!");

    try {
      return Integer.parseInt(attribute, 10);
    } catch (NumberFormatException e) {
      throw new CorruptedBackupError(
        "Invalid numeric value for " + attributeName + " on " + element.getTagName() + "!",
        e
      );
    }
  }

  /**
   * Require that exactly one element of a certain name exists on parent, or throw.
   * @param parent The parent whose children to inspect.
   * @param name The name to look for as children.
   */
  private Element requireExistanceUniquenessOfTagName(Element parent, String name) {
    // quite the mathematical name ^^^ :)
    NodeList tags = parent.getElementsByTagName(name);
    int numFound = tags.getLength();
    if (numFound <= 0)
      throw new CorruptedBackupError("Missing element: " + name + " on " + parent.getTagName() + "!");
    else if (numFound > 1)
      throw new CorruptedBackupError("Found more than 1 element (" + numFound + ") of " + name + " on " + parent.getTagName() + "!");

    return (Element) tags.item(0);
  }

  /**
   * Read the children of an element and put their from and to values into a map.
   * @param map The map to put parsed stuff into.
   * @param parent The parent element whose children to read.
   * @param childName The names of the children elements to read.
   *
   * See {@link #appendMapChildren(Document, Map, Element, String, boolean)}, inverse of that.
   */
  private void putChildrenToMap(Map<Integer, Integer> map, Element parent, String childName) {
    NodeList children = parent.getElementsByTagName(childName);
    for (int i = 0; i < children.getLength(); i++) {
      Element child = (Element) children.item(i);

      int from = requireAttributeParsed(child, "from");
      int to = requireAttributeParsed(child, "to");

      map.put(from - indexDelta, to - indexDelta);
    }
  }

  /**
   * Read from an XML format and parse it into the enigma machine.
   * @param istream The input stream to read the XML from.
   * @return The parsed XML written into an {@link EnigmaMachine}
   */
  public EnigmaMachine read(InputStream istream) throws Exception {
    Document doc = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(istream);
    doc.getDocumentElement().normalize();

    Element root = doc.getDocumentElement();

    String rootName = root.getTagName();
    if (!rootName.equals("EnigmaMachine"))
      throw new CorruptedBackupError("Document root is named differently than expected: " + rootName);

    int alphabet = requireAttributeParsed(root, "alphabet");
    EnigmaMachine enigmaMachine = new EnigmaMachine(alphabet);

    Element plugboard = requireExistanceUniquenessOfTagName(root, "Plugboard");
    SelfMap<Integer> plugboardMapping = new SelfMap<>(true);
    putChildrenToMap(plugboardMapping, plugboard, "Cable");
    enigmaMachine.setPlugboard(plugboardMapping);

    Element reflector = requireExistanceUniquenessOfTagName(root, "Reflector");
    SelfMap<Integer> reflectorMapping = new SelfMap<>(false);
    putChildrenToMap(reflectorMapping, reflector, "Cable");
    enigmaMachine.setReflector(reflectorMapping);

    Element rotors = requireExistanceUniquenessOfTagName(root, "Rotors");
    NodeList rotorChildren = rotors.getElementsByTagName("Rotor");
    int numRotors = rotorChildren.getLength();
    enigmaMachine.setNumRotors(numRotors);
    for (int i = 0; i < numRotors; i++) {
      Element rotor = (Element) rotorChildren.item(i);
      int position = requireAttributeParsed(rotor, "position");

      PairMap<Integer, Integer> wiring = new PairMap<>();
      putChildrenToMap(wiring, rotor, "Mapping");

      enigmaMachine.setWiring(position - indexDelta, wiring);
    }

    return enigmaMachine;
  }
  // }}}
}
