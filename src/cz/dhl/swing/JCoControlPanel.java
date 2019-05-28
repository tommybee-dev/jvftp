/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cz.dhl.io.CoSource;

/**
 * <P>Contains copy, delete and rename buttons.</P>
 * <P>Optional component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="control.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see JFtpBrowsePanel
 * @see JLocalBrowsePanel
 */
public class JCoControlPanel extends JPanel {
	private JButton copy = new JButton("Copy");
	private JButton delete = new JButton("Delete");
	private JButton rename = new JButton("Rename");
	private JButton makedir = new JButton("New Dir");

	/** <P>Creates a new CoControlPanel instance.</P> */
	public JCoControlPanel(CoSource sources[]) { /* Variables */
		for (int j = 0; j < sources.length; j++) {
			JCoEventQueue queue = JCoEventQueue.getEventQueue(sources[j]);
			queue.control = this;
		}

		setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		add(copy);
		add(delete);
		add(rename);
		add(makedir);

		for (int j = 0; j < sources.length; j++) {
			final JCoEventQueue queue = JCoEventQueue.getEventQueue(sources[j]);
			addListener(queue);
		}
	}

	void addListener(final JCoEventQueue queue) {
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.copyFiles();
			}
		});
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.deleteFiles();
			}
		});
		rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.renameFile();
			}
		});
		makedir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.makeDir();
			}
		});
	}

	private static final long serialVersionUID = 1L;
}