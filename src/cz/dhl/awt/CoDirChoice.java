/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.Choice;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoSort;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public class CoDirChoice extends Choice {
	private Vector<CoFile> roots = null;
	private CoFile dirs[] = new CoFile[0];
	private CoFile dir = null;
	CoFile sel = null;

	private ItemListener listener = null;
    private boolean hasFocus;
	CoDirChoice() {
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { hasFocus = true; }
            public void focusLost(FocusEvent e) { hasFocus = false; }
        });
		addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				try { // HACK ( FocusListener approach doesnot work with LINUX JRE 1.3 )
					// Java version check
					Class.forName("java.awt.Choice").getMethod("hasFocus",new Class[0]);
					
					// This is Java v1.2+
					if(hasFocus()) // HACK ( LINUX JDK: BUG produces events even on API calls )
						if (ie.getStateChange() == ItemEvent.SELECTED) {
							int i = getSelectedIndex();
							if (i >= 0 && i < dirs.length)
								sel = dirs[i];
							if (listener != null)
								listener.itemStateChanged(ie);
						}
				} catch (Exception e) {
					// This Java v1.0 or v1.1 
					if(hasFocus) // HACK ( LINUX JDK: BUG produces events even on API calls )
						if (ie.getStateChange() == ItemEvent.SELECTED) {
							int i = getSelectedIndex();
							if (i >= 0 && i < dirs.length)
								sel = dirs[i];
							if (listener != null)
								listener.itemStateChanged(ie);
						}					
				}

			}
		});		
	}

	/** Sets an ActionListener. The listener will receive 
		* an action event the user finishes making a dir selection. */
	public void setDirItemListener(ItemListener listener) {
		this.listener = listener;
	}

	/** Gets directory denoted by this component.
		* @return directory denoted by component or null */
	public synchronized CoFile getDir() {
		return dir;
	}

	/** Sets directory to be denoted by this component.
		* @param dir to be denoted by component or null */
	public synchronized void setDir(final CoFile dir) {
		this.dir = dir;
		this.sel = dir;
		if (dir != null) { 
			/* Setup Roots. */
			if (roots == null) {
				roots = new Vector<CoFile>();
				CoFile[] newroots = dir.listCoRoots();
				for (int i = 0; i < newroots.length; i++)
					roots.addElement(newroots[i]);
			}
			/* Setup Order Roots & Dirs. */
			Vector<CoFile> newdirs = (Vector<CoFile>) roots.clone();
			int n = dir.getPathDepth();
			for (int i = 1; i < n; i++)
				newdirs.addElement(dir.getPathFragment(i));
			if (n >= 1)
				newdirs.addElement(dir);
            dirs = new CoFile[newdirs.size()];
            newdirs.copyInto(dirs);
			dirs = CoSort.listOrder(dirs, CoSort.ORDER_BY_PATH);
		} else {
			dirs = new CoFile[0];
		}
		removeAll();
		for (int i = 0; i < dirs.length; i++) {
			String item = "";
			for (int j = 0; j < i; j++)
				item = item + "  ";
			if (dirs[i].getPathDepth() > 0)
				item = item + dirs[i].getName();
			else
				item = dirs[i].getHost() + dirs[i].toString();
			
			add(item);
		}
		for (int i = 0; i < dirs.length; i++) {
			if(sel.equals(dirs[i])) {
				select(i);
				break;		
			}
		}
		setEnabled(dirs.length > 0);
	}

	/** Gets directory denoted by (user) selection. 
		* @return directory denoted by (user) selection or null */
	public synchronized CoFile getSelectedDir() {
		return sel;
	}

	private static final long serialVersionUID = 1L;
}