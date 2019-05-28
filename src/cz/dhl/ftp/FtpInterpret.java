/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import java.util.Hashtable;

/**
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 */
final class FtpInterpret {
	/* do not allow execution *
	private static final String PASV = "PASV", REST = "REST", MODE = "MODE", STRU = "STRU";
	/* do not allow manual execution *
	private static final String PORT = "PORT", TYPE = "TYPE", RETR = "RETR", STOR = "STOR";
	private static final String STOU = "STOU", APPE = "APPE", LIST = "LIST", NLST = "NLST";
	private static final String USER = "USER", PASS = "PASS", QUIT = "QUIT";  
	private static final String notManual[] = {PORT,TYPE,RETR,STOR,STOU,APPE,LIST,NLST,USER,PASS,QUIT}; */

	/* do no require dir context update *
	private static final String MKD = "MKD";
	/* do no require dir context update *
	private static final String RNFR = "RNFR", PWD = "PWD";
	/* do no require dir context update on error. *
	private static final String DELE = "DELE", RNTO = "RNTO", CDUP = "CDUP";
	private static final String CWD = "CWD", RMD =  "RMD"; */
	private static final String notAllow[];
	private static final String notManual[];

	private static final Hashtable<String, String[]> library = new Hashtable<String, String[]>();

	static {
		/***************************************************************************
		 * RESULT							  * GROUP							 *
		 *									 * .0.  Syntax					   *
		 * 1..  Positive Preliminary		   * .1.  Information				  *
		 * 2..  Positive Completion			* .2.  Connections				  *
		 * 3..  Positive Intermediate		  * .3.  Authentication & Accounting  *
		 * 4..  Negative Transient Completion  *								   *
		 * 5..  Negative Permanent Completion  * .5.  File System				  * 
		 ***************************************************************************/

		String loginok[] = { "220" };
		putReplies("login-done", loginok);

		/* 20.   Positive Completion Syntax reply 
		   23.   Positive Completion Authentication & Accounting reply 
		   33.   Positive Intermediate Authentication & Accounting reply */
		String userok[] = { "230", "331" };
		putReplies("USER", userok);
		String skipok[] = { "230" };
		putReplies("USER-done", skipok);
		String passok[] = { "230", "202" };
		putReplies("PASS", passok);

		/* 12.   Positive Preliminary Connections reply 
		   15.   Positive Preliminary File System reply */
		String listok[] = { "125", "150" };
		putReplies("RETR", listok);
		putReplies("STOR", listok);
		putReplies("STOU", listok);
		putReplies("APPE", listok);
		putReplies("LIST", listok);
		putReplies("NLST", listok);
		/* 22.   Positive Completion Connections reply 
		   25.   Positive Completion File System reply */
		String doneok[] = { "226", "250" };
		putReplies("data-done", doneok);
		String aborok[] = { "225" };
		putReplies("ABOR", aborok);

		/* 20.   Positive Completion Syntax reply */
		String typeok[] = { "200" };
		putReplies("TYPE", typeok);
		putReplies("PORT", typeok);

		/* 25.   Positive Completion File system reply 
		   35.   Positive Intermediate File system reply */
		String cwdok[] = { "250" };
		putReplies("CWD", cwdok);
		putReplies("CDUP", cwdok);
		putReplies("RMD", cwdok);
		putReplies("DELE", cwdok);
		String rnfrok[] = { "350" };
		putReplies("RNFR", rnfrok);
		putReplies("RNTO", cwdok);
		String mkdok[] = { "257" };
		putReplies("MKD", mkdok);
		putReplies("PWD", mkdok);

		String systok[] = { "215" };
		putReplies("SYST", systok);

		/* 22.   Positive Completion Connections reply */
		String quitok[] = { "221" };
		putReplies("QUIT", quitok);
		String pasvok[] = { "227" };
		putReplies("PASV", pasvok);

		String nA[] = { "REST", "MODE", "STRU" };
		notAllow = nA;
		String nM[] = {
				"PORT", "TYPE", "RETR", "STOR", "STOU", "APPE",
				"LIST", "NLST", "USER", "PASS", "QUIT", "PASV" };
		notManual = nM;
	}

	static boolean startsWith(String string, String prefixes[]) {
		boolean done = false;
		for (int i = 0; i < prefixes.length; i++)
			if (string.indexOf(prefixes[i]) == 0) {
				done = true;
				break;
			}
		return done;
	}

	protected FtpInterpret() {
	}

	static String getCommand(String commandline) {
		return commandline.substring(
			0, ((commandline.indexOf(" ") >= 0 && commandline.indexOf(" ") <= 4)
				? commandline.indexOf(" ") : commandline.length()));
	}

	static void putReplies(String command, String replies[]) {
		library.put(command, replies);
	}

	static String[] getReplies(String commandline) {
		String replies[];
		String command = getCommand(commandline);
		replies = (String[]) library.get(command);
		if (replies == null)
			replies = new String[0];
		return replies;
	}

	static boolean allowExecution(String commandline) {
		return !startsWith(commandline, notAllow);
	}

	static boolean allowManualExecution(String commandline) {
		return !startsWith(commandline, notManual);
	}

	/* static boolean updateDir(String command) {
		if (command.compareTo(STOR) == 0
			|| command.compareTo(STOU) == 0
			|| command.compareTo(APPE) == 0
			|| command.compareTo(MKD) == 0
			|| !updateList(command))
			return false;
		else
			return true;
	}

	static boolean updateList(String command) {
		if (command.compareTo(TYPE) == 0
			|| command.compareTo(PORT) == 0
			|| command.compareTo(RETR) == 0
			|| command.compareTo(LIST) == 0
			|| command.compareTo(NLST) == 0
			|| command.compareTo(RNFR) == 0
			|| command.compareTo(PWD) == 0)
			return false;
		else
			return true;
	}

	static boolean updateListOnError(String command) {
		if (command.compareTo(DELE) == 0
			|| command.compareTo(RNTO) == 0
			|| command.compareTo(RMD) == 0
			|| command.compareTo(MKD) == 0
			|| command.compareTo(CWD) == 0
			|| command.compareTo(CDUP) == 0)
			return false;
		else
			return true;
	} */ 
}
