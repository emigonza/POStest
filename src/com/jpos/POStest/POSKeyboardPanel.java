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
// POSKeyboardPanel.java - The POSKeyboard panel of POStest
//
//------------------------------------------------------------------------------
// final framework completed 7-14-2004
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class POSKeyboardPanel extends Component implements StatusUpdateListener,
        ActionListener,
        DataListener {

	private static final long serialVersionUID = -8923742982697493857L;

	protected MainButtonPanel mainButtonPanel;
    
    private POSKeyboard posKeyboard;
    
    private String defaultLogicalName = "defaultPOSKeyboard";
    private JLabel keyLabel;
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox dataEventEnableCB;
    private JCheckBox autoDisableCB;
    
    private JCheckBox keyUpEvents;
    
    private JButton clearInputButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
//    private boolean ver_17_complient = false;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public POSKeyboardPanel() {
        posKeyboard = new POSKeyboard();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        
        mainPanel.add(buttonPanel);
        
        
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        
        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        
        autoDisableCB = new JCheckBox("Auto Disable");
        propPanel.add(autoDisableCB);
        
        deviceEnabledCB = new JCheckBox("Device enabled");
        propPanel.add(deviceEnabledCB);
        
        dataEventEnableCB = new JCheckBox("Data Events Enabled");
        propPanel.add(dataEventEnableCB);
        
        freezeEventsCB = new JCheckBox("Freeze events");
        propPanel.add(freezeEventsCB);
        
        keyUpEvents = new JCheckBox("Generate Key Up Events");
        propPanel.add(keyUpEvents);
        
        clearInputButton = new JButton("Clear Input");
        clearInputButton.setActionCommand("clearInput");
        clearInputButton.addActionListener(this);
        propPanel.add(clearInputButton);
        
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
        
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        keyUpEvents.setEnabled(false);
        autoDisableCB.setEnabled(false);
        dataEventEnableCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        keyUpEvents.addItemListener(cbListener);
        dataEventEnableCB.addItemListener(cbListener);
        autoDisableCB.addItemListener(cbListener);
        
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        
        // JPanel labelPanel = new JPanel();
        // labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        // JLabel label = new JLabel("Last POS key pressed:  ");
        // label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        
        
        // label.setFont(newf);
        // labelPanel.add(label);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        
        keyLabel = new JLabel(" ");
        keyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Font f = keyLabel.getFont();
        Font newf = new Font( f.getName(),Font.BOLD,20);
        
        keyLabel.setFont(newf);
        
        JLabel label = new JLabel("    Press a POS key.    ");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(newf);
        
        valuePanel.add(label);
        valuePanel.add(keyLabel);
        
        // statusPanel.add(labelPanel);
        statusPanel.add(Box.createRigidArea(new Dimension(150,5)));
        statusPanel.add(valuePanel);
        
        subPanel.add(statusPanel);
        subPanel.add(Box.createHorizontalGlue());
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        
        JOptionPane.showMessageDialog(null, "POSKeyboard got a Status Update Event: "+ sue, "SUE", JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    public void dataOccurred(DataEvent de) {
        try{
            String type =posKeyboard.getPOSKeyEventType() == POSKeyboardConst.KBD_ET_DOWN ? " Pressed " : " Released ";
            keyLabel.setText("POS key " + Integer.toString(posKeyboard.getPOSKeyData()) + type);
            updateGUI();
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
                posKeyboard.addStatusUpdateListener(this);
                posKeyboard.addDataListener(this);
                
                posKeyboard.open(logicalName);
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                dataEventEnableCB.setEnabled(true);
                autoDisableCB.setEnabled(true);
                
                keyUpEvents.setEnabled(posKeyboard.getCapKeyUp());
                int version = posKeyboard.getDeviceServiceVersion();
                if(version >= 1009000) {
                    ver_19_complient = true;
                    ver_18_complient = true;
//                    ver_17_complient = true;
                }
                if(version >= 1008000) {
                    ver_18_complient = true;
//                    ver_17_complient = true;
                }
//                if(version >= 1007000) {
//                    ver_17_complient = true;
//                }
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("claim")){
            try{
                posKeyboard.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                posKeyboard.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                posKeyboard.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                dataEventEnableCB.setEnabled(false);
                keyUpEvents.setEnabled(false);
                autoDisableCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(posKeyboard.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + posKeyboard.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(posKeyboard.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + posKeyboard.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + posKeyboard.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + posKeyboard.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (posKeyboard.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (posKeyboard.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + posKeyboard.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + posKeyboard.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + posKeyboard.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + posKeyboard.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                msg += "\nCapKeyUp: " + posKeyboard.getCapKeyUp();
                
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
                posKeyboard.addStatusUpdateListener(this);
                posKeyboard.addDataListener(this);
                
                posKeyboard.open(logicalName);
                posKeyboard.claim(1000);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                dataEventEnableCB.setEnabled(true);
                autoDisableCB.setEnabled(true);
                
                keyUpEvents.setEnabled(posKeyboard.getCapKeyUp());
                
                posKeyboard.setDeviceEnabled(true);
                posKeyboard.setDataEventEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(posKeyboard);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(posKeyboard);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
            	posKeyboard.clearInput();
            }
            catch(JposException je)
            {
                JOptionPane.showMessageDialog(null, "clearInput threw a JposException: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Unknown Action event recieved, someone forgot to implement something.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        //only do this if the device is not closed
        if(posKeyboard.getState() != JposConst.JPOS_S_CLOSED){
            updateGUI();
            
        }
        
    }
    
    private void updateGUI() {
        try {
            deviceEnabledCB.setSelected(posKeyboard.getDeviceEnabled());
            freezeEventsCB.setSelected(posKeyboard.getFreezeEvents());
            keyUpEvents.setSelected(posKeyboard.getEventTypes() == POSKeyboardConst.KBD_ET_DOWN_UP);
            dataEventEnableCB.setSelected(posKeyboard.getDataEventEnabled());
            autoDisableCB.setSelected(posKeyboard.getAutoDisable());
        } catch(JposException je) {
            JOptionPane.showMessageDialog(null, "Exception in updateGUI\nException: "+ je.getMessage(),
                    "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        posKeyboard.setDeviceEnabled(false);
                    }else{
                        posKeyboard.setDeviceEnabled(true);
                    }
                } else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        posKeyboard.setFreezeEvents(false);
                    }else{
                        posKeyboard.setFreezeEvents(true);
                    }
                } else if (source == keyUpEvents){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        posKeyboard.setEventTypes(POSKeyboardConst.KBD_ET_DOWN);
                    }else{
                        posKeyboard.setEventTypes(POSKeyboardConst.KBD_ET_DOWN_UP);
                    }
                } else if (source == dataEventEnableCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        posKeyboard.setDataEventEnabled(false);
                    }else{
                        posKeyboard.setDataEventEnabled(true);
                    }
                } else if (source == autoDisableCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        posKeyboard.setAutoDisable(false);
                    }else{
                        posKeyboard.setAutoDisable(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("POSKeyboardPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(posKeyboard != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(posKeyboard.getState()));
            }
        }
    }
}
