package irc.ui.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import irc.core.*;

/** 
 * This is the panel for all channels. 
 */
public class ChanPanel extends PanelTemplate implements ActionListener {

   //* The graphical nicklist. 
  private JList nicklist = new JList();

   //* The content of the nicklist.
  private DefaultListModel nicks = new DefaultListModel();

   //* The <code>JPopupMenu</code> is used for the right-click menu of the 
  private JPopupMenu nicklistPopup = getNicklistPopupMenu();

   //* The arraylist which saves the list of banmask. 
  private ArrayList<String> banlist = new ArrayList<String>();

   //* Represents the actual modes of the channel.<br />
  private StringBuffer modebuffer = new StringBuffer();

   //* The beginning index in the nicklist of the operators. 
  private int beginningOpers = 0;
    
   //* The beginning index in the nicklist of the voiced users. 
  private int beginningVoiced = 0;
    
   //* The beginning index in the nicklist of the normal users. 
  private int beginningNormal = 0;

   //* The amount of all users on the channel. 
  private int count = 0;

// ------------------------------

  /** 
   * The constructor calls the <code>makePanel</code>-method. 
   * @param owner The owning class.
   * @param name The channel's name.
   */
  public ChanPanel(IRCMainFrame owner, String name) {
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
    topicfield.addActionListener(this);
    part.setForeground(Color.red);
    part.addActionListener(this);
    nicklist.setModel(nicks);
    nicklist.setBackground(mainFrame.getBgColor());
    nicklist.add(nicklistPopup);
    nicklist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    nicklist.setLayoutOrientation(JList.VERTICAL);
    nicklist.setVisibleRowCount(-1);
    nicklist.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) { 
        if (e.getButton() == MouseEvent.BUTTON3)
          nicklistPopup.show(nicklist, e.getX(), e.getY());
      }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }
    } ); 
    JScrollPane scrolllist = new JScrollPane(nicklist, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
        scrollpane, scrolllist);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(mainFrame.getWidth() - 150);
    splitPane.setResizeWeight(1.0);
    add(splitPane, BorderLayout.CENTER); // add splitpane

  }
    
// ------------------------------

  /** 
   * Listens for actions of the buttons and the textarea. 
   * Empty lines are ignored, normal lines are sent as <code>PRIVMSG</code>
   * to the channel and lines led by a <code>/</code> are interpreted as
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
          addCmdToHistory(line); 
        } else {
          conn.doPrivmsg(name, line);
          updateText(line, mainFrame.getOwnColor(), true);
          resetLine();
          addCmdToHistory(line); 
        }
      }

    } else if (source.equals(part)) {

      conn.doPart(name);

    } else if (source.equals(topicfield) && mainFrame.isConnected()) {

      conn.doTopic(name, getTopicField());

    }

  }
    
// ------------------------------

  /** 
   * Adds a nickname to the userlist. 
   * @param nick The nickname which is to add.
   */
  public void addNick(String nick) {
	    char c = nick.charAt(0);
	    if (c == '@') {
	      nicks.add(beginningOpers, nick);
	      beginningVoiced++;
	      beginningNormal++;
	    } else if (c == '+') {
	      nicks.add(beginningVoiced, nick);
	      beginningNormal++;
	    } else {
	      nicks.add(beginningNormal, nick);
	    }
	    count++;
	  }

// ------------------------------

  /** 
   * Removes a nickname from the userlist. 
   * @param nick The nickname which is to remove.
   * @return <code>true</code> if the element could be removed.
   */
  public boolean removeNick(String nick) {
    if (nicks.contains(nick)) {
      nicks.removeElement(nick);
      char c = nick.charAt(0);
      switch (c) {
        case '@': beginningVoiced--;
        case '+': beginningNormal--;
        default:  count--;
      }
      return true;
    } else 
      return false;
  }
    
// ------------------------------

  /** 
   * Removes all nicknames from the userlist.<br />
   * In addition it resets the class-vars for the indexes for opers, voiced- 
   * and normal users in the nicklist and all users in the channel. 
   */
  public void removeAllNicks() {
    nicklist.removeAll();
    nicks.clear();
    beginningOpers = 0;
    beginningVoiced = 0;
    beginningNormal = 0;
    count = 0;
  }
    
