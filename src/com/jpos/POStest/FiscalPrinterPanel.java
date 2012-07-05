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
 *
 * -----------------------------------------------------------------------------
 * contribution of interface and implementation Bracci A. Sistemi Digitali s.r.l. Pisa (Italy)
 */

package com.jpos.POStest;

import javax.swing.*;
import jpos.events.OutputCompleteEvent;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import java.awt.*;
import java.awt.event.*;
import jpos.FiscalPrinterConst;
import jpos.*;
import jpos.events.*;


public class FiscalPrinterPanel extends Component implements StatusUpdateListener,OutputCompleteListener,ErrorListener{

	private static final long serialVersionUID = -1141207648205875965L;
	
	protected MainButtonPanel mainButtonPanel;
    private FiscalPrinter fiscalPrinter;
    private String defaultLogicalName = "fiscalprinter";
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox doubleWidthCB;
    private JCheckBox duplicateReceiptCB;
    private JCheckBox asyncModeCB;
    private JCheckBox checkTotalCB;
    private JCheckBox flagWhenIdleCB;
    private JCheckBox clearOutputForErrorCB;
    private JList itemList;
    private DefaultListModel itemListModel;
    private JList propList;
    private DefaultListModel propListModel;
    private JComboBox paymantFormCombo;
    private JComboBox vatIdCombo;
    private JComboBox vatIdCombo_2;
    private JComboBox adjustmentTypeCombo;
    private JComboBox reportTypeCombo;
    private JComboBox totalTypeCombo;
    private JRadioButton firmwareRB;
    private JRadioButton printerIDRB;
    private JRadioButton currentTotalRB;
    private JRadioButton grandTotalRB;
    private JRadioButton mitVoidRB;
    private JRadioButton fiscalRecRB;
    private JRadioButton receiptNumberRB;
    private JRadioButton refundRB;
    private JRadioButton fiscalRecVoidRB;
    private JRadioButton nonFiscalRecRB;
    private JRadioButton descriptionLengthRB;
    private JRadioButton zReportRB;
    private JRadioButton dayTotalizerRB;
    private JRadioButton grandTotalizerRB;
    private JRadioButton currentRecTotalRB;
    
    
    MethodListener methodListener = new MethodListener();
    private JButton beginFiscalReceiptButton;
    private JButton beginNonFiscalButton;
    private JButton endNonFiscalButton;
    private JButton printNormalButton;
    private JButton printRecItemButton;
    private JButton printRecItemAdjButton;
    private JButton printRecRefundButton;
    private JButton printRecSubTotalButton;
    private JButton printRecTotalButton;
    private JButton printDuplicateRecButton;
    private JButton printRecVoidButton;
    private JButton printRecRefundVoidButton;
    private JButton printRecVoidItemButton;
    private JButton endFiscalReceiptButton;
    private JButton printReportButton;
    private JButton printXReportButton;
    private JButton printZReportButton;
    private JButton setPrinterProp;
    private JButton getDataButton;
    private JButton getTotalizerButton;
    private JButton getDateButton;
    private JButton printPeriodicTotalReportButton;
    private JButton printerStatusButton;
    private JButton dayOpenedButton;
    private JButton remainingFiscalMemoryButton;
    private JButton showPropertyButton;
    private JButton beginTrainingButton;
    private JButton endTrainingButton;
    private JButton clearOutPutButton;
    private JButton getErrorLevelButton;
    private JButton getPropListButton;
    private JButton directIoButton;
    private JButton getAdditionalHeaderButton;
    private JButton getAdditionalTrailerButton;
    private JButton resetPrinterButton;
    private JButton clearFieldsButton;
    private JButton getTrainingStateButton;
    private JButton getCheckHealthButton;
    private JButton getOutPutIdButton;
    private JButton setVatTableButton;
    private JButton clearOutButton;
    private JButton clearErrorButton;
    private JButton printRecMessageButton;
    private JButton recSubTotalDiscountButton;
    private JButton recSubTotalDiscountVoidButton;
    
    
    
    private JTextField itemDescription;
    private JTextField itemPrice;
    private JTextField itemQuantity;
    private JTextField itemUnitPrice;
    private JTextField itemVatInfo;
    private JTextField unitName;
    private JTextField reportFrom;
    private JTextField reportTo;
    private JTextField additionalHeaderTxt;
    private JTextField additionalTrailerTxt;
    private JTextField dateTxt;
    private JTextField headerTxt;
    private JTextField trailerTxt;
    private JTextField vatIdTxt;
    private JTextField vatValueTxt;
    private JTextField headerLineNumberTxt;
    private JTextField itemAmountPercAdjTxt;
    private JTextField directIoCommand;
    private JTextField directIoData;
    private JTextField directIoObject;
    private JTextField checkHealthTxt;
    private JTextField preLineTxt;
    private JTextField postLineTxt;
    private JTextField recMessageTxt;
    
    private JTextArea nonFiscalTxt;
    
    private JLabel label;
    private long TOTAL=0;
    private long amountFactorDecimal=1;
    private int quantityFactorDecimal=1;
    private int getDataType=jpos.FiscalPrinterConst.FPTR_GD_FIRMWARE;
    private int adjustmentType=jpos.FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT;
    private int reportType=jpos.FiscalPrinterConst.FPTR_RT_ORDINAL;
    private String[] adjType={"Amount Discount","Amount Surcharge","Perc. Discount","Perc. Surcharge"};
    private int numAdjType=4;
    private int[] valuesGetData={0};
    private String[] strGetData={""};
    private java.util.Timer updateStatusTimer;
    StatusTimerUpdateTask updateStatusTask;
    
    private boolean ver_19_complient = false;
    private boolean ver_18_complient = false;
    
    public FiscalPrinterPanel() {
        fiscalPrinter = new FiscalPrinter();
        fiscalPrinter.addStatusUpdateListener(this);
        fiscalPrinter.addErrorListener(this);
        fiscalPrinter.addOutputCompleteListener(this);
        updateStatusTimer = new java.util.Timer(true);
        updateStatusTask =  new StatusTimerUpdateTask();
        updateStatusTimer.schedule(updateStatusTask, 200, 200);
    }
    
    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainButtonPanel = new MainButtonPanel(methodListener,defaultLogicalName);
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
        
        duplicateReceiptCB = new JCheckBox("Duplicate Receipt");
        propPanel.add(duplicateReceiptCB);//
        asyncModeCB = new JCheckBox("Async Mode");
        propPanel.add(asyncModeCB);
        checkTotalCB = new JCheckBox("Check Total");
        propPanel.add(checkTotalCB);
        flagWhenIdleCB = new JCheckBox("Flag When Idle");
        propPanel.add(flagWhenIdleCB);
        
        
        
