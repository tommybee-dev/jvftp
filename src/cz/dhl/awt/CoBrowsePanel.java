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
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoSort;
import cz.dhl.io.CoSource;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
abstract class CoBrowsePanel extends Panel {
	/* Dir Items */
	private CoDirChoice dirChoice = new CoDirChoice();
	/* File Items */
	private CoFileList fileList = new CoFileList();
	/* Filter Items */
	Choice filterChoice = new Choice();

	String filterNames[] = {
		"All Files (*)",
		"Text Files (txt)",
		"HTML Files (htm,html,shtml)",
		"Stype&Script Files (css,js,pl)",
		"Image Files (gif,jpg,png)",
		"Source Files (h,c,hpp,cpp,java)",
		"Database Query Files (sql,4gl)" 
	};

	String filterSs[][] = { {}, {
		".TXT" }, {
		".HTM", ".HTML", ".SHTML" }, {
		".CSS", ".JS", ".PL" }, {
		".GIF", ".JPG", ".PNG" }, {
		".H", ".C", ".HPP", ".CPP", ".JAVA" }, {
		".SQL", ".4GL" }
	};

	/* Order Items */
	Choice orderChoice = new Choice();
	int orderIs[] = {
		CoSort.ORDER_BY_NAME,
		CoSort.ORDER_BY_TYPE,
		CoSort.ORDER_BY_SIZE,
		CoSort.ORDER_BY_DATE,
		CoSort.ORDER_BY_NONE 
	};

	private CoEventQueue queue = null;

	CoBrowsePanel(CoSource source) { 
		/* Variables */
		queue = CoEventQueue.getEventQueue(source);

		/* State */
		dirChoice.setEnabled(false);

		fileList.setEnabled(false);

		filterChoice.add("All Files (*)");
		filterChoice.add("Text Files (txt)");
		filterChoice.add("HTML Files (htm,html,shtml)");
		filterChoice.add("Stype&Script Files (css,js,pl)");
		filterChoice.add("Image Files (gif,jpg,png)");
		filterChoice.add("Source Files (h,c,hpp,cpp,java)");
		filterChoice.add("Database Query Files (sql,4gl)");

		orderChoice.add("Name");
		orderChoice.add("Type");
		orderChoice.add("Size");
		orderChoice.add("Date");
		orderChoice.add("None");

		/* Structure */
		Panel toppanel = new Panel(new BorderLayout()),
			bottompanel = new Panel(new BorderLayout()),
			filterpanel = new Panel(new BorderLayout()),
			orderpanel = new Panel(new BorderLayout());
		setLayout(new BorderLayout());
		add(toppanel, BorderLayout.NORTH);
		toppanel.add("West", new Label("Look in:"));
		toppanel.add("Center", dirChoice);
		add(fileList, BorderLayout.CENTER);
		add(bottompanel, BorderLayout.SOUTH);
		bottompanel.add("North", filterpanel);
		filterpanel.add("West", new Label("File Type:"));
		filterpanel.add("Center", filterChoice);
		bottompanel.add("Center", orderpanel);
		orderpanel.add("West", new Label("Order by:"));
		orderpanel.add("Center", orderChoice);

		/* Listeners */
		dirChoice.setDirItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					queue.dirEvent();
			}
		});
		fileList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.fileEvent();
			}
		});
		fileList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) { 
				/*if(e.getStateChange()==e.SELECTED)*/
				queue.statusEvent();
			}
		});
		filterChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					filterEvent();
			}
		});
		orderChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					orderEvent();
			}
		});
	}

	public void setEnabled(boolean b) {
		dirChoice.setEnabled(b);
		fileList.setEnabled(b);
	}

	void filterEvent() {
		fileList.setFilter(filterSs[filterChoice.getSelectedIndex()]);
	}
	void orderEvent() {
		fileList.setOrder(orderIs[orderChoice.getSelectedIndex()]);
	}

	CoFile getDir() {
		return dirChoice.getDir();
	}
	void setDir(CoFile dir) {
		dirChoice.setDir(dir);
	}
	CoFile[] getFiles() {
		return fileList.getFiles();
	}
	void setFiles(CoFile files[]) {
		fileList.setFiles(files);
	}

	CoFile getSelectedDir() {
		return dirChoice.getSelectedDir();
	}
	/** <P>Get selected file.</P>
	 * @return the selected file handle, or null if no file
	 * is selected, or if more that one file is selected. */
	public CoFile getSelectedFile() {
		return fileList.getSelectedFile();
	}
	/** <P>Get multiple selected files.</P>
	 * @return an array of the selected files of this browse 
	 * panel. If no files are selected, a zero-length array 
	 * is returned. */
	public CoFile[] getSelectedFiles() {
		return fileList.getSelectedFiles();
	}

	void deselectAllFiles() {
		fileList.deselectAllFiles();
	}

	void setOpositePanel(CoBrowsePanel oposite) {
		queue.oposite = oposite.queue;
	}

	private static final long serialVersionUID = 1L;
}