package graphical;

import java.util.Map;
import components.TranslationLayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

public class MapperPanel<V extends Map<Integer, Integer>> extends JPanel {
  private TranslationLayer translationLayer;
  private Consumer<V> consumer;
  private Supplier<V> supplier;

  private JTextField mappingTextField;

  private String generateTextFieldDefaults() {
    V mapping = supplier.get();

    StringBuilder mapString = new StringBuilder();

    for (Map.Entry<Integer, Integer> map : mapping.entrySet()) {
      char key = translationLayer.intToChar(map.getKey());
      char value = translationLayer.intToChar(map.getValue());
      mapString.append(key);
      mapString.append(value);
      mapString.append(" ");
    }
    if (mapping.size() > 0)
      mapString.delete(mapString.length() - 1, mapString.length());

    return mapString.toString();
  }

  private V updateMapping(String text) {
    V mapping = supplier.get();

    String[] pairs = text.split(" ");
    for (String pair : pairs) {
      if (pair.length() != 2)
        return mapping; // shortcircuit on wrong mapping -- LET THE USER KNOW TODO!
    }

    mapping.clear();
    for (String pair : pairs) {
      int key = translationLayer.charToInt(pair.charAt(0));
      int value = translationLayer.charToInt(pair.charAt(1));
      mapping.put(key, value);
    }

    System.out.println(mapping.entrySet());
    return mapping;
  }

  private Action getUpdateHandler() {
    return new AbstractAction("Ok") {
      public void actionPerformed(ActionEvent e) {
        V updated = updateMapping(mappingTextField.getText());
        consumer.accept(updated);
      }
    };
  }

  public MapperPanel(TranslationLayer translationLayer, Consumer<V> consumer, Supplier<V> supplier) {
    this.translationLayer = translationLayer;
    this.consumer = consumer;
    this.supplier = supplier;

    this.mappingTextField = new JTextField(generateTextFieldDefaults());
    mappingTextField.setColumns(52);

    JButton okButton = new JButton(getUpdateHandler()); // via spec there should not be buttons

    add(mappingTextField);
    add(okButton);
  }
}
