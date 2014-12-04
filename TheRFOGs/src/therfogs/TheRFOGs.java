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
        boolean connected = false;
        Scanner inputReader = new Scanner(System.in);
        while(true)
        {
            if(connected == false)
            {
                System.out.println("1) Connect to Reader\n" +
                    "2) Exit\n");
                System.out.print("Enter one of the above commands: ");
            }
            else{
                System.out.println("1) Read Async\n" +
                    "2) Read for 1 sec\n" +
                    "3) Write EPC\n" +
                    "4) Exit\n");
                System.out.print("Enter one of the above commands: ");
            }
            
            int command = 4;
            try{
            
                command = inputReader.nextInt();
                System.out.println("");
                if(connected == false)
                {
                    if(command == 1){
                        connectReader();
                        connected = true;
                    }
                    else if(command == 2)
                    {
                        //disconnectReader();
                        System.out.println("Closing the application");
                        System.exit(1);
                    }
                }
                else{
                    if(command == 1)
                        asyncRead();
                    else if(command == 2)
                        readTags();
                    else if(command == 3)
                        writeEPC();
                    else if(command == 4)
                    {
                        disconnectReader();
                        System.out.println("Closing the application");
                        System.exit(1);
                    }
                }
            }
            catch(InputMismatchException e)
            {
                disconnectReader();
                System.out.println("You must enter a number next time. Closing Application");
                System.exit(1);
            }
            
            //connectReader();
        
           // asyncRead();
        }
            //disconnectReader();
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
            disconnectReader();
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
            disconnectReader();
            System.out.println("You must enter a number next time. Closing Application");
            System.exit(1);
        }
        
        System.out.println("You selected: " + portNames[number-1]);
        
        return (number-1);
    }

    private static void setBaudRate() {
        
        Scanner inputReader = new Scanner(System.in);
        System.out.println("Available Baud Rates:");
        System.out.println("9600");
        System.out.println("19200");
        System.out.println("38400");
        System.out.println("230400");
        System.out.println("460800");
        System.out.println("921600");
        System.out.print("Enter one of the baud rates from above "+
                "(entering 0 uses the default baud rate): ");
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
            disconnectReader();
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
                disconnectReader();
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
                  disconnectReader();
                  throw new Exception("Reader doesn't support any regions");
            }
            else
            {
                  r.paramSet("/reader/region/id", supportedRegions[0]);
            }
        }
        System.out.println("Region Set");
    }

    private static void disconnectReader() 
    {
        r.destroy();
    }

    //reads tag for a set amount of time
    private static TagReadData[] readTags() {
        TagReadData[] tags = null;
        try { 
            tags = r.read(1000);
            for(int x = 0; x < tags.length; x++)
            {
                TagData tag = tags[x].getTag();
                System.out.println("Tag " + (x+1) + " EPC: " + tag.epcString());
            }
        } catch (ReaderException ex) {
            disconnectReader();
            java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tags;
    }
  
    //ignore this for now
  public static void asyncRead() throws ReaderException
  {
     ReadListener l = new PrintListener();
        //timeout = 1000; // Is there a per-reader default value?
        r.addReadListener(l);
        r.startReading();
        
        Scanner inputReader = new Scanner(System.in);
        System.out.print("Enter 0 to Stop Reading: ");
        
        boolean stopReading = false;
        do{
            try{
                int number = inputReader.nextInt();
                if(number == 0)
                {
                    stopReading = true;
                }
            }
            catch(InputMismatchException e)
            {
                r.stopReading();
                r.removeReadListener(l);
                disconnectReader();
                System.out.println("You must enter a number next time. Closing Application");
                System.exit(1);
            }
        }while(stopReading == false);
    
        //Thread.sleep(timeout);
        r.stopReading();
        r.removeReadListener(l);
  }

    private static void writeEPC() throws ReaderException {
        TagReadData tags[] = readTags();
        
        Scanner inputReader = new Scanner(System.in);
        try{
            System.out.println("Which of the above tags would you like to write the epc of (only enter the tag#): ");
            int tagNum = inputReader.nextInt();
            while(tagNum < 1 || tagNum > tags.length)
            {
                System.out.println("Please select a valid tag number.");
                readTags();
                System.out.println("Which of the above tags would you like to write the epc of (only enter the tag#): ");
                tagNum = inputReader.nextInt();
            }
            
            String newEPC = "";
            
            System.out.println("What is the new EPC#(24 numbers in length): ");
            newEPC = inputReader.nextLine();
            
            while(newEPC.length() != 24)
            {
                System.out.println("Please enter a valid EPC number (24 numbers in length): ");
                newEPC = inputReader.nextLine();
            }
            
            TagData t = new TagData(newEPC);

            //NEED TO FIX THIS TO TARGET THE CORRECT TAG
            r.writeTag(null, t);
        }
        catch(InputMismatchException e)
        {
            System.out.println("Expecting a number, closing application");
            r.destroy();
            System.exit(1);
        }
        
        
    }
      
  //prints the tag info async
    static class PrintListener implements ReadListener
    {
        Vector<TagReadData> tags = new Vector<TagReadData>();
        Vector<Integer> counts = new Vector<Integer>();
        
        public void tagRead(Reader r, TagReadData tr)
        {
            //String epc = tr.epcString();
            listTags(tr);
            //System.out.println("Background read: " + tr.toString());
        }
        private void listTags(TagReadData tr)
        {
            if(tags.size() == 0){
                tags.add(tr);
                counts.add(1);
            }
            boolean duplicate = false;
            for(int x = 0; x < tags.size(); x++)
            {
                String epc = tr.epcString();
                //checking for same epc
                if(epc.compareTo(tags.get(x).epcString()) == 0){
                    duplicate = true;
                    counts.set(x, counts.get(x)+1);
                }
            }
            
            if(!duplicate)
            {
                tags.add(tr);
                counts.add(1);
            }
            
            for(int x = 0; x < tags.size(); x++)
            {
                System.out.println("EPC: " + tags.get(x).epcString() + " Count: " + counts.get(x));
            }
        }
    }
      
}
