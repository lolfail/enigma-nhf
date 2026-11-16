package graphical;

import components.EnigmaMachine;
import components.TranslationLayer;

import graphical.CryptoPanel;
import graphical.RotorPanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import utils.SelfMap;

// the reason the panels aren't handling this is:
// to fully abstract away the string ID stuff (so the parent class needs invoke a .getId() or something),
// we'd need to shift that behaviour down to the children
// however the ID needs to be registered at the parent container level
// so if the user of the class does not know this, we can get corrupted behaviour
class NavigationActionFactory {
  private Container container;
  private CardLayout cardLayoutManager;

  public NavigationActionFactory(Container container, CardLayout cardLayoutManager) {
    this.container = container;
    this.cardLayoutManager = cardLayoutManager;
  }

  public Action getNavigationAction(String identifier, String name) {
    return new AbstractAction(name) {
      public void actionPerformed(ActionEvent e) {
        cardLayoutManager.show(container, identifier);
      }
    };
  }
}

public class MainFrame extends JFrame {
  public MainFrame(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    setTitle("Configurable Enigma Simulator");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel contentPanel = new JPanel(new CardLayout());
    CardLayout cardLayoutManager = (CardLayout) contentPanel.getLayout();

    CryptoPanel cryptoPanel = new CryptoPanel(enigmaMachine, translationLayer);
    MapperPanel<SelfMap<Integer>> reflectorPanel = new MapperPanel<>(translationLayer, enigmaMachine::setReflector, enigmaMachine::getReflector);
    MapperPanel<SelfMap<Integer>> plugboardPanel = new MapperPanel<>(translationLayer, enigmaMachine::setPlugboard, enigmaMachine::getPlugboard);
    RotorPanel rotorPanel = new RotorPanel(enigmaMachine, translationLayer);

    contentPanel.add(cryptoPanel, "CRYPTO");
    contentPanel.add(reflectorPanel, "REFLECTOR");
    contentPanel.add(plugboardPanel, "PLUGBOARD");
    contentPanel.add(rotorPanel, "ROTOR");

    JMenuBar menuBar = new JMenuBar();
    NavigationActionFactory navFactory = new NavigationActionFactory(contentPanel, cardLayoutManager);
    menuBar.add(new JMenuItem(navFactory.getNavigationAction("CRYPTO", "crypto")));
    menuBar.add(new JMenuItem(navFactory.getNavigationAction("REFLECTOR", "reflector")));
    menuBar.add(new JMenuItem(navFactory.getNavigationAction("PLUGBOARD", "plugboard")));
    menuBar.add(new JMenuItem(navFactory.getNavigationAction("ROTOR", "rotors")));

    setJMenuBar(menuBar);

    add(contentPanel, BorderLayout.CENTER);
    setVisible(true);
  }
}