// ------------------------------

  /** 
   * Orders the nicklist by removing all nicks and then adding them again in 
   * right order. 
   */
  public void orderList() {
    int countOfFullList = nicks.getSize();

    Object[] opers = new Object[beginningVoiced]; 
    Object[] voiced = new Object[beginningNormal - beginningVoiced]; 
    Object[] normal = new Object[countOfFullList - beginningNormal]; 

    Object[] all = nicks.toArray();

    System.arraycopy(all, 0, opers, 0, opers.length);
    System.arraycopy(all, beginningVoiced, voiced, 0, voiced.length);
    System.arraycopy(all, beginningNormal, normal, 0, normal.length);

    Arrays.sort(opers);
    Arrays.sort(voiced);
    Arrays.sort(normal);

    nicks.ensureCapacity(countOfFullList);
    int i = 0;
    for (int j = 0; i < beginningVoiced; i++, j++)
      nicks.set(i, opers[j]);
    for (int j = 0; i < beginningNormal; i++, j++)
      nicks.set(i, voiced[j]);
    for (int j = 0; i < countOfFullList; i++, j++)
      nicks.set(i, normal[j]);
  }
    
// ------------------------------

  /** 
   * Returns the selected nicknames. 
   * @return The selected nicknames or an empty array if none is selected.
   */
  public String[] getSelectedNicks() {
    Object[] objects = nicklist.getSelectedValues();
    String[] nicks = new String[objects.length];
    String nick;
    for (int i = 0; i < nicks.length; i++) {
      nick = (String)objects[i];
      if (nick.charAt(0) == '@' || nick.charAt(0) == '+')
        nick = nick.substring(1);
      nicks[i] = nick;
    }
    return nicks;
  }

// ------------------------------

  /** 
   * Adds a banmask to the arraylist. 
   * @param banmask The banmask which is to add.
   * @param nick The nick who set the ban.
   * @param time The time in milliseconds when the ban was set.
   */
  public void addBan(String banmask, String nick, long time) {
    banlist.add(banmask +" (by "+ nick +" on "+ 
        ((time != 0) ? IRCUtil.BIGTIMESTAMP.format(new Date(time)) : 
        "unknown Date") +")");
  }

// ------------------------------

  /** 
   * Removes all banmasks from the Arraylist. 
   */
  public void removeAllBans() {
    banlist.clear();
  }
    
// ------------------------------

  /** 
   * Removes a banmask from the Arraylist. 
   * @param banmask The banmask which is to remove.
   */
  public void removeBan(String banmask) {
    String[] bans = new String[banlist.size()];
    banlist.toArray(bans);
    for (int i = 0; i < bans.length; i++)
      if (bans[i].startsWith(banmask))
        banlist.remove(bans[i]);
  }
    
// ------------------------------

  /** 
   * Updates the <code>modeBuffer</code> class-var with _all_ modes.<br />
   * It is essential for the <code>ChanCenter</code>. This method is called 
   * by the <code>updateModes(String)</code> method in the main class always 
   * when the user joins a channel. 
   * @param modes The modes which have been set.
   */
  public void updateModes(String modes) {
    IRCModeParser parser = new IRCModeParser(modes);
    modes = ""; // reset modes, we will order them
    String args = "";
    char operator;
    char mode;
    for (int i = 1; i <= parser.getCount(); i++) {
      operator = parser.getOperatorAt(i);
      mode = parser.getModeAt(i);
      modes += String.valueOf(operator) + String.valueOf(mode);
      if (mode == 'k' || mode == 'l') // limit and key have arguments
        args += " "+ parser.getArgAt(i);
    }
    modes += args;
    modebuffer = new StringBuffer(modes);
  }

