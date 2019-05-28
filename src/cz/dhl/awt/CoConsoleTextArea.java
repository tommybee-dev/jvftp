/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.TextArea;

import cz.dhl.ftp.Ftp;
import cz.dhl.ui.CoConsole;

/**
 * <P>Displays remote commands and server replies.</P>
 * <P>Optional component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="console.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see FtpBrowsePanel
 */
public final class CoConsoleTextArea extends TextArea implements CoConsole {
	/** <P>Creates a new CoConsoleTextArea instance.</P> */
	CoConsoleTextArea(Ftp client) {
		super("Visit url for update: http://sourceforge.net/projects/jvftp\n\n"
			+ "JvFTP was developed by http://sourceforge.net/users/bpetrovi\n" 
			+ "The sources was donated to sourceforge.net under the terms\n" 
			+ "of GNU Lesser General Public License (LGPL). Version 0.72 08/10/2003\n\n",
			-1, -1, TextArea.SCROLLBARS_VERTICAL_ONLY);

		/* State */
		setEditable(false);
		client.getContext().setConsole(this);
	}

	public void print(String message) {
		append(message + "\n");
	}

	private static final long serialVersionUID = 1L;
}