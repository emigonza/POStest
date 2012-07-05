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
// POSPrinterPanel.java - The POSPrinter panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class POSPrinterPanel extends Component {
    
	private static final long serialVersionUID = -2637467572297349614L;

	protected MainButtonPanel mainButtonPanel;
    
    private POSPrinter posPrinter;
    
    private String defaultLogicalName = "defaultPOSPrinter";
    
    private JTextArea posPrinterOutputArea;
    
    
    private JButton printNormalButton;
    private JButton printImmediateButton;
    private JButton printBarCodeButton;
    private JButton cutPaperButton;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox asyncCB;
    private JCheckBox idleCB;
    private JButton letterQualityButton;
    
    private JComboBox mapModeCombo;
    
    private JComboBox rotationCombo;
    
    private JButton beginInsertionButton;
    private JButton endInsertionButton;
    private JButton beginRemovalButton;
    private JButton endRemovalButton;
    
    private JButton addESCButton;
    private JButton topLogoButton;
    private JButton bottomLogoButton;
    private JButton validateDataButton;
    
    private JComboBox symbologyCombo;
    
    
    private JTextField barCodeHeight;
    private JTextField barCodeWidth;
    private JTextField barCodeData;
    private JComboBox barCodeAlignmentCombo ;
    private JComboBox barCodeTextCombo;
    private JButton rotateBarcodeButton;
    
    private JFileChooser bitmapFileChooser;
    private JTextField bitmapFileName;
    private JButton bitmapBrowse;
    private JButton printBitmapButton;
    private JComboBox bitmapWidth;
    private JComboBox bitmapAlign;
    private JButton setBitmapButton;
    private JComboBox bitmapNumberCombo;
    private JButton rotateBitmapButton;
    
    private JLabel pageModeArea;
    private JLabel pageModeDescriptor;
    private JTextField pageModeHorizontalPos;
    private JTextField pageModeVerticalPos;
    private JTextField pageModePrintArea;
    private JComboBox pageModeDirectionCombo;
    private JComboBox pageModeStationCombo;
    private JComboBox pageModeControlCombo;
    
    private JTextArea messages;
    private JScrollPane scrollPane;
    
    private JButton clearPrintAreadButton;
    private JButton pageModePrintButton;
    private JButton pageModeUpdatePropButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    private boolean ver_17_complient = false;
    
    private int station;
    
    boolean deviceEnabled;
    boolean freezeEvents;
    
    boolean updateDevice = true;
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    private class EventListener implements StatusUpdateListener,
            OutputCompleteListener,
            DirectIOListener,
            ErrorListener {
        
        public void statusUpdateOccurred( StatusUpdateEvent sue ){
        	updateMessages("SUE: " + getSUEMessage(sue.getStatus()));            
        }
        
        public void directIOOccurred( DirectIOEvent dioe){
        	updateMessages("Dir I/O:  Direct I/O Event " + dioe.getEventNumber() + "returned data ='" + Integer.toString(dioe.getData()));            
        }
        
        public void outputCompleteOccurred( OutputCompleteEvent oce){
        	updateMessages("OCE: Output Event" + oce.getOutputID() +" compleated");            
        }
        
        public void errorOccurred(ErrorEvent ee){
        	updateMessages("Error: Error Event" + ee);            
        }
    }
    
    private String getSUEMessage(int code){
        String value = "Unknown";
        switch(code){
            case POSPrinterConst.PTR_SUE_COVER_OPEN:
                value = "Cover Open";
                break;
            case POSPrinterConst.PTR_SUE_COVER_OK:
                value = "Cover OK";
                break;
            case POSPrinterConst.PTR_SUE_JRN_EMPTY:
                value = "Journal Paper Empty";
                break;
            case POSPrinterConst.PTR_SUE_JRN_NEAREMPTY:
                value = "Journal Paper Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_JRN_PAPEROK:
                value = "Journal Papey OK";
                break;
            case POSPrinterConst.PTR_SUE_REC_EMPTY:
                value = "Receipt Paper Empty";
                break;
            case POSPrinterConst.PTR_SUE_REC_NEAREMPTY:
                value = "Receipt Paper Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_REC_PAPEROK:
                value = "Receipt Paper OK";
                break;
            case POSPrinterConst.PTR_SUE_SLP_EMPTY:
                value = "Slip Paper Empty";
                break;
            case POSPrinterConst.PTR_SUE_SLP_NEAREMPTY:
                value = "Slip Paper Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_SLP_PAPEROK:
                value = "Slip Paper OK";
                break;
            case POSPrinterConst.PTR_SUE_IDLE:
                value = "Printer Idle";
                break;
            case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_EMPTY:
                value = "Journal Cartridge Empty";
                break;
            case POSPrinterConst.PTR_SUE_JRN_HEAD_CLEANING:
                value = "Journal Head Cleaning Started";
                break;
            case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_NEAREMPTY:
                value = "Journal Cartridge Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_JRN_CARTDRIGE_OK:
                value = "Journal Cartridge OK";
                break;
            case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_EMPTY:
                value = "Receipt Cartridge Empty";
                break;
            case POSPrinterConst.PTR_SUE_REC_HEAD_CLEANING:
                value = "Receipt Head Cleaning Started";
                break;
            case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_NEAREMPTY:
                value = "Receipt Cartridge Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_REC_CARTDRIGE_OK:
                value = "Receipt Cartridge OK";
                break;
            case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_EMPTY:
                value = "Slip Cartridge Empty";
                break;
            case POSPrinterConst.PTR_SUE_SLP_HEAD_CLEANING:
                value = "Slip Head Cleaning Started";
                break;
            case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_NEAREMPTY:
                value = "Slip Cartridge Near Empty";
                break;
            case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_OK:
                value = "Slip Cartridge OK";
                break;
        }
        return value;
    }
    private EventListener eventListener = new EventListener();
    
    public POSPrinterPanel() {
        posPrinter = new POSPrinter();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    private class StrVal{
        public StrVal(String n, int v){
            value = v;
            name = n;
        }
        
        public String toString(){
            return name;
        }
        
        int value;
        String name;
    }
    
    StrVal[] bcSymbologies = {
        new StrVal("UPC-A", POSPrinterConst.PTR_BCS_UPCA),
                new StrVal("UPC-A SUP", POSPrinterConst.PTR_BCS_UPCA_S),
                new StrVal("UPC-E", POSPrinterConst.PTR_BCS_UPCE),
                new StrVal("UPC-E SUP", POSPrinterConst.PTR_BCS_UPCE_S),
                new StrVal("UPC-D1", POSPrinterConst.PTR_BCS_UPCD1),
                new StrVal("UPC-D2", POSPrinterConst.PTR_BCS_UPCD2),
                new StrVal("UPC-D3", POSPrinterConst.PTR_BCS_UPCD3),
                new StrVal("UPC-D4", POSPrinterConst.PTR_BCS_UPCD4),
                new StrVal("UPC-D5", POSPrinterConst.PTR_BCS_UPCD5),
                new StrVal("EAN 8 / JAN 8", POSPrinterConst.PTR_BCS_EAN8),
                new StrVal("EAN 8 SUP", POSPrinterConst.PTR_BCS_EAN8_S),
                new StrVal("EAN 13 / JAN 13", POSPrinterConst.PTR_BCS_EAN13),
                new StrVal("EAN 13 SUP", POSPrinterConst.PTR_BCS_EAN13_S),
                new StrVal("EAN 128",  POSPrinterConst.PTR_BCS_EAN128),
                new StrVal("2 of 5",  POSPrinterConst.PTR_BCS_TF),
                new StrVal("Int. 2 of 5",  POSPrinterConst.PTR_BCS_ITF),
                new StrVal("Codabar",  POSPrinterConst.PTR_BCS_Codabar),
                new StrVal("Code 39",  POSPrinterConst.PTR_BCS_Code39),
                new StrVal("Code 93",  POSPrinterConst.PTR_BCS_Code93),
                new StrVal("Code 128", POSPrinterConst.PTR_BCS_Code128),
                new StrVal("OCR \"A\"", POSPrinterConst.PTR_BCS_OCRA),
                new StrVal("OCR \"B\"", POSPrinterConst.PTR_BCS_OCRB),
                new StrVal("PDF 417", POSPrinterConst.PTR_BCS_PDF417),
                new StrVal("MAXICODE", POSPrinterConst.PTR_BCS_MAXICODE),
    };
    
    StrVal[] bcAlignments = {
        new StrVal("Left", POSPrinterConst.PTR_BC_LEFT),
                new StrVal("Center", POSPrinterConst.PTR_BC_CENTER),
                new StrVal("Right", POSPrinterConst.PTR_BC_RIGHT),
    };
    
    StrVal[] bcTextPos = {
        new StrVal("None", POSPrinterConst.PTR_BC_TEXT_NONE),
                new StrVal("Above", POSPrinterConst.PTR_BC_TEXT_ABOVE),
                new StrVal("Below", POSPrinterConst.PTR_BC_TEXT_BELOW),
    };
    
    StrVal[] bitmapWidths = {
        new StrVal("One pixel per printer dot", POSPrinterConst.PTR_BM_ASIS),
    };
    
    StrVal[] bitmapAlignments = {
        new StrVal("Left", POSPrinterConst.PTR_BM_LEFT),
                new StrVal("Center", POSPrinterConst.PTR_BM_CENTER),
                new StrVal("Right", POSPrinterConst.PTR_BM_RIGHT),
    };
    
    StrVal[] rotationValues = {
        new StrVal("No Rotation", POSPrinterConst.PTR_RP_NORMAL),
                new StrVal("Rotate Right 90", POSPrinterConst.PTR_RP_RIGHT90),
                new StrVal("Rotate Left 90", POSPrinterConst.PTR_RP_LEFT90),
                new StrVal("Rotate 180", POSPrinterConst.PTR_RP_ROTATE180),
    };
    
    StrVal[] mapModeValues = {
    		new StrVal("Dots", POSPrinterConst.PTR_MM_DOTS),
    		new StrVal("Metric", POSPrinterConst.PTR_MM_METRIC),
    		new StrVal("English", POSPrinterConst.PTR_MM_ENGLISH),
    		new StrVal("Twips", POSPrinterConst.PTR_MM_TWIPS),
    };
    
    StrVal[] pmDirections = {
    		new StrVal("Left to Right", POSPrinterConst.PTR_PD_LEFT_TO_RIGHT),
    		new StrVal("Bottom to Top", POSPrinterConst.PTR_PD_BOTTOM_TO_TOP),
    		new StrVal("Right to Left", POSPrinterConst.PTR_PD_RIGHT_TO_LEFT),
    		new StrVal("Top to Bottom", POSPrinterConst.PTR_PD_TOP_TO_BOTTOM),
    };
    
    StrVal[] pmStations = {
    		new StrVal("None", 0),
    		new StrVal("Reciept", POSPrinterConst.PTR_S_RECEIPT),
    		new StrVal("Slip", POSPrinterConst.PTR_S_SLIP),
    };
    
    StrVal[] pmpCommands = {
    		new StrVal("Enter Page Mode", POSPrinterConst.PTR_PM_PAGE_MODE),
    		new StrVal("Print and Save Canvas", POSPrinterConst.PTR_PM_PRINT_SAVE),
    		new StrVal("Print and Destroy Canvas", POSPrinterConst.PTR_PM_NORMAL),
    		new StrVal("Clear Page and Exit Page Mode", POSPrinterConst.PTR_PM_CANCEL),
    };
    
    MethodListener methodListener = new MethodListener();
    
    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        mainButtonPanel = new MainButtonPanel(methodListener,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
        
        
        
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        
        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        
        JPanel devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        
        
        deviceEnabledCB = new JCheckBox("Device enabled");
        propPanel.add(deviceEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        propPanel.add(freezeEventsCB);
        
        asyncCB = new JCheckBox("Asynchronus Mode");
        propPanel.add(asyncCB);
        
        idleCB = new JCheckBox("Flag when idle");
        propPanel.add(idleCB);
        
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
        
        
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        asyncCB.addItemListener(cbListener);
        idleCB.addItemListener(cbListener);
        
        StationListener stationListener = new StationListener();
        
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        JLabel label = new JLabel("Station: ");
        radioPanel.add(label);
        JRadioButton rButton = new JRadioButton("Receipt");
        rButton.setActionCommand("receipt");
        rButton.addActionListener(stationListener);
        rButton.setSelected(true);
        JRadioButton jButton = new JRadioButton("Journal");
        jButton.setActionCommand("journal");
        jButton.addActionListener(stationListener);
        JRadioButton sButton = new JRadioButton("Slip");
        sButton.setActionCommand("slip");
        sButton.addActionListener(stationListener);
        
        station = POSPrinterConst.PTR_S_RECEIPT;
        
        
        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(rButton);
        group.add(jButton);
        group.add(sButton);
        
        radioPanel.add(rButton);
        radioPanel.add(jButton);
        radioPanel.add(sButton);
        
        rotationCombo = new JComboBox(rotationValues);
        rotationCombo.setMaximumSize(new Dimension(180,20));
        rotationCombo.setPreferredSize(new Dimension(180,20));
        rotationCombo.setActionCommand("rotationCombo");
        rotationCombo.addActionListener(methodListener);
        
        radioPanel.add(Box.createHorizontalGlue());
        radioPanel.add(rotationCombo);
        
        mapModeCombo = new JComboBox(mapModeValues);
        mapModeCombo.setMaximumSize(new Dimension(100,20));
        mapModeCombo.setPreferredSize(new Dimension(100,20));
        mapModeCombo.setActionCommand("mapModeCombo");
        mapModeCombo.addActionListener(methodListener);
        
        label = new JLabel("Map Mode: ");
        radioPanel.add(Box.createHorizontalGlue());
        radioPanel.add(label);
        radioPanel.add(mapModeCombo);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        GeneralPrintingPanel gpp = new GeneralPrintingPanel();
        tabbedPane.addTab("General Printing", null, gpp.make(), "General Printing");
        
        BarcodePanel bcp = new BarcodePanel();
        tabbedPane.addTab("Barcode", null, bcp.make(), "Barcode");
        
        BitmapPanel bmp = new BitmapPanel();
        tabbedPane.addTab("Bitmap", null, bmp.make(), "Bitmap");
        
        PageModePanel pmp = new PageModePanel();
        tabbedPane.addTab("Page Mode", null, pmp.make(), "Pagemode");
        
        messages = new JTextArea(5,5);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setEditable(false);
        scrollPane = new JScrollPane(messages);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        devicePanel.add(radioPanel);
        devicePanel.add(tabbedPane);
        label = new JLabel("Device Messages:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        devicePanel.add(label);
        devicePanel.add(scrollPane);
        
        subPanel.add(devicePanel);
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    /** Listens to the method buttons. */
    
    class MethodListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            mainButtonPanel.action(ae);
            String logicalName = mainButtonPanel.getLogicalName();
            if(ae.getActionCommand().equals("open")){
                try{
                    if(logicalName.equals("")){
                        logicalName = defaultLogicalName;
                    }
                    
                    posPrinter.open(logicalName);
                    posPrinter.addStatusUpdateListener(eventListener);
                    posPrinter.addOutputCompleteListener(eventListener);
                    posPrinter.addErrorListener(eventListener);
                    posPrinter.addDirectIOListener(eventListener);
                    mapModeCombo.setSelectedIndex(0);
                    int version = posPrinter.getDeviceServiceVersion();
                    if(version >= 1009000) {
                        ver_19_complient = true;
                        ver_18_complient = true;
                        ver_17_complient = true;
                    }
                    if(version >= 1008000) {
                        ver_18_complient = true;
                        ver_17_complient = true;
                    }
                    if(version >= 1007000) {
                        ver_17_complient = true;
                    }
                    
                    if(ver_19_complient)
                    {
                    	doUpdatePageModeProps();
                    }
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("claim")){
                try{
                    posPrinter.claim(0);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("release")){
                try{
                    posPrinter.release();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("close")){
                try{
                    posPrinter.close();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            
            else if(ae.getActionCommand().equals("printNormal")){
                try{
                    String text = posPrinterOutputArea.getText();
                    posPrinter.printNormal(station, text);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in printNormal(): " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    //System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("cutPaper")){
                try{
                    posPrinter.cutPaper(90);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in cutPaper(): " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    // System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("printBarCode")){
                try{
                    int index = symbologyCombo.getSelectedIndex();
                    int symbology = ((StrVal)(Object)symbologyCombo.getItemAt(index)).value;
                    
                    int height =Integer.parseInt( barCodeHeight.getText() );
                    int width = Integer.parseInt( barCodeWidth.getText() );
                    
                    index = barCodeAlignmentCombo.getSelectedIndex();
                    int alignment;
                    
                    if(index == -1){
                        alignment = Integer.parseInt((String)barCodeAlignmentCombo.getSelectedItem());
                    }else{
                        alignment =((StrVal)(Object)barCodeAlignmentCombo.getSelectedItem()).value;
                    }
                    
                    
                    index = barCodeTextCombo.getSelectedIndex();
                    int textPos =((StrVal)(Object)barCodeTextCombo.getItemAt(index)).value;
                    
                    posPrinter.printBarCode(station, barCodeData.getText(), symbology, height, width, alignment, textPos);
                    
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(null, "Make sure your barcode width & height are valid numbers", "Number Format Exception", JOptionPane.ERROR_MESSAGE);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in printBarCode(): " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            else if(ae.getActionCommand().equals("info")){
                try{
                    final JFrame infoFrame = new JFrame("POStest");
                    infoFrame.setSize(700, 500);
                    
                    JPanel mainPanel = new JPanel();
                    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                    
                    JTabbedPane tabbedPane = new JTabbedPane();
                    
                    JPanel generalInfo = new JPanel();
                    JPanel genCaps = new JPanel();
                    JPanel jrnCaps = new JPanel();
                    JPanel recCaps = new JPanel();
                    JPanel slpCaps = new JPanel();
                    
                    generalInfo.setLayout(new BoxLayout(generalInfo, BoxLayout.Y_AXIS));
                    generalInfo.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    String ver = new Integer(posPrinter.getDeviceServiceVersion()).toString();
                    generalInfo.add( new JLabel( "Service Description: " + posPrinter.getDeviceServiceDescription()) );
                    generalInfo.add( new JLabel("Service Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7))));
                    generalInfo.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    ver = new Integer(posPrinter.getDeviceControlVersion()).toString();
                    generalInfo.add( new JLabel("Control Description: " + posPrinter.getDeviceControlDescription()));
                    generalInfo.add( new JLabel("Control Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7))));
                    generalInfo.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    generalInfo.add( new JLabel("Physical Device Name: " + posPrinter.getPhysicalDeviceName()));
                    generalInfo.add( new JLabel("Physical Device Description: " + posPrinter.getPhysicalDeviceDescription()));
                    
                    genCaps.setLayout(new BoxLayout(genCaps, BoxLayout.Y_AXIS));
                    genCaps.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    genCaps.add( new JLabel("CapPowerReporting: " + (posPrinter.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (posPrinter.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"))));
                    
                    if(ver_18_complient)
                    {
                    	genCaps.add( new JLabel("CapStatisticsReporting: " + posPrinter.getCapStatisticsReporting()));                    
                    	genCaps.add( new JLabel("CapUpdateStatistics: " + posPrinter.getCapUpdateStatistics()));
                    }
                    else
                    {
                    	genCaps.add( new JLabel("CapStatisticsReporting: Service Object is not 1.8 complient"));                    
                    	genCaps.add( new JLabel("CapUpdateStatistics: Service Object is not 1.8 complient"));
                    }
                    
                    if(ver_19_complient)
                    {
                    	genCaps.add( new JLabel("CapCompareFirmwareVersion: " + posPrinter.getCapCompareFirmwareVersion()));                    
                    	genCaps.add( new JLabel("CapUpdateFirmware: " + posPrinter.getCapUpdateFirmware()));
                    }
                    else
                    {
                    	genCaps.add( new JLabel("CapCompareFirmwareVersion: Service Object is not 1.8 complient"));                    
                    	genCaps.add( new JLabel("CapUpdateFirmware: Service Object is not 1.8 complient"));
                    }
                    
                    genCaps.add( new JLabel("CapCharacterSet: "+ getCharSetName(posPrinter.getCapCharacterSet())));
                    genCaps.add( new JLabel("CapConcurrentJrnRec: " + posPrinter.getCapConcurrentJrnRec()));
                    genCaps.add( new JLabel("CapConcurrentJrnSlp: "+ posPrinter.getCapConcurrentJrnSlp()));
                    genCaps.add( new JLabel("CapConcurrentRecSlp: "+ posPrinter.getCapConcurrentRecSlp()));
                    if(ver_19_complient)
                    {
                    	genCaps.add( new JLabel("CapConcurentPageMode: " + posPrinter.getCapConcurrentPageMode()));
                    }
                    else
                    {
                    	genCaps.add( new JLabel("CapConcurentPageMode: Service Object is not 1.9 complient"));
                    }
                    genCaps.add( new JLabel("CapCoverSensor: " + posPrinter.getCapCoverSensor()));
                    
                    if(ver_17_complient)
                    {
                    	genCaps.add( new JLabel("CapMapCharacterSet: " + posPrinter.getCapMapCharacterSet()));
                    }
                    else
                    {
                    	genCaps.add( new JLabel("CapMapCharacterSet: Service Object is not 1.8 complient"));
                    }
                    
                    genCaps.add( new JLabel("CapTransaction: " + posPrinter.getCapMapCharacterSet()));
                    
                    
                    jrnCaps.setLayout(new BoxLayout(jrnCaps, BoxLayout.Y_AXIS));
                    jrnCaps.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    jrnCaps.add( new JLabel("CapJrnPresent: " + posPrinter.getCapJrnPresent()));
                    jrnCaps.add( new JLabel("CapJrn2Color: " + posPrinter.getCapJrn2Color()));
                    jrnCaps.add( new JLabel("CapJrnBold: " + posPrinter.getCapJrnBold()));
                    jrnCaps.add( new JLabel("CapJrnDhigh: " + posPrinter.getCapJrnDhigh()));
                    jrnCaps.add( new JLabel("CapJrnDwide: " + posPrinter.getCapJrnDwide()));
                    jrnCaps.add( new JLabel("CapJrnDwideDhigh: " + posPrinter.getCapJrnDwideDhigh()));
                    jrnCaps.add( new JLabel("CapJrnEmptySensor: " + posPrinter.getCapJrnEmptySensor()));
                    jrnCaps.add( new JLabel("CapJrnItalic: " + posPrinter.getCapJrnItalic()));
                    jrnCaps.add( new JLabel("CapJrnNearEndSensor: " + posPrinter.getCapJrnNearEndSensor()));
                    jrnCaps.add( new JLabel("CapJrnUnderline: " + posPrinter.getCapJrnUnderline()));
                    jrnCaps.add( new JLabel("CapJrnCartridgeSensor: " + getCartridgeSensorName(posPrinter.getCapJrnCartridgeSensor())));
                    jrnCaps.add( new JLabel("CapJrnColor: " + getColorName(posPrinter.getCapJrnColor())));
                    
                    recCaps.setLayout(new BoxLayout(recCaps, BoxLayout.Y_AXIS));
                    recCaps.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    recCaps.add( new JLabel("CapRecPresent: " + posPrinter.getCapRecPresent()));
                    recCaps.add( new JLabel("CapRec2Color: " + posPrinter.getCapRec2Color()));
                    recCaps.add( new JLabel("CapRecBarCode: " + posPrinter.getCapRecBarCode()));
                    recCaps.add( new JLabel("CapRecBitmap: " + posPrinter.getCapRecBitmap()));
                    recCaps.add( new JLabel("CapRecBold: " + posPrinter.getCapRecBold()));
                    recCaps.add( new JLabel("CapRecDhigh: " + posPrinter.getCapRecDhigh()));
                    recCaps.add( new JLabel("CapRecDwide: " + posPrinter.getCapRecDwide()));
                    recCaps.add( new JLabel("CapRecDwideDhigh: " + posPrinter.getCapRecDwideDhigh()));
                    recCaps.add( new JLabel("CapRecEmptySensor: " + posPrinter.getCapRecEmptySensor()));
                    recCaps.add( new JLabel("CapRecItalic: " + posPrinter.getCapRecItalic()));
                    recCaps.add( new JLabel("CapRecLeft90: " + posPrinter.getCapRecLeft90()));
                    recCaps.add( new JLabel("CapRecNearEndSensor: " + posPrinter.getCapRecNearEndSensor()));
                    recCaps.add( new JLabel("CapRecPapercut: " + posPrinter.getCapRecPapercut()));
                    recCaps.add( new JLabel("CapRecRight90: " + posPrinter.getCapRecRight90()));
                    recCaps.add( new JLabel("CapRecRotate180: " + posPrinter.getCapRecRotate180()));
                    recCaps.add( new JLabel("CapRecStamp: " + posPrinter.getCapRecStamp()));
                    recCaps.add( new JLabel("CapRecUnderline: " + posPrinter.getCapRecUnderline()));
                    recCaps.add( new JLabel("CapRecCartridgeSensor: " + getCartridgeSensorName(posPrinter.getCapRecCartridgeSensor())));
                    recCaps.add( new JLabel("CapRecColor: " + getColorName(posPrinter.getCapRecColor())));
                    recCaps.add( new JLabel("CapRecMarkFeed: " + getMarkFeedName(posPrinter.getCapRecMarkFeed())));
                    if(ver_19_complient)
                    {
                    	recCaps.add( new JLabel("CapRecPageMode: " + posPrinter.getCapRecPageMode()));
                    }
                    else
                    {
                    	recCaps.add( new JLabel("CapRecPageMode: Service Object is not 1.9 complient"));
                    }
                    slpCaps.setLayout(new BoxLayout(slpCaps, BoxLayout.Y_AXIS));
                    slpCaps.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    slpCaps.add( new JLabel("CapSlpPresent: " + posPrinter.getCapSlpPresent()));
                    slpCaps.add( new JLabel("CapSlp2Color: " + posPrinter.getCapSlp2Color()));
                    slpCaps.add( new JLabel("CapSlpBarCode: " + posPrinter.getCapSlpBarCode()));
                    slpCaps.add( new JLabel("CapSlpBitmap: " + posPrinter.getCapSlpBitmap()));
                    slpCaps.add( new JLabel("CapSlpBold: " + posPrinter.getCapSlpBold()));
                    slpCaps.add( new JLabel("CapSlpDhigh: " + posPrinter.getCapSlpDhigh()));
                    slpCaps.add( new JLabel("CapSlpDwide: " + posPrinter.getCapSlpDwide()));
                    slpCaps.add( new JLabel("CapSlpDwideDhigh: " + posPrinter.getCapSlpDwideDhigh()));
                    slpCaps.add( new JLabel("CapSlpEmptySensor: " + posPrinter.getCapSlpEmptySensor()));
                    slpCaps.add( new JLabel("CapSlpItalic: " + posPrinter.getCapSlpItalic()));
                    slpCaps.add( new JLabel("CapSlpLeft90: " + posPrinter.getCapSlpLeft90()));
                    slpCaps.add( new JLabel("CapSlpNearEndSensor: " + posPrinter.getCapSlpNearEndSensor()));
                    slpCaps.add( new JLabel("CapSlpRight90: " + posPrinter.getCapSlpRight90()));
                    slpCaps.add( new JLabel("CapSlpRotate180: " + posPrinter.getCapSlpRotate180()));
                    slpCaps.add( new JLabel("CapSlpUnderline: " + posPrinter.getCapSlpUnderline()));
                    slpCaps.add( new JLabel("CapSlpBothSidesPrint: " + posPrinter.getCapSlpBothSidesPrint()));
                    slpCaps.add( new JLabel("CapSlpCartridgeSensor: " + getCartridgeSensorName(posPrinter.getCapSlpCartridgeSensor())));
                    slpCaps.add( new JLabel("CapSlpColor: " + getColorName(posPrinter.getCapSlpColor())));
                    if(ver_19_complient)
                    {
                    	slpCaps.add( new JLabel("CapSlpPageMode: " + posPrinter.getCapSlpPageMode()));
                    }
                    else
                    {
                    	slpCaps.add( new JLabel("CapSlpPageMode: Service Object is not 1.9 complient"));
                    }
                    tabbedPane.addTab("General Info", null, generalInfo,  "GeneralInfo");
                    tabbedPane.addTab("General Capabilities", null, genCaps,  "GeneralCapabilities");
                    tabbedPane.addTab("Journal Capabilities", null, jrnCaps,  "JournalCapabilities");
                    tabbedPane.addTab("Receipt Capabilities", null, recCaps,  "ReceiptCapabilities");
                    tabbedPane.addTab("Slip Capabilities", null, slpCaps,  "SlipCapabilities");
                    
                    JButton okButton = new JButton("Close");
                    okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    okButton.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae){
                            infoFrame.setVisible(false);
                        }
                    });
                    
                    mainPanel.add(tabbedPane);
                    mainPanel.add(Box.createRigidArea(new Dimension(5,10)));
                    mainPanel.add(okButton);
                    mainPanel.add(Box.createRigidArea(new Dimension(5,10)));
                    
                    infoFrame.getContentPane().add(mainPanel);
                    infoFrame.setVisible(true);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("bitmapBrowse")){
                try{
                    int retVal = bitmapFileChooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION){
                        bitmapFileName.setText(bitmapFileChooser.getSelectedFile().toURI().toString());
                    }
                } catch(Exception e){
                    
                }
            } else if(ae.getActionCommand().equals("printBitmap")){
                try{
                    int width;
                    int alignment;
                    String filename = bitmapFileName.getText();
                    int index = bitmapWidth.getSelectedIndex();
                    
                    if(index == -1){
                        width = Integer.parseInt((String)bitmapWidth.getSelectedItem());
                    }else{
                        width = ((StrVal)bitmapWidth.getSelectedItem()).value;
                    }
                    
                    index = bitmapAlign.getSelectedIndex();
                    
                    if(index == -1){
                        alignment = Integer.parseInt((String)bitmapAlign.getSelectedItem());
                    }else{
                        alignment = ((StrVal)bitmapAlign.getSelectedItem()).value;
                    }
                    
                    posPrinter.printBitmap(station, filename, width, alignment);
                } catch(JposException e){
                    errorDialog(e, "printBitmap");
                }
            } else if(ae.getActionCommand().equals("beginInsertion")){
                try{
                    posPrinter.beginInsertion(-1);
                } catch(JposException e){
                    errorDialog(e, "beginInsertion");
                }
            } else if(ae.getActionCommand().equals("endInsertion")){
                try{
                    posPrinter.endInsertion();
                } catch(JposException e){
                    errorDialog(e, "endInsertion");
                }
            } else if(ae.getActionCommand().equals("beginRemoval")){
                try{
                    posPrinter.beginRemoval(-1);
                } catch(JposException e){
                    errorDialog(e, "beginRemoval");
                }
            } else if(ae.getActionCommand().equals("endRemoval")){
                try{
                    posPrinter.endRemoval();
                } catch(JposException e){
                    errorDialog(e, "endRemoval");
                }
            } else if(ae.getActionCommand().equals("printImmediate")){
                try{
                    posPrinter.printImmediate(station, posPrinterOutputArea.getText());
                } catch(JposException e){
                    errorDialog(e, "printImmediate");
                }
            } else if(ae.getActionCommand().equals("setBitmap")){
                try{
                    int width;
                    int alignment;
                    String filename = bitmapFileName.getText();
                    int index = bitmapWidth.getSelectedIndex();
                    int number = Integer.parseInt((String)bitmapNumberCombo.getSelectedItem());
                    
                    if(index == -1){
                        width = Integer.parseInt((String)bitmapWidth.getSelectedItem());
                    }else{
                        width = ((StrVal)bitmapWidth.getSelectedItem()).value;
                    }
                    
                    index = bitmapAlign.getSelectedIndex();
                    
                    if(index == -1){
                        alignment = Integer.parseInt((String)bitmapAlign.getSelectedItem());
                    }else{
                        alignment = ((StrVal)bitmapAlign.getSelectedItem()).value;
                    }
                    
                    posPrinter.setBitmap(number, station, filename, width, alignment);
                } catch(JposException e){
                    errorDialog(e, "setBitmap");
                }
            } else if(ae.getActionCommand().equals("addESC")){
                byte str[] = new byte[] {0x1b, '|',};
                posPrinterOutputArea.append(new String(str));
            } else if(ae.getActionCommand().equals("topLogo")){
                try{
                    posPrinter.setLogo(POSPrinterConst.PTR_L_TOP, posPrinterOutputArea.getText());
                } catch(JposException e){
                    errorDialog(e, "setLogo");
                }
            } else if(ae.getActionCommand().equals("bottomLogo")){
                try{
                    posPrinter.setLogo(POSPrinterConst.PTR_L_BOTTOM, posPrinterOutputArea.getText());
                } catch(JposException e){
                    errorDialog(e, "setLogo");
                }
            } else if(ae.getActionCommand().equals("validateData")){
                try{
                    posPrinter.validateData(station, posPrinterOutputArea.getText());
                } catch(JposException e){
                    errorDialog(e, "validateData");
                }
            } else if(ae.getActionCommand().equals("rotationCombo")){
                try{
                    int value = ((StrVal)rotationCombo.getSelectedItem()).value;
                    posPrinter.rotatePrint(station, value);
                } catch(JposException e){
                    errorDialog(e, "rotatePrint");
                }
            } else if(ae.getActionCommand().equals("rotateBarcode")){
                try{
                    int value = ((StrVal)rotationCombo.getSelectedItem()).value;
                    posPrinter.rotatePrint(station, value | POSPrinterConst.PTR_RP_BARCODE);
                } catch(JposException e){
                    errorDialog(e, "rotatePrint");
                }
            }else if(ae.getActionCommand().equals("mapModeCombo")){
                try{
                    int value = ((StrVal)mapModeCombo.getSelectedItem()).value;
                    posPrinter.setMapMode(value);
                    if(ver_19_complient){
                    	doUpdatePageModeProps();
                    }
                } catch(JposException e){
                    errorDialog(e, "setMapMode");
                }
            } else if(ae.getActionCommand().equals("rotateBitmap")){
                try{
                    int value = ((StrVal)rotationCombo.getSelectedItem()).value;
                    posPrinter.rotatePrint(station, value | POSPrinterConst.PTR_RP_BITMAP);
                } catch(JposException e){
                    errorDialog(e, "rotatePrint");
                }
            }else if(ae.getActionCommand().equals("stats")) {
                try{
                    StatisticsDialog dlg = new StatisticsDialog(posPrinter);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
                
            } else if(ae.getActionCommand().equals("firmware")) {
                try{
                    FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(posPrinter);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else if(ae.getActionCommand().equals("letterQuality")) {
                try{
                	if(station == POSPrinterConst.PTR_S_RECEIPT){
                		posPrinter.setRecLetterQuality(!posPrinter.getRecLetterQuality());
                		updateMessages("set RecLetterQuality to " + posPrinter.getRecLetterQuality());
                	}
                	else if(station == POSPrinterConst.PTR_S_SLIP){
                		posPrinter.setSlpLetterQuality(!posPrinter.getSlpLetterQuality());
                		updateMessages("set SlpLetterQuality to " + posPrinter.getSlpLetterQuality());
                	}
                	if(station == POSPrinterConst.PTR_S_JOURNAL){
                		posPrinter.setJrnLetterQuality(!posPrinter.getJrnLetterQuality());
                		updateMessages("set JrnLetterQuality to " + posPrinter.getJrnLetterQuality());
                	}
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else if(ae.getActionCommand().equals("pageModeUpdateProp")) {
                try{
                	//Only update the rest of the properties if we have set a valid station
                	if(posPrinter.getPageModeStation() != 0)
                	{
	                	posPrinter.setPageModeHorizontalPosition(Integer.parseInt(pageModeHorizontalPos.getText()));
	                	posPrinter.setPageModePrintArea(pageModePrintArea.getText());
	                	posPrinter.setPageModePrintDirection(((StrVal)pageModeDirectionCombo.getSelectedItem()).value);
	                	posPrinter.setPageModeVerticalPosition(Integer.parseInt(pageModeVerticalPos.getText()));
                	}
                	doUpdatePageModeProps();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }else if(ae.getActionCommand().equals("clearPrintArea")) {
                try{
                	posPrinter.clearPrintArea();                	
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }else if(ae.getActionCommand().equals("pageModePrint")) {
                try{
                	posPrinter.pageModePrint(((StrVal)pageModeControlCombo.getSelectedItem()).value);                	
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }else if(ae.getActionCommand().equals("pageModeStationCombo")) {
                try{
                	posPrinter.setPageModeStation(((StrVal)pageModeStationCombo.getSelectedItem()).value);
                	doUpdatePageModeProps();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                if(posPrinter.getState() != JposConst.JPOS_S_CLOSED){
                    updateDevice = false;
                    if( posPrinter.getClaimed()){
                        deviceEnabledCB.setSelected(posPrinter.getDeviceEnabled());
                    }
                    freezeEventsCB.setSelected(posPrinter.getFreezeEvents());
                    asyncCB.setSelected(posPrinter.getAsyncMode());
                    updateDevice = true;
                }
            } catch(JposException je) {
                System.err.println("POSPrinterPanel: ActionPerformedmethod received JposException: "+ je);
            }
        }
    }
    
    private void updateMessages(String msg){
    	messages.append(msg + "\n");
        scrollPane.getVerticalScrollBar().setValue(messages.getHeight() - messages.getVisibleRect().height);
    }
    
    private void doUpdatePageModeProps() throws JposException
    {
    	int num;
    	String str = "";
    	pageModeArea.setText(posPrinter.getPageModeArea());
    	num = posPrinter.getPageModeDescriptor();
    	if((num & POSPrinterConst.PTR_PM_BARCODE) > 0){
    		str += "Barcode ";
    	}
    	if((num & POSPrinterConst.PTR_PM_BC_ROTATE) > 0){
    		str += "Barcode_Rotate ";
    	}
    	if((num & POSPrinterConst.PTR_PM_BITMAP) > 0){
    		str += "Bitmap ";
    	}
    	if((num & POSPrinterConst.PTR_PM_BM_ROTATE) > 0){
    		str += "Bitmap_Rotate ";
    	}
    	if((num & POSPrinterConst.PTR_PM_OPAQUE) > 0){
    		str += "Opaque ";
    	}
        pageModeDescriptor.setText(str);
        pageModeHorizontalPos.setText(Integer.toString(posPrinter.getPageModeHorizontalPosition()));
        pageModeVerticalPos.setText(Integer.toString(posPrinter.getPageModeVerticalPosition()));;
        pageModePrintArea.setText(posPrinter.getPageModePrintArea());        
        pageModeDirectionCombo.setSelectedIndex(getComboBoxIndex(pageModeDirectionCombo, posPrinter.getPageModePrintDirection()));
        pageModeStationCombo.setSelectedIndex(getComboBoxIndex(pageModeStationCombo, posPrinter.getPageModeStation()));
        
    }
    public static String getCharSetName(int id) {
        String value = "Unknown";
        switch (id){
            case POSPrinterConst.PTR_CCS_ALPHA:
                value = "PTR_CCS_ALPHA";
                break;
            case POSPrinterConst.PTR_CCS_ASCII:
                value = "PTR_CCS_ASCII";
                break;
            case POSPrinterConst.PTR_CCS_KANA:
                value = "PTR_CCS_KANA";
                break;
            case POSPrinterConst.PTR_CCS_KANJI:
                value = "PTR_CCS_KANJI";
                break;
            case POSPrinterConst.PTR_CCS_UNICODE:
                value = "PTR_CCS_UNICODE";
                break;
        }
        return value;
    }
    
    public static String getCartridgeSensorName(int id){
        String value = "";
        if ((id & POSPrinterConst.PTR_CART_REMOVED) > 0){
            value += "PTR_CART_REMOVED | ";
        }
        if ((id & POSPrinterConst.PTR_CART_EMPTY) > 0){
            value += "PTR_CART_EMPTY | ";
        }
        if ((id & POSPrinterConst.PTR_CART_CLEANING) > 0){
            value += "PTR_CART_CLEANING | ";
        }
        if ((id & POSPrinterConst.PTR_CART_NEAREND) > 0){
            value += "PTR_CART_NEAREND";
        }
        if(id == 0){
            value="No cartridge sensing.";
        }
        return value;
    }
    
    public static String getColorName(int id){
        String value="";
        
        if ((id & POSPrinterConst.PTR_COLOR_PRIMARY) > 0){
            value += "PTR_COLOR_PRIMARY";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM1) > 0){
            value += " | PTR_COLOR_CUSTOM1";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM2) > 0){
            value += " | PTR_COLOR_CUSTOM2";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM3) > 0){
            value += " | PTR_COLOR_CUSTOM3";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM4) > 0){
            value += " | PTR_COLOR_CUSTOM4";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM5) > 0){
            value += " | PTR_COLOR_CUSTOM5";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CUSTOM6) > 0){
            value += " | PTR_COLOR_CUSTOM6";
        }
        if ((id & POSPrinterConst.PTR_COLOR_CYAN) > 0){
            value += " | PTR_COLOR_CYAN";
        }
        if ((id & POSPrinterConst.PTR_COLOR_MAGENTA) > 0){
            value += " | PTR_COLOR_MAGENTA";
        }
        if ((id & POSPrinterConst.PTR_COLOR_YELLOW) > 0){
            value += " | PTR_COLOR_YELLOW";
        }
        if ((id & POSPrinterConst.PTR_COLOR_FULL) > 0){
            value += " | PTR_COLOR_FULL";
        }
        
        return value;
    }
    
    public static String getMarkFeedName(int id){
        String value = "";
        
        if ((id & POSPrinterConst.PTR_MF_TO_TAKEUP) > 0){
            value += "PTR_MF_TO_TAKEUP | ";
        }
        if ((id & POSPrinterConst.PTR_MF_TO_CUTTER) > 0){
            value += "PTR_MF_TO_CUTTER | ";
        }
        if ((id & POSPrinterConst.PTR_MF_TO_CURRENT_TOF) > 0){
            value += "PTR_MF_TO_CURRENT_TOF | ";
        }
        if ((id & POSPrinterConst.PTR_MF_TO_NEXT_TOF) > 0){
            value += "PTR_MF_TO_NEXT_TOF";
        }
        
        if( id == 0){
            value = "Paper handling not supported";
        }
        return value;
    }
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if(updateDevice) {
                
                Object source = e.getItemSelectable();
                
                if (source == deviceEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setDeviceEnabled(false);
                        }else{
                            posPrinter.setDeviceEnabled(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "Exception in setDeviceEnabled: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (source == freezeEventsCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setFreezeEvents(false);
                        }else{
                            posPrinter.setFreezeEvents(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "Exception in setFreezeEvents: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else if(source == asyncCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setAsyncMode(false);
                        }else{
                            posPrinter.setAsyncMode(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "Exception in setAsyncMode: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else if(source == idleCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setFlagWhenIdle(false);
                        }else{
                            posPrinter.setFlagWhenIdle(true);
                        }
                    } catch(JposException je) {
                        JOptionPane.showMessageDialog(null, "Exception in setFlagWhenIdle: "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    
    
    /** Listens to the station radio buttons. */
    class StationListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("receipt")){station = POSPrinterConst.PTR_S_RECEIPT;}
            if(e.getActionCommand().equals("journal")){station = POSPrinterConst.PTR_S_JOURNAL;}
            if(e.getActionCommand().equals("slip")){station = POSPrinterConst.PTR_S_SLIP;}
        }
    }
    
    class BarcodePanel extends Component {
 
		private static final long serialVersionUID = 5645979411019493408L;

		public Component make() {
            JPanel mainBox = new JPanel();
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.X_AXIS));
            
            JPanel labelBox = new JPanel();
            labelBox.setLayout(new BoxLayout(labelBox, BoxLayout.Y_AXIS));
            
            JLabel label = new JLabel("Height: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            label = new JLabel("Width: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            label = new JLabel("Symbology: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            label = new JLabel("Alignment: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            label = new JLabel("Text Position: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            label = new JLabel("Barcode Data: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,20));
            label.setPreferredSize(new Dimension(110,20));
            labelBox.add(label);
            
            
            JPanel valuesBox = new JPanel();
            valuesBox.setLayout(new BoxLayout(valuesBox, BoxLayout.Y_AXIS));
            
            barCodeHeight = new JTextField(5);
            barCodeHeight.setAlignmentX(Component.LEFT_ALIGNMENT);
            barCodeHeight.setMaximumSize(new Dimension(80,20));
            barCodeHeight.setPreferredSize(new Dimension(80,20));
            
            barCodeWidth= new JTextField(5);
            barCodeWidth.setAlignmentX(Component.LEFT_ALIGNMENT);
            barCodeWidth.setMaximumSize(new Dimension(80,20));
            barCodeWidth.setPreferredSize(new Dimension(80,20));
            
            barCodeAlignmentCombo = new JComboBox(bcAlignments);
            barCodeAlignmentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            barCodeAlignmentCombo.setMaximumSize(new Dimension(110,20));
            barCodeAlignmentCombo.setPreferredSize(new Dimension(110,20));
            barCodeAlignmentCombo.setEditable(true);
            
            barCodeTextCombo = new JComboBox(bcTextPos);
            barCodeTextCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            barCodeTextCombo.setMaximumSize(new Dimension(110,20));
            barCodeTextCombo.setPreferredSize(new Dimension(110,20));
            
            symbologyCombo = new JComboBox(bcSymbologies);
            symbologyCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            symbologyCombo.setMaximumSize(new Dimension(110,20));
            symbologyCombo.setPreferredSize(new Dimension(110,20));
            
            barCodeData = new JTextField();
            barCodeData.setAlignmentX(Component.LEFT_ALIGNMENT);
            barCodeData.setMaximumSize(new Dimension(150,20));
            barCodeData.setPreferredSize(new Dimension(150,20));
            
            
            valuesBox.add(barCodeHeight);
            valuesBox.add(barCodeWidth);
            valuesBox.add(symbologyCombo);
            valuesBox.add(barCodeAlignmentCombo);
            valuesBox.add(barCodeTextCombo);
            valuesBox.add(barCodeData);
            
            JPanel buttonsBox = new JPanel();
            buttonsBox.setLayout(new BoxLayout(buttonsBox, BoxLayout.Y_AXIS));
            
            printBarCodeButton = new JButton("Print Bar Code");
            printBarCodeButton.setActionCommand("printBarCode");
            printBarCodeButton.addActionListener(methodListener);
            buttonsBox.add(printBarCodeButton);
            
            rotateBarcodeButton = new JButton("Start Barcode Rotation");
            rotateBarcodeButton.setActionCommand("rotateBarcode");
            rotateBarcodeButton.addActionListener(methodListener);
            buttonsBox.add(rotateBarcodeButton);
            
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(labelBox);
            mainBox.add(valuesBox);
            mainBox.add(Box.createHorizontalStrut(30));
            mainBox.add(buttonsBox);
            return mainBox;
        }
    }
    
    class GeneralPrintingPanel extends Component {

		private static final long serialVersionUID = 1884297964571344712L;

		public Component make() {
            JPanel mainBox = new JPanel();
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.X_AXIS));
            
            JPanel insertionButtonPanel = new JPanel();
            insertionButtonPanel.setLayout(new BoxLayout(insertionButtonPanel, BoxLayout.Y_AXIS));
            
            beginInsertionButton = new JButton("Begin Insertion");
            beginInsertionButton.setActionCommand("beginInsertion");
            beginInsertionButton.addActionListener(methodListener);
            
            endInsertionButton = new JButton("End Insertion");
            endInsertionButton.setActionCommand("endInsertion");
            endInsertionButton.addActionListener(methodListener);
            
            beginRemovalButton = new JButton("Begin Removal");
            beginRemovalButton.setActionCommand("beginRemoval");
            beginRemovalButton.addActionListener(methodListener);
            
            endRemovalButton = new JButton("End Removal");
            endRemovalButton.setActionCommand("endRemoval");
            endRemovalButton.addActionListener(methodListener);
            
            addESCButton = new JButton("Add Escape Seq.");
            addESCButton.setActionCommand("addESC");
            addESCButton.addActionListener(methodListener);
            
            insertionButtonPanel.add(beginInsertionButton);
            insertionButtonPanel.add(endInsertionButton);
            insertionButtonPanel.add(beginRemovalButton);
            insertionButtonPanel.add(endRemovalButton);
            insertionButtonPanel.add(Box.createVerticalStrut(10));
            insertionButtonPanel.add(addESCButton);
            
            JPanel buttonRow1Panel = new JPanel();
            buttonRow1Panel.setLayout(new BoxLayout(buttonRow1Panel, BoxLayout.Y_AXIS));
            
            topLogoButton = new JButton("Set Top Logo");
            topLogoButton.setActionCommand("topLogo");
            topLogoButton.addActionListener(methodListener);
            
            bottomLogoButton = new JButton("Set Bottom Logo");
            bottomLogoButton.setActionCommand("bottomLogo");
            bottomLogoButton.addActionListener(methodListener);
            
            cutPaperButton = new JButton("Cut Paper");
            cutPaperButton.setActionCommand("cutPaper");
            cutPaperButton.addActionListener(methodListener);
            
            buttonRow1Panel.add(topLogoButton);
            buttonRow1Panel.add(bottomLogoButton);
            buttonRow1Panel.add(cutPaperButton);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            
            printNormalButton = new JButton("Print Normal");
            printNormalButton.setActionCommand("printNormal");
            printNormalButton.addActionListener(methodListener);
            buttonPanel.add(printNormalButton);
            
            printImmediateButton = new JButton("Print Immediate");
            printImmediateButton.setActionCommand("printImmediate");
            printImmediateButton.addActionListener(methodListener);
            buttonPanel.add(printImmediateButton);
            
            validateDataButton = new JButton("Validate Data");
            validateDataButton.setActionCommand("validateData");
            validateDataButton.addActionListener(methodListener);
            buttonPanel.add(validateDataButton);
            
            letterQualityButton = new JButton("Letter Quality");
            letterQualityButton.setActionCommand("letterQuality");
            letterQualityButton.addActionListener(methodListener);
            buttonPanel.add(letterQualityButton);
            
            JPanel printerOutputPanel = new JPanel();
            printerOutputPanel.setLayout(new BoxLayout(printerOutputPanel, BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Send to printer: ");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            printerOutputPanel.add(label);
            
            posPrinterOutputArea = new JTextArea(10,10);
            JScrollPane scrollPane = new JScrollPane(posPrinterOutputArea);
            printerOutputPanel.add(scrollPane);
            
            mainBox.add(printerOutputPanel);
            mainBox.add(Box.createHorizontalStrut(5));
            mainBox.add(insertionButtonPanel);
            mainBox.add(Box.createHorizontalStrut(5));
            mainBox.add(buttonRow1Panel);
            mainBox.add(Box.createHorizontalStrut(5));
            mainBox.add(buttonPanel);
            
            return mainBox;
        }
    }
    
    class BitmapPanel extends Component {

		private static final long serialVersionUID = -8849851459571094770L;
		public Component make() {
            JPanel mainBox = new JPanel();
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.X_AXIS));
            
            JPanel browserPanel = new JPanel();
            browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.Y_AXIS));
            
            JPanel browserValuePanel = new JPanel();
            browserValuePanel.setLayout(new BoxLayout(browserValuePanel, BoxLayout.X_AXIS));
            
            JPanel argumentPanel = new JPanel();
            argumentPanel.setLayout(new BoxLayout(argumentPanel, BoxLayout.Y_AXIS));
            
            JLabel label = new JLabel("Image to Print:");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            browserPanel.add(label);
            browserPanel.add(Box.createRigidArea(new Dimension(0,0)));
            
            bitmapFileName = new JTextField();
            bitmapFileName.setAlignmentX(Component.LEFT_ALIGNMENT);
            bitmapFileName.setMaximumSize(new Dimension(180,20));
            bitmapFileName.setPreferredSize(new Dimension(180,20));
            
            bitmapBrowse = new JButton("Browse");
            bitmapFileChooser = new JFileChooser();
            ImageFilter filter = new ImageFilter();
            
            bitmapBrowse.setActionCommand("bitmapBrowse");
            bitmapBrowse.addActionListener(methodListener);
            
            bitmapFileChooser.addChoosableFileFilter(filter);
            
            browserValuePanel.add(bitmapFileName);
            browserValuePanel.add(bitmapBrowse);
            browserPanel.add(browserValuePanel);
            
            label = new JLabel("Bitmap Width");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            argumentPanel.add(label);
            
            bitmapWidth = new JComboBox(bitmapWidths);
            bitmapWidth.setMaximumSize(new Dimension(180,20));
            bitmapWidth.setPreferredSize(new Dimension(180,20));
            argumentPanel.add(bitmapWidth);
            
            label = new JLabel("Bitmap Alignment");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            argumentPanel.add(label);
            
            bitmapAlign = new JComboBox(bitmapAlignments);
            bitmapAlign.setMaximumSize(new Dimension(180,20));
            bitmapAlign.setPreferredSize(new Dimension(180,20));
            argumentPanel.add(bitmapAlign);
            
            label = new JLabel("Bitmap Number (setBitmap)");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            argumentPanel.add(label);
            
            String[] values = new String[]  {"1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",};
            bitmapNumberCombo = new JComboBox(values);
            bitmapNumberCombo.setMaximumSize(new Dimension(180,20));
            bitmapNumberCombo.setPreferredSize(new Dimension(180,20));
            argumentPanel.add(bitmapNumberCombo);
            
            bitmapNumberCombo.setEditable(true);
            bitmapWidth.setEditable(true);
            bitmapAlign.setEditable(true);
            
            JPanel buttonsBox = new JPanel();
            buttonsBox.setLayout(new BoxLayout(buttonsBox, BoxLayout.Y_AXIS));
            
            setBitmapButton = new JButton("Set Bitmap");
            setBitmapButton.setActionCommand("setBitmap");
            setBitmapButton.addActionListener(methodListener);
            
            
            printBitmapButton = new JButton("Print Bitmap");
            printBitmapButton.setActionCommand("printBitmap");
            printBitmapButton.addActionListener(methodListener);
            
            rotateBitmapButton = new JButton("Start Bitmap Rotation");
            rotateBitmapButton.setActionCommand("rotateBitmap");
            rotateBitmapButton.addActionListener(methodListener);
            
            buttonsBox.add(printBitmapButton);
            buttonsBox.add(setBitmapButton);
            buttonsBox.add(rotateBitmapButton);
            
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(browserPanel);
            mainBox.add(Box.createHorizontalGlue());
            mainBox.add(argumentPanel);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(buttonsBox);
            mainBox.add(Box.createHorizontalStrut(10));
            
            return mainBox;
        }
        private class ImageFilter extends FileFilter{
            private String getExtension(File file){
                String ext = null;
                String s = file.getName();
                int i = s.lastIndexOf('.');
                
                if( i > 0 && i < s.length()  - 1){
                    ext = s.substring(i+1).toLowerCase();
                }
                return ext;
            }
            
            public boolean accept(File f){
                if(f.isDirectory()) {
                    return true;
                }
                
                String extension = getExtension(f);
                if(extension != null){
                    if(extension.equals("tiff") ||
                            extension.equals("tif") ||
                            extension.equals("gif") ||
                            extension.equals("jpeg") ||
                            extension.equals("jpg") ||
                            extension.equals("png") ||
                            extension.equals("bmp")) {
                        return true;
                    } else{
                        return false;
                    }
                }
                return false;
            }
            
            public String getDescription() {
                return "Images";
            }
        }
    }
    
    class PageModePanel extends Component {

		private static final long serialVersionUID = -1932749969082747610L;

		public Component make() {
            JPanel mainBox = new JPanel();
            mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

            JPanel labelBox = new JPanel();
            labelBox.setLayout(new BoxLayout(labelBox, BoxLayout.Y_AXIS));
            
            JPanel controlBox = new JPanel();
            controlBox.setLayout(new BoxLayout(controlBox, BoxLayout.X_AXIS));
            
            JLabel label = new JLabel("Page Mode Area: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Descriptor: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Horizonal Position: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Vertical Position: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Print Area: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Print Direction: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
            
            label = new JLabel("Page Mode Station: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(180,20));
            label.setPreferredSize(new Dimension(180,20));
            labelBox.add(label);
                        
            
            JPanel valuesBox = new JPanel();
            valuesBox.setLayout(new BoxLayout(valuesBox, BoxLayout.Y_AXIS));
            
            pageModeArea = new JLabel();
            pageModeArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeArea.setMaximumSize(new Dimension(80,20));
            pageModeArea.setPreferredSize(new Dimension(80,20));


            pageModeDescriptor = new JLabel();
            pageModeDescriptor.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeDescriptor.setMaximumSize(new Dimension(500,20));
            pageModeDescriptor.setPreferredSize(new Dimension(80,20));
                        
            pageModeHorizontalPos = new JTextField();
            pageModeHorizontalPos.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeHorizontalPos.setMaximumSize(new Dimension(80,20));
            pageModeHorizontalPos.setPreferredSize(new Dimension(80,20));
            
            pageModeVerticalPos = new JTextField();
            pageModeVerticalPos.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeVerticalPos.setMaximumSize(new Dimension(80,20));
            pageModeVerticalPos.setPreferredSize(new Dimension(80,20));
            
            pageModePrintArea = new JTextField();
            pageModePrintArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModePrintArea.setMaximumSize(new Dimension(150,20));
            pageModePrintArea.setPreferredSize(new Dimension(150,20));
            
            pageModeDirectionCombo = new JComboBox(pmDirections);
            pageModeDirectionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeDirectionCombo.setMaximumSize(new Dimension(110,20));
            pageModeDirectionCombo.setPreferredSize(new Dimension(110,20));
            pageModeDirectionCombo.setEditable(false);
            
            pageModeStationCombo = new JComboBox(pmStations);
            pageModeStationCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeStationCombo.setMaximumSize(new Dimension(110,20));
            pageModeStationCombo.setPreferredSize(new Dimension(110,20));
            pageModeStationCombo.setEditable(false);
            pageModeStationCombo.setActionCommand("pageModeStationCombo");
            pageModeStationCombo.addActionListener(methodListener);
            
            
            valuesBox.add(pageModeArea);
            valuesBox.add(pageModeDescriptor);
            valuesBox.add(pageModeHorizontalPos);
            valuesBox.add(pageModeVerticalPos);
            valuesBox.add(pageModePrintArea);
            valuesBox.add(pageModeDirectionCombo);
            valuesBox.add(pageModeStationCombo);
            
            JPanel buttonsBox = new JPanel();
            buttonsBox.setLayout(new BoxLayout(buttonsBox, BoxLayout.Y_AXIS));
            
            clearPrintAreadButton = new JButton("Clear Print Area");
            clearPrintAreadButton.setActionCommand("clearPrintArea");
            clearPrintAreadButton.addActionListener(methodListener);
            
            pageModeUpdatePropButton = new JButton("Update Properties");
            pageModeUpdatePropButton.setActionCommand("pageModeUpdateProp");
            pageModeUpdatePropButton.addActionListener(methodListener);
            
            buttonsBox.add(clearPrintAreadButton);
            buttonsBox.add(pageModeUpdatePropButton);
            
            pageModeControlCombo = new JComboBox(pmpCommands);
            pageModeControlCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            pageModeControlCombo.setMaximumSize(new Dimension(200,20));
            pageModeControlCombo.setPreferredSize(new Dimension(200,20));
            pageModeControlCombo.setEditable(true);
            
            label = new JLabel("Page Mode Command: ");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(150,20));
            label.setPreferredSize(new Dimension(150,20));
            
            pageModePrintButton = new JButton("Send Page Mode Command");
            pageModePrintButton.setActionCommand("pageModePrint");
            pageModePrintButton.addActionListener(methodListener);
            
            controlBox.add(Box.createHorizontalStrut(10));
            controlBox.add(label);
            controlBox.add(pageModeControlCombo);
            controlBox.add(Box.createHorizontalStrut(10));
            controlBox.add(pageModePrintButton);
            controlBox.add(Box.createHorizontalStrut(10));            
            controlBox.setBorder(LineBorder.createBlackLineBorder());
            
            JPanel Row1 = new JPanel();
            Row1.setLayout(new BoxLayout(Row1, BoxLayout.X_AXIS));
            
            Row1.add(Box.createHorizontalStrut(10));
            Row1.add(labelBox);
            Row1.add(valuesBox);
            Row1.add(Box.createHorizontalStrut(30));
            Row1.add(buttonsBox);
            Row1.add(Box.createHorizontalStrut(10));
            mainBox.add(Row1);
            mainBox.add(controlBox);
            
            return mainBox;
        }
    }

    public void errorDialog(JposException je, String call){
        if(je.getErrorCode() == JposConst.JPOS_E_EXTENDED){
            //extended error
            String msg = "Unknown Extended Error";
            
            switch( je.getErrorCodeExtended()){
                case POSPrinterConst.JPOS_EPTR_COVER_OPEN:
                    msg = "Cover Open";
                    break;
                case POSPrinterConst.JPOS_EPTR_JRN_EMPTY:
                    msg = "Journal Paper Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_REC_EMPTY:
                    msg = "Reciept Papaer Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_SLP_EMPTY:
                    msg = "Slip Paper Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_SLP_FORM:
                    msg = "Form Present";
                    break;
                case POSPrinterConst.JPOS_EPTR_TOOBIG:
                    msg = "Bitmap Too Big";
                    break;
                case POSPrinterConst.JPOS_EPTR_BADFORMAT:
                    msg = "Bad Image Format";
                    break;
                case POSPrinterConst.JPOS_EPTR_JRN_CARTRIDGE_REMOVED:
                    msg = "Journal Cartridge Removed";
                    break;
                case POSPrinterConst.JPOS_EPTR_JRN_CARTRIDGE_EMPTY:
                    msg = "Journal Cartridge Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_JRN_HEAD_CLEANING:
                    msg = "Journal Head Cleaning";
                    break;
                case POSPrinterConst.JPOS_EPTR_REC_CARTRIDGE_REMOVED:
                    msg = "Reciept Cartridge Removed";
                    break;
                case POSPrinterConst.JPOS_EPTR_REC_CARTRIDGE_EMPTY:
                    msg = "Reciept Cartridge Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_REC_HEAD_CLEANING:
                    msg = "Reciept Head Cleaning";
                    break;
                case POSPrinterConst.JPOS_EPTR_SLP_CARTRIDGE_REMOVED:
                    msg = "Slip Cartridge Removed";
                    break;
                case POSPrinterConst.JPOS_EPTR_SLP_CARTRIDGE_EMPTY:
                    msg = "Slip Cartridge Empty";
                    break;
                case POSPrinterConst.JPOS_EPTR_SLP_HEAD_CLEANING:
                    msg = "Slip Head Cleaning";
                    break;
            }
            JOptionPane.showMessageDialog(null, "Extended error in " + call + ": "+ msg, "Exception", JOptionPane.ERROR_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Exception in " + call + ": "+ je.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(posPrinter != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(posPrinter.getState()));
            }
        }
    }
    
    private int getComboBoxIndex(JComboBox box, int strValValue)
    {    	
    	for(int i=0 ; i< box.getItemCount(); i++)
    	{
    		try
    		{
    			if(((StrVal)box.getItemAt(i)).value == strValValue)
    			{
    				return i; 
    			}
    		}
    		catch (ClassCastException e)
    		{
    		}
    	}
    	return -1;
    }
}
