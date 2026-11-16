package graphical;

import components.EnigmaMachine;
import components.TranslationLayer;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

public class CryptoPanel extends JPanel {
  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;

  private JTextArea inputTextArea;
  private JTextArea outputTextArea;

  private void updateOutputTextArea() {
    enigmaMachine.reset();

    String input = inputTextArea.getText();
    StringBuilder outputBuilder = new StringBuilder();

    for (char ch : input.toCharArray()) {
      // TODO! passthough newlines
      int rawInput = translationLayer.charToInt(ch);
      int rawOutput = enigmaMachine.passThrough(rawInput);
      char output = translationLayer.intToChar(rawOutput);
      outputBuilder.append(output);
    }

    outputTextArea.setText(outputBuilder.toString());
  }

  public CryptoPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    this.enigmaMachine = enigmaMachine;
    this.translationLayer = translationLayer;

    setLayout(new BorderLayout());

    JSplitPane vsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    vsplitPane.setResizeWeight(0.5); // set to middle split default

    this.inputTextArea = new JTextArea();
    this.outputTextArea = new JTextArea();
    outputTextArea.setEditable(false);

    vsplitPane.add(inputTextArea);
    vsplitPane.add(outputTextArea);

    JButton encrypt = new JButton(new AbstractAction("OK") {
      public void actionPerformed(ActionEvent e) {
        updateOutputTextArea();
      }
    });

    add(vsplitPane, BorderLayout.CENTER);
    add(encrypt, BorderLayout.SOUTH);
  }
}
