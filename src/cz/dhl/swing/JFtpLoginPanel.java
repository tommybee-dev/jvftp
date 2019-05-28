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
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpConnect;
import cz.dhl.ftp.FtpContext;

/**
 * <P>Allows setup connection details.</P>
 * <P>Main component of JFtpBrowsePanel.</P>
 * 
 * <IMG SRC="login.gif"><BR><BR>
 * 
 * @version 0.72 08/10/2003
 * @author Bea Petrovicova   
 * @see JFtpBrowsePanel
 */
public final class JFtpLoginPanel extends JPanel {
	FtpConnect connect = new FtpConnect();

	/* Login Controls */
	//JLabel configLabel = new JLabel("Config name:");

	JLabel hostLabel = new JLabel("Host address:");
	JTextField hostField = new JTextField();
	JLabel folderLabel = new JLabel("Initial folder:");
	JTextField folderField = new JTextField();

	JLabel userLabel = new JLabel("Username:");
	JTextField userField = new JTextField();
	JLabel passLabel = new JLabel("Password:");
	JPasswordField passField = new JPasswordField();

	JLabel portLabel = new JLabel("Port number:");
	JTextField portField = new JTextField();

	JLabel encodingLabel = new JLabel("contents encoding:");
	JTextField encodingField = new JTextField("UTF-8");

	JLabel dataLabel = new JLabel("Transfer mode:");
	JComboBox<String> dataChoice = new JComboBox<String>();
	char dataIs[] = { 'S', 'A', 'I' };

	JLabel listLabel = new JLabel("List command:");
	JComboBox<String> listChoice = new JComboBox<String>();
	int listIs[] =
		{
			FtpContext.LIST,
			FtpContext.NAME_LIST,
			FtpContext.NAME_LIST_LS_F,
			FtpContext.NAME_LIST_LS_P,
			FtpContext.NAME_LIST_LS_LA };

	JFtpEventQueue queue = null;

	/** <P>Creates a new FtpLoginPanel instance.</P> */
	public JFtpLoginPanel(Ftp client, JTabbedPanel panel) { /* Variables */
		queue = (JFtpEventQueue) JCoEventQueue.getEventQueue(client);
		queue.login = this;
		if (panel != null)
			queue.panel = panel;

		/* State */
		getConnect();

		dataChoice.addItem("Smart by Ext");
		dataChoice.addItem("Ascii Text");
		dataChoice.addItem("Binary Data");

		listChoice.addItem("LIST");
		listChoice.addItem("NLST");
		listChoice.addItem("NLST -F");
		listChoice.addItem("NLST -p");
		listChoice.addItem("NLST -la");

		/* Structure */
		JPanel top1panel = new JPanel(new BorderLayout()),
			top2panel = new JPanel(new BorderLayout()),
			top3panel = new JPanel(new BorderLayout()),
			hostpanel = new JPanel(new GridLayout(2, 2)),
			userpanel = new JPanel(new GridLayout(3, 3)),
			//encpanel = new JPanel(new GridLayout(2, 2)),
			pluspanel = new JPanel(new GridLayout(3, 2));
		setLayout(new BorderLayout());
		add("North", top1panel);
		String ss1[] =
			{ "Address of server to connect:", "For example: ftp.geocities.com" };
		JMultiLabel ml = new JMultiLabel(ss1);
		ml.setForeground(Color.red);
		top1panel.add("North", ml);
		top1panel.add("Center", hostpanel);
		hostpanel.add(hostLabel);
		hostpanel.add(hostField);
		hostpanel.add(folderLabel);
		hostpanel.add(folderField);
		String ss2[] =
			{ "Your login name & password:", "Can leave anonymous if allowed." };
		ml = new JMultiLabel(ss2);
		ml.setForeground(Color.red);
		top1panel.add("South", ml);
		add("Center", top2panel);
		top2panel.add("Center", userpanel);
		userpanel.add(userLabel);
		userpanel.add(userField);
		userpanel.add(passLabel);
		userpanel.add(passField);
		userpanel.add(encodingLabel);
		userpanel.add(encodingField);
		add("South", top3panel);
		String ss3[] = { "Advanced server settings:" };
		ml = new JMultiLabel(ss3);
		ml.setForeground(Color.red);
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
		ml = new JMultiLabel(ss4);
		ml.setForeground(Color.red);
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
		connect.setPassWord(new String(passField.getPassword()));
		connect.setControlEncoding(encodingField.getText().trim());
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
		
		encodingField.setText(String.valueOf(connect.getControlEncoding()));
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
		
		encodingLabel.setEnabled(b);
		encodingField.setEnabled(b);
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