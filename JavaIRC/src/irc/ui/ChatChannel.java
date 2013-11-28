package irc.ui;

import irc.core.IRCMain;

public final class ChatChannel extends ChatChannelTemplate {
	private IRCMain main;
	private String channelName;
	protected ChatChannel(String chanName, IRCMain main, String topic){
		super();
		channelName = chanName;
		setTitle(topic);
		main.addIRCEventListener(new Listener());
		this.main = main;
		setVisible(true);
	}
	
	@Override
	public void dispose(){
		main.doPart(channelName);
		super.dispose();
	}
}
