/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cz.dhl.swing.JFtpCopy;
import cz.dhl.util.CoActiveMap;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public class JMetFtpCopy extends JPanel {
	/** @param args the command line arguments */
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++)
			if (args[i].compareTo("--plaf") == 0 && (i + 1) < args.length)
				try {
					UIManager.setLookAndFeel(args[i + 1]);
				} catch (Exception e) {
				}

		final JFtpCopy instance = new JFtpCopy();
		final JFrame frame = new JFrame("JvFTP");
		instance.setConfig(args);

		CoActiveMap.terminate = true;
		CoActiveMap.progress.put(instance, instance);

		frame.getContentPane().add(instance);
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
		//frame.show();
		frame.setVisible(true);
		instance.start();
	}

	private static final long serialVersionUID = 1L;
}