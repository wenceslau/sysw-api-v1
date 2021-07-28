package com.suite.application;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.util.ResourceUtils;

public class SysTray {
	
	private MenuItem action;
	private TrayIcon trayIcon;
	private static boolean started = false;

	// start of main method
	public void init(String status) {

		System.out.println("started: " + started);
		if (started)
			return;

		started = true;

		// checking for support
		if (!SystemTray.isSupported()) {

			System.out.println("System tray is not supported !!! ");
			return;

		}

		// get the systemTray of the system
		SystemTray systemTray = SystemTray.getSystemTray();

		// get default toolkit
		// Toolkit toolkit = Toolkit.getDefaultToolkit();
		// get image
		// Toolkit.getDefaultToolkit().getImage("src/resources/busylogo.jpg");

		Image image = null;

		URL resource;
		
		try {

			resource = ResourceUtils.getURL("classpath:static/core_systray.png");
			
			image = Toolkit.getDefaultToolkit().getImage(resource);

		} catch (FileNotFoundException e2) {

			System.err.println(e2);

		}


		// popupmenu
		PopupMenu trayPopupMenu = new PopupMenu();

		// 1t menuitem for popupmenu
		action = new MenuItem("Open");
		action.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// JOptionPane.showMessageDialog(null, "Action Clicked");

				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {

					try {

						Desktop.getDesktop().browse(new URI("http://localhost:8084/suite/index.html"));

					} catch (IOException | URISyntaxException e1) {

						// TODO Auto-generated catch block
						e1.printStackTrace();

					}

				}

			}

		});
		trayPopupMenu.add(action);

		action.setEnabled(false);
		
		// 2nd menuitem of popupmenu
		MenuItem close = new MenuItem("Close");
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}

		});
		trayPopupMenu.add(close);

		// setting tray icon
		trayIcon = new TrayIcon(image, status, trayPopupMenu);
		// adjust to default size as per system recommendation
		trayIcon.setImageAutoSize(true);

		try {

			systemTray.add(trayIcon);

		} catch (AWTException awtException) {

			awtException.printStackTrace();

		}

		System.out.println("end of main");

	}// end of main

	public void setStatus(String status) {

		trayIcon.setToolTip(status);
		action.setEnabled(true);

	}

}// end of class
