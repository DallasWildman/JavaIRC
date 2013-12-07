package irc.ui.view;

import java.awt.Component;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import irc.core.*;

/** 
 * Listener for IRC servers' messages. 
 * It extends <code>IRCEventAdapter</code> and implements 
 * <code>IRCEventListener</code> which are both classes of the IRClib.
 */
public class Listener extends IRCEventAdapter implements IRCEventListener {
	
  // -------------------------------
  // Fields
  // -------------------------------

  /* Owner Class */
  private IRCMainFrame mainFrame;

  /* The owning class's <code>IRCMain</code> */
  private IRCMain conn;

  /** 
   * If <code>true</code>, the connection is registered. Before it is 
   * <code>false</code>. This <code>boolean</code> is very exact, because; 
   * more exact than <code>IRCmainFrame.isConnected()</code>. 
   * <code>registered</code> doesn't need to be a class-field of 
   * <code>IRCmainFrame</code>, because there such an exact variable isn't needed. 
   * Here it is needed to handle numeric error replies which are received 
   * _before_ the connection is registered. 
   */
  private boolean registered = false;

  /** 
   * Indicates if the nick was already checked. The nick is checked when the 
   * first reply is received. The nickname might be changed by the server 
   * because of to many characters or invalid characters. 
   */
  private boolean nickChecked = false;

// ------------------------------

  /**
   * Creates a new listener instance.
   * @param owner The owning <code>IRCmainFrame</code> instance.
   */
  public Listener(IRCMainFrame owner) {
    mainFrame = owner;
    conn = owner.getIRCConnection();
  }
  
  /*
   * Special field for collecting LIST data
   */
  private ArrayList<String> listOut = new ArrayList<String>();

  //-------------------------------
  // Listenning Events
  // -------------------------------
  
  /** 
   * Fired when the client is connected.<br />
   * The <code>Listener.registered</code> is set <code>true</code>
   * and the perform is executed with <code>executePerform</code>.
   */
  public void onRegistered() {
    registered = true;
    mainFrame.executePerform();
  }

// ------------------------------

  /** 
   * Fired when the client is disconnected. <br />
   * The <code>disconnect</code> method is invoked.
   */
  public void onDisconnected() {
    mainFrame.disconnect();
  }
  
// ------------------------------

  /** 
   * Fired when errors are received. This does not treat error replies!<br />
   * The error is just printed.
   * @param msg The errormessage.
   */
  public void onError(String msg) {
    int index = mainFrame.getSelectedIndex();
    String line = "* Error: "+ msg;
    mainFrame.updateTab(index, line);
  }

// ------------------------------

  /** 
   * Fired when errors or numeric error replies are received. <br />
   * The numeric error and the message are printed. If it's a nickname-error
   * and the connection isn't registered yet, the user is asked for a new
   * nickname.
   * @param num The error's number.
   * @param msg The errormessage.
   */
  public void onError(int num, String msg) {
    int index = mainFrame.getSelectedIndex();
    String line;
    line = "* Error: #"+ num +": "+ msg;
    mainFrame.updateTab(index, line);

    if ((num == IRCUtil.ERR_NICKNAMEINUSE || num == IRCUtil.ERR_ERRONEUSNICKNAME
      || num == IRCUtil.ERR_NONICKNAMEGIVEN) && (!registered)) { 
      String nickNew = JOptionPane.showInputDialog(null, "Nickname?");
      if (nickNew != null && nickNew.length() != 0) {
        conn.doNick(nickNew);
        mainFrame.setNick(nickNew);
      } else {
        conn.doQuit();
      }
    }
  }

// ------------------------------

  /** 
   * Fired when somebody gets invited.<br />
   * The information is just printed out. 
   * @param chan The channel.
   * @param user The active user.
   * @param nickPass The invited user.
   */
  public void onInvite(String chan, IRCUserInfo user, String nickPass) {
    String nickAct = user.getNick();
    int index = mainFrame.getSelectedIndex();
    String line = "* Invite: "+ nickPass +" is invited by "+ nickAct 
        +" to "+ chan;
    mainFrame.updateTab(index, line);
  }

// ------------------------------

