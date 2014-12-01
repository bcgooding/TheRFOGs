/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package therfogs;

import javax.comm.*;
import java.util.*;

/**
 *
 * @author BCG
 */
public class TheRFOGs {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        testSerial();
    }
    
    protected static void testSerial()
    {
        String wantedPortName = "/dev/ttya";
        Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
     
        //System.out.println(portIdentifiers.toString());
        CommPortIdentifier portId = null;  // will be set if port found

        while (portIdentifiers.hasMoreElements())
        {
            System.out.println("Has an element");
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
            //if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL &&
             //   pid.getName().equals(wantedPortName)) 
            if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL) 
            {
                //System.out.println(pid.getCurrentOwner());
                System.out.println(pid.getName());
                portId = pid;
                break;
            }
        }

        if(portId == null)
        {
            System.err.println("Could not find serial port " + wantedPortName);
            System.exit(1);
        }
    }
}
