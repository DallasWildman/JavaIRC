package irc.ui;

import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public final class ChatWindow extends JFrame implements ActionListener, KeyListener{

	/**
	 * 
	 */
	private JButton sendButton;
	private JButton clearButton;
	
	private JTextArea inputText;
	private JTextArea chatLog;
	
	private GridLayout uiLayout;
	
	private static final long serialVersionUID = 5452399921664353858L;

	public ChatWindow() throws HeadlessException {
		// TODO Auto-generated constructor stub
		super("Unknown channel");
		setDefault();
	}

	public ChatWindow(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ChatWindow(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
		setDefault();
	}

	public ChatWindow(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == sendButton){
			//Send message
		}
		inputText.setText(""); //Change this to users nickname
	}
	
	private void setDefault(){
		uiLayout = new GridLayout(3,2,10,10);
		chatLog.setSize(getWidth(), getHeight()*2/3);
		sendButton.setSize(getWidth()/4, getHeight()/3);
		sendButton.addActionListener(this);
		inputText.setSize(getWidth()/2, getHeight()/3);
		addKeyListener(this);
		clearButton.setSize(sendButton.getSize());
		clearButton.addActionListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{//Send text in textInput to chat
		}
	}
}
