/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package therfogs;

import jssc.*;
import com.thingmagic.*;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author BCG
 */
public class TheRFOGs {
    
    static Reader r = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
 
        connectReader();
        
        //testSerial();
    }
    
    protected static void testSerial() throws ReaderException, Exception
    {
        System.out.println("Reached");
 
        
        Reader r = null;
        r = Reader.create("tmr:///COM6");
        r.connect();
        
        if (Reader.Region.UNSPEC == (Reader.Region)r.paramGet("/reader/region/id"))
        {
            Reader.Region[] supportedRegions = (Reader.Region[])r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
            if (supportedRegions.length < 1)
            {
                  throw new Exception("Reader doesn't support any regions");
            }
            else
            {
                  r.paramSet("/reader/region/id", supportedRegions[0]);
            }
        }
        
    }

    private static String[] scanForReader() 
    {
        //will be used to tell us if a reader is plugged in.
        boolean detected = false;
        //holds the names of all the connected ports
        String [] portNames;
            
        System.out.println("Looking for connected devices...");
            
        //loop through until a device is connected
        do
        {
            //gets the name of all the connected serial ports
            portNames = SerialPortList.getPortNames();

            if(portNames.length != 0)
                detected = true;
        }while(detected == false);
    
        System.out.println("Connected Devices Found");
        return portNames;
    }

    private static void connectReader() throws Exception 
    {
        try {
            String[] portNames = scanForReader();
            int portNumber = selectPortNumber(portNames);
            //need to create the reader
            r = Reader.create("tmr:///" + portNames[portNumber]);
            setBaudRate();
            System.out.println("Attempting to connect to Reader");
            r.connect();
            System.out.println("Connected to reader");
            setRegion();
        } catch (ReaderException ex) {
            java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
       
    }

    private static int selectPortNumber(String[] portNames) {
        System.out.println("\n Which device would you like to connect to?");
        for(int i = 0; i < portNames.length; i++)
        {
            System.out.println((i+1) + ": " + portNames[i]);
        }
        
        Scanner inputReader = new Scanner(System.in);
        System.out.print("Enter the number to the left of the device name you wish to connect to: ");
        int number = 0;
        
        try{
            
            number = inputReader.nextInt();
            System.out.println("");
            while(number < 1 || number > portNames.length)
            {
                System.out.print("Please enter a number between 1 and " + portNames.length + ": ");
                number = inputReader.nextInt();
                System.out.println("");
            }
        }
        catch(InputMismatchException e)
        {
            System.out.println("You must enter a number next time. Closing Application");
            System.exit(1);
        }
        
        System.out.println("You selected: " + portNames[number-1]);
        
        return (number-1);
    }

    private static void setBaudRate() {
        
        Scanner inputReader = new Scanner(System.in);
        System.out.println("Available Baud Rates");
        System.out.println("9600");
        System.out.println("19200");
        System.out.println("38400");
        System.out.println("230400");
        System.out.println("460800");
        System.out.println("921600");
        System.out.print("Enter one of the baud rates from above (0 uses the default baud rate): ");
        int baud = 0;
        
        try{
            
            baud = inputReader.nextInt();
            System.out.println("");
            while(baud != 0 && baud != 9600 && baud != 19200 && baud != 38400 && baud != 230400
                    && baud != 460800 && baud != 921600)
            {
                System.out.print("Please select one of the baud rates listed: ");
                baud = inputReader.nextInt();
                System.out.println("");
            }
        }
        catch(InputMismatchException e)
        {
            System.out.println("You must enter a number next time. Closing Application");
            System.exit(1);
        }
        
        if(baud > 1)
        {
            try {
                System.out.println("Changing baudrate to " + baud);
                r.paramSet("/reader/baudrate", baud);
                System.out.println(r.paramGet("/reader/baudrate"));
            } catch (ReaderException ex) {
                java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }

    private static void setRegion() throws Exception 
    {
        System.out.println("Setting Region");
        if (Reader.Region.UNSPEC == (Reader.Region)r.paramGet("/reader/region/id"))
        {
            Reader.Region[] supportedRegions = (Reader.Region[])r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
            if (supportedRegions.length < 1)
            {
                  throw new Exception("Reader doesn't support any regions");
            }
            else
            {
                  r.paramSet("/reader/region/id", supportedRegions[0]);
            }
        }
        System.out.println("Region Set");
    }
      
}
