package irc.ui.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.*;
import irc.core.*;

/** 
 * This class is the base class for <code>ConsolePanel</code>, 
 * <code>ChanPanel</code> and <code>QueryPanel</code>. 
 * 
 * @author Luke
 * 
 */
public abstract class PanelTemplate extends JPanel {

  /**
   * The owning class.
   */
  protected IRCMainFrame mainFrame;

  /**
   * The connection to the IRC of the owning class.
   */
  protected IRCMain conn;

  /** 
   * The name of the window. 
   */
  protected String name;

  /**
   * The <code>DefaultStyledDocument</code> which contains the chat-text.
   */
  private DefaultStyledDocument doc;

  /**
   * The <code>AttributeSet</code> contains the foreground.
   */
  private SimpleAttributeSet attrset;

  /**
   * The <code>JTextPane</code> holds the document and shows the text.
   */
  protected JTextPane text = new JTextPane();

  /** 
   * The command line of the user. 
   */
  protected JTextField input = new JTextField();

  /** 
   * The textfield of the topic on top of the chat-text. 
   */
  protected JTextField topicfield = new JTextField();

  /** 
   * The part button next to the topic-field. 
   */
  protected JButton part = new JButton("");

  /** 
   * The scrollPane of the chat-textpane.
   */
  protected JScrollPane scrollpane;

  /**
   * The <code>JPopupMenu</code> is used for the right-click menu of the 
   * textfield.
   */
  private JPopupMenu selectionPopup = getSelectionPopupMenu();

  /**
   * The <code>JPopupMenu</code> is used for the right-click menu of the
   * inputline.
   */
  private JPopupMenu inputLinePopup = getInputLinePopupMenu();

  /** 
   * The filewriter logs the chat-text of queries and channels. 
   */
  protected FileWriter logger;

  /** 
   * The last ten commands of the command-line of the current window. 
   */
  private String[] cmdHistory = { "", "", "", "", "", "", "", "", "", "" };

  /** 
   * The current history index, used at the scrolling function with the 
   * arrow-buttons of the keyboard. 
   */
  private int currentCmdHistoryIndex = -1;

  /** 
   * The scrollbar can be locked.<br />
   * This is useful to mark something and scroll etc..<br />
   * <code>false</code> is default and means the scrollbar is unlocked. 
   */
  private boolean lockedScrollbar = false;

  /** 
   * This is the amount of characters in the textpane of the chat-text.<br />
   * The channel starts empty with 0 chars text. 
   */
  int length = 0;

// ------------------------------

  /** 
   * Constructor of the <code>IRCPanel</code> sets swing-items which are the 
   * same for all types of windows. 
   * @param owner The owning class.
   * @param name The channel's name.
   */
  public PanelTemplate(IRCMainFrame owner, String name) {
    setLayout(new BorderLayout());
    mainFrame = owner;
    this.name = name;
    conn = owner.getIRCConnection();
    doc = new DefaultStyledDocument();
    attrset = new SimpleAttributeSet();
    text.setDocument(doc);
    text.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    text.setEditable(false);
    text.setBackground(mainFrame.getBgColor());
    text.add(selectionPopup);
    input.setBackground(mainFrame.getBgColor());
    input.setForeground(mainFrame.getOwnColor());
    input.setCaretColor(mainFrame.getOwnColor());
    input.add(inputLinePopup);
    scrollpane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    text.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) { 
        int clickCount = e.getClickCount();
        int button = e.getButton();
        if (clickCount == 1 && button == MouseEvent.BUTTON1) {
          requestFocus();
        } else if (clickCount == 1 && button == MouseEvent.BUTTON3) {
          selectionPopup.show(text, e.getX(), e.getY());
        }
      }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }
    } ); 

    input.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) { 
        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
          inputLinePopup.show(input, e.getX(), e.getY());
        }
      }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }
    } ); 

    input.addKeyListener(new KeyListener() { 
      public void keyTyped(KeyEvent e) { }
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) { 
          if (currentCmdHistoryIndex < cmdHistory.length - 1) { // scroll up
            input.setText(cmdHistory[++currentCmdHistoryIndex]);
          } else { // if we are over the maximum, just set an empty line
            resetLine();
            currentCmdHistoryIndex = -1;  
          }
        } else if (key == KeyEvent.VK_DOWN) { 
          if (currentCmdHistoryIndex > 0) { // scroll down
            input.setText(cmdHistory[--currentCmdHistoryIndex]); 
          } else { // if we are at the bottom, delete the line
            resetLine();
            currentCmdHistoryIndex = -1;
          }
        }
      }
      public void keyReleased(KeyEvent e) { }
    } ); 
      
    add(makeNorth(), BorderLayout.NORTH);
    add(input, BorderLayout.SOUTH);
  }

