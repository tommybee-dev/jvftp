/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import cz.dhl.ui.CoConsole;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Allows writing into FTP file.
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see Ftp
 * @see FtpFile
 */
public final class FtpOutputStream extends OutputStream {
	private Ftp client;
	private FtpDataSocket data;
	private OutputStream stream;

	/** Opens 'store' OutputStream for given filename.
	 * <P>This constructor behaves similarly to 
	 * <code>{@link #FtpOutputStream(FtpFile,boolean)}</code>.</P>
	 * @param file the file to be opened for writing
	 * @exception IOException socket error */
	public FtpOutputStream(FtpFile file) throws IOException {
		this(file, false);
	}

	/** Open either 'store' or 'append' OutputStream for given filename.
	 * <P><B>STOR</B> - store</P>
	 * <P>This command causes the server-DTP to accept the data 
	 * transferred via the data connection and to store the data 
	 * as a file at the server site. If the file specified in the 
	 * pathname exists at the server site, then its contents shall 
	 * be replaced by the data being transferred. A new file is 
	 * created at the server site if the file specified in the 
	 * pathname does not already exist.</P>
	 * <P><B>APPE</B> - append (with create)</P>
	 * <P>This command causes the server-DTP to accept the data 
	 * transferred via the data connection and to store the data 
	 * in a file at the server site. If the file specified in the 
	 * pathname exists at the server site, then the data shall be 
	 * appended to that file; otherwise the file specified in the 
	 * pathname shall be created at the server site.</P>
	 * @param file the file to be opened for writing
	 * @param append if true, then bytes will be written to the 
	 *		end of the file rather than the beginning 
	 * @exception IOException socket error */
	public FtpOutputStream(FtpFile file, boolean append) throws IOException {
		client = null;
		data = new FtpDataSocket(file.client);
		if (append)
			stream = data.getOutputStream("APPE " + file, file.getDataType());
		else
			stream = data.getOutputStream("STOR " + file, file.getDataType());
	}

	/** Opens 'store' concurent OutputStream for given filename.
	 * <P>This constructor behaves similarly to 
	 * <code>{@link #FtpOutputStream(FtpFile,FtpConnect,CoConsole,boolean)}</code>.</P> 
	 * @param file the file to be opened for writing
	 * @param connect login details
	 * @param console message output
	 * @exception IOException socket error */
	public FtpOutputStream(FtpFile file, FtpConnect connect, CoConsole console)
		throws IOException {
		this(file, connect, console, false);
	}

	/** Open either 'store' or 'append' concurent OutputStream for given filename.
	 * <P>Single ftp connection cannot handle <B>multiple concurent 
	 * data transfers</B>. This limitation can be eliminated by 
	 * creating new ftp connection for each concurent data 
	 * transfer. This constructor creates separate Ftp instance.</P>
	 * <P>Note (1) supplying same CoConsole instance for 
	 * multiple sessions will produce messed output.</P>
	 * <P>Note (2) This code may need to be run in separate 
	 * thread to process multiple files concurently.</P>
	 * <P><B>STOR</B> - store</P>
	 * <P>This command causes the server-DTP to accept the data 
	 * transferred via the data connection and to store the data 
	 * as a file at the server site. If the file specified in the 
	 * pathname exists at the server site, then its contents shall 
	 * be replaced by the data being transferred. A new file is 
	 * created at the server site if the file specified in the 
	 * pathname does not already exist.</P>
	 * <P><B>APPE</B> - append (with create)</P>
	 * <P>This command causes the server-DTP to accept the data 
	 * transferred via the data connection and to store the data 
	 * in a file at the server site. If the file specified in the 
	 * pathname exists at the server site, then the data shall be 
	 * appended to that file; otherwise the file specified in the 
	 * pathname shall be created at the server site.</P>
	 * @param file the file to be opened for writing
	 * @param connect login details
	 * @param console message output
	 * @param append if true, then bytes will be written to the 
	 *		end of the file rather than the beginning
	 * @exception IOException socket error 
	 * @see #FtpOutputStream(FtpFile,boolean) */
	public FtpOutputStream(FtpFile file, FtpConnect connect, CoConsole console, boolean append)
		throws IOException {
		client = new Ftp();
		if (client.connect(connect)) {
			if (console != null)
				client.getContext().setConsole(console);
			file = new FtpFile(file.toString(), client);
			data = new FtpDataSocket(file.client);
			if (append)
				stream =
					data.getOutputStream("APPE " + file, file.getDataType());
			else
				stream =
					data.getOutputStream("STOR " + file, file.getDataType());
		} else
			throw new IOException("Connect failed.");
	}

