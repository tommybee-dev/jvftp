/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cz.dhl.ftp.Ftp;
import cz.dhl.io.CoSource;
import cz.dhl.io.LocalSource;

/**
 * <P>Sample implementation of JLocalBrowsePanel
 * in conjunction with JFtpBrowsePanel</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see FtpBrowsePanel
 * @see LocalBrowsePanel
 */
public final class JFtpCopy extends JTabbedPanel {
	private JFtpBrowsePanel browse = null;
	private JLocalBrowsePanel local = null;

	/** <P>Creates a new FtpCopy instance.</P> */
	public JFtpCopy() {
		Ftp client = new Ftp();
		LocalSource source = new LocalSource();
		/* Cards */
		JPanel logincard = new JPanel();
		JPanel listcard = new JPanel();
		JPanel logcard = new JPanel();

		addTab("Login", logincard);
		addTab("List", listcard);
		addTab("Log", logcard);
		showCard("Login");

		{ /* Login card initialisation */
			logincard.setLayout(new BorderLayout());
			JPanel logingrid = new JPanel(new GridLayout(1, 2));
			logincard.add("North", logingrid);
			logingrid.add(new JPanel());
			logingrid.add(new JFtpLoginPanel(client, this));
			JPanel connectgrid = new JPanel(new GridLayout(1, 2));
			logincard.add("South", connectgrid);
			connectgrid.add(new JPanel());
			connectgrid.add(new JFtpConnectPanel(client));
		}

		{ /* List card initialisation */
			CoSource sources[] = new CoSource[2];
			sources[0] = client;
			sources[1] = source;
			JCoStatusPanel status = new JCoStatusPanel(sources);
			JCoControlPanel control = new JCoControlPanel(sources);
			JPanel listgrid = new JPanel(new GridLayout(1, 2));
			JPanel bottomgrid = new JPanel(new GridLayout(1, 2));

			browse = new JFtpBrowsePanel(client);
			local = new JLocalBrowsePanel(source);
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

		{ /* Log card initialisation */
			logcard.setLayout(new BorderLayout());
			JScrollPane scroll = new JScrollPane(new JCoConsoleTextArea(client));
			scroll.setPreferredSize(new Dimension(600, 350));
			logcard.add("Center", scroll);
			logcard.add("South", new JFtpCommandPanel(client));
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