  /** 
   * Fired when somebody joins a channel. <br />
   * The user is added. <br />
   * If the joining user is us, we don't add us, because this is done 
   * when we receive the name-list of the channel automatically.
   * The modes bans are requested and all old bans are removed (which is 
   * useful if we were kicked and rejoined). 
   * @param chan The channel someone is joining.
   * @param user The joining user.
   */
  public void onJoin(String chan, IRCUserInfo user) {
    String nick = user.getNick();
    String username = user.getUsername();
    String host = user.getHost();
    String line = "* Join: "+ nick +" ("+ username +"@"+ host +") joins "+ 
        chan;
    mainFrame.updateTab(chan, line);
    if (!nick.equals(conn.getNick())) { 
      mainFrame.addNick(chan, nick);
    } else { 
      //TODO: BAN 
      //mainFrame.removeAllBans(chan); 
      conn.doMode(chan);
      conn.doMode(chan, "+b"); 
    }
  }

// ------------------------------

  /** 
   * Fired when somebody is kicked from a channel.<br />
   * The kicked user is removed from the nicklist. <br />
   * If the kicked user is us, the channel is not closed and the bans are 
   * not removed so that the user can check who kicked him and wether he 
   * is banned. But, of course, the nicknames are all removed from the 
   * nicklist.
   * @param chan The channel someone is kicked from.
   * @param user The kicking user.
   * @param nickPass The kicked user.
   * @param msg The kickmessage, <code>""</code> if empty.
   */
  public void onKick(String chan, IRCUserInfo user, String nickPass, String msg) {
    String nickAct = user.getNick();
    String line = "* Kick: "+ nickPass +" is kicked by "+ nickAct +" from "+ 
        chan;
    if (msg.length() > 0) line += " ("+ msg +")";
    mainFrame.updateTab(chan, line);
    mainFrame.removeNick(chan, nickPass);
    if (nickPass.equals(conn.getNick())) {
      mainFrame.updateTab(mainFrame.getSelectedIndex(), "You are kicked from "+ chan);
      mainFrame.removeAllNicks(chan); 
    }
  }

// ------------------------------

  /** 
   * Fired when a private message is received.<br />
   * The message is printed to the channel, or if it's sent to us
   * as a query, it's printed to the query. If the query isn't open
   * yet, it's opened now. Additionally the tab is highlighted. If
   * soundhighlighting is enabled and the line contains our nickname,
   * a beep is requested.
   * @param chan The channel.
   * @param user The user who sent the message.
   * @param msg The message itself.
   */
  public void onPrivmsg(String chan, IRCUserInfo user, String msg) {
    String nick = user.getNick();
    String line = "<"+ nick +"> "+ msg;
    if (IRCUtil.isChannel(chan)) {
      mainFrame.updateTab(chan, line); 
      mainFrame.doTabHighlight(chan); 
    } else {
      mainFrame.updateTab(nick, line); 
      mainFrame.doTabHighlight(nick); 
    }
    if (mainFrame.getNickSoundHighlight() 
      && new String(" "+ msg +" ").indexOf(" "+ conn.getNick() +" ") != -1 
      || msg.startsWith(conn.getNick() +": ")) { 
      mainFrame.doSoundHighlight();
    }
  }

// ------------------------------

  /** 
   * Fired when a mode is set.<br />
   * It analyzes the modes and decides what to do. For example, it updates
   * the nicklist by adding or removing a <code>@</code> or <code>+</code>
   * for opers and voiced users. Or it updates the modes like <code>i</code>,
   * <code>k</code> etc.
   * @param chan The channel.
   * @param user The user who is setting modes.
   * @param modeParser An <code>IRCModeParser</code> object which gives 
   *                   access to the modes.
   */
  public void onMode(String chan, IRCUserInfo user, IRCModeParser modeParser) {
    String nickAct = user.getNick();
    int index = mainFrame.indexOfTab(chan);
    String line = "* Mode: "+ nickAct +" sets mode: "+ modeParser.getLine();
    mainFrame.updateTab(index, line);
    int iLength = modeParser.getCount();
    char mode; // [o|p|s|i|t|n|b|v]
    char operator; // [+|-]
    String arg;
    String removedNickPrefix; // is @ for operator or + for visible
    for (int i = 1; i <= iLength; i++) {
      operator = modeParser.getOperatorAt(i);
      mode = modeParser.getModeAt(i);
      arg = modeParser.getArgAt(i);
      if (mode == 'o' || mode == 'v') { 
        removedNickPrefix = mainFrame.removeNick(index, arg); 
        if ((operator == '+' && mode == 'o') 
          || (mode != 'o' && removedNickPrefix.equals("@"))) { 
          mainFrame.addNick(index, "@"+ arg);
        } else if (operator == '+' && mode == 'v') { 
          mainFrame.addNick(index, "+"+ arg);
        } else { 
          mainFrame.addNick(index, arg);
        }
      } else if (mode == 'b') {
        if (operator == '+') {
        //TODO: BAN
          mainFrame.addBan(index, arg, nickAct, System.currentTimeMillis());
        } else if (operator == '-') {
        	//TODO: BAN
          mainFrame.removeBan(index, arg);
        }
      } else if (mode == 'i' || mode == 'k' || mode == 'l' || mode == 'm' 
          || mode == 'n' || mode == 'p' || mode == 's' || mode == 't') { 
        mainFrame.updateMode(index, operator, mode, arg);
      }
    }
  }

// ------------------------------

