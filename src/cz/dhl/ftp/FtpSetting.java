/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

/**
 * Wrapper for FTP client settings.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see Ftp
 */
public class FtpSetting {

	/* ServerSystemMode option, traditional UNIX system.
	 * <P><CODE>% ll<BR>
	 * -rw-r--r--   1 owner   group  239 Nov &nbsp;9 &nbsp;1998 file<BR>
	 * drw-r--r--   1 owner   group &nbsp;58 Nov 12 13:51 folder</CODE></P> */
	static final int UNIX = 1;

	/* ServerSystemMode option, MS Windows system.
	 * <P><CODE>C:\> dir<BR>
	 * 10-16-01  11:35PM 1479&nbsp; file <BR>
	 * 10-16-01  11:37PM <DIR> awt</P> */
	static final int WIN = 2;

	/** ListCommandMode option, gives detailed information on 
	 * file but might not work well on other than implemented 
	 * server systems.
	 *
	 * <P>Traditional UNIX system:<BR>
	 * <CODE>% ll<BR>
	 * -rw-r--r--   1 owner   group  239 Nov &nbsp;9 &nbsp;1998 file<BR>
	 * drw-r--r--   1 owner   group &nbsp;58 Nov 12 13:51 dir</CODE></P>
	 *
	 * <P>MS Windows system:<BR>
	 * <CODE>C:\> dir<BR>
	 * 10-16-01  11:35PM 1479&nbsp; file <BR>
	 * 10-16-01  11:37PM <DIR> folder</P>
	 */
	public static final int LIST = 1;

	/** ListCommandMode option, lists only filenames using 
	 * FTP NLST command. 
	 * <P><I>Warning! Directories are recognized by not 
	 * containing a dot (.) letter and might not correspond 
	 * to real directories.</I></P> */
	public static final int NAME_LIST = 2;

	/** ListCommandMode option, similar to NAME_LIST 
	 * except directories are recognized. 
	 * <P><I>Might not work well on other than UNIX server systems.</I></P>
	 * <P><CODE>% ls -p</CODE></P>
	 * Put a slash (/) after each file name if that file is 
	 * a directory. */
	public static final int NAME_LIST_LS_P = 3;

	/** ListCommandMode option, similar to NAME_LIST except 
	 * directories, executables and special files are recognized. 
	 * <P><I>Might not work well on other than UNIX server systems.</I></P>
	 * <P><CODE>% ls -F</CODE></P>
	 * Put a slash (/) after each file name if that file is 
	 * a directory or a symbolic link to a directory; put an 
	 * asterisk (*) after each file name if that file is 
	 * executable; put an at sign (@) after each file name if 
	 * that file is a symbolic link to a file. */
	public static final int NAME_LIST_LS_F = 4;

	/** ListCommandMode option, gives detailed information on file. 
	 * <P><I>Might not work well on other than UNIX server systems.</I></P>
	 * <P><CODE>% ls -la<BR>
	 * -rw-r--r--   1 owner   group  239 Nov &nbsp;9 &nbsp;1998 file<BR>
	 * drw-r--r--   1 owner   group &nbsp;58 Nov 12 13:51 folder</CODE></P> */
	public static final int NAME_LIST_LS_LA = 5;

	private int serversystemmode = UNIX;
	private int listcommandmode = LIST;
	private char filetransfermode = 'S';
	private boolean activesocketmode = false;
	private boolean securesocketmode = false;

	public FtpSetting() {
	}

	/* Sets type of server system used.
	 * Set by <code>{@link cz.dhl.ftp.FtpFile#listCoFile()}</code>
	 * @param serversystemmode must be one of following optional values; 
	 * @see #UNIX
	 * @see #WIN */
	void setServerSystemMode(int serversystemmode) {
		this.serversystemmode = serversystemmode;
	}

	/* Gets type of server system used.
	 * @return one of following optional values; 
	 * @see #UNIX
	 * @see #WIN */
	int getServerSystemMode() {
		return serversystemmode;
	}

	/** Sets type of list command used.
	 * @param listcommandmode must be one of following optional values; 
	 * @see #LIST
	 * @see #NAME_LIST
	 * @see #NAME_LIST_LS_P
	 * @see #NAME_LIST_LS_F
	 * @see #NAME_LIST_LS_LA */
	public void setListCommandMode(int listcommandmode) {
		this.listcommandmode = listcommandmode;
	}

	/** Gets type of list command used.
	 * @return one of following optional values; 
	 * @see #LIST
	 * @see #NAME_LIST
	 * @see #NAME_LIST_LS_P
	 * @see #NAME_LIST_LS_F
	 * @see #NAME_LIST_LS_LA */
	public int getListCommandMode() {
		return listcommandmode;
	}

	/** Sets transfer mode.
	 * @param filetransfermode must be one of following optional values; 
	 * 'A'=ASCII, 'I'=BINARY files, 'S'=Smart; 
	 * smart mode evaluates transfer mode by current TextFilter settings */
	public void setFileTransferMode(char filetransfermode) {
		this.filetransfermode = filetransfermode;
	}

	/** Gets transfer mode.
	 * @return one of following optional values; 
	 * 'A'=ASCII, 'I'=BINARY files, 'S'=Smart;
	 * smart mode evaluates transfer mode by current TextFilter settings */
	public char getFileTransferMode() {
		return filetransfermode;
	}

	/** Get active socket mode.
	 * @return 
	 * true stands for active socket /
	 * false for pasive socket */
	public boolean getActiveSocketMode() {
		return activesocketmode;
	}

	/** Sets active socket mode.
	 * @param activesocketmode 
	 * true stands for active socket /
	 * false for pasive socket */
	public void setActiveSocketMode(boolean activesocketmode) {
		this.activesocketmode = activesocketmode;
	}

	/* Get secure socket mode.
	 * @return true 
	 * stands for secure socket /
	 * false for plain socket */
	boolean getSecureSocketMode() {
		return securesocketmode;
	}

	/* Sets secure socket mode.
	 * @param securesocketmode
	 * true stands for secure socket /
	 * false for plain socket */
	void setSecureSocketMode(boolean securesocketmode) {
		this.securesocketmode = securesocketmode;
	}
}