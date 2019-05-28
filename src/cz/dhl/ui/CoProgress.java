/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ui;

import cz.dhl.io.CoFile;

/**
 * Defines interface to display progress.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public interface CoProgress
{
   /** Sets file. 
	* @param file Source file */
   abstract public void setFile(CoFile file);
   /** Sets to and file.
	* @param to Destination file 
	* @param file Source file */
   abstract public void setFile(CoFile to, CoFile file);
   /** Sets progress. 
	* @param increment Bytes transfered  */
   abstract public void setProgress(int increment);
   /** Handles transfer delay. */
   abstract public void setDelay(long increment);
   /** Chcecks for user abort request. */
   abstract public boolean isAborted();
}
