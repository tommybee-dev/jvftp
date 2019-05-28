/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.io;

import cz.dhl.ui.CoConsole;
import cz.dhl.ui.CoProgress;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Copies files.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see CoFile
 */
public abstract class CoLoad {
	CoConsole console;

	CoLoad() {
	}

	/** Delete Files Recursively. 
	 * @param files Files to be deleted */
	static public boolean delete(CoFile files[]) {
		return CoLoad.delete(files, new CoProgressNull());
	}

	/** Delete Files Recursively.
	 * @param files Files to be deleted
	 * @param progress Progress indicator
	 * @return True on success.
	 * @see CoProgress
	 */
	static public boolean delete(CoFile files[], CoProgress progress) {
		boolean done = true;
		if (files != null)
			for (int i = 0; i < files.length; i++)
				/* Aggressive delete, includes 
				   links and special files. */
				if (!files[i].isDirectory()) {
					if (progress.isAborted()) {
						done = false;
						break;
					}
					progress.setFile(files[i]);
					if (!files[i].delete()) {
						done = false;
						break;
					}
				} else if (files[i].isDirectory())
					try {
						if (progress.isAborted()) {
							done = false;
							break;
						}
						progress.setFile(files[i]);
						if (!CoLoad.delete(files[i].listCoFiles(), progress)) {
							done = false;
							break;
						} else if (!files[i].delete()) {
							done = false;
							break;
						}
					} catch (SecurityException e) {
						done = false;
						break;
					}
		return done;
	}

	/** Copy Files Recursively.
	 * @param to Destination directory
	 * @param files Source files
	 * @return True on success.
	 */
	static public boolean copy(CoFile to, CoFile files[]) {
		return CoLoad.copy(to, files, new CoProgressNull());
	}

	/** Copy Files Recursively.
	 * @param to Destination directory
	 * @param files Source files
	 * @param progress Progress indicator
	 * @return True on success.
	 */
	static public boolean copy(CoFile to, CoFile files[], CoProgress progress) {
		boolean done = true;
		if (files != null)
			for (int i = 0; i < files.length; i++)
				/* Selective copy, excluding 
				   links and special files. */
				if (files[i].isFile()) {
					if (progress.isAborted()) {
						done = false;
						break;
					}
					CoFile tofile = to.newFileChild(files[i].getName());
					progress.setFile(tofile, files[i]);
					if (!CoLoad.copy(tofile, files[i], progress)) {
						done = false;
						break;
					}
				} else if (files[i].isDirectory())
					try {
						if (progress.isAborted()) {
							done = false;
							break;
						}
						CoFile todir = to.newFileChild(files[i].getName());
						progress.setFile(todir, files[i]);
						/* Aggressive mkdir, attempt 
						   to copy contents on error. */
						todir.mkdir();
						if (!CoLoad
							.copy(
								to.newFileChild(files[i].getName()),
								files[i].listCoFiles(),
								progress)) {
							done = false;
							break;
						}
					} catch (SecurityException e) {
						done = false;
						break;
					}
		return done;
	}

	/** Copy File.
	 * @param dst Destination file
	 * @param file Source file
	 * @return True on success. */
	static public boolean copy(CoFile dst, CoFile file) {
		return copy(dst, file, new CoProgressNull());
	}

	/** Copy File.
	 * @param dst Destination file
	 * @param file Source file
	 * @param progress Progress indicator
	 * @return True on success. */
	static public boolean copy(CoFile dst, CoFile file, CoProgress progress) {
		CoConsole console = null;
		if (dst.getConsole() != null)
			console = dst.getConsole();
		else if (file.getConsole() != null)
			console = file.getConsole();
		else if (console == null)
			console = new CoConsole() {
			public void print(String message) {}
		};
		return copy(dst, file, progress, console);
	}