// ------------------------------

  /** 
   * Updates the <code>modebuffer</code> class-var with _one_ mode.<br />
   * It is essential for the <code>ChanCenter</code>. This method is called 
   * by <code>updateMode(int, char, char, String)</code> method always when 
   * a mode is changed.
   * @param operator The mode operator (+ or -).
   * @param mode The mode itself.
   * @param arg The mode's argument or <code>""</code> if it's not set.
   */
  public void updateMode(char operator, char mode, String arg) {
    if (operator == '+') {
      // argument-remove
      if (mode == 'l') {
        updateMode('-', 'l', ""); //old limit must be removed; done recursivly
        //modebuffer = this.modebuffer; // update cos of recursive method invoke
      }
        
      int modesEndIndex = modebuffer.indexOf(" ");
      if (modesEndIndex == -1)
        modesEndIndex = 0;
      modebuffer = modebuffer.insert(modesEndIndex, 
          String.valueOf(operator) + String.valueOf(mode)); 
      if (arg.length() > 0) {
        modebuffer = modebuffer.append(" "+ arg); // add args at the end
      }
    } else {
      if (mode == 'k') { // key MIGHT be the same as the limit, therefore...
        int indexOfKeyMode = modebuffer.indexOf("+k");
        int indexOfLimitMode = modebuffer.indexOf("+l");
        StringTokenizer stArgs = new StringTokenizer(modebuffer.toString());
        stArgs.nextToken(); // jump over the first (operators and modes)
        // the following checks which arg must be removed, 
        // the first or second. a bit complicated
        if ((indexOfKeyMode != -1) && (indexOfLimitMode == -1 || 
            indexOfKeyMode < indexOfLimitMode)) { // key is first argument
          String key = stArgs.nextToken();
          int keyIndex = modebuffer.indexOf(" "+ key); //' ' marks it as word
          // remove the arg. we start at -1 because we want to cut a space. 
          // add 1 to the arg's length because we added a space " " at the 
          // beginning of the arg when we searched for its index
          modebuffer = modebuffer.replace(keyIndex, 
              keyIndex + key.length() + 1, ""); 
        } else if (indexOfKeyMode != -1) {
          stArgs.nextToken(); // jump over the first arg, it must be the limit
          String key = stArgs.nextToken();
          int keyIndex = modebuffer.lastIndexOf(" "+ key); //' ' marks as word
          // remove the arg. we start at -1 because we want to cut a space
          modebuffer = modebuffer.replace(keyIndex, 
              keyIndex + key.length() + 1, ""); 
        }
      }
      // limit
      // if it is limit, there might be still a +l because a limit is not 
      // forced to be removed before changed
      if (mode == 'l') { 
        int indexOfKeyMode = modebuffer.indexOf("+k");
        int indexOfLimitMode = modebuffer.indexOf("+l");
        if (indexOfLimitMode != -1) { // stop if there is no limit yet
          StringTokenizer stArgs = new StringTokenizer(modebuffer.toString());
          stArgs.nextToken(); // jump over the first (operators and modes)
          // the following checks which arg must be removed, the first or 
          // second. a bit complicated
          // limit is first argument:
          if (indexOfKeyMode == -1 || indexOfLimitMode < indexOfKeyMode) { 
            String limit = stArgs.nextToken();
            int limitIndex = modebuffer.indexOf(" "+ limit); // ' ' marks word
            // remove the argument. we start at -1 because we want to cut a 
            // space. add 1 to the arg's length because we added a space " " 
            // at the beginning of the arg when we searched for its index.
            modebuffer = modebuffer.replace(limitIndex, 
                limitIndex + limit.length() + 1, ""); 
          } else {
            stArgs.nextToken(); // jump over the first arg, it must be the key
            String limit = stArgs.nextToken();
            int limitIndex = modebuffer.lastIndexOf(" "+ limit); // mark word
            // remove the arg. we start at -1 because we want to cut a space
            modebuffer = modebuffer.replace(limitIndex, 
                limitIndex + limit.length() + 1, ""); 
          }
        }
      }

        
      // mode-remove
      // the + <mode> must be replaced. so first get the index
      int replaceIndex = modebuffer.indexOf("+"+ String.valueOf(mode)); 
      if (replaceIndex != -1) {
        modebuffer = modebuffer.replace(replaceIndex, replaceIndex+2, "");
      }
    }

    //this.modebuffer = modebuffer;
  }

// ------------------------------

  /** 
   * Changes the content of the topic-textfield.
   * @param topic The new topic.
   */
  public void setTopicField(String topic) {
    topicfield.setText(topic);
  }

// ------------------------------

  /** 
   * Returns the content of the topic-textfield. 
   * @return The content of the topic-textfield. 
   */
  public String getTopicField() {
    return topicfield.getText();
  }

// ------------------------------

  /** 
   * Opens the channel-info-window. 
   */
  public void openControlCenter() {
	//TODO : Channel Center
    new ControlCenter(mainFrame, name, count, beginningVoiced, banlist, modebuffer.toString());
  }

// ------------------------------

  /** 
   * Returns amount of users in the channel. 
   * @return The count of users.
   */
  public int getUserCount() {
    return this.count;
  }

