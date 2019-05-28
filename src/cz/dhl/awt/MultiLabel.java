/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * <P>Multiple Line Label</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public final class MultiLabel extends Canvas {
	String label[];
	boolean sizeInit = false;

	/** <P>Creates a new MultiLabel instance.</P> */
	public MultiLabel(String label[]) {
		this.label = label;
		this.setSize(3, 3);
	}

	public void addNotify() {
		super.addNotify();
		Graphics g = this.getGraphics();
		FontMetrics m = g.getFontMetrics();
		int a = m.getAscent(), d = m.getDescent();
		if (!sizeInit) {
			sizeInit = true;
			int maxwidth = 0;
			for (int i = 0; i < label.length; i++) {
				int width = m.stringWidth(label[i]);
				if (width > maxwidth)
					maxwidth = width;
			}
			setSize(maxwidth + 2 * d, label.length * (a + d) + 2 * d);
			invalidate();
		}
	}

	public void paint(Graphics g) {
		FontMetrics m = g.getFontMetrics();
		int a = m.getAscent(), d = m.getDescent(); // y = a;
		for (int i = 0; i < label.length; i++) { //g.setColor(Color.white);
			//g.drawString(label[i],2 +1,(a+d)*i + a + d +1);
			g.setColor(Color.red);
			g.drawString(label[i], 2, (a + d) * i + a + d);
		}
	}

	private static final long serialVersionUID = 1L;
}