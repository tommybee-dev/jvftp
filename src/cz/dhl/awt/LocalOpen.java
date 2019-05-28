/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.BorderLayout;
import java.awt.Panel;

import cz.dhl.io.CoSource;
import cz.dhl.io.LocalSource;

/**
 * <P>Sample implementation of LocalBrowsePanel.</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public final class LocalOpen extends TabbedPanel {
	private LocalBrowsePanel local = null;

	/** <P>Creates a new LocalOpen instance.</P> */
	public LocalOpen() {
		LocalSource source = new LocalSource();
		/* Cards */
		Panel listcard = new Panel();

		addTab("List", listcard);
		showCard("List");

		{ /* List card initialisation */
			CoSource sources[] = new CoSource[1];
			sources[0] = source;
			CoControlPanel control = new CoControlPanel(sources);
			listcard.setLayout(new BorderLayout());
			local = new LocalBrowsePanel(source);
			listcard.add("Center", local);
			listcard.add("South", control);
		}
	}

	/**
	 * <P>Starts processing threads.
	 * <P>Call from Applet.init() or in applications from 
	 * main method, after GUI is initialized.
	 * <P>
	 * @see java.applet.Applet#init()
	 */
	public void start() {
		local.start();
	}

	/**
	 * <P>Ends processing threads and closes network connections.
	 * <P>Call on Applet.destroy() or when application exits.
	 * If called from application use Window.dispose() instead 
	 * of System.exit() to close GUI and end up execution. 
	 * If not possible, allow 2 seconds grace period to 
	 * stop threads before exit.
	 * <P>
	 * @see java.applet.Applet#destroy()
	 * @see java.awt.Window#dispose()
	 * @see java.lang.System#exit()
	 */
	public void end() {
		if (local != null) {
			local.end();
			local = null;
		}
	}

	private static final long serialVersionUID = 1L;
}