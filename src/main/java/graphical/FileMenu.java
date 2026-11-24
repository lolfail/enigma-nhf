package graphical;

import components.EnigmaMachine;
import components.EnigmaMarshaller;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.InputStream;

import java.awt.event.ActionEvent;

public class FileMenu extends JMenu {
  private EnigmaMarshaller marshaller;
  private EnigmaMachine enigmaMachine;

  private JFileChooser createChooser(String title) {
    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML configuration files", "xml");

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(title);
    chooser.setFileFilter(filter);
    return chooser;
  }

  private void save(ActionEvent e) {
    JFileChooser chooser = createChooser("Save Configuration");

    int returnVal = chooser.showSaveDialog(this.getParent());
    if (returnVal != JFileChooser.APPROVE_OPTION)
      return;

    File file = chooser.getSelectedFile();
    try (OutputStream fileOStream = Files.newOutputStream(file.toPath())) {
      marshaller.write(fileOStream, enigmaMachine);
    } catch (Exception err) {
      JOptionPane.showConfirmDialog(this.getParent(), err.getMessage());
      System.err.println(err);
    }
  }

  private void load(ActionEvent e) {
    JFileChooser chooser = createChooser("Load Configuration");

    int returnVal = chooser.showSaveDialog(this.getParent());
    if (returnVal != JFileChooser.APPROVE_OPTION)
      return;

    File file = chooser.getSelectedFile();
    try (InputStream fileIStream = Files.newInputStream(file.toPath())) {
      enigmaMachine.shallowCopyInternals(marshaller.read(fileIStream));
    } catch (Exception err) {
      JOptionPane.showConfirmDialog(this.getParent(), err.getMessage());
      System.err.println(err);
    }
  }

  public FileMenu(String name, EnigmaMachine enigmaMachine) {
    super(name);

    this.marshaller = new EnigmaMarshaller(true);
    this.enigmaMachine = enigmaMachine;

    JMenuItem saveItem = new JMenuItem("Save");
    saveItem.addActionListener(this::save);
    add(saveItem);

    JMenuItem loadItem = new JMenuItem("Load");
    loadItem.addActionListener(this::load);
    add(loadItem);
  }
}
