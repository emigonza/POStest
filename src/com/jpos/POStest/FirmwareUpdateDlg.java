/*
 *
 * Created on May 17, 2005, 4:26 AM
 * *
 * This software is provided "AS IS".  Ultimate Technology Corp.
 * OR ANY OTHER MEMBER OF THE JavaPOS Working Group MAKE NO
 * REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. The authors shall not be liable for
 * any damages suffered as a result of using, modifying or distributing this
 * software or its derivatives. Permission to use, copy, modify, and distribute
 * the software and its documentation for any purpose is hereby granted.
 *
 * FirmwareUpdateDlg.java a generic dialog box for performing Firmware functions
 */

package com.jpos.POStest;
import javax.swing.*;
import jpos.*;
import jpos.events.*;
import java.lang.reflect.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Jeff Lange
 */
public class FirmwareUpdateDlg extends JDialog implements ActionListener, StatusUpdateListener{

	private static final long serialVersionUID = 1180348090398413114L;
	
	private Object deviceObject;
    private Method compareMethod = null;
    private Method updateMethod   = null;
    private Method getCapUpdateFirmware = null;
    private Method getCapCompareFirmwareVersion = null;
    private Method addStatusUpdateListener = null;
    private Boolean capUpdateFirmware = new Boolean(false);
    private Boolean capCompareFirmwareVersion = new Boolean(false);
    
    private JRadioButton updateRadio;
    private JRadioButton compareRadio;
    private JTextField fileNameTF;
    private JTextArea messageWindow;
    private JProgressBar progressBar;
    private JButton goButton;
    private JButton closeButton;
    //private JList fileList;
    
