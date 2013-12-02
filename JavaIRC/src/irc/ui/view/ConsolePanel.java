package irc.ui.view;

import irc.core.IRCUtil;

import java.awt.*;
import java.awt.event.*;

/** 
 * This inner-class is the console which shows the server-messages. 
 */
public class ConsolePanel extends PanelTemplate implements ActionListener {

  /** 
   * The constructor calls the <code>makePanel</code>-method. 
   * @param owner The owning class.
   */
  public ConsolePanel(IRCMainFrame owner) {
    super(owner, IRCUtil.CONSOLEWINDOWTITLE);
    makePanel();
  }
    
// ------------------------------

  /** 
   * Makes the panel with textarea, inputline and nicklist. <br />
   * It also creates the splitpane and the scrollbars. 
   */
  private void makePanel() {
    input.addActionListener(this);
    topicfield.setEditable(false);
    part.setEnabled(false);
    add(scrollpane, BorderLayout.CENTER); // add scrollpane
  }
    
// ------------------------------

  /** 
   * Listens for actions of the buttons and the textarea. 
   * All input are just given to the <code>parseCmd</code> method.
   * @param e The <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source.equals(input)) {
      String line = input.getText().trim();
      if (line.length() > 0) {
        mainFrame.parseCmd(line);
        resetLine();
        addCmdToHistory(line);
      }
    }
  }
    
}

