package com.jpos.POStest;
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

/**
 * @author Jeff Lange
 *
 * This will display a JTable with the entries from the jpos.xml file in it.
 */
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import jpos.config.simple.*;
import jpos.config.simple.xml.*;
import jpos.config.*;

import java.util.*;

public class ConfiguredDevicesPanel extends Component{

	private static final long serialVersionUID = 8895419610452248342L;

	public Component make() {
		
		JPanel mainPanel = new JPanel(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(20));
		
		JLabel label = new JLabel("Below is a list of the devices found in your jpos.xml file.");
		mainPanel.add(label);
		
		SimpleEntryRegistry reg = new SimpleEntryRegistry(new SimpleXmlRegPopulator());
		reg.load();
		
		String[] colNames = {"Category", "Logical Name", "Vendor", "Product Name"};
		
		Object[][] data = new Object[reg.getSize()][4]; 
		
		Enumeration<?> entriesEnum = reg.getEntries();
		int count = 0;
		while(entriesEnum.hasMoreElements()){
			JposEntry entry = (JposEntry)entriesEnum.nextElement();
			Object[] row = { entry.getProp(JposEntry.DEVICE_CATEGORY_PROP_NAME).getValueAsString(),
										entry.getLogicalName(),
										entry.getProp(JposEntry.VENDOR_NAME_PROP_NAME).getValueAsString(),
										entry.getProp(JposEntry.PRODUCT_NAME_PROP_NAME).getValueAsString() };
			data[count] = row;			
			count++;
		}
		JTable table = new JTable(data, colNames);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(700,200));

		//Adjust the column widths
		//This is really kudgy, but it seems to work (a little)
		TableColumn column = null;
		int maxsize;
		int size;
		
		for(int i=0; i<4; i++){
			column = table.getColumnModel().getColumn(i);
			maxsize = 0;
			for(int j=0; j< count; j++){
				size = ((String)data[j][i]).length();
				if( maxsize < (size * 5)){
					maxsize = size * 5;
				}
			}
			column.setPreferredWidth(maxsize);
		}

		mainPanel.add(scrollPane);
		
		return mainPanel;
	}
}
