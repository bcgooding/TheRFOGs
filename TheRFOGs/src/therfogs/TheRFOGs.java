/**
 * @author Benjamin C. Gooding
 * @version 1.0
 * Student ID: 010529574
 * Date: 12/07/2014
 *
 * Purpose: API for a ThingMagic RFID Reader.
 */
package therfogs;

import jssc.*;
import com.thingmagic.*;
import java.util.*;
import java.util.logging.Level;

public class TheRFOGs {
    
     Reader r = null;
     ReadListener l = new PrintListener();
   

/**
 * Uses the JSSC framework to get a list of connected serial port devices. 
 *  
 * @return      Array of Strings containing the list of portnames
 */
    public  String[] scanForReader() 
    {
        //will be used to tell us if a reader is plugged in.
        boolean detected = false;
        //holds the names of all the connected ports
        String [] portNames;
            
        //System.out.println("Looking for connected devices...");
            
        //loop through until a device is connected
        //do
        //{
            //gets the name of all the connected serial ports
            portNames = SerialPortList.getPortNames();

        //    if(portNames.length != 0)
        //        detected = true;
        //}while(detected == false);
    
        //System.out.println("Connected Devices Found");
        return portNames;
    }

/**
 * 
 * Calls setBaudRate to set the rate based on user selection
 * Creates a reader, connects and then sets the region
 * 
 * @param portName the name of the port the user will be connecting to
 * @param baudRate the rate the baud will be set to. Based on the input from the
 *                  user selection. int
 * @return      void
 */
    public  void connectReader(String portName, int baudRate) throws Exception 
    {
        try {
            //need to create the reader
            r = Reader.create("tmr:///" + portName);
            
            //calls functionality to set the baud rate
            setBaudRate(baudRate);
            //System.out.println("Attempting to connect to Reader");
            r.connect();
            //System.out.println("Connected to reader");
            setRegion();
        } catch (ReaderException ex) {
            disconnectReader();
            java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    /**
 * @deprecated 
 * lists the ports found with JSSC
 * Get user port selection
 * 
 * @param portNames Array of strings containing the list of detected ports
 * @return      void
 */
    private  int selectPortNumber(String[] portNames) {
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

/**
 * 
 * Sets the baud rate based on the user's input
 * 
 * @param baud the baud rate pulled by the user interface
 * @return      void
 */
    private  void setBaudRate(int baud) {
        
        /*Scanner inputReader = new Scanner(System.in);
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
        */
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

 /**
 * 
 * Sets the region of the reader to the default region
 *
 * @return      void
 */
    private  void setRegion() throws Exception 
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

/**
 * 
 * Safely disconnects the reader

 */
    public  void disconnectReader() 
    {
        r.destroy();
    }

 /**
 * 
 * Reads the tags for a short period of time (1 sec) and returns an array of 
 * read tags.
 * 
 * @return      An array of TagReadData. Each element of the TagReadData can be
 *              accessed by creating a TagData and setting it equal to the element
 *              at the requested index.
 */
    public  TagReadData[] readTags() {
        TagReadData[] tags = null;
        try { 
            tags = r.read(1000);
            /*for(int x = 0; x < tags.length; x++)
            {
                TagData tag = tags[x].getTag();
                System.out.println("Tag " + (x+1) + " EPC: " + tag.epcString());
            }*/
        } catch (ReaderException ex) {
            disconnectReader();
            java.util.logging.Logger.getLogger(TheRFOGs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tags;
    }
  
  /**
 * 
 * Creates a thread to asynchronously read tags
 * 
 */
  public  void startAsyncRead()
  {
        
        r.addReadListener(l);
        r.startReading();

  }
  
   /**
 * 
 * Stops the asynchronously read thread.
 * 
 */
  public  void stopAsyncRead()
  {
        r.stopReading();
        r.removeReadListener(l);
  }

 
  /**
 * 
 * Overwrites the EPC of the Selected Tag
 * 
 * @param selectedTag this is a tagData element containing the selected tag's info
 * @param newEPC      a string containing the new EPC. Must be 24 characters long
 */
    public  void writeEPC(TagData selectedTag, String newEPC) throws ReaderException {
        
            TagData t = new TagData(newEPC);
            TagFilter target;
            //TagData selectedTag = new TagData(selectedEPC);
            target = selectedTag;

            //NEED TO FIX THIS TO TARGET THE CORRECT TAG
            if(newEPC.length() == 24)
            {
                r.writeTag(target, t);
            }
        
        
    }
      
 /**
 * 
 * Takes in a selectedTag, password, and an array of actions to perform. Based
 * on the actions different elements of the tag will be unlocked.
 * 
 * @param selectedTag this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @param actions   an array of strings containing commands to perform.
 *                  Actions can be ("KILL","ACCESS","EPC","TID","USER").
 *                  Actions are case sensitive
 * @return returns an array of strings containing the success or failure of each
 *          unlock attempt.
 */
    public  String[] unlockTag(TagData selectedTag, int pwd, String[] actions)
    {
        
       // TagData selectedTag = new TagData(selectedEPC);
        String []message = new String[actions.length];
        
        for(int x = 0; x < actions.length; x++)
        {
            if(actions[x].compareTo("KILL") == 0)
            {
                message[x] = unlockTagKill(selectedTag,pwd);
            }
            else if(actions[x].compareTo("ACCESS")==0)
            {
                 message[x] = unlockTagAccess(selectedTag,pwd);
            }
            else if(actions[x].compareTo("EPC")==0)
            {
                 message[x] = unlockTagEPC(selectedTag,pwd);
            }
            else if(actions[x].compareTo("TID")==0)
            {
                 message[x] = unlockTagTID(selectedTag,pwd);
            }
            else if(actions[x].compareTo("USER")==0)
            {
                 message[x] = unlockTagUser(selectedTag,pwd);
            }
        }
        
        if(actions.length == 0)
        {
            String []error = new String[1];
            error[1] = "No Optiones were selected";
            return error;
        }
        else
        {
            return message;
        }
    }

    /**
 * 
 * Unlocks the kill password of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String unlockTagKill(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.KILL_UNLOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully unlocked the KILL Password";
        }
        catch(Exception e)
        {
            return "Failed to unlock the KILL Password";     
        }
    }   
    
        /**
 * 
 * Unlocks the access password of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String unlockTagAccess(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.ACCESS_UNLOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully unlocked the ACCESS Password";
        }
        catch(Exception e)
        {
            return "Failed to unlock the ACCESS Password";     
        }
    }   
    
        /**
 * 
 * Unlocks the EPC bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String unlockTagEPC(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.EPC_UNLOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully unlocked the EPC";
        }
        catch(Exception e)
        {
            return "Failed to unlock the EPC";     
        }
    }
    
        /**
 * 
 * Unlocks the TID bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String unlockTagTID(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.TID_UNLOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully unlocked the TID";
        }
        catch(Exception e)
        {
            return "Failed to unlock the TID";     
        }
    }
    
        /**
 * 
 * Unlocks the User bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String unlockTagUser(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.USER_UNLOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully unlocked the USER";
        }
        catch(Exception e)
        {
            return "Failed to unlock the USER";     
        }
    }
  
     /**
 * 
 * Takes in a selectedTag, password, and an array of actions to perform. Based
 * on the actions different elements of the tag will be locked.
 * 
 * @param selectedTag this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @param actions   an array of strings containing commands to perform.
 *                  Actions can be ("KILL","ACCESS","EPC","TID","USER").
 *                  Actions are case sensitive
 * @return  returns an array of strings containing the success or failure of
 *          each lock attempt
 */
    public  String[] lockTag(TagData selectedTag, int pwd, String[] actions)
    {
        
        //TagData selectedTag = new TagData(selectedEPC);
        String []message = new String[actions.length];
        
        for(int x = 0; x < actions.length; x++)
        {
            if(actions[x].compareTo("KILL") == 0)
            {
                message[x] = lockTagKill(selectedTag,pwd);
            }
            else if(actions[x].compareTo("ACCESS")==0)
            {
                 message[x] = lockTagAccess(selectedTag,pwd);
            }
            else if(actions[x].compareTo("EPC")==0)
            {
                 message[x] = lockTagEPC(selectedTag,pwd);
            }
            else if(actions[x].compareTo("TID")==0)
            {
                 message[x] = lockTagTID(selectedTag,pwd);
            }
            else if(actions[x].compareTo("USER")==0)
            {
                 message[x] = lockTagUser(selectedTag,pwd);
            }
        }
        
        if(actions.length == 0)
        {
            String []error = new String[1];
            error[1] = "No Optiones were selected";
            return error;
        }
        else
        {
            return message;
        }
    }

        /**
 * 
 * Locks the kill password of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String lockTagKill(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.KILL_LOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully locked the KILL Password";
        }
        catch(Exception e)
        {
            return "Failed to lock the KILL Password";     
        }
    }   
    
       /**
 * 
 * Locks the access password of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String lockTagAccess(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.ACCESS_LOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully locked the ACCESS Password";
        }
        catch(Exception e)
        {
            return "Failed to lock the ACCESS Password";     
        }
    }   
    
       /**
 * 
 * Locks the EPC bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String lockTagEPC(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.EPC_LOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully locked the EPC";
        }
        catch(Exception e)
        {
            return "Failed to lock the EPC";     
        }
    }
    
       /**
 * 
 * Locks the TID bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String lockTagTID(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.TID_LOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully locked the TID";
        }
        catch(Exception e)
        {
            return "Failed to lock the TID";     
        }
    }
    
       /**
 * 
 * Locks the User bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @param pwd     an 8 character long int based access password
 * @return returns a string containing the success or failure of the unlock.
 */
    private  String lockTagUser(TagData target, int pwd) {
        TagLockAction la = new Gen2.LockAction(Gen2.LockAction.USER_LOCK);
        //la = (TagLockAction)parseValue(args[0]);
        Gen2.Lock lock0 = new Gen2.Lock(pwd,(Gen2.LockAction) la);
        try{
            r.executeTagOp(lock0, target);
            return "Successfully locked the USER";
        }
        catch(Exception e)
        {
            return "Failed to lock the USER";     
        }
    }
  
       /**
 * 
 * Inspects the memory bank of the RFID Tag. Inspects the EPC bank, TID bank,
 * Reserved Memory bank (access/kill password), and the user bank. Returns 
 * the RFID info.
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns an array of strings containing the inspected tags information.
 *          Info is stored in the following elements of the array:
 *              0: CRC
 *              1: PC
 *              2: EPC
 *              3: Access Password
 *              4: Kill Password
 *              5: ClsID
 *              6: VendorID
 *              7: ModelID
 *              8: UniqueID
 *              9: User Memory
 */
    public  String[] inspectTag(TagData target)
    {
        String []inspection = new String[10];
        
        String []epcMemBank = readEPCMem(target);
        inspection[0] = epcMemBank[0]; //CRC
        inspection[1] = epcMemBank[1]; //PC
        inspection[2] = epcMemBank[2]; //EPC
        
        inspection[3] = readAccessMem(target);
        inspection[4] = readKillMem(target);
        
        String []tidMemBank = readTIDMem(target);
        inspection[5] = tidMemBank[0]; //ClsID
        inspection[6] = tidMemBank[1]; //Vendor ID
        inspection[7] = tidMemBank[2]; //Model ID
        inspection[8] = tidMemBank[3]; //Unique ID
        
        inspection[9] = readUserMem(target);
        
        return inspection;
    }
    
/**
 * 
 * Reads the EPC memory bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns an array of strings containing the EPC memory bank info
 *          0: CRC
 *          1: PC
 *          2: EPC
 */
    private  String[] readEPCMem(TagData target)
    {
        String[] message = new String[3];
        try{
            //reading the CRC
            short []crc = r.readTagMemWords(target, 01, 0, 1);
            message[0] = "";
            for (int i = 0; i < crc.length; i++)
            {
                
                message[0] += String.format("%04x", crc[i]);
            }
            
            
            //reading the PC
            short []pc = r.readTagMemWords(target, 01, 1, 1);
            message[1] = "";
            for (int i = 0; i < pc.length; i++)
            {

                message[1] += String.format("%04x", pc[i]);
            }
            //reading the epc
            short []epc = r.readTagMemWords(target, 01, 2, 6);
            message[2] = "";
            for (int i = 0; i < epc.length; i++)
            {
                
                message[2] += String.format("%04x", epc[i]);
            }
        }
        catch(Exception e)
        {
            for(int x = 0; x < 3; x++)
            {
                message[x] = "Read Error";
            }
        }
        return message;
    }
    
    /**
 * 
 * Reads the Reserved memory bank of the selected tag, specifically the Access
 * Password portion.
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns a string containing the access password in HEX format.
 */
    private  String readAccessMem(TagData target)
    {

        String message = "";
        try{
            short []accessPassword = r.readTagMemWords(target, 0, 2, 2);
         
            for (int i = 0; i < accessPassword.length; i++)
            {
                message += String.format("%04x", accessPassword[i]);
            }
        }
        catch(Exception e)
        {
            message = "Read Error";
        }
        return message;
    }
    
    /**
 * 
 * Reads the Reserved memory bank of the selected tag, specifically the kill
 * password portion.
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns a string containing the Kill password in HEX format.
 */
    private  String readKillMem(TagData target)
    {
        String message = "";
        try{
            short []killPassword = r.readTagMemWords(target, 0, 0, 2);
        
            for (int i = 0; i < killPassword.length; i++)
            {
                message += String.format("%04x", killPassword[i]);
            }
        }
        catch(Exception e)
        {
            message = "Could not read User Memory";
        }
        return message;
    }
    
    /**
 * 
 * Reads the TID memory bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns an array of strings containing the TID memory bank info
 *          0: ClsID
 *          1: VendorID
 *          2: ModelID
 *          3: UniqueID
 */
    private  String[] readTIDMem(TagData target)
    {

        String []message = new String[4];
        try{
            short []other = r.readTagMemWords(target, 2, 0, 2);
            message[0] = "";
            message[1] ="";
            message[2] ="";
            message[3] = "";
            System.out.println("Other Length: " + other.length);
            for (int i = 0; i < other.length; i++)
            {
                String tidString = String.format("%04x", other[i]);
                if(i<1){
                
                    System.out.println("Reach Check 1");
                    message[0] = tidString.substring(0, 2);
                    System.out.println("Reached Check 2");
                    message[1] = tidString.substring(2);
                }
                else{
                    System.out.println("Reached Check 3");
                    message[1] += tidString.substring(0, 1);
                    System.out.println("Reached Check 4");
                    message[2] = tidString.substring(1);
                    System.out.println("Reached Check 5");
                }

            }
            
            /*short []tid = r.readTagMemWords(target, 2, 2, 1);
            System.out.println("Reached Check 6");
            for (int i = 0; i < tid.length; i++)
            {
                System.out.println("Reached Check 7");
                //System.out.printf("%04x", tid[i]);
                message[3] += String.format("%04x", tid[i]);
            }*/
        }
        catch(Exception e)
        {
            for(int x = 0; x < 4; x++)
            {
                message[x] = "Read Error";
            }
        }
        return message;
    }
    
    /**
 * 
 * Reads the User memory bank of the selected tag
 * 
 * @param target this is a tagData element containing the selected tag's info
 * @return returns a string containing the information contained in the user
 *          memory bank
 */
    private  String readUserMem(TagData target)
    {
        String message = "";
        try{
            
            short []user = r.readTagMemWords(target, 3, 0, 1);
        
            for (int i = 0; i < user.length; i++)
            {
                message += String.format("%04x", user[i]);
            }
        }
        catch(Exception e)
        {
            message = "Read Error";
        }
        return message;
    }
    
    /**
 * 
 * Implements the abstract ReadListener class. Allows for the use of async tag
 * reads.
 *
 */
     class PrintListener implements ReadListener
    {
        Vector<TagReadData> tags = new Vector<TagReadData>();
        Vector<Integer> counts = new Vector<Integer>();
        
 /**
 * 
 * Used to call the function to fill in the tags and counts vector
 * 
 * @param reader this is the reader being used to read the tags.
 * @param tr    this is the information read from the tags.
 */
        public void tagRead(Reader r, TagReadData tr)
        {
            //String epc = tr.epcString();
            listTags(tr);
            //System.out.println("Background read: " + tr.toString());
        }
        
 /**
 * 
 * Fills the tags and counts vectors based on the tag passed
 * 
 * @param tr this is the TagReadData of the current async read
 */
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
            
            /*
            for(int x = 0; x < tags.size(); x++)
            {
                System.out.println("EPC: " + tags.get(x).epcString() + " Count: " + counts.get(x));
            }*/
        }
    }
      
}
