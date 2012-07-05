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
// LineDisplayPanel.java - The Line Display panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import jpos.*;

public class LineDisplayPanel extends Component {
    
	private static final long serialVersionUID = -7597165738110136547L;

	protected MainButtonPanel mainButtonPanel;
    
    private LineDisplay lineDisplay;
    
    private String defaultLogicalName = "defaultLineDisplay";
    
    //	private JTextField row;
    //	private JTextField column;
    private JTextField lineDisplayData;
    //	private JTextField attribute;
    
    private JTextField windowVPRow;
    private JTextField windowVPCol;
    private JTextField windowVPHeight;
    private JTextField windowVPWidth;
    private JTextField windowWHeight;
    private JTextField windowWWidth;
    
    private JLabel brLabel;
    private JTextField brVal;
    private JButton blinkRateButton;
    
    private JComboBox rowCombo;
    private JComboBox columnCombo;
    private JComboBox attrCombo;
    
    private JComboBox descCombo;
    private JButton descOnButton;
    private JButton descOffButton;
    private JButton descClearButton;
    
    private JList windowList;
    private DefaultListModel windowListModel;
    
    private JButton displayTextAtButton;
    private JButton displayTextButton;
    private JButton clearTextButton;
    private JButton moveCursorButton;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JLabel label;
    
    private JButton addWindowButton;
    private JButton delWindowButton;
    private JButton setCurrentWindowButton;
    private JButton refreshWindowButton;
    
    private JButton shiftUpButton;
    private JButton shiftDownButton;
    private JButton shiftRightButton;
    private JButton shiftLeftButton;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    private JSlider brightnessSlider;
    
