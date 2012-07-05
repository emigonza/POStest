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
// MSRPanel.java - The MSR panel for POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-15-2004
//------------------------------------------------------------------------------

package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class MSRPanel extends Component implements DataListener, ActionListener {

	private static final long serialVersionUID = -1934362106766686694L;

	protected MSR msr;
    
    protected MainButtonPanel mainButtonPanel;
    
    private String defaultLogicalName = "defaultMSR";
    
    byte[] track1Data = new byte[] {};
    byte[] track2Data=  new byte[] {};
    byte[] track3Data = new byte[] {};
    byte[] track4Data = new byte[] {};
    String accountNumber = new String("");
    String expirationDate = new String("");
    String title = new String("");
    String firstName = new String("");
    String middleInitial = new String("");
    String surname = new String("");
    String suffix = new String("");
    String serviceCode = new String("");
    byte[] t1DiscData = new byte[] {};
    byte[] t2DiscData = new byte[] {};
    
    private boolean ver_110_complient = false;
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
//    private boolean ver_17_complient = false;
//    private boolean ver_16_complient = false;
    
    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    boolean decodeData;
    boolean parseDecodeData;
    
    boolean updateDevice = true;
    
    private JTextField track1DataTextField;
    private JTextField track2DataTextField;
    private JTextField track3DataTextField;
    private JTextField track4DataTextField;
    
    private JTextField t1LengthTF;
    private JTextField t2LengthTF;
    private JTextField t3LengthTF;
    private JTextField t4LengthTF;
    
    private int t1Length;
    private int t2Length;
    private int t3Length;
    private int t4Length;
    
    private JTextField accountNumberTextField;
    private JTextField expirationDateTextField;
    private JTextField titleTextField;
    private JTextField firstNameTextField;
    private JTextField middleInitialTextField;
    private JTextField surnameTextField;
    private JTextField suffixTextField;
    private JTextField serviceCodeTextField;
    private JTextField t1DiscDataTextField;
    private JTextField t2DiscDataTextField;
    
    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox decodeDataCB;
    private JCheckBox parseDecodeDataCB;
    private JCheckBox transmitSentinels;
    private JCheckBox perTrackErrorReporting;
    
    private JCheckBox track1Enabled;
    private JCheckBox track2Enabled;
    private JCheckBox track3Enabled;
    private JCheckBox track4Enabled;
    
    private JLabel dataCountLabel;
    
    private JButton clearFieldsButton;
    private JButton refreshFieldsButton;
    
    private JButton clearInputButton;
    private JButton clearInputPropertiesButton;
    
    private java.util.Timer updateDatacountTimer;
    DataCountTimerUpdateTask updateDataCountTask;
    
    Runnable doUpdateGUI;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    // MSR Writer variables
    private JCheckBox write1CB;
    private JCheckBox write2CB;
    private JCheckBox write3CB;
    private JCheckBox write4CB;
    private JLabel encodingMaxLengthLabel;
    private JButton updateTracksToWriteButton;
    private JTextField dataToWriteTextField;
    private JTextField writeTracksTimeoutTextField;
    private JButton writeTracksButton;
    
    public MSRPanel() {
        msr = new MSR();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public MSR getMSR() {
        return msr;
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
        
        parseDecodeDataCB = new JCheckBox("Parse decode data");
        parseDecodeDataCB.setFont(newf);
        
        propPanel.add(parseDecodeDataCB);
        
        transmitSentinels = new JCheckBox("Transmit sentinels");
        transmitSentinels.setFont(newf);
        
        propPanel.add(transmitSentinels);
        
        perTrackErrorReporting = new JCheckBox("Per-track error reporting");
        perTrackErrorReporting.setFont(newf);
        
        propPanel.add(perTrackErrorReporting);
        
        track1Enabled = new JCheckBox("Track 1 enabled");
        track1Enabled.setFont(newf);
        
        propPanel.add(track1Enabled);
        
        track2Enabled = new JCheckBox("Track 2 enabled");
        track2Enabled.setFont(newf);
        
        propPanel.add(track2Enabled);
        
        track3Enabled = new JCheckBox("Track 3 enabled");
        track3Enabled.setFont(newf);
        
        propPanel.add(track3Enabled);
        
        track4Enabled = new JCheckBox("Track 4 enabled");
        track4Enabled.setFont(newf);
        
        propPanel.add(track4Enabled);
        
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
        
        enableCheckBoxes(false);
        
        //setup listeners for check boxes
        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        decodeDataCB.addItemListener(cbListener);
        parseDecodeDataCB.addItemListener(cbListener);
        transmitSentinels.addItemListener(cbListener);
        perTrackErrorReporting.addItemListener(cbListener);
        track1Enabled.addItemListener(cbListener);
        track2Enabled.addItemListener(cbListener);
        track3Enabled.addItemListener(cbListener);
        track4Enabled.addItemListener(cbListener);
        
        clearFieldsButton = new JButton("Clear Fields");
        clearFieldsButton.setActionCommand("clearFields");
        clearFieldsButton.addActionListener(this);
        
        refreshFieldsButton = new JButton("Refresh Fields");
        refreshFieldsButton.setActionCommand("refreshFields");
        refreshFieldsButton.addActionListener(this);
        
        updateTracksToWriteButton = new JButton("Set tracks to write");
        updateTracksToWriteButton.setActionCommand("setTracksToWrite");
        updateTracksToWriteButton.addActionListener(this);

        writeTracksButton= new JButton("Write Tracks!");
        writeTracksButton.setActionCommand("writeTracks");
        writeTracksButton.addActionListener(this);

        
        JTabbedPane tabbedPane = new JTabbedPane();
        subPanel.add(tabbedPane);
        
        MSRReadPanel readPanel = new MSRReadPanel();
        tabbedPane.addTab("MSR Reading", null, readPanel.make(), "MSR Reading");
        
        MSRWritePanel writePanel = new MSRWritePanel();
        tabbedPane.addTab("MSR Writing", null, writePanel.make(), "MSR Writing");

        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        doUpdateGUI = new Runnable() {
            public void run() {
                updateGUI();
            }
        };
        
        return mainPanel;
    }
    
    private void enableCheckBoxes(boolean enable){
        autoDisableCB.setEnabled(enable);
        dataEventEnabledCB.setEnabled(enable);
        deviceEnabledCB.setEnabled(enable);
        freezeEventsCB.setEnabled(enable);
        decodeDataCB.setEnabled(enable);
        parseDecodeDataCB.setEnabled(enable);
        transmitSentinels.setEnabled(enable);
        perTrackErrorReporting.setEnabled(enable);
        track1Enabled.setEnabled(enable);
        track2Enabled.setEnabled(enable);
        track3Enabled.setEnabled(enable);
        track4Enabled.setEnabled(enable);
    }
    
    public void doOpen() throws JposException{
        String logicalName = mainButtonPanel.getLogicalName();
        if(logicalName.equals("")){
            logicalName = defaultLogicalName;
        }
        msr.open(logicalName);
        msr.addDataListener(this);
        msr.addErrorListener(new ErrorListener(){
            public void errorOccurred(jpos.events.ErrorEvent e){
                String msg = new String("MSR Error Event Occured\n");
                msg += "Error Code = " + com.jpos.POStest.MainButtonPanel.getErrorName(e.getErrorCode()) + "\n";
                if(e.getErrorCode() == JposConst.JPOS_E_EXTENDED){
                    int error = e.getErrorCodeExtended();
                    msg += "Extended Error Code:\n";
                    msg += "  Track 1 = " + com.jpos.POStest.MainButtonPanel.getErrorName((error & 0xFF))+ "\n";
                    msg += "  Track 2 = " + com.jpos.POStest.MainButtonPanel.getErrorName(((error & 0xFF00) >> 8 )) + "\n";
                    msg += "  Track 3 = " + com.jpos.POStest.MainButtonPanel.getErrorName(((error & 0xFF0000) >> 16 )) + "\n";
                    msg += "  Track 4 = " + com.jpos.POStest.MainButtonPanel.getErrorName(((error & 0xFF000000) >> 32 )) + "\n";
                }
                try{
                    ErrorPromptDlg dlg = new ErrorPromptDlg(e, msg);
                    dlg.setVisible(true);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Exception: "+ ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
                //JOptionPane.showMessageDialog(null, msg, "Error Event", JOptionPane.ERROR_MESSAGE);
                updateGUI();
            }
        });
        updateDatacountTimer = new java.util.Timer(true);
        updateDataCountTask =  new DataCountTimerUpdateTask();
        updateDatacountTimer.schedule(updateDataCountTask, 200, 200);
        int version = msr.getDeviceServiceVersion();
        if(version >= 1010000)
        {
        	ver_110_complient = true;
            ver_19_complient = true;
            ver_18_complient = true;
        }
        if(version >= 1009000)
        {
            ver_19_complient = true;
            ver_18_complient = true;
        }
        if(version >= 1008000)
        {
            ver_18_complient = true;
        }

    }
    public void doClaim() throws JposException{
        msr.claim(1000);
    }
    
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open")){
            try{
                doOpen();
                enableCheckBoxes(true);
                updateGUI();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
            
            
        } else if(ae.getActionCommand().equals("claim")){
            try{
                doClaim();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                msr.release();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                if(updateDatacountTimer != null){
                    updateDatacountTimer.cancel();
                    updateDatacountTimer = null;
                    updateDataCountTask = null;
                }
                msr.close();
                enableCheckBoxes(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(msr.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + msr.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(msr.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + msr.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + msr.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + msr.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (msr.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (msr.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + msr.getCapStatisticsReporting();
                	msg += "\nCapUpdateStatistics: " + msr.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + msr.getCapCompareFirmwareVersion();
                	msg += "\nCapUpdateFirmware: " + msr.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                if(ver_110_complient)
                {
                	msg += "\nCapWritableTracks: " + getCapWritableTracksString(msr.getCapWritableTracks());
                }
                else
                {
                	msg += "\nCapWritableTracks: Service Object is not 1.10 complient";
                }
                
                msg += "\nCapISO: "+ msr.getCapISO();
                msg += "\nCapJISOne: "+ msr.getCapJISOne();
                msg += "\nCapJISTwo: "+ msr.getCapJISTwo();
                msg += "\nCapTransmitSentinels: " + msr.getCapTransmitSentinels();
                
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("oce")){
            //try{
            if(logicalName.equals("")){
                logicalName = defaultLogicalName;
            }
            try{
                doOpen();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
                return;
            }
            
            enableCheckBoxes(true);
            updateGUI();
            try{
                doClaim();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
                return;
            }
            updateGUI();
            
            deviceEnabledCB.doClick();
            updateGUI();
        } else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(msr);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(msr);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } else if(ae.getActionCommand().equals("clearFields")){
            try{
                track1Data = new byte[] {};
                track2Data = new byte[] {};
                track3Data = new byte[] {};
                track4Data = new byte[] {};
                accountNumber = "";
                expirationDate = "";
                title = "";
                firstName = "";
                middleInitial = "";
                surname = "";
                suffix = "";
                serviceCode = "";
                t1Length = 0;
                t2Length = 0;
                t3Length = 0;
                t4Length = 0;
                t1DiscData = new byte[] {};
                t2DiscData = new byte[] {};
                //updateGUI();
                track1DataTextField.setText(new String(track1Data));
                track2DataTextField.setText(new String(track2Data));
                track3DataTextField.setText(new String(track3Data));
                track4DataTextField.setText(new String(track4Data));
                
                t1LengthTF.setText(Integer.toString(t1Length));
                t2LengthTF.setText(Integer.toString(t2Length));
                t3LengthTF.setText(Integer.toString(t3Length));
                t4LengthTF.setText(Integer.toString(t4Length));
                
                accountNumberTextField.setText(new String(accountNumber));
                expirationDateTextField.setText(new String(expirationDate));
                titleTextField.setText(new String(title));
                firstNameTextField.setText(new String(firstName));
                middleInitialTextField.setText(new String(middleInitial));
                surnameTextField.setText(new String(surname));
                suffixTextField.setText(new String(suffix));
                serviceCodeTextField.setText(new String(serviceCode));
                t1DiscDataTextField.setText(new String(t1DiscData));
                t2DiscDataTextField.setText(new String(t2DiscData));
                try {
                    autoDisableCB.setSelected(msr.getAutoDisable());
                    dataEventEnabledCB.setSelected(msr.getDataEventEnabled());
                    deviceEnabledCB.setSelected(msr.getDeviceEnabled());
                    freezeEventsCB.setSelected(msr.getFreezeEvents());
                    decodeDataCB.setSelected(msr.getDecodeData());
                    parseDecodeDataCB.setSelected(msr.getParseDecodeData());
                    
                    transmitSentinels.setSelected(msr.getTransmitSentinels());
                    perTrackErrorReporting.setSelected(msr.getErrorReportingType() == MSRConst.MSR_ERT_TRACK);
                    
                    int tracks = msr.getTracksToRead();
                    track1Enabled.setSelected((tracks & MSRConst.MSR_TR_1) > 0);
                    track2Enabled.setSelected((tracks & MSRConst.MSR_TR_2) > 0);
                    track3Enabled.setSelected((tracks & MSRConst.MSR_TR_3) > 0);
                    track4Enabled.setSelected((tracks & MSRConst.MSR_TR_4) > 0);
                } catch(JposException je) {
                    JOptionPane.showMessageDialog(null, "UpdateGUI method received JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception e){
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
                msr.clearInput();
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
            	msr.clearInputProperties();
            }
            catch(JposException je)
            {
                JOptionPane.showMessageDialog(null, "clearInputProperties threw a JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("refreshFields"))
        {
        	try
        	{
        		getMSRFieldData();
        		updateGUI();
        	}
        	catch(JposException je)
        	{
        		JOptionPane.showMessageDialog(null, "JposException caught while refreshing fields: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        	}
        }
        else if(ae.getActionCommand().equals("setTracksToWrite"))
        {
        	try
        	{
        		int tracks = 0;
        		if(write1CB.isSelected())
        		{
        			tracks |= MSRConst.MSR_TR_1;
        		}
        		if(write2CB.isSelected())
        		{
        			tracks |= MSRConst.MSR_TR_2;
        		}
        		if(write3CB.isSelected())
        		{
        			tracks |= MSRConst.MSR_TR_3;
        		}
        		if(write4CB.isSelected())
        		{
        			tracks |= MSRConst.MSR_TR_4;
        		}
        		msr.setTracksToWrite(tracks);
        	}
        	catch(JposException je)
        	{
        		JOptionPane.showMessageDialog(null, "JposException caught in setTracksToWrite(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        	}
        	updateGUI();
        }
        else if(ae.getActionCommand().equals("writeTracks"))
        {
        	try
        	{
        	    /*
        		msr.writeTracks(dataToWriteTextField.getText(),
        				Integer.parseInt(writeTracksTimeoutTextField.getText()));
        	     */
        	    
        	    byte [] data = dataToWriteTextField.getText().getBytes();
        	    byte [][] dataArray = {};
        	    dataArray [0] = data;
        	    msr.writeTracks(dataArray,
                        Integer.parseInt(writeTracksTimeoutTextField.getText()));
        	    
        	}
        	catch(JposException je)
        	{
        		JOptionPane.showMessageDialog(null, "JposException caught in writeTracks(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        	}
        	catch(Exception ex)
        	{
        		JOptionPane.showMessageDialog(null, "JposException caught: "+ ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        	}
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Unknown Action event recieved, someone forgot to implement something.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            //only do this if the device is not closed
            if(msr.getState() != JposConst.JPOS_S_CLOSED){
                updateDevice = false;
                autoDisableCB.setSelected(msr.getAutoDisable());
                dataEventEnabledCB.setSelected(msr.getDataEventEnabled());
                deviceEnabledCB.setSelected(msr.getDeviceEnabled());
                freezeEventsCB.setSelected(msr.getFreezeEvents());
                decodeDataCB.setSelected(msr.getDecodeData());
                parseDecodeDataCB.setSelected(msr.getParseDecodeData());
                perTrackErrorReporting.setSelected(msr.getErrorReportingType() == MSRConst.MSR_ERT_TRACK);
                updateDevice = true;
            }
        } catch(JposException je) {
            System.err.println("MSRPanel: JposException" + je);
        }
    }
    
    String getCapWritableTracksString(int cap)
    {
        String retVal = "";
        if(cap == MSRConst.MSR_TR_NONE)
        {
            return "The MSR does not support writing track data.";
        }
        else
        {
            if((cap & MSRConst.MSR_TR_1) > 0)
            {
                return "Track 1, ";
            }
            if((cap & MSRConst.MSR_TR_2) > 0)
            {
                return "Track 2, ";
            }
            if((cap & MSRConst.MSR_TR_3) > 0)
            {
                return "Track 3, ";
            }
            if((cap & MSRConst.MSR_TR_4) > 0)
            {
                return "Track 4, ";
            }
        }
        return retVal;
    }
    
    public void dataOccurred(DataEvent de)
    {
        try
        {
        	getMSRFieldData();
            
            t1Length = de.getStatus() & 0xFF;
            t2Length = (de.getStatus() & 0xFF00) >> 8;
            t3Length = (de.getStatus() & 0xFF0000) >> 16;
            t4Length = (de.getStatus() & 0xFF000000) >> 32;
            
            autoDisable = msr.getAutoDisable();
            dataEventEnabled = msr.getDataEventEnabled();
            deviceEnabled = msr.getDeviceEnabled();
            freezeEvents = msr.getFreezeEvents();
            decodeData = msr.getDecodeData();
            parseDecodeData = msr.getParseDecodeData();
        }
        catch(JposException je)
        {
            System.err.println("MSR: Jpos Exception");
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
    
    public void getMSRFieldData() throws JposException
    {
    	track1Data = msr.getTrack1Data();
        track2Data = msr.getTrack2Data();
        track3Data = msr.getTrack3Data();
        track4Data = msr.getTrack4Data();
        
        accountNumber = msr.getAccountNumber();
        expirationDate = msr.getExpirationDate();
        title = msr.getTitle();
        firstName = msr.getFirstName();
        middleInitial = msr.getMiddleInitial();
        surname = msr.getSurname();
        suffix = msr.getSuffix();
        serviceCode = msr.getServiceCode();
        t1DiscData = msr.getTrack1DiscretionaryData();
        t2DiscData = msr.getTrack2DiscretionaryData();
    }
    
    public void updateGUI() {
        track1DataTextField.setText(new String(track1Data));
        track2DataTextField.setText(new String(track2Data));
        track3DataTextField.setText(new String(track3Data));
        track4DataTextField.setText(new String(track4Data));
        
        t1LengthTF.setText(Integer.toString(t1Length));
        t2LengthTF.setText(Integer.toString(t2Length));
        t3LengthTF.setText(Integer.toString(t3Length));
        t4LengthTF.setText(Integer.toString(t4Length));
        
        accountNumberTextField.setText(new String(accountNumber));
        expirationDateTextField.setText(new String(expirationDate));
        titleTextField.setText(new String(title));
        firstNameTextField.setText(new String(firstName));
        middleInitialTextField.setText(new String(middleInitial));
        surnameTextField.setText(new String(surname));
        suffixTextField.setText(new String(suffix));
        serviceCodeTextField.setText(new String(serviceCode));
        t1DiscDataTextField.setText(new String(t1DiscData));
        t2DiscDataTextField.setText(new String(t2DiscData));
        
        try {
            autoDisableCB.setSelected(msr.getAutoDisable());
            dataEventEnabledCB.setSelected(msr.getDataEventEnabled());
            deviceEnabledCB.setSelected(msr.getDeviceEnabled());
            freezeEventsCB.setSelected(msr.getFreezeEvents());
            decodeDataCB.setSelected(msr.getDecodeData());
            parseDecodeDataCB.setSelected(msr.getParseDecodeData());
            
            transmitSentinels.setSelected(msr.getTransmitSentinels());
            perTrackErrorReporting.setSelected(msr.getErrorReportingType() == MSRConst.MSR_ERT_TRACK);
            
            int tracks = msr.getTracksToRead();
            track1Enabled.setSelected((tracks & MSRConst.MSR_TR_1) > 0);
            track2Enabled.setSelected((tracks & MSRConst.MSR_TR_2) > 0);
            track3Enabled.setSelected((tracks & MSRConst.MSR_TR_3) > 0);
            track4Enabled.setSelected((tracks & MSRConst.MSR_TR_4) > 0);
            
            // update the tracks to write, but only if we're 1.10 or higher
            if(ver_110_complient)
            {
            	int writeTracks = msr.getTracksToWrite();
                write1CB.setSelected((writeTracks & MSRConst.MSR_TR_1) > 0);
                write2CB.setSelected((writeTracks & MSRConst.MSR_TR_2) > 0);
                write3CB.setSelected((writeTracks & MSRConst.MSR_TR_3) > 0);
                write4CB.setSelected((writeTracks & MSRConst.MSR_TR_4) > 0);
                
                encodingMaxLengthLabel.setText(
                		"Encoding Max Length: " + 
                		String.valueOf(msr.getEncodingMaxLength()));
            }
        } catch(JposException je) {
            JOptionPane.showMessageDialog(null, "UpdateGUI method received JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            System.err.println("MSRPanel: UpdateGUI method received JposException: "+ je);
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if(updateDevice) {
                Object source = e.getItemSelectable();
                if (source == autoDisableCB){
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            msr.setAutoDisable(false);
                        } else{
                            msr.setAutoDisable(true);
                        }
                    } catch(JposException je)  {
                        JOptionPane.showMessageDialog(null, "exception in setAutoDisable(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setAutoDisable(): " + je);
                    }
                    
                } else if (source == dataEventEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setDataEventEnabled(false);
                        } else {
                            msr.setDataEventEnabled(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setDataEventEnabled(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setDataEventEnabled(): " + je);
                    }
                } else if (source == deviceEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setDeviceEnabled(false);
                        } else {
                            msr.setDeviceEnabled(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setDeviceEnabled(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setDeviceEnabled(): " + je);
                    }
                } else if (source == freezeEventsCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setFreezeEvents(false);
                        } else {
                            msr.setFreezeEvents(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setFreezeEvents(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setFreezeEvents(): " + je);
                    }
                } else if (source == decodeDataCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setDecodeData(false);
                        } else {
                            msr.setDecodeData(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setDecodeData(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setDecodeData(): " + je);
                    }
                } else if (source == parseDecodeDataCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setParseDecodeData(false);
                        } else {
                            msr.setParseDecodeData(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setParseDecodeData(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setParseDecodeData(): " + je);
                    }
                } else if (source == transmitSentinels) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setTransmitSentinels(false);
                        } else {
                            msr.setTransmitSentinels(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTransmitSentinels(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTransmitSentinels(): " + je);
                    }
                } else if (source == perTrackErrorReporting) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setErrorReportingType(MSRConst.MSR_ERT_CARD);
                        } else {
                            msr.setErrorReportingType(MSRConst.MSR_ERT_TRACK);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTransmitSentinels(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTransmitSentinels(): " + je);
                    }
                } else if (source == track1Enabled) {
                    try {
                        int tracks = msr.getTracksToRead();
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setTracksToRead( tracks &= (~MSRConst.MSR_TR_1));
                        } else {
                            msr.setTracksToRead( tracks |= MSRConst.MSR_TR_1);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTracksToRead(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTracksToRead(): " + je);
                    }
                } else if (source == track2Enabled) {
                    try {
                        int tracks = msr.getTracksToRead();
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setTracksToRead( tracks &= (~MSRConst.MSR_TR_2));
                        } else {
                            msr.setTracksToRead( tracks |= MSRConst.MSR_TR_2);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTracksToRead(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTracksToRead(): " + je);
                    }
                } else if (source == track3Enabled) {
                    try {
                        int tracks = msr.getTracksToRead();
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setTracksToRead( tracks &= (~MSRConst.MSR_TR_3));
                        } else {
                            msr.setTracksToRead( tracks |= MSRConst.MSR_TR_3);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTracksToRead(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTracksToRead(): " + je);
                    }
                } else if (source == track4Enabled) {
                    try {
                        int tracks = msr.getTracksToRead();
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            msr.setTracksToRead( tracks &= (~MSRConst.MSR_TR_4));
                        } else {
                            msr.setTracksToRead( tracks |= MSRConst.MSR_TR_4);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "exception in setTracksToRead(): "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                        System.err.println("MSRPanel: exception in setTracksToRead(): " + je);
                    }
                }
                updateGUI();
            }
        }
    }
    
    private class DataCountTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            try{
                dataCountLabel.setText("Data Count: " + Integer.toString(msr.getDataCount()));
            }catch(JposException e){
                System.out.println("Failed to retrieve the data count:\n" + e.getMessage());
            }
        }
    }
    
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(msr != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(msr.getState()));
            }
        }
    }
    
    class MSRReadPanel extends Component
    {
		private static final long serialVersionUID = 5550374415910334154L;

		public Component make()
    	{
			JPanel mainBox = new JPanel();
			mainBox.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.X_AXIS));

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
            
            JLabel dataLabel = new JLabel("Track 1 Data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Track 2 Data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Track 3 Data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Track 4 Data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Account number: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Expiration date: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Title: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("First name: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Middle initial: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Surname: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Suffix: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Service Code: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Track 1 discretionary data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataLabel = new JLabel("Track 2 discretionary data: ");
            dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            labelPanel.add(dataLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
            
            dataCountLabel = new JLabel("Data Count: ");
            dataCountLabel.setMaximumSize(new Dimension(Short.MAX_VALUE,25));
            labelPanel.add(dataCountLabel);
            
            labelPanel.add(Box.createVerticalGlue());
            
            
            mainBox.add(labelPanel);
            
            JPanel countPanel = new JPanel();
            countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.Y_AXIS));
            
            t1LengthTF = new JTextField(4);
            t1LengthTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            countPanel.add(t1LengthTF);
            
            t2LengthTF = new JTextField(4);
            t2LengthTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            countPanel.add(t2LengthTF);
            
            t3LengthTF = new JTextField(4);
            t3LengthTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            countPanel.add(t3LengthTF);
            
            t4LengthTF = new JTextField(4);
            t4LengthTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            countPanel.add(t4LengthTF);
            
            countPanel.add(Box.createVerticalGlue());
            
            
            JPanel tfPanel = new JPanel();
            tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.Y_AXIS));
            
            track1DataTextField = new JTextField(50);
            track1DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(track1DataTextField);
            track2DataTextField = new JTextField(50);
            track2DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(track2DataTextField);
            track3DataTextField = new JTextField(50);
            track3DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(track3DataTextField);
            track4DataTextField = new JTextField(50);
            track4DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(track4DataTextField);
            
            
            accountNumberTextField = new JTextField(50);
            accountNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(accountNumberTextField);
            expirationDateTextField = new JTextField(50);
            expirationDateTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(expirationDateTextField);
            titleTextField = new JTextField(50);
            titleTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(titleTextField);
            firstNameTextField = new JTextField(50);
            firstNameTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(firstNameTextField);
            middleInitialTextField = new JTextField(50);
            middleInitialTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(middleInitialTextField);
            surnameTextField = new JTextField(50);
            surnameTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(surnameTextField);
            suffixTextField = new JTextField(50);
            suffixTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(suffixTextField);
            serviceCodeTextField = new JTextField(50);
            serviceCodeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(serviceCodeTextField);
            t1DiscDataTextField = new JTextField(50);
            t1DiscDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(t1DiscDataTextField);
            t2DiscDataTextField = new JTextField(50);
            t2DiscDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            tfPanel.add(t2DiscDataTextField);
            
            tfPanel.add(Box.createRigidArea(new Dimension(0,25)));
            
            
            
            JPanel bottomButtonPanel = new JPanel();
            bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
            
            
            bottomButtonPanel.add(clearFieldsButton);
            bottomButtonPanel.add(refreshFieldsButton);
            tfPanel.add(bottomButtonPanel);
            
            tfPanel.add(Box.createVerticalGlue());
            
            mainBox.add(countPanel);
            mainBox.add(tfPanel);
            
            return mainBox;
    	}
    }
    
    class MSRWritePanel extends Component
    {
		private static final long serialVersionUID = -7813746193583966760L;

		public Component make()
    	{
			JPanel mainBox = new JPanel();
			mainBox.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.X_AXIS));
            
            JPanel tracksToWriteBox = new JPanel();
            tracksToWriteBox.setBorder(BorderFactory.createEtchedBorder());
            tracksToWriteBox.setLayout(new BoxLayout(tracksToWriteBox, BoxLayout.Y_AXIS));
            
            write1CB = new JCheckBox("Track 1");
            write2CB = new JCheckBox("Track 2");
            write3CB = new JCheckBox("Track 3");
            write4CB = new JCheckBox("Track 4");
            encodingMaxLengthLabel = new JLabel();
            
            JLabel label = new JLabel("Tracks to Write:");
            tracksToWriteBox.add(label);
            tracksToWriteBox.add(write1CB);
            tracksToWriteBox.add(write2CB);
            tracksToWriteBox.add(write3CB);
            tracksToWriteBox.add(write4CB);
            tracksToWriteBox.add(updateTracksToWriteButton);
            mainBox.add(tracksToWriteBox);
            
            JPanel writeBox = new JPanel();
            writeBox.setBorder(BorderFactory.createEtchedBorder());
            writeBox.setLayout(new BoxLayout(writeBox, BoxLayout.Y_AXIS));
            encodingMaxLengthLabel = new JLabel("Encoding Max Length: ");
            
            dataToWriteTextField = new JTextField();
            dataToWriteTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
            
            writeTracksTimeoutTextField = new JTextField("-1");
            writeTracksTimeoutTextField.setMaximumSize(new Dimension(100, 25));
            
            writeBox.add(encodingMaxLengthLabel);
            writeBox.add(new JLabel("Data To Write:"));
            writeBox.add(dataToWriteTextField);
            writeBox.add(new JLabel("\nTimeout (ms):"));
            writeBox.add(writeTracksTimeoutTextField);
            writeBox.add(writeTracksButton);
            
            mainBox.add(writeBox);
            
            return mainBox;
    	}
    }
}
