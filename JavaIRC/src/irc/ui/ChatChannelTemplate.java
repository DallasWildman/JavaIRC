package irc.ui;

import irc.core.IRCEventAdapter;
import irc.core.IRCEventListener;
import irc.core.IRCMain;
import irc.core.IRCModeParser;
import irc.core.IRCNumericReplies;
import irc.core.IRCUserInfo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public abstract class ChatChannelTemplate extends JFrame {
	private JTextArea messageField;
	private JTextArea chatLog;
	private JList<String> users;
	private IRCMain main;
	private String channelName;
	private Vector<String> userList;
	private ChannelMenu owningChanMen;

	/**
	 * For editing purposes only!
	 */
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatChannel frame = new ChatChannel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatChannelTemplate() {
		setBounds(100, 100, 800, 450);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		chatLog = new JTextArea();
		chatLog.setEditable(false);
		chatLog.setLineWrap(true);
		chatLog.setText("");
		
		messageField = new JTextArea();
		messageField.setLineWrap(true);
		messageField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String written = messageField.getText();
				main.doPrivmsg("#" + channelName, written);
				chatLog.append("\nYou: " + written);
				messageField.setText("");
			}
		});
		
		userList = new Vector<String>();
		users = new JList<String>(userList);
		users.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(messageField)
						.addComponent(chatLog, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
					.addGap(18)
					.addComponent(users, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(30)
							.addComponent(users, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(29)
							.addComponent(chatLog, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(messageField, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);

	}
	
	
	public String getChannelName() {
		return channelName;
	}


	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}


	public IRCMain getMain() {
		return main;
	}


	public void setMain(IRCMain main) {
		this.main = main;
	}
	
	public void setUserList(Collection<String> list){
		userList.removeAllElements();
		userList.addAll(list);
		userList.remove(main.getNick());
		users.setListData(userList);
	}
	
	@Override
	public void dispose(){
		main.doPart("#"+channelName);
		owningChanMen.remove(this);
		super.dispose();
	}


	public ChannelMenu getOwningChanMen() {
		return owningChanMen;
	}


	public void setOwningChanMen(ChannelMenu owningChanMen) {
		this.owningChanMen = owningChanMen;
	}


	class Listener extends IRCEventAdapter implements IRCEventListener{
		@Override
		public void onRegistered() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnected() {
			System.out.println("Disconnected.");
			
		}

		@Override
		public void onError(String msg) {
			System.out.println("ERROR: "+ msg);
			
		}

		@Override
		public void onError(int num, String msg) {
			System.out.println("Error #"+ num +": "+ 
			        msg);
		}

		@Override
		public void onInvite(String chan, IRCUserInfo user, String pNick) {
			System.out.println("INVITE: "+ user.getNick() 
			        +" invites "+ pNick +" to "+ chan);
		}

		@Override
		public void onJoin(String chan, IRCUserInfo user) {
			if(chan.equals("#" + channelName))
				userList.add(user.getNick());
		}

		@Override
		public void onKick(String chan, IRCUserInfo user, String pNick,
				String msg) {
			if(chan.equals("#" + channelName)){
				userList.remove(pNick);
				chatLog.append("\n"+ user.getNick() 
			        +" kicks "+ pNick +"("+ msg +")");
				users.setListData(userList);}
		}

		@Override
		public void onMode(String chan, IRCUserInfo user, IRCModeParser modeParser) {
			if(chan.equals("#" + channelName))
				chatLog.append("\n" + user.getNick() + " changes the mode: " + modeParser.getLine());
		}

		@Override
		public void onMode(IRCUserInfo user, String passiveNick, String mode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNick(IRCUserInfo user, String newNick) {
			userList.set(userList.indexOf(user.getNick()), newNick);
			users.setListData(userList);
		}


		@Override
		public void onPart(String chan, IRCUserInfo user, String msg) {
			if(chan.equals("#" + channelName)){
				userList.remove(user.getNick());
				chatLog.append("\n" + user.getNick() + " has left the channel"); users.setListData(userList);}
		}


		@Override
		public void onPrivmsg(String chan, IRCUserInfo user, String msg) {
			if(chan.equals("#"+channelName))
				if(user.equals(main.getNick()))
					chatLog.append("\nYou: "+ msg+"\n");
				else
					chatLog.append("\n" + user.getNick() +": "+ msg+"\n");
		}

		@Override
		public void onQuit(IRCUserInfo user, String msg) {
			System.out.println("QUIT: "+ user.getNick() +" ("+ 
			        user.getUsername() +"@"+ user.getHost() +") ("+ msg +")");
			    // remove the nickname from the nickname-table
		}

		@Override
		public void onReply(int num, String value, String msg) {
			switch(num){
			case IRCNumericReplies.RPL_NAMREPLY:
				if(value.startsWith(main.getNick() + " = #"+channelName)){
					userList.removeAllElements();
					String [] users = msg.split(" ");
					for(String item : users)
						if(!item.equals(main.getNick()))
							userList.add(item);
					ChatChannelTemplate.this.users.setListData(userList);}
			}
		}

		@Override
		public void onTopic(String chan, IRCUserInfo user, String topic) {
			setTitle(topic);
		}

		@Override
		public void onNotice(String target, IRCUserInfo user, String msg) {
			if(target.equals("#" + channelName))
				if(user.equals(main.getNick()))
					chatLog.append("\nNotice:" + msg.replaceFirst(main.getNick(), "You"));
				else
					chatLog.append("\nNotice:" + msg);
		}

		@Override
		public void onPing(String ping) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unknown(String prefix, String command, String middle,
				String trailing) {
			// TODO Auto-generated method stub
			
		}
	}
}
