package irc.core;

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JOptionPane;

/*
 * This class is modified from IRClib to provide some 
 * useful utilities to process client-side tasks
 * 
 * Detailed information for IRClib can be found at:
 * http://moepii.sourceforge.net/
 */
public class IRCUtil implements IRCNumericReplies{
	
	// -------------------------------
	// Constants
	// -------------------------------
 
	/* The dateformat used for the timestamps */
	public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss");
	
	/* Timestamps with year, month and day */
	public final static SimpleDateFormat BIGTIMESTAMP = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd"); 
	
	/* Console window title */
	public static final String CONSOLEWINDOWTITLE = "Console ";
	
	/* Indicator of Console-Window in tabs */
	public static final int CONSOLEWINDOWINDEX = 0; 
	
	// -------------------------------
	// Constructors
	// -------------------------------
	
	/*Prevent from instantiating this class */
	private IRCUtil() {
		//Do Nothing
	}
	
	// -------------------------------
	// Public Methods
	// -------------------------------

	/** 
	 * According to RFC1459 the channel's name may and must start with one of the
	 * following characters.
	 * <ul>
	 * <li># == 35</li>
	 * <li>&amp; == 38</li>
	 * </ul>. 
	 * @param str The name to check if it's a channel. 
	 * @return <code>true</code> if the argument starts with one of the characters
	 *         mentioned above.
	 */
	public static boolean isChannel(String str) {
		int c;
		return (str.length() >= 2) 
		&& ((c = str.charAt(0)) == 35 || c == 38);
	}
	
	/**
	 * Parses a <code>String</code> to an <code>int</code> via
	 * <code>Integer.parseInt</code> but avoids the
	 * <code>NumberFormatException</code>.
	 * @param str The <code>String</code> to parse.
	 * @return The parsed new <code>int</code>. <code>-1</code> if
	 *         <code>NumberFormatException</code> was thrown. 
	 */
	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException exc) {
			return -1;
		}
	}
	
	/**
	 * Splits a string into substrings. 
	 * @param str The string which is to split.
	 * @param delim The delimiter character, for example a space <code>' '</code>.
	 * @param trailing The ending which is added as a substring though it wasn't 
	 *                 in the <code>str</code>. This parameter is just for the 
	 *                 <code>IRCParser</code> class which uses this method to 
	 *                 split the <code>middle</code> part into the parameters. 
	 *                 But as last parameter always the <code>trailing</code> is 
	 *                 added. This is done here because it's the fastest way to 
	 *                 do it here. <br />
	 *                 If the <code>end</code> is <code>null</code> or 
	 *                 <code>""</code>, nothing is appended.
	 * @return An array with all substrings.
	 * @see #split(String, int)
	 */
	public static String[] split(String str, int delim, String trailing) {
		Vector items = new Vector(15);
		int last = 0;
		int index = 0; 
		int len = str.length(); 
		while (index < len) {
			if (str.charAt(index) == delim) {
				items.add(str.substring(last, index));
				last = index + 1;
			}
			index++;
		}
		if (last != len)
			items.add(str.substring(last));
		if (trailing != null && trailing.length() != 0)
			items.add(trailing);
		String[] result = new String[items.size()];
		items.copyInto(result);
		return result;
	}
	
	/**
	 * Splits a string into substrings. This method is totally equal to 
	 * <code>split(str, delim, null)</code>.
	 * @param str The string which is to split.
	 * @param delim The delimiter character, for example a space <code>' '</code>.
	 * @return An array with all substrings.
	 * @see #split(String, int, String)
	 */
	public static String[] split(String str, int delim) {
		return split(str, delim, null);
	}
	
	/**
	 * Returns <code>true</code> if the "Yes" button is clicked in a "Yes/No"
	 * popup.
	 * @return <code>true</code> for yes.
	 */
	 public static boolean confirmDialog(String msg, String title) {
	    return (JOptionPane.showConfirmDialog(null, msg, title, 
	        JOptionPane.YES_NO_OPTION) == 0);
	  }
	
	 /**
	   * Formats an array in a new string. All items are separated with 
	   * <code>sep</code>.<br />
	   * For example: If you have a <code>String[] arr = String[] { "elem1", 
	   * "elem2", "elem3" }</code> and call the method <code>formatArray(arr, 
	   * ", ")</code>, the string <code>elem1, elem2, elem3</code> is returned.
	   * @param arr The array whose elements are to form into the string.
	   * @param sep The string which is to be used as seperator.
	   * @return A string with all elements of the array seperated by 
	   *         <code>sep</code>.
	   */
	  public static String formatArray(String[] arr, char sep) {
	    if (arr.length == 0)
	      return "";
	    StringBuffer sb = new StringBuffer();
	    int sepcount = arr.length - 1;
	    for (int i = 0; i < sepcount; i++)
	      sb.append(arr[i] + sep);
	    sb.append(arr[sepcount]);
	    return sb.toString();
	  }
	  
}
