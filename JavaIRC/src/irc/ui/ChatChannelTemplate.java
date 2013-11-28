package irc.ui;

import irc.core.IRCEventAdapter;
import irc.core.IRCEventListener;
import irc.core.IRCModeParser;
import irc.core.IRCUserInfo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

public abstract class ChatChannelTemplate extends JFrame {
	private JTextField textField;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea textArea = new JTextArea();
		textArea.setText("");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(textField, Alignment.LEADING)
						.addComponent(textArea, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
					.addContainerGap(101, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(29)
					.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSend, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
						.addComponent(textField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);

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
			System.out.println("JOIN: "+ user.getNick() 
			        +" joins "+ chan);
			    // add the nickname to the nickname-table
		}

		@Override
		public void onKick(String chan, IRCUserInfo user, String pNick,
				String msg) {
			System.out.println("KICK: "+ user.getNick() 
			        +" kicks "+ pNick +"("+ msg +")");
			    // remove the nickname from the nickname-table
		}

		@Override
		public void onMode(String chan, IRCUserInfo user, IRCModeParser modeParser) {
			System.out.println("MODE: "+ user.getNick() 
			        +" changes modes in "+ chan +": "+ modeParser.getLine());
			    // some operations with the modes
		}

		@Override
		public void onMode(IRCUserInfo user, String passiveNick, String mode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNick(IRCUserInfo user, String newNick) {
			System.out.println("NICK: "+ user.getNick() 
			        +" is now known as "+ newNick);
			    // update the nickname in the nickname-table
		}


		@Override
		public void onPart(String chan, IRCUserInfo user, String msg) {
			System.out.println("PART: "+ user.getNick() 
			        +" parts from "+ chan +"("+ msg +")");
			    // remove the nickname from the nickname-table
		}


		@Override
		public void onPrivmsg(String target, IRCUserInfo user, String msg) {
			System.out.println("PRIVMSG: "+ user.getNick() 
			        +" to "+ target +": "+ msg);
		}

		@Override
		public void onQuit(IRCUserInfo user, String msg) {
			System.out.println("QUIT: "+ user.getNick() +" ("+ 
			        user.getUsername() +"@"+ user.getHost() +") ("+ msg +")");
			    // remove the nickname from the nickname-table
		}

		@Override
		public void onReply(int num, String value, String msg) {
			System.out.println("Reply #"+ num +": Message: "+ 
			        msg +" | Value: "+ value);
		}

		@Override
		public void onTopic(String chan, IRCUserInfo user, String topic) {
			setTitle(topic);
		}

		@Override
		public void onNotice(String target, IRCUserInfo user, String msg) {
			// TODO Auto-generated method stub
			
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
