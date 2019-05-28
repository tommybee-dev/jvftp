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
import java.awt.GridLayout;
import java.awt.Panel;

import cz.dhl.ftp.Ftp;
import cz.dhl.io.CoSource;
import cz.dhl.io.LocalSource;

/**
 * <P>Sample implementation of LocalBrowsePanel
 * in conjunction with FtpBrowsePanel</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see FtpBrowsePanel
 * @see LocalBrowsePanel
 */
public final class FtpCopy extends TabbedPanel {
	private FtpBrowsePanel browse = null;
	private LocalBrowsePanel local = null;

	/** <P>Creates a new FtpCopy instance.</P> */
	public FtpCopy() {
		Ftp client = new Ftp();
		LocalSource source = new LocalSource();
		/* Cards */
		Panel logincard = new Panel();
		Panel listcard = new Panel();
		Panel logcard = new Panel();

		addTab("Login", logincard);
		addTab("List", listcard);
		addTab("Log", logcard);
		showCard("Login");

		{	/* Login card initialisation */
			logincard.setLayout(new BorderLayout());
			Panel logingrid = new Panel(new GridLayout(1, 2));
			logincard.add("North", logingrid);
			logingrid.add(new Panel());
			logingrid.add(new FtpLoginPanel(client, this));
			Panel connectgrid = new Panel(new GridLayout(1, 2));
			logincard.add("South", connectgrid);
			connectgrid.add(new Panel());
			connectgrid.add(new FtpConnectPanel(client));
		}

		{	/* List card initialisation */
			CoSource sources[] = new CoSource[2];
			sources[0] = client;
			sources[1] = source;
			CoStatusPanel status = new CoStatusPanel(sources);
			CoControlPanel control = new CoControlPanel(sources);
			Panel listgrid = new Panel(new GridLayout(1, 2));
			Panel bottomgrid = new Panel(new GridLayout(1, 2));

			browse = new FtpBrowsePanel(client);
			local = new LocalBrowsePanel(source);
			browse.setOpositePanel(local);
			local.setOpositePanel(browse);

			listcard.setLayout(new BorderLayout());
			listcard.add("Center", listgrid);
			listgrid.add(local);
			listgrid.add(browse);
			listcard.add("South", bottomgrid);
			bottomgrid.add(status);
			bottomgrid.add(control);
		}

		{	/* Log card initialisation */
			logcard.setLayout(new BorderLayout());
			logcard.add("Center", new CoConsoleTextArea(client));
			logcard.add("South", new FtpCommandPanel(client));
		}
	}

	/** Sets FTP config string. */
	public void setConfig(String config) {
		browse.setConfig(config);
	}
	/** Sets FTP config string. */
	public void setConfig(String config[]) {
		browse.setConfig(config);
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
		browse.start();
	}

	/**
	 * <P>Ends processing threads and closes network connections.
	 * <P>Call on Applet.destroy() or when application exits.
	 * If called from application use Window.dispose() instead 
	 * of System.exit() to close GUI and end up execution. 
	 * If not possible, allow 2 seconds grace period to 
	 * close network sockets and stop threads before exit.
	 * <P>
	 * @see java.applet.Applet#destroy()
	 * @see java.awt.Window#dispose()
	 * @see java.lang.System#exit(int)
	 */
	public void end() {
		if (local != null) {
			local.end();
			local = null;
		}
		if (browse != null) {
			browse.end();
			browse = null;
		}
	}

	private static final long serialVersionUID = 1L;
}