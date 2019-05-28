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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import cz.dhl.io.CoFile;
import cz.dhl.ui.CoProgress;

class JCoProgressDialog extends JDialog implements CoProgress {
	static final int PROGRESS_COPY = 1,
		PROGRESS_DELETE = 2,
		PROGRESS_RENAME = 3,
		PROGRESS_MAKEDIR = 4;

	JLabel filelabel = null;
	JLabel tolabel = null;
	JLabel progress = null;
	JButton cancel = new JButton("Cancel");

	CoFile file = null;
	CoFile to = null;
	boolean abort = false;
	long done, total, delay;

	static JCoProgressDialog newProgressDialog(
		JCoBrowsePanel browse,
		int operation) {
		Container object = browse.getParent();
		while (object != null && !(object instanceof Frame))
			object = object.getParent();
		if (object != null) {
			JCoProgressDialog dialog = new JCoProgressDialog((Frame) object, operation);
			dialog.pack();
			dialog.setVisible(true);
			return dialog;
		} else
			return null;
	}

	JCoProgressDialog(Frame parent, int operation) {
		super(parent); 
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
		JPanel statuspanel = new JPanel(new GridLayout(gridy, 1));
		JPanel commandpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(statuspanel, BorderLayout.CENTER);
		filelabel = new JLabel("File: ");
		statuspanel.add(filelabel);
		if (operation != PROGRESS_DELETE && operation != PROGRESS_MAKEDIR) {
			tolabel = new JLabel("To: ");
			statuspanel.add(tolabel);
		}
		if (operation == PROGRESS_COPY) {
			progress = new JLabel("");
			statuspanel.add(progress);
		}
		getContentPane().add(commandpanel, BorderLayout.SOUTH);
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
		setRefreshRate(0);
		SwingUtilities.invokeLater(remove);
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

		setRefreshRate(300);
		SwingUtilities.invokeLater(copy);
	}

	Runnable copy = new Runnable() {
		public void run() {
			tolabel.setText("To: " + to.toString());
			filelabel.setText("File: " + file.toString());
			if (progress != null)
				if (!file.isDirectory())
					progress.setText("" + done + " of " + total);
				else
					progress.setText("");
			pack();
		}
	};

	Timer fresher = null;
	public void setRefreshRate(int rate) {
		if (rate != 0) {
			if (fresher == null) {
				fresher = new Timer(rate, refreshEvent);
				fresher.start();
			}
			fresher.setDelay(rate);
		} else if (fresher != null) {
			fresher.stop();
			fresher.removeActionListener(refreshEvent);
			fresher = null;
		}
	}

	ActionListener refreshEvent = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
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
		setRefreshRate(0);
		abort = true;
	}

	private static final long serialVersionUID = 1L;
}