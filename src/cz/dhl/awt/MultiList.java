/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.List;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <P>Multiple Block Selection List Box</P>
 * <P>AWT List Box implementation including 
 * SHIFT and CONTROL key functionality for 
 * selecting multiple items from a list.</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public class MultiList extends List {
	private static final int SINGLE = 0;
	private static final int RANGE = 1;
	private static final int INVERT = 2;

	private int mode = SINGLE;

	private int begin = -1;
	private int last = -1;

	/* Some implementations send ItemEvent 
	 * between KeyEvents on VK_UP or VK_DOWN
	 * but some does not. */
	private boolean gotItemEvent = false;
	private int gotItemCount = 0;

	private void selectRange(int begin, int end) {
		int min = begin, max = begin;
		if (end < min)
			min = end;
		if (end > max)
			max = end;
		/* Deselect items outside of range. */
		int is[] = getSelectedIndexes();
		for (int i = 0; i < is.length; i++)
			if (is[i] < min || is[i] > max)
				deselect(is[i]);
		/* Select items inside of range. */
		if (begin < end) {
			for (int i = min; i < max; i++)
				if (!this.isIndexSelected(i))
					select(i);
			select(max);
		} else {
			for (int i = max; i > min; i--)
				if (!this.isIndexSelected(i))
					select(i);
			select(min);
		}
	}

	private void setBlockMode(int i) {
		if (!isMultipleMode()) {
			begin = i;
			setMultipleMode(true);
			requestFocus();
			if (begin != -1)
				select(begin);
		}
	}

	private void setSingleMode(int i) {
		if (isMultipleMode()) { /* Set single mode and select item. */
			setMultipleMode(false);
			requestFocus();
			select(i);
		}
	}

	private void selectSmart() {
		switch (mode) {
			case SINGLE :
				setSingleMode(last);
				break;
			case RANGE :
				if (begin != -1) {
					selectRange(begin, last);
				} else
					begin = last;
				break;
			case INVERT :
				begin = last;
				break;
		}
	}

	/** <P>Creates a new MultiList instance.</P> */
	public MultiList() {
		setMultipleMode(false);

		/* Focus Adapter */
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				mode = SINGLE;
			}
		});

		/* Key Adapter */
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_CONTROL :
						if (mode != INVERT)
							setBlockMode(getSelectedIndex());
						mode = INVERT;
						break;
					case KeyEvent.VK_SHIFT :
						if (mode != RANGE)
							setBlockMode(getSelectedIndex());
						mode = RANGE;
						break;
					case KeyEvent.VK_UP :
					case KeyEvent.VK_DOWN :
						gotItemEvent = false;
						gotItemCount++;
						break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_CONTROL :
					case KeyEvent.VK_SHIFT :
						mode = SINGLE;
						break;
					case KeyEvent.VK_UP :
						if (!gotItemEvent && last != -1) {
							if (last >= gotItemCount)
								last -= gotItemCount;
							else
								last = 0;
							selectSmart();
						}
						gotItemCount = 0;
						break;
					case KeyEvent.VK_DOWN :
						if (!gotItemEvent && last != -1) {
							if (last + gotItemCount < getItemCount())
								last += gotItemCount;
							else
								last = getItemCount() - 1;
							selectSmart();
						}
						gotItemCount = 0;
						break;
				}
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		/* Key Adapter */
		this.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				last = ((Integer) e.getItem()).intValue();
				gotItemEvent = true;
				selectSmart();
			}
		});
	}

	private static final long serialVersionUID = 1L;
}