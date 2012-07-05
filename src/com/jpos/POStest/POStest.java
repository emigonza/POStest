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
// POStest.java - The main class for POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-15-2004 JMenuBar, JMenuItem, ActionListeners
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.*; 			// no windowClosing event

public class POStest extends JMenuBar {		// 6/21 whole gui is now JFrame

	private static final long serialVersionUID = 2025128872901159912L;

	public static final String version = "v1.10.0";
	
	private JMenuBar menubar;

	private JMenuItem Exit;

	
	public POStest(){
		
		menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		menubar.add(fileMenu);

		menubar.setBorder(new BevelBorder(BevelBorder.RAISED));

		Exit = new JMenuItem("Exit");

		fileMenu.add(Exit);
	
		add(fileMenu);
	
		hookUpEvents();
	}
	
	private void hookUpEvents(){

		Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}			
		});

	}
	
	public static void main(String[] args) {
		    
		POStestGUI gui = new POStestGUI();
	                           
        JFrame frame = new JFrame();
     
        frame.setTitle("JavaPOStester in Progress");			// 6-21
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setJMenuBar(new POStest());						// 7-15
        frame.getContentPane().add(gui,BorderLayout.CENTER);  
        frame.pack();											// 6-21
        frame.setSize(850,700 );
        frame.setVisible(true);

    }

}


    

