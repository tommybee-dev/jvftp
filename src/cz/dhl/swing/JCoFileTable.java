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
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/** 
 * Swing Directory Selector Combo Box Component.
 * 
 * <P><IMG SRC="JCoFileTable.gif"><BR><BR></P>
 *
 * <P><I>To listen for file selection making events:</I><BR>
 * <CODE>
 * fileList.getSelectionModel().addListSelectionListener(new ListSelectionListener()<BR>
 * {&nbsp; public void valueChanged(ListSelectionEvent e)<BR>
 * &nbsp;&nbsp; { myFileSelectHandler(); } } );
 * </CODE></P>
 *
 * <P><I>To listen for file selection entering (doubleclick) events:</I><BR>
 * <CODE>
 * fileList.addMouseListener(new MouseAdapter()<BR>
 * {&nbsp; public void mouseClicked(MouseEvent e)<BR>
 * &nbsp;&nbsp; {&nbsp; if(e.getClickCount()>1)<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; myFileEnterHandler(); } } );
 * </CODE></P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see java.io.File
 */
public class JCoFileTable extends JTable {
	private Icon directoryIcon = null;
	private Icon fileIcon = null;

	private CoFile files[] = new CoFile[0], origfiles[] = new CoFile[0];

	private int orderI = CoSort.ORDER_BY_NAME;
	private String filterS[] = {
	};

	private void installIcons() {
		if (directoryIcon == null) {
			directoryIcon = UIManager.getIcon("FileView.directoryIcon");
		}
		if (fileIcon == null) {
			fileIcon = UIManager.getIcon("FileView.fileIcon");
		}
	}

	final private DefaultTableCellRenderer renderer =
		new DefaultTableCellRenderer() {
		public void setValue(Object value) {
			if (value instanceof CoFile) {
				installIcons();
				CoFile f = (CoFile) value;
				Icon icon;
				if (f.isDirectory())
					icon = directoryIcon;
				else
					icon = fileIcon;
				if (icon != null)
					setIcon(icon);
				setText(f.getName());
			} else
				super.setValue(value);
		}
		private static final long serialVersionUID = 1L;
	};

	final private AbstractTableModel model = new AbstractTableModel() {
		String names[] = { "File Name", "Property", "Last Modified" };
		public int getColumnCount() {
			return 3;
		}
		public int getRowCount() {
			return files.length;
		}
		public Object getValueAt(int row, int col) {
			Object item;
			switch (col) {
				case 0 :
					item = files[row];
					break;
				case 1 :
					item = files[row].propertyString();
					break;
				case 2 :
					item = files[row].lastModifiedString();
					break;
				default :
					item = "";
			}
			return item;
		}
		public String getColumnName(int column) {
			return names[column];
		}
		private static final long serialVersionUID = 1L;
	};



