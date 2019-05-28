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
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cz.dhl.ui.CoTabbed;

/**
 * <P>Tabbed Panel</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public class TabbedPanel extends Panel implements ActionListener, CoTabbed {
	private Panel tabstrip = new Panel(new FlowLayout(FlowLayout.LEFT));

	private CardLayout cardlayout = new CardLayout(6, 6);
	private Panel cardpanel = new BorderPanel(cardlayout);

	/** <P>Creates a new TabbedPanel instance.</P> */
	public TabbedPanel() {
		setLayout(new BorderLayout());
		add("North", tabstrip);
		add("Center", cardpanel);
	}

	public void addTab(String label, Panel panel) {
		Button button = new Button(label);
		button.addActionListener(this);
		tabstrip.add(button);
		cardpanel.add(label, panel);
	}

	public void actionPerformed(ActionEvent e) {
		showCard(e.getActionCommand());
	}

	public void showCard(String name) {
		cardlayout.show(cardpanel, name);
	}

	private static final long serialVersionUID = 1L;
}

final class BorderPanel extends Panel {
	BorderPanel(LayoutManager manager) {
		super(manager);
	}

	public void paint(Graphics g) {
		Dimension d = getSize();

		/* Win styled top frame line */
		g.setColor(Color.white);
		g.drawLine(0, 0, d.width - 2, 0);
		/* Win styled left frame line */
		g.drawLine(0, 0, 0, d.height - 2);
		g.setColor(Color.black);
		/* Win styled right frame line */
		g.drawLine(d.width - 1, 0, d.width - 1, d.height - 1);
		g.setColor(Color.gray);
		g.drawLine(d.width - 2, 1, d.width - 2, d.height - 2);
		/* Win styled bottom frame line */
		g.drawLine(1, d.height - 2, d.width - 2, d.height - 2);
		g.setColor(Color.black);
		g.drawLine(0, d.height - 1, d.width - 1, d.height - 1);
	}

	private static final long serialVersionUID = 1L;
}