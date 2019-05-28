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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.dhl.ftp.Ftp;

/**
 * <P>Allows entering commands manually.</P>
 * <P>Optional component of JFtpBrowsePanel.</P>
 * 
 * <IMG SRC="command.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see JFtpBrowsePanel
 */
public final class JFtpCommandPanel extends JPanel {
	JLabel commandLabel = new JLabel("Command:");
	JTextField commandField = new JTextField();
	JButton enterButton = new JButton("Enter");

	/** <P>Creates a new FtpCommandPanel instance.</P> */
	public JFtpCommandPanel(Ftp client) { /* Variables */
		final JFtpEventQueue queue =
			(JFtpEventQueue) JCoEventQueue.getEventQueue(client);
		queue.command = this;

		/* State */
		setEnabled(false);

		/* Structure */
		setLayout(new BorderLayout(5, 5));
		add(commandLabel, BorderLayout.WEST);
		add(commandField, BorderLayout.CENTER);
		add(enterButton, BorderLayout.EAST);

		/* Listeners */
		commandField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.command();
			}
		});
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.command();
			}
		});
	}

	String getText() {
		return commandField.getText();
	}

	public void setEnabled(boolean b) {
		commandLabel.setEnabled(b);
		commandField.setEnabled(b);
		enterButton.setEnabled(b);
	}

	private static final long serialVersionUID = 1L;
}