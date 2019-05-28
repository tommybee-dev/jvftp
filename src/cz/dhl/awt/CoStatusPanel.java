/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

import cz.dhl.io.CoSource;
import cz.dhl.ui.CoStatus;

/**
 * <P>Displays properties for file.</P>
 * <P>Optional component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="status.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see FtpBrowsePanel
 * @see LocalBrowsePanel
 */
public class CoStatusPanel extends Panel implements CoStatus {
	Label size = new Label("");
	Label date = new Label("");

	/** <P>Creates a new CoStatusPanel instance.</P> */
	public CoStatusPanel(CoSource sources[]) { /* Variables */
		for (int j = 0; j < sources.length; j++) {
			CoEventQueue queue = CoEventQueue.getEventQueue(sources[j]);
			queue.status = this;
		}

		setLayout(new GridLayout(1, 2));
		add(size);
		add(date);
	}

	public void setSize(String s) {
		size.setText(s);
	}
	public void setDate(String s) {
		date.setText(s);
	}

	private static final long serialVersionUID = 1L;
}