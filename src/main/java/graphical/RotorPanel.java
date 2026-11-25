package graphical;

import graphical.MapperPanel;

import utils.PairMap;

import components.EnigmaMachine;
import components.Rotor;
import components.TranslationLayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Window;
import javax.swing.BoxLayout;
import java.awt.GridLayout;

import java.awt.Dialog;
import javax.swing.JDialog;

/**
 * A combobox of characters who should handle setting the starting position of a rotor.
 */
class RotorComboBox extends JComboBox<Character> {
  private TranslationLayer translationLayer;
  private Consumer<Integer> selectStartIndex;
  private Supplier<Integer> previousStartIndex;

  private void handleSelection(ActionEvent e) {
    Character selected = (Character) getSelectedItem();
    if (selected == null)
      return;

    int position = translationLayer.charToInt(selected);
    selectStartIndex.accept(position);
  }

  public RotorComboBox(TranslationLayer translationLayer, Consumer<Integer> selectStartIndex, Supplier<Integer> previousStartIndex) {
    this.translationLayer = translationLayer;
    this.selectStartIndex = selectStartIndex;
    this.previousStartIndex = previousStartIndex;

    // populate with the chars
    for (char ch : translationLayer.getDictionary().keySet())
      addItem(ch);

    char defaultSelection = translationLayer.intToChar(previousStartIndex.get());
    setSelectedItem(defaultSelection);

    // handle setting it
    addActionListener(this::handleSelection);
  }
}

/**
 * A view handling setting up the rotors.
 * Provide a "spinner" to set the number of rotors, and comboboxes associated with rotors for the starting position.
 * Also provide a button next to each combobox such that one may set their internal wirings.
 */
public class RotorPanel extends JPanel {
  private int rows = 5;
  private int cols = 4;

  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;
  private JPanel managedRotorsPanel;
  private Window parentWindow = SwingUtilities.getWindowAncestor(this);

  /**
   * Helper to clear off the rotors panel and repopulate it again.
   * This is needed because if the number of rotors is changed, we need to change the number of comboboxes.
   */
  private void redrawManagedRotorsPanel() {
    remove(managedRotorsPanel);
    managedRotorsPanel = constructRotorConfigPanel();
    add(managedRotorsPanel);
    revalidate();
    repaint();
  }

  // construct a panel for rotor #rotorIndex to configure it
  /**
   * Construct a configuration panel for a rotor at a certain index.
   * @param rotorIndex The index of the rotor within the EnigmaMachine to associate this entry with.
   * @return A panel containing the combobox (starting position) and the button for the wiring popup.
   */
  private JPanel constructRotorConfigEntry(int rotorIndex) {
    JPanel rotorEntryPanel = new JPanel();

    // number of the rotor
    rotorEntryPanel.add(new JLabel("Rotor #" + (rotorIndex + 1)));
    // combobox
    rotorEntryPanel.add(new RotorComboBox(translationLayer,
      (Integer position) -> enigmaMachine.setStartingPosition(rotorIndex, position),
      () -> enigmaMachine.getStartingPosition(rotorIndex))
    );

    // wiring configuration button
    JButton configButton = new JButton("Configure");
    configButton.addActionListener(e -> {
      // popup with a MapperPanel as its content for the wiring of the rotor at this index
      JDialog dialog = new JDialog(parentWindow, "Rotor Configuration", Dialog.ModalityType.APPLICATION_MODAL);
      dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      JPanel content = new MapperPanel<>(translationLayer,
        (PairMap<Integer, Integer> map) -> enigmaMachine.setWiring(rotorIndex, map),
        () -> enigmaMachine.getWiring(rotorIndex)
      );
      dialog.setContentPane(content);

      dialog.pack();
      dialog.setLocationRelativeTo(parentWindow);
      dialog.setVisible(true);
    });

    rotorEntryPanel.add(configButton);
    return rotorEntryPanel;
  }

  /**
   * Construct a panel from {@link #constructRotorConfigEntry(int)} for all rotors in the machine.
   * @return The panel for this in a nice grid layout.
   */
  private JPanel constructRotorConfigPanel() {
    JPanel rotorConfigPanel = new JPanel();
    rotorConfigPanel.setLayout(new GridLayout(rows, cols));

    for (int i = 0; i < enigmaMachine.getNumRotors(); i++) {
      rotorConfigPanel.add(constructRotorConfigEntry(i));
    }

    return rotorConfigPanel;
  }

  /**
   * Construct a panel for configuring the number of rotors within the machine.
   * @return The panel holding a spinner to set the number and a button to "save" changes.
   */
  private JPanel constructRotorNumConfigPanel() {
    JPanel rotorNumConfigPanel = new JPanel();

    // spinner
    SpinnerNumberModel rotorNumSpinnerModel = new SpinnerNumberModel(enigmaMachine.getNumRotors(), 1, rows * cols, 1);
    JSpinner rotorNumSpinner = new JSpinner(rotorNumSpinnerModel);

    // button
    JButton commitRotorNumButton = new JButton("Ok");
    commitRotorNumButton.addActionListener((ActionEvent e) -> {
      int value = (Integer) rotorNumSpinner.getValue();
      enigmaMachine.setNumRotors(value);
      redrawManagedRotorsPanel();
    });

    rotorNumConfigPanel.add(rotorNumSpinner);
    rotorNumConfigPanel.add(commitRotorNumButton);

    return rotorNumConfigPanel;
  }

  public RotorPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    this.enigmaMachine = enigmaMachine;
    this.translationLayer = translationLayer;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JPanel rotorNumConfigPanel = constructRotorNumConfigPanel();
    this.managedRotorsPanel = constructRotorConfigPanel();

    add(rotorNumConfigPanel);
    add(managedRotorsPanel);
  }
}
