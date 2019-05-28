/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.awt;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpConnect;
import cz.dhl.ftp.FtpContext;

/**
 * <P>Allows setup connection details.</P>
 * <P>Main component of FtpBrowsePanel.</P>
 * 
 * <IMG SRC="login.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova 
 * @see FtpBrowsePanel
 */
public final class FtpLoginPanel extends Panel {
	FtpConnect connect = new FtpConnect();

	/* Login Controls */
	Label configLabel = new Label("Config name:");

	Label hostLabel = new Label("Host address:");
	TextField hostField = new TextField();
	Label folderLabel = new Label("Initial folder:");
	TextField folderField = new TextField();

	Label userLabel = new Label("Username:");
	TextField userField = new TextField();
	Label passLabel = new Label("Password:");
	TextField passField = new TextField();

	Label portLabel = new Label("Port number:");
	TextField portField = new TextField();

	Label dataLabel = new Label("Transfer mode:");
	Choice dataChoice = new Choice();
	char dataIs[] = { 'S', 'A', 'I' };

	Label listLabel = new Label("List command:");
	Choice listChoice = new Choice();
	int listIs[] =
		{
			FtpContext.LIST,
			FtpContext.NAME_LIST,
			FtpContext.NAME_LIST_LS_F,
			FtpContext.NAME_LIST_LS_P,
			FtpContext.NAME_LIST_LS_LA };

	FtpEventQueue queue = null;

	/** <P>Creates a new FtpLoginPanel instance.</P> */
	public FtpLoginPanel(Ftp client, TabbedPanel panel) { /* Variables */
		queue = (FtpEventQueue) CoEventQueue.getEventQueue(client);
		queue.login = this;
		if (panel != null)
			queue.panel = panel;

		/* State */
		passField.setEchoChar('*');
		getConnect();

		dataChoice.add("Smart by Ext");
		dataChoice.add("Ascii Text");
		dataChoice.add("Binary Data");

		listChoice.add("LIST");
		listChoice.add("NLST");
		listChoice.add("NLST -F");
		listChoice.add("NLST -p");
		listChoice.add("NLST -la");

		/* Structure */
		Panel top1panel = new Panel(new BorderLayout()),
			top2panel = new Panel(new BorderLayout()),
			top3panel = new Panel(new BorderLayout()),
			hostpanel = new Panel(new GridLayout(2, 2)),
			userpanel = new Panel(new GridLayout(2, 2)),
			pluspanel = new Panel(new GridLayout(3, 2));
		setLayout(new BorderLayout());
		add("North", top1panel);
		String ss1[] =
			{ "Address of server to connect:", "For example: ftp.geocities.com" };
		MultiLabel ml = new MultiLabel(ss1);
		ml.setForeground(Color.white);
		top1panel.add("North", ml);
		top1panel.add("Center", hostpanel);
		hostpanel.add(hostLabel);
		hostpanel.add(hostField);
		hostpanel.add(folderLabel);
		hostpanel.add(folderField);
		String ss2[] =
			{ "Your login name & password:", "Can leave anonymous if allowed." };
		ml = new MultiLabel(ss2);
		ml.setForeground(Color.white);
		top1panel.add("South", ml);
		add("Center", top2panel);
		top2panel.add("Center", userpanel);
		userpanel.add(userLabel);
		userpanel.add(userField);
		userpanel.add(passLabel);
		userpanel.add(passField);
		add("South", top3panel);
		String ss3[] = { "Advanced server settings:" };
		ml = new MultiLabel(ss3);
		ml.setForeground(Color.white);
		top3panel.add("North", ml);
		top3panel.add("Center", pluspanel);
		pluspanel.add(portLabel);
		pluspanel.add(portField);
		pluspanel.add(dataLabel);
		pluspanel.add(dataChoice);
		pluspanel.add(listLabel);
		pluspanel.add(listChoice);
		String ss4[] =
			{
				"Change list command if files",
				"are displayed not or incorrectly.",
				"NLST is safe but less informative." };
		ml = new MultiLabel(ss4);
		ml.setForeground(Color.white);
		top3panel.add("South", ml);

		/* Listeners */
		dataChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					datatypeEvent();
			}
		});
		listChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					listtypeEvent();
			}
		});
	}

	void setConnect() {
		connect.setHostName(hostField.getText());
		connect.setPathName(folderField.getText());
		connect.setUserName(userField.getText());
		connect.setPassWord(passField.getText());
		try {
			connect.setPortNum(Integer.parseInt(portField.getText()));
		} catch (NumberFormatException e) {
			portField.setText("21");
			connect.setPortNum(21);
		}

		if (connect.getUserName().compareTo("anonymous") == 0
			&& connect.getPassWord().indexOf('@') == -1)
			connect.setPassWord("trinity@matrix.com");
	}

	void getConnect() {
		hostField.setText(connect.getHostName());
		folderField.setText(connect.getPathName());
		userField.setText(connect.getUserName());
		passField.setText(connect.getPassWord());
		portField.setText(String.valueOf(connect.getPortNum()));
	}

	public void setEnabled(boolean b) {
		hostLabel.setEnabled(b);
		hostField.setEnabled(b);
		folderLabel.setEnabled(b);
		folderField.setEnabled(b);
		userLabel.setEnabled(b);
		userField.setEnabled(b);
		passLabel.setEnabled(b);
		passField.setEnabled(b);
		portLabel.setEnabled(b);
		portField.setEnabled(b);
	}

	void datatypeEvent() {
		queue.client.getContext().setFileTransferMode(
			dataIs[dataChoice.getSelectedIndex()]);
	}

	void listtypeEvent() {
		queue.client.getContext().setListCommandMode(
			listIs[listChoice.getSelectedIndex()]);
	}

	private static final long serialVersionUID = 1L;
}