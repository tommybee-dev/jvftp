/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cz.dhl.ui.CoTabbed;

/**
 * <P>Tabbed Panel</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public class JTabbedPanel extends JTabbedPane implements CoTabbed {

	Vector<String> v = new Vector<String>(5);

	/** <P>Creates a new JTabbedPanel instance.</P> */
	public JTabbedPanel() {
	}

	public void addTab(String label, JPanel panel) {
		super.addTab(label, panel);
		v.add(label);
	}

	public void showCard(String label) {
		if(!label.equals(getCard())) {
			setSelectedIndex(v.indexOf(label));
		}
	}

	public String getCard() {
		return (String)v.get(getSelectedIndex());
	}

	private static final long serialVersionUID = 1L;
}