// ------------------------------

  /** 
   * Creates the content of the northern area with the part-button and the 
   * topic-textfield and topic-change-button. 
   * @return The northern box.
   */
  private Box makeNorth() {
    Box boxNorth = Box.createHorizontalBox();
    boxNorth.add(topicfield);
    part.setIcon(new ImageIcon(PanelTemplate.class.getResource("/irc/ui/resources/cross.png")));
    part.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
    	}
    });
    boxNorth.add(part);
    return boxNorth;
  }

  
// ------------------------------

  /** 
   * Changes the name of the window. 
   * @param name The new window name.
   */
  public void setWindowName(String name) {
    this.name = name;
  }
  
// ------------------------------

  /** 
   * Returns the name of the window and overwrites the 
   * <code>Component.getName</code>-method. 
   * @return The window's name.
   */
  public String getWindowName() {
    return name;
  }

// ------------------------------

  /** 
   * Requests the focus for the textfield <code>input</code>. 
   */
  public void requestFocus() { 
    input.requestFocus(); 
  }

// ------------------------------

  /** 
   * It removes all content from the command-line. 
   */
  protected void resetLine() {
    input.setText("");
  }

// ------------------------------

  /** 
   * Appends something to the textarea.<br />
   * The nick is not put in front of the message.<br />
   * Additionally it cuts the upper part, if the channel-text gets to long 
   * (<code>maxTextLength</code>). 
   * @param line The new line.
   * @param c The line's color.
   */
  protected void updateText(String line, Color c) {
    updateText(line, c, false);
  }

// ------------------------------

  /** 
   * Appends something to the textarea.<br />
   * The boolean stands for &lt;Nick&gt; in front (<code>true</code>) of the 
   * message or not (<code>false</code>).<br />
   * Additionally it cuts the upper part, if the channel-text gets to long 
   * (<code>maxTextLength</code>). 
   * @param line The new line.
   * @param c The line's color.
   * @param printNick If <code>true</code> the nick of us is printed, too.
   */
  protected void updateText(String line, Color c, boolean printNick) {
    if (printNick) 
      line = "<"+ conn.getNick() +"> "+ line; 
    line = "["+ IRCUtil.TIMESTAMP.format(new Date()) +"] "+ line +'\n'; 

    StyleConstants.setForeground(attrset, c);
    try {
      doc.insertString(length, line, attrset);
    } catch (Exception exc) {
      exc.printStackTrace();
    }

    length += line.length();
    if (!lockedScrollbar && text.getSelectedText() == null) 
      text.setCaretPosition(length);

    // channel-cut
    if (mainFrame.getCutChannelText() && length > mainFrame.getMaxTextLength()) { 
      int cutIndex = length / 2;
      try {
        cutIndex = doc.getText(0, doc.getLength()).indexOf('\n', cutIndex);
        doc.remove(0, cutIndex);
      } catch (Exception exc) {
        exc.printStackTrace();
      }
      length = doc.getLength();
      text.setCaretPosition(length); 
    }

    this.currentCmdHistoryIndex = -1;

  }

// ------------------------------

  /** 
   * Returns selected text of the <code>IRCPanel</code>. 
   * @return The selected string, or <code>""</code> if nothing is selected.
   */
  public String getSelection() {
    String selection = text.getSelectedText();
    removeSelection();
    if (selection != null) {
      return selection;
    } else {
      return "";
    }
  }

// ------------------------------

  /**
   * Removes the selection in the textframe.
   */
  public void removeSelection() {
    //text.select(length, length);
    text.setCaretPosition(length);
  }

// ------------------------------

  /** 
   * Puts the selection into the clipboard. 
   */
  public void copySelection() {
    text.copy();
  }

// ------------------------------

  /** 
   * Puts the content of the clipboard into the text-line. 
   */
  public void pasteLine() {
    input.paste();
  }

// ------------------------------

  /** 
   * Closes the channel-window and especially the logging-FileWriter. 
   */
  public void close() {
    try {
      logger.close();
    } catch (Exception exc) {
      // nothing
    }
    try {
      this.finalize();
    } catch (Throwable thr) {
      thr.printStackTrace();
    }
  }

// ------------------------------

  /** 
   * Used to lock the scrollbar.<br />
   * Sets the <code>lockedScrollbar</code> inner-class-var. 
   * @param lock If <code>true</code>, the scrollbar is locked.
   */
  public void lockScrollbar(boolean lock) {
    lockedScrollbar = lock;
  }

