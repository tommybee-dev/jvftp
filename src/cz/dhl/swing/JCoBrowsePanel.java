/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoSort;
import cz.dhl.io.CoSource;

abstract class JCoBrowsePanel extends JPanel {
	/* Dir Items */
	final private JCoDirComboBox dirChoice = new JCoDirComboBox();
	/* File Items */
	final private JCoFileTable fileList = new JCoFileTable();
	final private JScrollPane scroll = new JScrollPane(fileList);
	final private Border normalborder = BorderFactory.createLineBorder(Color.gray);
	final private Border activeborder = BorderFactory.createLineBorder(Color.red);

	/* Filter Items */
	final private JComboBox<String> filterChoice = new JComboBox<String>();
	private String filterSs[][] = { {}, {
			".TXT" }, {
			".HTM", ".HTML", ".SHTML" }, {
			".CSS", ".JS", ".PL", ".PHP" }, {
			".CLASS", ".JAR", ".ZIP", ".CAB" }, {
			".GIF", ".JPG", ".PNG" }, {
			".H", ".C", ".HPP", ".CPP", ".JAVA" }, {
			".SQL", ".4GL" }, {
			".BAT", ".SH", ".AWK" }
	};

	/* Order Items */
	final private JComboBox<String> orderChoice = new JComboBox<String>();
	
	final private int orderIs[] = {
			CoSort.ORDER_BY_NAME,
			CoSort.ORDER_BY_TYPE,
			CoSort.ORDER_BY_SIZE,
			CoSort.ORDER_BY_DATE,
			CoSort.ORDER_BY_NONE };

	private JCoEventQueue queue = null;

	JCoBrowsePanel(CoSource source) { 
		/* Variables */
		queue = JCoEventQueue.getEventQueue(source);

		/* State */
		dirChoice.setEnabled(false);

		fileList.setEnabled(false);

		filterChoice.addItem("All Files (*)");
		filterChoice.addItem("Text Files (txt)");
		filterChoice.addItem("HTML Files (htm,html,shtml)");
		filterChoice.addItem("Stype&Script Files (css,js,pl,php)");
		filterChoice.addItem("Java Class Files (class,jar,zip,cab)");
		filterChoice.addItem("Image Files (gif,jpg,png)");
		filterChoice.addItem("Source Files (h,c,hpp,cpp,java)");
		filterChoice.addItem("Database Query Files (sql,4gl)");
		filterChoice.addItem("Batch Files (bat,sh,awk)");

		orderChoice.addItem("Name");
		orderChoice.addItem("Type");
		orderChoice.addItem("Size");
		orderChoice.addItem("Date");
		orderChoice.addItem("None");

		/* Structure */
		JPanel toppanel = new JPanel(new BorderLayout()),
			bottompanel = new JPanel(new BorderLayout()),
			typepanel = new JPanel(new BorderLayout()),
			orderpanel = new JPanel(new BorderLayout());
		setLayout(new BorderLayout());
		add("North", toppanel);
		toppanel.add("West", new JLabel("Look in: "));
		toppanel.add("Center", dirChoice);
		scroll.setPreferredSize(new Dimension(300, 300));
		scroll.setBorder(normalborder);
		add("Center", scroll);
		add("South", bottompanel);
		bottompanel.add("North", typepanel);
		typepanel.add("West", new JLabel("File Type: "));
		typepanel.add("Center", filterChoice);
		bottompanel.add("Center", orderpanel);
		orderpanel.add("West", new JLabel("Order by: "));
		orderpanel.add("Center", orderChoice);

		/* Listeners */
		dirChoice.setDirActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queue.dirEvent();
			}
		});
		fileList.getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				queue.statusEvent();
			}
		});
		fileList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1)
					queue.fileEvent();
			}
		});
		fileList.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				queue.focusEvent();
			}
		});

		fileList.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					queue.fileEvent();
			}
		}); 
		
		try {
			// Java version check
			getClass().getMethod("getActionMap",new Class[0]);
			
			// This is Java v1.3+ 
			// Handle SHIFT-TAB and TAB actions 
			fileList.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {  
					if (e.getKeyCode() == KeyEvent.VK_TAB) 
						if(e.isShiftDown()) {
							// select dirChoice on SHIFT-TAB 
							dirChoice.requestFocus(); 
						} else {
							// select oposite fileList on TAB 
							queue.oposite.browse.fileList.requestFocus(); 
						} 
				}
			}); 
		} catch (Exception e) {
			// This is Java v1.2
		}
					
		filterChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterEvent();
			}
		});
		orderChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				orderEvent();
			}
		});
	}

	void filterEvent() {
		fileList.setFilter(filterSs[filterChoice.getSelectedIndex()]);
	}
	void orderEvent() {
		fileList.setOrder(orderIs[orderChoice.getSelectedIndex()]);
	}

	public void setEnabled(boolean e) {
		dirChoice.setEnabled(false);
		fileList.setEnabled(false);
	}

	/** Gets directory denoted by this component.
	 * @return directory denoted by component or null */
	CoFile getDir() {
		return dirChoice.getDir();
	}
	/** Sets directory to be denoted by this component.
	 * @parameter dir to be denoted by component or null */
	void setDir(CoFile dir) {
		dirChoice.setDir(dir);
	}

	/** Gets files denoted by this component.
	 * @return files denoted by component or null */
	CoFile[] getFiles() {
		return fileList.getFiles();
	}
	/** Sets files to be denoted by this component.
	 * @parameter files to be denoted by component or null */
	void setFiles(CoFile files[]) {
		fileList.setFiles(files);
	}

	/** Gets directory denoted by (user) selection. 
	 * @return directory denoted by (user) selection or null */
	CoFile getSelectedDir() {
		return dirChoice.getSelectedDir();
	}

	/** <P>Get selected file.</P>
	 * @return the selected file handle, or null if no file
	 * is selected, or if more that one file is selected. */
	public CoFile getSelectedFile() {
		return fileList.getSelectedFile();
	}
	/** Sets file denoted by selection. 
	 * @parameter file to be denoted by selection */
	public void setSelectedFile(CoFile file) {
		fileList.setSelectedFile(file);
	}

	/** <P>Get multiple selected files.</P>
	 * @return an array of the selected files of this browse 
	 * panel. If no files are selected, a zero-length array 
	 * is returned. */
	public CoFile[] getSelectedFiles() {
		return fileList.getSelectedFiles();
	}
	/** Sets files denoted by selection. 
	 * @parameter files to be denoted by selection */
	public void setSelectedFiles(CoFile files[]) {
		fileList.setSelectedFiles(files);
	}

	/** Deselects all files denoted by (user) selection. */
	void deselectAllFiles() {
		fileList.deselectAllFiles();
	}
	/** Sets active/normal border apperance. */
	public void setActiveBorder(boolean active) {
		if (active)
			scroll.setBorder(activeborder);
		else
			scroll.setBorder(normalborder);
	}

	void setOpositePanel(JCoBrowsePanel oposite) {
		queue.oposite = oposite.queue;
	}

	private static final long serialVersionUID = 1L;
}