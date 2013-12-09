package irc.ui.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/** 
 * This is the <code>IRCPanel</code> for all queries. 
 * 
 * @author Luke
 * 
 */
public class QueryPanel extends PanelTemplate implements ActionListener {

  /** 
   * The constructor calls the <code>makePanel</code>-method. 
   * @param owner The owning class.
   * @param name The query's name.
   */
  public QueryPanel(IRCMainFrame owner, String name) {
    super(owner, name);
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
    part.setForeground(Color.blue);
    part.addActionListener(this);
      
    add(scrollpane, BorderLayout.CENTER); 

  }
    
// ------------------------------

  /** 
   * Listens for actions of the buttons and the textarea.<br />
   * Empty lines are ignored, normal lines are sent as <code>PRIVMSG</code>
   * to the recipient and lines led by a <code>/</code> are interpreted as
   * commands and given to the <code>parseCmd</code> method.
   * @param e The <code>ActionEvent</code>.
   */
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source.equals(input)) {
      String line = input.getText().trim();
      if (line.length() > 0) { 
        if (line.charAt(0) == '/') { 
          mainFrame.parseCmd(line);
          resetLine();
          addCmdToHistory(line); // save command
        } else { 
          conn.doPrivmsg(name,line);
          updateText(line, mainFrame.getOwnColor(), true);
          resetLine();
          addCmdToHistory(line); // save command
        }
      }
    } else if (source.equals(part) && mainFrame.isConnected()) {
      mainFrame.closePanel(name);
    }
  }
    
}