/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import javax.swing.SwingUtilities;

import cz.dhl.ftp.Ftp;
import cz.dhl.io.CoFile;
import cz.dhl.io.CoLoad;
import cz.dhl.io.CoSource;
import cz.dhl.io.LocalSource;
import cz.dhl.ui.CoStatus;
import cz.dhl.ui.CoTabbed;
import cz.dhl.util.CoHandleMap;

abstract class JCoEventQueue {
	boolean active = false;
	CoFile selfiles[] = new CoFile[0];
	JCoEventQueue oposite = null;

	JCoControlPanel control = null;
	JCoBrowsePanel browse = null;
	CoTabbed panel = new CoTabbed() {
		public void showCard(String name) {
		}
	};
	CoStatus status = new CoStatus() {
		public void setSize(String s) {
		}
		public void setDate(String s) {
		}
	};

	void update(CoFile dir) {
		panel.showCard("Log");
		browse.setEnabled(false);
		(new Thread(new Update(dir))).start();
	}

	class Update implements Runnable {
		CoFile dir;
		CoFile files[] = null;
		Update(CoFile dir) {
			this.dir = dir;
		}
		public void run() {
			if (dir != null) { //if(dir.isDirectory())
				files = dir.listCoFiles();
				if (files == null) {
					dir = browse.getDir();
					files = browse.getFiles();
				}
			}
			SwingUtilities.invokeLater(new UpdateUI(dir, files));
		}
	}

	class UpdateUI implements Runnable {
		CoFile dir;
		CoFile files[];
		UpdateUI(CoFile dir, CoFile files[]) {
			this.dir = dir;
			this.files = files;
		}
		public void run() {
			CoFile sel[] = browse.getSelectedFiles();
			browse.setDir(dir);
			browse.setFiles(files);
			browse.setSelectedFiles(sel);
			panel.showCard((dir != null ? "List" : "Login"));
		}
	}

	void copyFiles() {
		if (active && oposite != null) {
			CoFile files[] = browse.getSelectedFiles();
			CoFile to = oposite.browse.getDir();
			if (files != null && to != null)
				if (files.length > 0 || to != null)
					if (oposite instanceof JFtpEventQueue)
						oposite.copyFiles(to, files);
					else
						copyFiles(to, files);
		}
	}

	void copyFiles(CoFile to, CoFile files[]) {
		panel.showCard("Log");
		JCoProgressDialog progress =
			JCoProgressDialog.newProgressDialog(browse, JCoProgressDialog.PROGRESS_COPY);
		if (progress != null)
			 (new Thread(new CopyFiles(progress, to, files))).start();
	}

	class CopyFiles implements Runnable {
		JCoProgressDialog progress;
		CoFile to;
		CoFile files[];
		CopyFiles(JCoProgressDialog progress, CoFile to, CoFile files[]) {
			this.progress = progress;
			this.to = to;
			this.files = files;
		}
		public void run() {
			CoLoad.copy(to, files, progress);
			SwingUtilities.invokeLater(new CopyFilesUI(progress));
		}
	}

	class CopyFilesUI implements Runnable {
		JCoProgressDialog progress;
		CopyFilesUI(JCoProgressDialog progress) {
			this.progress = progress;
		}
		public void run() {
			progress.close();
			update(browse.getDir());
			oposite.update(oposite.browse.getDir());
		}
	}

	void deleteFiles() {
		if (active) {
			CoFile files[] = browse.getSelectedFiles();
			if (files != null)
				if (files.length > 0)
					deleteFiles(files);
		}
	}

	void deleteFiles(CoFile files[]) {
		panel.showCard("Log");
		JCoProgressDialog progress =
			JCoProgressDialog.newProgressDialog(browse, JCoProgressDialog.PROGRESS_DELETE);
		if (progress != null)
			 (new Thread(new DeleteFiles(progress, files))).start();
	}

	class DeleteFiles implements Runnable {
		JCoProgressDialog progress;
		CoFile files[];
		DeleteFiles(JCoProgressDialog progress, CoFile files[]) {
			this.progress = progress;
			this.files = files;
		}
		public void run() {
			CoLoad.delete(files, progress);
			SwingUtilities.invokeLater(new DeleteFilesUI(progress));
		}
	}

