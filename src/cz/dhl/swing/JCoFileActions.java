/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import cz.dhl.io.CoSort;

public class JCoFileActions {

	protected Action deleteAction = new DeleteAction();
	final class DeleteAction extends AbstractAction {
		DeleteAction() {
			putValue(NAME, "Delete");
			putValue(SHORT_DESCRIPTION, "Delete selected file");
		}
		public void actionPerformed(ActionEvent actionEvent) {
			delete();
		}
		private static final long serialVersionUID = 1L;
	}

	protected Action renameAction = new RenameAction();
	final class RenameAction extends AbstractAction {
		RenameAction() {
			putValue(NAME, "Rename ...");
			putValue(SHORT_DESCRIPTION, "Rename selected file ...");
		}
		public void actionPerformed(ActionEvent actionEvent) {
			rename();
		}
		private static final long serialVersionUID = 1L;
	}

	protected Action newDirAction = new NewDirAction();
	final class NewDirAction extends AbstractAction {
		NewDirAction() {
			putValue(NAME, "New Dir");
			putValue(SHORT_DESCRIPTION, "Create new directory ...");
		}
		public void actionPerformed(ActionEvent actionEvent) {
			newDir();
		}
		private static final long serialVersionUID = 1L;
	}

	protected Action refreshAction = new RefreshAction();
	final class RefreshAction extends AbstractAction {
		RefreshAction() {
			putValue(NAME, "Refresh");
			putValue(SHORT_DESCRIPTION, "Refresh");
		}
		public void actionPerformed(ActionEvent actionEvent) {
			newDir();
		}
		private static final long serialVersionUID = 1L;
	}

	private static final String SORT_SEQ_ID = "SORT_SEQ_ID";
	protected Action sortByAction = new SortByAction();
	final class SortByAction extends AbstractAction {
		SortByAction() {}
		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getSource() instanceof JMenuItem) {
				Integer sort = (Integer)
					((JMenuItem)actionEvent.getSource()).
					getClientProperty(SORT_SEQ_ID);
				sortBy(sort!=null ? sort.intValue() : CoSort.ORDER_BY_NONE);
			}
		}
		private static final long serialVersionUID = 1L;
	}

	public JCoFileActions() {}

	protected void delete() {}
	protected void rename() {}
	protected void newDir() {}
	protected void refresh() {}
	protected void sortBy(int sort) {}

	private JMenuItem createMenuItem(String name, String description, Action action, int value) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setAction(action);
		menuItem.setToolTipText(description);
		menuItem.putClientProperty(SORT_SEQ_ID, new Integer(value));
		return menuItem;
	}

	/** Creates a new instance of JCoFilePopupMenu */
	JPopupMenu createFilePopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(deleteAction);
		menu.add(renameAction);
		menu.add(newDirAction);
		menu.add(new JSeparator());
		JMenu sortBy = new JMenu("Sort by");
		menu.add(sortBy);
			sortBy.add(createMenuItem("Name", "Sort by Name", 
				sortByAction, CoSort.ORDER_BY_NAME));
			sortBy.add(createMenuItem("Type", "Sort by Type", 
				sortByAction, CoSort.ORDER_BY_TYPE));
			sortBy.add(createMenuItem("Date", "Sort by Date", 
				sortByAction, CoSort.ORDER_BY_DATE));
			sortBy.add(createMenuItem("Size", "Sort by Size", 
				sortByAction, CoSort.ORDER_BY_SIZE));
			sortBy.add(createMenuItem("None", "Sort by None", 
				sortByAction, CoSort.ORDER_BY_NONE));
		JMenuItem filterBy = new JMenuItem("Filter ...");
		menu.add(filterBy);
		menu.add(new JSeparator());
		menu.add(refreshAction);
		return menu;
	}

	/** Creates a new instance of JCoFilePopupMenu */
	JMenu createFileMenu() {
		JMenu menu = new JMenu();
		JMenuItem delete = new JMenuItem("Delete");
		menu.add(deleteAction);
		menu.add(renameAction);
		menu.add(newDirAction);
		menu.add(new JSeparator());
		JMenu sortBy = new JMenu("Sort by");
		menu.add(sortBy);
			sortBy.add(createMenuItem("Name", "Sort by Name", 
				sortByAction, CoSort.ORDER_BY_NAME));
			sortBy.add(createMenuItem("Type", "Sort by Type", 
				sortByAction, CoSort.ORDER_BY_TYPE));
			sortBy.add(createMenuItem("Date", "Sort by Date", 
				sortByAction, CoSort.ORDER_BY_DATE));
			sortBy.add(createMenuItem("Size", "Sort by Size", 
				sortByAction, CoSort.ORDER_BY_SIZE));
			sortBy.add(createMenuItem("None", "Sort by None", 
				sortByAction, CoSort.ORDER_BY_NONE));
		JMenuItem filterBy = new JMenuItem("Filter ...");
		menu.add(filterBy);
		menu.add(new JSeparator());
		menu.add(refreshAction);
		return menu;
	}
}