// ------------------------------

  /** 
   * Adds a command to the list of stored commands.<br />
   * These commands are called with the !-command by the user and parsed by 
   * <code>parseHistoryCmd</code>. <br />
   * All commands are shifted by one. The positions in the array shift as 
   * follows: 0 -&gt; 1, 1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 4 -&gt; 5, ...,
   * 9 -&gt; 10, 10 -&gt; OUT.
   * @param cmd The command which is to add to the history.
   */
  protected void addCmdToHistory(String cmd) {
    String[] cmdHistoryTemp = new String[cmdHistory.length];
    for (int i = 0; i < cmdHistory.length-1; i++) 
      cmdHistoryTemp[i + 1] = cmdHistory[i]; 
    cmdHistoryTemp[0] = cmd; // save last cmd as first one
    this.cmdHistory = cmdHistoryTemp;
  }

// ------------------------------

  /**
   * Generates a new <code>JPopupMenu</code> for the right-click popup of the
   * <code>IRCPanel</code> text field. 
   * @return The selection <code>JPopupMenu</code>.
   */
  private JPopupMenu getSelectionPopupMenu() {
    JPopupMenu popup = new JPopupMenu("Selection");
    // MenuBar -> Selection -> Copy
    JMenuItem menuItemCopy = new JMenuItem("Copy");
    menuItemCopy.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        copySelection();
      } 
    } );
    popup.add(menuItemCopy);
    // MenuBar -> Selection -> Paste
    JMenuItem menuItemPaste = new JMenuItem("Paste");
    menuItemPaste.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        pasteLine(); 
      } 
    } );
    popup.add(menuItemPaste);
    popup.addSeparator();
   
    // MenuBar -> Selection -> Join
    JMenuItem menuItemJoinSelection = new JMenuItem("Join (Channel)");
    menuItemJoinSelection.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        if (!mainFrame.isConnected())
          return;
        String chan = mainFrame.getSelection().trim();
        if (IRCUtil.isChannel(chan)) {
          conn.doJoin(chan); 
        } else if (chan.length() > 0) {
          conn.doJoin("#"+ chan);
        }
      } 
    } );
    popup.add(menuItemJoinSelection);
    // MenuBar -> Selection -> Whois
    JMenuItem menuItemWhoisUser = new JMenuItem("Whois (User)");
    menuItemWhoisUser.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        if (!mainFrame.isConnected())
          return;
        conn.doWhois(mainFrame.getSelection()); 
      } 
    } );
    popup.add(menuItemWhoisUser);
    popup.addSeparator();
    // MenuBar -> Selection -> ChanCenter
    JMenuItem menuItemChanCenter = new JMenuItem("Control Center");
    menuItemChanCenter.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel) 
          ((ChanPanel)component).openControlCenter();
      }
    } );
    popup.add(menuItemChanCenter);
    JMenuItem menuItemPasteDialog = new JMenuItem("Full Message");
    menuItemPasteDialog.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Component component = mainFrame.getSelectedComponent();
        if (component instanceof ChanPanel || component instanceof QueryPanel) {
          String chan = ((PanelTemplate)component).getWindowName();
          new PasteDialog(mainFrame, chan);
        }
      }
    } );
    popup.add(menuItemPasteDialog);
    return popup;
  }

// ------------------------------

  /**
   * Generates a new <code>JPopupMenu</code> for the right-click popup of the
   * <code>IRCPanel</code>'s input line. 
   * @return The inputline <code>JPopupMenu</code>.
   */
  private JPopupMenu getInputLinePopupMenu() {
    JPopupMenu popup = new JPopupMenu("Input");
    // MenuBar -> Selection -> Copy
    JMenuItem menuItemCopy = new JMenuItem("Copy");
    menuItemCopy.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        input.copy(); 
      } 
    } );
    popup.add(menuItemCopy);
    // MenuBar -> Selection -> Cut
    JMenuItem menuItemCut = new JMenuItem("Cut");
    menuItemCut.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        input.cut(); 
      } 
    } );
    popup.add(menuItemCut);
    // MenuBar -> Selection -> Paste
    JMenuItem menuItemPaste = new JMenuItem("Paste");
    menuItemPaste.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        pasteLine(); 
      } 
    } );
    popup.add(menuItemPaste);
    popup.addSeparator();
    // MenuBar -> Selection -> Select All
    JMenuItem menuItemSelectAll = new JMenuItem("Select All");
    menuItemSelectAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        input.selectAll();
      }
    } );
    popup.add(menuItemSelectAll);
    return popup;
  }

}