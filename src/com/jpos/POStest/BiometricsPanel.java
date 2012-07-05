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
// BiometricsPanel.java - The Biometrics panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class BiometricsPanel extends Component implements StatusUpdateListener, ActionListener {
 
    private static final long serialVersionUID = -3504677363114146010L;

    protected MainButtonPanel mainButtonPanel;
    
    private Biometrics biometrics;
    
    private String defaultLogicalName = "defaultbiometrics";
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public BiometricsPanel() {
        biometrics = new Biometrics();
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
        System.out.println("Biometrics received status update event.");
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
                biometrics.addStatusUpdateListener(this);
                
                biometrics.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("claim")){
            try{
                biometrics.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                biometrics.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                biometrics.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(biometrics.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + biometrics.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(biometrics.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + biometrics.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + biometrics.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + biometrics.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (biometrics.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (biometrics.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                
                // since Biometrics didn't come out until 1.10, no need to check if we support up to 1.10
                msg += "\nCapStatisticsReporting: " + biometrics.getCapStatisticsReporting();                    
                msg += "\nCapUpdateStatistics: " + biometrics.getCapUpdateStatistics();
                
                msg += "\nCapCompareFirmwareVersion: " + biometrics.getCapCompareFirmwareVersion();                    
                msg += "\nCapUpdateFirmware: " + biometrics.getCapUpdateFirmware();
                
                msg += "\n AgorithmList: " + biometrics.getAlgorithmList();
                msg += "\n CapPrematchData: " + biometrics.getCapPrematchData();
                msg += "\n CapRawSensorData: " + biometrics.getCapRawSensorData();
                msg += "\n CapRealTimeData: " + biometrics.getCapRealTimeData();
                msg += "\n CapSensorColor: " + getCapSensorColorString(biometrics.getSensorColor());
                msg += "\n CapSensorOrientation: " + getCapSensorOrientationString(biometrics.getCapSensorOrientation());
                msg += "\n CapSensorType: " + getCapSensorTypeString(biometrics.getCapSensorType());
                msg += "\n CapTemplateAdaptation: " + biometrics.getCapTemplateAdaptation();
                msg += "\n SensorBPP: " + new Integer(biometrics.getSensorBPP()).toString();
                msg += "\n SensorHeight: " + new Integer(biometrics.getSensorHeight()).toString();
                msg += "\n SensorWidth: " + new Integer(biometrics.getSensorWidth()).toString();
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(biometrics);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(biometrics);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try {
            deviceEnabledCB.setSelected(biometrics.getDeviceEnabled());
            freezeEventsCB.setSelected(biometrics.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("BiometricsPanel: MethodListener: JposException");
        }
    }
    
    String getCapSensorColorString(int cap)
    {
        String retval = "";
        if((cap & BiometricsConst.BIO_CSC_MONO) > 0)
        {
            retval += "Bi-tonal ( B/W ), ";
        }
        if((cap & BiometricsConst.BIO_CSC_GRAYSCALE) > 0)
        {
            retval += "Gray scale, ";
        }
        if((cap & BiometricsConst.BIO_CSC_16) > 0)
        {
            retval += "16 Colors, ";
        }
        if((cap & BiometricsConst.BIO_CSC_256) > 0)
        {
            retval += "256 Colors, ";
        }
        if((cap & BiometricsConst.BIO_CSC_FULL) > 0 )
        {
            retval += "Full colors, ";
        }
        return retval;
    }
    
    String getCapSensorOrientationString(int cap)
    {
        String retval;
        switch(cap)
        {
            case BiometricsConst.BIO_CSO_NORMAL:
                retval = "0 deg.";
                break;
            case BiometricsConst.BIO_CSO_RIGHT:
                retval = "90 deg.";
                break;
            case BiometricsConst.BIO_CSO_INVERTED:
                retval = "180 deg.";
                break;
            case BiometricsConst.BIO_CSO_LEFT:
                retval = "270 deg.";
                break;
            default:
                retval = "Driver returned ivalid response";
                break;
        }
        return retval;
    }
    
    String getCapSensorTypeString(int cap)
    {
        String retval = "";
        if((cap & BiometricsConst.BIO_CST_FACIAL_FEATURES) > 0)
        {
            retval += "Facial Features/Topography, ";
        }
        if((cap & BiometricsConst.BIO_CST_VOICE) > 0)
        {
            retval += "Voice, ";
        }
        if((cap & BiometricsConst.BIO_CST_FINGERPRINT) > 0)
        {
            retval += "Fingerprint, ";
        }
        if((cap & BiometricsConst.BIO_CST_IRIS) > 0)
        {
            retval += "Iris, ";
        }
        if((cap & BiometricsConst.BIO_CST_RETINA) > 0)
        {
            retval += "Retina, ";
        }
        if((cap & BiometricsConst.BIO_CST_HAND_GEOMETRY) > 0)
        {
            retval += "Hand Geometry, ";
        }
        if((cap & BiometricsConst.BIO_CST_SIGNATURE_DYNAMICS) > 0)
        {
            retval += "Signature, ";
        }
        if((cap & BiometricsConst.BIO_CST_KEYSTOKE_DYNAMICS) > 0)
        {
            retval += "Keystrokes, ";
        }
        if((cap & BiometricsConst.BIO_CST_LIP_MOVEMENT) > 0)
        { 
            retval += "Lip Movement, ";
        }
        if((cap & BiometricsConst.BIO_CST_THERMAL_FACE_IMAGE) > 0)
        {
            retval += "Face Image, ";
        }
        if((cap & BiometricsConst.BIO_CST_THERMAL_HAND_IMAGE) > 0)
        {
            retval += "Hand Image, ";
        }
        if((cap & BiometricsConst.BIO_CST_GAIT) > 0)
        {
            retval += "Gait/Stride, ";
        }
        if((cap & BiometricsConst.BIO_CST_PASSWORD) > 0)
        {
            retval += "Password, ";
        }
        return retval;
    }
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        biometrics.setDeviceEnabled(false);
                    }else{
                        biometrics.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        biometrics.setFreezeEvents(false);
                    }else{
                        biometrics.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("BiometricsPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(biometrics != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(biometrics.getState()));
            }
        }
    }
}
