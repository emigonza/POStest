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
// KeylockPanel.java - The Keylock panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class KeylockPanel extends Component implements StatusUpdateListener, ActionListener {
    
	private static final long serialVersionUID = -4978509274426826670L;

	protected MainButtonPanel mainButtonPanel;
    
    private Keylock keylock;
    
    private String defaultLogicalName = "defaultKeylock";
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JLabel numPosLabel;
    private JLabel currentPosLabel;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    public KeylockPanel() {
        keylock = new Keylock();
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
        subPanel.add(propPanel);
        
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Number of positions: ");
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        Font f = label.getFont();
        Font newf = new Font( f.getName(),Font.BOLD,20);
        
        label.setFont(newf);
        labelPanel.add(label);
        
        label = new JLabel("Current position: ");
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setFont(newf);
        
        labelPanel.add(label);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        
        numPosLabel = new JLabel(" ");
        numPosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        currentPosLabel = new JLabel(" ");
        currentPosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        numPosLabel.setFont(newf);
        currentPosLabel.setFont(newf);
        
        valuePanel.add(numPosLabel);
        valuePanel.add(currentPosLabel);
        
        statusPanel.add(labelPanel);
        statusPanel.add(valuePanel);
        
        subPanel.add(statusPanel);
        subPanel.add(Box.createHorizontalGlue());
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        refreshKeyPos();
    }
    
    private void refreshKeyPos(){
        try{
            String label = "";
            switch(keylock.getKeyPosition()){
                case 1:
                    label =" ( LOCK )";
                    break;
                case 2:
                    label = " ( NORM )";
                    break;
                case 3:
                    label = " ( SUPR )";
                    break;
            }
            currentPosLabel.setText(Integer.toString(keylock.getKeyPosition()) + label);
        }catch (JposException e){
            JOptionPane.showMessageDialog(null, "Exception in getKeyPosition(): "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            System.err.println("Jpos exception " + e);
        }
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
                keylock.addStatusUpdateListener(this);
                
                keylock.open(logicalName);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                numPosLabel.setText(Integer.toString(keylock.getPositionCount()));
                int version = keylock.getDeviceServiceVersion();
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
                //this should always fail
                keylock.claim(0);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                // this should always fail
                keylock.release();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                keylock.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                numPosLabel.setText(" ");
                currentPosLabel.setText(" ");
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(keylock.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + keylock.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(keylock.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + keylock.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + keylock.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + keylock.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (keylock.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (keylock.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + keylock.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + keylock.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + keylock.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + keylock.getCapUpdateFirmware();
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
        }
        if(ae.getActionCommand().equals("oce")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                keylock.addStatusUpdateListener(this);
                
                keylock.open(logicalName);
                keylock.claim(0);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                keylock.setDeviceEnabled(true);
                numPosLabel.setText(Integer.toString(keylock.getPositionCount()));
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(keylock);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(keylock);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            //only do this if the device is not closed
            if(keylock.getState() != JposConst.JPOS_S_CLOSED){
                deviceEnabledCB.setSelected(keylock.getDeviceEnabled());
                freezeEventsCB.setSelected(keylock.getFreezeEvents());
            }
        } catch(JposException je) {
            System.err.println("KeylockPanel: MethodListener: JposException");
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        keylock.setDeviceEnabled(false);
                    }else{
                        keylock.setDeviceEnabled(true);
                        refreshKeyPos();
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        keylock.setFreezeEvents(false);
                    }else{
                        keylock.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("KeylockPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(keylock != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(keylock.getState()));
            }
        }
    }
}
