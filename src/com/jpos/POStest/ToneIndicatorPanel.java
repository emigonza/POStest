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
// ToneIndicatorPanel.java - The ToneIndicator panel of POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-14-2004
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class ToneIndicatorPanel extends Component
        implements StatusUpdateListener, ActionListener {
    
	private static final long serialVersionUID = -385357392108807346L;

	protected MainButtonPanel mainButtonPanel;
    
    private ToneIndicator toneIndicator;
    private String defaultLogicalName = "defaultToneIndicator";
    private String logicalName = "";
    
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    boolean toneOpened = false;
    
    // commands
    private JButton toneSoundButton = new JButton("Sound");
    private JButton toneSoundImmediateButton = new JButton("Sound Immediate");
    
    // command parameters
    private JLabel toneNumberOfCyclesLabel = new JLabel("Number of Cycles");
    private JTextField toneNumberOfCyclesField = new JTextField("1",5);
    private JLabel toneInterSoundWaitLabel = new JLabel("InterSound Wait");
    private JTextField toneInterSoundWaitField = new JTextField("0",5);
    
    // properites
    private JCheckBox toneCapPitchBox = new JCheckBox("CapPitch");
    private JCheckBox toneCapVolumeBox = new JCheckBox("CapVolume");
    private JCheckBox toneAsyncModeBox = new JCheckBox("AsyncMode");
    private JLabel toneInterToneWaitLabel = new JLabel("InterTone Wait");
    private JTextField toneInterToneWaitField = new JTextField("0",5);
    private JLabel toneTone1DurationLabel = new JLabel("Duration");
    private JTextField toneTone1DurationField = new JTextField("100",5);
    private JLabel toneTone1PitchLabel = new JLabel("Pitch");
    private JTextField toneTone1PitchField = new JTextField("100",5);
    private JLabel toneTone1VolumeLabel = new JLabel("Volume");
    private JTextField toneTone1VolumeField = new JTextField("1",5);
    private JLabel toneTone2DurationLabel = new JLabel("Duration");
    private JTextField toneTone2DurationField = new JTextField("100",5);
    private JLabel toneTone2PitchLabel = new JLabel("Pitch");
    private JTextField toneTone2PitchField = new JTextField("100",5);
    private JLabel toneTone2VolumeLabel = new JLabel("Volume");
    private JTextField toneTone2VolumeField = new JTextField("1",5);
    
    
    
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    public ToneIndicatorPanel() {
        toneIndicator = new ToneIndicator();
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //MethodListener methodListener = new MethodListener();
        
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
        
        JPanel tonePanel = new JPanel();
        
        GridBagLayout toneLayout = new GridBagLayout();
        GridBagConstraints toneConstraints = new GridBagConstraints();
        
        //gridx set separately for each component
        toneConstraints.gridy = 0;
        toneConstraints.gridwidth = 1;
        toneConstraints.gridheight = 1;
        //toneConstraints.ipadx = 20;
        toneConstraints.insets = new Insets(0, 10, 0, 10);  // 10 pixel spaces to left,right
        toneConstraints.fill = GridBagConstraints.NONE;     // the default
        toneConstraints.anchor = GridBagConstraints.CENTER; // the default
        
        tonePanel.setLayout(toneLayout);
        
        JPanel toneCommandsPanel = new JPanel();
        toneCommandsPanel.setBorder(new TitledBorder("*** Commands ***"));
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        toneCommandsPanel.setLayout( layout );
        
        // row 0
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;     //the default
        constraints.anchor = GridBagConstraints.CENTER; //the default
        layout.setConstraints(toneSoundButton, constraints);
        toneSoundButton.setToolTipText("use cycles");
        toneCommandsPanel.add(toneSoundButton);
        
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        layout.setConstraints(toneSoundButton, constraints);
        toneSoundImmediateButton.setToolTipText("no cycles");
        toneCommandsPanel.add(toneSoundImmediateButton);
        
        
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = 2;
        layout.setConstraints(toneNumberOfCyclesLabel, constraints);
        toneCommandsPanel.add(toneNumberOfCyclesLabel);
        
        constraints.gridx = 1;
        constraints.gridy = 2;
        layout.setConstraints(toneNumberOfCyclesField, constraints);
        toneCommandsPanel.add(toneNumberOfCyclesField);
        toneNumberOfCyclesField.setToolTipText("enter integer");
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        layout.setConstraints(toneInterSoundWaitLabel, constraints);
        toneCommandsPanel.add(toneInterSoundWaitLabel);
        
        constraints.gridx = 1;
        constraints.gridy = 3;
        layout.setConstraints(toneInterSoundWaitField, constraints);
        toneCommandsPanel.add(toneInterSoundWaitField);
        toneInterSoundWaitField.setToolTipText("in milliseconds");
        toneConstraints.gridx = 0;
        toneConstraints.gridheight = 2;
        toneLayout.setConstraints(toneCommandsPanel, toneConstraints);
        //tonePanel.add(toneCommandsPanel); *// look at more
        
        mainPanel.add(toneCommandsPanel); // 2:19p
        
        
        //set up listeners to command buttons
        toneSoundButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone1Duration(Integer.parseInt(toneTone1DurationField.getText()));
                    toneIndicator.setTone1Pitch(Integer.parseInt(toneTone1PitchField.getText()));
                    toneIndicator.setTone1Volume(Integer.parseInt(toneTone1VolumeField.getText()));
                    toneIndicator.setTone2Duration(Integer.parseInt(toneTone2DurationField.getText()));
                    toneIndicator.setTone2Pitch(Integer.parseInt(toneTone2PitchField.getText()));
                    toneIndicator.setTone2Volume(Integer.parseInt(toneTone2VolumeField.getText()));
                    toneIndicator.setInterToneWait(Integer.parseInt(toneInterToneWaitField.getText()));
                    
                    int cycles = Integer.parseInt(toneNumberOfCyclesField.getText());
                    int wait = Integer.parseInt(toneInterSoundWaitField.getText());
                    toneIndicator.sound(cycles, wait);
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                    
                } catch( NumberFormatException e ) {
                    System.err.println("Sound parameter not a number\n" + e); }
            }
        });
        
        toneSoundImmediateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.soundImmediate();
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });
        
        // use the standard listener for OutputComplete and Error Events
        
        JPanel tonePropertiesPanel = new JPanel();
        tonePropertiesPanel.setLayout(new FlowLayout());
        
        JPanel tone1Panel = new JPanel();
        tone1Panel.setBorder(new TitledBorder("Tone 1 Properties"));
        tone1Panel.setLayout(new GridLayout(3, 2));
        toneTone1DurationField.setToolTipText("in milliseconds");
        toneTone1PitchField.setToolTipText("in hertz");
        toneTone1VolumeField.setToolTipText("range is 0 (silent) to 100");
        tone1Panel.add(toneTone1DurationLabel);
        tone1Panel.add(toneTone1DurationField);
        tone1Panel.add(toneTone1PitchLabel);
        tone1Panel.add(toneTone1PitchField);
        tone1Panel.add(toneTone1VolumeLabel);
        tone1Panel.add(toneTone1VolumeField);
        toneConstraints.gridx = 1;
        toneConstraints.gridheight = 1;
        toneLayout.setConstraints(tone1Panel, toneConstraints);
        //tonePanel.add(tone1Panel);
        mainPanel.add(tone1Panel);
        
        JPanel tone2Panel = new JPanel();
        tone2Panel.setBorder(new TitledBorder("Tone 2 Properties"));
        tone2Panel.setLayout(new GridLayout(3, 2));
        toneTone2DurationField.setToolTipText("in milliseconds");
        toneTone2PitchField.setToolTipText("in hertz");
        toneTone2VolumeField.setToolTipText("range is 0 (silent) to 100");
        tone2Panel.add(toneTone2DurationLabel);
        tone2Panel.add(toneTone2DurationField);
        tone2Panel.add(toneTone2PitchLabel);
        tone2Panel.add(toneTone2PitchField);
        tone2Panel.add(toneTone2VolumeLabel);
        tone2Panel.add(toneTone2VolumeField);
        toneConstraints.gridx = 2;
        toneConstraints.gridheight = 1;
        toneLayout.setConstraints(tone2Panel, toneConstraints);
        //tonePanel.add(tone2Panel);
        mainPanel.add(tone2Panel);
        
        JPanel toneWaitPanel = new JPanel();
        toneWaitPanel.setLayout( new GridLayout(1,2) );
        toneWaitPanel.add(toneInterToneWaitLabel);
        toneWaitPanel.add(toneInterToneWaitField);
        toneConstraints.gridx = 1;
        toneConstraints.gridy = 1;
        toneConstraints.gridwidth = 2;
        toneLayout.setConstraints(toneWaitPanel, toneConstraints);
        //tonePanel.add(toneWaitPanel);
        mainPanel.add(toneWaitPanel);
        
        JPanel toneMiscPanel = new JPanel();
        layout = new GridBagLayout();
        toneMiscPanel.setLayout( layout );
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(toneCapPitchBox, constraints);
        toneMiscPanel.add(toneCapPitchBox);
        constraints.gridy = 1;
        layout.setConstraints(toneCapVolumeBox, constraints);
        toneMiscPanel.add(toneCapVolumeBox);
        
        toneCapPitchBox.setEnabled(false);
        toneCapVolumeBox.setEnabled(false);
        
        constraints.gridy = 2;
        layout.setConstraints(toneAsyncModeBox, constraints);
        toneMiscPanel.add(toneAsyncModeBox);
        
        toneConstraints.gridx = 3;
        toneConstraints.gridy = 0;
        toneConstraints.gridwidth = 1;
        toneConstraints.gridheight = 2;
        toneLayout.setConstraints(toneMiscPanel, toneConstraints);
        //tonePanel.add(toneMiscPanel);
        mainPanel.add(toneMiscPanel);
        
        JScrollPane toneScroll = new JScrollPane();
        toneScroll.setViewportView(mainPanel);
        
        
        // add listeners for each of the settable properties
        toneAsyncModeBox.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                boolean state = toneAsyncModeBox.isSelected();
                try {
                    toneIndicator.setAsyncMode(state);
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                    toneAsyncModeBox.setSelected(!state);
                }
            }
        });
        
        toneTone1DurationField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone1Duration(Integer.parseInt(toneTone1DurationField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                } catch( NumberFormatException e) {
                    System.err.println("Tone1Duration: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneTone1PitchField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone1Pitch(Integer.parseInt(toneTone1PitchField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("Tone1Pitch: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneTone1VolumeField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone1Volume(Integer.parseInt(toneTone1VolumeField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("Tone1Volume: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneTone2DurationField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone2Duration(Integer.parseInt(toneTone2DurationField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("Tone2Duration: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneTone2PitchField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone2Pitch(Integer.parseInt(toneTone2PitchField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("Tone2Pitch: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneTone2VolumeField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setTone2Volume(Integer.parseInt(toneTone2VolumeField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("Tone2Volume: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        
        toneInterToneWaitField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                try {
                    toneIndicator.setInterToneWait(Integer.parseInt(toneInterToneWaitField.getText()));
                } catch( JposException e ) {
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName +
                            "\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                } catch( NumberFormatException e) {
                    System.err.println("InterToneWait: not a number\n" + e);
                }
                //initCurrentDeviceProperties();
            }
        });
        return mainPanel;
        
    }
    
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        System.out.println("ToneIndicator received status update event.");
    }
    
    
    /** Listens to the method buttons. */
    //class MethodListener implements ActionListener{
    
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getActionCommand().equals("open")){
            try{
                if(logicalName.equals("")){
                    logicalName = defaultLogicalName;
                }
                toneIndicator.addStatusUpdateListener(this);
                
                toneIndicator.open(logicalName);
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                int version = toneIndicator.getDeviceServiceVersion();
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
                toneIndicator.claim(0);
                
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("release")){
            try{
                toneIndicator.release();
                
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("close")){
            try{
                toneIndicator.close();
                deviceEnabledCB.setEnabled(false);
                freezeEventsCB.setEnabled(false);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("info")){
            try{
                String ver = new Integer(toneIndicator.getDeviceServiceVersion()).toString();
                String msg = "Service Description: " + toneIndicator.getDeviceServiceDescription();
                msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                ver = new Integer(toneIndicator.getDeviceControlVersion()).toString();
                msg += "\n\nControl Description: " + toneIndicator.getDeviceControlDescription();
                msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                msg += "\n\nPhysical Device Name: " + toneIndicator.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + toneIndicator.getPhysicalDeviceDescription();
                
                msg += "\n\nProperties:\n------------------------";
                
                msg += "\nCapPowerReporting: " + (toneIndicator.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (toneIndicator.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                if(ver_18_complient)
                {
                	msg += "\nCapStatisticsReporting: " + toneIndicator.getCapStatisticsReporting();                    
                	msg += "\nCapUpdateStatistics: " + toneIndicator.getCapUpdateStatistics();
                }
                else
                {
                	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                }
                
                if(ver_19_complient)
                {
                	msg += "\nCapCompareFirmwareVersion: " + toneIndicator.getCapCompareFirmwareVersion();                    
                	msg += "\nCapUpdateFirmware: " + toneIndicator.getCapUpdateFirmware();
                }
                else
                {
                	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                }
                
                msg += "\nCapPitch: " + toneIndicator.getCapPitch();
                msg += "\nCapVolume: " + toneIndicator.getCapVolume();
                
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
                toneIndicator.addStatusUpdateListener(this);
                toneIndicator.open(logicalName);
                toneIndicator.claim(0);
                deviceEnabledCB.setEnabled(true);
                freezeEventsCB.setEnabled(true);
                toneIndicator.setDeviceEnabled(true);
                
            }catch(JposException e){
                JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        }else if(ae.getActionCommand().equals("stats")) {
            try{
                StatisticsDialog dlg = new StatisticsDialog(toneIndicator);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }else if(ae.getActionCommand().equals("firmware")) {
            try{
                FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(toneIndicator);
                dlg.setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        try {
            deviceEnabledCB.setSelected(toneIndicator.getDeviceEnabled());
            freezeEventsCB.setSelected(toneIndicator.getFreezeEvents());
        } catch(JposException je) {
            System.err.println("ToneIndicatorPanel: MethodListener: JposException");
        }
    }
    //}
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        toneIndicator.setDeviceEnabled(false);
                    }else{
                        toneIndicator.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        toneIndicator.setFreezeEvents(false);
                    }else{
                        toneIndicator.setFreezeEvents(true);
                    }
                }
            } catch(JposException je) {
                System.err.println("ToneIndicatorPanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(toneIndicator != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(toneIndicator.getState()));
            }
        }
    }
}