        beginTrainingButton = new JButton("Begin Training");
        beginTrainingButton.setMaximumSize(new Dimension(160,17));
        beginTrainingButton.setPreferredSize(new Dimension(160,17));
        beginTrainingButton.setActionCommand("beginTraining");
        beginTrainingButton.addActionListener(methodListener);
        beginTrainingButton.setAlignmentX(Component.TOP_ALIGNMENT);
        beginTrainingButton.setEnabled(false);
        propPanel.add(beginTrainingButton);
        endTrainingButton = new JButton("End Training");
        endTrainingButton.setMaximumSize(new Dimension(160,17));
        endTrainingButton.setPreferredSize(new Dimension(160,17));
        endTrainingButton.setActionCommand("endTraining");
        endTrainingButton.addActionListener(methodListener);
        endTrainingButton.setAlignmentX(Component.TOP_ALIGNMENT);
        endTrainingButton.setEnabled(false);
        propPanel.add(endTrainingButton);
        clearOutButton = new JButton("Clear OutPut");
        clearOutButton.setMaximumSize(new Dimension(160,17));
        clearOutButton.setPreferredSize(new Dimension(160,17));
        clearOutButton.setActionCommand("clearOut");
        clearOutButton.addActionListener(methodListener);
        clearOutButton.setAlignmentX(Component.TOP_ALIGNMENT);
        clearOutButton.setEnabled(false);
        propPanel.add(clearOutButton);
        clearErrorButton = new JButton("Clear Error");
        clearErrorButton.setMaximumSize(new Dimension(160,17));
        clearErrorButton.setPreferredSize(new Dimension(160,17));
        clearErrorButton.setActionCommand("clearError");
        clearErrorButton.addActionListener(methodListener);
        clearErrorButton.setAlignmentX(Component.TOP_ALIGNMENT);
        clearErrorButton.setEnabled(false);
        propPanel.add(clearErrorButton);
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);
        duplicateReceiptCB.setEnabled(false);
        asyncModeCB.setEnabled(false);
        checkTotalCB.setEnabled(false);
        flagWhenIdleCB.setEnabled(false);
        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        duplicateReceiptCB.addItemListener(cbListener);
        asyncModeCB.addItemListener(cbListener);
        checkTotalCB.addItemListener(cbListener);
        flagWhenIdleCB.addItemListener(cbListener);
        JTabbedPane tabbedPane = new JTabbedPane();
        //    tabbedPane.setLayout(new BoxLayout(tabbedPane, BoxLayout.Y_AXIS));
        FiscalPrinterSettingPanel fpt = new  FiscalPrinterSettingPanel();
        tabbedPane.addTab("Printer Setting", null, fpt.make(), "PrinterSetting");
        
        FiscalReceiptPanel frp = new FiscalReceiptPanel();
        tabbedPane.addTab("Fiscal Receipt", null, frp.make(), "FiscalReceipt");
        
        NonFiscalPanel nfp = new NonFiscalPanel();
        tabbedPane.addTab("Non Fiscal Printing", null, nfp.make(), "NonFiscalPriting");
        
        FiscalPrinterFscReportPanel ffr=new FiscalPrinterFscReportPanel();
        tabbedPane.addTab("Fiscal Report", null, ffr.make(), "FiscalReport");
        
        FiscalPrinterStatusPanel fps=new FiscalPrinterStatusPanel();
        tabbedPane.addTab("Fiscal Printer Status", null, fps.make(), "PrinterStatus");
        
        DirectIOPanel dct=new DirectIOPanel();
        tabbedPane.addTab("Direct IO", null, dct.make(), "directIO");
        //DirectIOPanel
        
        subPanel.add(tabbedPane);
        subPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        return mainPanel;
    }
    
    private void setEnableButtonTo(boolean param){
        deviceEnabledCB.setEnabled(param);
        freezeEventsCB.setEnabled(param);
        duplicateReceiptCB.setEnabled(param);
        asyncModeCB.setEnabled(param);
        checkTotalCB.setEnabled(param);
        flagWhenIdleCB.setEnabled(param);
        beginFiscalReceiptButton.setEnabled(param);
        printRecItemButton.setEnabled(param);
        printRecItemAdjButton.setEnabled(param);
        endFiscalReceiptButton.setEnabled(param);
        printRecRefundButton.setEnabled(param);
        printRecSubTotalButton.setEnabled(param);
        printRecTotalButton.setEnabled(param);
        printRecVoidButton.setEnabled(param);
        printRecRefundVoidButton.setEnabled(param);
        printRecVoidItemButton.setEnabled(param);
        printReportButton.setEnabled(param);
        printXReportButton.setEnabled(param);
        printZReportButton.setEnabled(param);
        printDuplicateRecButton.setEnabled(param);
        beginNonFiscalButton.setEnabled(param);
        endNonFiscalButton.setEnabled(param);
        printNormalButton.setEnabled(param);
        setPrinterProp.setEnabled(param);
        paymantFormCombo.setEnabled(param);
        vatIdCombo.setEnabled(param);
        vatIdCombo_2.setEnabled(param);
        getDataButton.setEnabled(param);
        getDateButton.setEnabled(param);
        getTotalizerButton.setEnabled(param);
        showPropertyButton.setEnabled(param);
        printPeriodicTotalReportButton.setEnabled(param);
        printerStatusButton.setEnabled(param);
        dayOpenedButton.setEnabled(param);
        getErrorLevelButton.setEnabled(param);
        getOutPutIdButton.setEnabled(param);
        remainingFiscalMemoryButton.setEnabled(param);
        getPropListButton.setEnabled(param);
        directIoButton.setEnabled(param);
        getAdditionalHeaderButton.setEnabled(param);
        getAdditionalTrailerButton.setEnabled(param);
        resetPrinterButton.setEnabled(param);
        getTrainingStateButton.setEnabled(param);
        getCheckHealthButton.setEnabled(param);
        setVatTableButton.setEnabled(param);
        clearOutButton.setEnabled(param);
        clearErrorButton.setEnabled(param);
        printRecMessageButton.setEnabled(param);
        recSubTotalDiscountButton.setEnabled(param);
        recSubTotalDiscountVoidButton.setEnabled(param);
    }
    
    public void errorOccurred(ErrorEvent errorEvent) {
        String errorCodestr=String.valueOf(errorEvent.getErrorCode());
        String errorExtstr=String.valueOf(errorEvent.getErrorCodeExtended());
        String msg="Occurred Error : ("+errorCodestr+","+errorExtstr+")";
        itemListModel.addElement(msg);
        if(clearOutputForErrorCB.isSelected()){
            errorEvent.setErrorResponse(jpos.JposConst.JPOS_ER_CLEAR);
        }
    }
    public void outputCompleteOccurred(OutputCompleteEvent outputCompleteEvent) {
        String msg="Completed Async Output N. : "+String.valueOf(outputCompleteEvent.getOutputID());
        itemListModel.addElement(msg);
    }
    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        String msg = "Status Update Event: ";
        switch(sue.getStatus()){
            case FiscalPrinterConst.FPTR_SUE_COVER_OK:
                msg += "Cover is OK\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_COVER_OPEN:
                msg += "Cover is Open\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_JRN_EMPTY:
                msg += "Jrn is Empty\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_JRN_NEAREMPTY:
                msg += "Jrn is NearEmpty\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_JRN_PAPEROK:
                msg += "Jrn is OK\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_REC_EMPTY:
                msg += "Rec is Empty\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_REC_NEAREMPTY:
                msg += "Rec is NearEmpty\n";
                break;
            case FiscalPrinterConst.FPTR_SUE_REC_PAPEROK:
                msg += "Rec is OK\n";
                break;
            case jpos.JposConst.JPOS_S_IDLE:
                msg += "printer is IDLE";
                flagWhenIdleCB.setSelected(false);
                break;
        }
        itemListModel.addElement(msg);
        
    }
    
    
    /** Listens to the method buttons. */
    
    private String decodeErrorStation(int errStat){
        String errStatSTR="UNKNOWN";
        switch(errStat){
            case jpos.FiscalPrinterConst.FPTR_S_JOURNAL:errStatSTR="JOURNAL";break;
            case jpos.FiscalPrinterConst.FPTR_S_JOURNAL_RECEIPT:errStatSTR="JOURNAL RECEIPT";break;
            case jpos.FiscalPrinterConst.FPTR_S_RECEIPT:errStatSTR="RECEIPT";break;
            case jpos.FiscalPrinterConst.FPTR_S_SLIP:errStatSTR="SLIP";break;
        }
        return errStatSTR;
    }
    
    private String decodeErrorLevel(int errLev){
        String errLevSTR="UNKNOWN";
        switch(errLev){
            case jpos.FiscalPrinterConst.FPTR_EL_NONE:errLevSTR="NONE";break;
            case jpos.FiscalPrinterConst.FPTR_EL_FATAL:errLevSTR="FATAL";break;
            case jpos.FiscalPrinterConst.FPTR_EL_RECOVERABLE:errLevSTR="RECOVERABLE";break;
            case jpos.FiscalPrinterConst.FPTR_EL_BLOCKED:errLevSTR="BLOCKED";break;
        }
        return errLevSTR;
    }
    
    private String decodeDeviceState(int state){
        String stateSTR="UNKNOWN";
        switch(state){
            case jpos.JposConst.JPOS_S_BUSY:stateSTR="BUSY";break;
            case jpos.JposConst.JPOS_S_IDLE:stateSTR="IDLE";break;
            case jpos.JposConst.JPOS_S_CLOSED:stateSTR="CLOSED";break;
            case jpos.JposConst.JPOS_S_ERROR:stateSTR="ERROR";break;
        }
        return stateSTR;
    }
    
    private String decodeState(int state){
        String stateSTR="UNKNOWN";
        switch(state){
            case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT:stateSTR="FISCAL RECEIPT";break;
            case jpos.FiscalPrinterConst.FPTR_PS_MONITOR:stateSTR="MONITOR";break;
            case jpos.FiscalPrinterConst.FPTR_PS_REPORT:stateSTR="REPORT";break;
            case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING:stateSTR="FISCAL RECEIPT ENDING";break;
            case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_TOTAL:stateSTR="FISCAL RECEIPT TOTAL";break;
            case jpos.FiscalPrinterConst.FPTR_PS_LOCKED:stateSTR="LOCKED";break;
            case jpos.FiscalPrinterConst.FPTR_PS_NONFISCAL:stateSTR="NON FISCAL";break;
        }
        return stateSTR;
    }
    private String decodeCountryCode(int cc){
        String countrySTR="Unknown";
        switch(cc){
            case jpos.FiscalPrinterConst.FPTR_CC_BRAZIL:countrySTR="BRAZIL";break;
            case jpos.FiscalPrinterConst.FPTR_CC_BULGARIA:countrySTR="BULGARIA";break;
            case jpos.FiscalPrinterConst.FPTR_CC_GREECE:countrySTR="GREECE";break;
            case jpos.FiscalPrinterConst.FPTR_CC_HUNGARY:countrySTR="HUNGARY";break;
            case jpos.FiscalPrinterConst.FPTR_CC_ITALY:countrySTR="ITALY";break;
            case jpos.FiscalPrinterConst.FPTR_CC_POLAND:countrySTR="POLAND";break;
            case jpos.FiscalPrinterConst.FPTR_CC_ROMANIA:countrySTR="ROMANIA";break;
            case jpos.FiscalPrinterConst.FPTR_CC_RUSSIA:countrySTR="RUSSIA";break;
            case jpos.FiscalPrinterConst.FPTR_CC_TURKEY:countrySTR="TURKEY";break;
        }
        countrySTR="Country : "+countrySTR;
        return countrySTR;
    }
    
    private String decodeTypeMessage(int cc){
        String typeMsg="Unknown";
        switch(cc){
            case jpos.FiscalPrinterConst.FPTR_MT_FREE_TEXT:typeMsg="FREE TEXT";break;
            case jpos.FiscalPrinterConst.FPTR_MT_EMPTY_LINE:typeMsg="EMPTY LINE";break;
        }
        typeMsg="Type Message : "+typeMsg;
        return typeMsg;
    }
    
    private int getTotalizerType(String[] strLab){
        int totzType=jpos.FiscalPrinterConst.FPTR_GT_ITEM;
        int index=totalTypeCombo.getSelectedIndex();
        String str="";
        switch(index){
            case 0:totzType=jpos.FiscalPrinterConst.FPTR_GT_ITEM;
            str="Item Totalizer: ";break;
            case 1:totzType=jpos.FiscalPrinterConst.FPTR_GT_REFUND;
            str="Refund Totalizer : ";break;
            case 2:totzType=jpos.FiscalPrinterConst.FPTR_GT_ITEM_VOID;
            str="Voided Item Totalizer : ";break;
            case 3:totzType=jpos.FiscalPrinterConst.FPTR_GT_DISCOUNT;
            str="Discount Totalizer : ";break;
            case 4:totzType=jpos.FiscalPrinterConst.FPTR_GT_GROSS;
            str="Gross Totalizer : ";break;
        }
        strLab[0]=str;
        return totzType;
    }
    private void executeGetData()throws JposException{
        String lblStr="Firmware Rel. : ";
        valuesGetData[0]=0;strGetData[0]="";
        try{
            getDataType=jpos.FiscalPrinterConst.FPTR_GD_FIRMWARE;
            if(printerIDRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_PRINTER_ID;
                lblStr="Printer ID : ";
            }else if(currentTotalRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL;
                lblStr="Daily Total : ";
            }else if(grandTotalRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_GRAND_TOTAL;
                lblStr="Grand Total : ";
            }else if(mitVoidRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_MID_VOID;
                lblStr="N. of Voided Rec. : ";
            }else if(fiscalRecRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_FISCAL_REC;
                lblStr="N. of Daily Fiscal Rec. : ";
            }else if(receiptNumberRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_RECEIPT_NUMBER;
                lblStr="N. of Fiscal Rec. Printed : ";
            }else if(refundRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_REFUND;
                lblStr="Current Tot. of Refunds : ";
            }else if(fiscalRecVoidRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_FISCAL_REC_VOID;
                lblStr="N. of Daily Voided Fiscal Rec. : ";
            }else if(nonFiscalRecRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_NONFISCAL_REC;
                lblStr="N. of Daily Non Fiscal Rec. : ";
            }else if(descriptionLengthRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_DESCRIPTION_LENGTH;
                lblStr="Description Length : ";
            }else if(zReportRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_Z_REPORT;
                lblStr="Z Report : ";
            }else if(currentRecTotalRB.isSelected()){
                getDataType=jpos.FiscalPrinterConst.FPTR_GD_CURRENT_TOTAL;
                lblStr="Current Rec. Total : ";
            }
            fiscalPrinter.getData(getDataType,valuesGetData,strGetData);
            itemListModel.addElement(new String(lblStr+strGetData[0]));
            
        }catch(JposException je) {
            JOptionPane.showMessageDialog(null, "JPosException calling getData", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearFields(){
        itemQuantity.setText("");
        itemPrice.setText("");
        itemDescription.setText("");
        itemUnitPrice.setText("");
        itemVatInfo.setText("");
        unitName.setText("");
        reportFrom.setText("");
        reportTo.setText("");
        additionalHeaderTxt.setText("");
        additionalTrailerTxt.setText("");
        dateTxt.setText("");
        headerTxt.setText("");
        headerLineNumberTxt.setText("");
        nonFiscalTxt.setText("");
        itemAmountPercAdjTxt.setText("");
        directIoCommand.setText("0");
        directIoData.setText("");
        directIoObject.setText("");
        trailerTxt.setText("");
        vatIdTxt.setText("");
        vatValueTxt.setText("");
        preLineTxt.setText("");
        postLineTxt.setText("");
        recMessageTxt.setText("");
        if(vatIdCombo_2.isEnabled()){
            vatValueTxt.setText((String)vatIdCombo_2.getSelectedItem());
        }
        checkHealthTxt.setText("");
    }
    
    
    
    
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setDeviceEnabled(false);
                        endTrainingButton.setEnabled(false);
                        beginTrainingButton.setEnabled(false);
                    }else{
                        fiscalPrinter.setDeviceEnabled(true);
                        amountFactorDecimal=1;quantityFactorDecimal=1;
                        int temp=fiscalPrinter.getAmountDecimalPlace();
                        if(temp>0){
                            for(int i=1;i<=temp;i++)amountFactorDecimal=amountFactorDecimal*10;
                        }
                        temp=fiscalPrinter.getQuantityDecimalPlaces();
                        if(temp>0){
                            for(int i=1;i<=temp;i++)quantityFactorDecimal=quantityFactorDecimal*10;
                        }
                        endTrainingButton.setEnabled(true);
                        beginTrainingButton.setEnabled(true);
                        paymantFormCombo.removeAllItems();
                        if(fiscalPrinter.getCapPredefinedPaymentLines()){
                            String S=fiscalPrinter.getPredefinedPaymentLines(),str;
                            for(int i=0;i<S.length();i++){
                                str=S.substring(i, i+1);
                                if(!str.equals(","))paymantFormCombo.addItem(str);
                            }
                        }else{
                            paymantFormCombo.addItem("CASH");
                        }
                        vatIdCombo.removeAllItems();
                        vatIdCombo_2.removeAllItems();
                        if(fiscalPrinter.getCapHasVatTable()){
                            int nv=fiscalPrinter.getNumVatRates();
                            for(int i=1;i<=nv;i++){
                                vatIdCombo.addItem(String.valueOf(i));
                                vatIdCombo_2.addItem(String.valueOf(i));
                            }
                        }else{
                            vatIdCombo.addItem("0");
                            vatIdCombo_2.addItem("0");
                        }
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setFreezeEvents(false);
                    }else{
                        fiscalPrinter.setFreezeEvents(true);
                    }
                }else if (source == duplicateReceiptCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setDuplicateReceipt(false);
                    }else{
                        fiscalPrinter.setDuplicateReceipt(true);
                    }
                }else if (source == asyncModeCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setAsyncMode(false);
                    }else{
                        fiscalPrinter.setAsyncMode(true);
                    }
                }else if (source == checkTotalCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setCheckTotal(false);
                    }else{
                        fiscalPrinter.setCheckTotal(true);
                    }
                } else if (source == flagWhenIdleCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        fiscalPrinter.setFlagWhenIdle(false);
                    }else{
                        fiscalPrinter.setFlagWhenIdle(true);
                    }
                }
            } catch(JposException je) {
                
                JOptionPane.showMessageDialog(null, "Exception in GetPrinter State\nException: " + je.getErrorCode()+"-"+je.getErrorCodeExtended(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("FiscalPrinterPanel: CheckBoxListener: Jpos Exception" + je.getErrorCode()+"-"+je.getErrorCodeExtended());
            }
        }
    }
    
    //Class MethodListner
    class MethodListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            mainButtonPanel.action(ae);
            String logicalName = mainButtonPanel.getLogicalName();
            if(ae.getActionCommand().equals("open")){
                try{
                    if(logicalName.equals("")){
                        logicalName = defaultLogicalName;
                    }
                    // fiscalPrinter.addStatusUpdateListener(this);
                    fiscalPrinter.open(logicalName);
                    deviceEnabledCB.setEnabled(false);
                    freezeEventsCB.setEnabled(true);
                    duplicateReceiptCB.setEnabled(true);
                    asyncModeCB.setEnabled(true);
                    checkTotalCB.setEnabled(true);
                    flagWhenIdleCB.setEnabled(true);
                    boolean dupRec=fiscalPrinter.getDuplicateReceipt();
                    if(dupRec)duplicateReceiptCB.doClick();
                    int version = fiscalPrinter.getDeviceServiceVersion();
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
                    fiscalPrinter.claim(0);
                    paymantFormCombo.removeAllItems();
                    vatIdCombo.removeAllItems();
                    vatIdCombo_2.removeAllItems();
                    setEnableButtonTo(true);//button enabled
                    clearFields();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to claim \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("release")){
                try{
                    fiscalPrinter.release();
                    setEnableButtonTo(false);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to release \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("close")){
                try{
                    fiscalPrinter.close();
                    setEnableButtonTo(false);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Failed to close \""+logicalName+"\"\nException: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("clearOutPut")){
                itemListModel.clear();
            } else if(ae.getActionCommand().equals("info")){
                try{
                    String ver = new Integer(fiscalPrinter.getDeviceServiceVersion()).toString();
                    String msg = "Service Description: " + fiscalPrinter.getDeviceServiceDescription();
                    msg = msg + "\nService Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                    ver = new Integer(fiscalPrinter.getDeviceControlVersion()).toString();
                    msg += "\n\nControl Description: " + fiscalPrinter.getDeviceControlDescription();
                    msg += "\nControl Version: v"+ new Integer(ver.substring(0,1)) + "." + new Integer(ver.substring(1,4)) + "." + new Integer(ver.substring(4,7));
                    msg += "\n\nPhysical Device Name: " + fiscalPrinter.getPhysicalDeviceName();
                    msg += "\nPhysical Device Description: " + fiscalPrinter.getPhysicalDeviceDescription();
                    msg += "\n\nProperties:\n------------------------";
                    if(ver_18_complient)
                    {
                    	msg += "\nCapStatisticsReporting: " + fiscalPrinter.getCapStatisticsReporting();                    
                    	msg += "\nCapUpdateStatistics: " + fiscalPrinter.getCapUpdateStatistics();
                    }
                    else
                    {
                    	msg += "\nCapStatisticsReporting: Service Object is not 1.8 complient";
                    	msg += "\nCapUpdateStatistics: Service Object is not 1.8 complient";
                    }
                    
                    if(ver_19_complient)
                    {
                    	msg += "\nCapCompareFirmwareVersion: " + fiscalPrinter.getCapCompareFirmwareVersion();                    
                    	msg += "\nCapUpdateFirmware: " + fiscalPrinter.getCapUpdateFirmware();
                    }
                    else
                    {
                    	msg += "\nCapCompareFirmwareVersion: Service Object is not 1.9 complient";                    
                    	msg += "\nCapUpdateFirmware: Service Object is not 1.9 complient";
                    }
                    msg += "\nCapPowerReporting: " + (fiscalPrinter.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (fiscalPrinter.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                    JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("stats")) {
                try{
                    StatisticsDialog dlg = new StatisticsDialog(fiscalPrinter);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }else if(ae.getActionCommand().equals("firmware")) {
                try{
                    FirmwareUpdateDlg dlg = new FirmwareUpdateDlg(fiscalPrinter);
                    dlg.setVisible(true);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception: "+ e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }else if(ae.getActionCommand().equals("paymantFormCombo")){
                try{
                    
                    itemDescription.setText((String)paymantFormCombo.getSelectedItem());
                    
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception in Payamant Form\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Exception " + e);
                }
            }else if(ae.getActionCommand().equals("vatIdCombo")){
                try{
                    
                    itemVatInfo.setText((String)vatIdCombo.getSelectedItem());
                    
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception in Vat info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Exception " + e);
                }
            } else if(ae.getActionCommand().equals("adjustmentTypeCombo")){
                try{
                    String S=(String)adjustmentTypeCombo.getSelectedItem();
                    adjustmentType=jpos.FiscalPrinterConst.FPTR_AT_AMOUNT_DISCOUNT;
                    if(S.equals(adjType[1]))adjustmentType=jpos.FiscalPrinterConst.FPTR_AT_AMOUNT_SURCHARGE;
                    else if(S.equals(adjType[2]))adjustmentType=jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_DISCOUNT;
                    else if(S.equals(adjType[3]))adjustmentType=jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_SURCHARGE;
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception in adjustmentCombo\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Exception " + e);
                }
            }else if(ae.getActionCommand().equals("reportTypeCombo")){
                try{
                    
                    String S=(String)reportTypeCombo.getSelectedItem();
                    reportType=jpos.FiscalPrinterConst.FPTR_RT_ORDINAL;
                    if(S.equals("DATE"))reportType=jpos.FiscalPrinterConst.FPTR_RT_DATE;
                    
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Exception in Reporttype\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Exception " + e);
                }
            } else if(ae.getActionCommand().equals("beginFiscalReceipt")){
                try{
                    TOTAL=0;
                    fiscalPrinter.beginFiscalReceipt(true);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Begin Fiscal Receipt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecItem")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemPrice.getText();
                    String itQuantity=itemQuantity.getText();
                    String itUnitPrice=itemUnitPrice.getText();
                    String itVatInfo=itemVatInfo.getText();
                    String itUnitName=unitName.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    double dUnitPrice=Double.parseDouble(itUnitPrice);
                    long unitPrice=(long)(dUnitPrice*amountFactorDecimal);
                    int vatInfo=0;
                    if(itVatInfo.length()>0)vatInfo=Integer.parseInt(itVatInfo);
                    TOTAL=TOTAL+price;
                    double dquantity=Double.parseDouble(itQuantity);
                    int quantity=(int)(dquantity*quantityFactorDecimal);
                    String preL=preLineTxt.getText();if(preL.length()>0)fiscalPrinter.setPreLine(preL);
                    String postL=postLineTxt.getText();if(postL.length()>0)fiscalPrinter.setPostLine(postL);
                    clearFields();
                    fiscalPrinter.printRecItem(itDesc,price,quantity,vatInfo,unitPrice,itUnitName);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in printRecItem \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getData")){
                try{
                    executeGetData();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetData\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("endFiscalReceipt")){
                try{
                    fiscalPrinter.endFiscalReceipt(false);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in End Fiscal Receipt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecItemAdj")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemAmountPercAdjTxt.getText();
                    String itVatInfo=itemVatInfo.getText();
                    double dprice=Double.parseDouble(itPrice);
                    double kFactor=amountFactorDecimal;
                    if((adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_DISCOUNT)||
                            (adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_SURCHARGE))kFactor=1;
                    long price=(long)(dprice*kFactor);
                    int vatInfo=0;
                    if(itVatInfo.length()>0)vatInfo=Integer.parseInt(itVatInfo);
                    String preL=preLineTxt.getText();if(preL.length()>0)fiscalPrinter.setPreLine(preL);
                    fiscalPrinter.printRecItemAdjustment(adjustmentType,itDesc,price,vatInfo);
                    clearFields();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Begin Fiscal Receipt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecRefund")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemPrice.getText();
                    String itVatInfo=itemVatInfo.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    int vatInfo=0;
                    if(itVatInfo.length()>0)vatInfo=Integer.parseInt(itVatInfo);
                    String preL=preLineTxt.getText();if(preL.length()>0)fiscalPrinter.setPreLine(preL);
                    fiscalPrinter.printRecRefund(itDesc,price,vatInfo);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecRefund\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecSubTotal")){
                try{
                    String itPrice=itemPrice.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    String postL=postLineTxt.getText();if(postL.length()>0)fiscalPrinter.setPostLine(postL);
                    fiscalPrinter.printRecSubtotal(price);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Begin Fiscal Receipt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecTotal")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemPrice.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    long __Total=TOTAL;
                    if(price<TOTAL)TOTAL=TOTAL-price;
                    String postL=postLineTxt.getText();if(postL.length()>0)fiscalPrinter.setPostLine(postL);
                    String itAmountPerc=itemAmountPercAdjTxt.getText();
                    if(itAmountPerc.length()>0){
                        double ddprice=Double.parseDouble(itAmountPerc);
                        long erratedTotale=(long)(ddprice*amountFactorDecimal);
                        __Total=erratedTotale;
                    }
                    fiscalPrinter.printRecTotal(__Total,price,itDesc);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Print Receipt Total\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecVoid")){
                try{
                    String itDesc=itemDescription.getText();
                    fiscalPrinter.printRecVoid(itDesc);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecVoid \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecRefundVoid")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemPrice.getText();
                    String itVatInfo=itemVatInfo.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    int vatInfo=0;
                    if(itVatInfo.length()>0)vatInfo=Integer.parseInt(itVatInfo);
                    fiscalPrinter.printRecRefundVoid(itDesc,price,vatInfo);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRefundVoid\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printRecMessage")){
                try{
                    String itMsg=recMessageTxt.getText();
                    fiscalPrinter.printRecMessage(itMsg);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecMessage \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("recSubTotalDiscount")){
                try{
                    String itAmountPerc=itemAmountPercAdjTxt.getText();
                    double dprice=Double.parseDouble(itAmountPerc);
                    double kFactor=amountFactorDecimal;
                    if((adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_DISCOUNT)||
                            (adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_SURCHARGE))kFactor=1;
                    long price=(long)(dprice*kFactor);
                    String preL=preLineTxt.getText();if(preL.length()>0)fiscalPrinter.setPreLine(preL);
                    String itDesc=itemDescription.getText();
                    fiscalPrinter.printRecSubtotalAdjustment(adjustmentType,itDesc,price);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecSubTotalDiscount \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("recSubTotalDiscountVoid")){
                try{
                    String itAmountPerc=itemAmountPercAdjTxt.getText();
                    double dprice=Double.parseDouble(itAmountPerc);
                    double kFactor=amountFactorDecimal;
                    if((adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_DISCOUNT)||
                            (adjustmentType==jpos.FiscalPrinterConst.FPTR_AT_PERCENTAGE_SURCHARGE))kFactor=1;
                    long price=(long)(dprice*kFactor);
                    String preL=preLineTxt.getText();if(preL.length()>0)fiscalPrinter.setPreLine(preL);
                    fiscalPrinter.printRecSubtotalAdjustVoid(adjustmentType,price);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecSubTotalDiscount \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("printRecVoidItem")){
                try{
                    String itDesc=itemDescription.getText();
                    String itPrice=itemPrice.getText();
                    String itQuantity=itemQuantity.getText();
                    String itVatInfo=itemVatInfo.getText();
                    String amountAdjTxt=itemAmountPercAdjTxt.getText();
                    double dprice=Double.parseDouble(itPrice);
                    long price=(long)(dprice*amountFactorDecimal);
                    double damount=Double.parseDouble(amountAdjTxt);
                    long amount=(long)(damount*amountFactorDecimal);
                    double dquantity=Double.parseDouble(itQuantity);
                    int quantity=(int)(dquantity*quantityFactorDecimal);
                    int vatInfo=0;
                    if(itVatInfo.length()>0)vatInfo=Integer.parseInt(itVatInfo);
                    fiscalPrinter.printRecVoidItem(itDesc,price,quantity,adjustmentType,amount,vatInfo);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintRecVoiItem\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printReport")){
                try{
                    String repFrom=reportFrom.getText();
                    String repTo=reportTo.getText();
                    fiscalPrinter.printReport(reportType, repFrom,repTo);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Print Report\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printXReport")){
                try{
                    fiscalPrinter.printXReport();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintXReport\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printZReport")){
                try{
                    fiscalPrinter.printZReport();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PrintZReport\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printDuplicateRec")){
                try{
                    fiscalPrinter.printDuplicateReceipt();
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Print Duplicate Receipt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("setVatTable")){
                try{
                    fiscalPrinter.setVatTable();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in SetVatTable\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("setPrinterProp")){
                try{
                    String S= additionalHeaderTxt.getText();
                    if(S.length()>0)fiscalPrinter.setAdditionalHeader(S);
                    S= additionalTrailerTxt.getText();
                    if(S.length()>0)fiscalPrinter.setAdditionalTrailer(S);
                    S= dateTxt.getText();
                    if(S.length()>0)fiscalPrinter.setDate(S);
                    String SST=trailerTxt.getText();
                    String SS= headerTxt.getText();
                    if(SS.length()>0){
                        String s1=headerLineNumberTxt.getText();
                        int lineNumber=-1;
                        if(s1.length()>0)lineNumber=Integer.parseInt(s1);
                        if(lineNumber>0)fiscalPrinter.setHeaderLine(lineNumber,SS,doubleWidthCB.isSelected());
                        else JOptionPane.showMessageDialog(null, "Illegal Line Number","Exception", JOptionPane.ERROR_MESSAGE);
                    }
                    if(SST.length()>0){
                        String s1=headerLineNumberTxt.getText();
                        int lineNumber=-1;
                        if(s1.length()>0)lineNumber=Integer.parseInt(s1);
                        if(lineNumber>0)fiscalPrinter.setTrailerLine(lineNumber,SST,doubleWidthCB.isSelected());
                        else JOptionPane.showMessageDialog(null, "Illegal Line Number","Exception", JOptionPane.ERROR_MESSAGE);
                    }
                    String SvatId=vatIdTxt.getText();
                    String SvatValue=vatValueTxt.getText();
                    if(SvatId.length()>0){
                        int vatId=Integer.parseInt(SvatId);
                        fiscalPrinter.setVatValue( vatId,SvatValue);
                        
                    }
                    String sckh=checkHealthTxt.getText();
                    if(sckh.length()>0){
                        int chk=Integer.parseInt(sckh);
                        fiscalPrinter.checkHealth(chk);
                    }
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Set Property\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("beginNonFiscal")){
                try{
                    fiscalPrinter.beginNonFiscal();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Begin Non Fiscal\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("endNonFiscal")){
                try{
                    fiscalPrinter.endNonFiscal();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in End Non Fiscal\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printNormal")){
                try{
                    int station=jpos.FiscalPrinterConst.FPTR_S_RECEIPT;
                    String txt=nonFiscalTxt.getText();
                    fiscalPrinter.printNormal(station,txt);
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Print Normalt\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("printPeriodicReport")){
                try{
                    String repFrom=reportFrom.getText();
                    String repTo=reportTo.getText();
                    fiscalPrinter.printPeriodicTotalsReport(repFrom,repTo);
                    clearFields();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in PeriodicReport\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getDate")){
                try{
                    String[] txt={""};
                    fiscalPrinter.getDate(txt);
                    itemListModel.addElement(new String("DATE : "+txt[0]));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetDate\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getErrorLevel")){
                try{
                    int errState=fiscalPrinter.getErrorState();
                    itemListModel.addElement(new String("ERROR STATE : "+String.valueOf(errState)));
                    int errLev=fiscalPrinter.getErrorLevel();
                    String str=decodeErrorLevel(errLev);
                    itemListModel.addElement(new String("ERROR LEVEL : "+str));
                    int errId=fiscalPrinter.getErrorOutID();
                    itemListModel.addElement(new String("ERROR OUT ID : "+String.valueOf(errId)));
                    int errStat=fiscalPrinter.getErrorStation();
                    str=decodeErrorStation(errStat);
                    itemListModel.addElement(new String("ERROR STATION : "+str));
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetPrinter Error Info\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getOutPutId")){
                try{
                    int outId=fiscalPrinter.getOutputID();
                    itemListModel.addElement(new String("OUTPUT ID : "+String.valueOf(outId)));
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Get OutputID Level\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("getTrainingState")){
                try{
                    boolean traiMode=fiscalPrinter.getTrainingModeActive();
                    String str="FALSE";
                    if(traiMode)str="TRUE";
                    itemListModel.addElement(new String("TRAINING MODE ACTIVE : "+str));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetTraining State\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("printerStatus")){
                try{
                    
                    int tempstate=fiscalPrinter.getPrinterState();
                    String str=decodeState(tempstate);
                    itemListModel.addElement(new String("STATE : "+str));
                    tempstate=fiscalPrinter.getState();
                    str=decodeDeviceState(tempstate);
                    itemListModel.addElement(new String("DEVICE STATE : "+str));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetPrinter State\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("dayOpened")){
                try{
                    String str;
                    boolean dayOpened=fiscalPrinter.getDayOpened();
                    if(dayOpened)str="TRUE";else str="FALSE";
                    itemListModel.addElement(new String("DAY OPENED : "+str));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetPrinter State\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("remainingFiscalMemory")){
                try{
                    int remFiscMem=fiscalPrinter.getRemainingFiscalMemory();
                    itemListModel.addElement(new String("Rem. Fisc. Memory : "+String.valueOf(remFiscMem)));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in GetPrinter State\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("clearOut")){
                try{
                    fiscalPrinter.clearOutput();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Clear Output\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("clearError")){
                try{
                    fiscalPrinter.clearError();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Clear Error\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("showProperty")){
                try{
                    String str;
                    boolean jrnEmpty=fiscalPrinter.getJrnEmpty();
                    if(jrnEmpty)str="TRUE";else str="FALSE";
                    itemListModel.addElement(new String("JrnEMPTY : "+str));
                    boolean jrnNearEnd=fiscalPrinter.getJrnNearEnd();
                    if(jrnNearEnd)str="TRUE";else str="FALSE";
                    itemListModel.addElement(new String("JrnNEAREND : "+str));
                    boolean recEmpty=fiscalPrinter.getRecEmpty();
                    if(recEmpty)str="TRUE";else str="FALSE";
                    itemListModel.addElement(new String("recEMPTY : "+str));
                    boolean recNearEnd=fiscalPrinter.getRecNearEnd();
                    if(recNearEnd)str="TRUE";else str="FALSE";
                    itemListModel.addElement(new String("recNEAREND : "+str));
                    if(fiscalPrinter.getCapHasVatTable()){
                        int nv=fiscalPrinter.getNumVatRates();
                        int[] v={0};
                        for(int i=1;i<=nv;i++){
                            fiscalPrinter.getVatEntry(i,0, v);
                            itemListModel.addElement(new String("Vat ID: "+String.valueOf(i)+" - Value : "+String.valueOf(v[0])));
                        }
                    }
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in ShowProperty\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getTotalizer")){
                try{
                    String labStr;
                    String[] str={""},valStr={""};
                    if(grandTotalizerRB.isSelected()){
                        fiscalPrinter.setTotalizerType(jpos.FiscalPrinterConst.FPTR_TT_GRAND);
                        labStr="GRAND TOTAL - ";
                    }else{
                        fiscalPrinter.setTotalizerType(jpos.FiscalPrinterConst.FPTR_TT_DAY);
                        labStr="DAY TOTAL - ";
                    }
                    int totalizerType=getTotalizerType(str);
                    String tempVatid =(String)vatIdCombo_2.getSelectedItem();
                    int vatid=Integer.parseInt(tempVatid);
                    fiscalPrinter.getTotalizer(vatid, totalizerType,valStr);
                    itemListModel.addElement(new String(labStr+str[0]+valStr[0]));
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Get Totalizer\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            } else if(ae.getActionCommand().equals("beginTraining")){
                try{
                    fiscalPrinter.beginTraining();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in BeginTraning\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("endTraining")){
                try{
                    fiscalPrinter.endTraining();
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Endtraning\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getPropList")){
                try{
                    propListModel.clear();
                    int cc=fiscalPrinter.getCountryCode();
                    String s;
                    
                    propListModel.addElement(decodeCountryCode(cc));
                    cc=fiscalPrinter.getDescriptionLength();
                    propListModel.addElement("Description Length : "+String.valueOf(cc));
                    cc=fiscalPrinter.getMessageLength();
                    propListModel.addElement("Message Length : "+String.valueOf(cc));
                    cc=fiscalPrinter.getNumHeaderLines();
                    propListModel.addElement("Num. Header Lines : "+String.valueOf(cc));
                    if(fiscalPrinter.getCapPredefinedPaymentLines()){
                        s=fiscalPrinter.getPredefinedPaymentLines();
                        propListModel.addElement("Pred. Payment Lines : "+s);
                    }else{
                        propListModel.addElement("Pred. Payment Lines : NOT Supported");
                    }
                    cc=fiscalPrinter.getQuantityDecimalPlaces();
                    propListModel.addElement("Quantity Dec. Places : "+String.valueOf(cc));
                    cc=fiscalPrinter.getQuantityLength();
                    propListModel.addElement("Quantity Length : "+String.valueOf(cc));
                    s=fiscalPrinter.getReservedWord();
                    propListModel.addElement("Reserved Word : "+s);
                    propListModel.addElement("********** Capability **********");
                    boolean b=fiscalPrinter.getCapAdditionalLines();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapAdditionalLines : "+s));
                    b=fiscalPrinter.getCapAmountAdjustment();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapAmountAdjustment : "+s));
                    b=fiscalPrinter.getCapAmountNotPaid();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapAmountNotPaid : "+s));
                    b=fiscalPrinter.getCapChangeDue();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapChangeDue : "+s));
                    b=fiscalPrinter.getCapCheckTotal();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapCheckTotal : "+s));
                    b=fiscalPrinter.getCapJrnEmptySensor();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapJrnEmptySensor : "+s));
                    b=fiscalPrinter.getCapJrnNearEndSensor();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapJrnNearEndSensor : "+s));
                    b=fiscalPrinter.getCapPredefinedPaymentLines();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapPredefinedPaymentLines : "+s));
                    b=fiscalPrinter.getCapRecEmptySensor();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapRecEmptySensor : "+s));
                    b=fiscalPrinter.getCapRecPresent();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapRecPresent : "+s));
                    b=fiscalPrinter.getCapReservedWord();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapReservedWord : "+s));
                    b=fiscalPrinter.getCapRemainingFiscalMemory();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapRemainingFiscalMemory : "+s));
                    b=fiscalPrinter.getCapSetHeader();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSetHeader : "+s));
                    b=fiscalPrinter.getCapSetPOSID();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSetPOSID : "+s));
                    b=fiscalPrinter.getCapSetTrailer();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSetTrailer : "+s));
                    b=fiscalPrinter.getCapSetVatTable();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSetVatTable : "+s));
                    b=fiscalPrinter.getCapSubAmountAdjustment();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSubAmountAdjustment : "+s));
                    b=fiscalPrinter.getCapSubPercentAdjustment();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSubPercentAdjustment : "+s));
                    b=fiscalPrinter.getCapSubtotal();if(b)s="TRUE";else s="FALSE";
                    propListModel.addElement(new String("CapSubtotal : "+s));
                    propListModel.addElement("--------------------------------");
                    
                    
                    if(fiscalPrinter.getDeviceEnabled()){
                        cc=fiscalPrinter.getMessageType();
                        propListModel.addElement("Message Type :"+decodeTypeMessage(cc));
                        cc=fiscalPrinter.getAmountDecimalPlace();
                        propListModel.addElement("Amount Dec. Places : "+String.valueOf(cc));
                    }else{
                        propListModel.addElement("For others properties enable device");
                    }
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in Get Property \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("directIoButton")){
                try{
                    String s1=directIoCommand.getText();
                    String s2=directIoData.getText();
                    String s3=directIoObject.getText();
                    StringBuffer sb=new StringBuffer(s3);
                    int dC=0;
                    int[] dD={0};
                    try{
                        dC=Integer.parseInt(s1);
                        dD[0]=Integer.parseInt(s2);
                        clearFields();
                    } catch(Exception e){
                        JOptionPane.showMessageDialog(null, "Dati Errati \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                    fiscalPrinter.directIO(dC, dD, sb);
                    directIoData.setText(String.valueOf(dD[0]));
                    directIoObject.setText(sb.toString());
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in DirectIO \nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getAdditionalHeader")){
                try{
                    String s=fiscalPrinter.getAdditionalHeader();
                    propListModel.addElement("Additional Header : "+s);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in getAdditionalHeader\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("getAdditionalTrailer")){
                try{
                    String s=fiscalPrinter.getAdditionalTrailer();
                    propListModel.addElement("Additional Trailer : "+s);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in getAdditionalTrailer\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("resetPrinter")){
                try{
                    fiscalPrinter.resetPrinter();
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in resetPrinter\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }else if(ae.getActionCommand().equals("clearFieldsButton")){
                clearFields();
            }else if(ae.getActionCommand().equals("getCheckHealth")){
                try{
                    String s=fiscalPrinter.getCheckHealthText();
                    propListModel.addElement("CheckHealthText : "+s);
                    
                }catch(JposException e){
                    JOptionPane.showMessageDialog(null, "Exception in getCheckHealth\nException: "+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
            
            try {
                if(deviceEnabledCB.isSelected()){
                    deviceEnabledCB.setSelected(fiscalPrinter.getDeviceEnabled());
                    freezeEventsCB.setSelected(fiscalPrinter.getFreezeEvents());
                }
            } catch(JposException je) {
                System.err.println("FiscalPrinterPanel: MethodListener: JposException");
            }
        }
    }
    // DirectIO Panel
    class DirectIOPanel extends Component{

		private static final long serialVersionUID = 9108485289446253369L;

		public Component make(){
            JPanel labelPanel = new JPanel();
            labelPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
            label = new JLabel("Number");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(80,17));
            label.setPreferredSize(new Dimension(80,17));
            labelPanel.add(label);
            label = new JLabel("Data");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(80,17));
            label.setPreferredSize(new Dimension(80,17));
            labelPanel.add(label);
            label = new JLabel("String");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(80,17));
            label.setPreferredSize(new Dimension(80,17));
            labelPanel.add(label);
            label = new JLabel(" ");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(80,17));
            label.setPreferredSize(new Dimension(80,17));
            labelPanel.add(label);
            
            JPanel dataPanel = new JPanel();
            dataPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
            directIoCommand = new JTextField();
            directIoCommand.setMaximumSize(new Dimension(80,18));
            directIoCommand.setPreferredSize(new Dimension(80,18));
            directIoCommand.setMinimumSize(new Dimension(80,18));
            directIoCommand.setText("0");
            directIoCommand.setAlignmentX(Component.LEFT_ALIGNMENT);
            dataPanel.add(directIoCommand);
            directIoData = new JTextField();
            directIoData.setMaximumSize(new Dimension(160,18));
            directIoData.setPreferredSize(new Dimension(160,18));
            directIoData.setMinimumSize(new Dimension(160,18));
            directIoData.setAlignmentX(Component.LEFT_ALIGNMENT);
            dataPanel.add(directIoData);
            directIoObject = new JTextField();
            directIoObject.setMaximumSize(new Dimension(500,18));
            directIoObject.setPreferredSize(new Dimension(500,18));
            directIoObject.setMinimumSize(new Dimension(450,18));
            directIoObject.setAlignmentX(Component.LEFT_ALIGNMENT);
            dataPanel.add(directIoObject);
            directIoButton = new JButton("Direct IO");
            directIoButton.setMaximumSize(new Dimension(160,17));
            directIoButton.setPreferredSize(new Dimension(160,17));
            directIoButton.setActionCommand("directIoButton");
            directIoButton.addActionListener(methodListener);
            directIoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            directIoButton.setEnabled(false);
            dataPanel.add(directIoButton);
            clearFieldsButton = new JButton("Clear Fields");
            clearFieldsButton.setMaximumSize(new Dimension(160,17));
            clearFieldsButton.setPreferredSize(new Dimension(160,17));
            clearFieldsButton.setActionCommand("clearFieldsButton");
            clearFieldsButton.addActionListener(methodListener);
            clearFieldsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            clearFieldsButton.setEnabled(true);
            dataPanel.add(clearFieldsButton);
            JPanel directIOPanel = new JPanel();
            directIOPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            directIOPanel.setLayout(new BoxLayout(directIOPanel,BoxLayout.X_AXIS));
            directIOPanel.add(labelPanel);
            directIOPanel.add(dataPanel);
            return directIOPanel;
        }
    }
    //Non fiscalPrinting Panel
    class NonFiscalPanel extends Component{

		private static final long serialVersionUID = -8116838958814524668L;

		public Component make() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            beginNonFiscalButton = new JButton("Begin Non Fiscal");
            beginNonFiscalButton.setMaximumSize(new Dimension(160,17));
            beginNonFiscalButton.setPreferredSize(new Dimension(160,17));
            beginNonFiscalButton.setActionCommand("beginNonFiscal");
            beginNonFiscalButton.addActionListener(methodListener);
            beginNonFiscalButton.setAlignmentX(Component.TOP_ALIGNMENT);
            beginNonFiscalButton.setEnabled(false);
            buttonPanel.add(beginNonFiscalButton);
            printNormalButton = new JButton("Print   Normal");
            printNormalButton.setMaximumSize(new Dimension(160,17));
            printNormalButton.setPreferredSize(new Dimension(160,17));
            printNormalButton.setActionCommand("printNormal");
            printNormalButton.addActionListener(methodListener);
            printNormalButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printNormalButton.setEnabled(false);
            buttonPanel.add(printNormalButton);
            endNonFiscalButton = new JButton("End Non Fiscal");
            endNonFiscalButton.setMaximumSize(new Dimension(160,17));
            endNonFiscalButton.setPreferredSize(new Dimension(160,17));
            endNonFiscalButton.setActionCommand("endNonFiscal");
            endNonFiscalButton.addActionListener(methodListener);
            endNonFiscalButton.setAlignmentX(Component.TOP_ALIGNMENT);
            endNonFiscalButton.setEnabled(false);
            buttonPanel.add(endNonFiscalButton);
            //Label Panel
            JPanel itemLabelPanel = new JPanel();
            itemLabelPanel.setLayout( new BoxLayout(itemLabelPanel, BoxLayout.Y_AXIS));
            itemLabelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            itemLabelPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Text");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            
            //Field Panel
            JPanel itemFieldPanel = new JPanel();
            
            itemFieldPanel.setLayout( new BoxLayout(itemFieldPanel, BoxLayout.Y_AXIS));
            itemFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            nonFiscalTxt = new JTextArea(50,40);
            nonFiscalTxt.setMaximumSize(new Dimension(300,200));
            nonFiscalTxt.setPreferredSize(new Dimension(300,200));
            nonFiscalTxt.setMinimumSize(new Dimension(300,200));
            itemFieldPanel.add(new JScrollPane(nonFiscalTxt),BorderLayout.CENTER);
            
            JPanel nonFiscalControlPanel = new JPanel();
            nonFiscalControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            nonFiscalControlPanel.setLayout(new BoxLayout(nonFiscalControlPanel,BoxLayout.X_AXIS));
            nonFiscalControlPanel.add(buttonPanel);
            nonFiscalControlPanel.add(itemLabelPanel);
            nonFiscalControlPanel.add(itemFieldPanel);
            
            
            return nonFiscalControlPanel;
            
        }
    }
    //Fiscal Printer Setting
    class FiscalPrinterSettingPanel extends Component{

		private static final long serialVersionUID = 699060709578537513L;

		public Component make() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            
            label = new JLabel("Additional Trailer");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Additional Header");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Date");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Header");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            label = new JLabel("Trailer");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Line Number");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Vat ID");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Vat Value (% * 100)");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            label = new JLabel("Check Health");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            buttonPanel.add(label);
            
            setPrinterProp = new JButton("Set Printer");
            setPrinterProp.setMaximumSize(new Dimension(160,17));
            setPrinterProp.setPreferredSize(new Dimension(160,17));
            setPrinterProp.setActionCommand("setPrinterProp");
            setPrinterProp.addActionListener(methodListener);
            setPrinterProp.setAlignmentX(Component.TOP_ALIGNMENT);
            setPrinterProp.setEnabled(false);
            buttonPanel.add(setPrinterProp);
            setVatTableButton = new JButton("Set VatTable");
            setVatTableButton.setMaximumSize(new Dimension(160,17));
            setVatTableButton.setPreferredSize(new Dimension(160,17));
            setVatTableButton.setActionCommand("setVatTable");
            setVatTableButton.addActionListener(methodListener);
            setVatTableButton.setAlignmentX(Component.TOP_ALIGNMENT);
            setVatTableButton.setEnabled(false);
            buttonPanel.add(setVatTableButton);
            
            //Fields Panel
            JPanel itemFieldPanel = new JPanel();
            
            itemFieldPanel.setLayout( new BoxLayout(itemFieldPanel, BoxLayout.Y_AXIS));
            itemFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            additionalTrailerTxt = new JTextField();
            additionalTrailerTxt.setMaximumSize(new Dimension(160,18));
            additionalTrailerTxt.setPreferredSize(new Dimension(160,18));
            additionalTrailerTxt.setMinimumSize(new Dimension(160,18));
            itemFieldPanel.add(additionalTrailerTxt);
            additionalHeaderTxt = new JTextField();
            additionalHeaderTxt.setMaximumSize(new Dimension(160,18));
            additionalHeaderTxt.setPreferredSize(new Dimension(160,18));
            additionalHeaderTxt.setMinimumSize(new Dimension(160,18));
            itemFieldPanel.add(additionalHeaderTxt);
            dateTxt = new JTextField();
            dateTxt.setMaximumSize(new Dimension(160,18));
            dateTxt.setPreferredSize(new Dimension(160,18));
            dateTxt.setMinimumSize(new Dimension(160,18));
            itemFieldPanel.add(dateTxt);
            headerTxt = new JTextField();
            headerTxt.setMaximumSize(new Dimension(160,18));
            headerTxt.setPreferredSize(new Dimension(160,18));
            headerTxt.setMinimumSize(new Dimension(160,18));
            itemFieldPanel.add(headerTxt);
            trailerTxt = new JTextField();
            trailerTxt.setMaximumSize(new Dimension(160,18));
            trailerTxt.setPreferredSize(new Dimension(160,18));
            trailerTxt.setMinimumSize(new Dimension(160,18));
            itemFieldPanel.add(trailerTxt);
            
            headerLineNumberTxt = new JTextField();
            headerLineNumberTxt.setMaximumSize(new Dimension(50,18));
            headerLineNumberTxt.setPreferredSize(new Dimension(50,18));
            headerLineNumberTxt.setMinimumSize(new Dimension(50,18));
            itemFieldPanel.add(headerLineNumberTxt);
            vatIdTxt = new JTextField();
            vatIdTxt.setMaximumSize(new Dimension(50,18));
            vatIdTxt.setPreferredSize(new Dimension(50,18));
            vatIdTxt.setMinimumSize(new Dimension(50,18));
            itemFieldPanel.add(vatIdTxt);
            vatValueTxt = new JTextField();
            vatValueTxt.setMaximumSize(new Dimension(50,18));
            vatValueTxt.setPreferredSize(new Dimension(50,18));
            vatValueTxt.setMinimumSize(new Dimension(50,18));
            itemFieldPanel.add(vatValueTxt);
            checkHealthTxt = new JTextField();
            checkHealthTxt.setMaximumSize(new Dimension(50,18));
            checkHealthTxt.setPreferredSize(new Dimension(50,18));
            checkHealthTxt.setMinimumSize(new Dimension(50,18));
            itemFieldPanel.add(checkHealthTxt);
            
            label = new JLabel("");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setMaximumSize(new Dimension(160,17));
            label.setPreferredSize(new Dimension(160,17));
            itemFieldPanel.add(label);
            
            doubleWidthCB= new JCheckBox("Set Double Width");
            doubleWidthCB.setMaximumSize(new Dimension(160,14));
            doubleWidthCB.setPreferredSize(new Dimension(160,14));
            doubleWidthCB.setMinimumSize(new Dimension(160,14));
            doubleWidthCB.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemFieldPanel.add(doubleWidthCB);
            JPanel propListPanel = new JPanel();
            propListPanel.setLayout(new BoxLayout(propListPanel,BoxLayout.Y_AXIS));
            propListPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Property : ");
            label.setMaximumSize(new Dimension(160,17));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            propListModel = new DefaultListModel();
            propList = new JList(propListModel);
            propList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            propList.setLayoutOrientation(JList.VERTICAL);
            propList.setVisibleRowCount(7);
            
            
            JScrollPane windowScrollPane = new JScrollPane(propList);
            windowScrollPane.setMinimumSize(new Dimension( 200,250));
            windowScrollPane.setPreferredSize(new Dimension(350,300));
            windowScrollPane.setMaximumSize(new Dimension( 350,300));
            propListPanel.add(label);
            propListPanel.add(windowScrollPane);
            getPropListButton = new JButton("Get Property");
            getPropListButton.setMaximumSize(new Dimension(200,17));
            getPropListButton.setPreferredSize(new Dimension(200,17));
            getPropListButton.setActionCommand("getPropList");
            getPropListButton.addActionListener(methodListener);
            getPropListButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getPropListButton.setEnabled(false);
            propListPanel.add(getPropListButton);
            getAdditionalHeaderButton = new JButton("Get Additional Header");
            getAdditionalHeaderButton.setMaximumSize(new Dimension(200,17));
            getAdditionalHeaderButton.setPreferredSize(new Dimension(200,17));
            getAdditionalHeaderButton.setActionCommand("getAdditionalHeader");
            getAdditionalHeaderButton.addActionListener(methodListener);
            getAdditionalHeaderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getAdditionalHeaderButton.setEnabled(false);
            propListPanel.add(getAdditionalHeaderButton);
            getAdditionalTrailerButton = new JButton("Get Additional Trailer");
            getAdditionalTrailerButton.setMaximumSize(new Dimension(200,17));
            getAdditionalTrailerButton.setPreferredSize(new Dimension(200,17));
            getAdditionalTrailerButton.setActionCommand("getAdditionalTrailer");
            getAdditionalTrailerButton.addActionListener(methodListener);
            getAdditionalTrailerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getAdditionalTrailerButton.setEnabled(false);
            propListPanel.add(getAdditionalTrailerButton);
            getCheckHealthButton = new JButton("Get CheckHealth");
            getCheckHealthButton.setMaximumSize(new Dimension(200,17));
            getCheckHealthButton.setPreferredSize(new Dimension(200,17));
            getCheckHealthButton.setActionCommand("getCheckHealth");
            getCheckHealthButton.addActionListener(methodListener);
            getCheckHealthButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getCheckHealthButton.setEnabled(false);
            propListPanel.add(getCheckHealthButton);
            JPanel fiscalPtrSettingControlPanel = new JPanel();
            fiscalPtrSettingControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            fiscalPtrSettingControlPanel.setLayout(new BoxLayout(fiscalPtrSettingControlPanel,BoxLayout.X_AXIS));
            fiscalPtrSettingControlPanel.add(buttonPanel);
            fiscalPtrSettingControlPanel.add(itemFieldPanel);
            fiscalPtrSettingControlPanel.add(propListPanel);
            
            return fiscalPtrSettingControlPanel;
        }
    }
    //Fiscal Receipt Panel
    class FiscalReceiptPanel extends Component{

		private static final long serialVersionUID = 1191991536139213593L;

		public Component make() {
            //Buttons Panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            beginFiscalReceiptButton = new JButton("Begin Fiscal Receipt");
            beginFiscalReceiptButton.setMaximumSize(new Dimension(160,17));
            beginFiscalReceiptButton.setPreferredSize(new Dimension(160,17));
            beginFiscalReceiptButton.setActionCommand("beginFiscalReceipt");
            beginFiscalReceiptButton.addActionListener(methodListener);
            beginFiscalReceiptButton.setAlignmentX(Component.TOP_ALIGNMENT);
            beginFiscalReceiptButton.setEnabled(false);
            buttonPanel.add(beginFiscalReceiptButton);
            printRecItemButton = new JButton("Print   Receipt   Item");
            printRecItemButton.setMaximumSize(new Dimension(160,17));
            printRecItemButton.setPreferredSize(new Dimension(160,17));
            printRecItemButton.setActionCommand("printRecItem");
            printRecItemButton.addActionListener(methodListener);
            printRecItemButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecItemButton.setEnabled(false);
            buttonPanel.add(printRecItemButton);
            printRecItemAdjButton = new JButton("Print Rec Adj  Item");
            printRecItemAdjButton.setMaximumSize(new Dimension(160,17));
            printRecItemAdjButton.setPreferredSize(new Dimension(160,17));
            printRecItemAdjButton.setActionCommand("printRecItemAdj");
            printRecItemAdjButton.addActionListener(methodListener);
            printRecItemAdjButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecItemAdjButton.setEnabled(false);
            buttonPanel.add(printRecItemAdjButton);
            printRecRefundButton = new JButton("Print Receipt Refund");
            printRecRefundButton.setMaximumSize(new Dimension(160,17));
            printRecRefundButton.setPreferredSize(new Dimension(160,17));
            printRecRefundButton.setActionCommand("printRecRefund");
            printRecRefundButton.addActionListener(methodListener);
            printRecRefundButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecRefundButton.setEnabled(false);
            buttonPanel.add(printRecRefundButton);
            printRecRefundVoidButton = new JButton("Print RecRef. Void");
            printRecRefundVoidButton.setMaximumSize(new Dimension(160,17));
            printRecRefundVoidButton.setPreferredSize(new Dimension(160,17));
            printRecRefundVoidButton.setActionCommand("printRecRefundVoid");
            printRecRefundVoidButton.addActionListener(methodListener);
            printRecRefundVoidButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecRefundVoidButton.setEnabled(false);
            buttonPanel.add(printRecRefundVoidButton);
            printRecTotalButton = new JButton("Print Receipt Total");
            printRecTotalButton.setMaximumSize(new Dimension(160,17));
            printRecTotalButton.setPreferredSize(new Dimension(160,17));
            printRecTotalButton.setActionCommand("printRecTotal");
            printRecTotalButton.addActionListener(methodListener);
            printRecTotalButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecTotalButton.setEnabled(false);
            buttonPanel.add(printRecTotalButton);
            printRecSubTotalButton = new JButton("Print Receipt SubT.");
            printRecSubTotalButton.setMaximumSize(new Dimension(160,17));
            printRecSubTotalButton.setPreferredSize(new Dimension(160,17));
            printRecSubTotalButton.setActionCommand("printRecSubTotal");
            printRecSubTotalButton.addActionListener(methodListener);
            printRecSubTotalButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecSubTotalButton.setEnabled(false);
            buttonPanel.add(printRecSubTotalButton);
            printRecVoidButton = new JButton("Print Receipt Void");
            printRecVoidButton.setMaximumSize(new Dimension(160,17));
            printRecVoidButton.setPreferredSize(new Dimension(160,17));
            printRecVoidButton.setActionCommand("printRecVoid");
            printRecVoidButton.addActionListener(methodListener);
            printRecVoidButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecVoidButton.setEnabled(false);
            buttonPanel.add(printRecVoidButton);
            
            printRecVoidItemButton = new JButton("Print Rec. Void Item");
            printRecVoidItemButton.setMaximumSize(new Dimension(160,17));
            printRecVoidItemButton.setPreferredSize(new Dimension(160,17));
            printRecVoidItemButton.setActionCommand("printRecVoidItem");
            printRecVoidItemButton.addActionListener(methodListener);
            printRecVoidItemButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecVoidItemButton.setEnabled(false);
            buttonPanel.add(printRecVoidItemButton);
            recSubTotalDiscountButton = new JButton("Rec. SubTotal Adj.");
            recSubTotalDiscountButton.setMaximumSize(new Dimension(160,17));
            recSubTotalDiscountButton.setPreferredSize(new Dimension(160,17));
            recSubTotalDiscountButton.setActionCommand("recSubTotalDiscount");
            recSubTotalDiscountButton.addActionListener(methodListener);
            recSubTotalDiscountButton.setAlignmentX(Component.TOP_ALIGNMENT);
            recSubTotalDiscountButton.setEnabled(false);
            buttonPanel.add(recSubTotalDiscountButton);
            recSubTotalDiscountVoidButton = new JButton("Rec. SubT. Adj. Void");
            recSubTotalDiscountVoidButton.setMaximumSize(new Dimension(160,17));
            recSubTotalDiscountVoidButton.setPreferredSize(new Dimension(160,17));
            recSubTotalDiscountVoidButton.setActionCommand("recSubTotalDiscountVoid");
            recSubTotalDiscountVoidButton.addActionListener(methodListener);
            recSubTotalDiscountVoidButton.setAlignmentX(Component.TOP_ALIGNMENT);
            recSubTotalDiscountVoidButton.setEnabled(false);
            buttonPanel.add(recSubTotalDiscountVoidButton);
            printRecMessageButton = new JButton("Print Rec. Message");
            printRecMessageButton.setMaximumSize(new Dimension(160,17));
            printRecMessageButton.setPreferredSize(new Dimension(160,17));
            printRecMessageButton.setActionCommand("printRecMessage");
            printRecMessageButton.addActionListener(methodListener);
            printRecMessageButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printRecMessageButton.setEnabled(false);
            buttonPanel.add(printRecMessageButton);
            
            printDuplicateRecButton = new JButton("Duplicate Receipt");
            printDuplicateRecButton.setMaximumSize(new Dimension(160,17));
            printDuplicateRecButton.setPreferredSize(new Dimension(160,17));
            printDuplicateRecButton.setActionCommand("printDuplicateRec");
            printDuplicateRecButton.addActionListener(methodListener);
            printDuplicateRecButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printDuplicateRecButton.setEnabled(false);
            buttonPanel.add(printDuplicateRecButton);
            endFiscalReceiptButton = new JButton("End  Fiscal   Receipt");
            endFiscalReceiptButton.setMaximumSize(new Dimension(160,17));
            endFiscalReceiptButton.setPreferredSize(new Dimension(160,17));
            endFiscalReceiptButton.setActionCommand("endFiscalReceipt");
            endFiscalReceiptButton.addActionListener(methodListener);
            endFiscalReceiptButton.setAlignmentX(Component.TOP_ALIGNMENT);
            endFiscalReceiptButton.setEnabled(false);
            buttonPanel.add(endFiscalReceiptButton);
            //Labels Panel
            JPanel itemLabelPanel = new JPanel();
            itemLabelPanel.setLayout( new BoxLayout(itemLabelPanel, BoxLayout.Y_AXIS));
            itemLabelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            itemLabelPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Receipt Message");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("PreLine Text");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("PostLine Text");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Description");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Price-Amount");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Quantity");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Unit Price");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Vat Info");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Unit Name");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Amount - Adj.");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            //Fields Panel
            JPanel itemFieldPanel = new JPanel();
            itemFieldPanel.setLayout( new BoxLayout(itemFieldPanel, BoxLayout.Y_AXIS));
            itemFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            recMessageTxt = new JTextField();
            recMessageTxt.setMaximumSize(new Dimension(160,17));
            recMessageTxt.setPreferredSize(new Dimension(160,17));
            recMessageTxt.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(recMessageTxt);
            preLineTxt = new JTextField();
            preLineTxt.setMaximumSize(new Dimension(160,17));
            preLineTxt.setPreferredSize(new Dimension(160,17));
            preLineTxt.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(preLineTxt);
            postLineTxt = new JTextField();
            postLineTxt.setMaximumSize(new Dimension(160,17));
            postLineTxt.setPreferredSize(new Dimension(160,17));
            postLineTxt.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(postLineTxt);
            itemDescription = new JTextField();
            itemDescription.setMaximumSize(new Dimension(160,17));
            itemDescription.setPreferredSize(new Dimension(160,17));
            itemDescription.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemDescription);
            itemPrice = new JTextField();
            itemPrice.setMaximumSize(new Dimension(160,17));
            itemPrice.setPreferredSize(new Dimension(160,17));
            itemPrice.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemPrice);
            itemQuantity = new JTextField();
            itemQuantity.setMaximumSize(new Dimension(160,17));
            itemQuantity.setPreferredSize(new Dimension(160,17));
            itemQuantity.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemQuantity);
            itemUnitPrice = new JTextField();
            itemUnitPrice.setMaximumSize(new Dimension(160,17));
            itemUnitPrice.setPreferredSize(new Dimension(160,17));
            itemUnitPrice.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemUnitPrice);
            itemVatInfo = new JTextField();
            itemVatInfo.setMaximumSize(new Dimension(160,17));
            itemVatInfo.setPreferredSize(new Dimension(160,17));
            itemVatInfo.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemVatInfo);
            unitName = new JTextField();
            unitName.setMaximumSize(new Dimension(160,17));
            unitName.setPreferredSize(new Dimension(160,17));
            unitName.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(unitName);
            itemAmountPercAdjTxt = new JTextField();
            itemAmountPercAdjTxt.setMaximumSize(new Dimension(160,17));
            itemAmountPercAdjTxt.setPreferredSize(new Dimension(160,17));
            itemAmountPercAdjTxt.setMinimumSize(new Dimension(160,17));
            itemFieldPanel.add(itemAmountPercAdjTxt);
            //Combo Panel
            JPanel comboPanel = new JPanel();
            comboPanel.setLayout( new BoxLayout(comboPanel, BoxLayout.Y_AXIS));
            comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            comboPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Payament Forms :");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setMaximumSize(new Dimension(130,22));
            label.setPreferredSize(new Dimension(130,22));
            comboPanel.add(label);
            paymantFormCombo = new JComboBox();
            paymantFormCombo.setMaximumSize(new Dimension(150,22));
            paymantFormCombo.setPreferredSize(new Dimension(150,22));
            paymantFormCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            paymantFormCombo.setActionCommand("paymantFormCombo");
            paymantFormCombo.addActionListener(methodListener);
            paymantFormCombo.setEnabled(false);
            comboPanel.add(paymantFormCombo);
            label = new JLabel("Vat ID :");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setMaximumSize(new Dimension(130,22));
            label.setPreferredSize(new Dimension(130,22));
            comboPanel.add(label);
            vatIdCombo = new JComboBox();
            vatIdCombo.setMaximumSize(new Dimension(150,22));
            vatIdCombo.setPreferredSize(new Dimension(150,22));
            vatIdCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            vatIdCombo.setActionCommand("vatIdCombo");
            vatIdCombo.addActionListener(methodListener);
            vatIdCombo.setEnabled(false);
            comboPanel.add(vatIdCombo);
            label = new JLabel("Adjustment Type :");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setMaximumSize(new Dimension(130,22));
            label.setPreferredSize(new Dimension(130,22));
            comboPanel.add(label);
            adjustmentTypeCombo = new JComboBox();
            adjustmentTypeCombo.setMaximumSize(new Dimension(150,22));
            adjustmentTypeCombo.setPreferredSize(new Dimension(150,22));
            adjustmentTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            adjustmentTypeCombo.setActionCommand("adjustmentTypeCombo");
            adjustmentTypeCombo.addActionListener(methodListener);
            // adjustmentTypeCombo.setFont(new Font(null,Font.PLAIN,10));
            adjustmentTypeCombo.setEnabled(true);
            for(int i=0;i<numAdjType;i++)adjustmentTypeCombo.addItem(adjType[i]);
            comboPanel.add(adjustmentTypeCombo);
            
            
            JPanel fiscalReceiptControlPanel = new JPanel();
            fiscalReceiptControlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            fiscalReceiptControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            fiscalReceiptControlPanel.setLayout(new BoxLayout(fiscalReceiptControlPanel,BoxLayout.X_AXIS));
            fiscalReceiptControlPanel.add(buttonPanel);
            fiscalReceiptControlPanel.add(itemLabelPanel);
            fiscalReceiptControlPanel.add(itemFieldPanel);
            fiscalReceiptControlPanel.add(comboPanel);
            
            
            return fiscalReceiptControlPanel;
        }
    }
    
    //Fiscal Printer Fiscal Report Panel
    
    class FiscalPrinterFscReportPanel extends Component{

		private static final long serialVersionUID = 8226960362715736598L;

		public Component make() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            printReportButton = new JButton("Print Report");
            printReportButton.setMaximumSize(new Dimension(160,17));
            printReportButton.setPreferredSize(new Dimension(160,17));
            printReportButton.setActionCommand("printReport");
            printReportButton.addActionListener(methodListener);
            printReportButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printReportButton.setEnabled(false);
            buttonPanel.add(printReportButton);
            printXReportButton = new JButton("Print X Report");
            printXReportButton.setMaximumSize(new Dimension(160,17));
            printXReportButton.setPreferredSize(new Dimension(160,17));
            printXReportButton.setActionCommand("printXReport");
            printXReportButton.addActionListener(methodListener);
            printXReportButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printXReportButton.setEnabled(false);
            buttonPanel.add(printXReportButton);
            printZReportButton = new JButton("Print Z Report");
            printZReportButton.setMaximumSize(new Dimension(160,17));
            printZReportButton.setPreferredSize(new Dimension(160,17));
            printZReportButton.setActionCommand("printZReport");
            printZReportButton.addActionListener(methodListener);
            printZReportButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printZReportButton.setEnabled(false);
            buttonPanel.add(printZReportButton);
            printPeriodicTotalReportButton = new JButton("Print Periodic Report");
            printPeriodicTotalReportButton.setMaximumSize(new Dimension(160,17));
            printPeriodicTotalReportButton.setPreferredSize(new Dimension(160,17));
            printPeriodicTotalReportButton.setActionCommand("printPeriodicReport");
            printPeriodicTotalReportButton.addActionListener(methodListener);
            printPeriodicTotalReportButton.setAlignmentX(Component.TOP_ALIGNMENT);
            printPeriodicTotalReportButton.setEnabled(false);
            buttonPanel.add(printPeriodicTotalReportButton);
            //Labels Panel
            JPanel itemLabelPanel = new JPanel();
            itemLabelPanel.setLayout( new BoxLayout(itemLabelPanel, BoxLayout.Y_AXIS));
            itemLabelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            itemLabelPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Report From :");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            label = new JLabel("Report To :");
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,17));
            label.setPreferredSize(new Dimension(110,17));
            itemLabelPanel.add(label);
            //Fields Panel
            JPanel itemFieldPanel = new JPanel();
            itemFieldPanel.setLayout( new BoxLayout(itemFieldPanel, BoxLayout.Y_AXIS));
            itemFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            reportFrom = new JTextField();
            reportFrom.setMaximumSize(new Dimension(130,17));
            reportFrom.setPreferredSize(new Dimension(130,17));
            reportFrom.setMinimumSize(new Dimension(130,17));
            itemFieldPanel.add(reportFrom);
            reportTo = new JTextField();
            reportTo.setMaximumSize(new Dimension(130,17));
            reportTo.setPreferredSize(new Dimension(130,17));
            reportTo.setMinimumSize(new Dimension(130,17));
            itemFieldPanel.add(reportTo);
            
            
            //Combo Panel
            JPanel comboPanel = new JPanel();
            comboPanel.setLayout( new BoxLayout(comboPanel, BoxLayout.Y_AXIS));
            comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            comboPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            label = new JLabel("Report Type :");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setMaximumSize(new Dimension(110,22));
            label.setPreferredSize(new Dimension(110,22));
            comboPanel.add(label);
            reportTypeCombo = new JComboBox();
            reportTypeCombo.setMaximumSize(new Dimension(110,22));
            reportTypeCombo.setPreferredSize(new Dimension(110,22));
            reportTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            reportTypeCombo.setActionCommand("reportTypeCombo");
            reportTypeCombo.addActionListener(methodListener);
            reportTypeCombo.setFont(new Font(null,Font.PLAIN,10));
            reportTypeCombo.setEnabled(true);
            reportTypeCombo.addItem("ORDINAL");
            reportTypeCombo.addItem("DATE");
            comboPanel.add(reportTypeCombo);
            JPanel fiscalPtrFscReportControlPanel = new JPanel();
            fiscalPtrFscReportControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            fiscalPtrFscReportControlPanel.setLayout(new BoxLayout(fiscalPtrFscReportControlPanel,BoxLayout.X_AXIS));
            fiscalPtrFscReportControlPanel.add(buttonPanel);
            fiscalPtrFscReportControlPanel.add(itemLabelPanel);
            fiscalPtrFscReportControlPanel.add(itemFieldPanel);
            fiscalPtrFscReportControlPanel.add(reportTypeCombo);
            return fiscalPtrFscReportControlPanel;
            
        }
    }
    //Fiscal Printer Status Panel
    
    class FiscalPrinterStatusPanel extends Component{

		private static final long serialVersionUID = 8878274840702299074L;

		public Component make() {
            //List Items Panel
            JPanel itemListPanel = new JPanel();
            itemListPanel.setLayout(new BoxLayout(itemListPanel,BoxLayout.Y_AXIS));
            itemListPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            
            label = new JLabel("Text Output: ");
            label.setMaximumSize(new Dimension(160,17));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemListPanel.add(label);
            
            itemListModel = new DefaultListModel();
            itemList = new JList(itemListModel);
            itemList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            itemList.setLayoutOrientation(JList.VERTICAL);
            itemList.setVisibleRowCount(7);
            
            
            JScrollPane windowScrollPane = new JScrollPane(itemList);
            windowScrollPane.setMinimumSize(new Dimension( 200,250));
            windowScrollPane.setPreferredSize(new Dimension(350,300));
            windowScrollPane.setMaximumSize(new Dimension( 350,300));
            itemListPanel.add(windowScrollPane);
            clearOutPutButton = new JButton("Clear Text Output");
            clearOutPutButton.setMaximumSize(new Dimension(160,17));
            clearOutPutButton.setPreferredSize(new Dimension(160,17));
            clearOutPutButton.setActionCommand("clearOutPut");
            clearOutPutButton.addActionListener(methodListener);
            clearOutPutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearOutPutButton.setEnabled(true);
            itemListPanel.add(clearOutPutButton);
            resetPrinterButton = new JButton("Reset Printer");
            resetPrinterButton.setMaximumSize(new Dimension(160,17));
            resetPrinterButton.setPreferredSize(new Dimension(160,17));
            resetPrinterButton.setActionCommand("resetPrinter");
            resetPrinterButton.addActionListener(methodListener);
            resetPrinterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            resetPrinterButton.setEnabled(false);
            itemListPanel.add(resetPrinterButton);
            clearOutputForErrorCB= new JCheckBox("Clear output for Error Event");
            clearOutputForErrorCB.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemListPanel.add(clearOutputForErrorCB);
            //dataPanel
            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel,BoxLayout.Y_AXIS));
            dataPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            ButtonGroup bg=new ButtonGroup();
            firmwareRB=new JRadioButton("Firmware Release Number");//firmware
            bg.add(firmwareRB);dataPanel.add(firmwareRB);firmwareRB.doClick();
            printerIDRB=new JRadioButton("Printer's Fiscal ID");
            bg.add(printerIDRB);dataPanel.add(printerIDRB);
            currentRecTotalRB=new JRadioButton("Current Receipt Total");
            bg.add(currentRecTotalRB);dataPanel.add(currentRecTotalRB);
            currentTotalRB=new JRadioButton("Daily Total");
            bg.add(currentTotalRB);dataPanel.add(currentTotalRB);
            grandTotalRB=new JRadioButton("Fiscal Printer's Grand Total");
            bg.add(grandTotalRB);dataPanel.add(grandTotalRB);
            mitVoidRB=new JRadioButton("Total Number of Voided Receipts");
            bg.add(mitVoidRB);dataPanel.add(mitVoidRB);
            fiscalRecRB=new JRadioButton("N. of Daily Fiscal Sales Receipts");
            bg.add(fiscalRecRB);dataPanel.add(fiscalRecRB);
            receiptNumberRB=new JRadioButton("N. of Fiscal Receipts Printed");
            bg.add(receiptNumberRB);dataPanel.add(receiptNumberRB);
            refundRB=new JRadioButton("Current Total of Refunds");
            bg.add(refundRB);dataPanel.add(refundRB);
            fiscalRecVoidRB=new JRadioButton("N. of Daily Voided Fiscal Sales Receipt");
            bg.add(fiscalRecVoidRB);dataPanel.add(fiscalRecVoidRB);
            nonFiscalRecRB=new JRadioButton("N. of Daily Non Fiscal Sales Receipts");
            bg.add(nonFiscalRecRB);dataPanel.add(nonFiscalRecRB);
            descriptionLengthRB=new JRadioButton("Description Length");
            bg.add(descriptionLengthRB);dataPanel.add(descriptionLengthRB);
            zReportRB=new JRadioButton("Z Report");
            bg.add(zReportRB);dataPanel.add(zReportRB);
            getDataButton = new JButton("GetData");
            getDataButton.setMaximumSize(new Dimension(160,17));
            getDataButton.setPreferredSize(new Dimension(160,17));
            getDataButton.setActionCommand("getData");
            getDataButton.addActionListener(methodListener);
            getDataButton.setAlignmentX(Component.TOP_ALIGNMENT);
            getDataButton.setEnabled(false);
            dataPanel.add(getDataButton);
            
            //dataPanelBis
            JPanel dataPanelBis = new JPanel();
            dataPanelBis.setLayout(new BoxLayout(dataPanelBis,BoxLayout.Y_AXIS));
            dataPanelBis.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
            ButtonGroup bgbis=new ButtonGroup();
            dayTotalizerRB=new JRadioButton("Daily Totalizer");
            dayTotalizerRB.setAlignmentX(Component.CENTER_ALIGNMENT);
            bgbis.add(dayTotalizerRB);dataPanelBis.add(dayTotalizerRB);dayTotalizerRB.doClick();
            grandTotalizerRB=new JRadioButton("Grand Totalizer");
            grandTotalizerRB.setAlignmentX(Component.CENTER_ALIGNMENT);
            bgbis.add(grandTotalizerRB);dataPanelBis.add(grandTotalizerRB);
            
            totalTypeCombo = new JComboBox();
            totalTypeCombo.setMaximumSize(new Dimension(160,22));
            totalTypeCombo.setPreferredSize(new Dimension(160,22));
            totalTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            totalTypeCombo.setActionCommand("totalTypeCombo");
            //  totalTypeCombo.setFont(new Font(null,Font.PLAIN,10));
            totalTypeCombo.setEnabled(true);
            totalTypeCombo.addItem("Item Totalizer");totalTypeCombo.addItem("Refund Totalizer");
            totalTypeCombo.addItem("Voided Item Totalizer");totalTypeCombo.addItem("Discount Totalizer");
            totalTypeCombo.addItem("Gross Totalizer");
            dataPanelBis.add(totalTypeCombo);
            vatIdCombo_2 = new JComboBox();
            vatIdCombo_2.setMaximumSize(new Dimension(150,22));
            vatIdCombo_2.setPreferredSize(new Dimension(150,22));
            vatIdCombo_2.setAlignmentX(Component.CENTER_ALIGNMENT);
            vatIdCombo_2.setActionCommand("vatIdCombo_2");
            vatIdCombo_2.addActionListener(methodListener);
            vatIdCombo_2.setEnabled(false);
            dataPanelBis.add(vatIdCombo_2);
            dataPanelBis.add(Box.createVerticalStrut(17));
            getTotalizerButton = new JButton("GetTotalizer");
            getTotalizerButton.setMaximumSize(new Dimension(160,17));
            getTotalizerButton.setPreferredSize(new Dimension(160,17));
            getTotalizerButton.setActionCommand("getTotalizer");
            getTotalizerButton.addActionListener(methodListener);
            getTotalizerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getTotalizerButton.setEnabled(false);
            dataPanelBis.add(getTotalizerButton);
            getDateButton = new JButton("Get Date");
            getDateButton.setMaximumSize(new Dimension(160,17));
            getDateButton.setPreferredSize(new Dimension(160,17));
            getDateButton.setActionCommand("getDate");
            getDateButton.addActionListener(methodListener);
            getDateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getDateButton.setEnabled(false);
            dataPanelBis.add(getDateButton);
            getTrainingStateButton = new JButton("Get Training Mode");
            getTrainingStateButton.setMaximumSize(new Dimension(160,17));
            getTrainingStateButton.setPreferredSize(new Dimension(160,17));
            getTrainingStateButton.setActionCommand("getTrainingState");
            getTrainingStateButton.addActionListener(methodListener);
            getTrainingStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getTrainingStateButton.setEnabled(false);
            dataPanelBis.add(getTrainingStateButton);
            getErrorLevelButton = new JButton("Get Error Info");
            getErrorLevelButton.setMaximumSize(new Dimension(160,17));
            getErrorLevelButton.setPreferredSize(new Dimension(160,17));
            getErrorLevelButton.setActionCommand("getErrorLevel");
            getErrorLevelButton.addActionListener(methodListener);
            getErrorLevelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getErrorLevelButton.setEnabled(false);
            dataPanelBis.add(getErrorLevelButton);
            
            getOutPutIdButton = new JButton("Get Output ID");
            getOutPutIdButton.setMaximumSize(new Dimension(160,17));
            getOutPutIdButton.setPreferredSize(new Dimension(160,17));
            getOutPutIdButton.setActionCommand("getOutPutId");
            getOutPutIdButton.addActionListener(methodListener);
            getOutPutIdButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            getOutPutIdButton.setEnabled(false);
            dataPanelBis.add(getOutPutIdButton);
            
            printerStatusButton = new JButton("Get Printer Status");
            printerStatusButton.setMaximumSize(new Dimension(160,17));
            printerStatusButton.setPreferredSize(new Dimension(160,17));
            printerStatusButton.setActionCommand("printerStatus");
            printerStatusButton.addActionListener(methodListener);
            printerStatusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            printerStatusButton.setEnabled(false);
            dataPanelBis.add(printerStatusButton);
            
            
            dayOpenedButton = new JButton("Get Day Opened");
            dayOpenedButton.setMaximumSize(new Dimension(160,17));
            dayOpenedButton.setPreferredSize(new Dimension(160,17));
            dayOpenedButton.setActionCommand("dayOpened");
            dayOpenedButton.addActionListener(methodListener);
            dayOpenedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            dayOpenedButton.setEnabled(false);
            dataPanelBis.add(dayOpenedButton);
            remainingFiscalMemoryButton = new JButton("Get Rem. Fiscal Memory");
            remainingFiscalMemoryButton.setMaximumSize(new Dimension(160,17));
            remainingFiscalMemoryButton.setPreferredSize(new Dimension(160,17));
            remainingFiscalMemoryButton.setActionCommand("remainingFiscalMemory");
            remainingFiscalMemoryButton.addActionListener(methodListener);
            remainingFiscalMemoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            remainingFiscalMemoryButton.setEnabled(false);
            dataPanelBis.add(remainingFiscalMemoryButton);
            showPropertyButton = new JButton("Show Other Property");
            showPropertyButton.setMaximumSize(new Dimension(160,17));
            showPropertyButton.setPreferredSize(new Dimension(160,17));
            showPropertyButton.setActionCommand("showProperty");
            showPropertyButton.addActionListener(methodListener);
            showPropertyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            showPropertyButton.setEnabled(false);
            dataPanelBis.add(showPropertyButton);
            
            JPanel fiscalPtrStatusControlPanel = new JPanel();
            fiscalPtrStatusControlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
            fiscalPtrStatusControlPanel.setLayout(new BoxLayout(fiscalPtrStatusControlPanel,BoxLayout.X_AXIS));
            fiscalPtrStatusControlPanel.add(itemListPanel);
            fiscalPtrStatusControlPanel.add(dataPanel);
            fiscalPtrStatusControlPanel.add(dataPanelBis);
            return fiscalPtrStatusControlPanel;
        }
    }
    private class StatusTimerUpdateTask extends java.util.TimerTask{
        public void run(){
            if(fiscalPrinter != null){
                mainButtonPanel.currentStatus.setText(MainButtonPanel.getStatusString(fiscalPrinter.getState()));
            }
        }
    }
}

