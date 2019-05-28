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
 * Defines interface of filtering files.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
public interface CoFilenameFilter {
	/** Tests if a specified file should be included in a file list.
	 * @param dir the directory in which the file was found
	 * @param name the name of the file
	 * @return true if and only if the name should 
	   be included in the file list; false otherwise. */
	public boolean accept(CoFile dir, String name);
}