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
// CheckScannerPanel.java - The CheckScanner panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class CheckScannerPanel extends Component implements StatusUpdateListener, ActionListener {

	private static final long serialVersionUID = 1618429583773178897L;

	protected MainButtonPanel mainButtonPanel;
    
    private CheckScanner checkScanner;
    
    private String defaultLogicalName = "defaultCheckScanner";
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JButton beginInsertionButton;
    private JButton endInsertionButton;
    private JButton beginRemovalButton;
    private JButton endRemovalButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public CheckScannerPanel() {
        checkScanner = new CheckScanner();
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
        
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        
        mainPanel.add(buttonPanel);
        
        
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
        subPanel.add(propPanel);
        
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Not yet implemented.");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(label);
        statusPanel.add(Box.createHorizontalGlue());
        
        
        subPanel.add(statusPanel);
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        System.out.println("CheckScanner received status update event.");
    }
    
    
    /** Listens to the method buttons. */
    
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                checkScanner.addStatusUpdateListener(this);
                
                checkScanner.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = checkScanner.getDeviceServiceVersion();
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
                checkScanner.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                beginInsertionButton.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                checkScanner.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                checkScanner.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(checkScanner.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + checkScanner.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(checkScanner.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + checkScanner.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + checkScanner.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + checkScanner.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (checkScanner.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (checkScanner.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + checkScanner.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + checkScanner.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + checkScanner.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + checkScanner.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapAutoGenerateFileID: " + checkScanner.getCapAutoGenerateFileID();
                msg += "\nCapAutoGenerateImageTagData: " + checkScanner.getCapAutoGenerateImageTagData();
                msg += "\nCapAutoSize: " + checkScanner.getCapAutoSize();
                msg += "\nCapColor: ";
                int color = checkScanner.getCapColor();
                if((color & CheckScannerConst.CHK_CCL_MONO) > 0){
                    msg += "CHK_CCL_MONO | ";
                }
                if((color & CheckScannerConst.CHK_CCL_GRAYSCALE) > 0){
                    msg += "CHK_CCL_GRAYSCALE | ";
                }
                if((color & CheckScannerConst.CHK_CCL_16) > 0){
                    msg += "CHK_CCL_16 | ";
                }
                if((color & CheckScannerConst.CHK_CCL_256) > 0){
                    msg += "CHK_CCL_256 | ";
                }
                if((color & CheckScannerConst.CHK_CCL_FULL) > 0){
                    msg += "CHK_CCL_FULL";
                }
                
                msg += "\nCapConcurrentMICR: " + checkScanner.getCapConcurrentMICR();
                msg += "\nCapDefineCropArea: " + checkScanner.getCapDefineCropArea();
                msg += "\nCapImageFormat: ";
                int format = checkScanner.getCapImageFormat();
                if((format & CheckScannerConst.CHK_CIF_NATIVE) > 0){
                    msg += "Bi-CHK_CIF_NATIVE | ";
                }
                if((format & CheckScannerConst.CHK_CIF_TIFF) > 0){
                    msg += "Bi-CHK_CIF_TIFF | ";
                }
                if((format & CheckScannerConst.CHK_CIF_BMP) > 0){
                    msg += "Bi-CHK_CIF_BMP | ";
                }
                if((format & CheckScannerConst.CHK_CIF_JPEG) > 0){
                    msg += "Bi-CHK_CIF_JPEG | ";
                }
                if((format & CheckScannerConst.CHK_CIF_GIF) > 0){
                    msg += "Bi-CHK_CIF_GIF";
                }
                
                msg += "\nCapImageTagData: " + checkScanner.getCapImageTagData();
                msg += "\nCapMICRDevice: " + checkScanner.getCapMICRDevice();
                msg += "\nCapStoreImageFiles: " + checkScanner.getCapStoreImageFiles();
                msg += "\nCapValidationDevice: " + checkScanner.getCapValidationDevice();
                
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("beginInsertion")){
            try{
                checkScanner.beginInsertion(-1);
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(true);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in beginInsertion: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("endInsertion")){
            try{
                checkScanner.endInsertion();
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(true);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in endInsertion: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("beginRemoval")){
            try{
                checkScanner.beginRemoval(-1);
                beginInsertionButton.setEnabled(false);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in beginRemoval: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("endRemoval")){
            try{
                checkScanner.endRemoval();
                beginInsertionButton.setEnabled(true);
                endInsertionButton.setEnabled(false);
                beginRemovalButton.setEnabled(false);
                endRemovalButton.setEnabled(false);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in endRemoval: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(checkScanner);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(checkScanner);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            if(checkScanner.getState() != JposConst.JPOS_S_CLOSED){
                if(checkScanner.getClaimed()){
                    deviceEnabledCB.setSelected(checkScanner.getDeviceEnabled());
                }
                freezeEventsCB.setSelected(checkScanner.getFreezeEvents());
            }
        } catch(JposException je) {
            JOptionPane.showMessageDialog(null, "Exception in Method Panel:\n "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        checkScanner.setDeviceEnabled(false);
                    }else{
                        checkScanner.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        checkScanner.setFreezeEvents(false);
                    }else{
                        checkScanner.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("CheckScannerPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(checkScanner != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(checkScanner.getState()));
            }
        }
    }
}
