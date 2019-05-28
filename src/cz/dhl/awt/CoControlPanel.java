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

import cz.dhl.io.CoSource;

/**
 * <P>Contains copy, delete and rename buttons.</P>
 * <P>Optional component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="control.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see LocalBrowsePanel
 */
public class CoControlPanel extends Panel {
	private Button copy = new Button("Copy");
	private Button delete = new Button("Delete");
	private Button rename = new Button("Rename");
	private Button makedir = new Button("New Dir");

	/** <P>Creates a new CoControlPanel instance.</P> */
	public CoControlPanel(CoSource sources[]) { /* Variables */
		for (int j = 0; j < sources.length; j++) {
			CoEventQueue queue = CoEventQueue.getEventQueue(sources[j]);
			queue.control = this;
		}

		setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		add(copy);
		add(delete);
		add(rename);
		add(makedir);

		for (int j = 0; j < sources.length; j++) {
			final CoEventQueue queue = CoEventQueue.getEventQueue(sources[j]);
			addListener(queue);
		}
	}

	void addListener(final CoEventQueue queue) {
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