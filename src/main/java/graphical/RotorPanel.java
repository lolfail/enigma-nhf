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

class RotorComboBox extends JComboBox<Character> {
  private TranslationLayer translationLayer;
  private Consumer<Integer> selectedIndexSetter;
  private Supplier<Integer> startIndexGetter;

  private void handleSelection(ActionEvent e) {
    Character selected = (Character) getSelectedItem();
    if (selected == null)
      return;

    int position = translationLayer.charToInt(selected);
    selectedIndexSetter.accept(position);
  }

  public RotorComboBox(TranslationLayer translationLayer, Consumer<Integer> selectedIndexSetter, Supplier<Integer> startIndexGetter) {
    this.translationLayer = translationLayer;
    this.selectedIndexSetter = selectedIndexSetter;
    this.startIndexGetter = startIndexGetter;

    for (char ch : translationLayer.getDictionary().keySet())
      addItem(ch);

    char defaultSelection = translationLayer.intToChar(startIndexGetter.get());
    setSelectedItem(defaultSelection);

    addActionListener(this::handleSelection);
  }
}

public class RotorPanel extends JPanel {
  private int rows = 5;
  private int cols = 4;

  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;
  private JPanel managedRotorsPanel;
  private Window parentWindow = SwingUtilities.getWindowAncestor(this);

  private void redrawManagedRotorsPanel() {
    remove(managedRotorsPanel);
    managedRotorsPanel = constructRotorConfigPanel();
    add(managedRotorsPanel);
    revalidate();
    repaint();
  }

  // construct a panel for rotor #rotorIndex to configure it
  private JPanel constructRotorConfigEntry(int rotorIndex) {
    JPanel rotorEntryPanel = new JPanel();

    rotorEntryPanel.add(new JLabel("Rotor #" + (rotorIndex + 1)));
    rotorEntryPanel.add(new RotorComboBox(translationLayer,
      (Integer position) -> enigmaMachine.setStartingPosition(rotorIndex, position),
      () -> enigmaMachine.getStartingPosition(rotorIndex))
    );

    JButton configButton = new JButton("Configure");
    configButton.addActionListener(e -> {
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

  // construct the panel holding each individual rotor's config panels
  private JPanel constructRotorConfigPanel() {
    JPanel rotorConfigPanel = new JPanel();
    rotorConfigPanel.setLayout(new GridLayout(rows, cols));

    for (int i = 0; i < enigmaMachine.getNumRotors(); i++) {
      rotorConfigPanel.add(constructRotorConfigEntry(i));
    }

    return rotorConfigPanel;
  }

  // construct the spinner to set number of rotors in machine
  private JPanel constructRotorNumConfigPanel() {
    JPanel rotorNumConfigPanel = new JPanel();

    SpinnerNumberModel rotorNumSpinnerModel = new SpinnerNumberModel(enigmaMachine.getNumRotors(), 1, rows * cols, 1);
    JSpinner rotorNumSpinner = new JSpinner(rotorNumSpinnerModel);

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
