/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpConnect;

/**
 * <P>Allows browsing FTP files.</P>
 * <P>Main component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="browse.gif"><BR><BR>
 * 
 * <DL><DT><B>FtpFile Swing browser</B> consist of:
 * <DD>JFtp<B>BrowsePanel</B> <I>(required)</I>
 * <DD>JFtp<B>CommandPanel</B> <I>(optional)</I>
 * <DD>JFtp<B>ConnectPanel</B> <I>(required)</I>
 * <DD>JFtp<B>LoginPanel</B> <I>(required)</I>
 * <DD>JCo<B>ConsoleTextArea</B> <I>(optional)</I></DL>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see JFtpCommandPanel
 * @see JFtpConnectPanel
 * @see JFtpLoginPanel
 * @see JCoConsoleTextArea
 * @see JCoStatusPanel
 */
public final class JFtpBrowsePanel extends JCoBrowsePanel {
	JFtpEventQueue queue = null;

	/** <P>Creates a new FtpBrowsePanel instance.</P> */
	JFtpBrowsePanel(Ftp client) {
		super(client);
		/* Variables */
		queue = (JFtpEventQueue) JCoEventQueue.getEventQueue(client);
		queue.client = client;
		queue.browse = this;
	}

	public void setConfig(String config) {
		queue.login.connect = FtpConnect.newConnect(config);
		queue.login.getConnect();
	}
	public void setConfig(String config[]) {
		queue.login.connect = FtpConnect.newConnect(config);
		queue.login.getConnect();
	}

	/** <P>Start processing threads.</P>
	 * <P>Call from Applet.init() or in applications from 
	 * main method, after GUI is initialized. </P>
	 * @see java.applet.Applet#init() */
	public void start() {
	}

	/** <P>End processing threads and close network connections.</P>
	 * <P>Call on Applet.destroy() or when application exits.
	 * If called from application use Window.dispose() instead 
	 * of System.exit() to close GUI and end up execution. 
	 * If not possible, allow 2 seconds grace period to 
	 * close network sockets and stop threads before exit.</P>
	 * @see java.applet.Applet#destroy()
	 * @see java.awt.Window#dispose()
	 * @see java.lang.System#exit() */
	public void end() {
	}

	private static final long serialVersionUID = 1L;
}