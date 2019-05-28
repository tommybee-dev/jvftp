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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cz.dhl.io.CoFile;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
class CoEnterDialog extends Dialog {
	static final int 
		ENTER_RENAME = 3, 
		ENTER_MAKEDIR = 4;

	TextField input = new TextField();
	Button enter = new Button("Enter");
	Button cancel = new Button("Cancel");

	CoEventQueue queue = null;
	CoFile file = null;
	int operation = 0;

	static void newEnterDialog(
		CoBrowsePanel browse,
		CoEventQueue queue,
		CoFile file,
		int operation) {
		Container object = browse.getParent();
		while (object != null && !(object instanceof Frame))
			object = object.getParent();
		if (object != null) {
			CoEnterDialog dialog =
				new CoEnterDialog((Frame) object, queue, file, operation);
			dialog.pack();
			dialog.setVisible(true);
		}
	}

	CoEnterDialog(Frame parent, CoEventQueue queue, CoFile file, int operation) {
		super(parent);
		this.queue = queue;
		this.file = file;
		this.operation = operation;

		if (operation == ENTER_RENAME)
			setTitle("Rename");
		else
			setTitle("Create Directory");
		Panel commandpanel = new Panel(new FlowLayout(FlowLayout.CENTER));
		Panel renamepanel = new Panel(new GridLayout(2, 1));
		setLayout(new BorderLayout());
		add(renamepanel, BorderLayout.CENTER);
		if (operation == ENTER_RENAME) {
			renamepanel.add(new Label("Rename \"" + file.getName() + "\" to:"));
			input.setText(file.getName());
		} else
			renamepanel.add(new Label("Create the directory:"));
		renamepanel.add(input);
		add(commandpanel, BorderLayout.SOUTH);
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