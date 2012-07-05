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
// SigpadPanel.java - The Signature Capture panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class SigpadPanel extends Component implements DataListener, ActionListener {

	private static final long serialVersionUID = -5375294458678884018L;

	protected MainButtonPanel mainButtonPanel;
    
    private SignatureCapture sigpad;
    
    private String defaultLogicalName = "defaultSigcap";
    
    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JButton beginCaptureButton;
    private JButton endCaptureButton;
    
    private DrawPanel drawPanel;
    private JTextField formNameTextField;
    
    private JButton clearInputButton;
    private JButton clearInputPropertiesButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    Runnable doDataUpdate;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public SigpadPanel(){
        sigpad = new SignatureCapture();
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
        beginCaptureButton = new JButton("Begin Capture");
        beginCaptureButton.setActionCommand("beginCapture");
        beginCaptureButton.addActionListener(this);
        beginCaptureButton.setEnabled(false);
        buttonPanel.add(beginCaptureButton);
        
        formNameTextField = new JTextField(12);
        buttonPanel.add(formNameTextField);
        
        endCaptureButton = new JButton("End Capture");
        endCaptureButton.setActionCommand("endCapture");
        endCaptureButton.addActionListener(this);
        endCaptureButton.setEnabled(false);
        buttonPanel.add(endCaptureButton);
        
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
        
        autoDisableCB.setEnabled(false);
        dataEventEnabledCB.setEnabled(false);
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        JPanel dataPanel = new JPanel();
        
        drawPanel = new DrawPanel();
        
        dataPanel.add(drawPanel);
        subPanel.add(dataPanel);
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
                sigpad.addDataListener(this);
                
                sigpad.open(logicalName);
                autoDisableCB.setEnabled(true);
                //    dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = sigpad.getDeviceServiceVersion();
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
                sigpad.claim(0);
                beginCaptureButton.setEnabled(true);
                autoDisableCB.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                sigpad.release();
                beginCaptureButton.setEnabled(false);
                endCaptureButton.setEnabled(false);
                autoDisableCB.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                sigpad.close();
                autoDisableCB.setEnabled(false);
                dataEventEnabledCB.setEnabled(false);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("beginCapture")){
            try{
                sigpad.beginCapture(formNameTextField.getText());
            }catch(JposException e){
                System.err.println("Jpos exception " + e);
            }
            beginCaptureButton.setEnabled(false);
            endCaptureButton.setEnabled(true);
        } else if(ae.getActionCommand().equals("endCapture")){
            try{
                sigpad.endCapture();
                updateData();
            }catch(JposException e){
                System.err.println("Jpos exception " + e);
            }
            beginCaptureButton.setEnabled(true);
            endCaptureButton.setEnabled(false);
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(sigpad.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + sigpad.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(sigpad.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + sigpad.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + sigpad.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + sigpad.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (sigpad.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (sigpad.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + sigpad.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + sigpad.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + sigpad.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + sigpad.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapDisplay: " + sigpad.getCapDisplay();
                msg += "\nCapRealTimeData: " + sigpad.getCapRealTimeData();
                msg += "\nCapUserTerminated: " + sigpad.getCapUserTerminated();
                
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
                sigpad.addDataListener(this);
                sigpad.open(logicalName);
                sigpad.claim(0);
                beginCaptureButton.setEnabled(true);
                autoDisableCB.setEnabled(true);
                dataEventEnabledCB.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                sigpad.setDeviceEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(sigpad);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(sigpad);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
            	sigpad.clearInput();
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
            	sigpad.clearInputProperties();
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
            autoDisableCB.setSelected(sigpad.getAutoDisable());
            dataEventEnabledCB.setSelected(sigpad.getDataEventEnabled());
            deviceEnabledCB.setSelected(sigpad.getDeviceEnabled());
            freezeEventsCB.setSelected(sigpad.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("SigpadPanel: MethodListener: JposException");
        }
    }
    
    public void updateData() {
        try {
            Point[] p = sigpad.getPointArray();
            drawPanel.setPoints(p);
            drawPanel.repaint();
            
            sigpad.setDataEventEnabled(true);
        } catch(JposException je) {
            System.err.println("SigpadPanel: dataOccurred 1: Jpos Exception");
        }
        try {
            autoDisableCB.setSelected(sigpad.getAutoDisable());
            dataEventEnabledCB.setSelected(sigpad.getDataEventEnabled());
            deviceEnabledCB.setSelected(sigpad.getDeviceEnabled());
            freezeEventsCB.setSelected(sigpad.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("SigpadPanel: dataOccurred 2: JposException");
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
                if (source == autoDisableCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        sigpad.setAutoDisable(false);
                    }else{
                        sigpad.setAutoDisable(true);
                    }
                }else if (source == dataEventEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        sigpad.setDataEventEnabled(false);
                    }else{
                        sigpad.setDataEventEnabled(true);
                    }
                }else if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        sigpad.setDeviceEnabled(false);
                    }else{
                        sigpad.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        sigpad.setFreezeEvents(false);
                    }else{
                        sigpad.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("SigpadPanel: CheckBoxListener: Jpos Exception" + source);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(sigpad != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(sigpad.getState()));
            }
        }
    }
}
