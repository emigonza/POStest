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
// ElectronicJournalPanel.java - The ElectronicJournal panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class ElectronicJournalPanel extends Component implements StatusUpdateListener, ActionListener 
{
    private static final long serialVersionUID = -3504677263114136010L;
    protected MainButtonPanel mainButtonPanel;
    
    private ElectronicJournal electronicJournal;
    
    private String defaultLogicalName = "defaultElectronicJournal";
    
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public ElectronicJournalPanel()
    {
        electronicJournal = new ElectronicJournal();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make()
    {
        
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
    
    public void statusUpdateOccurred(StatusUpdateEvent sue)
    {
        System.out.println("ElectronicJournal received status update event.");
    }
    
    
    /** Listens to the method buttons. */
    
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open"))
        {
            try
            {
                if(logicalName.equals(""))
                {
                    logicalName = defaultLogicalName;
                }
                electronicJournal.addStatusUpdateListener(this);
                
                electronicJournal.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                //int version = electronicJournal.getDeviceServiceVersion();
                
            }
            catch(JposException e)
            {
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
        else if(ae.getActionCommand().equals("claim"))
        {
            try
            {
                electronicJournal.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }
            catch(JposException e)
            {
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
        else if(ae.getActionCommand().equals("release"))
        {
            try
            {
                electronicJournal.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }
            catch(JposException e)
            {
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
        else if(ae.getActionCommand().equals("close"))
        {
            try
            {
                electronicJournal.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }
            catch(JposException e)
            {
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
        else if(ae.getActionCommand().equals("info"))
        {
            try
            {
                String ver = new Integer(electronicJournal.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + electronicJournal.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(electronicJournal.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + electronicJournal.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + electronicJournal.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + electronicJournal.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (electronicJournal.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (electronicJournal.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                
                //since ElectronicJournal didn't come out until 1.10, no need to check if we support up to 1.10
                msg += "\nCapStatisticsReporting: " + electronicJournal.getCapStatisticsReporting();                    
                msg += "\nCapUpdateStatistics: " + electronicJournal.getCapUpdateStatistics();
                
                msg += "\nCapCompareFirmwareVersion: " + electronicJournal.getCapCompareFirmwareVersion();                    
                msg += "\nCapUpdateFirmware: " + electronicJournal.getCapUpdateFirmware();
                
                msg += "\nCapAddMarker: " + electronicJournal.getCapAddMarker();
                msg += "\nCapErasableMedium: " + electronicJournal.getCapErasableMedium();
                msg += "\nCapInitializeMedium: " + electronicJournal.getCapInitializeMedium();
                msg += "\nCapMediumIsAvailable: " + electronicJournal.getCapMediumIsAvailable();
                msg += "\nCapPrintContent: " + electronicJournal.getCapPrintContent();
                msg += "\nCapPrintContentFile: " + electronicJournal.getCapPrintContentFile();
                msg += "\nCapRetrieveCurrentMarker: " + electronicJournal.getCapRetrieveCurrentMarker();
                msg += "\nCapRetrieveMarker: " + electronicJournal.getCapRetrieveMarker();
                msg += "\nCapRetrieveMarkerByDateTime: " + electronicJournal.getCapRetrieveMarkerByDateTime();
                msg += "\nCapRetrieveMarkersDateTime: " + electronicJournal.getCapRetrieveMarkersDateTime();
                msg += "\nCapStation: " + getCapStationString(electronicJournal.getCapStation());
                msg += "\nCapStorageEnabled: " + electronicJournal.getCapStorageEnabled();
                msg += "\nCapSuspendPrintContent: " + electronicJournal.getCapSuspendPrintContent();
                msg += "\nCapSuspendQueryContent: " + electronicJournal.getCapSuspendQueryContent();
                msg += "\nCapWaterMark: " + electronicJournal.getCapWaterMark();

                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                
            }
            catch(JposException e)
            {
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
        else if(ae.getActionCommand().equals("stats"))
        {
            try
            {
                StatisticsDialog dlg = new StatisticsDialog(electronicJournal);
                dlg.setVisible(true);
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware"))
        {
            try
            {
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(electronicJournal);
                dlg.setVisible(true);
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try
        {
            deviceEnabledCB.setSelected(electronicJournal.getDeviceEnabled());
            freezeEventsCB.setSelected(electronicJournal.getFreezeEvents());
        }
        catch(JposException je)
        {
            System.err.println("BiometricsPanel: MethodListener: JposException");
        }
    }
    
    String getCapStationString(int cap)
    {
        String retval = "";
        if((cap & ElectronicJournalConst.EJ_S_RECEIPT) > 0)
        {
            retval += "Receipt, ";
        }
        if((cap & ElectronicJournalConst.EJ_S_SLIP) > 0)
        {
            retval += "Slip, ";
        }
        if((cap & ElectronicJournalConst.EJ_S_JOURNAL) > 0)
        {
            retval += "Journal, ";
        }
        return retval;
    }
    
    class CheckBoxListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            Object source = e.getItemSelectable();
            try
            {
                if (source == deviceEnabledCB)
                {
                    if (e.getStateChange() == ItemEvent.DESELECTED)
                    {
                        electronicJournal.setDeviceEnabled(false);
                    }
                    else
                    {
                        electronicJournal.setDeviceEnabled(true);
                    }
                }
                else if (source == freezeEventsCB)
                {
                    if (e.getStateChange() == ItemEvent.DESELECTED)
                    {
                        electronicJournal.setFreezeEvents(false);
                    }
                    else
                    {
                        electronicJournal.setFreezeEvents(true);
                    }
                }
            }
            catch(JposException je)
            {
                System.err.println("BiometricsPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask
    {
        public void run()
        {
            if(electronicJournal != null)
            {
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(electronicJournal.getState()));
            }
        }
    }
}
