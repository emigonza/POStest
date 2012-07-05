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
// ScalePanel.java - The Scale panel of POStest
//
//------------------------------------------------------------------------------
// contribution of interface/implementation    Rory K. Shaw/Raleigh/IBM   7/7/04
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


public class ScalePanel extends Component implements StatusUpdateListener,
        ActionListener {
    
	private static final long serialVersionUID = -4174319101156849494L;

	protected MainButtonPanel mainButtonPanel;
    
    private Scale scale;
    
    private String defaultLogicalName = "defaultScale";
    private String logicalName = "";
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    boolean scaleOpened = false;
    
    private JButton scaleReadWeightButton = new JButton("Read Weight");
    private JTextField scaleCurrWeight = new JTextField(10);
    private JCheckBox scaleCap = new JCheckBox("Cap Display");
    private JLabel weightUnitsb = new  JLabel("Weight Units");
    private JTextField weightUnits = new JTextField(12);
    private JLabel maxWeightb = new JLabel("Maximum Weight");
    private JTextField maxWeight = new JTextField(10);
    private JButton scaleDisplayTextButton = new JButton("Display Text");
    private JButton scaleZeroScaleButton = new JButton("Zero Scale");
    
    private JButton clearInputButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public ScalePanel() {
        scale = new Scale();
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
        
        
        deviceEnabledCB = new JCheckBox("Device enabled");
        propPanel.add(deviceEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        propPanel.add(freezeEventsCB);
        
        clearInputButton = new JButton("Clear Input");
        clearInputButton.setActionCommand("clearInput");
        clearInputButton.addActionListener(this);
        propPanel.add(clearInputButton);
        
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
        
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        
        
        /*
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Not yet implemented.");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(label);
        statusPanel.add(Box.createHorizontalGlue());
        subPanel.add(statusPanel);
         */
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        JPanel scalePanel = new JPanel();
        scalePanel.setLayout(new FlowLayout());
        
        JPanel scalemethods = new JPanel();
        scalemethods.setLayout(new GridLayout(3,2,3,4));
        //scalemethods.setLayout(new FlowLayout());
        scalemethods.setBorder(new TitledBorder("Method"));
        
        scalemethods.add(scaleReadWeightButton);
        scaleReadWeightButton.setToolTipText("Read Weight on Scale");
        scalemethods.add(scaleCurrWeight);
        scaleCurrWeight.setToolTipText("Displays Weight in lbs");
        scalemethods.add(scaleDisplayTextButton);
        scaleDisplayTextButton.setToolTipText("Display String of Data");
        scalemethods.add(scaleZeroScaleButton);
        scaleZeroScaleButton.setToolTipText("Zero Out Scale");
        scalePanel.add(scalemethods);
        
        
        JPanel scaleprops = new JPanel();
        scaleprops.setLayout(new GridLayout(3,2,3,4));
        scaleprops.setBorder(new TitledBorder("Read-Only Properties"));
        scalePanel.add(scaleprops);
        
        scaleprops.add(scaleCap);
        scaleprops.add(new JLabel(" "));
        scaleprops.add(maxWeightb);
        scaleprops.add(maxWeight);
        scaleprops.add(weightUnitsb);
        scaleprops.add(weightUnits);
        
        JScrollPane scaleScroll = new JScrollPane();
        scaleScroll.setViewportView(scalePanel);
        
        scaleReadWeightButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    int weight[] = new int[1];
                    scale.readWeight(weight,3000);
                    scaleCurrWeight.setText(String.valueOf(weight[0]));
                    System.out.println("Read Weight Button pressed!\n");
                } catch( JposException e ) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null,"Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });
        
        scaleDisplayTextButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    String text = "";
                    scale.displayText(text);
                    System.out.println("Display Text Button pressed!\n");
                } catch( JposException e ) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null,"Failed to open \"" + logicalName
                            + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });
        
        scaleZeroScaleButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    scale.zeroScale();
                    System.out.println("Zero Scale Button pressed!\n");
                } catch( JposException e ) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null,"Failed to open \"" + logicalName
                            + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });
        
        subPanel.add(scalePanel);
        return mainPanel;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        System.out.println("Scale received status update event.");
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
                scale.addStatusUpdateListener(this);
                scale.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = scale.getDeviceServiceVersion();
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
                scale.claim(0);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                scale.release();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                scale.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(scale.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + scale.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(scale.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + scale.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + scale.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + scale.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (scale.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (scale.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + scale.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + scale.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + scale.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + scale.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapDisplay: " + scale.getCapDisplay();
                msg += "\nCapDisplayText: " + scale.getCapDisplayText();
                msg += "\nCapPriceCalculating: " + scale.getCapPriceCalculating();
                msg += "\nCapTareWeight: " + scale.getCapTareWeight();
                msg += "\nCapZeroScale: " + scale.getCapZeroScale();
                if(ver_19_complient)
                {
                	msg += "\nCapStatusUpdate: " + scale.getCapStatusUpdate();
                }
                else
                {
                	msg += "\nCapStatusUpdate: Service Object is not 1.9 complient";
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
                scale.addStatusUpdateListener(this);
                scale.open(logicalName);
                scale.claim(0);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                scale.setDeviceEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(scale);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(scale);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(ae.getActionCommand().equals("clearInput"))
        {
            try
            {
            	scale.clearInput();
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
        try {
            deviceEnabledCB.setSelected(scale.getDeviceEnabled());
            freezeEventsCB.setSelected(scale.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("ScalePanel: MethodListener: JposException");
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        scale.setDeviceEnabled(false);
                    }else{
                        scale.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        scale.setFreezeEvents(false);
                    }else{
                        scale.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("ScalePanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(scale != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(scale.getState()));
            }
        }
    }
}
