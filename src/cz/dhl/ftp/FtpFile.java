/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import cz.dhl.io.CoFile;
import cz.dhl.io.CoFilenameFilter;
import cz.dhl.io.CoOrder;
import cz.dhl.ui.CoConsole;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Allows uniform manipulation with FTP files.
 * Equivalent for File object.
 * 
 * <P><B>Only absolute pathnames are supported!</B></P>
 *
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 *
 * @see Ftp
 * @see cz.dhl.io.CoFile
 * @see java.io.File
 */
public final class FtpFile implements CoFile {
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
		if (file instanceof FtpFile) {
			FtpFile l2 = (FtpFile) file;
			return name.compareTo(l2.name);
		} else
			throw new ClassCastException();
	}

	public int compareExtToIgnoreCase(CoOrder file) {
		if (file instanceof FtpFile) {
			FtpFile l2 = (FtpFile) file;
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

	public int compareTo(String o) {
		String s1 = getHost() + getAbsolutePath(), s2;
		s2 = (String) o;
		return s1.compareTo(s2);
	}

	/* CoOrder Implementation. */

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

	public boolean isConnected() {
		return client.isConnected();
	}

	public char getDataType() {
		if (client.getContext().getFileTransferMode() == 'S')
			if (equalsExtTo(client.getContext().getTextFilter()))
				return 'A';
			else
				return 'I';
		else
			return client.getContext().getFileTransferMode();
	}

	public InputStream getInputStream() throws IOException {
		return new FtpInputStream(this);
	}

	public OutputStream getOutputStream() throws IOException {
		return new FtpOutputStream(this, false);
	}

	public OutputStream getOutputStream(boolean append) throws IOException {
		return new FtpOutputStream(this, append);
	}

	public CoFile newFileChild(String child) {
		return new FtpFile(this, child, this.client);
	}

	public CoFile newFileRename(String name) {
		return new FtpFile(this.getParent(), name, this.client);
	}

	public CoConsole getConsole() {
		return client.getContext().getConsole();
	}

	/* CoFile Implementation. */

	private static final String months[] =
		{ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	private static final char LINK = 'l';
	private static final char SPECIAL = 'c';
	private static final char FOLDER = 'd';
	private static final char FILE = '-';

	Ftp client = null;

	private String access = null;
	private String owner = null;
	private String group = null;
	private long size = 0L;
	private long date = 0L;
	String path = null;

	private FtpFile() {
	}

	/** Creates a new FtpFile instance by converting the 
	 * given pathname string into an abstract pathname. */
	public FtpFile(String path, Ftp client) {
		access = "d?????????";
		owner = "";
		group = "";
		size = -1;
		date = 0L;
		if (path.compareTo("/") != 0 && path.endsWith("/"))
			this.path = path.substring(0, path.length() - 1);
		else
			this.path = path;
		this.client = client;
		sortSetup(getName());
	}

	/** Creates a new FtpFile instance from a parent 
	 * pathname string and a child pathname string. */
	public FtpFile(String path, String name, Ftp client) {
		access = "f?????????";
		owner = "";
		group = "";
		size = -1;
		date = 0L;
		if (path.endsWith("/"))
			this.path = path + name;
		else
			this.path = path + "/" + name;
		this.client = client;
		sortSetup(name);
	}

	/** Creates a new FtpFile instance from a parent 
	 * abstract pathname and a child pathname string. */
	public FtpFile(FtpFile dir, String name, Ftp client) {
		this(dir.toString(), name, client);
	}

	public String getHost() {
		String system = "";
		try {
			system = client.host();
		} catch (IOException e) {
			client.getContext().printlog(
				"< File: Can't obtain host name. >\n" + e);
		}
		return system;
	}

	public String getAbsolutePath() {
		return path;
	}

	public int getPathDepth() {
		int depth = -1;
		int length = -1;
		while ((length = path.indexOf('/', length + 1)) >= 0)
			depth++;
		if (!path.endsWith("/"))
			depth++;
		return depth;
	}

	public CoFile getPathFragment(int depth) {
		if (depth > 0) {
			int length = -1;
			for (int n = 0; n <= depth; n++)
				if ((length = path.indexOf('/', length + 1)) < 0)
					break;
			if (length > 0)
				return new FtpFile(path.substring(0, length), client);
			else
				return this;
		} else
			return new FtpFile("/", client);
	}

	public String[] getPathArray() {
		Vector<String> dv = new Vector<String>();
		dv.addElement(getHost());
		StringTokenizer toker = new StringTokenizer(path, "/");
		while (true)
			try {
				String d = toker.nextToken();
				dv.addElement(d);
			} catch (NoSuchElementException e) {
				break;
			}
		String[] ds = new String[dv.size()];
		dv.copyInto(ds);
		return ds;
	}

	public String getName() {
		if (path.lastIndexOf('/') >= 0)
			return path.substring(path.lastIndexOf('/') + 1);
		else
			return path;
	}

	public String getParent() {
		if (path.lastIndexOf('/') > 0)
			return path.substring(0, path.lastIndexOf('/'));
		else
			return new String("/");
	}

	public boolean delete() throws SecurityException {
		if (isDirectory()) { /* release dir if current */
			client.cd(getParent());
			if (client.rmdir(path))
				return true;
			/* When using NAME_LIST, files/dirs 
			   are distinguished by guessing. */
			else
				return client.rm(path);
		} else if (client.rm(path))
			return true;
		else { 
			/* release dir if current */
			client.cd(getParent());
			/* When using NAME_LIST, files/dirs 
			   are distinguished by guessing. */
			return client.rmdir(path);
		}
	}

	public boolean mkdir() throws SecurityException {
		return client.mkdir(path);
	}
	public boolean mkdirs() throws SecurityException {
		boolean done = true;
		int depth = getPathDepth();
		for (int i = 0; i < depth; i++)
			if (!((FtpFile) getPathFragment(i)).mkdir())
				done = false;
			else
				done = true;
		return done;
	}
	public boolean renameTo(CoFile dest) throws SecurityException {
		return client.mv(path, dest.getAbsolutePath());
	}

	public long length() { return size; }
	public long lastModified() { return date; }
	public String lastModifiedString() {
		return (DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT)).format(new Date(lastModified()));
	}

	public boolean isAbsolute() { return (path.charAt(0) == '/'); }
	public boolean isDirectory() { return (access.charAt(0) == FOLDER); }
	public boolean isFile() { return (access.charAt(0) == FILE); }
	public boolean isSpecial() { return (access.charAt(0) == SPECIAL); }
	public boolean isLink() { return (access.charAt(0) == LINK); }
	/** Tests if the file represented by this File object is executable. */
	public boolean isExecutable() { return (access.indexOf('x') != -1); }
	public boolean isHidden() { return false; }
	public boolean canRead() { return true; }
	public boolean canWrite() { return true; }
	public boolean exists() { return false; }

	public String getAccess() { return access; }
	/** Get owner of this file object. */
	public String getOwner() { return owner; }
	/** Get group of users for this file object. */
	public String getGroup() { return group; }
	public String propertyString() {
		String desc = getAccess() + " " + getOwner() + " " + getGroup();
		return (isFile() ? "" + length() + " " + desc : desc);
	}

	public CoFile[] listCoRoots() {
		CoFile fs[] = new FtpFile[1];
		fs[0] = getPathFragment(0);
		return fs;
	}

	public CoFile[] listCoFiles() throws SecurityException {
		FtpFile[] fs = null;
		String line;
		BufferedReader ibuf = null;

		int listtype = client.getContext().getListCommandMode();
		try {
			boolean error = false;
			Vector<FtpFile> fv = new Vector<FtpFile>();
			
			ibuf =
				new BufferedReader(
					new InputStreamReader(new FtpListInputStream(this), client.getControlEncoding()));
			while ((line = ibuf.readLine()) != null) {
				try {
					switch (listtype) {
						case FtpContext.LIST :
							fv.addElement(FtpFile.examineListLine(this, line));
							break;
						case FtpContext.NAME_LIST :
							if (line.startsWith("/") && line.endsWith(":"))
								break;
							if (line.indexOf("//") != -1)
								line = line.substring(line.lastIndexOf('/') + 1);
						case FtpContext.NAME_LIST_LS_F :
						case FtpContext.NAME_LIST_LS_P :
							if (line.length() > 0)
								fv.addElement(
									FtpFile.examineNameListLine(
										this, line, listtype));
							break;
						case FtpContext.NAME_LIST_LS_LA :
							fv.addElement(FtpFile.examineUnixListLine(this, line));
							break;
					}
				} catch (NoSuchElementException e) {
					if (!error
						&& (e.getMessage() == null
							|| !e.getMessage().equals("skip"))) {
						client.printlog(
							"\n   < File: Invalid List Format ! >"
								+ (e.getMessage() != null
									? "\n   " + e.getMessage()
									: "")
								+ "\n   Line: "
								+ line
								+ "\n   Try: 'NAME_LIST' List Command");
						error = true;
					}
				}
			}
			fs = new FtpFile[fv.size()];
			fv.copyInto(fs);
		} catch (IOException e) {
			client.printlog("< File: Can't list directory! >\n" + e);
		} finally {
			try {
				Reader r;
				if (ibuf != null) {
					r = ibuf;
					//ibuf = null;
					r.close();
				}
			} catch (IOException e) {
				client.printerr(e);
			}
		}
		return fs;
	}

	public CoFile[] listCoFiles(CoFilenameFilter filter)
		throws SecurityException {
		FtpFile[] fs = (FtpFile[]) listCoFiles();
		if (fs == null)
			return null;
		if (filter != null) {
			Vector<FtpFile> fv = new Vector<FtpFile>();
			for (int i = 0; i < fs.length; i++)
				if (filter.accept(this, fs[i].getName()))
					fv.addElement(fs[i]);
			fs = new FtpFile[fv.size()];
			fv.copyInto(fs);
		}
		return fs;
	}

	private static FtpFile examineListLine(FtpFile path, String line)
		throws NoSuchElementException {
		if ("0123456789".indexOf(line.charAt(0)) < 0)
			/* unix list format never strarts with number */
			return FtpFile.examineUnixListLine(path, line);
		/* windows list format always starts with number */
		else
			return FtpFile.examineWinListLine(path, line);
	}

	private static FtpFile examineNameListLine(
		FtpFile path,
		String line,
		int listtype)
		throws NoSuchElementException {
		FtpFile ff = new FtpFile();
		ff.client = path.client;
		switch (listtype) {
			case FtpContext.NAME_LIST :
				/**************************************
				* file folder executable link special *
				**************************************/
				if (line.indexOf('.') != -1)
					ff.access = "-?????????";
				else
					ff.access = "d?????????";
				break;
			case FtpContext.NAME_LIST_LS_P :
				/***************************************
				* file folder/ executable link special *
				***************************************/
				if (line.endsWith("/")) {
					ff.access = "d?????????";
					line = line.substring(0, line.length() - 1);
				} else
					ff.access = "-?????????";
				break;
			case FtpContext.NAME_LIST_LS_F :
				/*****************************************
				* file folder/ executable* link@ special *
				*****************************************/
				if (line.endsWith("/")) {
					ff.access = "d?????????";
					line = line.substring(0, line.length() - 1);
				} else if (line.endsWith("*")) {
					ff.access = "-??x??x??x";
					line = line.substring(0, line.length() - 1);
				} else if (line.endsWith("@")) {
					ff.access = "l?????????";
					line = line.substring(0, line.length() - 1);
				} else
					ff.access = "-?????????";
				break;
		}
		ff.owner = "";
		ff.group = "";
		ff.size = -1;
		ff.date = 0L;
		ff.path = path.getAbsolutePath();

		if (!ff.path.endsWith("/"))
			ff.path = ff.path + '/' + line;
		else
			ff.path = ff.path + line;

		ff.sortSetup(line);

		/* Skip current '.' and parent '..' directory aliases. */
		if (ff.getName().compareTo(".") == 0
			|| ff.getName().compareTo("..") == 0)
			throw new NoSuchElementException("skip");
		return ff;
	}

	private static FtpFile examineWinListLine(FtpFile path, String line)
		throws NoSuchElementException {
		FtpFile ff = new FtpFile();
		ff.client = path.client;
		/**********************************************
		* 10-16-01  11:35PM				 1479 file *
		* 10-16-01  11:37PM	   <DIR>		  awt  *
		**********************************************/
		try {
			StringTokenizer toker = new StringTokenizer(line);
			ff.date = examineWinListDate(toker.nextToken(), /* date */
			toker.nextToken()); /* time */
			String size2dir = toker.nextToken(); /* size or dir */
			if (size2dir.equals("<DIR>")) {
				ff.access = "d?????????";
				ff.size = -1;
			} else {
				ff.access = "-?????????";
				ff.size = Long.parseLong(size2dir);
			}
			String name = toker.nextToken("").trim(); /* name */

			ff.owner = "";
			ff.group = "";

			ff.path = path.toString();

			if (!ff.path.endsWith("/"))
				ff.path = ff.path + '/' + name;
			else
				ff.path = ff.path + name;

			ff.sortSetup(name);
		} catch (NumberFormatException e) {
			throw new NoSuchElementException("Win-List: Invalid Number Format");
		}
		/* Skip current '.' and parent '..' directory aliases. */
		if (ff.getName().compareTo(".") == 0
			|| ff.getName().compareTo("..") == 0)
			throw new NoSuchElementException("skip");
		return ff;
	}

	private static long examineWinListDate(String date, String time) { 
		/**********************
		* 10-16-01	11:35PM *
		* 10-16-2001  11:35PM *
		**********************/
		Calendar c = Calendar.getInstance();
		try {
			StringTokenizer toker = new StringTokenizer(date, "-");
			int m = Integer.parseInt(toker.nextToken())-1,
				d = Integer.parseInt(toker.nextToken()),
				y = Integer.parseInt(toker.nextToken());
			if (y >= 70)
				y += 1900;
			else
				y += 2000;
			toker = new StringTokenizer(time, ":APM");
			c.set(y, m, d, (time.endsWith("PM") ? 12 : 0)
					+ Integer.parseInt(toker.nextToken()),
				Integer.parseInt(toker.nextToken()));
		} catch (NumberFormatException e) {
			throw new NoSuchElementException("Win-List: Invalid Date Format");
		}
		return c.getTime().getTime();
	}

	private static FtpFile examineUnixListLine(FtpFile path, String line)
		throws NoSuchElementException {
		FtpFile ff = new FtpFile();
		ff.client = path.client;

		/*********************************************************************
		* -rw-r--r--   1 owner   group	   239 Nov  9  1998 file		   *
		* crw-rw-rw-   1 root	sys	  11, 42 Aug  3  2000 sun_device	 * 
		* crw-------   1 root	sys  137 0x0089 Nov 25 11:39 hpux_device	* 
		* drw-r--r--   1 owner   group		58 Nov 12 13:51 folder		 *
		* lrw-r--r--   1 owner   group		58 Nov 12 13:51 link -> source *
		* -rw-r--r--   1 4					58 Nov 12 13:51 uu_file		*
		* crw-------   1 4			137 0x0089 Nov 25 11:39 uu_device	  * 
		* drw-r--r--   1 4					58 Nov 12 13:51 uu_folder	  *
		* lrw-r--r--   1 4					58 Nov 12 13:51 uu_link -> src *
		**********************************************************************/
		try {
			if (line.indexOf("->") >= 0)
				line = line.substring(0, line.indexOf("->"));
			StringTokenizer toker = new StringTokenizer(line);
			ff.access = toker.nextToken(); /* access */
			toker.nextToken(); /* links */
			ff.owner = toker.nextToken(); /* owner */
			ff.group = toker.nextToken(); /* group */
			String size = toker.nextToken(); /* size */
			if (size.endsWith(","))
				size = size.substring(0, size.indexOf(","));
			String uu = size;
			if (ff.access.startsWith("c"))
				uu = toker.nextToken(); /* device */
			/* if uu.charAt(0) is not digit try uu_file format */
			if ("0123456789".indexOf(uu.charAt(0)) < 0) {
				size = ff.group;
				ff.group = "";
			}
			ff.size = Integer.parseInt(size);
			ff.date =
				examineUnixListDate(
					("0123456789".indexOf(uu.charAt(0)) < 0 ? uu : toker.nextToken()),
			/* month */
			toker.nextToken(), /* day */
			toker.nextToken()); /* time or year */
			String name = toker.nextToken("").trim(); /* name */

			ff.path = path.toString();
			if (!ff.path.endsWith("/"))
				ff.path = ff.path + '/' + name;
			else
				ff.path = ff.path + name;
			ff.sortSetup(name);
		} catch (NumberFormatException e) {
			throw new NoSuchElementException("Unix-List: Invalid Number Format");
		} catch (NoSuchElementException e) { /* Skip 'total n' message. */
			try {
				StringTokenizer toker = new StringTokenizer(line);
				if (!toker.nextToken().equals("total"))
					throw e;
				Long.parseLong(toker.nextToken());
				if (!toker.hasMoreTokens())
					throw new NoSuchElementException("skip");
				else
					throw e;
			} catch (NumberFormatException x) {
				throw e;
			}
		}
		/* Skip current '.' and parent '..' directory aliases. */
		if (ff.getName().compareTo(".") == 0
			|| ff.getName().compareTo("..") == 0)
			throw new NoSuchElementException("skip");
		return ff;
	}

	private static long examineUnixListDate(String month, String day, String year2time) { 
		/***************
		* Nov  9  1998 *
		* Nov 12 13:51 *
		***************/
		Calendar c = Calendar.getInstance();
		month = month.toUpperCase();
		try {
			for (int m = 0; m < 12; m++)
				if (month.equals(months[m])) {
					if (year2time.indexOf(':') != -1) { /* current year */
						c.setTime(new Date(System.currentTimeMillis()));
						StringTokenizer toker =
							new StringTokenizer(year2time, ":");
						/* date and time */
						c.set(c.get(Calendar.YEAR), m,
							Integer.parseInt(day),
							Integer.parseInt(toker.nextToken()),
							Integer.parseInt(toker.nextToken()));
					} else
						/* date */
						c.set(Integer.parseInt(year2time),
							m, Integer.parseInt(day), 0, 0);
					break;
				}
		} catch (NumberFormatException e) {
			throw new NoSuchElementException("Unix-List: Invalid Date Format");
		}
		return c.getTime().getTime();
	}

	public String toString() {
		return getAbsolutePath();
	}
}
