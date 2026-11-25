package graphical;

import components.EnigmaMachine;
import components.TranslationLayer;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import utils.SelfMap;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.util.function.Supplier;

/**
 * Custom CardLayout-like "view manager" that redraws (regenerates entirely) the views upon switching to them.
 *
 * Provide a factory method, that makes a JPanel and give it an id.
 * Then calling {@link #showView(String)} with the id shall generate that panel and show it.
 *
 * Reason for needing such a thing is that CardLayout keeps every view in memory at all times, never kills them off.
 * This is not a problem in itself, however for this small app it is acceptable to regenerate the panel every time something changes.
 * (Instead of handling changes dynamically.)
 */
class LazyCardPanel extends JPanel {
  private Map<String, Supplier<JPanel>> views = new HashMap<>();
  private JPanel currentView;

  public LazyCardPanel() {
    super(new BorderLayout());
  }

  /**
   * Register / add to this view handler a JPanel generating method with an id.
   * @param identifier The id (unique name) to assign to this view.
   * @param factory The supplier method that should provide a way to generate the JPanel.
   */
  public void registerView(String identifier, Supplier<JPanel> factory) {
    views.put(identifier, factory);
  }

  /**
   * Show the view owning the id provided.
   * @param identifier The id (unique name) of the view to show.
   */
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

  /**
   * Helper to construct a JMenuItem that when clicked switches to a view.
   * @param identifier The identifier to associate this JMenuItem with.
   * @param name The name/text the menu item shall bear.
   * @return The JMenuItem constructed.
   */
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

/**
 * The primary JFrame class that handles other high level graphical components.
 */
public class MainFrame extends JFrame {
  public MainFrame(EnigmaMachine enigmaMachine, TranslationLayer translationLayer, List<Character> ignoredChars) {
    setTitle("Configurable Enigma Simulator");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    LazyCardPanel lazyCardPanel = new LazyCardPanel();
    lazyCardPanel.registerView("CRYPTO", () ->
        new CryptoPanel(enigmaMachine, translationLayer, ignoredChars));
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
