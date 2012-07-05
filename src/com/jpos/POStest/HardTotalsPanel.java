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
// HardTotalsPanel.java - The HardTotals panel of POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation  Rory K. Shaw/raleigh/IBM 7-8-04
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class HardTotalsPanel extends Component
        implements StatusUpdateListener,
        ActionListener {
    
	private static final long serialVersionUID = -3416954127723228837L;

	protected MainButtonPanel mainButtonPanel;
    
    private HardTotals hardTotals;
    
    private String defaultLogicalName = "defaultHardTotals";
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JTextArea results;
    
    //private JTextArea statusTextArea;
    
    // Hard Totals specific stuff
    boolean hardTotalsOpened = false;
    private HardTotalsListener htListen;
    private JButton htReadButton = new JButton("Read");
    private JButton htWriteButton = new JButton("Write");
    private JButton htClaimFileButton = new JButton("Claim File");
    private JButton htReleaseFileButton = new JButton("Release File");
    private JButton htSetAllButton = new JButton("Set All");
    private JButton htValidateDataButton = new JButton("Validate Data");
    private JButton htRecalculateButton = new JButton("Recalculate");
    private JButton htCreateButton = new JButton("Create");
    private JButton htFindButton = new JButton("Find");
    private JButton htFindByIndexButton = new JButton("Find by Index");
    private JButton htDeleteButton = new JButton("Delete");
    private JButton htRenameButton = new JButton("Rename");
    private JButton htBeginTransButton = new JButton("Begin Trans");
    private JButton htCommitTransButton = new JButton("Commit Trans");
    private JButton htRollbackButton = new JButton("Rollback");
    private JButton clearResultsButton = new JButton("Clear Results");
    
    private JTextField htCursorSet = new JTextField(5);
    private JTextField htTextOut = new JTextField(5);
    private JTextField htFile = new JTextField(5);
    
    private JCheckBox htErrorDetect = new JCheckBox("CapErrorDetection", false);
    private JCheckBox htSingleFile = new JCheckBox("CapSingleFile", false);
    private JCheckBox htTransactions = new JCheckBox("CapTransactions", false);
    private JCheckBox htTransactionsInProgress = new JCheckBox("Transactions in Progress", false);
    private JTextField htFreeData = new JTextField(10);
    private JTextField htTotalsSize = new JTextField(10);
    private JTextField htNumberOfFiles = new JTextField(10);
    private JTextField htByteArraySize = new JTextField(10);
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    protected int hTotalsFile;
    
    protected boolean update;
    
    protected int handle;
    
    protected int offset;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public HardTotalsPanel() {
        hardTotals = new HardTotals();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //      MethodListener methodListener = new MethodListener();
        
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
        
        // HardTotals pane
        htListen = new HardTotalsListener();
        JPanel hardTotalsPanel = new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);
        hardTotalsPanel.setLayout(fl);
        
        
        JPanel hardTotalsROPanel = new JPanel();
        hardTotalsROPanel.setLayout(new GridLayout(5,2));
        hardTotalsROPanel.setBorder(new TitledBorder("Read-Only Properties"));
        hardTotalsROPanel.add(htErrorDetect);
        hardTotalsROPanel.add(htSingleFile);
        hardTotalsROPanel.add(htTransactions);
        hardTotalsROPanel.add(htTransactionsInProgress);
        JLabel htlabel1 =new JLabel("Free Data");
        JLabel htlabel2 = new JLabel("Totals Size");
        JLabel htlabel3 = new JLabel("Number Of Files");
        hardTotalsROPanel.add(htlabel1);
        hardTotalsROPanel.add(htFreeData);
        hardTotalsROPanel.add(htlabel2);
        hardTotalsROPanel.add(htTotalsSize);
        hardTotalsROPanel.add(htlabel3);
        hardTotalsROPanel.add(htNumberOfFiles);
        htFreeData.setEnabled(false);
        htTotalsSize.setEnabled(false);
        htNumberOfFiles.setEnabled(false);
        htErrorDetect.setEnabled(false);
        htTransactions.setEnabled(false);
        htTransactionsInProgress.setEnabled(false);
        htSingleFile.setEnabled(false);
        
        JPanel htCursorPanel = new JPanel();
        htCursorPanel.setLayout(new GridLayout(4,2));
        htCursorPanel.setBorder(new TitledBorder("File/Cursor"));
        htCursorPanel.add(new JLabel("Current File Handle"));
        htCursorPanel.add(htFile);
        htFile.setToolTipText("enter integer");
        htCursorPanel.add(new JLabel("Offset for next method"));
        htCursorPanel.add(htCursorSet);
        htCursorSet.setToolTipText("enter < 0 - (ByteArraySize-1) >");
        htCursorPanel.add(new JLabel("Text for next method"));
        htCursorPanel.add(htTextOut);
        htTextOut.setToolTipText("enter any text, strings or integers");
        htCursorPanel.add(new JLabel("Byte Array Size"));
        htCursorPanel.add(htByteArraySize);
        htByteArraySize.setToolTipText("enter integer > 400, will be set to 400 by default");
        
        JPanel htIOPanel = new JPanel();
        htIOPanel.setLayout(new GridLayout(3,8,7,7));
        htIOPanel.setBorder(new TitledBorder("Specific Methods"));
        htIOPanel.add(htReadButton);
        htIOPanel.add(htWriteButton);
        htIOPanel.add(htCreateButton);
        htCreateButton.setActionCommand("nohandle");
        htIOPanel.add(htClaimFileButton);
        htIOPanel.add(htReleaseFileButton) ;
        htIOPanel.add(htSetAllButton);
        htIOPanel.add(htValidateDataButton);
        htIOPanel.add(htRecalculateButton);
        htIOPanel.add(htFindButton);
        htFindButton.setActionCommand("nohandle");
        htIOPanel.add(htFindByIndexButton);
        htIOPanel.add(htDeleteButton);
        htDeleteButton.setActionCommand("nohandle");
        htIOPanel.add(htRenameButton);
        htIOPanel.add(htBeginTransButton);
        htBeginTransButton.setActionCommand("nohandle");
        htIOPanel.add(htCommitTransButton);
        htCommitTransButton.setActionCommand("nohandle");
        htIOPanel.add(htRollbackButton);
        htRollbackButton.setActionCommand("nohandle");
        
        hardTotalsPanel.add(hardTotalsROPanel);
        hardTotalsPanel.add(htCursorPanel);
        hardTotalsPanel.add(htIOPanel);
        
                /* eliminated added all panels
                int tx = (hardTotalsROPanel.getSize()).height + (htCursorPanel.getSize()).height
                        + (hardTotalsPanel.getSize()).height;
                 
                hardTotalsPanel.setPreferredSize( new Dimension((htIOPanel.getSize()).width,tx));
                        JScrollPane htScroll = new JScrollPane();
                htScroll.setViewportView(hardTotalsPanel);
                 */
        
        htReadButton.addActionListener(htListen);
        htReadButton.setToolTipText("Read a series of bytes");
        htWriteButton.addActionListener(htListen);
        htWriteButton.setToolTipText("Write a series of bytes");
        htClaimFileButton.addActionListener(htListen);
        htClaimFileButton.setToolTipText("Claim a specific file");
        htReleaseFileButton.addActionListener(htListen);
        htReleaseFileButton.setToolTipText("Release access to a file");
        htSetAllButton.addActionListener(htListen);
        htSetAllButton.setToolTipText("Set all data in totals file to a value ");
        htValidateDataButton.addActionListener(htListen);
        htValidateDataButton.setToolTipText("Validate data in totals file");
        htRecalculateButton.addActionListener(htListen);
        htRecalculateButton.setToolTipText("Recalculate validation data in totals file");
        htCreateButton.addActionListener(htListen);
        htCreateButton.setToolTipText("Create a totals file ");
        htFindButton.addActionListener(htListen);
        htFindButton.setToolTipText("Find totals file by name");
        htFindByIndexButton.addActionListener(htListen);
        htFindByIndexButton.setToolTipText("Enumerate all files in Hard Totals area");
        htDeleteButton.addActionListener(htListen);
        htDeleteButton.setToolTipText("Delete a totals file by name");
        htRenameButton.addActionListener(htListen);
        htRenameButton.setToolTipText("Rename an existing totals file");
        htBeginTransButton.addActionListener(htListen);
        htBeginTransButton.setToolTipText("Marks beginning of a transaction");
        htCommitTransButton.addActionListener(htListen);
        htCommitTransButton.setToolTipText("Ends the current transaction," +
                "and saves the updated data");
        htRollbackButton.addActionListener(htListen);
        htRollbackButton.setToolTipText("Ends the current transaction," +
                "and discards the updates");
        
        
        JPanel resultsPanel = new JPanel();
        results = new JTextArea();
        results.setFont(new Font("arialnarrow",Font.BOLD,10));
        results.setLineWrap(true);
        results.setWrapStyleWord(true);
        results.setEditable(false);
        JScrollPane resultsScrollPane = new JScrollPane(results);
        resultsScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setPreferredSize(new Dimension(300,100));
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Results"));
        resultsPanel.add(resultsScrollPane);
        resultsPanel.add(clearResultsButton);
        
        //mainPanel.add(hardTotalsPanel);
        mainPanel.add(hardTotalsROPanel);
        mainPanel.add(htCursorPanel);
        mainPanel.add(htIOPanel);
        mainPanel.add(resultsPanel);
        
        clearResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                results.setText("");
            }
        });
        
        
        return mainPanel;
    }
    
    
    protected void setHandleTotalsFile(int htf) {
        hTotalsFile = htf;
    }
    
    protected int getHandleTotalsFile() {
        return hTotalsFile;
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        System.out.println("HardTotals received status update event.");
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
                hardTotals.addStatusUpdateListener(this);
                hardTotals.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = hardTotals.getDeviceServiceVersion();
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
                hardTotals.claim(1000);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                hardTotals.release();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                hardTotals.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(hardTotals.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + hardTotals.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(hardTotals.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + hardTotals.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + hardTotals.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + hardTotals.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (hardTotals.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (hardTotals.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + hardTotals.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + hardTotals.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + hardTotals.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + hardTotals.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }                
                
                
                msg += "\nCapErrorDetection: " + hardTotals.getCapErrorDetection();
                msg += "\nCapSingleFile: " + hardTotals.getCapSingleFile();
                msg += "\nCapTransactions: " + hardTotals.getCapTransactions();
                
                
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
                hardTotals.addStatusUpdateListener(this);
                hardTotals.open(logicalName);
                hardTotals.claim(1000);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                hardTotals.setDeviceEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(hardTotals);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(hardTotals);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            deviceEnabledCB.setSelected(hardTotals.getDeviceEnabled());
            freezeEventsCB.setSelected(hardTotals.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("HardTotalsPanel: MethodListener: JposException");
        }
    }
    
    
    private class HardTotalsListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            boolean update= false;
            boolean handlevalid = true;
            Object obj = e.getSource();
            int handle = 0;
            results.append("\nin HardTotalsListener method\n");
            try{
                handle = (new Integer(htFile.getText())).intValue();
            } catch(Exception exc){
                handlevalid = false;
            }
            try{
                if( (e.getActionCommand()).equals("nohandle")){
                    if(obj == htRollbackButton) {
                        hardTotals.rollback();
                        results.append("Rollback successful\n");
                    }
                    if(obj == htCommitTransButton) {
                        hardTotals.commitTrans();
                        results.append("Commit Transaction successful\n");
                    }
                    if(obj == htBeginTransButton) {
                        hardTotals.beginTrans();
                        results.append("Begin Transaction successful\n");
                    }
                    if(obj == htDeleteButton) {
                        hardTotals.delete(htTextOut.getText());
                        update=true;
                        results.append("Delete successful\n");
                    }
                    if(obj == htFindButton) {
                        int nhandle[] = new int[1];
                        int nsize[] = new int[1];
                        hardTotals.find(htTextOut.getText(),nhandle,nsize);
                        results.append("Found " +htTextOut.getText()+ " handle = " + String.valueOf(nhandle[0]) + " and size = "
                                + String.valueOf(nsize[0])+"\n");
                    }
                    if(obj == htCreateButton) {
                        int nhandle[] = new int[1];
                        
                        int arraySize = (new Integer(htByteArraySize.getText())).intValue();
                        System.out.println(arraySize);
                        if (arraySize>400) {
                            hardTotals.create(htTextOut.getText(),nhandle,arraySize,htErrorDetect.isSelected());
                            htFile.setText(String.valueOf(nhandle[0]));
                            update=true;
                            results.append("Create successful\n");
                        }else {
                            hardTotals.create(htTextOut.getText(),nhandle,400,htErrorDetect.isSelected());
                            htFile.setText(String.valueOf(nhandle[0]));
                            update=true;
                            results.append("Create successful\n");
                        }
                    }
                    
                }//if equals "nohandle"
                else{
                    if (!handlevalid) {
                        results.append("Handle not a valid number *** !true ***\n");
                    } else{
                        if(obj == htSetAllButton) {
                            String x= htTextOut.getText();
                            byte temp;
                            if (x.equals(""))
                                temp =  0;
                            else
                                temp = (byte)x.charAt(0);
                            hardTotals.setAll(handle,temp);
                            results.append("SetAll successful\n");
                            System.out.println("Pressed Set All Button!");
                        }
                        if(obj == htRecalculateButton) {
                            hardTotals.recalculateValidationData(handle);
                            results.append("Recalculate successful\n");
                            System.out.println("Pressed Recalculate Data Button!");
                        }
                        if(obj == htValidateDataButton) {
                            hardTotals.validateData(handle);
                            results.append("ValidateData successful\n");
                            System.out.println("Pressed Validate Data Button");
                        }
                        if(obj == htReleaseFileButton) {
                            hardTotals.releaseFile(handle);
                            results.append("Release File successful\n");
                            System.out.println("Pressed Release File Button!");
                        }
                        if(obj == htClaimFileButton) {
                            hardTotals.claimFile(handle,200);
                            results.append("ClaimFile successful\n");
                            System.out.println("Pressed Claim File Button!");
                        }
                        if(obj == htRenameButton) {
                            hardTotals.rename(handle,htTextOut.getText());
                            results.append("Rename successful\n");
                            System.out.println("Pressed Rename Button!");
                        }
                        if(obj == htFindByIndexButton) {
                            String data[] = new String[1];
                            hardTotals.findByIndex(handle,data);
                            results.append("Found index " + String.valueOf(handle) + " name = \"" + data[0] + "\"\n");
                        }
                        if(obj==htWriteButton || obj==htReadButton){
                            int offset = 0;
                            try{
                                offset = (new Integer(htCursorSet.getText())).intValue();
                            } catch(Exception rwexc){
                                results.append("Offset for next method is not a valid number\n");
                                handlevalid = false;
                            }
                            if (handlevalid) {
                                if(obj == htWriteButton) {
                                    byte outs[] = (htTextOut.getText()).getBytes();
                                    hardTotals.write(handle,outs ,offset,outs.length);
                                    results.append("Write successful\n");
                                }
                                if(obj == htReadButton) {
                                    try{
                                        int len = (new Integer(htTextOut.getText())).intValue();
                                        byte data[] = new byte[len];
                                        hardTotals.read(handle,data,offset,len);
                                        results.append("read \"" +(new String(data)));
                                        results.append("\"  \n");
                                    } catch(NumberFormatException nfe){
                                        results.append("Text for next method is not a valid size for read\n");
                                    }
                                }
                            }//if handlevalid and valid offset for read write
                        }//if htwrite or htread
                    }//else handlevalid
                }//else nohandle false
                //HardTotals Properties intial values
                if(update){
                    htTotalsSize.setText(String.valueOf( hardTotals.getTotalsSize()));
                    htFreeData.setText(String.valueOf( hardTotals.getFreeData()));
                    htNumberOfFiles.setText(String.valueOf(hardTotals.getNumberOfFiles()));
                    htErrorDetect.setSelected(hardTotals.getCapErrorDetection());
                    htSingleFile.setSelected(hardTotals.getCapSingleFile());
                    htTransactions.setSelected(hardTotals.getCapTransactions());
                    htTransactionsInProgress.setSelected(hardTotals.getTransactionInProgress());
                }
            } catch(JposException ex){
                
                System.err.println("Jpos exception " + ex);
                results.append("STOP THAT!!!!!!!");
                results.append("Stop going straight to JposException");
                
            }
        }
    }
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        hardTotals.setDeviceEnabled(false);
                    }else{
                        hardTotals.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        hardTotals.setFreezeEvents(false);
                    }else{
                        hardTotals.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("HardTotalsPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(hardTotals != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(hardTotals.getState()));
            }
        }
    }
    
}
