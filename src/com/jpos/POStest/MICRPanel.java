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
// MICRPanel.java - The MICR panel for POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-15-2004 (oce, clearData)
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class MICRPanel extends Component implements  ActionListener {
    
	private static final long serialVersionUID = -5786054917462102074L;

	protected MainButtonPanel mainButtonPanel;
    
    private MICR micr = null;
    
    private String defaultLogicalName = "defaultMICR";
    
    String rawData = new String("");
    String accountNumber = new String("");
    String bankNumber = new String("");
    String serialNumber = new String("");
    
    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    
    protected JTextField accountNumberTextField;
    protected JTextField amountTextField;
    protected JTextField bankNumberTextField;
    protected JTextField checkTypeTextField;
    protected JTextField countryCodeTextField;
    protected JTextField epcTextField;
    protected JTextField rawDataTextField;
    protected JTextField serialNumberTextField;
    protected JTextField transitNumberTextField;
    
    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    boolean updateDevice = true;
    
    private JButton beginInsertionButton;
    private JButton endInsertionButton;
    private JButton beginRemovalButton;
    private JButton endRemovalButton;
    private JButton clearFieldsButton;
    private JButton refreshFieldsButton;
    
    private JButton clearInputButton;
    private JButton clearInputPropertiesButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    Runnable doUpdateGUI;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    private EventListener listener = new EventListener();
    
    private class EventListener implements StatusUpdateListener, DirectIOListener, ErrorListener, DataListener{
        public void statusUpdateOccurred( StatusUpdateEvent sue ){
            JOptionPane.showMessageDialog(null, "Status Update Event: "+ sue, "SUE", JOptionPane.INFORMATION_MESSAGE);
        }
        
        public void directIOOccurred( DirectIOEvent dioe){
            JOptionPane.showMessageDialog(null, "Direct I/O Event: "+ dioe.getEventNumber() + "returned data ='" + Integer.toString(dioe.getData()) , "Direct I/O Event", JOptionPane.INFORMATION_MESSAGE);
        }
        
        public void errorOccurred(ErrorEvent ee){
            String msg = "Unknown Extended Error: ";
            int errorcode = ee.getErrorCode();
            if(errorcode == JposConst.JPOS_E_EXTENDED){
                //extended error
                int exterr = ee.getErrorCodeExtended();
                msg += Integer.toString(exterr);
                
                switch( exterr ){
                    case MICRConst.JPOS_EMICR_BADDATA:
                        msg = "Bad Data";
                        break;
                    case MICRConst.JPOS_EMICR_NODATA:
                        msg = "No Data";
                        break;
                    case MICRConst.JPOS_EMICR_BADSIZE:
                        msg = "Bad Size";
                        break;
                    case MICRConst.JPOS_EMICR_JAM:
                        msg = "Paper Jam";
                        break;
                    case MICRConst.JPOS_EMICR_CHECKDIGIT:
                        msg = "Check digit verification failed";
                        break;
                    case MICRConst.JPOS_EMICR_COVEROPEN:
                        msg = "Cover Open";
                        break;
                    case MICRConst.JPOS_EMICR_NOCHECK:
                        msg = "No Check";
                        break;
                    case MICRConst.JPOS_EMICR_CHECK:
                        msg = "Check Present";
                        break;
                }
            }else{
                msg = Integer.toString(errorcode);
            }
            JOptionPane.showMessageDialog(null, "Error Event: "+ msg, "Error Event", JOptionPane.ERROR_MESSAGE);
        }
        
        public void dataOccurred(DataEvent dataEvent) {
            doGetFields();
        }
    }
    
    public void doGetFields()
    {
    	try {
            rawData = micr.getRawData();
            accountNumber = micr.getAccountNumber();
            bankNumber = micr.getBankNumber();
            serialNumber = micr.getSerialNumber();
            
            autoDisable = micr.getAutoDisable();
            dataEventEnabled = micr.getDataEventEnabled();
            deviceEnabled = micr.getDeviceEnabled();
            freezeEvents = micr.getFreezeEvents();
        }
    	catch(JposException je)
    	{
    		JOptionPane.showMessageDialog(null,
    				"doGetFields() caught a JposException: "+ je.getMessage(),
    				"Exception", JOptionPane.ERROR_MESSAGE);
        }
        
        updateDevice = false;
        
        try {
            SwingUtilities.invokeLater(doUpdateGUI);
        } catch(Exception e) {
            System.err.println("InvokeLater exception.");
        }
        
        updateDevice = true;
    }
    
    public MICRPanel(){
        micr = new MICR();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //      MethodListener methodListener = new MethodListener();
        
        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
        JPanel buttonPanel = new JPanel();
        beginInsertionButton = new JButton("Begin Insertion");
        beginInsertionButton.setActionCommand("beginInsertion");
        beginInsertionButton.addActionListener(this);
        beginInsertionButton.setEnabled(false);
        buttonPanel.add(beginInsertionButton);
        
        endInsertionButton = new JButton("End Insertion");
        endInsertionButton.setActionCommand("endInsertion");
        endInsertionButton.addActionListener(this);
        endInsertionButton.setEnabled(false);
        buttonPanel.add(endInsertionButton);
        
        beginRemovalButton = new JButton("Begin Removal");
        beginRemovalButton.setActionCommand("beginRemoval");
        beginRemovalButton.addActionListener(this);
        beginRemovalButton.setEnabled(false);
        buttonPanel.add(beginRemovalButton);
        
        endRemovalButton = new JButton("End Removal");
        endRemovalButton.setActionCommand("endRemoval");
        endRemovalButton.addActionListener(this);
        endRemovalButton.setEnabled(false);
        buttonPanel.add(endRemovalButton);
        
        clearFieldsButton = new JButton("Clear Fields");
        clearFieldsButton.setToolTipText("clears input fields");
        clearFieldsButton.setActionCommand("clearFields");
        clearFieldsButton.addActionListener(this);
        clearFieldsButton.setEnabled(true);
        
        refreshFieldsButton = new JButton("Refresh Fields");
        refreshFieldsButton.setToolTipText("Refresh input fields");
        refreshFieldsButton.setActionCommand("refreshFields");
        refreshFieldsButton.addActionListener(this);
        refreshFieldsButton.setEnabled(true);
        
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        
        mainPanel.add(buttonPanel);
        
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        
        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        autoDisableCB = new JCheckBox("Auto disable");
        Font f = autoDisableCB.getFont();
        Font newf = new Font(f.getName(),Font.PLAIN,f.getSize());
        autoDisableCB.setFont(newf);
        propPanel.add(autoDisableCB);
        dataEventEnabledCB = new JCheckBox("Data event enabled");
        dataEventEnabledCB.setFont(newf);
        propPanel.add(dataEventEnabledCB);
        deviceEnabledCB = new JCheckBox("Device enabled");
        deviceEnabledCB.setFont(newf);
        propPanel.add(deviceEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        freezeEventsCB.setFont(newf);
        propPanel.add(freezeEventsCB);
        
        clearInputButton = new JButton("Clear Input");
        clearInputButton.setActionCommand("clearInput");
        clearInputButton.addActionListener(this);
        
        clearInputPropertiesButton = new JButton("Clear Input Properties");
        clearInputPropertiesButton.setActionCommand("clearInputProperties");
        clearInputPropertiesButton.addActionListener(this);
        
        propPanel.add(clearInputButton);
        propPanel.add(clearInputPropertiesButton);
        
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
        
        autoDisableCB.setEnabled(true);
        dataEventEnabledCB.setEnabled(true);
        deviceEnabledCB.setEnabled(true);
        freezeEventsCB.setEnabled(true);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Account number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Amount:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Bank number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Check type:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Country code:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("EPC:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Raw data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Serial number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        
        label = new JLabel("Transit number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        labelPanel.add(Box.createVerticalGlue());
        
        
        subPanel.add(labelPanel);
        
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        accountNumberTextField = new JTextField();
        accountNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(accountNumberTextField);
        
        amountTextField = new JTextField();
        amountTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(amountTextField);
        
        bankNumberTextField = new JTextField();
        bankNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(bankNumberTextField);
        
        checkTypeTextField = new JTextField();
        checkTypeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(checkTypeTextField);
        
        countryCodeTextField = new JTextField();
        countryCodeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(countryCodeTextField);
        
        epcTextField = new JTextField();
        epcTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(epcTextField);
        
        rawDataTextField = new JTextField();
        rawDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(rawDataTextField);
        
        serialNumberTextField = new JTextField();
        serialNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(serialNumberTextField);
        
        transitNumberTextField = new JTextField();
        transitNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        fieldPanel.add(transitNumberTextField);
        
        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.X_AXIS));
        
        buttonBox.add(refreshFieldsButton);
        buttonBox.add(clearFieldsButton);
        fieldPanel.add(buttonBox);
        
        fieldPanel.add(Box.createVerticalGlue());
        
        subPanel.add(fieldPanel);
        subPanel.add(Box.createHorizontalGlue());
        
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        doUpdateGUI = new Runnable() {
            public void run() {
                updateGUI();
            }
        };
        
        
        return mainPanel;
    }
    
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                micr.open(logicalName);
                micr.addDataListener(listener);
                micr.addErrorListener(listener);
                micr.addStatusUpdateListener(listener);
                int version = micr.getDeviceServiceVersion();
                if(version >= 1009000) {
                    ver_19_complient = true;
                    ver_18_complient = true;
                }
                if(version >= 1008000) {
                    ver_18_complient = true;
                }
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("claim")){
            try{
                micr.claim(0);
                beginInsertionButton.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                micr.release();
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                micr.close();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("beginInsertion")){
            try{
                micr.beginInsertion(-1);
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(true);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in beginInsertion: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("endInsertion")){
            try{
                micr.endInsertion();
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(true);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in endInsertion: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("beginRemoval")){
            try{
                micr.beginRemoval(-1);
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in beginRemoval: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("endRemoval")){
            try{
                micr.endRemoval();
                beginInsertionButton.setEnabled(true);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in endRemoval: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(micr.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + micr.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(micr.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + micr.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + micr.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + micr.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (micr.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (micr.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + micr.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + micr.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + micr.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + micr.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }  
                msg += "\nCapValidationDevice: " + micr.getCapValidationDevice();
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("oce")){			// 7-14-04
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                micr.open(logicalName);
                micr.addDataListener(listener);
                micr.addErrorListener(listener);
                micr.addStatusUpdateListener(listener);
                micr.claim(0);
                beginInsertionButton.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                micr.setDeviceEnabled(true);
                micr.setDataEventEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName
                        + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(micr);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(micr);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
                micr.clearInput();
            }
            catch(JposException je)
            {
                JOptionPane.showMessageDialog(null, "clearInput threw a JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInputProperties"))
        {
            try
            {
            	micr.clearInputProperties();
            }
            catch(JposException je)
            {
                JOptionPane.showMessageDialog(null, "clearInputProperties threw a JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearFields"))
        {
        	accountNumber = "";
        	bankNumber = "";
        	rawData = "";
        	serialNumber = "";
        	accountNumberTextField.setText(new String(accountNumber));
        	amountTextField.setText("");
            bankNumberTextField.setText(new String(bankNumber));
            checkTypeTextField.setText("");
            countryCodeTextField.setText("");
            epcTextField.setText("");
            rawDataTextField.setText(new String(rawData));
            serialNumberTextField.setText(new String(serialNumber));
            transitNumberTextField.setText("");
        }
        else if(ae.getActionCommand().equals("refreshFields"))
        {
        	doGetFields();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Unknown Action event recieved, someone forgot to implement something.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            if(micr.getState() != JposConst.JPOS_S_CLOSED){
                updateDevice = false;
                autoDisableCB.setSelected(micr.getAutoDisable());
                dataEventEnabledCB.setSelected(micr.getDataEventEnabled());
                if( micr.getClaimed()){
                    deviceEnabledCB.setSelected(micr.getDeviceEnabled());
                }
                freezeEventsCB.setSelected(micr.getFreezeEvents());
                updateDevice = true;
            }
        } catch(JposException je) {
            System.err.println("MICRPanel: MethodListener: JposException");
        }
    }
    
    
    
    
    
    
    public void updateGUI() {
        rawDataTextField.setText(new String(rawData));
        accountNumberTextField.setText(new String(accountNumber));
        bankNumberTextField.setText(new String(bankNumber));
        serialNumberTextField.setText(new String(serialNumber));
        
        autoDisableCB.setSelected(autoDisable);
        dataEventEnabledCB.setSelected(dataEventEnabled);
        deviceEnabledCB.setSelected(deviceEnabled);
        freezeEventsCB.setSelected(freezeEvents);
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if(updateDevice) {
                Object source = e.getItemSelectable();
                if (source == autoDisableCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setAutoDisable(false);
                        }else{
                            micr.setAutoDisable(true);
                        }
                    } catch(JposException je) {
                        System.err.println("MICRPanel: CheckBoxListener: autoDisable Jpos Exception: " + je + "\nSource: " + source);
                    }
                    
                } else if (source == dataEventEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setDataEventEnabled(false);
                        }else{
                            micr.setDataEventEnabled(true);
                        }
                    } catch(JposException je) {
                        System.err.println("MICRPanel: CheckBoxListener: dataEventEnable Jpos Exception: " + je + "\nSource: " + source);
                    }
                    
                } else if (source == deviceEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setDeviceEnabled(false);
                        }else{
                            micr.setDeviceEnabled(true);
                        }
                    } catch(JposException je) {
                        System.err.println("MICRPanel: CheckBoxListener: deviceEnable Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == freezeEventsCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setFreezeEvents(false);
                        }else{
                            micr.setFreezeEvents(true);
                        }
                    } catch(JposException je) {
                        System.err.println("MICRPanel: CheckBoxListener: freezeEvents Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                try {
                    updateDevice = false;
                    autoDisableCB.setSelected(micr.getAutoDisable());
                    dataEventEnabledCB.setSelected(micr.getDataEventEnabled());
                    deviceEnabledCB.setSelected(micr.getDeviceEnabled());
                    freezeEventsCB.setSelected(micr.getFreezeEvents());
                    updateDevice = true;
                } catch(JposException je) {
                    System.err.println("MICRPanel: CheckBoxListener method received JposException: "+ je);
                }
                
            }
        }
    }
    
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(micr != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(micr.getState()));
            }
        }
    }
    
}
