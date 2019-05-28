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
 * Defines interface to display client 
 * network commands and server replies.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public interface CoConsole {
	/** Displays message on application console. */
	abstract public void print(String message);
}
