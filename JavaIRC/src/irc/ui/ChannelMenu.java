package irc.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.List;
import javax.swing.JTextField;
import java.awt.Button;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import irc.core.IRCEventAdapter;
import irc.core.IRCEventListener;
import irc.core.IRCMain;
import irc.core.IRCModeParser;
import irc.core.IRCUserInfo;

public class ChannelMenu extends JFrame {
	private JTextField textField;
	private JTextField txtChannelNameHere;
	private JTextField txtSetPasswordHere;
	private JTextField txtLimit;
	private JCheckBox chckbxModerated;
	private JCheckBox chckbxSetBanMask;
	private JCheckBox chckbxNoMessagesTo;
	private JCheckBox chckbxSetUserLimit;
	private JCheckBox chckbxInviteOnly;
	private JCheckBox chckbxSetPasswordFor;
	private JCheckBox chckbxTopicSettableBy;
	private JCheckBox chckbxPrivChan;
	private JCheckBox chckbxSecretChannelFlag;
	private boolean initialClick = true;
	private IRCMain main;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChannelMenu frame = new ChannelMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public ChannelMenu() throws IOException {
		main = new IRCMain("irc://quakenet", new int[] {6667} , "", "dwildman", "", "");
		main.addIRCEventListener(new Listener());
		setBounds(100, 100, 450, 540);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		
		Panel joinChannel = new Panel();
		tabbedPane.addTab("Join", null, joinChannel, null);
		joinChannel.setLayout(null);
		
		List list = new List();
		list.setBounds(10, 10, 409, 212);
		joinChannel.add(list);
		
		textField = new JTextField();
		textField.setBounds(10, 228, 409, 37);
		joinChannel.add(textField);
		textField.setColumns(10);
		
		Button button = new Button("Join");
		button.setFont(new Font("Arial Black", Font.BOLD, 20));
		button.setBounds(10, 271, 121, 94);
		joinChannel.add(button);
		
		Panel createChannel = new Panel();
		tabbedPane.addTab("Create", null, createChannel, null);
		createChannel.setLayout(null);
		
		txtChannelNameHere = new JTextField();
		txtChannelNameHere.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(initialClick)
					txtChannelNameHere.setText("");
				initialClick = false;
			}
		});
		txtChannelNameHere.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtChannelNameHere.setText("Set Topic Here");
		txtChannelNameHere.setBounds(10, 11, 409, 28);
		createChannel.add(txtChannelNameHere);
		txtChannelNameHere.setColumns(10);
		
		txtSetPasswordHere = new JTextField();
		txtSetPasswordHere.setHorizontalAlignment(SwingConstants.CENTER);
		txtSetPasswordHere.setText("set password here");
		txtSetPasswordHere.setBounds(163, 255, 111, 20);
		createChannel.add(txtSetPasswordHere);
		txtSetPasswordHere.setColumns(10);
		
		txtLimit = new JTextField();
		txtLimit.setHorizontalAlignment(SwingConstants.CENTER);
		txtLimit.setText("limit");
		txtLimit.setBounds(95, 203, 58, 20);
		createChannel.add(txtLimit);
		txtLimit.setColumns(10);
		
		chckbxPrivChan = new JCheckBox("private channel flag");
		chckbxPrivChan.setBounds(10, 46, 121, 23);
		createChannel.add(chckbxPrivChan);
		
		chckbxSecretChannelFlag = new JCheckBox("secret channel flag");
		chckbxSecretChannelFlag.setBounds(10, 72, 121, 23);
		createChannel.add(chckbxSecretChannelFlag);
		
		chckbxInviteOnly = new JCheckBox("invite only");
		chckbxInviteOnly.setBounds(10, 98, 97, 23);
		createChannel.add(chckbxInviteOnly);
		
		chckbxTopicSettableBy = new JCheckBox("topic settable by channelop only");
		chckbxTopicSettableBy.setBounds(10, 124, 181, 23);
		createChannel.add(chckbxTopicSettableBy);
		
		chckbxNoMessagesTo = new JCheckBox("no messages to channel from outside clients");
		chckbxNoMessagesTo.setBounds(10, 150, 237, 23);
		createChannel.add(chckbxNoMessagesTo);
		
		chckbxModerated = new JCheckBox("moderated");
		chckbxModerated.setBounds(10, 176, 97, 23);
		createChannel.add(chckbxModerated);
		
		chckbxSetUserLimit = new JCheckBox("set user limit");
		chckbxSetUserLimit.setBounds(10, 202, 97, 23);
		createChannel.add(chckbxSetUserLimit);
		
		chckbxSetBanMask = new JCheckBox("set ban mask to keep users out");
		chckbxSetBanMask.setBounds(10, 230, 175, 23);
		createChannel.add(chckbxSetBanMask);
		
		chckbxSetPasswordFor = new JCheckBox("set password for channel");
		chckbxSetPasswordFor.setBounds(10, 254, 147, 23);
		createChannel.add(chckbxSetPasswordFor);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setBounds(29, 354, 162, 91);
		createChannel.add(btnCreate);
		main.connect();
		getChannelList();
	}
	private boolean[] getCheckBoxFlags(){
		boolean flags[] = null;
		flags[0] = chckbxModerated.isSelected();
		flags[1] = chckbxSetBanMask.isSelected();
		flags[2] = chckbxNoMessagesTo.isSelected();
		flags[3] = chckbxSetUserLimit.isSelected();
		flags[4] = chckbxInviteOnly.isSelected();
		flags[5] = chckbxSetPasswordFor.isSelected();
		flags[6] = chckbxTopicSettableBy.isSelected();
		flags[7] = chckbxPrivChan.isSelected();
		flags[8] = chckbxSecretChannelFlag.isSelected();
		return flags;
	}
	
	private void getChannelList(){
		main.doList();
	}
	
	private class Listener extends IRCEventAdapter implements IRCEventListener {
		protected String result;
	    public void onRegistered() {
	      result = "Connected";
	    }
	    
	    public void onDisconnected() {
	      result = "Disconnected";
	    }

	    public void onError(String msg) {
	      result = "Error: "+ msg;
	    }
	    
	    public void onError(int num, String msg) {
	      result = "Error #"+ num +": "+ msg;
	    }

	    public void onInvite(String chan, IRCUserInfo u, String nickPass) {
	      result = chan +"> "+ u.getNick() +" invites "+ nickPass;
	    }

	    public void onJoin(String chan, IRCUserInfo u) {
	      result = chan +"> "+ u.getNick() +" joins";
	    }
	    
	    public void onKick(String chan, IRCUserInfo u, String nickPass, String msg) {
	      result = chan +"> "+ u.getNick() +" kicks "+ nickPass;
	    }

	    public void onMode(IRCUserInfo u, String nickPass, String mode) {
	      result = "Mode: "+ u.getNick() +" sets modes "+ mode +" "+ 
	          nickPass;
	    }

	    public void onMode(IRCUserInfo u, String chan, IRCModeParser mp) {
	      result = chan +"> "+ u.getNick() +" sets mode: "+ mp.getLine();
	    }

	    public void onNick(IRCUserInfo u, String nickNew) {
	      result = "Nick: "+ u.getNick() +" is now known as "+ nickNew;
	    }
	    public void onNotice(String target, IRCUserInfo u, String msg) {
	        result = target +"> "+ u.getNick() +" (notice): "+ msg;
	      }

	      public void onPart(String chan, IRCUserInfo u, String msg) {
	        result = chan +"> "+ u.getNick() +" parts";
	      }
	      
	      public void onPrivmsg(String chan, IRCUserInfo u, String msg) {
	        result = chan +"> "+ u.getNick() +": "+ msg;
	      }

	      public void onQuit(IRCUserInfo u, String msg) {
	        result = "Quit: "+ u.getNick();
	      }

	      public void onReply(int num, String value, String msg) {
	        result = "Reply #"+ num +": "+ value +" "+ msg;
	      }

	      public void onTopic(String chan, IRCUserInfo u, String topic) {
	        result = chan +"> "+ u.getNick() +" changes topic into: "+ topic;
	      }
	}
}
