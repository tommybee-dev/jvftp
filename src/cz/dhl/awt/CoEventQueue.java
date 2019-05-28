/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import cz.dhl.ftp.Ftp;
import cz.dhl.io.CoFile;
import cz.dhl.io.CoLoad;
import cz.dhl.io.CoSource;
import cz.dhl.io.LocalSource;
import cz.dhl.ui.CoStatus;
import cz.dhl.ui.CoTabbed;
import cz.dhl.util.CoHandleMap;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
abstract class CoEventQueue {
	boolean active = false;
	CoEventQueue oposite = null;

	CoControlPanel control = null;
	CoBrowsePanel browse = null;
	CoTabbed panel = new CoTabbed() {
		public void showCard(String name) {}
	};
	CoStatus status = new CoStatus() {
		public void setSize(String s) {}
		public void setDate(String s) {}
	};

	void update(CoFile dir) {
		panel.showCard("Log");
		browse.setEnabled(false);
		(new Thread(new Update(dir))).start();
	}

	class Update implements Runnable {
		CoFile dir;
		CoFile files[];
		Update(CoFile dir) {
			this.dir = dir;
		}
		public void run() {
			if (dir != null) {
				if (dir.isDirectory())
					files = dir.listCoFiles();
				if (files == null) {
					dir = browse.getDir();
					files = browse.getFiles();
				}
			}
			browse.setDir(dir);
			browse.setFiles(files);
			panel.showCard((dir != null ? "List" : "Login"));
		}
	}

	void copyFiles() {
		if (active && oposite != null) {
			CoFile files[] = browse.getSelectedFiles();
			CoFile to = oposite.browse.getDir();
			if (files != null && to != null)
				if (files.length > 0 || to != null)
					if (oposite instanceof FtpEventQueue)
						oposite.copyFiles(to, files);
					else
						copyFiles(to, files);
		}
	}

	void copyFiles(CoFile to, CoFile files[]) {
		panel.showCard("Log");
		CoProgressDialog progress =
			CoProgressDialog.newProgressDialog(browse, CoProgressDialog.PROGRESS_COPY);
		if (progress != null)
			 (new Thread(new CopyFiles(progress, to, files))).start();
	}

	class CopyFiles implements Runnable {
		CoProgressDialog progress;
		CoFile to;
		CoFile files[];
		CopyFiles(CoProgressDialog progress, CoFile to, CoFile files[]) {
			this.progress = progress;
			this.to = to;
			this.files = files;
		}
		public void run() {
			CoLoad.copy(to, files, progress);

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
		CoProgressDialog progress =
			CoProgressDialog.newProgressDialog(browse, CoProgressDialog.PROGRESS_DELETE);
		if (progress != null)
			 (new Thread(new DeleteFiles(progress, files))).start();
	}

	class DeleteFiles implements Runnable {
		CoProgressDialog progress;
		CoFile files[];
		DeleteFiles(CoProgressDialog progress, CoFile files[]) {
			this.progress = progress;
			this.files = files;
		}
		public void run() {
			CoLoad.delete(files, progress);

			progress.close();
			update(browse.getDir());
		}
	}

	void renameFile() {
		if (active) {
			CoFile file = browse.getSelectedFile();
			if (file != null)
				CoEnterDialog.newEnterDialog(browse, this, file, CoEnterDialog.ENTER_RENAME);
		}
	}

	void renameFile(String to, CoFile file) {
		panel.showCard("Log");
		CoProgressDialog progress =
			CoProgressDialog.newProgressDialog(browse, CoProgressDialog.PROGRESS_RENAME);
		if (progress != null) {
			progress.setFile(file.newFileRename(to), file);
			(new Thread(new RenameFiles(progress, file.newFileRename(to), file))).start();
		}
	}

	class RenameFiles implements Runnable {
		CoProgressDialog progress;
		CoFile to, file;
		RenameFiles(CoProgressDialog progress, CoFile to, CoFile file) {
			this.progress = progress;
			this.to = to;
			this.file = file;
		}
		public void run() {
			file.renameTo(to);

			progress.close();
			update(browse.getDir());
		}
	}

	void makeDir() {
		if (active) {
			CoFile cur = browse.getDir();
			if (cur != null)
				CoEnterDialog.newEnterDialog(browse, this, cur, CoEnterDialog.ENTER_MAKEDIR);
		}
	}

	void makeDir(String dir, CoFile cur) {
		panel.showCard("Log");
		CoProgressDialog progress =
			CoProgressDialog.newProgressDialog(browse, CoProgressDialog.PROGRESS_MAKEDIR);
		if (progress != null) {
			progress.setFile(cur.newFileChild(dir));
			(new Thread(new MakeDir(progress, dir, cur))).start();
		}
	}

	class MakeDir implements Runnable {
		CoProgressDialog progress;
		String dir;
		CoFile cur;
		MakeDir(CoProgressDialog progress, String dir, CoFile cur) {
			this.progress = progress;
			this.dir = dir;
			this.cur = cur;
		}
		public void run() {
			(cur.newFileChild(dir)).mkdir();

			progress.close();
			update(browse.getDir());
		}
	}

	void dirEvent() {
		update(browse.getSelectedDir());
	}
	void statusEvent() {
		active = true;
		if (oposite != null) {
			oposite.browse.deselectAllFiles();
			oposite.active = false;
		}
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
	static CoEventQueue getEventQueue(CoSource source) {
		CoEventQueue thread = (CoEventQueue) threads.get(source);
		if (thread == null)
			if (source instanceof LocalSource) {
				thread = new LocalEventQueue();
				threads.put(source, thread);
			} else if (source instanceof Ftp) {
				thread = new FtpEventQueue();
				threads.put(source, thread);
			}
		return thread;
	}
}