	class DeleteFilesUI implements Runnable {
		JCoProgressDialog progress;
		DeleteFilesUI(JCoProgressDialog progress) {
			this.progress = progress;
		}
		public void run() {
			progress.close();
			update(browse.getDir());
		}
	}

	void renameFile() {
		if (active) {
			CoFile file = browse.getSelectedFile();
			if (file != null)
				JCoEnterDialog.newEnterDialog(browse, this, file, JCoEnterDialog.ENTER_RENAME);
		}
	}

	void renameFile(String to, CoFile file) {
		panel.showCard("Log");
		JCoProgressDialog progress =
			JCoProgressDialog.newProgressDialog(browse, JCoProgressDialog.PROGRESS_RENAME);
		if (progress != null) {
			progress.setFile(file.newFileRename(to), file);
			(new Thread(new RenameFile(progress, file.newFileRename(to), file))).start();
		}
	}

	class RenameFile implements Runnable {
		JCoProgressDialog progress;
		CoFile to, file;
		RenameFile(JCoProgressDialog progress, CoFile to, CoFile file) {
			this.progress = progress;
			this.to = to;
			this.file = file;
		}
		public void run() {
			file.renameTo(to);
			SwingUtilities.invokeLater(new DeleteFilesUI(progress));
		}
	}

	void makeDir() {
		if (active) {
			CoFile cur = browse.getDir();
			if (cur != null)
				JCoEnterDialog.newEnterDialog(browse, this, cur, JCoEnterDialog.ENTER_MAKEDIR);
		}
	}

	void makeDir(String dir, CoFile cur) {
		panel.showCard("Log");
		JCoProgressDialog progress =
			JCoProgressDialog.newProgressDialog(browse, JCoProgressDialog.PROGRESS_MAKEDIR);
		if (progress != null) {
			progress.setFile(cur.newFileChild(dir));
			(new Thread(new MakeDir(progress, dir, cur))).start();
		}
	}

	class MakeDir implements Runnable {
		JCoProgressDialog progress;
		String dir;
		CoFile cur;
		MakeDir(JCoProgressDialog progress, String dir, CoFile cur) {
			this.progress = progress;
			this.dir = dir;
			this.cur = cur;
		}
		public void run() {
			(cur.newFileChild(dir)).mkdir();
			SwingUtilities.invokeLater(new DeleteFilesUI(progress));
		}
	}

	void dirEvent() {
		update(browse.getSelectedDir());
	}
	void focusEvent() {
		if (oposite != null && oposite.active) {
			oposite.active = false;
			oposite.selfiles = oposite.browse.getSelectedFiles();
			oposite.browse.deselectAllFiles();
			oposite.browse.setActiveBorder(false);
		}
		if (!active) {
			active = true;
			browse.setSelectedFiles(selfiles);
			browse.setActiveBorder(true);
		}
	}
	void statusEvent() {
		CoFile f = browse.getSelectedFile();
		if (f != null) {
			status.setSize(f.propertyString());
			status.setDate(f.lastModifiedString());
		} else {
			CoFile fs[] = browse.getSelectedFiles();
			if (fs != null) {
				long sum = 0;
				for (int i = 0; i < fs.length; i++)
					sum += fs[i].length();
				status.setSize("" + sum);
				status.setDate(fs.length + " files");
			}
		}
	}
	void fileEvent() {
		CoFile f = browse.getSelectedFile();
		if (f != null)
			update(f);
	}

	protected static CoHandleMap threads = new CoHandleMap();
	static JCoEventQueue getEventQueue(CoSource source) {
		JCoEventQueue thread = (JCoEventQueue) threads.get(source);
		if (thread == null)
			if (source instanceof LocalSource) {
				thread = new JLocalEventQueue();
				threads.put(source, thread);
			} else if (source instanceof Ftp) {
				thread = new JFtpEventQueue();
				threads.put(source, thread);
			}
		return thread;
	}
}