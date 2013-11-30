package irc.ui;

import irc.core.IRCMain;

@SuppressWarnings("serial")
public final class ChatChannel extends ChatChannelTemplate {
	protected ChatChannel(String chanName, IRCMain main, String topic, ChannelMenu channelMenu){
		super();
		setChannelName(chanName);
		setTitle(topic);
		main.addIRCEventListener(new Listener());
		setMain(main);
		setVisible(true);
		setOwningChanMen(channelMenu);
	}
	
	
}
