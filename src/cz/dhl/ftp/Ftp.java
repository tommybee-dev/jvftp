/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import cz.dhl.io.CoSource;
import java.io.IOException;

/**
 * Allows connect to FTP server and 
 * maintains control connection.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 */
public final class Ftp implements CoSource {
	/** Default FTP port number. */
	public static final int PORT = 21;
	
	private FtpContext context = new FtpContext();
	FtpControlSocket control = new FtpControlSocket(context);
	
	private String encoding;
	
	/** Creates a new Ftp instance. */
	public Ftp() {
		encoding = "UTF-8";
	}

	/** Connect & login.
	 * @param connect Connection details.
	 * @return port Server FTP port number normally 21. */
	public boolean connect(FtpConnect connect) throws IOException {
		setControlEncoding(connect.getControlEncoding());
		
		if (connect(connect.getHostName(), connect.getPortNum(), connect.getControlEncoding()))
			if (login(connect.getUserName(), connect.getPassWord())) {
				//String pathname = connect.getPathName();
				if (connect.getPathName().length() > 0)
					cd(connect.getPathName());
			} else
				disconnect();
		return isConnected();
	}
	
	/** Connect to server, open control connection.
	 * @param server Server DNS server name or dot delimited IP address.
	 * @return port Server FTP port number normally 21. */
	public boolean connect(String server, int port, String encoding) throws IOException {
		if (!isConnected() && server != null) {
			if (control.connect(server, port, encoding)) {
				if (!control
					.completeCommand(FtpInterpret.getReplies("login-done"))) {
					printlog("< Can't obtain welcome message from host! >");
					control.disconnect();
					return false;
				} else
					return true;
			} else
				return false;
		} else
			return false;
	}
	
	
	/** Connect to server, open control connection.
	 * @param server Server DNS server name or dot delimited IP address.
	 * @return port Server FTP port number normally 21. */
	public boolean connect(String server, int port) throws IOException {
		return connect(server, port, "UTF-8");
	}

	/** Disconnect from server, close control connection. */
	public void disconnect() {
		control.disconnect();
	}

	/** Abort connection. */
	public void abort() { //context.setConsole(null); 
		disconnect();
	}

	/** Log in to server, enter username and password.
	 * <P><B>USER</B> - user name.</P>
	 * <P>The argument field is a Telnet string identifying the 
	 * user. The user identification is that does require the 
	 * server for access to its file system. This command will 
	 * normally be the first command transmitted by the user after 
	 * the control connections are made (some servers may require 
	 * this). Additional identification information in the form of 
	 * a password and/or an account command may also be required 
	 * by some servers. Servers may allow a new USER command to 
	 * be entered at any point in order to change the access 
	 * control and/or accounting information. This has the effect 
	 * of flushing any user, password, and account information 
	 * already supplied and beginning the login sequence again. 
	 * All transfer parameters are unchanged and any file transfer 
	 * in progress is completed under the old access control 
	 * parameters.</P>
	 * <P><B>PASS</B> - password.</P>
	 * <P>The argument field is a Telnet string specifying the user's 
	 * password. This command must be immediately preceded by the 
	 * user name command, and, for some sites, completes the 
	 * user's identification for access control. Since password 
	 * information is quite sensitive, it is desirable in general 
	 * to "mask" it or suppress typeout. It appears that the 
	 * server has no foolproof way to achieve this. It is 
	 * therefore the responsibility of the user-FTP process to 
	 * hide the sensitive password information.</P>
	 * @param username Server account username.
	 * @param password Server account password.
	 * @return True on success. */
	public boolean login(String username, String password) throws IOException {
		if (control.executeCommand("USER " + username))
			if (control.executeCommand("PASS " + password)) {
				syst();
				return true;
			} else {
				printlog("< Can't login to host. >");
				return false;
			}
		else {
			printlog("< Can't login to host. >");
			return false;
		}
	}

	/** Returns server host name.
	 * @return Server host name on success. */
	public String host() throws IOException {
		if (isConnected())
			return control.server;
		else
			throw new IOException("Ctrl: No Connection!");
	}

	/** Enter custom server command.
	 * <P>Not all FTP commands are accepted. 
	 * Exceptions are especially commands for transferring 
	 * files and altering way data transfer is performed.</P>
	 * @return True on success. */
	public boolean command(String commandline) {
		return control.manualCommand(commandline);
	}

	/** Remove server directory.
	 * <P><B>RMD</B> - remove directory.</P>
	 * <P>This command causes the directory specified in the pathname 
	 * to be removed as a directory (if the pathname is absolute) 
	 * or as a subdirectory of the current working directory (if 
	 * the pathname is relative).</P>
	 * @param directory Name of directory to be removed.
	 * @return True on success. */
	public boolean rmdir(String directory) {
		return control.executeCommand("RMD " + directory);
	}

