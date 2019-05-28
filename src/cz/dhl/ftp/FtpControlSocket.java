/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

final class FtpControlSocket {
	private Socket control = null;

	private BufferedReader in = null;
	private BufferedWriter out = null;

	private FtpContext context = null;
	private String replyline = null;
	String server = null;

	FtpControlSocket(FtpContext context) {
		this.context = context;
	}
/*
	synchronized boolean connect(String server, int port) {
		
		return connect(server, port, "UTF-8");
	}
*/	
	synchronized boolean connect(String server, int port, String encoding) {
		boolean done = false;

		if (control == null) {
			try { /* Get host name */
				context.printlog("Getting host by name: " + server);
				context.printlog("encoding: " + encoding);
				InetAddress addr = InetAddress.getByName(server);

				/* Connect to host */
				context.printlog(
					"Connecting to host: " + addr.getHostAddress());
				control = new Socket(addr, port);
				control.setSoTimeout(60000);
				
				/* Open input / output streams */
				in =
					new BufferedReader(
						new InputStreamReader(control.getInputStream(), encoding));
				out =
					new BufferedWriter(
						new OutputStreamWriter(control.getOutputStream(), encoding));

				this.server = server;
				done = true;
			} catch (UnknownHostException e) {
				context.printlog("< Ctrl: Can't resolve host address! >");
			} catch (IOException e) {
				disconnect();
				context.printlog("< Ctrl: Can't obtain connection to host! >");
			} catch (Exception e) {
				context.printlog("< Ctrl: Permission denied! >");
			}
		}
		return done;
	}

	synchronized void disconnect() {
		while (in != null || out != null) {
			try {
				Reader r;
				Writer w;
				if (in != null) {
					r = in;
					in = null;
					r.close();
				}
				if (out != null) {
					w = out;
					out = null;
					w.close();
				}
			} catch (IOException e) {
				context.printerr(e);
			}
		}

		if (control != null) {
			try {
				control.close();
			} catch (IOException e) {
				context.printerr(e);
			} finally {
				control = null;
				context.printlog("< Ctrl: Disconnected! >");
			}
		}
		server = null;
	}

	private synchronized void writeLine(String line) throws IOException {
		if (out != null) {
			try {
				out.write(line + "\r\n");
				out.flush();
			} catch (IOException e) {
				throw new IOException("Ctrl: Write, failed!\n" + e);
			}
		} else
			throw new IOException("Ctrl: Write, No connection!");
	}

	private synchronized String readLine() throws IOException {
		String line = null;
		if (in != null) {
			try {
				line = in.readLine();
				
			} catch (IOException e) {
				throw new IOException("Ctrl: Read, Error!\n" + e);
			}
			if (line == null) {
				disconnect(); /* NULL on END OF THE STREAM */
				throw new IOException("Ctrl: Read, End Of File!");
			}
		} else
			throw new IOException("Ctrl: Read, No connection!");
		return line;
	}

	private synchronized String readReply() throws IOException {
		String line = null;
		do {
			line = readLine();
			context.printlog(line);
		} while (line.length() == 0 || /* Skip empty lines */ 
				 line.indexOf("-") == 3 || /* Skip intermediate replies. */ 
				 "0123456789".indexOf(line.charAt(0)) < 0); /* Skip lines that don't start with digit */
		return line;
	}

	synchronized boolean manualCommand(String commandline) {
		if (!FtpInterpret.allowManualExecution(commandline)) {
			context.printlog("< Ctrl: Command, No Manual Execution! >");
			return false;
		} else
			return executeCommand(commandline);
	}

	synchronized boolean executeCommand(String commandline) {
		if (writeCommand(commandline))
			return completeCommand(FtpInterpret.getReplies(commandline));
		else
			return false;
	}

	synchronized boolean writeCommand(String commandline) {
		if (commandline.startsWith("PASS"))
			context.printlog("Ftp> PASS ******");
		else
			context.printlog("Ftp> " + commandline);
		if (!FtpInterpret.allowExecution(commandline)) {
			context.printlog("< Ctrl: Command, Not Implemented! >");
			return false;
		} else {
			boolean done = true;
			try {
				replyline = null;
				writeLine(commandline.trim());
			} catch (IOException e) {
				done = false;
				if (e.getMessage() != null)
					context.printlog("< " + e.getMessage() + " >");
				else
					context.printerr(e);
			}
			return done;
		}
	}

	synchronized boolean completeCommand(String replies[]) {
		boolean done = false;
		try {
			replyline = readReply();
			done = FtpInterpret.startsWith(replyline, replies);
		} catch (IOException e) {
			if (e.getMessage() != null)
				context.printlog("< " + e.getMessage() + " >");
			else
				context.printerr(e);
		}
		return done;
	}

	String replyOfCommand() throws IOException {
		if (replyline != null)
			return replyline;
		else
			throw new IOException("Ctrl: No Reply!");
	}

	boolean isConnected() {
		return (control != null);
	}
}
