/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import cz.dhl.io.LocalFile;
import cz.dhl.io.LocalSource;

/**
 * <P>Allows browsing local files.</P>
 * 
 * <IMG SRC="browse.gif"><BR><BR>
 * 
 * <DL><DT><B>LocalFile AWT browser</B> consist of:
 * <DD>Local<B>BrowsePanel</B> <I>(required)</I>
 * <DD>Co<B>StatusPanel</B> <I>(optional)</I>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova
 * @see CoStatusPanel
 */
public final class LocalBrowsePanel extends CoBrowsePanel {
	LocalEventQueue queue = null;

	/** <P>Creates a new LocalBrowsePanel instance.</P> */
	public LocalBrowsePanel(LocalSource source) {
		super(source);
		/* Variables */
		queue = (LocalEventQueue) CoEventQueue.getEventQueue(source);
		queue.browse = this;
	}

	/** <P>Get home directory.</P>
	 * @return home direcotry from java system properties. */
	public LocalFile getHomeDir()
		throws SecurityException, RuntimeException, NullPointerException {
		LocalFile home = new LocalFile(System.getProperty("user.dir"));
		if (home == null)
			throw new NullPointerException();
		else
			return home;
	}

	/** <P>Init current directory.</P> */
	public void initHomeDir() { /* Set directory on start */
		try {
			queue.update(getHomeDir());
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	/** <P>Start processing threads.</P>
	 * <P>Call from Applet.init() or in applications from 
	 * main method, after GUI is initialized.</P>
	 * @see java.applet.Applet#init() */
	public void start() {
		initHomeDir();
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