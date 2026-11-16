package graphical;

import graphical.MapperPanel;

import utils.PairMap;

import components.EnigmaMachine;
import components.TranslationLayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Window;
import javax.swing.BoxLayout;
import java.awt.GridLayout;

import java.awt.Dialog;
import javax.swing.JDialog;

// This level of indentation of inner classes seemed like a good idea at the time...
public class RotorPanel extends JPanel {
  private int rows = 5;
  private int cols = 4;

  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;
  private JPanel managedRotorsPanel;
  private Window parentWindow = SwingUtilities.getWindowAncestor(this);

  private void redrawManagedRotorsPanel() {
    remove(managedRotorsPanel);
    managedRotorsPanel = new RotorConfigPanel(enigmaMachine, translationLayer);
    add(managedRotorsPanel);
    revalidate();
    repaint();
  }

  private class RotorNumConfigPanel extends JPanel {
    public RotorNumConfigPanel() {
      SpinnerNumberModel rotorNumSpinnerModel = new SpinnerNumberModel(enigmaMachine.getNumRotors(), 1, rows * cols, 1);
      JSpinner rotorNumSpinner = new JSpinner(rotorNumSpinnerModel);

      JButton commitRotorNumButton = new JButton(new AbstractAction("Ok") {
        public void actionPerformed(ActionEvent e) {
          int value = (Integer) rotorNumSpinner.getValue();
          enigmaMachine.setNumRotors(value);
          redrawManagedRotorsPanel();
        }
      });

      add(rotorNumSpinner);
      add(commitRotorNumButton);
    }
  }

  private class RotorConfigPanel extends JPanel {
    EnigmaMachine enigmaMachine;
    TranslationLayer translationLayer;

    private class RotorConfigEntry extends JPanel {
      private int rotorIndex;

      private class RotorComboBox extends JComboBox<Character> {
        public RotorComboBox() {
          for (char ch : translationLayer.getDictionary().keySet())
            addItem(ch);

          char defaultSelection = translationLayer.intToChar(enigmaMachine.getStartingPosition(rotorIndex));
          setSelectedItem(defaultSelection);

          addActionListener(e -> {
            Character selected = (Character) getSelectedItem();
            if (selected == null)
              return;

            int position = translationLayer.charToInt(selected);
            enigmaMachine.setStartingPosition(rotorIndex, position);
          });
        }
      }

      public RotorConfigEntry(int rotorIndex) {
        this.rotorIndex = rotorIndex;

        Consumer<PairMap<Integer, Integer>> consumer = (PairMap<Integer, Integer> map) -> enigmaMachine
            .setWiring(rotorIndex, map);
        Supplier<PairMap<Integer, Integer>> supplier = () -> enigmaMachine.getWiring(rotorIndex);

        add(new JLabel("Rotor #" + rotorIndex));
        add(new RotorComboBox());
        add(new JButton(new AbstractAction("Configure") {
          public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog(parentWindow, "Rotor Configuration", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            JPanel content = new MapperPanel<>(translationLayer, consumer, supplier);
            dialog.setContentPane(content);

            dialog.pack();
            dialog.setLocationRelativeTo(parentWindow);
            dialog.setVisible(true);
          }
        }));
      }
    }

    public RotorConfigPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
      this.enigmaMachine = enigmaMachine;
      this.translationLayer = translationLayer;

      setLayout(new GridLayout(rows, cols));

      for (int i = 0; i < enigmaMachine.getNumRotors(); i++) {
        add(new RotorConfigEntry(i));
      }
    }
  }

  public RotorPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    this.enigmaMachine = enigmaMachine;
    this.translationLayer = translationLayer;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    RotorNumConfigPanel rotorNumConfigPanel = new RotorNumConfigPanel();
    this.managedRotorsPanel = new RotorConfigPanel(enigmaMachine, translationLayer);

    add(rotorNumConfigPanel);
    add(managedRotorsPanel);
  }
}
