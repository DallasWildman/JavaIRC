package irc.ui.view;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;

/** 
 * The paste-dialog where the user can input long text which is pasted then.
 */
public class PasteDialog extends JDialog implements Runnable {

  /**
   * The owning class.
   */
  private IRCMainFrame mainFrame;

  /** 
   * The channel the text will be sent to. 
   */
  private String chan;

  /** 
   * The textarea for the input. 
   */
  private JTextArea input;


  /** 
   * The constructor creates the GUI of the paste-dialog. 
   * @param owner The owning <code>MoepIRC</code> instance.
   * @param chan The on-top console, channel or query.
   */
  public PasteDialog(IRCMainFrame owner, String chan) {
    super(owner, true);
    setTitle("Paste Dialog - "+ chan);
    this.mainFrame = owner;
    this.chan = chan;
    JButton okay = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    okay.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        sendInput();
        dispose();
      }
    } );
    cancel.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    } );

    JPanel bottom = new JPanel(); // child of the main panel
    bottom.add(okay);
    bottom.add(cancel);

    input = new JTextArea();
    JScrollPane scrollpane = new JScrollPane(input, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel center = new JPanel();
    center.setLayout(new GridLayout(1,1));
    center.add(scrollpane);

    JPanel main = new JPanel();
    main.setLayout(new BorderLayout());
    main.add(center, BorderLayout.CENTER);
    main.add(bottom, BorderLayout.SOUTH);
    getContentPane().add(main);

    setSize(275,300); 
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension mySize = getSize();
    if (mySize.height > screenSize.height)
      mySize.height = screenSize.height;
    if (mySize.width > screenSize.width)
      mySize.width = screenSize.width;
    int x = (screenSize.width - mySize.width)/2;
    int y = (screenSize.height - mySize.height)/2;
    setLocation(x, y); 
    setVisible(true);
  }
    
// ------------------------------

  /** 
   * Parses the content of <code>input</code> and sends it particular to the 
   * IRC. This is done by starting the thread via <code>this.start()</code>. 
   */
  private void sendInput() {
    new Thread(this).start();
  }

// ------------------------------

  /** 
   * The content of the thread scans the text of the <code>input</code> and 
   * sends it particular to the IRC. The thread sleeps after every line it 
   * send for <code>&lt;length of line&gt;<sup>1.5</sup> + 10 * &lt;number 
   * of line&gt; + 1250</code> milliseconds. 
   */
  public void run() {
    StringTokenizer stInput = new StringTokenizer(input.getText(), "\n");
    String line;
    int lineLength;
    for (int i = 0; stInput.hasMoreTokens(); i++) {
      line = stInput.nextToken();
      lineLength = line.length();
      if (lineLength > 0) {
        if (i != 0) {
            try {
              Thread.sleep((long)(Math.pow(lineLength, 1.5) + 10*i + 1250));
            } catch (Exception exc) {
              exc.printStackTrace();
            }
          }
        }
        if (line.charAt(0) == '/') 
          mainFrame.parseCmd(line);
        else
          mainFrame.parsePrivmsg(chan, line);
    }
  }

}
