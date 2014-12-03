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

    private static void connectReader() 
    {
        String[] portNames = scanForReader();
        int portNumber = selectPortNumber(portNames);
        setBaudRate();
       
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
        
        return number;
    }

    private static void setBaudRate() {
        
        try {
            System.out.println("Changing baudrate to " + 0);
            r.paramSet("/reader/baudrate", Integer.parseInt("0"));
            System.out.println(r.paramGet("/reader/baudrate"));
        } catch (ReaderException ex) {
            java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      
}
