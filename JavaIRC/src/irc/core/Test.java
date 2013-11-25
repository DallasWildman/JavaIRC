package irc.core;

import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		IRCMain client = new IRCMain("localhost", new int[] { 6667,
				6668, 6669 }, null, "Foo", "Mr. Foobar", "foo@bar.com");

		client.addIRCEventListener(new IRCLocalListener()); // see next section
		client.setDaemon(true);
		client.setPong(true);

		try {
			client.connect(); // Connect! (Don't forget this!!)
		} catch (IOException ioexc) {
			ioexc.printStackTrace(); // Connection failed
		}
	}
}
