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
// CashDrawerPanel.java - The cash drawer panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class CashDrawerPanel extends Component implements StatusUpdateListener, ActionListener {
    
	private static final long serialVersionUID = 4066023079410077425L;

	protected MainButtonPanel mainButtonPanel;
    
    private CashDrawer cashDrawer;
    
    private String defaultLogicalName = "defaultCashDrawer";
    
    
    private JButton openCashDrawerButton;
    private JButton getDrawerOpenedButton;
    private JButton waitForDrawerCloseButton;
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JTextArea statusTextArea;
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    public CashDrawerPanel() {
        cashDrawer = new CashDrawer();
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
        openCashDrawerButton = new JButton("Open Cash Drawer");
        openCashDrawerButton.setActionCommand("openCashDrawer");
        openCashDrawerButton.addActionListener(this);
        openCashDrawerButton.setEnabled(false);
        buttonPanel.add(openCashDrawerButton);
        
        getDrawerOpenedButton = new JButton("Get Drawer Opened");
        getDrawerOpenedButton.setActionCommand("getDrawerOpened");
        getDrawerOpenedButton.addActionListener(this);
        getDrawerOpenedButton.setEnabled(false);
        buttonPanel.add(getDrawerOpenedButton);
        
        waitForDrawerCloseButton = new JButton("Wait For Drawer Close");
        waitForDrawerCloseButton.setActionCommand("waitForDrawerClose");
        waitForDrawerCloseButton.addActionListener(this);
        waitForDrawerCloseButton.setEnabled(false);
        buttonPanel.add(waitForDrawerCloseButton);
        
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
        JLabel label = new JLabel("Action log: ");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(label);
        
        statusTextArea = new JTextArea(10,10);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        statusPanel.add(scrollPane);
        
        
        subPanel.add(statusPanel);
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        String msg = "Status Update Event: ";
        switch(sue.getStatus()){
            case CashDrawerConst.CASH_SUE_DRAWERCLOSED:
                msg += "Drawer Closed\n";
                break;
            case CashDrawerConst.CASH_SUE_DRAWEROPEN:
                msg += "Drawer Opened\n";
                break;
            default:
                msg += "Unknown Status: " + Integer.toString(sue.getStatus()) + "\n";
                break;
        }
        statusTextArea.append(msg);
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
                cashDrawer.addStatusUpdateListener(this);
                
                cashDrawer.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = cashDrawer.getDeviceServiceVersion();
                if(version >= 1009000) {
                    ver_19_complient = true;
                    ver_18_complient = true;
                }
                if(version >= 1008000) {
                    ver_18_complient = true;
                }
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("claim")){
            try{
                cashDrawer.claim(0);
                openCashDrawerButton.setEnabled(true);
                getDrawerOpenedButton.setEnabled(true);
                waitForDrawerCloseButton.setEnabled(true);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                cashDrawer.release();
                openCashDrawerButton.setEnabled(false);
                getDrawerOpenedButton.setEnabled(false);
                waitForDrawerCloseButton.setEnabled(false);
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                cashDrawer.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        else if(ae.getActionCommand().equals("openCashDrawer")){
            statusTextArea.append("Open cash drawer.\n");
            try{
                cashDrawer.openDrawer();
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in openDrawer: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("getDrawerOpened")){
            try{
                if(cashDrawer.getDrawerOpened()) {
                    statusTextArea.append("Cash drawer is open.\n");
                } else {
                    statusTextArea.append("Cash drawer is closed.\n");
                }
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in getDrawerOpened: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("waitForDrawerClose")){
            try{
                cashDrawer.waitForDrawerClose(100,500,100,200);
                statusTextArea.append("Cash drawer closed.\n");
            }catch(JposException e){
                statusTextArea.append("Jpos exception " + e + "\n");
                JOptionPane.showMessageDialog(null, "Exception in waitForDrawerClose: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(cashDrawer.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + cashDrawer.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(cashDrawer.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + cashDrawer.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + cashDrawer.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + cashDrawer.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                msg += "\nCapPowerReporting: " + (cashDrawer.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (cashDrawer.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                msg += "\nCapStatisticsReporting: " + cashDrawer.getCapStatisticsReporting();
                msg += "\nCapUpdateStatistics: " + cashDrawer.getCapUpdateStatistics();
                msg += "\nCapStatus: " + cashDrawer.getCapStatus();
                msg += "\nCapStatusMultiDrawerDetect: " + cashDrawer.getCapStatusMultiDrawerDetect();
                
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + cashDrawer.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + cashDrawer.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + cashDrawer.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + cashDrawer.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                //System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("oce")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                cashDrawer.addStatusUpdateListener(this);
                cashDrawer.open(logicalName);
                cashDrawer.claim(0);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                cashDrawer.setDeviceEnabled(true);
                openCashDrawerButton.setEnabled(true);
                getDrawerOpenedButton.setEnabled(true);
                waitForDrawerCloseButton.setEnabled(true);
                
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(cashDrawer);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(cashDrawer);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            if(cashDrawer.getState() != JposConst.JPOS_S_CLOSED){
                deviceEnabledCB.setSelected(cashDrawer.getDeviceEnabled());
                freezeEventsCB.setSelected(cashDrawer.getFreezeEvents());
            }
        } catch(JposException je) {
            System.err.println("POSPrinterPanel: MethodListener: JposException");
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cashDrawer.setDeviceEnabled(false);
                    }else{
                        cashDrawer.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cashDrawer.setFreezeEvents(false);
                    }else{
                        cashDrawer.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("CashDrawerPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(cashDrawer != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(cashDrawer.getState()));
            }
        }
    }
}
