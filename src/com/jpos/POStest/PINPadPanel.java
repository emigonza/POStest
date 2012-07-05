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
// PINPadPanel.java - The PINPad panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class PINPadPanel extends Component implements DataListener, ActionListener {
    
	private static final long serialVersionUID = -5872374527899063970L;

	protected MainButtonPanel mainButtonPanel;
    
    private PINPad pinpad;
    
    private String defaultLogicalName = "defaultPINPad";
    
    protected JTextField accountNumberTextField;
    protected JTextField amountTextField;
    protected JTextField merchantIDTextField;
    protected JTextField terminalIDTextField;
    protected JTextField track1DataTextField;
    protected JTextField track2DataTextField;
    protected JTextField track3DataTextField;
    protected JTextField track4DataTextField;
    protected JTextField transactionTypeTextField;
    protected JTextField pinPadSystemTextField;
    protected JTextField transactionHostTextField;
    protected JTextField completionCodeTextField;
    protected JTextField keyNumTextField;
    protected JTextField keyTextField;
    protected JTextField encryptedPINTextField;
    
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JButton beginEFTTransactionButton;
    private JButton endEFTTransactionButton;
    private JButton updateKeyButton;
    
    private JButton clearInputButton;
    private JButton clearInputPropertiesButton;
    private JButton clearFieldsButton;
    private JButton refreshFieldsButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    Runnable doDataUpdate;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public PINPadPanel() {
        pinpad = new PINPad();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    
    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //        MethodListener methodListener = new MethodListener();
        
        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
        JPanel buttonPanel = new JPanel();
        beginEFTTransactionButton = new JButton("Begin EFT Transaction");
        beginEFTTransactionButton.setActionCommand("beginEFTTransaction");
        beginEFTTransactionButton.addActionListener(this);
        beginEFTTransactionButton.setEnabled(false);
        buttonPanel.add(beginEFTTransactionButton);
        
        endEFTTransactionButton = new JButton("End EFT Transaction");
        endEFTTransactionButton.setActionCommand("endEFTTransaction");
        endEFTTransactionButton.addActionListener(this);
        endEFTTransactionButton.setEnabled(false);
        buttonPanel.add(endEFTTransactionButton);
        
        updateKeyButton = new JButton("Update Key");
        updateKeyButton.setActionCommand("updateKey");
        updateKeyButton.addActionListener(this);
        updateKeyButton.setEnabled(false);
        buttonPanel.add(updateKeyButton);
        
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        
        mainPanel.add(buttonPanel);
        
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        
        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        dataEventEnabledCB = new JCheckBox("Data event enabled");
        Font f = dataEventEnabledCB.getFont();
        Font newf = new Font(f.getName(),Font.PLAIN,f.getSize());
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
        
        dataEventEnabledCB.setEnabled(false);
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        int dim = 4;
        
        JLabel label = new JLabel("Account number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Amount:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Merchant ID:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("TerminalID:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Track 1 Data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Track 2 Data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Track 3 Data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Track 4 Data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Transaction Type:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("PINPad System:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Transaction host:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Completion code:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Key number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Key:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        label = new JLabel("Encrypted PIN:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,dim)));
        
        labelPanel.add(Box.createVerticalGlue());
        
        subPanel.add(labelPanel);
        
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        
        
        accountNumberTextField = new JTextField(50);
        accountNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(accountNumberTextField);
        
        amountTextField = new JTextField(50);
        amountTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(amountTextField);
        
        merchantIDTextField = new JTextField(50);
        merchantIDTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(merchantIDTextField);
        
        terminalIDTextField = new JTextField(50);
        terminalIDTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(terminalIDTextField);
        
        track1DataTextField = new JTextField(50);
        track1DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(track1DataTextField);
        
        track2DataTextField = new JTextField(50);
        track2DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(track2DataTextField);
        
        track3DataTextField = new JTextField(50);
        track3DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(track3DataTextField);
        
        track4DataTextField = new JTextField(50);
        track4DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(track4DataTextField);
        
        transactionTypeTextField = new JTextField(50);
        transactionTypeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(transactionTypeTextField);
        
        pinPadSystemTextField = new JTextField(50);
        pinPadSystemTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(pinPadSystemTextField);
        
        transactionHostTextField = new JTextField(50);
        transactionHostTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(transactionHostTextField);
        
        completionCodeTextField = new JTextField(50);
        completionCodeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(completionCodeTextField);
        
        keyNumTextField = new JTextField(50);
        keyNumTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(keyNumTextField);
        
        keyTextField = new JTextField(50);
        keyTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(keyTextField);
        
        encryptedPINTextField = new JTextField(50);
        encryptedPINTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fieldPanel.add(encryptedPINTextField);
        
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        
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
        
        bottomButtonPanel.add(clearFieldsButton);
        bottomButtonPanel.add(refreshFieldsButton);
        fieldPanel.add(bottomButtonPanel);
        
        fieldPanel.add(Box.createVerticalGlue());
        
        subPanel.add(fieldPanel);
        subPanel.add(Box.createHorizontalGlue());
        
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        doDataUpdate = new Runnable() {
            public void run() {
                updateData();
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
                pinpad.addDataListener(this);
                
                pinpad.open(logicalName);
                //    dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = pinpad.getDeviceServiceVersion();
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
                pinpad.claim(0);
                beginEFTTransactionButton.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                pinpad.release();
                beginEFTTransactionButton.setEnabled(false);
                endEFTTransactionButton.setEnabled(false);
                updateKeyButton.setEnabled(false);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                pinpad.close();
                dataEventEnabledCB.setEnabled(false);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("beginEFTTransaction")){
            try{
                pinpad.setAccountNumber(accountNumberTextField.getText());
                pinpad.setAmount(Long.parseLong(amountTextField.getText()));
                pinpad.setMerchantID(merchantIDTextField.getText());
                pinpad.setTerminalID(terminalIDTextField.getText());
                pinpad.setTrack1Data(track1DataTextField.getText().getBytes());
                pinpad.setTrack2Data(track2DataTextField.getText().getBytes());
                pinpad.setTrack3Data(track3DataTextField.getText().getBytes());
                pinpad.setTransactionType(Integer.parseInt(transactionTypeTextField.getText()));
                pinpad.beginEFTTransaction(pinPadSystemTextField.getText(),Integer.parseInt(transactionHostTextField.getText()));
                
            }catch(JposException e){
                System.err.println("beginEFTTransaction: Jpos exception " + e);
            }
            beginEFTTransactionButton.setEnabled(false);
            endEFTTransactionButton.setEnabled(true);
        } else if(ae.getActionCommand().equals("endEFTTransaction")){
            try{
                pinpad.endEFTTransaction(Integer.parseInt(completionCodeTextField.getText()));
            }catch(JposException e){
                System.err.println("Jpos exception " + e);
            }
            beginEFTTransactionButton.setEnabled(true);
            endEFTTransactionButton.setEnabled(false);
        } else if(ae.getActionCommand().equals("updateKey")){
            try{
                pinpad.updateKey(Integer.parseInt(keyNumTextField.getText()),keyTextField.getText());
            }catch(JposException e){
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(pinpad.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + pinpad.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(pinpad.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + pinpad.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + pinpad.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + pinpad.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (pinpad.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (pinpad.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + pinpad.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + pinpad.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + pinpad.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + pinpad.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapDisplay: ";
                int disp = pinpad.getCapDisplay();
                switch(disp){
                    case PINPadConst.PPAD_DISP_UNRESTRICTED:
                        msg += "PPAD_DISP_UNRESTRICTED";
                        break;
                    case PINPadConst.PPAD_DISP_PINRESTRICTED:
                        msg += "PPAD_DISP_PINRESTRICTED";
                        break;
                    case PINPadConst.PPAD_DISP_RESTRICTED_LIST:
                        msg += "PPAD_DISP_RESTRICTED_LIST";
                        break;
                    case PINPadConst.PPAD_DISP_RESTRICTED_ORDER:
                        msg += "PPAD_DISP_RESTRICTED_ORDER";
                        break;
                    case PINPadConst.PPAD_DISP_NONE:
                        msg += "PPAD_DISP_NONE";
                        break;
                }
                
                msg += "\nCapKeyboard: " + pinpad.getCapKeyboard();
                msg += "\nCapLanguage: ";
                int lang = pinpad.getCapLanguage();
                switch(lang){
                    case PINPadConst.PPAD_LANG_NONE:
                        msg += "PPAD_LANG_NONE";
                        break;
                    case PINPadConst.PPAD_LANG_ONE:
                        msg += "PPAD_LANG_ONE";
                        break;
                    case PINPadConst.PPAD_LANG_PINRESTRICTED:
                        msg += "PPAD_LANG_PINRESTRICTED";
                        break;
                        //This is wrong! the const should be RESCTICTED_ORDER not UNRESCRICTED
                    case PINPadConst. PPAD_LANG_UNRESTRICTED:
                        msg += "PPAD_LANG_UNRESTRICTED / RESTRICTED_ORDER";
                        break;
                }
                
                msg += "\nCapMACCalulation: " + pinpad.getCapMACCalculation();
                msg += "\nCapTone: " + pinpad.getCapTone();
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("oce")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                pinpad.addDataListener(this);
                pinpad.open(logicalName);
                pinpad.claim(0);
                beginEFTTransactionButton.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                pinpad.setDeviceEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(pinpad);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(pinpad);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
            	pinpad.clearInput();
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
            	pinpad.clearInputProperties();
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
	        	accountNumberTextField.setText(pinpad.getAccountNumber());
	        	amountTextField.setText(String.valueOf(pinpad.getAmount()));
	        	merchantIDTextField.setText(pinpad.getMerchantID());
	        	terminalIDTextField.setText(pinpad.getTerminalID());
	        	track1DataTextField.setText(pinpad.getTrack1Data().toString());
	        	track2DataTextField.setText(pinpad.getTrack2Data().toString());
	        	track3DataTextField.setText(pinpad.getTrack3Data().toString());
	        	track4DataTextField.setText(pinpad.getTrack4Data().toString());
	        	transactionTypeTextField.setText(String.valueOf(pinpad.getTransactionType()));
	            encryptedPINTextField.setText(pinpad.getEncryptedPIN());
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
        
        try {
            dataEventEnabledCB.setSelected(pinpad.getDataEventEnabled());
            deviceEnabledCB.setSelected(pinpad.getDeviceEnabled());
            freezeEventsCB.setSelected(pinpad.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("PINPadPanel: MethodListener: JposException");
        }
    }
    
    public void updateData() {
        try {
            encryptedPINTextField.setText(new String(pinpad.getEncryptedPIN()));
            pinpad.setDataEventEnabled(true);
        } catch(JposException je) {
            System.err.println("PINPadPanel: dataOccurred 1: Jpos Exception");
        }
        try {
            dataEventEnabledCB.setSelected(pinpad.getDataEventEnabled());
            deviceEnabledCB.setSelected(pinpad.getDeviceEnabled());
            freezeEventsCB.setSelected(pinpad.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("PINPadPanel: dataOccurred 2: JposException");
        }
        
    }
    
    
    public void dataOccurred(DataEvent dataEvent) {
        try {
            SwingUtilities.invokeLater(doDataUpdate);
        } catch(Exception e) {
            System.err.println("InvokeLater exception.");
        }
    }
    
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == dataEventEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        pinpad.setDataEventEnabled(false);
                    }else{
                        pinpad.setDataEventEnabled(true);
                    }
                }else if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        pinpad.setDeviceEnabled(false);
                    }else{
                        pinpad.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        pinpad.setFreezeEvents(false);
                    }else{
                        pinpad.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("PINPadPanel: CheckBoxListener: Jpos Exception" + source);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(pinpad != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(pinpad.getState()));
            }
        }
    }
}
