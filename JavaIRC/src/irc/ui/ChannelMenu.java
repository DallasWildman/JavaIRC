package irc.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JMenu;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.List;
import javax.swing.JTextField;
import java.awt.Button;
import java.awt.Font;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import java.awt.Checkbox;
import javax.swing.SwingConstants;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;

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
	private JCheckBox chckbxChannelOpPriv;
	private JCheckBox chckbxSecretChannelFlag;
	private boolean initialClick = true;

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
	 */
	public ChannelMenu() {
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
		txtSetPasswordHere.setBounds(163, 281, 111, 20);
		createChannel.add(txtSetPasswordHere);
		txtSetPasswordHere.setColumns(10);
		
		txtLimit = new JTextField();
		txtLimit.setHorizontalAlignment(SwingConstants.CENTER);
		txtLimit.setText("limit");
		txtLimit.setBounds(95, 229, 58, 20);
		createChannel.add(txtLimit);
		txtLimit.setColumns(10);
		
		chckbxChannelOpPriv = new JCheckBox("give/take channel operator privileges");
		chckbxChannelOpPriv.setBounds(10, 46, 203, 23);
		createChannel.add(chckbxChannelOpPriv);
		
		chckbxPrivChan = new JCheckBox("private channel flag");
		chckbxPrivChan.setBounds(10, 72, 121, 23);
		createChannel.add(chckbxPrivChan);
		
		chckbxSecretChannelFlag = new JCheckBox("secret channel flag");
		chckbxSecretChannelFlag.setBounds(10, 98, 121, 23);
		createChannel.add(chckbxSecretChannelFlag);
		
		chckbxInviteOnly = new JCheckBox("invite only");
		chckbxInviteOnly.setBounds(10, 124, 97, 23);
		createChannel.add(chckbxInviteOnly);
		
		chckbxTopicSettableBy = new JCheckBox("topic settable by channelop only");
		chckbxTopicSettableBy.setBounds(10, 150, 181, 23);
		createChannel.add(chckbxTopicSettableBy);
		
		chckbxNoMessagesTo = new JCheckBox("no messages to channel from outside clients");
		chckbxNoMessagesTo.setBounds(10, 176, 237, 23);
		createChannel.add(chckbxNoMessagesTo);
		
		chckbxModerated = new JCheckBox("moderated");
		chckbxModerated.setBounds(10, 202, 97, 23);
		createChannel.add(chckbxModerated);
		
		chckbxSetUserLimit = new JCheckBox("set user limit");
		chckbxSetUserLimit.setBounds(10, 228, 97, 23);
		createChannel.add(chckbxSetUserLimit);
		
		chckbxSetBanMask = new JCheckBox("set ban mask to keep users out");
		chckbxSetBanMask.setBounds(10, 256, 175, 23);
		createChannel.add(chckbxSetBanMask);
		
		chckbxSetPasswordFor = new JCheckBox("set password for channel");
		chckbxSetPasswordFor.setBounds(10, 280, 147, 23);
		createChannel.add(chckbxSetPasswordFor);
		
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
		flags[8] = chckbxChannelOpPriv.isSelected();
		flags[9] = chckbxSecretChannelFlag.isSelected();
		return flags;
	}
}
