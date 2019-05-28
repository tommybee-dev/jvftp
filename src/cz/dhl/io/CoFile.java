/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.io;

/**
 * Allows uniform manipulation with files from 
 * various sources. Equivalent for File object.
 *
 * <P><B>Only absolute pathnames are supported!</B></P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 *
 * @see java.io.File
 */
public interface CoFile extends CoOrder, CoOpen {
	/** Returns the host name. 
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	public String getHost();

	/* Returns the name of filesystem root. 
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> 
	public String getRoot(); */

	/** Returns the absolute pathname of the file represented 
	 * by this object.
	 * @return full pathname (directory components + filename). */
	abstract public String getAbsolutePath();

	/** Returns depth (number of levels) of this abstract pathname.
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	abstract public int getPathDepth();

	/** Returns abstract pathname containing first depth 
	 * levels of this pathname of the file represented by this object.
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	abstract public CoFile getPathFragment(int depth);

	/** Tokens the pathname by separator.
	 * <BR><I>This function is not part of java.io.File specification.</I><BR>
	 * @return Array of strings describing directory part of the pathname:
	 * <CODE>[Server-name]/[Disk-root]<BR>
	 * [1st-level-parent-dir-name]<BR>
	 * [2nd-level-parent-dir-name]<BR>
	 * ...<BR>
	 * [Top-level-parent-dir-name]</CODE> */
	abstract public String[] getPathArray();

	/** Returns the name of the file represented by this object. 
	 * The name is everything in the pathame after 
	 * the last occurrence of the separator character.
	 * @return name of the file (NO directory components). */
	abstract public String getName();

	/** Returns the parent part of the pathname of this File 
	 * object, or null if the name has no parent part. 
	 * The parent part is generally everything leading up to 
	 * the last occurrence of the separator character, 
	 * although the precise definition is system dependent.
	 * @return directory part of the pathname (NO filename). */
	abstract public String getParent();

	/** Deletes the file or directory denoted by this abstract pathname. */
	abstract public boolean delete() throws SecurityException;

	/** Creates the directory named by this abstract pathname. */
	abstract public boolean mkdir() throws SecurityException;

	/** Creates the directory named by this abstract pathname, 
	 * including any necessary but nonexistent parent directories. */
	abstract public boolean mkdirs() throws SecurityException;

	/** Renames the file denoted by this abstract pathname. */
	abstract public boolean renameTo(CoFile dest) throws SecurityException;

	/** Returns the length of the file represented by this 
	 * File object.
	 * @return the length, in bytes, of the file specified by 
	 * this object, or 0L if the specified file does not exist. */
	abstract public long length();

	/** Returns the time that the file represented by this 
	 * File object was last modified.
	 * @return the time the file specified by this object was 
	 * last modified, or 0L if the specified file does not exist. */
	abstract public long lastModified();

	/** Returns the time string that the file represented by this 
	 * CoFile object was last modified in short mode.
	 * @return the time string the file specified by this object was 
	 * last modified. */
	public String lastModifiedString();

	/* Sets the last-modified time of the file or directory 
	 * named by this abstract pathname.
	 * @version 1.2 
	public boolean setLastModified(long time); */

	/** Tests if the file represented by this File object 
	 * is an absolute pathname.
	 * @return true if the pathname indicated by the File 
	 * object is an absolute pathname; false otherwise */
	public boolean isAbsolute();

	/** Tests if the file represented by this File object 
	 * is a directory.
	 * @return true if this File exists and is a 
	 * directory; false otherwise */
	public boolean isDirectory();

	/** Tests if the file represented by this File object 
	 * is a "normal" file.
	 * @return true if the file specified by this object 
	 * exists and is a "normal" file; false otherwise */
	public boolean isFile();

	/** Tests whether the file named by this abstract pathname is a special file. 
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	public boolean isSpecial();

	/** Tests whether the file named by this abstract pathname is a link. 
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	public boolean isLink();

	/** Tests whether the file named by this abstract pathname is a hidden file.
	 * @version 1.2 */
	public boolean isHidden();

	/** Tests whether the application can read the file denoted 
	 * by this abstract pathname. */
	public boolean canRead();

	/* Marks the file or directory named by this abstract pathname 
	 * so that only read operations are allowed.
	 * @version 1.2 
	abstract public boolean setReadOnly(); */

	/** Tests whether the application can modify to the file 
	 * denoted by this abstract pathname. */
	public boolean canWrite();

	/** Tests whether the file denoted by this abstract pathname exists. */
	public boolean exists();

	/** Returns access string.
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> */
	public String getAccess();

	/** Returns a property string.
	 * <BR><I>This function is not part of java.io.File specification.</I><BR> 
	 * @return a property string. */
	public String propertyString();

	/** List the available filesystem roots.
	 * <BR><I>This function is static in java.io.File specification.</I><BR>
	 * @version 1.2 */
	public CoFile[] listCoRoots();

	/** Returns an array of abstract pathnames denoting the files in 
	 * the directory denoted by this abstract pathname. If this 
	 * abstract pathname does not denote a directory, then this method 
	 * returns null. Otherwise an array of File objects is returned, 
	 * one for each file or directory in the directory. Pathnames 
	 * denoting the directory itself and the directory's parent 
	 * directory are not included in the result. Each resulting 
	 * abstract pathname is constructed from this abstract pathname.
	 * There is no guarantee that the name strings in the resulting 
	 * array will appear in any specific order; they are not, in 
	 * particular, guaranteed to appear in alphabetical order. */
	abstract public CoFile[] listCoFiles() throws SecurityException;
	
	/** Returns an array of strings naming the files and directories 
	 * in the directory denoted by this abstract pathname that satisfy 
	 * the specified filter. The behavior of this method is the same as 
	 * that of the {@link #listCoFiles()}} method, except that the strings 
	 * in the returned array must satisfy the filter. If the given filter
	 * is null then all names are accepted. */
	abstract public CoFile[] listCoFiles(CoFilenameFilter filter)
		throws SecurityException;

	/** Returns a string representation of this object.
	 * @return a string giving the pathname of this object. */
	public String toString();

	/* Converts this abstract pathname into a file: URL. The exact 
	 * form of the URL is system-dependent. If it can be determined 
	 * that the file denoted by this abstract pathname is a directory, 
	 * then the resulting URL will end with a slash.
	 * @since 1.2
	abstract public URL toURL() throws MalformedURLException; */
}