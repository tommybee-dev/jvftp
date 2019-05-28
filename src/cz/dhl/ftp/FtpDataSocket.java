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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

final class FtpDataSocket {
	private ServerSocket dataserver = null;
	private Socket data = null;

	FtpContext context = null;
	private FtpControlSocket control = null;

	FtpDataSocket(Ftp client) throws IOException {
		if (client.isConnected()) {
			this.control = client.control;
			this.context = client.getContext();
		} else {
			throw new IOException("Data: CreateSocket, No Connection!");
		}
	}

	/** @return Connect string.
	 * Using format: 'h1,h2,h3,h4,p1,p2'; */
	String getConnect(String reply) throws NumberFormatException {
		if (reply == null)
			throw new NumberFormatException("Null Reply!\n");

		int begin = reply.indexOf('(');
		int end = reply.indexOf(')');
		if (begin != -1 && end != -1 && begin < end)
			return reply.substring(begin + 1, end);
		else
			throw new NumberFormatException("Invalid Reply!\n" + reply);
	}

	/** @param connect Remote hostport number string.<BR> 
	 * Expected format: 'h1,h2,h3,h4,p1,p2'<BR>
	 * h1..h4 - IP address; p1,p2 - port number; 
	 * @return IP address<BR> Using format: 'h1.h2.h3.h4' */
	String getConnectAddress(String connect) throws NumberFormatException {
		int s4 = -1;
		for (int i = 0; i < 4; i++)
			if ((s4 = connect.indexOf(',', s4 + 1)) == -1)
				throw new NumberFormatException(
					"Misformated Reply! " + i + ":" + s4 + " " + connect);
		return connect.substring(0, s4).replace(',', '.');
	}

	/** @param connect Remote hostport number string.<BR> 
	 * Expected format: 'h1,h2,h3,h4,p1,p2'<BR>
	 * h1..h4 - IP address; p1,p2 - port number;
	 * @return Port number. */
	int getConnectPort(String connect)
		throws NumberFormatException, NoSuchElementException {
		int s4 = -1;
		for (int i = 0; i < 4; i++)
			if ((s4 = connect.indexOf(',', s4 + 1)) == -1)
				throw new NumberFormatException(
					"Misformated Reply! " + i + ":" + s4 + " " + connect);
		StringTokenizer tokenizer =
			new StringTokenizer(connect.substring(s4 + 1), ",");
		return Integer.parseInt(tokenizer.nextToken()) * 256
			+ Integer.parseInt(tokenizer.nextToken());
	}

	void openPassiveDataSocket(String commandline, char type)
		throws IOException {
		if (control.isConnected()) {
			try {
				control.executeCommand("TYPE " + type);
				control.executeCommand("PASV");
				String connect = getConnect(control.replyOfCommand());
				String address = getConnectAddress(connect);
				int port = getConnectPort(connect);
				data = new Socket(address, port);
				data.setSoTimeout(60000);
				if (!control.executeCommand(commandline))
					throw new IOException(control.replyOfCommand());
			} catch (NoSuchElementException e) {
				throw new IOException(
					"Data: OpenSocket, Invalid Format!\n" + e);
			} catch (NumberFormatException e) {
				throw new IOException(
					"Data: OpenSocket, Invalid Format!\n" + e);
			} catch (SocketException e) {
				throw new IOException("Data: OpenSocket, Socket Error!\n" + e);
			} catch (IOException e) {
				throw new IOException("Data: OpenSocket, IO Error!\n" + e);
			} catch (Exception e) {
				throw new IOException(
					"Data: OpenSocket, Permission Denied?\n" + e);
			}
		} else {
			throw new IOException("Data: OpenSocket, No Connection!");
		}
	}

	/** 
	 * @return Local hostport number string,
	 * Using format: 'h1,h2,h3,h4,p1,p2'<BR>
	 * h1..h4 - IP address; p1,p2 - port number; */
	String getConnect() throws UnknownHostException {
		short port = (short) dataserver.getLocalPort();
		return ((InetAddress.getLocalHost()).getHostAddress()).replace(
			'.', ',') + "," + port / 256 + "," + port % 256;
	}

	void openActiveDataSocket(String commandline, char type)
		throws IOException {
		if (control.isConnected()) {
			try {
				control.executeCommand("TYPE " + type);
				dataserver = new ServerSocket(0);
				dataserver.setSoTimeout(20000);
				control.executeCommand("PORT " + getConnect());
				synchronized (control) {
					control.writeCommand(commandline);
					data = dataserver.accept();
					data.setSoTimeout(60000);
					if (!control.completeCommand(FtpInterpret.getReplies(commandline)))
						throw new IOException(control.replyOfCommand());
				}
			} catch (SocketException e) {
				throw new IOException("Data: OpenSocket, Socket Error!\n" + e);
			} catch (IOException e) {
				throw new IOException("Data: OpenSocket, IO Error!\n" + e);
			} catch (Exception e) {
				throw new IOException(
					"Data: OpenSocket, Permission Denied!\n" + e);
			}
		} else {
			throw new IOException("Data: OpenSocket, No Connection!");
		}
	}

	void openDataSocket(String commandline, char type) throws IOException {
		if (context.getActiveSocketMode())
			openActiveDataSocket(commandline, type);
		else
			openPassiveDataSocket(commandline, type);
	}

	InputStream getInputStream(String commandline, char type)
		throws IOException {
		if (data == null)
			openDataSocket(commandline, type);
		return data.getInputStream();
	}

	OutputStream getOutputStream(String commandline, char type)
		throws IOException {
		if (data == null)
			openDataSocket(commandline, type);
		return data.getOutputStream();
	}

	void close() throws IOException {
		try {
			if (data != null)
				data.close();
		} finally {
			data = null;
			if (control.isConnected()) {
				if (!control.completeCommand(FtpInterpret.getReplies("data-done"))) {
					control.executeCommand("ABOR");
					throw new IOException("Data: CloseSocket, Transfer Aborted!");
				}
			} else {
				throw new IOException("Data: CloseSocket, No Connection!");
			}
			try {
				if (dataserver != null)
					dataserver.close();
			} finally {
				dataserver = null;
			}
		}
	}
}