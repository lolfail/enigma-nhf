package graphical;

import java.util.Map;
import components.TranslationLayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.Box;
import java.awt.Color;

public class MapperPanel<V extends Map<Integer, Integer>> extends JPanel {
  private TranslationLayer translationLayer;
  private Consumer<V> consumer;
  private Supplier<V> supplier;

  private JTextField mappingTextField;
  private JLabel feedbackLabel;
  private MappingsListPanel mappingsListPanel;

  private void setFailureLabel(String message) {
    feedbackLabel.setText(message);
    feedbackLabel.setForeground(new Color(0x60, 0x10, 0x0b)); // jam red TODO! not visible enough
  }
  private void setSuccessLabel(Map<Integer, Integer> map) {
    int mappedNum = map.entrySet().size();
    feedbackLabel.setText(mappedNum + " mappings created.");
    feedbackLabel.setForeground(new Color(0x00, 0x42, 0x25)); // british racing green TODO! not visible enough
    mappingsListPanel.updateMappings(map);
  }

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
    if (pairs.length == 1 && pairs[0].equals(""))
      pairs = new String[0]; // if empty split would still make: [""]

    for (String pair : pairs) {
      if (pair.length() != 2)
        throw new RuntimeException("Wrong format!");
    }

    mapping.clear();
    for (String pair : pairs) {
      Integer key = translationLayer.charToInt(pair.charAt(0));
      Integer value = translationLayer.charToInt(pair.charAt(1));
      if (key == null)
        throw new RuntimeException("Illegal character: " + pair.charAt(0));
      else if (value == null)
        throw new RuntimeException("Illegal character: " + pair.charAt(1));

      mapping.put(key, value);
    }

    return mapping;
  }

  private void getUpdateHandler(ActionEvent e) {
    try {
      V updated = updateMapping(mappingTextField.getText());
      consumer.accept(updated);
      setSuccessLabel(updated);
    } catch (RuntimeException err) {
      setFailureLabel(err.getMessage());
    }
  }

  public MapperPanel(TranslationLayer translationLayer, Consumer<V> consumer, Supplier<V> supplier) {
    this.translationLayer = translationLayer;
    this.consumer = consumer;
    this.supplier = supplier;

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    // left horizontal spacer
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    gbc.weightx = 0.2;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.BOTH;
    add(Box.createGlue(), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.6;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 4, 8);
    this.mappingTextField = new JTextField(generateTextFieldDefaults());
    mappingTextField.setColumns(26);
    add(mappingTextField, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.insets = new Insets(0, 0, 12, 8);
    this.feedbackLabel = new JLabel();
    add(feedbackLabel, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.insets = new Insets(0, 0, 0, 0);
    JButton okButton = new JButton("Ok"); // via spec there should not be buttons
    okButton.addActionListener(this::getUpdateHandler);
    add(okButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    gbc.weightx = 0.6;
    gbc.weighty = 0.4;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 0, 0);
    this.mappingsListPanel = new MappingsListPanel(translationLayer);
    add(mappingsListPanel, gbc);

    // right horizontal spacer
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 3;
    gbc.weightx = 0.2;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.BOTH;
    add(Box.createGlue(), gbc);

    // bottom vertical spacer
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 4;
    gbc.gridheight = 1;
    gbc.weightx = 0.0;
    gbc.weighty = 0.6;
    gbc.fill = GridBagConstraints.BOTH;
    add(Box.createGlue(), gbc);
  }
}

// translationLayer is passed down instead of giving it a charmap
// because otherwise constructing a new generic map would be needed
class MappingsListPanel extends JPanel {
  private TranslationLayer translationLayer;

  public MappingsListPanel(TranslationLayer translationLayer) {
    this.translationLayer = translationLayer;
    setLayout(new GridLayout(5, 6));
  }

  void updateMappings(Map<Integer, Integer> map) {
    removeAll();
    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      // TODO! add borders around it
      char key = translationLayer.intToChar(entry.getKey());
      char value = translationLayer.intToChar(entry.getValue());

      String format = key + " - " + value;
      JLabel entryMappingLabel = new JLabel(format, SwingConstants.CENTER);
      entryMappingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      add(entryMappingLabel);
    }
    revalidate();
    repaint();
  }
}
