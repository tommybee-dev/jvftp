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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.dhl.io.CoFile;

class JCoEnterDialog extends JDialog {
	static final int ENTER_RENAME = 3, ENTER_MAKEDIR = 4;

	JTextField input = new JTextField();
	JButton enter = new JButton("Enter");
	JButton cancel = new JButton("Cancel");

	JCoEventQueue queue = null;
	CoFile file = null;
	int operation = 0;

	static void newEnterDialog(
		JCoBrowsePanel browse,
		JCoEventQueue queue,
		CoFile file,
		int operation) {
		Container object = browse.getParent();
		while (object != null && !(object instanceof Frame))
			object = object.getParent();
		if (object != null) {
			JCoEnterDialog dialog =
				new JCoEnterDialog((Frame) object, queue, file, operation);
			dialog.pack();
			dialog.setVisible(true);
		}
	}

	JCoEnterDialog(Frame parent, JCoEventQueue queue, CoFile file, int operation) {
		super(parent);
		this.queue = queue;
		this.file = file;
		this.operation = operation;

		if (operation == ENTER_RENAME)
			setTitle("Rename");
		else
			setTitle("Create Directory");
		JPanel commandpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel renamepanel = new JPanel(new GridLayout(2, 1));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(renamepanel, BorderLayout.CENTER);
		if (operation == ENTER_RENAME) {
			renamepanel.add(new JLabel("Rename \"" + file.getName() + "\" to:"));
			input.setText(file.getName());
		} else
			renamepanel.add(new JLabel("Create the directory:"));
		renamepanel.add(input);
		getContentPane().add(commandpanel, BorderLayout.SOUTH);
		commandpanel.add(enter);
		commandpanel.add(cancel);

		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				enter();
			}
		});
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				enter();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				close();
			}
		});

		Point p = parent.getLocationOnScreen();
		p.translate(0, 0);
		setLocation(p);
		setResizable(false);
	}

	void enter() {
		String to = input.getText();
		close();
		if (to.length() > 0 && file != null)
			if (operation == ENTER_RENAME)
				queue.renameFile(to, file);
			else
				queue.makeDir(to, file);
	}

	void close() {
		setVisible(false);
		dispose();
	}

	private static final long serialVersionUID = 1L;
}