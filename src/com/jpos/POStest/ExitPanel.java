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
 * Created on Jun 21, 2004
 */
  
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ExitPanel extends Component {
					   
	private static final long serialVersionUID = -8548674045237604058L;

	public Component make() {
		
		JPanel subPanel = new JPanel(false);
		JLabel  jlabel = new JLabel("Exit the Program");
		subPanel.add(jlabel);
				
		JButton exit = new JButton("EXIT");
		exit.setToolTipText("Terminate");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				exitProgram();
			}
		});
				
		subPanel.add(exit);
		
		return subPanel;
	}
	
	private void exitProgram() {
		System.out.println("...Exit JavaPOS Tester...\n");
		System.exit(0);
	}

}
