/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl;

import java.applet.Applet;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cz.dhl.awt.FtpCopy;
import cz.dhl.util.CoActiveMap;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public class MetFtpCopy extends Applet {
	FtpCopy browse = null;

	public void init() {
		browse = new FtpCopy();

		setBackground(Color.lightGray);
		setLayout(new CardLayout(4, 4));
		add("browse", browse);

		browse.start();
	}

	public void start() {
		browse.setConfig(getParameter("config"));
	}

	public void destroy() {
		if (browse != null) {
			browse.end();
			browse = null;
		}
	}

	/** @param args the command line arguments */
	public static void main(String[] args) {
		final FtpCopy instance = new FtpCopy();
		final Frame frame = new Frame("JvFTP (AWT)");
		instance.setConfig(args);

		CoActiveMap.terminate = true;
		CoActiveMap.progress.put(instance, instance);

		frame.add(instance);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				instance.end();
				frame.dispose();
			}
			public void windowClosed(WindowEvent e) {
				CoActiveMap.progress.remove(instance);
			}
		});
		frame.pack();
		frame.show();
		instance.start();
	}

	private static final long serialVersionUID = 1L;
}