	public JCoFileTable() {
		setModel(model);
		TableColumn column = getColumn("File Name");
		column.setCellRenderer(renderer);
		setIntercellSpacing(new Dimension(0, 0));
		setShowGrid(false);

		/* Select file on character typed */
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
				int is[] = getSelectedRows();
				if (e.getModifiers() != InputEvent.SHIFT_MASK) {
					for (int i = (is.length > 0 ? is[is.length - 1] + 1 : 0); i < files.length; i++)
						if (files[i].startsWithIgnoreCase(ch)) {
							setSelectedFile(files[i]);
							return;
						}
					for (int i = 0; i < files.length; i++)
						if (files[i].startsWithIgnoreCase(ch)) {
							setSelectedFile(files[i]);
							return;
						}
				} else {
					for (int i = (is.length > 0 ? is[is.length - 1] - 1 : 0);
						i >= 0;
						i--)
						if (files[i].startsWithIgnoreCase(ch)) {
							setSelectedFile(files[i]);
							return;
						}
					for (int i = files.length - 1; i >= 0; i--)
						if (files[i].startsWithIgnoreCase(ch)) {
							setSelectedFile(files[i]);
							return;
						}
				}
			}
		});

		try {
			// Java version check
			getClass().getMethod("getActionMap",new Class[0]);

			// This is Java v1.3+ 
			// Unmap standard JTable TAB and ENTER actions 
			class NullAction implements javax.swing.Action {
				javax.swing.Action delegate;
				//boolean setOnce = false;
				public NullAction(JTable table, javax.swing.Action delegate) {
					this.delegate = delegate;
				}
				public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
					delegate.addPropertyChangeListener(listener);
				}
				public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
					delegate.removePropertyChangeListener(listener);
				}
				public Object getValue(String key) {
					return delegate.getValue(key);
				}
				public void putValue(String key, Object value) {
					delegate.putValue(key, value);
				}
				public void setEnabled(boolean enabled) {
					delegate.setEnabled(enabled);
				}
				public boolean isEnabled() {
					return delegate.isEnabled();
				}
				public void actionPerformed(java.awt.event.ActionEvent event) {
				}
			}  
			getActionMap().put("selectNextColumnCell", new NullAction(this,getActionMap().get("selectNextColumnCell"))); 
			getActionMap().put("selectPreviousColumnCell", new NullAction(this,getActionMap().get("selectPreviousColumnCell"))); 
			getActionMap().put("selectNextRowCell", new NullAction(this,getActionMap().get("selectNextRowCell"))); 
			getActionMap().put("selectPreviousRowCell", new NullAction(this,getActionMap().get("selectPreviousRowCell"))); 
		} catch (Exception e) {
			// This is Java v1.2 
		}  
	}

	/** Gets files denoted by this component.
	 * @return files denoted by component or null */
	public CoFile[] getFiles() {
		return origfiles;
	}

	/** Sets files to be denoted by this component.
	 * @param files to be denoted by component or null */
	public void setFiles(CoFile files[]) {
		if (files == null)
			origfiles = new CoFile[0];
		else origfiles = files;
		this.files = CoSort.listSplit( CoSort.listOrder( 
			CoSort.listFilter(origfiles, filterS), orderI));
		setEnabled((this.files.length > 0));
		model.fireTableDataChanged();
	}

	/** Gets file denoted by (user) selection. 
	 * @return file denoted by (user) selection 
	 * or null for no/multiple selection */
	public CoFile getSelectedFile() {
		int is[] = getSelectedRows();
		if (is != null && is.length == 1 && is[0] >= 0 && is[0] < files.length)
			return files[is[0]];
		else
			return null;
	}

	/** Sets file denoted by selection. 
	 * @param file to be denoted by selection */
	public void setSelectedFile(CoFile file) {
		clearSelection();
		for (int j = 0; j < this.files.length; j++)
			if (this.files[j].equals(file)) {
				addRowSelectionInterval(j, j);
				break;
			}
	}

	/** Gets files denoted by (user) selection. 
	 * @return files denoted by (user) selection */
	public CoFile[] getSelectedFiles() {
		int is[] = getSelectedRows();
		CoFile fs[] = new CoFile[is.length];
		for (int i = 0; i < is.length; i++)
			fs[i] = files[is[i]];
		return fs;
	}

	/** Sets files denoted by selection. 
	 * @param files to be denoted by selection */
	public void setSelectedFiles(CoFile files[]) {
		clearSelection();
		for (int i = 0; i < files.length; i++)
			for (int j = 0; j < this.files.length; j++)
				if (this.files[j].equals(files[i])) {
					addRowSelectionInterval(j, j);
					break;
				}
	}

	/** Deselects all files denoted by (user) selection. */
	public void deselectAllFiles() {
		clearSelection();
	}

	/** Sets file order.
	 * @param order must be one of following optional values:
	 * @see cz.dhl.io.CoSort#ORDER_BY_NAME
	 * @see cz.dhl.io.CoSort#ORDER_BY_TYPE
	 * @see cz.dhl.io.CoSort#ORDER_BY_SIZE
	 * @see cz.dhl.io.CoSort#ORDER_BY_DATE
	 * @see cz.dhl.io.CoSort#ORDER_BY_PATH
	 * @see cz.dhl.io.CoSort#ORDER_BY_NONE
	 * @see cz.dhl.io.CoSort#ORDER_INVERSE */
	public void setOrder(int order) {
		this.orderI = order;
		setFiles(origfiles);
	}

	/** Sets file filter.
	 * @param filter must be array of uppercase strings with a leading '.' sign; 
	 * example: { ".TXT", ".HTM", ".HTML", etc ... } */
	public void setFilter(String filter[]) {
		this.filterS = filter;
		setFiles(origfiles);
	}

	private static final long serialVersionUID = 1L;
}
