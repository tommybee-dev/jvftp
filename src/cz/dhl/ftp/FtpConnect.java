/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

/**
 * Wrapper for FTP connect & login information.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see Ftp
 */
public final class FtpConnect {
	//private String hostname = "ftp.netscape.com";
	private String hostname = "192.9.150.201";
	private String pathname = "";

	//private String username = "anonymous";
	private String username = "ftpuser";
	private String password = "!skens2014";
	
	private String controlEncoding = "UTF-8";
	
	private int portnum = 21;

	/** Creates default FtpConnect object.
	 * <DL><DT>Initial values are:
	 * <DD>&nbsp;&nbsp;hostname='ftp.netscape.com'; 
	 * <DD>&nbsp;&nbsp;pathname=''; 
	 * <DD>&nbsp;&nbsp;username='anonymous'
	 * <DD>&nbsp;&nbsp;password=''
	 * <DD>&nbsp;&nbsp;portnum=21</DL> */
	public FtpConnect() {
	}

	public String saveConnect(String hostname, String filename)
		throws MalformedURLException {
		return URLEncoder.encode((new URL("http", hostname, filename)).toString() + "?config=" + getConnect());
	}

	/** Parses FtpConnect object into string representing 
	 * command-line arguments concatenated by '|' character.
	 * @see #newConnect(String []) */
	public String getConnect() throws MalformedURLException {
		return (new URL("ftp", hostname, portnum, pathname)).toString() + "|-user|" + username;
	}

	/** Parses string representing command-line arguments 
	 * concatenated by '|' character into FtpConnect object.
	 * @see #newConnect(String []) */
	public static FtpConnect newConnect(String args) {
		if (args != null) {
			StringTokenizer argt = new StringTokenizer(args, "|");
			int n = argt.countTokens();
			String argn[] = new String[n];
			for (int i = 0; i < n; i++)
				argn[i] = argt.nextToken();
			return newConnect(argn);
		} else
			return new FtpConnect();
	}

	/** Parses array of strings representing 
	 * command-line arguments into FtpConnect object.
	 * @param args Command-line arguments
	 * <DL><DT>Following option values are recognized:
	 * <DD>ftp://ftp.server.com:21/default/pathname/
	 * <DD>&nbsp;&nbsp;hostname='ftp.server.com'; 
	 * <DD>&nbsp;&nbsp;pathname='/default/pathname'; 
	 * <DD>&nbsp;&nbsp;portnum=21
	 * <DD>-user eternity 
	 * <DD>&nbsp;&nbsp;username='eternity'</DL>
	 * @return FtpConnect object */
	public static FtpConnect newConnect(String args[]) {
		FtpConnect connect = new FtpConnect();

		for (int i = 0; i < args.length; i++)
			if (args[i].startsWith("ftp:"))
				try {
					URL argi = new URL(args[i]);
					connect.hostname = argi.getHost();
					String pathname = argi.getFile();
					if (pathname.compareTo("/") != 0)
						connect.pathname = pathname;
					int portnum = argi.getPort();
					if (portnum != -1)
						connect.portnum = portnum;
					break;
				} catch (MalformedURLException e) {
				} finally {
					for (i = 0; i < (args.length - 1); i++)
						if (args[i].compareTo("-user") == 0) {
							connect.username = args[i + 1];
							break;
						}
					break;
				}
		return connect;
	}

	/** Get ftp client hostname string. For example: <CODE>ftp.netscape.com</CODE> */
	public String getHostName() {
		return hostname;
	}
	/** Get initial pathname string. Empty string is indicating default directory. */
	public String getPathName() {
		return pathname;
	}
	/** Gets username string. */
	public String getUserName() {
		return username;
	}
	/** Gets password string. */
	public String getPassWord() {
		return password;
	}
	/** Gets socket port number. */
	public int getPortNum() {
		return portnum;
	}

	/** Sets ftp client hostname string. For example: <CODE>ftp.netscape.com</CODE> */
	public void setHostName(String hostname) {
		this.hostname = hostname;
	}
	/** Sets initial pathname string. Empty string is indicating default directory. */
	public void setPathName(String pathname) {
		this.pathname = pathname;
	}
	/** Sets username string. */
	public void setUserName(String username) {
		this.username = username;
	}
	/** Sets password string. */
	public void setPassWord(String password) {
		this.password = password;
	}
	/** Sets socket port number. */
	public void setPortNum(int portnum) {
		this.portnum = portnum;
	}

	public String getControlEncoding() {
		return controlEncoding;
	}

	public void setControlEncoding(String controlEncoding) {
		this.controlEncoding = controlEncoding;
	}
}
