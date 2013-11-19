package irc.ui;

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

}
