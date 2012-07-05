/*
 * Created on May 16, 2005, 3:53 PM
 *
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
 * ErrorPromptDlg.java a generic dialog box for displaying JavaPOS Error Events
 *
 */

package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.*;
import jpos.*;
import java.awt.event.*;
import java.awt.*;

/**
 *
 * @author Jeff Lange
 */
public class ErrorPromptDlg extends JDialog implements ActionListener{
    
	private static final long serialVersionUID = -4332027575682829695L;
	
	private jpos.events.ErrorEvent errorEvent;
    private JRadioButton clearRadio;
    private JRadioButton retryRadio;
    private JRadioButton continueRadio;
    private JTextArea messageWindow;
           
    /** Creates a new instance of ErrorPromptDlg
     * @param event the JavaPOS Error Event to display and set the status of.
     * @param msg a custom message to display in the dialog.
     */
    public ErrorPromptDlg(jpos.events.ErrorEvent event, String msg) {
        errorEvent = event;
        
        setModal(true);
        setSize(500, 350);
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
        
        messageWindow = new JTextArea();
        messageWindow.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        JScrollPane scrollPane = new JScrollPane(messageWindow);
        scrollPane.setPreferredSize(new Dimension(Short.MAX_VALUE, 100));
        row1.add(scrollPane);
        messageWindow.setText(msg);
        
        JLabel label = new JLabel();
        String text = "Error Locus: ";
        switch(errorEvent.getErrorLocus()){
            case JposConst.JPOS_EL_INPUT:
                text += "Input";
                break;
            case JposConst.JPOS_EL_INPUT_DATA:
                text += "Input with Data";
                break;
            case JposConst.JPOS_EL_OUTPUT:
                text += "Output";
                break;
            default:
                text += "Unknown.. This is an error with the SO!!!";
                break;
        }
        label.setText(text);
        row2.add(label);
        
        ButtonGroup group = new ButtonGroup();
        
        retryRadio= new JRadioButton("Retry");
        retryRadio.setSelected(errorEvent.getErrorResponse() == JposConst.JPOS_ER_RETRY);
        group.add(retryRadio);
        row3.add(retryRadio);
        
        clearRadio = new JRadioButton("Clear");
        clearRadio.setSelected(errorEvent.getErrorResponse() == JposConst.JPOS_ER_CLEAR);
        group.add(clearRadio);
        row3.add(clearRadio);
        
        continueRadio = new JRadioButton("Continue Input");
        continueRadio.setSelected(errorEvent.getErrorResponse() == JposConst.JPOS_ER_CONTINUEINPUT);
        group.add(continueRadio);
        row3.add(continueRadio);
        
        JButton closeButton = new JButton("OK");
        closeButton.setActionCommand("OK");
        closeButton.addActionListener(this);
        row4.add(closeButton);
        
        row5.add(Box.createVerticalStrut(10));
        
        setTitle("Error Event");
    }
    
    public void actionPerformed(ActionEvent ae) {
        int resp = 0;
        if(ae.getActionCommand().equals("OK")){
            if(clearRadio.isSelected()){
                resp = JposConst.JPOS_ER_CLEAR;
            }else if(retryRadio.isSelected()){
                resp = JposConst.JPOS_ER_RETRY;
            }else if(continueRadio.isSelected()){
                resp = JposConst.JPOS_ER_CONTINUEINPUT;
            }
            
            errorEvent.setErrorResponse(resp);
            
            setVisible(false);
        }
    }
}
