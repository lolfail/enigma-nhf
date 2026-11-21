package graphical;

import components.EnigmaMachine;
import components.TranslationLayer;

import java.util.List;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;

public class CryptoPanel extends JPanel {
  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;
  private List<Character> ignoredChars;

  private JTextArea inputTextArea;
  private JTextArea outputTextArea;

  private void handleCryptButtonPress(ActionEvent e) {
    enigmaMachine.reset();

    String input = inputTextArea.getText();
    StringBuilder outputBuilder = new StringBuilder();

    for (char ch : input.toCharArray()) {
      if (ignoredChars.contains(ch)) {
        outputBuilder.append(ch);
        continue;
      }
      int rawInput = translationLayer.charToInt(ch);
      int rawOutput = enigmaMachine.passThrough(rawInput);
      char output = translationLayer.intToChar(rawOutput);
      outputBuilder.append(output);
    }

    outputTextArea.setText(outputBuilder.toString());
  }

  public CryptoPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer, List<Character> ignoredChars) {
    this.enigmaMachine = enigmaMachine;
    this.translationLayer = translationLayer;
    this.ignoredChars = ignoredChars;

    setLayout(new BorderLayout());

    JSplitPane vsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    vsplitPane.setResizeWeight(0.5); // set to middle split default

    this.inputTextArea = new JTextArea();
    this.outputTextArea = new JTextArea();
    outputTextArea.setEditable(false);

    vsplitPane.add(inputTextArea);
    vsplitPane.add(outputTextArea);

    JButton encrypt = new JButton("Ok");
    encrypt.addActionListener(this::handleCryptButtonPress);

    add(vsplitPane, BorderLayout.CENTER);
    add(encrypt, BorderLayout.SOUTH);
  }
}
