/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cz.dhl.ftp.Ftp;
import cz.dhl.ui.CoConsole;

/**
 * <P>Displays remote commands and server replies.</P>
 * <P>Optional component of JFtpBrowsePanel.</P>
 * 
 * <IMG SRC="console.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see JFtpBrowsePanel
 */
public final class JCoConsoleTextArea extends JTextArea implements CoConsole {

	/** <P>Creates a new CoConsoleTextArea instance.</P> */
	public JCoConsoleTextArea(Ftp client) {
		super("Visit url for update: http://sourceforge.net/projects/jvftp\n\n"
			+ "JvFTP was developed by http://sourceforge.net/users/bpetrovi\n" 
			+ "The sources was donated to sourceforge.net under the terms\n" 
			+ "of GNU Lesser General Public License (LGPL). Version 0.72 08/10/2003\n\n"); 
		/* State */
		setEditable(false);

		client.getContext().setConsole(this);
	}

	public void print(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int length = getDocument().getLength();
				append(message + "\n");
				setCaretPosition(length);
			}
		});
	}

	private static final long serialVersionUID = 1L;
}