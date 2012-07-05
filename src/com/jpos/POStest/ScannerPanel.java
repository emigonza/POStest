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
// ScannerPanel.java - The Scanner panel for POStest
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

public class ScannerPanel extends Component implements DataListener,
        ErrorListener,
        ActionListener {

	private static final long serialVersionUID = -18803538624903477L;

	protected MainButtonPanel mainButtonPanel;
    
    private Scanner scanner = null;
    //private int status = 0;
    
    private String defaultLogicalName = "defaultScanner";
    
    
    byte[] scanData = new byte[] {};
    byte[] scanDataLabel = new byte[] {};
    int    scanDataType = -1;
    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    boolean decodeData;
    
    boolean updateDevice = true;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    //private JTextField scanDataTextField;
    //private JTextField scanDataLabelTextField;
    private JTextField scanDataTypeTextField;
    private JTextArea scanDataTextArea;
    private JScrollPane scanDataTextAreaScrollPane;
    private JTextArea scanDataLabelTextArea;
    private JScrollPane scanDataLabelScrollPane;
    
    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox decodeDataCB;
    
    private JButton clearFieldsButton;
    private JButton refreshFieldsButton;
    
    private JLabel dataCountLabel;
    private java.util.Timer updateDatacountTimer;
    DataCountTimerUpdateTask updateDataCountTask;
    
    private JButton clearInputButton;
    private JButton clearInputPropertiesButton;
    
    Runnable doUpdateGUI;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public ScannerPanel(){
        scanner = new Scanner();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    
    
    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        
        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
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
        decodeDataCB = new JCheckBox("Decode data");
        decodeDataCB.setFont(newf);
        propPanel.add(decodeDataCB);
        
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
        decodeDataCB.setEnabled(true);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        decodeDataCB.addItemListener(cbListener);
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        JPanel scanDataPanel = new JPanel();
        scanDataPanel.setLayout(new BoxLayout(scanDataPanel, BoxLayout.X_AXIS));
        
        JPanel scanDataTypePanel = new JPanel();
        scanDataTypePanel.setLayout(new BoxLayout(scanDataTypePanel, BoxLayout.X_AXIS));
        
        JPanel scanDataLabelPanel = new JPanel();
        scanDataLabelPanel.setLayout(new BoxLayout(scanDataLabelPanel, BoxLayout.X_AXIS));
        
        JPanel clearFieldsPanel = new JPanel();
        clearFieldsPanel.setLayout(new BoxLayout(clearFieldsPanel, BoxLayout.X_AXIS));
        
        
        labelPanel.add(scanDataPanel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        labelPanel.add(scanDataTypePanel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        labelPanel.add(scanDataLabelPanel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        labelPanel.add(clearFieldsPanel);
        
        JLabel label;

        labelPanel.add(Box.createVerticalGlue());
                
        subPanel.add(labelPanel);
        
        label = new JLabel("Scan Data: ");
        label.setPreferredSize(new Dimension(130, 25));
        scanDataTextArea = new JTextArea(5,5);
        scanDataTextArea.setLineWrap(false);
        scanDataTextArea.setWrapStyleWord(false);
        scanDataTextArea.setEditable(false);
        scanDataTextAreaScrollPane = new JScrollPane(scanDataTextArea);
        scanDataTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scanDataTextAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scanDataTextAreaScrollPane.setPreferredSize(new Dimension(300,100));
        scanDataTextAreaScrollPane.setMaximumSize(new Dimension(300,100));
        scanDataTextAreaScrollPane.setAlignmentX(0);
        scanDataPanel.add(label);
        scanDataPanel.add(scanDataTextAreaScrollPane);
        scanDataPanel.add(Box.createHorizontalGlue());
        
        label = new JLabel("Scan Data Type: ");
        label.setPreferredSize(new Dimension(130, 25));
        scanDataTypeTextField = new JTextField(30);
        scanDataTypeTextField.setMaximumSize(new Dimension(60, 25));
        scanDataTypeTextField.setPreferredSize(new Dimension(60, 25));     
        scanDataTypeTextField.setEditable(false);
        scanDataTypePanel.add(label);
        scanDataTypePanel.add(scanDataTypeTextField);
        scanDataTypePanel.add(Box.createHorizontalGlue());
        
        label = new JLabel("Scan Data Label: ");
        label.setPreferredSize(new Dimension(130, 25));
        scanDataLabelTextArea = new JTextArea(5,5);
        scanDataLabelTextArea.setLineWrap(false);
        scanDataLabelTextArea.setWrapStyleWord(false);
        scanDataLabelTextArea.setEditable(false);
        scanDataLabelScrollPane = new JScrollPane(scanDataLabelTextArea);
        scanDataLabelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scanDataLabelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scanDataLabelScrollPane.setPreferredSize(new Dimension(300,100));
        scanDataLabelScrollPane.setMaximumSize(new Dimension(300,100));
        scanDataLabelPanel.add(label);
        scanDataLabelPanel.add(scanDataLabelScrollPane);
        scanDataLabelPanel.add(Box.createHorizontalGlue());
        
        dataCountLabel = new JLabel("Data Count: ");
        dataCountLabel.setMaximumSize(new Dimension(Short.MAX_VALUE,25));
        label.setPreferredSize(new Dimension(130, 25));
        
        clearFieldsButton = new JButton("Clear Fields");
        clearFieldsButton.setActionCommand("clearFields");
        clearFieldsButton.addActionListener(this);
        
        refreshFieldsButton = new JButton("Refresh Fields");
        refreshFieldsButton.setActionCommand("refreshFields");
        refreshFieldsButton.addActionListener(this);
        
        clearFieldsPanel.add(dataCountLabel);
        clearFieldsPanel.add(clearFieldsButton);
        clearFieldsPanel.add(refreshFieldsButton);
        clearFieldsPanel.add(Box.createHorizontalGlue());
        
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
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                
                scanner.open(logicalName);
                scanner.addErrorListener(this);
                scanner.addDataListener(this);
                int version = scanner.getDeviceServiceVersion();
                if(version >= 1009000) {
                    ver_19_complient = true;
                    ver_18_complient = true;
                }
                if(version >= 1008000) {
                    ver_18_complient = true;
                }
                updateDatacountTimer = new java.util.Timer(true);
                updateDataCountTask =  new DataCountTimerUpdateTask();
                updateDatacountTimer.schedule(updateDataCountTask, 200, 200);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on open " + e);
            }
        } else if(ae.getActionCommand().equals("claim")){
            try{
                scanner.claim(0);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on claim " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                scanner.release();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on release " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                scanner.close();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on close" + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(scanner.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + scanner.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(scanner.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + scanner.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + scanner.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + scanner.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (scanner.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (scanner.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + scanner.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + scanner.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + scanner.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + scanner.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("oce")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                scanner.open(logicalName);
                scanner.addErrorListener(this);
                scanner.addDataListener(this);
                scanner.claim(1000);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                scanner.setDeviceEnabled(true);
                scanner.setDataEventEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(scanner);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(scanner);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }else if(ae.getActionCommand().equals("exit")){
        	if(scanner.getState() != JposConst.JPOS_S_CLOSED){
        		try {
					scanner.close();
				} catch (JposException e) {
					JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
				}
        	}
        }
        else if(ae.getActionCommand().equals("clearFields"))
        {
        	scanData = new byte[] {};
        	scanDataLabel = new byte[] {};
        	scanDataType = -1;
        	scanDataTextArea.setText("");
        	scanDataLabelTextArea.setText("");
        	scanDataTypeTextField.setText("");
        }
        else if(ae.getActionCommand().equals("refreshFields"))
        {
        	doDataUpdate();
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
            	scanner.clearInput();
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
            	scanner.clearInputProperties();
            }
            catch(JposException je)
            {
                JOptionPane.showMessageDialog(null, "clearInputProperties threw a JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Unknown Action event recieved, someone forgot to implement something.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(scanner.getState() != JposConst.JPOS_S_CLOSED){
            updateGUI();
        }
        mainButtonPanel.action(ae);
        
    }
    
    public static String getBarcodeTypeName(int code){
        String val = "Unknown";
        switch (code){
            case ScannerConst.SCAN_SDT_UPCA:
                val = "UPC-A";
                break;
            case ScannerConst.SCAN_SDT_UPCE:
                val = " UPC-E";
                break;
            case ScannerConst.SCAN_SDT_JAN8:
                val = "JAN 8 / EAN 8";
                break;
            case ScannerConst.SCAN_SDT_JAN13:
                val = "JAN 13 / EAN 13";
                break;
            case ScannerConst.SCAN_SDT_TF:
                val = "2 of 5";
                break;
            case ScannerConst.SCAN_SDT_ITF:
                val = "Interleaved 2 of 5";
                break;
            case ScannerConst.SCAN_SDT_Codabar:
                val = "Codabar";
                break;
            case ScannerConst.SCAN_SDT_Code39:
                val = "Code 39";
                break;
            case ScannerConst.SCAN_SDT_Code93:
                val = "Code 93";
                break;
            case ScannerConst.SCAN_SDT_Code128:
                val = "Code 128";
                break;
            case ScannerConst.SCAN_SDT_UPCA_S:
                val = " UPC-A with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCE_S:
                val = "UPC-E with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCD1:
                val = "UPC-D1";
                break;
            case ScannerConst.SCAN_SDT_UPCD2:
                val = "UPC-D2";
                break;
            case ScannerConst.SCAN_SDT_UPCD3:
                val = "UPC-D3";
                break;
            case ScannerConst.SCAN_SDT_UPCD4:
                val = "UPC-D4";
                break;
            case ScannerConst.SCAN_SDT_UPCD5:
                val = "UPC-D5";
                break;
            case ScannerConst.SCAN_SDT_EAN8_S:
                val = "EAN-8 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN13_S:
                val = "EAN-13 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN128:
                val = "EAN-128";
                break;
            case ScannerConst.SCAN_SDT_OCRA:
                val = "OCR \"A\"";
                break;
            case ScannerConst.SCAN_SDT_OCRB:
                val = "OCR \"B\"";
                break;
            case ScannerConst.SCAN_SDT_PDF417:
                val = "PDF 417";
                break;
            case ScannerConst.SCAN_SDT_MAXICODE:
                val = "MAXICODE";
                break;
            case ScannerConst.SCAN_SDT_OTHER:
                val = "Other";
                break;
        }
        return val;
    }
    
    public void dataOccurred(DataEvent de)
    {
        doDataUpdate();
    }
    
    public void doDataUpdate()
    {
    	try
    	{
            scanData = scanner.getScanData();
            scanDataLabel = scanner.getScanDataLabel();
            scanDataType = scanner.getScanDataType();
            autoDisable = scanner.getAutoDisable();
            dataEventEnabled = scanner.getDataEventEnabled();
            deviceEnabled = scanner.getDeviceEnabled();
            freezeEvents = scanner.getFreezeEvents();
            decodeData = scanner.getDecodeData();
        }
    	catch(JposException je)
    	{
    		JOptionPane.showMessageDialog(null, "Exception in doDataUpdate(): "+ je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        
        updateDevice = false;
        
        try
        {
            SwingUtilities.invokeLater(doUpdateGUI);
        }
        catch(Exception e)
        {
            System.err.println("InvokeLater exception.");
        }
        updateDevice = true;
    }
    
    public void errorOccurred(ErrorEvent ee) {
        System.out.println("Error Occurred");
    }
    
    public void updateGUI() {
        scanDataTextArea.setText(new String(scanData));
        scanDataLabelTextArea.setText(new String(scanDataLabel));

        if(scanDataType == -1){
        	scanDataTypeTextField.setText("");
        }else{
        	scanDataTypeTextField.setText(Integer.toString(scanDataType) + " ("+getBarcodeTypeName(scanDataType)+")");
        }
        
        try {
            updateDevice = false;
            autoDisableCB.setSelected(scanner.getAutoDisable());
            dataEventEnabledCB.setSelected(scanner.getDataEventEnabled());
            deviceEnabledCB.setSelected(scanner.getDeviceEnabled());
            freezeEventsCB.setSelected(scanner.getFreezeEvents());
            decodeDataCB.setSelected(scanner.getDecodeData());
            updateDevice = true;
        } catch(JposException je) {
            System.err.println("ScannerPanel: updateGUI method received JposException: "+ je);
        }
    }
    
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if(updateDevice) {
                Object source = e.getItemSelectable();
                if (source == autoDisableCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setAutoDisable(false);
                        }else{
                            scanner.setAutoDisable(true);
                        }
                    } catch(JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                    
                } else if (source == dataEventEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDataEventEnabled(false);
                        }else{
                            scanner.setDataEventEnabled(true);
                        }
                    } catch(JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                    
                } else if (source == deviceEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDeviceEnabled(false);
                        }else{
                            scanner.setDeviceEnabled(true);
                        }
                    } catch(JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == freezeEventsCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setFreezeEvents(false);
                        }else{
                            scanner.setFreezeEvents(true);
                        }
                    } catch(JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == decodeDataCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDecodeData(false);
                        }else{
                            scanner.setDecodeData(true);
                        }
                    } catch(JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                updateGUI();
            }
            
            
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(scanner != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(scanner.getState()));
            }
        }
    }
    private class DataCountTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            try{
                dataCountLabel.setText("Data Count: " + Integer.toString(scanner.getDataCount()));
            }catch(JposException e){
                System.out.println("Failed to retrieve the data count:\n" + e.getMessage());
            }
        }
    }
}
