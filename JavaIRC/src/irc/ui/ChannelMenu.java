package irc.ui;

import irc.core.IRCEventAdapter;
import irc.core.IRCEventListener;
import irc.core.IRCMain;
import irc.core.IRCModeParser;
import irc.core.IRCNumericReplies;
import irc.core.IRCUserInfo;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ChannelMenu extends JFrame {
	private JTextField txtPleaseWaitConnecting;
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
	private boolean getChannelsFlag;
	private boolean startChannelsQuery;
	private boolean isJoiningAChannel = false;
	private IRCMain main;
	private Listener internalList = new Listener();
	private List list;
	private JButton btnRefresh;
	private ArrayList<ChatChannel> channels = new ArrayList<ChatChannel>();
	private int numChannelFlag;


	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
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
	}*/

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public ChannelMenu(String host, String pass, String nick, String user, String real) throws IOException {
		super(host);
		main = new IRCMain(host, new int[] {1024, 2048, 6667, 6669} , pass, nick, user, real);
		main.addIRCEventListener(internalList);
		//main.addIRCEventListener(new IRCLocalListener());
		setBounds(100, 100, 450, 540);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new GridLayout(1, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);

		Panel joinChannel = new Panel();
		tabbedPane.addTab("Join", null, joinChannel, null);
		joinChannel.setLayout(null);

		list = new List();
		list.setMultipleMode(true);
		list.setBounds(10, 10, 409, 212);
		joinChannel.add(list);

		txtPleaseWaitConnecting = new JTextField();
		txtPleaseWaitConnecting.setText("Please wait, connecting...");
		txtPleaseWaitConnecting.setBounds(10, 228, 409, 37);
		txtPleaseWaitConnecting.setEditable(false);
		joinChannel.add(txtPleaseWaitConnecting);
		txtPleaseWaitConnecting.setColumns(10);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					getChannelList();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		btnRefresh.setBounds(10, 333, 89, 46);
		joinChannel.add(btnRefresh);

		JButton btnJoin = new JButton("Join");
		btnJoin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String[] selected = list.getSelectedItems();
				System.out.println("Please wait...");
				if(selected.length == 0){
					System.out.println("Joining typed channel...");
					isJoiningAChannel = true; numChannelFlag = 1;
					main.doJoin("#" + txtPleaseWaitConnecting.getText());}
				else
					joinChannels(selected);
			}

			private void joinChannels(String[] selected){
				System.out.println("Joining selected channels...");
				String chanConcat = "";
				numChannelFlag = selected.length;
				for(int i = 0; i < selected.length; i++){
					chanConcat += selected[i].split(" :")[0] + ",";}
				chanConcat = chanConcat.substring(0, chanConcat.length()-1);
				System.out.println(chanConcat);
				isJoiningAChannel = true;
				main.doJoin(chanConcat);}
		});
		btnJoin.setBounds(10, 276, 89, 46);
		joinChannel.add(btnJoin);

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
	}

	private void getChannelList() throws InterruptedException{
		if(!main.isConnected()){
			System.out.println("Not Connected!");
			return;}
		System.out.println("Please wait, refreshing...");
		list.removeAll();
		//list.setVisible(false);
		startChannelsQuery = true;
		main.doList();
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis() < t + 15000 && getChannelsFlag){}
		getChannelsFlag = false;
		//list.setVisible(true);
		System.out.println("Done");
		return;
	}
	
	protected void removeChannel(ChatChannel arg){
		channels.remove(arg);
	}

	private class Listener extends IRCEventAdapter implements IRCEventListener {
		private ArrayList<String> gatheredNameData = new ArrayList<String>();
		private boolean gatheringNames;
		private String namChannelGathered;

		public void onRegistered() {
			txtPleaseWaitConnecting.setFont(new Font("Tahoma", Font.PLAIN, 11));
			txtPleaseWaitConnecting.setText("Connected!  Type here to search for channels");
			txtPleaseWaitConnecting.setEditable(true);
		}

		public void onDisconnected() {
			txtPleaseWaitConnecting.setText("Disconnected from server");
			txtPleaseWaitConnecting.setEditable(false);
		}

		public void onError(String msg) {
			System.out.println(msg);
		}

		public void onError(int num, String msg) {
			System.out.println(num + ": " + msg);
			switch(num){
			case IRCNumericReplies.ERR_NEEDMOREPARAMS:
			case IRCNumericReplies.ERR_INVITEONLYCHAN:
			case IRCNumericReplies.ERR_CHANNELISFULL:
			case IRCNumericReplies.ERR_NOSUCHCHANNEL:
			case IRCNumericReplies.ERR_BADCHANNELKEY:
			case IRCNumericReplies.ERR_BANNEDFROMCHAN:
			case IRCNumericReplies.ERR_BADCHANMASK:
				numChannelFlag--;
				break;
			case IRCNumericReplies.ERR_TOOMANYCHANNELS:
				isJoiningAChannel = false;
				System.out.println("Aborted.");
			}
			if(numChannelFlag == 0)
				isJoiningAChannel = false;
				System.out.println("Done.");
		}

		public void onInvite(String chan, IRCUserInfo u, String nickPass) {
			System.out.println(chan +"> "+ u.getNick() +" invites "+ nickPass+"\n");
		}

		public void onJoin(String chan, IRCUserInfo u) {
			//result += chan +"> "+ u.getNick() +" joins"+"\n";
		}

		public void onKick(String chan, IRCUserInfo u, String nickPass, String msg) {
			System.out.println(chan +"> "+ u.getNick() +" kicks "+ nickPass+"\n");
		}

		public void onMode(IRCUserInfo u, String nickPass, String mode) {
			System.out.println("Mode: "+ u.getNick() +" sets modes "+ mode +" "+ 
					nickPass+"\n");
		}

		@SuppressWarnings("unused")
		public void onMode(IRCUserInfo u, String chan, IRCModeParser mp) {
			//result += chan +"> "+ u.getNick() +" sets mode: "+ mp.getLine()+"\n";
		}

		public void onNick(IRCUserInfo u, String nickNew) {
			//result += "Nick: "+ u.getNick() +" is now known as "+ nickNew+"\n";
		}
		public void onNotice(String target, IRCUserInfo u, String msg) {
			System.out.println(target +"> "+ u.getNick() +" (notice): "+ msg+"\n");
		}

		public void onPart(String chan, IRCUserInfo u, String msg) {
			System.out.println(chan +"> "+ u.getNick() +" parts"+"\n");
		}

		public void onPrivmsg(String chan, IRCUserInfo u, String msg) {
			System.out.println(chan +"> "+ u.getNick() +": "+ msg+"\n");
		}

		public void onQuit(IRCUserInfo u, String msg) {
			//result += "Quit: "+ u.getNick()+"\n";
		}

		public void onReply(int num, String value, String msg) {
			//result += "Reply #"+ num +": "+ value +" "+ msg+"\n";
			switch(num){
			case IRCNumericReplies.RPL_LISTSTART:
				if(startChannelsQuery){
					getChannelsFlag = true; startChannelsQuery = false;}
				break;
			case IRCNumericReplies.RPL_LIST:
				if(getChannelsFlag){
					String element = value.replaceAll(main.getNick() + " ", "").split(" ")[0];
					msg = msg.trim().length() > 0 ? "Public" : "";
					list.add(element + " :" + msg);}
				break;
			case IRCNumericReplies.RPL_LISTEND:
				getChannelsFlag = false; break;
			case IRCNumericReplies.RPL_TOPIC:
				System.out.println("Topic received...");
				if(isJoiningAChannel){
					channels.add(new ChatChannel(value.replaceFirst(main.getNick() + " #", ""), main, msg, ChannelMenu.this));
					numChannelFlag--;}
				if(numChannelFlag == 0){
					isJoiningAChannel = false;
					System.out.println("Done.");}
				break;
			case IRCNumericReplies.RPL_NAMREPLY:
				if(!gatheringNames){
					gatheringNames = true;
					gatheredNameData.clear();
					namChannelGathered = value.split(" = #")[1];}
				gatheredNameData.addAll(new ArrayList<String>(Arrays.asList(msg.split(" ")))); break;
			case IRCNumericReplies.RPL_ENDOFNAMES:
				System.out.println("Names done.");
				for(ChatChannel window : channels){
					if(namChannelGathered.equals(window.getChannelName())){
						window.setUserList(gatheredNameData); break;}
				}
				gatheringNames = false; break;
			default:
				System.out.println(num + ": " + value + ": " + msg);
			}
		}
		
		//This method is NOT called
		public void onTopic(String chan, IRCUserInfo u, String topic) {
			System.out.println(chan + " " + u.getNick() + " " + topic);
			if(isJoiningAChannel && u.getNick() == main.getNick()){
				System.out.println("Topic received...");
				//channels.add(new ChatChannel(chan, main, topic));
				numChannelFlag--;
				if(numChannelFlag == 0){
					isJoiningAChannel = false;
					System.out.println("Done.");}}
		}
	}
}
