POStest

POStest is a GUI based utility for exercising POS devices using JavaPOS. 
Currently it supports the following devices:
- Cash Drawer
- HardTotals
- Keylock
- Line Display
- MICR
- MotionSensor
- MSR
- PIN Pad
- POSKeyboard
- POSPrinter
- Scale
- Scanner
- Signature Capture
- ToneIndicator


To use POStest, the classpath should be configured for JavaPOS. This means
that the classpath should be configured to include the location of POStest.
This includes the following packages: 
 - Jpos 1.8 Controls (jpos18.jar)
 - Xerces (xerces.jar)
 - and the JavaPOS services for the devices.

The jpos.xml file should also contain the necessary JposEntries for the 
devices to be used.

To build POStest automaticly, type "ant all", (You must have ant installed)

To build by hand, simply compile the classes in src/com/jpos/POStest.

To run POStest:
  If you have the POStest.jar built, you can use the scripts:
    bin/POStest.sh   (Linux)
       - or -
    bin/POStest.bat  (Windows)
 
  Be sure to edit the classpath's in these scripts to reflect the correct
 settings for your system.

To run POStest not from a jar, enter the following at a command line:
>java com.jpos.POStest.POStest

