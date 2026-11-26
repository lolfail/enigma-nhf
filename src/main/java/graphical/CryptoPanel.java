package graphical;

import components.EnigmaMachine;
import components.TranslationLayer;

import java.util.List;

import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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

    this.inputTextArea = new JTextArea();
    inputTextArea.setEditable(true);
    inputTextArea.setLineWrap(true);
    inputTextArea.setWrapStyleWord(true);

    this.outputTextArea = new JTextArea();
    outputTextArea.setEditable(false);
    outputTextArea.setLineWrap(true);
    outputTextArea.setWrapStyleWord(true);

    JScrollPane inputScroller = new JScrollPane(
        inputTextArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    JScrollPane outputScroller = new JScrollPane(
        outputTextArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    inputScroller.setMinimumSize(new Dimension(0, 0));
    outputScroller.setMinimumSize(new Dimension(0, 0));

    JSplitPane vsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputScroller, outputScroller);
    vsplitPane.setResizeWeight(0.5);
    vsplitPane.setContinuousLayout(true);
    vsplitPane.setOneTouchExpandable(true);
    SwingUtilities.invokeLater(() -> vsplitPane.setDividerLocation(0.5));

    JButton encrypt = new JButton("Ok");
    encrypt.addActionListener(this::handleCryptButtonPress);

    add(vsplitPane, BorderLayout.CENTER);
    add(encrypt, BorderLayout.SOUTH);
  }
}
