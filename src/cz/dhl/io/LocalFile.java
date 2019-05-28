/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.io;

import cz.dhl.io.CoFile;
import cz.dhl.ui.CoConsole;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

/**
 * Allows uniform manipulation with local files.
 * Equivalent for File object.
 * 
 * <P><B>Only absolute pathnames are supported!</B></P>
 *
 * @see CoFile
 * @see java.io.File
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova  
 */
public final class LocalFile extends File implements CoFile {
	/* CoOrder Implementation. */

	private String name = null, ext = null;

	private void sortSetup(String name) {
		this.name = name.toUpperCase();
		int index = this.name.lastIndexOf(".");
		if (index != -1 && index < this.name.length())
			ext = this.name.substring(index);
		else
			ext = " " + this.name;
	}

	public int compareNameToIgnoreCase(CoOrder file) {
		if (file instanceof LocalFile) {
			LocalFile l2 = (LocalFile) file;
			return name.compareTo(l2.name);
		} else
			throw new ClassCastException();
	}

	public int compareExtToIgnoreCase(CoOrder file) {
		if (file instanceof LocalFile) {
			LocalFile l2 = (LocalFile) file;
			int result = ext.compareTo(l2.ext);
			if (result == 0)
				result = name.compareTo(l2.name);
			return result;
		} else
			throw new ClassCastException();
	}

	public boolean startsWithIgnoreCase(char ch) {
		return (name.charAt(0) == Character.toUpperCase(ch));
	}

	public boolean equalsExtTo(String filter) {
		return (ext.compareTo(filter) == 0);
	}

	public boolean equalsExtTo(String filter[]) {
		boolean done = false;
		for (int j = 0; j < filter.length; j++)
			if (ext.compareTo(filter[j]) == 0) {
				done = true;
				break;
			}
		return done;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if(o instanceof CoOrder) {
			return (compareTo((CoOrder)o) == 0);
		} else {
			return (compareTo((String)o) == 0);
		}
	}

	public boolean isConnected() {
		return true;
	}

	/* CoOrder Implementation. */

	public int compareTo(String o) {
		String s1 = getHost() + getAbsolutePath(), s2;
		s2 = (String) o;
		return s1.compareTo(s2);
	}

	public int compareTo(CoOrder o) {
		String s1 = getHost() + getAbsolutePath(), s2;
		if (o instanceof CoFile) {
			CoFile f2 = (CoFile) o;
			s2 = f2.getHost() + f2.getAbsolutePath();
		} else
			throw new ClassCastException();
		return s1.compareTo(s2);
	}

	public int compareTo(File o) {
		return compareTo((CoOrder)o);
	}

