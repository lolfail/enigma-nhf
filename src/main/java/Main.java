import components.EnigmaMachine;
import components.TranslationLayer;

import utils.PairMap;
import utils.SelfMap;

import java.util.Map;
import java.util.HashMap;

import graphical.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
  private static Map<Integer, Integer> identityMapping(int count) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < count; i++)
      map.put(i, i);
    return map;
  }
  private static Map<Integer, Integer> shiftMapping(int count) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < count-1; i+=2)
      map.put(i, i+1);
    return map;
  }

  public static void main(String args[]) {
    // create the EnigmaMachine here <= maybe CLI interface later
    TranslationLayer translationLayer = new TranslationLayer();
    EnigmaMachine enigmaMachine = new EnigmaMachine(26);

    enigmaMachine.setNumRotors(3);

    enigmaMachine.setReflector(new SelfMap<>(false, shiftMapping(26)));

    enigmaMachine.setPlugboard(new SelfMap<>(true, identityMapping(26)));

    SwingUtilities.invokeLater(() -> {
      MainFrame frame = new MainFrame(enigmaMachine, translationLayer);
      frame.setVisible(true);
    });
  }
};
