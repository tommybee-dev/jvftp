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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/* SINCE 1.3
import java.util.Timer; */

import cz.dhl.io.CoFile;
import cz.dhl.ui.CoProgress;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
class CoProgressDialog extends Dialog implements CoProgress {

	static final int 
		PROGRESS_COPY = 1,
		PROGRESS_DELETE = 2,
		PROGRESS_RENAME = 3,
		PROGRESS_MAKEDIR = 4;

	Label filelabel = null;
	Label tolabel = null;
	Label progress = null;
	Button cancel = new Button("Cancel");

	CoFile file = null;
	CoFile to = null;
	boolean abort = false;
	long done, total, delay;

	static CoProgressDialog newProgressDialog(CoBrowsePanel browse,int operation) {
		Container object = browse.getParent();
		while (object != null && !(object instanceof Frame))
			object = object.getParent();
		if (object != null) {
			CoProgressDialog dialog = new CoProgressDialog((Frame) object, operation);
			dialog.pack();
			dialog.setVisible(true);
			return dialog;
		} else
			return null;
	}

	CoProgressDialog(Frame parent, int operation) {
		super(parent); /* this.operation = operation; */
		done = 0;

		int gridy = 1;
		switch (operation) {
			case PROGRESS_COPY :
				setTitle("Copying ...");
				gridy = 3;
				break;
			case PROGRESS_DELETE :
				setTitle("Deleting ...");
				gridy = 1;
				break;
			case PROGRESS_RENAME :
				setTitle("Renaming ...");
				gridy = 2;
				break;
			case PROGRESS_MAKEDIR :
				setTitle("Creating Dir ...");
				gridy = 1;
				break;
		}
		Panel statuspanel = new Panel(new GridLayout(gridy, 1));
		Panel commandpanel = new Panel(new FlowLayout(FlowLayout.CENTER));

		setLayout(new BorderLayout());
		add(statuspanel, BorderLayout.CENTER);
		filelabel = new Label("File: ");
		statuspanel.add(filelabel);
		if (operation != PROGRESS_DELETE && operation != PROGRESS_MAKEDIR) {
			tolabel = new Label("To: ");
			statuspanel.add(tolabel);
		}
		if (operation == PROGRESS_COPY) {
			progress = new Label("");
			statuspanel.add(progress);
		}
		add(commandpanel, BorderLayout.SOUTH);
		commandpanel.add(cancel);

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				abort();
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				abort();
			}
		});

		Point p = parent.getLocationOnScreen();
		p.translate(0, 0);
		setLocation(p);
		setResizable(false);
	}

	void close() {
		abort();
		setVisible(false);
		dispose();
	}

	/** Thread safe. */
	public void setFile(CoFile newfile) {
		this.file = newfile;
        /* SINCE 1.3
		synchronized (this) {
			if (repeat != null)
				repeat.cancel();
			repeat = null;
		} */
		(new Thread(remove)).start();
	}

	Runnable remove = new Runnable() {
		public void run() {
			filelabel.setText("File: " + file.toString());
			if (progress != null)
				progress.setText("");
			pack();
		}
	};

	/** Thread safe. */
	public void setFile(CoFile newto, CoFile newfile) {
		this.to = newto;
		this.file = newfile;
		done = 0;
		total = file.length();
		delay = 0;
        /* SINCE 1.3
        synchronized (this) {
             if (repeat != null)
				repeat.cancel();
				   repeat=new Timer();
					 repeat.schedule(new TimerTask()
						{  public void run()
						   {  System.out.println("Timer tick"); } } 
						,500,500); 
		} */
		(new Thread(copy)).start();
	}

	Runnable copy = new Runnable() {
		public void run() {
			tolabel.setText("To: " + to.toString());
			filelabel.setText("File: " + file.toString());
			if (progress != null)
				if (!file.isDirectory())
					progress.setText(
					/*""+done+*/ "? of " + total);
				else
					progress.setText("");
			pack();
		}
	};

    /* SINCE 1.3
	Timer repeat = null; */
	Runnable status = new Runnable() {
		public void run() {
			if (progress != null)
				progress.setText(
					"" + done + " of " + total + (delay > 0 ? " " + delay + "sec no answer" : ""));
		}
	};

	/** Thread safe. */
	public void setProgress(int increment) {
		done += increment;
		if (increment > 0)
			delay = 0;
	}

	public void setDelay(long increment) {
		delay += (int) (increment / 1000);
	}

	public boolean isAborted() {
		return abort;
	}

	public void abort() {
        /* SINCE 1.3
		synchronized (this) {
			if (repeat != null)
				repeat.cancel();
			repeat = null;
		} */
		abort = true;
	}

	private static final long serialVersionUID = 1L;
}