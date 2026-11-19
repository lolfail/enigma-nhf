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

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

// reason for this is that CardLayout keeps every view in memory
// => would have to write refresh handlers upon navigation
// simpler to just re-create panels
class LazyCardPanel extends JPanel {
  private Map<String, Supplier<JPanel>> views = new HashMap<>();
  private JPanel currentView;

  public LazyCardPanel() {
    super(new BorderLayout());
  }

  public void registerView(String identifier, Supplier<JPanel> factory) {
    views.put(identifier, factory);
  }

  public void showView(String identifier) {
    Supplier<JPanel> viewFactory = views.get(identifier);
    if (viewFactory == null)
      return;
    JPanel view = viewFactory.get();

    if (currentView != null)
      remove(currentView);
    add(view);
    currentView = view;

    revalidate();
    repaint();
  }

  public JMenuItem getJMenuItem(String identifier, String name) {
    JMenuItem menuItem = new JMenuItem(name);

    // trick to keep it from stretching across the entire menubar
    menuItem.setMaximumSize(menuItem.getPreferredSize());

    menuItem.addActionListener(e -> {
      this.showView(identifier);
    });

    return menuItem;
  }

}

public class MainFrame extends JFrame {
  public MainFrame(EnigmaMachine enigmaMachine, TranslationLayer translationLayer) {
    setTitle("Configurable Enigma Simulator");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    LazyCardPanel lazyCardPanel = new LazyCardPanel();
    lazyCardPanel.registerView("CRYPTO", () ->
        new CryptoPanel(enigmaMachine, translationLayer));
    lazyCardPanel.registerView("REFLECTOR", () ->
        new MapperPanel<SelfMap<Integer>>(translationLayer, enigmaMachine::setReflector, enigmaMachine::getReflector));
    lazyCardPanel.registerView("PLUGBOARD", () ->
        new MapperPanel<SelfMap<Integer>>(translationLayer, enigmaMachine::setPlugboard, enigmaMachine::getPlugboard));
    lazyCardPanel.registerView("ROTOR", () ->
        new RotorPanel(enigmaMachine, translationLayer));

    lazyCardPanel.showView("CRYPTO");

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(new FileMenu("File", enigmaMachine));
    menuBar.add(lazyCardPanel.getJMenuItem("CRYPTO", "crypto"));
    menuBar.add(lazyCardPanel.getJMenuItem("REFLECTOR", "reflector"));
    menuBar.add(lazyCardPanel.getJMenuItem("PLUGBOARD", "plugboard"));
    menuBar.add(lazyCardPanel.getJMenuItem("ROTOR", "rotors"));

    setJMenuBar(menuBar);

    add(lazyCardPanel, BorderLayout.CENTER);
    setVisible(true);
  }
}
