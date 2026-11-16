package graphical;
import graphical.MapperPanel;

import utils.SelfMap;
import components.EnigmaMachine;
import components.TranslationLayer;

import javax.swing.JPanel;

public class ReflectorPanel extends JPanel {
  public ReflectorPanel(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    MapperPanel<SelfMap<Integer>> mapperPanel
      = new MapperPanel<>(translationLayer, enigmaMachine::setReflector, enigmaMachine::getReflector);

    add(mapperPanel);
  }
}