    /** Creates a new instance of FirmwareUpdateDlg */
    public FirmwareUpdateDlg(Object object) throws Exception{
        deviceObject = object;
        Class<? extends Object> c = object.getClass();
        Method[] theMethods = c.getMethods();
        for (int i = 0; i < theMethods.length; i++) {
            if(theMethods[i].getName().equals("compareFirmwareVersion")){
                compareMethod = theMethods[i];
            }else if(theMethods[i].getName().equals("updateFirmware")){
                updateMethod = theMethods[i];
            }else if(theMethods[i].getName().equals("getCapUpdateFirmware")){
                getCapUpdateFirmware = theMethods[i];
            }else if(theMethods[i].getName().equals("getCapCompareFirmwareVersion")){
                getCapCompareFirmwareVersion = theMethods[i];
            }else if(theMethods[i].getName().equals("addStatusUpdateListener")){
                addStatusUpdateListener = theMethods[i];
            }
        }
        
        // If we don't have one of these methods, this isn't a supported object type
        if(compareMethod == null || updateMethod == null || getCapUpdateFirmware == null || getCapCompareFirmwareVersion == null) {
            throw new Exception("Object passed to FirmwareUpdateDialog() does not support the firmware interface.\nPerhaps the Service Object is not UPOS v1.9 complient.");
        }
        
        try{
            Object[] args = new Object[0];
            capCompareFirmwareVersion = (Boolean)getCapCompareFirmwareVersion.invoke(deviceObject, args);
            capUpdateFirmware = (Boolean)getCapUpdateFirmware.invoke(deviceObject, args);
        }catch(InvocationTargetException e){
            JposException je = (JposException)e.getTargetException();
            JOptionPane.showMessageDialog(null,"exception getting device caps:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
        }catch(IllegalAccessException e){
            JOptionPane.showMessageDialog(null,"call to a getCap function threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
        }
        
        try{
            Object[] args = new Object[1];
            args[0] = this;
            addStatusUpdateListener.invoke(deviceObject, args);
        }catch(InvocationTargetException e){
            JposException je = (JposException)e.getTargetException();
            JOptionPane.showMessageDialog(null,"exception calling addStatusUpdateListener:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
        }catch(IllegalAccessException e){
            JOptionPane.showMessageDialog(null,"addStatusUpdateListener threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
        }
        
        setModal(true);
        setSize(500, 300);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //Make this dialog display the main panel.
        setContentPane(mainPanel);
        
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        mainPanel.add(row1);
        
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        mainPanel.add(row2);
        
        JPanel row3 = new JPanel();
        row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
        mainPanel.add(row3);
        
        JPanel row4 = new JPanel();
        row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
        mainPanel.add(row4);
        
        JPanel row5 = new JPanel();
        row5.setLayout(new BoxLayout(row5, BoxLayout.X_AXIS));
        mainPanel.add(row5);
        
        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        row2.add(textBox);
        
        ButtonGroup group = new ButtonGroup();
        
        updateRadio= new JRadioButton("Update");
        updateRadio.setEnabled(capUpdateFirmware.booleanValue());
        updateRadio.setSelected(true);
        group.add(updateRadio);
        row1.add(updateRadio);
        
        compareRadio = new JRadioButton("Compare Versions");
        compareRadio.setSelected(false);
        compareRadio.setEnabled(capCompareFirmwareVersion.booleanValue());
        group.add(compareRadio);
        row1.add(compareRadio);
        
        row1.add(Box.createHorizontalStrut(150));
        
        goButton = new JButton("Go");
        goButton.setActionCommand("Go");
        goButton.addActionListener(this);
        row1.add(goButton);
        
        row1.add(Box.createHorizontalStrut(5));
        
        closeButton = new JButton("Close");
        closeButton.setActionCommand("Close");
        closeButton.addActionListener(this);
        row1.add(closeButton);
        
        JLabel label = new JLabel("Select the file you would like to use.");
        textBox.add(label);
        
        row3.add(Box.createHorizontalStrut(10));
        label = new JLabel("Filename:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        //label.setEnabled(capUpdateStatistics);
        row3.add(label);
        
        fileNameTF = new JTextField();
        fileNameTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        fileNameTF.setPreferredSize(new Dimension(200, 25));
        //fileNameTF.setEnabled(capUpdateStatistics);
        row3.add(fileNameTF);
        row3.add(Box.createHorizontalStrut(10));
        
        JButton browseButton = new JButton("Browse");
        browseButton.setActionCommand("Browse");
        browseButton.addActionListener(this);
        row3.add(browseButton);
        row3.add(Box.createHorizontalStrut(80));

//        String[] data = {"test","test2","test3"};
//        fileList = new JList(data);
//        JScrollPane scrollP = new JScrollPane(fileList);
        
//        fileList.setVisibleRowCount(2);
        //fileList.setPreferredSize(new Dimension(50,40));
        //fileList.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
//        row3.add(fileList);
        
        progressBar = new JProgressBar(0,100);
        row4.add(progressBar);
        
        messageWindow = new JTextArea();
        messageWindow.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        JScrollPane scrollPane = new JScrollPane(messageWindow);
        scrollPane.setPreferredSize(new Dimension(Short.MAX_VALUE, 200));
        row5.add(scrollPane);
        
        if(!capUpdateFirmware.booleanValue() && !capCompareFirmwareVersion.booleanValue()){
            messageWindow.setText(messageWindow.getText() + 
                    "capUpdateFirmware and capCompareFirmwareVersion are both false for this device.\n"+
                    " Nothing to do.");
            goButton.setEnabled(false);
        }
        setTitle("Firmware Utilities");
    }
    
    public void actionPerformed(ActionEvent ae) {
        if(ae.getActionCommand().equals("Close")){
            setVisible(false);
        } else if(ae.getActionCommand().equals("Go")){
            if(compareRadio.isSelected()){
                try{
                    int[] result = new int[1];
                    Object[] args = new Object[1];
                    args[0] = fileNameTF.getText();
                    args[1] = result;
                    compareMethod.invoke(deviceObject, args);
                }catch(InvocationTargetException e){
                    JposException je = (JposException)e.getTargetException();
                    JOptionPane.showMessageDialog(null,"compareFirmwareVersion threw an exception:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }catch(IllegalAccessException e){
                    JOptionPane.showMessageDialog(null,"call to compareFirmwareVersion threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }catch(Exception e){
                    try{
                        if(e.getClass().equals(Class.forName("JposException"))){
                            JOptionPane.showMessageDialog(null,"call to compareFirmwareVersion threw a JposException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                        }
                    }catch(ClassNotFoundException ex){
                        JOptionPane.showMessageDialog(null,"Exception in compareFirmwareVersion:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }else if(updateRadio.isSelected()){
                messageWindow.setText(messageWindow.getText() + "The update firmware process is starting..\n");
                goButton.setEnabled(false);
                closeButton.setEnabled(false);
                UpdateThread thread = new UpdateThread();
                thread.start();
            }
        }else if(ae.getActionCommand().equals("Browse")){
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                fileNameTF.setText(chooser.getSelectedFile().getPath());
            }
        }
    }
    
    public void statusUpdateOccurred( StatusUpdateEvent sue ){
        int status = sue.getStatus();
//        JOptionPane.showMessageDialog(null,
//            "JPOS_SUE_UF_PROGRESS = " +JposConst.JPOS_SUE_UF_PROGRESS + "\n"+
//            "JPOS_SUE_UF_COMPLETE = " +JposConst.JPOS_SUE_UF_COMPLETE + "\n"+
//            "JPOS_SUE_UF_COMPLETE_DEV_NOT_RESTORED = " + JposConst.JPOS_SUE_UF_COMPLETE_DEV_NOT_RESTORED + "\n"+
//            "JPOS_SUE_UF_FAILED_DEV_OK = " + JposConst.JPOS_SUE_UF_FAILED_DEV_OK + "\n"+
//            "JPOS_SUE_UF_FAILED_DEV_UNRECOVERABLE = " + JposConst.JPOS_SUE_UF_FAILED_DEV_UNRECOVERABLE + "\n"+
//            "JPOS_SUE_UF_FAILED_DEV_NEEDS_FIRMWARE = " + JposConst.JPOS_SUE_UF_FAILED_DEV_NEEDS_FIRMWARE + "\n"+
//            "JPOS_SUE_UF_FAILED_DEV_UNKNOWN = " + JposConst.JPOS_SUE_UF_FAILED_DEV_UNKNOWN + "\n",
//            "Exception",JOptionPane.ERROR_MESSAGE);

        if(status >= JposConst.JPOS_SUE_UF_PROGRESS && status < JposConst.JPOS_SUE_UF_COMPLETE) {
            progressBar.setValue(status - JposConst.JPOS_SUE_UF_PROGRESS);
            return;
        }
        switch (status){
            case JposConst.JPOS_SUE_UF_COMPLETE:
                progressBar.setValue(100);
                messageWindow.setText(messageWindow.getText() + 
                        "The update firmware process has completed successfully.\n");
                break;
            case JposConst.JPOS_SUE_UF_COMPLETE_DEV_NOT_RESTORED:
                messageWindow.setText(messageWindow.getText() + 
                        "********************************************************************************\n"+
                        "The update firmware process succeeded, however the Service and/or \n"+
                        "the physical device cannot be returned to the state they were in before the \n"+
                        "update firmware process started. The Service has restored all properties to \n"+
                        "their default initialization values. To ensure consistent Service and \n"+
                        "physical device states, the application needs to close the Service, then \n"+
                        "open, claim, and enable again, and also restore all custom application settings.\n"+
                        "********************************************************************************\n");
                break;
            case JposConst.JPOS_SUE_UF_FAILED_DEV_OK:
                messageWindow.setText(messageWindow.getText() + 
                        "********************************************************************************\n"+
                        "The update firmware process failed but the device is still operational. \n"+
                        "********************************************************************************\n");
                break;
            case JposConst.JPOS_SUE_UF_FAILED_DEV_UNRECOVERABLE:
                messageWindow.setText(messageWindow.getText() + 
                        "********************************************************************************\n"+
                        "The update firmware process failed and the device is neither usable\n"+
                        "nor recoverable through software. The device requires service to be returned to an \n"+
                        "operational state.\n"+
                        "********************************************************************************\n");
                break;
            case JposConst.JPOS_SUE_UF_FAILED_DEV_NEEDS_FIRMWARE:
                messageWindow.setText(messageWindow.getText() + 
                        "********************************************************************************\n"+
                        "The update firmware process failed and the device will\n" +
                        "not be operational until another attempt to update the\n" +
                        "firmware is successful.\n" +
                        "********************************************************************************\n");
                break;
            case JposConst.JPOS_SUE_UF_FAILED_DEV_UNKNOWN:
                messageWindow.setText(messageWindow.getText() + 
                        "********************************************************************************\n"+
                        "The update firmware process failed and the device is in an indeterminate state.\n"+
                        "********************************************************************************\n");
                break;
        }
        goButton.setEnabled(false);
        closeButton.setEnabled(true);
    }
    
    private class UpdateThread extends Thread{
        public void run(){
            try{
                Object[] args = new Object[1];
                args[0] = fileNameTF.getText();
                updateMethod.invoke(deviceObject, args);
            }catch(InvocationTargetException e){
                JposException je = (JposException)e.getTargetException();
                JOptionPane.showMessageDialog(null,"updateFirmware threw an exception:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
            }catch(IllegalAccessException e){
                JOptionPane.showMessageDialog(null,"call to updateFirmware threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                try{
                    if(e.getClass().equals(Class.forName("JposException"))){
                        JOptionPane.showMessageDialog(null,"call to updateFirmware threw a JposException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                    }
                }catch(ClassNotFoundException ex){
                    JOptionPane.showMessageDialog(null,"Exception in updateFirmware:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    }
}