	/** Make server working directory.
	 * <P><B>MKD</B> - make directory.</P>
	 * <P>This command causes the directory specified in the 
	 * pathname to be created as a directory (if the pathname is 
	 * absolute) or as a subdirectory of the current working 
	 * directory (if the pathname is relative).</P>
	 * @param directory Name of newly created directory.
	 * @return True on success. */
	public boolean mkdir(String directory) {
		return control.executeCommand("MKD " + directory);
	}

	/** Print server working directory.
	 * <P><B>PWD</B> - print working directory.</P>
	 * <P>This command causes the name of the current working 
	 * directory to be returned in the reply.</P>
	 * @return Current working directory. */
	public String pwd() throws IOException {
		if (isConnected()) {
			String directory = null, replyline;
			control.executeCommand("PWD");
			replyline = control.replyOfCommand();
			try {
				directory = replyline.substring(
						replyline.indexOf('\"') + 1,
						replyline.lastIndexOf('\"'));
			} catch (StringIndexOutOfBoundsException e) {
				throw new IOException("Ctrl: PWD, Invalid Format!");
			}
			return directory;
		} else
			throw new IOException("Ctrl: PWD, No Connection!");
	}

	/** Print server system.
	 * <P><B>SYST</B> - print server system.</P>
	 * <P>This command causes the name name and version of 
	 * the server system to be returned in the reply.</P>
	 * @return Server system. */
	public String syst() throws IOException {
		if (isConnected()) {
			control.executeCommand("SYST");
			String system = control.replyOfCommand();
			getContext().setServerSystemMode(FtpSetting.UNIX);
			if (system != null
				&& system.toUpperCase().indexOf("WINDOWS") >= 0) {
				getContext().setServerSystemMode(FtpSetting.WIN);
				printlog("< File: Setting 'WIN' Server Mode >");
			}
			return system;
		} else
			throw new IOException("Ctrl: PWD, No Connection!");
	}

	/** Change server working directory.
	 * <P><B>CWD</B> - change working directory.</P>
	 * <P>This command allows the user to work with a different 
	 * directory or dataset for file storage or retrieval without 
	 * altering his login or accounting information. Transfer 
	 * parameters are similarly unchanged. The argument is a 
	 * pathname specifying a directory or other system dependent 
	 * file group designator.</P>
	 * @param directory Name of new working directory.</P>
	 * @return True on success. */
	public boolean cd(String directory) {
		return control.executeCommand("CWD " + directory);
	}

	/** Change server directory to parent.
	 * <P><B>CDUP</B> - change up.</P>
	 * <P>This command is a special case of CWD, and is included to 
	 * simplify the implementation of programs for transferring 
	 * directory trees between operating systems having different 
	 * File Transfer Protocol syntaxes for naming the parent 
	 * directory.</P>
	 * @return True on success. */
	public boolean cdup() {
		return control.executeCommand("CDUP");
	}

	/** Delete server file.
	 * <P><B>DELE</B> - delete.</P>
	 * <P>This command causes the file specified in the pathname 
	 * to be deleted at the server site. If an extra level of 
	 * protection is desired (such as the query, "Do you really 
	 * wish to delete?"), it should be provided by the user-FTP 
	 * process.</P>
	 * @param filename Name of file to be deleted.
	 * @return True on success. */
	public boolean rm(String filename) {
		return control.executeCommand("DELE " + filename);
	}

	/** Rename server file.
	 * <P><B>RNFR</B> - rename from.</P>
	 * <P>This command specifies the old pathname of the file, 
	 * which is to be renamed. This command must be immediately 
	 * followed by a "rename to" command specifying the new file 
	 * pathname.</P>
	 * <P><B>RNTO</B> - rename to.</P>
	 * <P>This command specifies the new pathname of the file 
	 * specified in the immediately preceding "rename from" 
	 * command. Together the two commands cause a file to be 
	 * renamed.</P>
	 * @param oldfilename Old name of file.
	 * @param newfilename New name of file.
	 * @return True on success. */
	public boolean mv(String oldfilename, String newfilename) {
		if (control.executeCommand("RNFR " + oldfilename))
			return control.executeCommand("RNTO " + newfilename);
		else
			return false;
	}

	/** Change server file mode.
	 * @param filename Name of file to change mode.
	 * @param mode Mode is UNIX "777" format string.
	 * @return True on success. */
	public boolean chmod(String filename, String mode) {
		return control.executeCommand("SITE CHMOD " + mode + " " + filename);
	}

	/** Tests connection. */
	public boolean isConnected() {
		return control.isConnected();
	}

	/** Return FtpContext object.
	 * @return context
	 * @see cz.dhl.ftp.FtpContext */
	public FtpContext getContext() {
		return context;
	}

	/* Print message line to output console. */
	void printlog(String message) {
		context.printlog(message);
	}

	/* Print object to standard output. */
	void printerr(Exception exception) {
		context.printerr(exception);
	}

	public String getControlEncoding() {
		return encoding;
	}

	public void setControlEncoding(String encoding) {
		this.encoding = encoding;
	}
};
