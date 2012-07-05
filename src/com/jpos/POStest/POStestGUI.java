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
// POStestGUI.java - The overall GUI container
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class POStestGUI extends JPanel implements  ActionListener{
    
	private static final long serialVersionUID = -1541022284758514323L;

	public POStestGUI()  {
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        AboutTab about = new AboutTab();
        tabbedPane.addTab("About", null, about.make(), "About");
        
        ConfiguredDevicesPanel devPanel = new ConfiguredDevicesPanel(); // 6/15
        tabbedPane.addTab("Configured Devices", null, devPanel.make(), "Configured Devices");
        
        BiometricsPanel biometricsPanel = new BiometricsPanel();
        tabbedPane.addTab("Biometrics", null, biometricsPanel.make(), "Biometrics");
        
        BumpBarPanel bumpBarPanel = new BumpBarPanel();
        tabbedPane.addTab("Bump/Bar", null, bumpBarPanel.make(), "Bump-Bar");
        
        CashChangerPanel cashChangerPanel = new CashChangerPanel();
        tabbedPane.addTab("CashChanger", null, cashChangerPanel.make(), "CashChanger");
        
        CashDrawerPanel cashDrawerPanel1 = new CashDrawerPanel();
        tabbedPane.addTab("CashDrawer", null, cashDrawerPanel1.make(), "CashDrawer");
        
        CATPanel catPanel = new CATPanel();
        tabbedPane.addTab("CAT", null, catPanel.make(), "CAT");
        
        CheckScannerPanel checkScannerPanel = new CheckScannerPanel();
        tabbedPane.addTab("CheckScanner", null, checkScannerPanel.make(), "CheckScanner");
        
        CoinDispenserPanel coinDispenserPanel = new CoinDispenserPanel();
        tabbedPane.addTab("CoinDispenser", null, coinDispenserPanel.make(), "CoinDispenser");
        
        ElectronicJournalPanel electronicJournalPanel = new ElectronicJournalPanel();
        tabbedPane.addTab("ElectronicJournal", null, electronicJournalPanel.make(), "ElectronicJournal");
        
        FiscalPrinterPanel fiscalPrinterPanel = new FiscalPrinterPanel();
        tabbedPane.addTab("FiscalPrinter", null, fiscalPrinterPanel.make(), "FiscalPrinter");
        
        HardTotalsPanel hardTotalsPanel = new HardTotalsPanel();
        tabbedPane.addTab("HardTotals", null, hardTotalsPanel.make(), "HardTotals");
        
        KeylockPanel keylockPanel = new KeylockPanel();
        tabbedPane.addTab("Keylock", null, keylockPanel.make(), "Keylock");
        
        LineDisplayPanel lineDisplayPanel = new LineDisplayPanel();
        tabbedPane.addTab("LineDisplay", null, lineDisplayPanel.make(), "LineDisplay");
        
        MICRPanel micrPanel = new MICRPanel();
        tabbedPane.addTab("MICR", null, micrPanel.make(), "MICR");
        
        MotionSensorPanel motionSensorPanel = new MotionSensorPanel();
        tabbedPane.addTab("MotionSensor", null, motionSensorPanel.make(), "MotionSensor");
        
        MSRPanel msrPanel = new MSRPanel();
        tabbedPane.addTab("MSR", null, msrPanel.make(), "MSR");
        
        PINPadPanel pinpadPanel = new PINPadPanel();
        tabbedPane.addTab("PINPad", null, pinpadPanel.make(), "PINPad");
        
        PointCardRWPanel pointCardRWPanel = new PointCardRWPanel();
        tabbedPane.addTab("PointCardRW", null, pointCardRWPanel.make(), "PointCardRW");
        
        POSKeyboardPanel posKeyboardPanel = new POSKeyboardPanel();
        tabbedPane.addTab("POSKeyboard", null, posKeyboardPanel.make(), "POSKeyboard");
        
        POSPowerPanel posPowerPanel = new POSPowerPanel();
        tabbedPane.addTab("POSPower", null, posPowerPanel.make(), "POSPower");
        
        POSPrinterPanel posPrinterPanel = new POSPrinterPanel();
        tabbedPane.addTab("POSPrinter", null, posPrinterPanel.make(), "POSPrinter");
        
        RemoteOrderDisplayPanel remoteOrderDisplayPanel = new RemoteOrderDisplayPanel();
        tabbedPane.addTab("RemoteOrderDisplay", null, remoteOrderDisplayPanel.make(), "RemoteOrderDisplay");
        
        ScalePanel scalePanel = new ScalePanel();
        tabbedPane.addTab("Scale", null, scalePanel.make(), "Scale");
        
        ScannerPanel scannerPanel = new ScannerPanel();
        tabbedPane.addTab("Scanner", null, scannerPanel.make(), "Scanner");
        
        SigpadPanel sigpadPanel = new SigpadPanel();
        tabbedPane.addTab("SignatureCapture", null, sigpadPanel.make(), "SignatureCapture");
        
        SmartCardRWPanel smartCardRWPanel = new SmartCardRWPanel();
        tabbedPane.addTab("SmartCardRW", null, smartCardRWPanel.make(), "SmartCardRW");
        
        ToneIndicatorPanel toneIndicatorPanel = new ToneIndicatorPanel();
        tabbedPane.addTab("ToneIndicator", null, toneIndicatorPanel.make(), "ToneIndicator");
        
        ExitPanel exitPanel = new ExitPanel();
        tabbedPane.addTab("Exit", null, exitPanel.make(), "Exit");
        
        tabbedPane.setSelectedIndex(0);
        
        
        //Add the tabbed pane to this panel.
        setLayout(new GridLayout(1,1));
        add(tabbedPane);
        
        
        char k = 'á';
        KeyStroke ks = KeyStroke.getKeyStroke(k);
        this.registerKeyboardAction(this,ks,JComponent.WHEN_IN_FOCUSED_WINDOW);
        
    }
    
    
    
    
    public void actionPerformed(ActionEvent ae) {
    }
    
    
}
