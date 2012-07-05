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
// CATPanel.java - The CAT panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class CATPanel extends Component implements StatusUpdateListener, ActionListener {
    
	private static final long serialVersionUID = -460029188605524789L;

	protected MainButtonPanel mainButtonPanel;
    
    private CAT cat;
    
    private String defaultLogicalName = "defaultCAT";
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public CATPanel() {
        cat = new CAT();
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
        System.out.println("CAT received status update event.");
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
                cat.addStatusUpdateListener(this);
                
                cat.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = cat.getDeviceServiceVersion();
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
                cat.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                cat.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                cat.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(cat.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + cat.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(cat.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + cat.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + cat.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + cat.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (cat.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (cat.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + cat.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + cat.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + cat.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + cat.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapAdditionalSecurityInformation: " + cat.getCapAdditionalSecurityInformation();
                msg += "\nCapAuthorizeCompletion: " + cat.getCapAuthorizeCompletion();
                msg += "\nCapAuthorizePreSales: " + cat.getCapAuthorizePreSales();
                msg += "\nCapAuthorizeRefund: " + cat.getCapAuthorizeRefund();
                msg += "\nCapAuthorizeVoid: " + cat.getCapAuthorizeVoid();
                msg += "\nCapAuthorizeVoidPreSales: " + cat.getCapAuthorizeVoidPreSales();
                msg += "\nCapCenterResultCode: " + cat.getCapCenterResultCode();
                msg += "\nCapCheckCard: " + cat.getCapCheckCard();
                msg += "\nCapDailyLog: ";
                int log = cat.getCapDailyLog();
                switch (log){
                    case CATConst.CAT_DL_NONE:
                        msg += "CAT_DL_NONE";
                        break;
                    case CATConst.CAT_DL_REPORTING:
                        msg += "CAT_DL_REPORTING";
                        break;
                    case CATConst.CAT_DL_REPORTING_SETTLEMENT:
                        msg += "CAT_DL_REPORTING_SETTLEMENT";
                        break;
                    case CATConst.CAT_DL_SETTLEMENT:
                        msg += "CAT_DL_SETTLEMENT";
                        break;
                }
                msg += "\nCapInstallments: " + cat.getCapInstallments();
                msg += "\nCapPaymentDetail: " + cat.getCapPaymentDetail();
                msg += "\nCapTaxOthers: " + cat.getCapTaxOthers();
                msg += "\nCapTransactionNumber: " + cat.getCapTransactionNumber();
                msg += "\nCapTrainingMode: " + cat.getCapTrainingMode();
                
                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(cat);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(cat);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try {
            deviceEnabledCB.setSelected(cat.getDeviceEnabled());
            freezeEventsCB.setSelected(cat.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("CATPanel: MethodListener: JposException");
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cat.setDeviceEnabled(false);
                    }else{
                        cat.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cat.setFreezeEvents(false);
                    }else{
                        cat.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("CATPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(cat != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(cat.getState()));
            }
        }
    }
}
