/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ui;

/**
 * Defines interface to display file status.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see cz.dhl.io.CoFile
 */
public interface CoStatus 
{	 
   /** Display size. */
   abstract public void setSize(String s);
   /** Display date. */
   abstract public void setDate(String s);
}
