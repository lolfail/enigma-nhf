package graphical;
import graphical.MapperPanel;

import utils.SelfMap;
import components.EnigmaMachine;
import components.TranslationLayer;

import javax.swing.JPanel;

public class PlugboardPanel extends JPanel {
  public PlugboardPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    MapperPanel<SelfMap<Integer>> mapperPanel
      = new MapperPanel<>(translationLayer, enigmaMachine::setPlugboard, enigmaMachine::getPlugboard);

    add(mapperPanel);
  }
}
