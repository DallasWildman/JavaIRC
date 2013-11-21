package irc.core;

/**
 * This class is modified from IRClib to hold user's
 * real&nick names, host information.
 * 
 * Detailed information for IRClib can be found at:
 * http://moepii.sourceforge.net/
 */
public class IRCUserInfo {
	
	/** 
	 * The user's nickname.
	 */
	private String nick;
	
	/**
	 * The user's username.
	 */
	private String username;
	
	/**
	 * The user's host.
	 */
	private String host;
	
// ------------------------------
	
	/**
	 * Creates a new <code>IRCUser</code> object.
	 * @param nick The user's nickname.
	 * @param username The user's username.
	 * @param host The user's host.
	 */
	public IRCUserInfo(String nick, String username, String host) {
		this.nick = nick;
		this.username = username;
		this.host = host;
	}
	
// ------------------------------
	
	/** 
	 * Returns the nickname of the person who sent the line 
	 * or the servername of the server which sent the line. <br />
	 * If no nickname is given, <code>null</code> is returned.
	 * <br /><br />
	 * <b>Note:</b> This method is totally equal to <code>getServername</code>!
	 * @return The nickname or the servername of the line. If no nick is given,
	 *         <code>null</code> is returned.
	 * @see #getUsername()
	 * @see #getHost()
	 */
	public String getNick() {
		return nick;
	}
	
// ------------------------------
	
	/** 
	 * Returns the username of the person who sent the line. <br />
	 * If the username is not specified, this method returns <code>null</code>.
	 * @return The username of the line; <code>null</code> if it's not given.
	 * @see #getNick()
	 * @see #getHost()
	 */
	public String getUsername() {
		return username;
	}
	
// ------------------------------
	
	/** 
	 * Returns the host of the person who sent the line. <br />
	 * If the host is not specified, this method returns <code>null</code>.
	 * @return The host of the line; <code>null</code> if it's not given.
	 * @see #getNick()
	 * @see #getUsername()
	 */
	public String getHost() {
		return host;
	}
	
// ------------------------------
	
	/**
	 * Returns the nickname.
	 * @return The nickname.
	 */
	public String toString() {
		return getNick();
	}
}
