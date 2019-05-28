/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoSort;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.UIManager;

/** 
 * Swing Directory Selector Combo Box Component.
 * 
 * <P><IMG SRC="JCoDirComboBox.gif"></P>
 *
 * <P><I>To listen for dir selection events:</I><BR>
 * <CODE>
 * dirChoice.setDirActionListener(new ActionListener()<BR>
 * {&nbsp;&nbsp;public void actionPerformed(ActionEvent e)<BR>
 * &nbsp;&nbsp;&nbsp;{&nbsp;&nbsp;myDirSelectHandler(); } } ); 
 * </CODE></P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see java.io.File
 */
public class JCoDirComboBox extends JComboBox {
	private Icon hardDriveIcon = null;
	private Icon directoryIcon = null;

	private Vector<CoFile> roots = null;
	private CoFile dirs[] = new CoFile[0];
	private CoFile dir = null;
	CoFile sel = null;

	private ActionListener listener = null;

	private void installIcons() {
		if (hardDriveIcon == null) {
			hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
		}
		if (directoryIcon == null) {
			directoryIcon = UIManager.getIcon("FileView.directoryIcon");
		}
	}

	private class DirRenderer extends DefaultListCellRenderer {
		IndentIcon indent = new IndentIcon();
		public Component getListCellRendererComponent(
			JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof CoFile) {
				CoFile d = (CoFile) value;
				indent.depth = d.getPathDepth();
				installIcons();
				if (indent.depth > 0)
					indent.icon = directoryIcon;
				else
					indent.icon = hardDriveIcon;
				setIcon(indent);
				if (indent.depth > 0)
					setText(d.getName());
				else
					setText(d.getHost() + d.toString());
			}
			return this;
		}
		private static final long serialVersionUID = 1L;
	};

	private final static int space = 10;
	private class IndentIcon implements Icon {
		Icon icon = null;
		int depth = 0;
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (icon != null)
				icon.paintIcon(c, g, x + depth * space, y);
		}
		public int getIconWidth() {
			return depth * space + (icon != null ? icon.getIconWidth() : 0);
		}
		public int getIconHeight() {
			return (icon != null ? icon.getIconHeight() : 1);
		}
	}

	private class DirModel extends AbstractListModel implements ComboBoxModel {
		public void update() {
			fireContentsChanged(this, 0, dirs.length - 1);
		}
		public int getSize() {
			return dirs.length;
		}
		public Object getElementAt(int index) {
			return dirs[index];
		}
		public void setSelectedItem(Object anItem) {
			sel = (CoFile) anItem;
			if (listener != null)
				listener.actionPerformed(
					new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sel.toString()));
			fireContentsChanged(this, -1, -1);
		}
		public Object getSelectedItem() {
			return sel;
		}
		private static final long serialVersionUID = 1L;
	};

	public JCoDirComboBox() {
		setModel(new DirModel());
		setRenderer(new DirRenderer());
	}

	/** Sets an ActionListener. The listener will receive 
		* an action event the user finishes making a dir selection. */
	public void setDirActionListener(ActionListener listener) {
		this.listener = listener;
	}

	/** Gets directory denoted by this component.
		* @return directory denoted by component or null */
	public CoFile getDir() {
		return dir;
	}

	/** Sets directory to be denoted by this component.
		* @param dir to be denoted by component or null */
	public void setDir(final CoFile dir) {
		this.dir = dir;
		this.sel = dir;
		if (dir != null) { /* Setup Roots. */
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
		((DirModel) getModel()).update();
		setEnabled(dirs.length > 0);
	}

	/** Gets directory denoted by (user) selection. 
		* @return directory denoted by (user) selection or null */
	public CoFile getSelectedDir() {
		return sel;
	}

	private static final long serialVersionUID = 1L;
}