// ------------------------------

  /**
   * Generates a <code>JPopupMenu</code> with all options related with the 
   * nicklist and a nickname selected in the nicklist. This method 
   * (<code>getNicklistPopupMenu</code>) is used by the constructor to set
   * the <code>nicklistPopup</code> class-var. The other, very similar method
   * (<code>getNicklistMenu</code>) is used for the <code>JMenuBar</code>.
   * @return The nicklist <code>JPopupMenu</code>.
   */
  private JPopupMenu getNicklistPopupMenu() {
    JPopupMenu popup = new JPopupMenu("Nicklist");
    // MenuBar -> Nicklist -> Modes
    JMenu menuModenicklist = new JMenu("ChannelOp");
    popup.add(menuModenicklist);
    // MenuBar -> Nicklist -> +Operator
    JMenuItem menuItemPosOpNicklistModes = new JMenuItem("+ Op");
    menuItemPosOpNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String nicks[] = chanPanel.getSelectedNicks();
          char[] modes = new char[nicks.length+1];
          modes[0] = '+';
          for (int i = 1; i < modes.length; i++)
            modes[i] = 'o';
          conn.doMode(chanPanel.getWindowName(), new String(modes) +" "+ 
              IRCUtil.formatArray(nicks, ' '));
        }
      } 
    } );
    menuModenicklist.add(menuItemPosOpNicklistModes);
    // MenuBar -> Nicklist -> -Operator
    JMenuItem menuItemNegOpNicklistModes = new JMenuItem("- Op");
    menuItemNegOpNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String nicks[] = chanPanel.getSelectedNicks();
          char[] modes = new char[nicks.length+1];
          modes[0] = '-';
          for (int i = 1; i < modes.length; i++)
            modes[i] = 'o';
          conn.doMode(chanPanel.getWindowName(), new String(modes) +" "+ 
              IRCUtil.formatArray(nicks, ' '));
        }
      } 
    } );
    menuModenicklist.add(menuItemNegOpNicklistModes);
    menuModenicklist.addSeparator();
    // MenuBar -> Nicklist -> +Voice
    JMenuItem menuItemPosVoiceNicklistModes = new JMenuItem("+ Voice");
    menuItemPosVoiceNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String nicks[] = chanPanel.getSelectedNicks();
          char[] modes = new char[nicks.length+1];
          modes[0] = '+';
          for (int i = 1; i < modes.length; i++)
            modes[i] = 'v';
          conn.doMode(chanPanel.getWindowName(), new String(modes) +" "+ 
              IRCUtil.formatArray(nicks, ' '));
        }
      } 
    } );
    menuModenicklist.add(menuItemPosVoiceNicklistModes);
    // MenuBar -> Nicklist -> -Voice
    JMenuItem menuItemNegVoiceNicklistModes = new JMenuItem("- Voice");
    menuItemNegVoiceNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String nicks[] = chanPanel.getSelectedNicks();
          char[] modes = new char[nicks.length+1];
          modes[0] = '-';
          for (int i = 1; i < modes.length; i++)
            modes[i] = 'v';
          conn.doMode(chanPanel.getWindowName(), new String(modes) +" "+ 
              IRCUtil.formatArray(nicks, ' '));
        }
      } 
    } );
    menuModenicklist.add(menuItemNegVoiceNicklistModes);
    
    menuModenicklist.addSeparator();
    
    // MenuBar -> Nicklist -> Kick
    JMenuItem menuItemKickNicklistModes = new JMenuItem("Kick");
    menuItemKickNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String nicks[] = chanPanel.getSelectedNicks();
          String chan = chanPanel.getWindowName();
          for (int i = 0; i < nicks.length; i++)
            conn.doKick(chan, nicks[i]);
        }
      } 
    } );
    menuModenicklist.add(menuItemKickNicklistModes);
    
    // MenuBar -> Nicklist -> Modes -> Kick (Msg)
    JMenuItem menuItemKickMsgNicklistModes = new JMenuItem("Kick (Msg)");
    menuItemKickMsgNicklistModes.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String[] nicks = chanPanel.getSelectedNicks();
          String chan = chanPanel.getWindowName();
          if (nicks.length == 0)
            return;
          String msg = JOptionPane.showInputDialog(null, "Kickmessage?");
          if (msg == null)
            msg = "";
          for (int i = 0; i < nicks.length; i++) 
            conn.doKick(chan, nicks[i]);
        }
      } 
    } );
    menuModenicklist.add(menuItemKickMsgNicklistModes);
   
    // MenuBar -> Nicklist -> Whois
    JMenuItem menuItemWhoinicklist = new JMenuItem("Whois");
    menuItemWhoinicklist.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) { 
          ChanPanel chanPanel = (ChanPanel)component;
          String[] nicks = chanPanel.getSelectedNicks();
          if (nicks.length > 0)
            for (int i = 0; i < nicks.length; i++)
              conn.doWhois(nicks[i]);
          else {
            String nick = mainFrame.getSelection();
            if (nick != null)
              conn.doWhois(nick);
          }
        }
      } 
    } );
    popup.add(menuItemWhoinicklist);

    return popup;
  }

}