    private MethodListener methodListener = new MethodListener();
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public LineDisplayPanel() {
        lineDisplay = new LineDisplay();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        mainButtonPanel = new MainButtonPanel(methodListener,defaultLogicalName);
        mainPanel.add(mainButtonPanel);
        
        
        
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        subPanel.setMinimumSize(new Dimension(Short.MAX_VALUE,370));
        subPanel.setPreferredSize(new Dimension(Short.MAX_VALUE,370));
        subPanel.setMaximumSize(new Dimension(Short.MAX_VALUE,400));
        
        
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
        
        
        JPanel lineDisplayOutputPanel = new JPanel();
        lineDisplayOutputPanel.setLayout(new BoxLayout(lineDisplayOutputPanel, BoxLayout.Y_AXIS));
        lineDisplayOutputPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        lineDisplayOutputPanel.setMinimumSize(new Dimension(600,370));
        lineDisplayOutputPanel.setPreferredSize(new Dimension(600,370));
        lineDisplayOutputPanel.setMaximumSize(new Dimension(600,370));
        
        JPanel topRowPanel = new JPanel();
        topRowPanel.setLayout(new BoxLayout(topRowPanel, BoxLayout.X_AXIS));
        
        JPanel bottomRowPanel = new JPanel();
        bottomRowPanel.setLayout(new BoxLayout(bottomRowPanel, BoxLayout.X_AXIS));
        
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        DisplayTextPanel dtp = new DisplayTextPanel();
        tabbedPane.addTab("Display Text", null, dtp.make(), "DisplayText");
        
        WindowManagementPanel wmp = new WindowManagementPanel();
        tabbedPane.addTab("Window Control", null, wmp.make(), "WindowControl");
        
        MiscControlPanel mcp = new MiscControlPanel();
        tabbedPane.addTab("Misc Controls", null, mcp.make(), "MiscCtrl");
        
        
        topRowPanel.add(tabbedPane);
        topRowPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        
        
        lineDisplayOutputPanel.add(topRowPanel);
        lineDisplayOutputPanel.add(bottomRowPanel);
        
        subPanel.add(lineDisplayOutputPanel);
        
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    private void updateRowColumn() throws JposException{
        int rows = lineDisplay.getRows();
        int cols = lineDisplay.getColumns();
        
        rowCombo.removeAllItems();
        columnCombo.removeAllItems();
        
        for(int i=0; i< rows; i++){
            rowCombo.addItem(new Integer(i));
        }
        for(int i=0; i< cols; i++){
            columnCombo.addItem(new Integer(i));
        }
    }
    
    class SliderListener implements ChangeListener{
        public void stateChanged(ChangeEvent ev){
            int val = ((JSlider)ev.getSource()).getValue();
            try{
                lineDisplay.setDeviceBrightness(val);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Exception in setDeviceBrightness\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }
    }
    
    /** Listens to the method buttons. */
    
    class MethodListener implements ActionListener {
        private boolean DoOpen(){
            String logicalName = mainButtonPanel.getLogicalName();
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                lineDisplay.open(logicalName);
                mainButtonPanel.currentStatus.setText("Open");
                mainButtonPanel.logicalNameTextField.setEnabled(false);
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
                //get the max rows and columns
                updateRowColumn();
                
                rowCombo.setEnabled(true);
                columnCombo.setEnabled(true);
                
                attrCombo.removeAllItems();
                attrCombo.addItem(new String("Normal"));
                int capBlink = lineDisplay.getCapBlink();
                int capReverse = lineDisplay.getCapReverse();
                if(capBlink > 0) {
                    attrCombo.addItem(new String("Blink"));
                }
                if(capReverse > 0) {
                    attrCombo.addItem(new String("Reverse"));
                }
                if(capBlink > 0 && capReverse > 0) {
                    attrCombo.addItem(new String("Blink & Reverse"));
                }
                
                if(lineDisplay.getCapBlinkRate()){
                    blinkRateButton.setEnabled(true);
                    brVal.setEnabled(true);
                    brLabel.setText("Blink Rate (" + Integer.toString(lineDisplay.getBlinkRate()) + ")");
                }
                brightnessSlider.setEnabled(lineDisplay.getCapBrightness());
                moveCursorButton.setEnabled(true);
                setCurrentWindowButton.setEnabled(true);
                int version = lineDisplay.getDeviceServiceVersion();
                if(version >= 1009000) {
                    ver_19_complient = true;
                    ver_18_complient = true;
                }
                if(version >= 1008000) {
                    ver_18_complient = true;
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Failed to open \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
                return false;
            }
            return true;
        }
        
        private boolean DoClaim() {
            String logicalName = mainButtonPanel.getLogicalName();
            try{
                lineDisplay.claim(0);
                mainButtonPanel.currentStatus.setText("Claimed");
                displayTextAtButton.setEnabled(true);
                displayTextButton.setEnabled(true);
                lineDisplayData.setEnabled(true);
                clearTextButton.setEnabled(true);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                addWindowButton.setEnabled(true);
                delWindowButton.setEnabled(true);
                refreshWindowButton.setEnabled(true);
                attrCombo.setEnabled(true);
                attrCombo.setSelectedIndex(0);
                
                if(lineDisplay.getCapDescriptors()){
                    //populate the descriptor combo
                    int desc = lineDisplay.getColumns();
                    
                    descCombo.removeAllItems();
                    for(int i=0; i< desc; i++){
                        descCombo.addItem(new Integer(i));
                    }
                    
                    descCombo.setEnabled(true);
                    descOnButton.setEnabled(true);
                    descOffButton.setEnabled(true);
                    descClearButton.setEnabled(true);
                }
                
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
                return false;
            }
            return true;
        }
        public boolean DoEnabled(boolean enable) {
            String logicalName = mainButtonPanel.getLogicalName();
            try {
                lineDisplay.setDeviceEnabled(enable);
            } catch(JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to enable \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
                return false;
            }
            return true;
        }
        public void actionPerformed(ActionEvent ae) {
            mainButtonPanel.action(ae);
            String logicalName = mainButtonPanel.getLogicalName();
            
            if(ae.getActionCommand().equals("open")){
                DoOpen();
            } else if(ae.getActionCommand().equals("claim")){
                DoClaim();
            } else if(ae.getActionCommand().equals("release")){
                try{
                    lineDisplay.release();
                    mainButtonPanel.currentStatus.setText("Released");
                    lineDisplayData.setEnabled(false);
                    displayTextAtButton.setEnabled(false);
                    displayTextButton.setEnabled(false);
                    clearTextButton.setEnabled(false);
                    deviceEnabledCB.setEnabled(false);
                    addWindowButton.setEnabled(false);
                    delWindowButton.setEnabled(false);
                    refreshWindowButton.setEnabled(false);
                    attrCombo.setEnabled(false);
                    descCombo.setEnabled(false);
                    descOnButton.setEnabled(false);
                    descOffButton.setEnabled(false);
                    descClearButton.setEnabled(false);
                    
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            if(ae.getActionCommand().equals("close")){
                try{
                    lineDisplay.close();
                    mainButtonPanel.currentStatus.setText("Closed");
                    lineDisplayData.setEnabled(false);
                    moveCursorButton.setEnabled(false);
                    displayTextAtButton.setEnabled(false);
                    displayTextButton.setEnabled(false);
                    clearTextButton.setEnabled(false);
                    deviceEnabledCB.setEnabled(false);
                    rowCombo.setEnabled(false);
                    columnCombo.setEnabled(false);
                    attrCombo.setEnabled(false);
                    mainButtonPanel.logicalNameTextField.setEnabled(true);
                    freezeEventsCB.setEnabled(false);
                    brightnessSlider.setEnabled(false);
                    blinkRateButton.setEnabled(false);
                    addWindowButton.setEnabled(false);
                    delWindowButton.setEnabled(false);
                    refreshWindowButton.setEnabled(false);
                    setCurrentWindowButton.setEnabled(false);
                    brVal.setEnabled(false);
                    brLabel.setText("Blink Rate");
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            
            if(ae.getActionCommand().equals("displayTextAt")){
                try{
                    int attr;
                    if(((String)attrCombo.getSelectedItem()).equals( "Blink")){
                        attr = LineDisplayConst.DISP_DT_BLINK;
                    }else if(((String)attrCombo.getSelectedItem()).equals( "Reverse")){
                        attr = LineDisplayConst.DISP_DT_REVERSE;
                    }else if(((String)attrCombo.getSelectedItem()).equals( "Blink & Reverse")){
                        attr = LineDisplayConst.DISP_DT_BLINK_REVERSE;
                    }else{
                        attr = LineDisplayConst.DISP_DT_NORMAL;
                    }
                    
                    lineDisplay.displayTextAt( ((Integer)rowCombo.getSelectedItem()).intValue(),((Integer)columnCombo.getSelectedItem()).intValue(),lineDisplayData.getText(),attr);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in displayTextAt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("displayText")){
                try{
                    //lineDisplay.setCursorRow(Integer.parseInt(row.getText()));
                    //lineDisplay.setCursorColumn(Integer.parseInt(column.getText()));
                    int attr;
                    if(((String)attrCombo.getSelectedItem()).equals( "Blink")){
                        attr = LineDisplayConst.DISP_DT_BLINK;
                    }else if(((String)attrCombo.getSelectedItem()).equals( "Reverse")){
                        attr = LineDisplayConst.DISP_DT_REVERSE;
                    }else if(((String)attrCombo.getSelectedItem()).equals( "Blink & Reverse")){
                        attr = LineDisplayConst.DISP_DT_BLINK_REVERSE;
                    }else{
                        attr = LineDisplayConst.DISP_DT_NORMAL;
                    }
                    
                    lineDisplay.displayText(lineDisplayData.getText(),attr);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in displayText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("info")){
                try{
                    String ver = new Integer(lineDisplay.getDeviceServiceVersion()).toString();
                    String msg = "Service Description: " + lineDisplay.getDeviceServiceDescription();
                    msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                    ver = new Integer(lineDisplay.getDeviceControlVersion()).toString();
                    msg += "\n\nControl Description: " + lineDisplay.getDeviceControlDescription();
                    msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                    msg += "\n\nPhysical Device Name: " + lineDisplay.getPhysicalDeviceName();
                    msg += "\nPhysical Device Description: " + lineDisplay.getPhysicalDeviceDescription();
                    
                    msg += "\n\nProperties:\n------------------------";
                    
                    msg += "\nCapPowerReporting: " + (lineDisplay.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (lineDisplay.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                    if(ver_18_complient)
                    {
                    	msg += "\nCapStatisticsReporting: " + lineDisplay.getCapStatisticsReporting();                    
                    	msg += "\nCapUpdateStatistics: " + lineDisplay.getCapUpdateStatistics();
                    }
                    else
                    {
                    	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                    	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                    }
                    
                    if(ver_19_complient)
                    {
                    	msg += "\nCapCompareFirmwareVersion: " + lineDisplay.getCapCompareFirmwareVersion();                    
                    	msg += "\nCapUpdateFirmware: " + lineDisplay.getCapUpdateFirmware();
                    }
                    else
                    {
                    	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                    	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                    }              
                    
                    msg = msg + "\nCapBitmap: "+ lineDisplay.getCapBitmap();
                    int blink = lineDisplay.getCapBlink();
                    msg = msg + "\nCapBlink: "+ (blink == LineDisplayConst.DISP_CB_BLINKEACH ? "DISP_CB_BLINK_EACH" : ( blink == LineDisplayConst.DISP_CB_BLINKALL ? "DISP_CB_BLINKALL" : "DISP_CB_NOBLINK"));
                    msg = msg + "\nCapBlinkRate: "+ lineDisplay.getCapBlinkRate();
                    msg = msg + "\nCapBrightness: "+ lineDisplay.getCapBrightness();
                    
                    msg = msg + "\nCapCharacterSet: ";
                    int charSet = lineDisplay.getCapCharacterSet();
                    switch(charSet){
                        case LineDisplayConst.DISP_CCS_NUMERIC:
                            msg = msg + "DISP_CCS_NUMERIC";
                            break;
                        case LineDisplayConst.DISP_CCS_ALPHA:
                            msg = msg + "DISP_CCS_ALPHA";
                            break;
                        case LineDisplayConst.DISP_CCS_ASCII:
                            msg = msg + "DISP_CCS_ASCII";
                            break;
                        case LineDisplayConst.DISP_CCS_KANA:
                            msg = msg + "DISP_CCS_KANA";
                            break;
                        case LineDisplayConst.DISP_CCS_KANJI:
                            msg = msg + "DISP_CCS_KANJI";
                            break;
                        case LineDisplayConst.DISP_CCS_UNICODE:
                            msg = msg + "DISP_CCS_UNICODE";
                            break;
                    }
                    
                    msg = msg + "\nCapCursorType: ";
                    int cursorType = lineDisplay.getCapCursorType();
                    if(cursorType == 0){ //should be DISP_CCT_NONE
                        msg = msg + "DISP_CCT_NONE ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_FIXED) > 0){
                        msg = msg + "DISP_CCT_FIXED ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_BLOCK) > 0){
                        msg = msg + "DISP_CCT_BLOCK ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_HALFBLOCK) > 0){
                        msg = msg + "DISP_CCT_HALFBLOCK ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_UNDERLINE) > 0){
                        msg = msg + "DISP_CCT_UNDERLINE ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_REVERSE) > 0){
                        msg = msg + "DISP_CCT_REVERSE ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_OTHER) > 0){
                        msg = msg + "DISP_CCT_OTHER ";
                    }
                    if( (cursorType & LineDisplayConst.DISP_CCT_FIXED) > 0){
                        msg = msg + "DISP_CCT_FIXED ";
                    }
                    
                    msg = msg + "\nCapCustomGlyph: "+ lineDisplay.getCapCustomGlyph();
                    msg = msg + "\nCapDescriptors: "+ lineDisplay.getCapDescriptors();
                    msg = msg + "\nCapHMarquee: "+ lineDisplay.getCapHMarquee();
                    msg = msg + "\nCapICharWait: "+ lineDisplay.getCapICharWait();
                    msg = msg + "\nCapMapCharacterSet: "+ lineDisplay.getCapMapCharacterSet();
                    msg = msg + "\nCapReadBack: ";
                    int readback = lineDisplay.getCapReadBack();
                    switch(readback){
                        case LineDisplayConst.DISP_CRB_NONE:
                            msg = msg + "DISP_CRB_NONE";
                            break;
                        case LineDisplayConst.DISP_CRB_SINGLE:
                            msg = msg + "DISP_CRB_SINGLE";
                            break;
                    }
                    msg = msg + "\nCapReverse: ";
                    int reverse = lineDisplay.getCapReverse();
                    switch(reverse){
                        case LineDisplayConst.DISP_CR_NONE:
                            msg = msg + "DISP_CR_NONE";
                            break;
                        case LineDisplayConst.DISP_CR_REVERSEALL:
                            msg = msg + "DISP_CR_REVERSEALL";
                            break;
                        case LineDisplayConst.DISP_CR_REVERSEEACH:
                            msg = msg + "DISP_CR_REVERSEEACH";
                            break;
                    }
                    
                    msg = msg + "\nCapScreenMode: "+ lineDisplay.getCapScreenMode();
                    msg = msg + "\nCapVMarquee: "+ lineDisplay.getCapVMarquee();
                    
                    JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("clearText")){
                try{
                    lineDisplay.clearText();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in clearText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("Add Window")){
                try{
                    int vr, vc, vh, vw, wh, ww;
                    int curWin;
                    vr = Integer.parseInt(windowVPRow.getText());
                    vc = Integer.parseInt(windowVPCol.getText());
                    vh = Integer.parseInt(windowVPHeight.getText());
                    vw = Integer.parseInt(windowVPWidth.getText());
                    wh = Integer.parseInt(windowWHeight.getText());
                    ww = Integer.parseInt(windowWWidth.getText());
                    lineDisplay.createWindow( vr, vc, vh, vw, wh, ww);
                    
                    curWin = lineDisplay.getCurrentWindow();
                    windowListModel.addElement(new String(Integer.toString(curWin)));
                    updateRowColumn();
                    
                }catch( java.lang.NumberFormatException e){
                    JOptionPane.showMessageDialog(null, "You must fill in all fields with valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Add Window\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("Del Window")){
                try{
                    int curWin = lineDisplay.getCurrentWindow();
                    lineDisplay.destroyWindow();
                    for(int i = 0; i < windowListModel.getSize(); i++){
                        if(((String)windowListModel.get(i)).equalsIgnoreCase(Integer.toString(curWin))){
                            windowListModel.remove(i);
                        }
                    }
                    updateRowColumn();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Del Window\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            else if(ae.getActionCommand().equals("Set Cur Window")){
                try{
                    int index = windowList.getSelectedIndex();
                    if(index == -1){
                        JOptionPane.showMessageDialog(null, "Please select the window from the list first.", "Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        int selectedWin = Integer.parseInt((String)windowListModel.get(index));
                        lineDisplay.setCurrentWindow(selectedWin);
                        updateRowColumn();
                    }
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Set Cur Window\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("Shift Up")){
                try{
                    lineDisplay.scrollText(LineDisplayConst.DISP_ST_UP, 1);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in scrollText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("Shift Down")){
                try{
                    lineDisplay.scrollText(LineDisplayConst.DISP_ST_DOWN, 1);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in scrollText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("Shift Right")){
                try{
                    lineDisplay.scrollText(LineDisplayConst.DISP_ST_RIGHT, 1);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in scrollText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("Shift Left")){
                try{
                    lineDisplay.scrollText(LineDisplayConst.DISP_ST_LEFT, 1);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in scrollText\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("blinkRate")){
                try{
                    lineDisplay.setBlinkRate(Integer.parseInt(brVal.getText()));
                    brLabel.setText("Blink Rate (" + Integer.toString(lineDisplay.getBlinkRate()) + ")");
                }catch( java.lang.NumberFormatException e){
                    JOptionPane.showMessageDialog(null, "You must enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setBlinkRate\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("moveCursor")){
                try{
                    lineDisplay.setCursorRow(((Integer)rowCombo.getSelectedItem()).intValue());
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setCursorRow\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
                
                try{
                    lineDisplay.setCursorColumn(((Integer)columnCombo.getSelectedItem()).intValue());
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setCursorColumn\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("Refresh Window")){
                try{
                    int index = windowList.getSelectedIndex();
                    if(index == -1){
                        JOptionPane.showMessageDialog(null, "Please select the window to refresh from the list first.", "Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        int selectedWin = Integer.parseInt((String)windowListModel.get(index));
                        lineDisplay.refreshWindow(selectedWin);
                        updateRowColumn();
                    }
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setCursorRow\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("descOn")){
                try{
                    int descNum = ((Integer)descCombo.getSelectedItem()).intValue();
                    lineDisplay.setDescriptor(descNum, LineDisplayConst.DISP_SD_ON);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setDescriptor\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("descOff")){
                try{
                    int descNum = ((Integer)descCombo.getSelectedItem()).intValue();
                    lineDisplay.setDescriptor(descNum, LineDisplayConst.DISP_SD_OFF);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in setDescriptor\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("descClear")){
                try{
                    lineDisplay.clearDescriptors();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to clear descriptors\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("oce")){
                if(DoOpen()) {
                    if(DoClaim()) {
                        DoEnabled(true);
                    }
                }
            }else if(ae.getActionCommand().equals("stats")) {
                try{
                    StatisticsDialog dlg = new StatisticsDialog(lineDisplay);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
                
            }else if(ae.getActionCommand().equals("firmware")) {
                try{
                    FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(lineDisplay);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                if(deviceEnabledCB.isEnabled()){
                    deviceEnabledCB.setSelected(lineDisplay.getDeviceEnabled());
                }
                if(freezeEventsCB.isEnabled()){
                    freezeEventsCB.setSelected(lineDisplay.getFreezeEvents());
                }
            } catch(JposException je) {
                System.err.println("LineDisplayPanel: MethodListener: JposException");
            }
        }
    }
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        lineDisplay.setDeviceEnabled(false);
                    }else{
                        lineDisplay.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        lineDisplay.setFreezeEvents(false);
                    }else{
                        lineDisplay.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("LineDisplayPanel: CheckBoxListener: Jpos Exception" + source);
            }
        }
    }
    class WindowManagementPanel extends Component {

		private static final long serialVersionUID = 6595899267624438905L;

		public Component make() {
            JPanel windowButtonPanel = new JPanel();
            windowButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            windowButtonPanel.setLayout(new BoxLayout(windowButtonPanel, BoxLayout.Y_AXIS));
            windowButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,5));
            
            addWindowButton = new JButton("Add Window");
            addWindowButton.setActionCommand("Add Window");
            addWindowButton.addActionListener(methodListener);
            addWindowButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            
            delWindowButton = new JButton("Del Cur Window");
            delWindowButton.setActionCommand("Del Window");
            delWindowButton.addActionListener(methodListener);
            delWindowButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            setCurrentWindowButton = new JButton("Set Cur Window");
            setCurrentWindowButton.setActionCommand("Set Cur Window");
            setCurrentWindowButton.addActionListener(methodListener);
            setCurrentWindowButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            refreshWindowButton = new JButton("Refresh Window");
            refreshWindowButton.setActionCommand("Refresh Window");
            refreshWindowButton.addActionListener(methodListener);
            refreshWindowButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            addWindowButton.setEnabled(false);
            delWindowButton.setEnabled(false);
            setCurrentWindowButton.setEnabled(false);
            refreshWindowButton.setEnabled(false);
            
            windowButtonPanel.add(addWindowButton);
            windowButtonPanel.add(delWindowButton);
            windowButtonPanel.add(setCurrentWindowButton);
            windowButtonPanel.add(refreshWindowButton);
            
            JPanel windowArgPanel = new JPanel();
            JPanel windowArgLabelPanel = new JPanel();
            JPanel windowArgFieldPanel = new JPanel();
            
            windowArgLabelPanel.setLayout( new BoxLayout(windowArgLabelPanel, BoxLayout.Y_AXIS));
            windowArgLabelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            windowArgLabelPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            
            label = new JLabel("Viewport Row");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            label = new JLabel("Viewport Col");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            label = new JLabel("Viewport Height");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            label = new JLabel("Viewport Width");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            label = new JLabel("Window Height");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            label = new JLabel("Window Width");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            windowArgLabelPanel.add(label);
            
            windowArgFieldPanel.setLayout( new  BoxLayout(windowArgFieldPanel, BoxLayout.Y_AXIS));
            windowArgFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            
            windowArgFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            windowArgPanel.setLayout(new  BoxLayout(windowArgPanel, BoxLayout.X_AXIS));
            windowArgPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            //windowArgPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            
            windowVPRow = new JTextField();
            windowVPRow.setMaximumSize(new Dimension(50,17));
            windowVPRow.setPreferredSize(new Dimension(50,17));
            windowVPRow.setMinimumSize(new Dimension(50,17));
            
            windowVPCol = new JTextField();
            windowVPCol.setMaximumSize(new Dimension(50,17));
            windowVPCol.setMinimumSize(new Dimension(50,17));
            windowVPCol.setPreferredSize(new Dimension(50,17));
            
            
            windowVPHeight = new JTextField();
            windowVPHeight.setMaximumSize(new Dimension(50,17));
            windowVPHeight.setMinimumSize(new Dimension(50,17));
            windowVPHeight.setPreferredSize(new Dimension(50,17));
            
            
            windowVPWidth = new JTextField();
            windowVPWidth.setMaximumSize(new Dimension(50,17));
            windowVPWidth.setMinimumSize(new Dimension(50,17));
            windowVPWidth.setPreferredSize(new Dimension(50,17));
            
            windowWHeight = new JTextField();
            windowWHeight.setMaximumSize(new Dimension(50,17));
            windowWHeight.setMinimumSize(new Dimension(50,17));
            windowWHeight.setPreferredSize(new Dimension(50,17));
            
            windowWWidth = new JTextField();
            windowWWidth.setMaximumSize(new Dimension(50,17));
            windowWWidth.setMinimumSize(new Dimension(50,17));
            windowWWidth.setPreferredSize(new Dimension(50,17));
            
            windowArgFieldPanel.add(windowVPRow);
            windowArgFieldPanel.add(windowVPCol);
            windowArgFieldPanel.add(windowVPHeight);
            windowArgFieldPanel.add(windowVPWidth);
            windowArgFieldPanel.add(windowWHeight);
            windowArgFieldPanel.add(windowWWidth);
            
            windowArgPanel.add(windowArgLabelPanel);
            windowArgPanel.add(windowArgFieldPanel);
            
            
            
            JPanel windowListPanel = new JPanel();
            //windowListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            //windowListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            windowListPanel.setLayout(new BoxLayout(windowListPanel,BoxLayout.Y_AXIS));
            windowListPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            
            label = new JLabel("Windows: ");
            label.setMaximumSize(new Dimension(70,17));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            
            windowListModel = new DefaultListModel();
            windowListModel.addElement(new String("0"));
            
            windowList = new JList(windowListModel);
            windowList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            windowList.setLayoutOrientation(JList.VERTICAL);
            windowList.setVisibleRowCount(7);
            
            JScrollPane windowScrollPane = new JScrollPane(windowList);
            windowScrollPane.setMinimumSize(new Dimension( 80,80));
            windowScrollPane.setPreferredSize(new Dimension( 80,80));
            windowScrollPane.setMaximumSize(new Dimension( 80,80));
            
            
            windowListPanel.add(label);
            windowListPanel.add(windowScrollPane);
            //windowListPanel.setBorder(BorderFactory.createEtchedBorder());
            
            JPanel windowBoxPanel = new JPanel();
            windowBoxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            windowBoxPanel.setLayout(new BoxLayout(windowBoxPanel, BoxLayout.X_AXIS));
            windowBoxPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,5));
            
            windowBoxPanel.setBorder(BorderFactory.createEtchedBorder());
            windowBoxPanel.add(windowButtonPanel);
            windowBoxPanel.add(windowArgPanel);
            windowBoxPanel.add(windowListPanel);
            
            return windowBoxPanel;
        }
    }
    class DisplayTextPanel extends Component {

		private static final long serialVersionUID = -8460617569432301745L;

		public Component make() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            
            JPanel buttonRow2Panel = new JPanel();
            buttonRow2Panel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonRow2Panel.setLayout(new BoxLayout(buttonRow2Panel, BoxLayout.X_AXIS));
            
            displayTextAtButton = new JButton("Dispay Text At");
            displayTextAtButton.setActionCommand("displayTextAt");
            displayTextAtButton.addActionListener(methodListener);
            displayTextAtButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            displayTextAtButton.setEnabled(false);
            buttonPanel.add(displayTextAtButton);
            
            displayTextButton = new JButton("Dispay Text");
            displayTextButton.setActionCommand("displayText");
            displayTextButton.addActionListener(methodListener);
            displayTextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            displayTextButton.setEnabled(false);
            buttonPanel.add(displayTextButton);
            
            clearTextButton = new JButton("Clear Text");
            clearTextButton.setActionCommand("clearText");
            clearTextButton.addActionListener(methodListener);
            clearTextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            clearTextButton.setEnabled(false);
            buttonPanel.add(clearTextButton);
            
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            
            label = new JLabel("Row: ");
            rowPanel.add(label);
            
            rowCombo = new JComboBox();
            rowCombo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rowCombo.setMaximumSize(new Dimension(50,30));
            rowPanel.add(rowCombo);
            
            
            JPanel columnPanel = new JPanel();
            columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));
            
            label = new JLabel("Column: ");
            columnPanel.add(label);
            
            columnCombo = new JComboBox();
            columnCombo.setMaximumSize(new Dimension(50,30));
            columnCombo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            columnPanel.add(columnCombo);
            
            
            JPanel attrPanel = new JPanel();
            attrPanel.setLayout(new BoxLayout(attrPanel, BoxLayout.X_AXIS));
            
            label = new JLabel("Attribute: ");
            attrPanel.add(label);
            attrCombo = new JComboBox();
            attrCombo.setMaximumSize(new Dimension(80,30));
            attrCombo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            attrPanel.add(attrCombo);
            
            rowCombo.setEnabled(false);
            columnCombo.setEnabled(false);
            attrCombo.setEnabled(false);
            
            
            JPanel argPanel = new JPanel();
            argPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            argPanel.setLayout(new BoxLayout(argPanel, BoxLayout.Y_AXIS));
            
            argPanel.add(rowPanel);
            argPanel.add(columnPanel);
            argPanel.add(attrPanel);
            
            JPanel textControlPanel = new JPanel();
            textControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            textControlPanel.setLayout(new BoxLayout(textControlPanel,BoxLayout.X_AXIS));
            textControlPanel.add(argPanel);
            
            
            moveCursorButton = new JButton("Move Cursor");
            moveCursorButton.setActionCommand("moveCursor");
            moveCursorButton.addActionListener(methodListener);
            moveCursorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            moveCursorButton.setEnabled(false);
            
            buttonRow2Panel.add(moveCursorButton);
            
            JPanel displayTextPanel = new JPanel();
            displayTextPanel.setLayout(new BoxLayout(displayTextPanel,BoxLayout.Y_AXIS));
            displayTextPanel.add(buttonPanel);
            displayTextPanel.add(buttonRow2Panel);
            
            label = new JLabel("Send to line display: ");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            displayTextPanel.add(label);
            
            lineDisplayData = new JTextField(30);
            lineDisplayData.setMinimumSize(new Dimension(300,20));
            lineDisplayData.setMaximumSize(new Dimension(300,20));
            lineDisplayData.setPreferredSize(new Dimension(300,20));
            lineDisplayData.setEnabled(false);
            
            displayTextPanel.add(lineDisplayData);
            
            textControlPanel.add(argPanel);
            textControlPanel.add(displayTextPanel);
            
            
            return textControlPanel;
        }
    }
    
    class MiscControlPanel extends Component {

		private static final long serialVersionUID = -6781522099126306328L;

		public Component make() {
            JPanel shiftPanel = new JPanel();
            shiftPanel.setLayout( new GridLayout(3,3));
            shiftPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            shiftPanel.setPreferredSize(new Dimension(105,105));
            shiftPanel.setMaximumSize(new Dimension(105,105));
            
            
            shiftUpButton = new JButton(new ImageIcon(LineDisplayPanel.class.getResource("res/lineDisplayUpArrow.png")));
            shiftUpButton.setActionCommand("Shift Up");
            shiftUpButton.addActionListener(methodListener);
            shiftUpButton.setPreferredSize(new Dimension(35,35));
            shiftUpButton.setMaximumSize(new Dimension(35,35));
            //shiftUpButton.setPreferredSize(new Dimension(50,50));
            
            shiftDownButton = new JButton(new ImageIcon(LineDisplayPanel.class.getResource("res/lineDisplayDownArrow.png")));
            shiftDownButton.setActionCommand("Shift Down");
            shiftDownButton.addActionListener(methodListener);
            shiftDownButton.setPreferredSize(new Dimension(35,35));
            shiftDownButton.setMaximumSize(new Dimension(35,35));
            
            shiftRightButton = new JButton(new ImageIcon(LineDisplayPanel.class.getResource("res/lineDisplayRightArrow.png")));
            shiftRightButton.setActionCommand("Shift Right");
            shiftRightButton.addActionListener(methodListener);
            shiftRightButton.setPreferredSize(new Dimension(35,35));
            shiftRightButton.setMaximumSize(new Dimension(35,35));
            
            shiftLeftButton = new JButton(new ImageIcon(LineDisplayPanel.class.getResource("res/lineDisplayLeftArrow.png")));
            shiftLeftButton.setActionCommand("Shift Left");
            shiftLeftButton.addActionListener(methodListener);
            shiftLeftButton.setPreferredSize(new Dimension(35,35));
            shiftLeftButton.setMaximumSize(new Dimension(35,35));
            
            shiftPanel.add(Box.createHorizontalStrut(35));
            shiftPanel.add(shiftUpButton);
            shiftPanel.add(Box.createHorizontalStrut(35));
            shiftPanel.add(shiftLeftButton);
            shiftPanel.add(Box.createHorizontalStrut(35));
            shiftPanel.add(shiftRightButton);
            shiftPanel.add(Box.createHorizontalStrut(35));
            shiftPanel.add(shiftDownButton);
            shiftPanel.add(Box.createHorizontalStrut(35));
            
            JPanel shiftBoxPanel = new JPanel();
            shiftBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            shiftBoxPanel.setLayout(new BoxLayout(shiftBoxPanel, BoxLayout.Y_AXIS));
            shiftBoxPanel.setBorder(BorderFactory.createEtchedBorder());
            
            label = new JLabel("Scroll Text");
            label.setMaximumSize(new Dimension(65,17));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            shiftBoxPanel.add(label);
            shiftBoxPanel.add(shiftPanel);
            
            
            //brightness control
            
            SliderListener slideListener = new SliderListener();
            
            JPanel brightnessPanel = new JPanel();
            brightnessPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            brightnessPanel.setLayout(new BoxLayout(brightnessPanel, BoxLayout.Y_AXIS));
            brightnessPanel.setBorder(BorderFactory.createEtchedBorder());
            
            brightnessSlider = new JSlider(0,100,100);
            brightnessSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
            brightnessSlider.setPreferredSize(new Dimension(75,20));
            brightnessSlider.setMaximumSize(new Dimension(75,20));
            brightnessSlider.addChangeListener(slideListener);
            brightnessSlider.setEnabled(false);
            
            label = new JLabel("Device Brightness");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            brightnessPanel.add(label);
            brightnessPanel.add(brightnessSlider);
            
            JPanel miscGridPanel = new JPanel();
            miscGridPanel.setLayout( new BoxLayout( miscGridPanel, BoxLayout.X_AXIS));
            miscGridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            miscGridPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            
            //Blink Rate
            JPanel blinkRatePanel = new JPanel();
            blinkRatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            blinkRatePanel.setLayout(new BoxLayout(blinkRatePanel, BoxLayout.Y_AXIS));
            blinkRatePanel.setBorder(BorderFactory.createEtchedBorder());
            
            brLabel = new JLabel("Blink Rate");
            brLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            brVal = new JTextField();
            brVal.setMaximumSize(new Dimension(50,17));
            brVal.setPreferredSize(new Dimension(50,17));
            brVal.setMinimumSize(new Dimension(50,17));
            
            blinkRateButton = new JButton("Set Blink Rate");
            blinkRateButton.setActionCommand("blinkRate");
            blinkRateButton.addActionListener(methodListener);
            blinkRateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // disable this, and only enable if capBlinkRate is true on open
            blinkRateButton.setEnabled(false);
            brVal.setEnabled(false);
            
            blinkRatePanel.add(brLabel);
            blinkRatePanel.add(brVal);
            blinkRatePanel.add(blinkRateButton);
            
            //descriptors
            JPanel descriptorsPanel = new JPanel();
            descriptorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            descriptorsPanel.setLayout(new BoxLayout(descriptorsPanel, BoxLayout.Y_AXIS));
            descriptorsPanel.setBorder(BorderFactory.createEtchedBorder());
            
            label = new JLabel("Descriptors");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            descCombo = new JComboBox();
            descCombo.setMaximumSize(new Dimension(50,30));
            descCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            descOnButton= new JButton("On");
            descOnButton.setActionCommand("descOn");
            descOnButton.addActionListener(methodListener);
            descOnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            descOffButton = new JButton("Off");
            descOffButton.setActionCommand("descOff");
            descOffButton.addActionListener(methodListener);
            descOffButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            descClearButton = new JButton("Clear All");
            descClearButton.setActionCommand("descClear");
            descClearButton.addActionListener(methodListener);
            descClearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // disable this, and only enable if capDescriptors is true on open
            descCombo.setEnabled(false);
            descOnButton.setEnabled(false);
            descOffButton.setEnabled(false);
            descClearButton.setEnabled(false);
            
            descriptorsPanel.add(label);
            descriptorsPanel.add(descCombo);
            descriptorsPanel.add(descOnButton);
            descriptorsPanel.add(descOffButton);
            descriptorsPanel.add(descClearButton);
            
            miscGridPanel.add(shiftBoxPanel);
            miscGridPanel.add(brightnessPanel);
            miscGridPanel.add(blinkRatePanel);
            miscGridPanel.add(descriptorsPanel);
            return miscGridPanel;
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(lineDisplay != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(lineDisplay.getState()));
            }
        }
    }
}