	public char getDataType() {
		return 'I';
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this);
	}

	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(this);
	}

	public OutputStream getOutputStream(boolean append) throws IOException {
		return new FileOutputStream(toString(), append);
	}

	public CoFile newFileChild(String child) {
		return new LocalFile(this, child);
	}

	public CoFile newFileRename(String name) {
		return new LocalFile(this.getParent(), name);
	}

	public CoConsole getConsole() {
		return null;
	}

	/* CoFile Implementation. */

	/** Creates a new LocalFile instance by converting the 
	 * given pathname string into an abstract pathname. */
	public LocalFile(String path) {
		super(path);
		sortSetup(getName());
	}

	/** Creates a new LocalFile instance from a parent 
	 * pathname string and a child pathname string. */
	public LocalFile(String path, String name) {
		super(path, name);
		sortSetup(name);
	}

	/** Creates a new LocalFile instance from a parent 
	 * abstract pathname and a child pathname string. */
	public LocalFile(LocalFile dir, String name) {
		super(dir, name);
		sortSetup(name);
	}

	public String getHost() {
		return "";
	}

	public int getPathDepth() {
		String path = getAbsolutePath();
		int depth = -1;
		int length = -1;
		while ((length = path.indexOf(separatorChar, length + 1)) >= 0)
			depth++;
		if (!path.endsWith(separator))
			depth++;
		return depth;
	}

	public CoFile getPathFragment(int depth) {
		String path = getAbsolutePath();
		if (depth > 0) {
			int length = -1;
			for (int n = 0; n <= depth; n++)
				if ((length = path.indexOf(separatorChar, length + 1)) < 0)
					break;
			if (length > 0)
				return new LocalFile(path.substring(0, length));
			else
				return this;
		} else
			return new LocalFile(
				path.substring(0, path.indexOf(separatorChar) + 1));
	}

	public String[] getPathArray() {
		Vector<String> dv = new Vector<String>();
		String path = getAbsolutePath();
		if (path != null) {
			StringTokenizer tokenizer = new StringTokenizer(path, separator);
			while (true)
				try {
					String d = tokenizer.nextToken();
					dv.addElement(d);
				} catch (NoSuchElementException e) {
					break;
				}
		}
		String[] ds = new String[dv.size()];
		dv.copyInto(ds);
		return ds;
	}

	public String getName() {
		return super.getName();
	}

	public String getParent() {
		return super.getParent();
	}

	public boolean delete() throws SecurityException {
		return super.delete();
	}
	public boolean mkdir() throws SecurityException {
		return super.mkdir();
	}
	public boolean mkdirs() throws SecurityException {
		return super.mkdirs();
	}

	public boolean renameTo(CoFile dest) throws SecurityException {
		return super.renameTo(((File) dest));
	}

	public String lastModifiedString() {
		return (
			DateFormat.getDateTimeInstance( DateFormat.SHORT,
				DateFormat.SHORT)).format(new Date(lastModified()));
	}

	public long length() {
		return super.length();
	}
	public long lastModified() {
		return super.lastModified();
	}

	public boolean isAbsolute() {
		return super.isAbsolute();
	}
	public boolean isDirectory() {
		return super.isDirectory();
	}
	public boolean isFile() {
		return super.isFile();
	}

	public boolean isSpecial() {
		return false;
	}
	public boolean isLink() {
		return false;
	}

	public boolean isHidden() {
		try {
			// Java version check
			Class.forName("java.io.File").getMethod("isHidden",new Class[0]);

			// This is Java v1.2+ 
			return super.isHidden();

		} catch (Exception e) { 
			// This Java v1.0 or v1.1 
			return false;
		} 
	}
	public boolean canRead() {
		return super.canRead();
	}
	public boolean canWrite() {
		return super.canWrite();
	}
	public boolean exists() {
		return super.exists();
	}

	public String getAccess() {
		String access = null;
		if (isDirectory())
			access = "d";
		else
			access = "-";
		if (canRead())
			access += "r";
		else
			access += "-";
		if (canWrite())
			access += "w?";
		else
			access += "-?";
		return access;
	}

	public String propertyString() {
		return (isFile() ? "" + length() + " " : "") + getAccess();
	}

	public CoFile[] listCoRoots() {
		try {
			// Java version check
			Class.forName("java.io.File").getMethod("listRoots",new Class[0]);
			
			// This is Java v1.2+
			File[] ls = listRoots();
			if (ls == null)
				return null;
			CoFile[] fs = new LocalFile[ls.length];
			for (int i = 0; i < ls.length; i++)
				fs[i] = new LocalFile(ls[i].getAbsolutePath());
			return fs;

		} catch (Exception e) { 
			// This Java v1.0 or v1.1 
			CoFile fs[] = new CoFile[1];
			fs[0] = getPathFragment(0);
			return fs;
		}
	}

	
	public CoFile[] listCoFiles() throws SecurityException {
		String[] ss = list();
		if (ss == null)
			return null;
		CoFile[] fs = new LocalFile[ss.length];
		for (int i = 0; i < ss.length; i++)
			fs[i] = new LocalFile(getAbsolutePath(), ss[i]);
		return fs;
	}

	public CoFile[] listCoFiles(CoFilenameFilter filter)
		throws SecurityException {
		LocalFile[] fs = (LocalFile[]) listCoFiles();
		if (fs == null)
			return null;
		if (filter != null) {
			Vector<LocalFile> fv = new Vector<LocalFile>();
			for (int i = 0; i < fs.length; i++)
				if (filter.accept(this, fs[i].getName()))
					fv.addElement(fs[i]);
			fs = new LocalFile[fv.size()];
			fv.copyInto(fs);
		}
		return fs;
	}

	public String toString() {
		return getAbsolutePath();
	}

	private static final long serialVersionUID = 1L;
}
