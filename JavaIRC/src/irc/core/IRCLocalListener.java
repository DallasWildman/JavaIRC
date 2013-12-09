package irc.core;

/**
 * IRCEventLocalListner is a concrete implementation of <code>IRCEventListener</code>,
 *  basically it simply print out the relative information on terminal
 * 
 * @author Luke
 *
 */
public class IRCLocalListener extends IRCEventAdapter 
							  implements IRCEventListener {

	public void onConnect() {
	    System.out.println("Connected successfully.");
	  }
	
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
		System.out.println("TOPIC: "+ user.getNick() 
		        +" changes topic of "+ chan +" into: "+ topic);
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
