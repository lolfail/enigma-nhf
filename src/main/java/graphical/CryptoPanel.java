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

/**
 * The view, handling encryption/decryption with an {@link EnigmaMachine}.
 *
 * Left panel is for input, right panel for output (not editable).
 * Click the OK button to handle it.
 */
public class CryptoPanel extends JPanel {
  private EnigmaMachine enigmaMachine;
  private TranslationLayer translationLayer;
  private List<Character> ignoredChars;

  private JTextArea inputTextArea;
  private JTextArea outputTextArea;

  /**
   * Callback / event handler to run the cryptographic functions.
   * @param _e Unused.
   */
  private void handleCryptButtonPress(ActionEvent _e) {
    enigmaMachine.reset();

    String input = inputTextArea.getText();
    StringBuilder outputBuilder = new StringBuilder();

    // C style goto cleanup structure could be used for those ifs :(
    for (char ch : input.toCharArray()) {
      if (ignoredChars.contains(ch)) {
        outputBuilder.append(ch);
        continue;
      }

      Integer rawInput = translationLayer.charToInt(ch);
      if (rawInput == null) {
        outputBuilder.append(ch);
        continue;
      }

      Integer rawOutput = enigmaMachine.passThrough(rawInput);
      Character output = translationLayer.intToChar(rawOutput);
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
    vsplitPane.setResizeWeight(0.5); // set to middle be at the middle by default

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
