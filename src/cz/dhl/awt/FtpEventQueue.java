/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.io.IOException;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpFile;
import cz.dhl.io.CoFile;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
final class FtpEventQueue extends CoEventQueue {
	Ftp client = null;

	FtpCommandPanel command = null;
	FtpConnectPanel connect = null;
	FtpLoginPanel login = null;

	void update(CoFile dir) {
		if (dir == null || dir.isConnected()) {
			panel.showCard("Log");
			browse.setEnabled(false);
			(new Thread(new FtpUpdate(dir))).start();
		} else
			disconnect();
	}

	class FtpUpdate extends Update {
		FtpUpdate(CoFile dir) {
			super(dir);
		}
		public void run() {
			if (dir != null) { /* Try first ... a soft link gag */
				if (client.cd(dir.getAbsolutePath()))
					try {
						dir = new FtpFile(client.pwd(), client);
						files = dir.listCoFiles();
					} catch (IOException e) {
					}
				if (files == null) {
					dir = browse.getDir();
					files = browse.getFiles();
				}
			}
			browse.setDir(dir);
			browse.setFiles(files);
			panel.showCard((dir != null ? "List" : "Login"));
		}
	}

	void connect() {
		if (client != null && !client.isConnected()) {
			panel.showCard("Log");
			login.setEnabled(false);
			connect.setEnabled(false, false);
			(new Thread(new Connect())).start();
		}
	}

	class Connect implements Runnable {
		public void run() {
			FtpFile cwd = null;
			login.setConnect();
			boolean done = false;
			try {
				done = client.connect(login.connect);
				cwd = new FtpFile(client.pwd(), client);
			} catch (IOException e) {
				client.getContext().printerr(e);
			}
			if (done) {
				connect.setEnabled(false, true);
				command.setEnabled(true);
				update(cwd);
			} else {
				login.setEnabled(true);
				connect.setEnabled(true, false);
			}
		}
	}

	void disconnect() {
		panel.showCard("Log");
		connect.setEnabled(false, false);
		(new Thread(new Disconnect())).start();
	}

	class Disconnect implements Runnable {
		public void run() {
			client.disconnect();

			login.setEnabled(true);
			connect.setEnabled(true, false);
			command.setEnabled(false);
			update((CoFile) null);
		}
	}

	void disconnectEvent() {
		if (client.isConnected()) {
			connect.setEnabled(false, false);
			panel.showCard("Log");
			client.disconnect();
			login.setEnabled(true);
			update((CoFile) null);
			connect.setEnabled(true, false);
			if (command != null)
				command.setEnabled(false);
			panel.showCard("Login");
		}
	}

	void command() {
		if (client.isConnected()) {
			String line = command.getText();
			(new Thread(new Command(line))).start();
		}
	}

	class Command implements Runnable {
		String line;
		Command(String line) {
			this.line = line;
		}
		public void run() {
			client.command(line);
		}
	}
}