import components.EnigmaMachine;
import components.TranslationLayer;

import utils.SelfMap;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import graphical.MainFrame;
import javax.swing.SwingUtilities;

/**
 * The good old boilerplate stuff :)
 */
public class Main {
  /**
   * Yield a Map containing entries: (0,1), (2,3) ... (param-2, param-1).
   * @param count How far to go with these mappings.
   * @return The mapping backed by a HashMap
   *
   * Simple helper to prefill the default {@link EnigmaMachine}'s reflector on creation.
   */
  private static Map<Integer, Integer> shiftMapping(int count) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < count-1; i+=2)
      map.put(i, i+1);
    return map;
  }

  /**
   * Entrypoint for the program.
   */
  public static void main() {
    // create the EnigmaMachine here <= maybe CLI interface later
    TranslationLayer translationLayer = new TranslationLayer();
    List<Character> ignoredChars = List.of(' ', '\n');

    EnigmaMachine enigmaMachine = new EnigmaMachine(26);
    enigmaMachine.setNumRotors(3);
    enigmaMachine.setReflector(new SelfMap<>(false, shiftMapping(26)));

    SwingUtilities.invokeLater(() -> {
      MainFrame frame = new MainFrame(enigmaMachine, translationLayer, ignoredChars);
      frame.setVisible(true);
    });
  }
};