  /** 
   * Fired when a user-mode is set. <br />
   * Just prints out the modes.
   * @param user The user who is setting modes.
   * @param nickPass The passive user.
   * @param modes The modes.
   */
  public void onMode(IRCUserInfo user, String nickPass, String modes) {
    String nickAct = user.getNick();
    int index = mainFrame.getSelectedIndex();
    String line = "* Mode: "+ nickAct +" sets mode: "+ modes +" "+ nickPass;
    mainFrame.updateTab(index, line);
  }

// ------------------------------

  /** 
   * Fired when a nickname is changed. <br />
   * It updates the nicklist and, if a query is open, the query's title.
   * @param user The user who changes his nickname.
   * @param nickNew His new nickname.
   */
  public void onNick(IRCUserInfo user, String nickNew) {
    String nick = user.getNick();
    if (nickNew.equals(conn.getNick()))
      mainFrame.setNick(nickNew);
    String line = "* Nick: "+ nick +" is now known as "+ nickNew;
    int tabsAmount = mainFrame.getTabCount();
    for (int i = 0; i < tabsAmount; i++) {
      Component component = mainFrame.getComponentAt(i);
      if (component instanceof ChanPanel) {
        String status = mainFrame.removeNick(i, nick); 
        if (status != null) { 
          mainFrame.addNick(i, status + nickNew);
          mainFrame.updateTab(i, line);
        }
      } else if (component instanceof QueryPanel && 
                 mainFrame.getTitleAt(i).equals(nick)) {
        mainFrame.setTitleAt(i, nickNew); 
        ((PanelTemplate)component).setWindowName(nickNew); 
        mainFrame.setSelectedTab(); 
      }
    }
  }

// ------------------------------

  /** 
   * Fired when a notice is received. <br />
   * Just prints out the message.
   * @param target The user or group who received the message.
   * @param user The user who sends a <code>NOTICE</code>.
   * @param msg The message itself.
   */
  public void onNotice(String target, IRCUserInfo user, String msg) {
    String nick = user.getNick();
    int index = mainFrame.getSelectedIndex();
    String line = "* Notice: "+ nick +" to "+ target +": "+ msg;
    mainFrame.updateTab(index, line); 
  }

// ------------------------------

  /** 
   * Fired when somebody parts from a channel. <br />
   * Updates the nicklist and prints a message.
   * @param chan The channel.
   * @param user The parting user.
   * @param msg His partmessage, <code>""</code> if nothing set.
   */
  public void onPart(String chan, IRCUserInfo user, String msg) {
    String nick = user.getNick();
    String username = user.getUsername();
    String host = user.getHost();
    if (msg.length() > 0)
      msg = " ("+ msg +")";
    String line = "* Part: "+ nick +" ("+ username +"@"+ host +") parts "+ 
        chan + msg;
    mainFrame.updateTab(chan, line);
    mainFrame.removeNick(chan, nick);
    if (nick.equals(conn.getNick()))
      mainFrame.closePanel(chan);
  }

// ------------------------------

  /** 
   * Fired when the server requests a PONG with his PING. <br />
   * Answers with a <code>PONG</code> and prints out a notice.
   * @param ping The ping.
   */
  public void onPing(String ping) {
    conn.doPong(ping);
    mainFrame.updateTab(IRCUtil.CONSOLEWINDOWINDEX, "Ping? Pong!");
  }

// ------------------------------