	/** Copy File.
	 * @param dst Destination file
	 * @param file Source file
	 * @param progress Progress indicator
	 * @param console Console output
	 * @return True on success. */
	static public boolean copy(CoFile dst, CoFile file, CoProgress progress, CoConsole console) {
		boolean done = false;
		CoLoad load = null;
		try {
			load = CoLoad.open(dst, file, console);
			int increment = 0;
			while (increment != -1) {
				if (progress.isAborted()) {
					done = false;
					break;
				}
				try {
					increment = load.transfer();
				} catch (IOException e) {
					progress.setDelay(60000);
					increment = 0;
				}
				if (increment != -1)
					progress.setProgress(increment);
			}
			done = true;
		} catch (IOException e) { /* ??.printerr(e); */
		} finally {
			try {
				if (load != null)
					load.close();
			} catch (IOException e) { /* ??.printerr(e); */
			}
		}
		return done;
	}

	static CoLoad open(CoFile dst, CoFile src, CoConsole console)
		throws IOException {
		if (dst.getDataType() == 'A' || src.getDataType() == 'A')
			return new CoTextLoad(dst, src, console);
		else
			return new CoDataLoad(dst, src, console);
	}

	abstract int transfer() throws IOException;
	abstract void close() throws IOException;
}

final class CoDataLoad extends CoLoad {
	private byte cbuf[] = new byte[4096];
	private InputStream ibuf = null;
	private OutputStream obuf = null;

	CoDataLoad(CoFile dst, CoFile src, CoConsole console) throws IOException {
		this.console = console;
		try {
			ibuf = new BufferedInputStream(src.getInputStream());
			obuf = new BufferedOutputStream(dst.getOutputStream());
		} catch (IOException e) {
			if (ibuf == null)
				console.print("< Can't obtain INPUT STREAM for '" + src.getName() + "'! >");
			else if (obuf == null)
				console.print("< Can't obtain OUTPUT STREAM for '" + dst.getName() + "'! >");
			close();
			throw e;
		}
	}

	public int transfer() throws IOException {
		int len;
		if ((len = ibuf.read(cbuf, 0, 4096)) > 0)
			obuf.write(cbuf, 0, len);
		return len;
	}

	public void close() throws IOException {
		IOException ex = null;
		while (ibuf != null || obuf != null) {
			try {
				InputStream i;
				OutputStream o;
				if (ibuf != null) {
					i = ibuf;
					ibuf = null;
					i.close();
				}
				if (obuf != null) {
					o = obuf;
					obuf = null;
					o.close();
				}
			} catch (IOException e) {
				ex = e;
			}
		}
		if (ex != null)
			throw ex;
	}

}

final class CoTextLoad extends CoLoad {
	private BufferedReader ibuf = null;
	private BufferedWriter obuf = null;

	CoTextLoad(CoFile dst, CoFile src, CoConsole console) throws IOException {
		this.console = console;
		try {
			ibuf = new BufferedReader(new InputStreamReader(src.getInputStream()));
			obuf = new BufferedWriter(new OutputStreamWriter(dst.getOutputStream()));
		} catch (IOException e) {
			if (ibuf == null)
				console.print(
					"< Can't obtain INPUT STREAM for '" + src.getName() + "'! >");
			else if (obuf == null)
				console.print("< Can't obtain OUTPUT STREAM for '" + dst.getName() + "'! >");
			close();
			throw e;
		}
	}

	public int transfer() throws IOException {
		String line = null;
		if ((line = ibuf.readLine()) != null) {
			obuf.write(line);
			obuf.newLine();
			return line.length();
		} else
			return -1;
	}

	public void close() throws IOException {
		IOException x = null;
		while (ibuf != null || obuf != null) {
			try {
				Reader r;
				Writer w;
				if (ibuf != null) {
					r = ibuf;
					ibuf = null;
					r.close();
				}
				if (obuf != null) {
					w = obuf;
					obuf = null;
					w.close();
				}
			} catch (IOException e) {
				x = e;
			}
		}
		if (x != null)
			throw x;
	}
}

class CoProgressNull implements CoProgress {
	CoProgressNull() {
	}
	public void setProgress(int done) {
	}
	public void setFile(CoFile file) {
	}
	public void setFile(CoFile to, CoFile file) {
	}
	public void setDelay(long increment) {
	}
	public boolean isAborted() {
		return false;
	}
}
