/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import java.io.IOException;

/**
 * Allows to list FTP directory.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova  
 * @author Bea Petrovicova   
 */
final public class FtpListInputStream extends FtpInputStream {
	/** Open 'list' or 'name list' InputStream for given directory.
	 * <P><B>LIST</B> - list</P>
	 * <P>This command causes a list to be sent from the server to 
	 * the passive DTP. If the pathname specifies a directory or 
	 * other group of files, the server should transfer a list of 
	 * files in the specified directory. If the pathname specifies 
	 * a file then the server should send current information on 
	 * the file. A null argument implies the user's current working 
	 * or default directory. The data transfer is over the data 
	 * connection in type ASCII. Since the information on a file 
	 * may vary widely from system to system, this information may 
	 * be hard to use automatically in a program, but may be quite 
	 * useful to a human user.</P>
	 * <P><B>NLST</B> - name list</P>
	 * <P>This command causes a directory listing to be sent from 
	 * server to user site. The pathname should specify a 
	 * directory or other system-specific file group descriptor; 
	 * a null argument implies the current directory. The server 
	 * will return a stream of names of files and no other 
	 * information. The data will be transferred in ASCII type 
	 * over the data connection as valid pathname strings 
	 * separated by [CRLF] or [NL]. This command is intended to 
	 * return information that can be used by a program to further 
	 * process the files automatically, for example in the 
	 * implementation of a "multiple get" function.</P>
	 * @param dir directory to be listed
	 * @exception IOException socket error
	 * @exception FileNotFoundException server directory not found */
	public FtpListInputStream(FtpFile dir) throws IOException {
		String command = null;
		if (!dir.client.cd(dir.toString()))
			throw new IOException("File: cd command failed!\ncd " + dir);
		dir.path = dir.client.pwd();
		data = new FtpDataSocket(dir.client);
		switch (data.context.getListCommandMode()) {
			case FtpContext.LIST :
				command = "LIST";
				break;
			case FtpContext.NAME_LIST :
				command = "NLST";
				break;
			case FtpContext.NAME_LIST_LS_F :
				command = "NLST -F";
				break;
			case FtpContext.NAME_LIST_LS_P :
				command = "NLST -p";
				break;
			case FtpContext.NAME_LIST_LS_LA :
				command = "NLST -la";
				break;
			default :
				throw new IOException("File: Invalid List Command Mode!");
		}
		stream = data.getInputStream(command /* +dir*/
		, 'A');
	}
}