  /** 
   * Fired when somebody quits. <br />
   * Updates the nicklist and prints a message.
   * @param user The quitting user.
   * @param msg His quitmessage or <code>""</code> if noone was set.
   */
  public void onQuit(IRCUserInfo user, String msg) {
    String nick = user.getNick();
    String username = user.getUsername();
    String host = user.getHost();
    if (msg.length() > 0)
      msg = "("+ msg +")";
    String line = "* Quit: "+ nick +" ("+ username +"@"+ host +") quits "+ msg;
    int iTabsAmount = mainFrame.getTabCount();
    for (int i = 0; i < iTabsAmount; i++) {
      Component component = mainFrame.getComponentAt(i);
      if (component instanceof ChanPanel) {
        String status = mainFrame.removeNick(i, nick); 
        if (status != null) 
          mainFrame.updateTab(i, line);
      }
    }
  }

// ------------------------------

  /** 
   * Fired when a numeric reply is received. <br />
   * Treats the channelmodes. In every case the mode is printed to consle,
   * and in some special cases like <code>WHOIS</code> it's also printed to
   * the on-top panel.
   * @param num The reply's number.
   * @param value The reply's value.
   * @param msg The reply's message.
   */
  public void onReply(int num, String value, String msg) {

    if (num == IRCUtil.RPL_CHANNELMODEIS) { // mode <chan> -> channelmodes

      StringTokenizer stValue = new StringTokenizer(value +" "+ msg);
      stValue.nextToken();
      String chan = stValue.nextToken();
      int index = mainFrame.indexOfTab(chan);
      String modes = stValue.nextToken();
      while (stValue.hasMoreTokens())
        modes += " "+ stValue.nextToken();
      mainFrame.updateModes(index, modes);

    } else if (num == IRCUtil.RPL_BANLIST) { // who -> add nicks to nicklist

      StringTokenizer stValue = new StringTokenizer(value);
      stValue.nextToken(); // jump over the first (it is our name)
      String chan = stValue.nextToken();
      int index = mainFrame.indexOfTab(chan);
      String banmask = stValue.nextToken();
      String nick = stValue.nextToken();
      long time;
      try {
        time = Long.parseLong(msg) * 1000;
      } catch (Exception exc) {
        time = 0;
      }

    } else if (num == IRCUtil.RPL_NAMREPLY) { // who -> add nicks to nicklist

      StringTokenizer stValue = new StringTokenizer(value);
      stValue.nextToken(); // jump over the first (it is our name)
      stValue.nextToken(); // jump over the second (it is a '@', '*' or '=')
      String chan = stValue.nextToken();
      int index = mainFrame.indexOfTab(chan);
      if (index != -1) { 
        StringTokenizer stNicks = new StringTokenizer(msg);
        String[] nicks = new String[stNicks.countTokens()];
        for (int i = 0; stNicks.hasMoreTokens(); i++)
          nicks[i] = stNicks.nextToken();
        mainFrame.addNicks(chan, nicks);
      }

    } else if (num == IRCUtil.RPL_TOPIC) { // on-join topic

      StringTokenizer stValue = new StringTokenizer(value);
      stValue.nextToken(); // jump over the first (it is our name)
      String chan = stValue.nextToken();
      int index = mainFrame.indexOfTab(chan);
      String line = "* Topic: "+ msg; 
      if (index != -1) { // echo in channel and update topic field
        mainFrame.updateTab(index, line); 
        ((ChanPanel)mainFrame.getComponentAt(index)).setTopicField(msg); 
      }

    } else if (num == IRCUtil.RPL_TOPICINFO) { // topic set by ... at ...

      StringTokenizer stTokennickAndDate = new StringTokenizer(value);
      stTokennickAndDate.nextToken(); // the first one is our name. jump it.
      String chan = stTokennickAndDate.nextToken(); 
      String nick = stTokennickAndDate.nextToken(); 
      long topicTime = Long.parseLong(msg) * 1000; // it's given in seconds!!
      String sDate = new Date(topicTime).toString(); 
      String line = "* Topic: set by "+ nick +" on "+ sDate; 
      int index = mainFrame.indexOfTab(chan);
      if (index != -1) 
        mainFrame.updateTab(index, line); 

    } else if (num == IRCUtil.RPL_AUTHNAME || num == IRCUtil.RPL_WHOISUSER || 
      num == IRCUtil.RPL_WHOISOPERATOR || num == IRCUtil.RPL_WHOISSERVER || 
      num == IRCUtil.RPL_WHOISCHANNELS || num == IRCUtil.RPL_AWAY || 
      num == IRCUtil.RPL_WHOISIDLE || num == IRCUtil.RPL_ENDOFWHOIS) {

      String line;
      int indexOfFirstSpace = value.indexOf(' ');
      int indexOfSecondSpace = value.indexOf(' ', indexOfFirstSpace + 1);
      try {
        String nick = value.substring(indexOfFirstSpace + 1, 
            indexOfSecondSpace);
        String param = value.substring(indexOfSecondSpace + 1); 
        if (num == IRCUtil.RPL_WHOISIDLE) { // parse time and the idle-seconds 
          StringTokenizer stWhoisIdle = new StringTokenizer(param);
          String idleSeconds = stWhoisIdle.nextToken();
          long signOnTime = Long.parseLong(stWhoisIdle.nextToken()) * 1000; 
          line = nick +": "+ idleSeconds +" "+ msg +" "+ 
              new Date(signOnTime).toString();
        } else { // normal whois handling
          line = nick +": "+ msg +" "+ param;
        }
      } catch (Exception exc) {
        String nick = value.substring(indexOfFirstSpace + 1);
        line = nick +": "+ msg;
      }
      int index = mainFrame.getSelectedIndex();
      mainFrame.updateTab(index, line); 
      
      //BAN MASK IS NOT SUPPORTED BY OUR SERVER AT THIS TIME
      
    } else if (num == IRCUtil.RPL_NOWAWAY || num == IRCUtil.RPL_UNAWAY) { // away&back

      int index = mainFrame.getSelectedIndex();
      String line = msg;
      mainFrame.updateTab(index, line);
      
    }


    // check nick's length & characters; the value _is_ or _contains_ the 
    // (possibly changed) nickname
    if (!nickChecked) {
      int spaceIndex = value.indexOf(' ');
      String nickNew;
      if (spaceIndex != -1)
        nickNew = value.substring(0, spaceIndex);
      else 
        nickNew = value;
      if (!mainFrame.getNick().equals(nickNew)) 
        mainFrame.setNick(nickNew);
      nickChecked = true; // don't check & cut anymore
    }
    
	// <MRW> List command sent start constructing a list
	if(num == IRCNumericReplies.RPL_LISTSTART)
		listOut.clear();

    // print out info to console <MRW>(Unless a list command is sent)
    if(num != IRCNumericReplies.RPL_LIST){
    	String line;
    	line = "* Reply: #"+ num +": Msg: "+ msg; 
    	mainFrame.updateTab(IRCUtil.CONSOLEWINDOWINDEX, line);
    	line = "* Reply: #"+ num +": Value: "+ value;
    	mainFrame.updateTab(IRCUtil.CONSOLEWINDOWINDEX, line);}
    else{
    	//mainFrame.updateTab(IRCUtil.CONSOLEWINDOWINDEX, value);
    	listOut.add(value.substring(value.indexOf(' ')));
    }
    
    //<MRW> Show a dialog featuring the list of channels.
    if(num == IRCNumericReplies.RPL_LISTEND){
    	String input = (String) JOptionPane.showInputDialog(mainFrame, "", "Choose From a list of channels",
    			JOptionPane.PLAIN_MESSAGE, new ImageIcon(IRCMainFrame.class.getResource("/irc/ui/resources/user_add.png")),
    			listOut.toArray(), listOut.get(0));
		mainFrame.updateChannels(listOut);
    	if(mainFrame.isConnected() && input != null)
    		conn.doJoin(input.split(" ")[1]);
    }
    
  }

// ------------------------------

  /** 
   * Fired when the topic is changed. <br />
   * Prints a message to the channel.
   * @param chan The channel.
   * @param user The user who changes the topic.
   * @param topic The new topic.
   */
  public void onTopic(String chan, IRCUserInfo user, String topic) {
    String nick = user.getNick();
    String line; 
    if (topic.length() > 0) 
      line = "* Topic: "+ nick +" changes topic into: "+ topic;
    else
      line = "* Topic: "+ nick +" removes the topic";
    mainFrame.updateTab(chan, line);
    Component component = mainFrame.getComponentAt(mainFrame.indexOfTab(chan));
    ((ChanPanel)component).setTopicField(topic);
  }

// ------------------------------

  /** 
   * This event is fired when the incoming line can not be identified as a known
   * event.
   * @param prefix The prefix of the incoming line.
   * @param command The command of the incoming line.
   * @param middle The part until the colon (<code>:</code>).
   * @param trailing The part behind the colon (<code>:</code>).
   */
  public void unknown(String prefix, String command, String middle,
                      String trailing) {
    String line = "Well, I received '"+ command +"' from the "+ 
        "server, but I dunno what to do with it...";
    mainFrame.updateTab(IRCUtil.CONSOLEWINDOWTITLE, line);
 }

}
