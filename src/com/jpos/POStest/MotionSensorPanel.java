//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as a result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// MotionSensorPanel.java - The MotionSensor panel of POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 7/12/04
//------------------------------------------------------------------------------
// framework added for o-c-e 7-14-2004
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class MotionSensorPanel extends Component implements
		StatusUpdateListener, ActionListener {

	private static final long serialVersionUID = -4446838855128096416L;

	protected MainButtonPanel mainButtonPanel;

	private MotionSensor motionSensor;

	private String defaultLogicalName = "defaultMotionSensor";

	private String logicalName = "";

	private JCheckBox deviceEnabledCB;

	private JCheckBox freezeEventsCB;

	private JLabel motionLabel = new JLabel(
			"Current Motion Property: (Enable Service First)");

	private boolean ver_19_complient = false;

	private boolean ver_18_complient = false;

	boolean motionOpened = false;

	// commands
	private JButton waitForMotionButton = new JButton("Start");
	
	private JButton motionTimeoutButton = new JButton("Set Timeout");

	// command parameters
	private JLabel motionTimeWaitLabel = new JLabel("Timeout (ms):");

	private JTextField motionTimeWaitField = new JTextField("0", 5);
	
	private JTextField motionTimeoutField = new JTextField("0", 5);

	private java.util.Timer updateStatusTimer;

	StatusTimerUpdateTask updateStatusTask;

	public MotionSensorPanel() {
		motionSensor = new MotionSensor();
		updateStatusTimer = new java.util.Timer(true);
		updateStatusTask = new StatusTimerUpdateTask();
		updateStatusTimer.schedule(updateStatusTask, 200, 200);
	}

	public Component make() {

		JPanel mainPanel = new JPanel(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// MethodListener methodListener = new MethodListener();

		mainButtonPanel = new MainButtonPanel(this, defaultLogicalName);
		mainPanel.add(mainButtonPanel);

		JPanel buttonPanel = new JPanel();

		buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));

		mainPanel.add(buttonPanel);

		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

		JPanel propPanel = new JPanel();
		propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));

		deviceEnabledCB = new JCheckBox("Device enabled");
		propPanel.add(deviceEnabledCB);
		freezeEventsCB = new JCheckBox("Freeze events");
		propPanel.add(freezeEventsCB);
		propPanel.add(Box.createVerticalGlue());
		propPanel.add(motionLabel);
		subPanel.add(propPanel);

		deviceEnabledCB.setEnabled(false);
		freezeEventsCB.setEnabled(false);

		deviceEnabledCB.setActionCommand("deviceEnabled");
		deviceEnabledCB.addActionListener(this);
		freezeEventsCB.setActionCommand("freezeEvents");
		freezeEventsCB.addActionListener(this);

		mainPanel.add(subPanel);
		mainPanel.add(Box.createVerticalGlue());

		JPanel motionPanel = new JPanel();
		JPanel motionMethodsPanel = new JPanel();
		motionMethodsPanel.setBorder(new TitledBorder("Wait For Motion"));

		motionMethodsPanel.add(motionTimeWaitLabel);
		motionMethodsPanel.add(motionTimeWaitField);
		motionMethodsPanel.add(waitForMotionButton);
		motionTimeWaitField.setToolTipText("Enter milliseconds");
		waitForMotionButton.setToolTipText("start button");

		JPanel motionTimeoutPanel = new JPanel();
		motionTimeoutPanel.setBorder(new TitledBorder("Motion Timeout"));

		motionTimeoutPanel.add(new JLabel("Timeout Value (ms)"));
		motionTimeoutPanel.add(motionTimeoutField);
		motionTimeoutPanel.add(motionTimeoutButton);
		motionTimeoutButton.setActionCommand("setTimeout");
		motionTimeoutButton.addActionListener(this);
		/*
		motionTimeWaitField.setToolTipText("Enter milliseconds");
		waitForMotionButton.setToolTipText("start button");
		*/
		motionPanel.add(motionMethodsPanel);
		motionPanel.add(motionTimeoutPanel);

		mainPanel.add(motionPanel);

		waitForMotionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					int timeout = Integer.parseInt(motionTimeWaitField
							.getText());
					motionSensor.waitForMotion(timeout);
					System.out.println("pressed wait for motion button!!!");
				} catch (JposException e) {
					JOptionPane.showMessageDialog(null, "Failed to open \""
							+ logicalName + "\"\nException: " + e.getMessage(),
							"Failed", JOptionPane.ERROR_MESSAGE);
					System.err.println("Jpos exception " + e);

				}
			}
		});

		return mainPanel;
	}

	public void statusUpdateOccurred(StatusUpdateEvent sue) {
		if (sue.getStatus() == MotionSensorConst.MOTION_M_ABSENT) {
			motionLabel.setText("Current Motion Property: MOTION_M_ABSENT");
		} else if (sue.getStatus() == MotionSensorConst.MOTION_M_PRESENT) {
			motionLabel.setText("Current Motion Property: MOTION_M_PRESENT");
		} else {
			motionLabel
					.setText("Current Motion Property: Service sent an invalid status");
		}
	}

	/** Listens to the method buttons. */

	public void actionPerformed(ActionEvent ae) {
		mainButtonPanel.action(ae);
		String logicalName = mainButtonPanel.getLogicalName();
		if (ae.getActionCommand().equals("open")) {
			try {
				if (logicalName.equals("")) {
					logicalName = defaultLogicalName;
				}

				motionSensor.open(logicalName);
				motionSensor.addStatusUpdateListener(this);

				deviceEnabledCB.setEnabled(false);
				freezeEventsCB.setEnabled(true);
				int version = motionSensor.getDeviceServiceVersion();
				if (version >= 1009000) {
					ver_19_complient = true;
					ver_18_complient = true;
				}
				if (version >= 1008000) {
					ver_18_complient = true;
				}
			} catch (JposException e) {
				JOptionPane.showMessageDialog(null, "Failed to open \""
						+ logicalName + "\"\nException: " + e.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("claim")) {
			try {
				motionSensor.claim(0);

				deviceEnabledCB.setEnabled(true);
				freezeEventsCB.setEnabled(true);
			} catch (JposException e) {
				JOptionPane.showMessageDialog(null, "Failed to claim \""
						+ logicalName + "\"\nException: " + e.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("release")) {
			try {
				motionSensor.release();

				deviceEnabledCB.setEnabled(false);
				freezeEventsCB.setEnabled(true);

			} catch (JposException e) {
				JOptionPane.showMessageDialog(null, "Failed to release \""
						+ logicalName + "\"\nException: " + e.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("close")) {
			try {
				motionSensor.close();
				deviceEnabledCB.setEnabled(false);
				freezeEventsCB.setEnabled(false);

			} catch (JposException e) {
				JOptionPane.showMessageDialog(null, "Failed to close \""
						+ logicalName + "\"\nException: " + e.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("info")) {
			try {
				String ver = new Integer(motionSensor.getDeviceServiceVersion())
						.toString();
				String msg = "Service Description: "
						+ motionSensor.getDeviceServiceDescription();
				msg = msg + "\nService Version: v"
						+ new Integer(ver.substring(0, 1)) + "."
						+ new Integer(ver.substring(1, 4)) + "."
						+ new Integer(ver.substring(4, 7));
				ver = new Integer(motionSensor.getDeviceControlVersion())
						.toString();
				msg += "\n\nControl Description: "
						+ motionSensor.getDeviceControlDescription();
				msg += "\nControl Version: v"
						+ new Integer(ver.substring(0, 1)) + "."
						+ new Integer(ver.substring(1, 4)) + "."
						+ new Integer(ver.substring(4, 7));
				msg += "\n\nPhysical Device Name: "
						+ motionSensor.getPhysicalDeviceName();
				msg += "\nPhysical Device Description: "
						+ motionSensor.getPhysicalDeviceDescription();

				msg += "\n\nProperties:\n------------------------";

				msg += "\nCapPowerReporting: "
						+ (motionSensor.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced"
								: (motionSensor.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard"
										: "None"));
				if (ver_18_complient) {
					msg += "\nCapStatisticsReporting: "
							+ motionSensor.getCapStatisticsReporting();
					msg += "\nCapUpdateStatistics: "
							+ motionSensor.getCapUpdateStatistics();
				} else {
					msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
					msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
				}

				if (ver_19_complient) {
					msg += "\nCapCompareFirmwareVersion: "
							+ motionSensor.getCapCompareFirmwareVersion();
					msg += "\nCapUpdateFirmware: "
							+ motionSensor.getCapUpdateFirmware();
				} else {
					msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";
					msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
				}

				JOptionPane.showMessageDialog(null, msg, "Info",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (JposException e) {
				JOptionPane.showMessageDialog(null,
						"Exception in Info\nException: " + e.getMessage(),
						"Exception", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("oce")) {
			try {
				if (logicalName.equals("")) {
					logicalName = defaultLogicalName;
				}
				motionSensor.addStatusUpdateListener(this);
				motionSensor.open(logicalName);
				motionSensor.claim(0);
				deviceEnabledCB.setEnabled(true);
				freezeEventsCB.setEnabled(true);
				motionSensor.setDeviceEnabled(true);

			} catch (JposException e) {
				JOptionPane.showMessageDialog(null, "Failed to claim \""
						+ logicalName + "\"\nException: " + e.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
				System.err.println("Jpos exception " + e);
			}
		} else if (ae.getActionCommand().equals("stats")) {
			try {
				StatisticsDialog dlg = new StatisticsDialog(motionSensor);
				dlg.setVisible(true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Exception: "
						+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
			}

		} else if (ae.getActionCommand().equals("firmware")) {
			try {
				FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(motionSensor);
				dlg.setVisible(true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Exception: "
						+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ae.getActionCommand().equals("deviceEnabled")) {
			if (!deviceEnabledCB.isSelected()) {
				try {
					motionSensor.setDeviceEnabled(false);
					motionLabel.setText("Current Motion Property: (Enable First)");
				} catch (JposException je) {
					JOptionPane.showMessageDialog(null,
							"Failed to disable \"" + logicalName
									+ "\"\nException: " + je.getMessage(),
							"Failed", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				try {
				motionSensor.setDeviceEnabled(true);
				motionLabel.setText("Current Motion Property: "
						+ (motionSensor.getMotion() ? "MOTION_M_PRESENT"
								: "MOTION_M_ABSENT"));
				} catch (JposException je) {
					JOptionPane.showMessageDialog(null,
							"Failed to enable \"" + logicalName
									+ "\"\nException: " + je.getMessage(),
							"Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (ae.getActionCommand().equals("freezeEvents")) {
			if (!freezeEventsCB.isSelected()) {
				try {
					motionSensor.setFreezeEvents(false);
				} catch (JposException je) {
					JOptionPane.showMessageDialog(null,
							"Failed to un-freeze events on \"" + logicalName
									+ "\"\nException: " + je.getMessage(),
							"Failed", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				try {
					motionSensor.setFreezeEvents(true);
				} catch (JposException je) {
					JOptionPane.showMessageDialog(null,
							"Failed to freeze events on \"" + logicalName
									+ "\"\nException: " + je.getMessage(),
							"Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (ae.getActionCommand().equals("setTimeout")) {
			try
			{
				motionSensor.setTimeout(Integer.parseInt(motionTimeoutField.getText()));
			}
			catch (Exception je)
			{
				JOptionPane.showMessageDialog(null,
						"Failed to update timeout value on \"" + logicalName
								+ "\"\nException: " + je.getMessage(),
						"Failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		try {
			deviceEnabledCB.setSelected(motionSensor.getDeviceEnabled());
			freezeEventsCB.setSelected(motionSensor.getFreezeEvents());
		} catch (JposException je) {
			JOptionPane.showMessageDialog(null,
					"MotionSensorPanel: MethodListener:\nException: "
							+ je.getMessage(), "Failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	

	private class StatusTimerUpdateTask extends java.util.TimerTask {
		public void run() {
			if (motionSensor != null) {
				mainButtonPanel.currentStatus.setText(MainButtonPanel
						.getStatusString(motionSensor.getState()));
			}
		}
	}
}
