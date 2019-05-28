/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cz.dhl.ftp.Ftp;
import cz.dhl.io.CoSource;

/**
 * <P>Sample implementation of FtpBrowsePanel.</P>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see FtpBrowsePanel
 */
public final class JFtpOpen extends JTabbedPanel
{  private JFtpBrowsePanel browse = null;
   
   /** <P>Creates a new FtpOpen instance.</P> */
   public JFtpOpen()
   {  Ftp client = new Ftp();   
	  /* Cards */	
	  JPanel logincard = new JPanel();
	  JPanel listcard = new JPanel();
	  JPanel logcard = new JPanel();
	  
	  addTab("Login",logincard);
	  addTab("List",listcard);
	  addTab("Log",logcard);
	  showCard("Login");
	  
	  {  /* Login card initialisation */
	 logincard.setLayout(new BorderLayout());
	 logincard.add("South",new JFtpConnectPanel(client));
	 logincard.add("North",new JFtpLoginPanel(client,this)); 
	  }
	  
	  {  /* List card initialisation */
	 CoSource sources[] = new CoSource[1]; sources[0] = client;
	 JCoControlPanel control = new JCoControlPanel(sources);
	 JPanel commandpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));
	 listcard.setLayout(new BorderLayout());
	 browse = new JFtpBrowsePanel(client);
	 listcard.add("Center",browse);
	 listcard.add("South",control);
	  }
	  
	  {  /* Log card initialisation */
	 logcard.setLayout(new BorderLayout());
		 JScrollPane scroll = new JScrollPane( 
			new JCoConsoleTextArea(client));
		 scroll.setPreferredSize(new Dimension(300,350));
	 logcard.add("Center",scroll);
	 logcard.add("South",new JFtpCommandPanel(client)); 
	  }
   }   

   /** Sets FTP config string. */
   public void setConfig(String config)
	  { browse.setConfig(config); }
   /** Sets FTP config string. */
   public void setConfig(String config[])
	  { browse.setConfig(config); }
   
   /**
	* <P>Starts processing threads.
	* <P>Call from Applet.init() or in applications from 
	* main method, after GUI is initialized.
	* <P>
	* @see java.applet.Applet#init()
	*/
   public void start()
	  { browse.start(); }
   
   /**
	* <P>Ends processing threads and closes network connections.
	* <P>Call on Applet.destroy() or when application exits.
	* If called from application use Window.dispose() instead 
	* of System.exit() to close GUI and end up execution. 
	* If not possible, allow 2 seconds grace period to 
	* close network sockets and stop threads before exit.
	* <P>
	* @see java.applet.Applet#destroy()
	* @see java.awt.Window#dispose()
	* @see java.lang.System#exit()
	*/   
   public void end() 
   {  if(browse!=null)
		 { browse.end(); browse = null; }
   }

   private static final long serialVersionUID = 1L;
}