	/* <P><B>STOU</B> - store unique</P>
	 * <P>This command behaves like STOR except that the resultant 
	 * file is to be created in the current directory under a name 
	 * unique to that directory. The 250 Transfer Started response 
	 * must include the name generated.</P> */

	/** Close current data transfer and close data connection.
	 * <P>If no reply <B>ABOR</B> - abort</P>
	 * <P>This command tells the server to abort the previous FTP 
	 * service command and any associated transfer of data. No 
	 * action is to be taken if the previous command has been 
	 * completed (including data transfer). The control connection 
	 * is not to be closed by the server, but the data connection 
	 * must be closed. There are two cases for the server upon 
	 * receipt of this command:</P>
	 * <P>(1) the FTP service command was already completed. The 
	 * server closes the data connection (if it is open) and 
	 * responds with a 226 reply, indicating that the abort 
	 * command was successfully processed.</P>
	 * <P>(2) the FTP service command is still in progress. The 
	 * server aborts the FTP service in progress and closes the 
	 * data connection, returning a 426 reply to indicate that 
	 * the service request terminated abnormally. The server then 
	 * sends a 226 reply, indicating that the abort command was 
	 * successfully processed.</P>
	 * @exception IOException socket error */
	public void close() throws IOException {
		IOException x = null;
		while (stream != null || data != null || client != null)
			try {
				OutputStream o;
				FtpDataSocket d;
				Ftp c;
				if (stream != null) {
					o = stream;
					stream = null;
					o.close();
				}
				if (data != null) {
					d = data;
					data = null;
					d.close();
				}
				if (client != null) {
					c = client;
					client = null;
					c.disconnect();
				}
			} catch (IOException e) {
				x = e;
			}
		if (x != null)
			throw x;
	}

	/**
	 * Writes the specified byte to this output stream. The general 
	 * contract for <code>write</code> is that one byte is written 
	 * to the output stream. The byte to be written is the eight 
	 * low-order bits of the argument <code>b</code>. The 24 
	 * high-order bits of <code>b</code> are ignored.
	 *
	 * @param	  b   the <code>byte</code>.
	 * @exception  IOException  if an I/O error occurs. In particular, 
	 *			 an <code>IOException</code> may be thrown if the 
	 *			 output stream has been closed.
	 */
	public void write(int b) throws IOException {
		stream.write(b);
	}

	/**
	 * Writes <code>b.length</code> bytes from the specified byte array 
	 * to this output stream. The general contract for <code>write(b)</code> 
	 * is that it should have exactly the same effect as the call 
	 * <code>write(b, 0, b.length)</code>.
	 *
	 * @param	  b   the data.
	 * @exception  IOException  if an I/O error occurs.
	 * @see		cz.dhl.ftp.FtpOutputStream#write(byte[], int, int)
	 */
	public void write(byte b[]) throws IOException {
		stream.write(b);
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array 
	 * starting at offset <code>off</code> to this output stream. 
	 * The general contract for <code>write(b, off, len)</code> is that 
	 * some of the bytes in the array <code>b</code> are written to the 
	 * output stream in order; element <code>b[off]</code> is the first 
	 * byte written and <code>b[off+len-1]</code> is the last byte written 
	 * by this operation.
	 * <p>
	 * The <code>write</code> method of <code>OutputStream</code> calls 
	 * the write method of one argument on each of the bytes to be 
	 * written out. Subclasses are encouraged to override this method and 
	 * provide a more efficient implementation. 
	 * <p>
	 * If <code>b</code> is <code>null</code>, a 
	 * <code>NullPointerException</code> is thrown.
	 * <p>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or 
	 * <code>off+len</code> is greater than the length of the array 
	 * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
	 *
	 * @param	  b	 the data.
	 * @param	  off   the start offset in the data.
	 * @param	  len   the number of bytes to write.
	 * @exception  IOException  if an I/O error occurs. In particular, 
	 *			 an <code>IOException</code> is thrown if the output 
	 *			 stream is closed.
	 */
	public void write(byte b[], int off, int len) throws IOException {
		stream.write(b, off, len);
	}

	/**
	 * Flushes this output stream and forces any buffered output bytes 
	 * to be written out. The general contract of <code>flush</code> is 
	 * that calling it is an indication that, if any bytes previously 
	 * written have been buffered by the implementation of the output 
	 * stream, such bytes should immediately be written to their 
	 * intended destination.
	 *
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void flush() throws IOException {
		stream.flush();
	}
}
