/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cz.dhl.ftp.Ftp;

/**
 * <P>Contains connect and disconnect buttons.</P>
 * <P>Required component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="connect.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see FtpBrowsePanel
 */
public final class FtpConnectPanel extends Panel {
	Button connectButton = new Button("Connect");
	Button disconnectButton = new Button("Disconnect");

	/** <P>Creates a new FtpConnectPanel instance.</P> */
	FtpConnectPanel(Ftp client) { /* Variables */
		final FtpEventQueue queue = (FtpEventQueue) CoEventQueue.getEventQueue(client);
		queue.connect = this;

		/* State */
		disconnectButton.setEnabled(false);

		/* Structure */
		setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		add(connectButton);
		add(disconnectButton);

		/* Listeners */
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.connect();
			}
		});
		disconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.disconnect();
			}
		});
	}

	public void setEnabled(boolean bc, boolean bd) {
		connectButton.setEnabled(bc);
		disconnectButton.setEnabled(bd);
	}

	private static final long serialVersionUID = 1L;
}