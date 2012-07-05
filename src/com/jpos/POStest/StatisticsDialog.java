/*
 *   This software is provided "AS IS".  The JavaPOS working group (including
 *   each of the Corporate members, contributors and individuals)  MAKES NO
 *   REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 *   EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NON-INFRINGEMENT. The JavaPOS working group shall not be liable for
 *   any damages suffered as a result of using, modifying or distributing this
 *   software or its derivatives. Permission to use, copy, modify, and distribute
 *   the software and its documentation for any purpose is hereby granted
 */

/*
 * StatisticsDialog.java
 *
 * Created on April 21, 2005, 7:34 AM
 */
package com.jpos.POStest;

import javax.swing.*;
import jpos.*;
import java.lang.reflect.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Jeff Lange
 */
public class StatisticsDialog extends JDialog implements ActionListener{
    
	private static final long serialVersionUID = -6789011810352167205L;
	private JRadioButton updateRadio;
    private JRadioButton resetRadio;
    private JRadioButton retrieveRadio;
    
    private Method retrieveMethod = null;
    private Method updateMethod   = null;
    private Method resetMethod    = null;
    private Method getCapUpdateStatistics = null;
    private Method getCapStatisticsReporting = null;
    
    private Boolean capUpdateStatistics = new Boolean(false);
    private Boolean capStatisticsReporting = new Boolean(false);
    
    private Object deviceObject;
    
    private JTextField argumentTF;
    private JTextArea messageWindow;
    
    /** Creates a new instance of StatisticsDialog */
    public StatisticsDialog(Object object) throws Exception{
        deviceObject = object;
        Class<? extends Object> c = object.getClass();
        Method[] theMethods = c.getMethods();
        
        for (int i = 0; i < theMethods.length; i++) {
            if(theMethods[i].getName().equals("retrieveStatistics")){
                retrieveMethod = theMethods[i];
            }else if(theMethods[i].getName().equals("updateStatistics")){
                updateMethod = theMethods[i];
            }else if(theMethods[i].getName().equals("resetStatistics")){
                resetMethod = theMethods[i];
            }else if(theMethods[i].getName().equals("getCapUpdateStatistics")){
                getCapUpdateStatistics = theMethods[i];
            }else if(theMethods[i].getName().equals("getCapStatisticsReporting")){
                getCapStatisticsReporting = theMethods[i];
            }
        }
        
        // If we don't have one of these methods, this isn't a supported object type
        if(retrieveMethod == null || updateMethod == null || resetMethod == null) {
            throw new Exception("Object passed to StatisticsDialog() does not support the statistics interface.\nPerhaps the Service Object is UPOS v1.7 complient or lower.");
        }
        
        //check the two caps that are if interest,
        //CapUpdateStatistics and CapStatisticsReporting
        
        try{
            Object[] args = new Object[0];
            capStatisticsReporting = (Boolean)getCapStatisticsReporting.invoke(deviceObject, args);
            capUpdateStatistics = (Boolean)getCapUpdateStatistics.invoke(deviceObject, args);
        }catch(InvocationTargetException e){
            JposException je = (JposException)e.getTargetException();
            JOptionPane.showMessageDialog(null,"exception getting device caps:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
        }catch(IllegalAccessException e){
            JOptionPane.showMessageDialog(null,"call to a getCap function threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
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
        
        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        row2.add(textBox);
        
        ButtonGroup group = new ButtonGroup();
        
        retrieveRadio= new JRadioButton("Retrieve");
        retrieveRadio.setEnabled(capStatisticsReporting.booleanValue());
        retrieveRadio.setSelected(true);
        group.add(retrieveRadio);
        row1.add(retrieveRadio);
        
        resetRadio = new JRadioButton("Reset");
        resetRadio.setSelected(false);
        resetRadio.setEnabled(capUpdateStatistics.booleanValue());
        group.add(resetRadio);
        row1.add(resetRadio);
        
        updateRadio = new JRadioButton("Update");
        updateRadio.setSelected(false);
        updateRadio.setEnabled(capUpdateStatistics.booleanValue());
        group.add(updateRadio);
        row1.add(updateRadio);
        
        row1.add(Box.createHorizontalStrut(150));
        
        JButton goButton = new JButton("Go");
        goButton.setActionCommand("Go");
        goButton.addActionListener(this);        
        row1.add(goButton);
        
        row1.add(Box.createHorizontalStrut(5));
        
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand("Close");
        closeButton.addActionListener(this);
        row1.add(closeButton);
        
        JLabel label = new JLabel("\"Argument\" is the value to be passed to the statistics methods.");
        textBox.add(label);
        label = new JLabel("Separate multiple statistics with commas.");
        textBox.add(label);
        label = new JLabel("When updating statistics, use name=value pairs.");
        textBox.add(label);
        
        
        label = new JLabel("Argument:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        label.setEnabled(capUpdateStatistics.booleanValue());
        row3.add(label);
        
        argumentTF = new JTextField();
        argumentTF.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        argumentTF.setPreferredSize(new Dimension(200, 25));
        argumentTF.setEnabled(capStatisticsReporting.booleanValue());
        row3.add(argumentTF);
        row3.add(Box.createHorizontalStrut(150));
        
        messageWindow = new JTextArea();
        messageWindow.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        JScrollPane scrollPane = new JScrollPane(messageWindow);
        scrollPane.setPreferredSize(new Dimension(Short.MAX_VALUE, 200));
        row4.add(scrollPane);
        
        if(!capStatisticsReporting.booleanValue()){
            messageWindow.setText("capStatisticsReporting is false for this device");
            goButton.setEnabled(false);            
        }
        setTitle("Device Statistics");
    }
    
    public void actionPerformed(ActionEvent ae) {
        if(ae.getActionCommand().equals("Close")){
            setVisible(false);
        } else if(ae.getActionCommand().equals("Go")){
            if(updateRadio.isSelected()){
                try{
                    Object[] args = new Object[1];
                    args[0] = argumentTF.getText();
                    updateMethod.invoke(deviceObject, args);
                }catch(InvocationTargetException e){
                    JposException je = (JposException)e.getTargetException();
                    JOptionPane.showMessageDialog(null,"updateStatistics threw an exception:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }catch(IllegalAccessException e){
                    JOptionPane.showMessageDialog(null,"call to updateStatistics threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }
            }
            
            if(resetRadio.isSelected()){
                try{
                    Object[] args = new Object[1];
                    args[0] = argumentTF.getText();
                    resetMethod.invoke(deviceObject, args);
                }catch(InvocationTargetException e){
                    JposException je = (JposException)e.getTargetException();
                    JOptionPane.showMessageDialog(null,"resetStatistics threw an exception:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }catch(IllegalAccessException e){
                    JOptionPane.showMessageDialog(null,"call to resetStatistics threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }
            }
            
            if(retrieveRadio.isSelected()){
                try{
                    Object[] args = new Object[1];
                    String[] stats = new String[1];
                    stats[0] = argumentTF.getText();
                    
                    args[0] = stats;
                    retrieveMethod.invoke(deviceObject, args);
                    messageWindow.setText(stats[0]);
                }catch(InvocationTargetException e){
                    JposException je = (JposException)e.getTargetException();
                    JOptionPane.showMessageDialog(null,"retrieveStatistics threw an exception:\n" +je.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }catch(IllegalAccessException e){
                    JOptionPane.showMessageDialog(null,"call to retrieveStatistics threw an IllegalAccessException:\n" +e.getMessage() ,"Exception",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
