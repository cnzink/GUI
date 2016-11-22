/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.guiport;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.guiApp.GUICmdProcessor;
import mesh.util.Converter;

/**
 *
 * This TcpRecvPort keeps 1 socket connection open the entire time it is running.
 * 
 * protocol: first 2 bytes contains the size of the incoming message
 * 
 * @author Tom
 */
public class GUITcpRecvPort implements Runnable{
    private final static Logger LOGGER = Logger.getLogger(GUITcpRecvPort.class.getName());
    private boolean stop = false;
    private boolean hasStopped = false;
    private Socket mySocket;
    
    private GUICmdProcessor cmdProcessor;
    
    /**
     * Constructor
     * @param s Socket connection with the ManagerCore
     * @param npduProcessor processes commands received from ManagerCore
     * @throws IOException 
     */
    public GUITcpRecvPort(Socket s, GUICmdProcessor npduProcessor) throws IOException
    {
        this.mySocket = s;
        this.cmdProcessor = npduProcessor;
    }
    
    public void run()
    {
        try
        {
            InputStream in = mySocket.getInputStream();
            byte[] sizeBytes = new byte[2]; //size of incoming message
            int size;
            int size2;
            byte[] msg = null;
            while (!stop)
            {
                size = in.read(sizeBytes); //here size is temp, read in 2 bytes containing size of incoming message
                if (size <= 0) //skip 0 and -1
                    continue;
                //calculate actual size
                size = (int) ((sizeBytes[0] & 0xFF) | ((sizeBytes[1] & 0xFF) << 8));
                msg = new byte[size];
                size2 = in.read(msg);
                if (size2 < size) //keep reading in all bytes of the message
                {
                    byte[] remaining;
                    int count = size2; //count is the number of bytes we have successfully read in
                    while (count < size)
                    {
                        remaining = new byte[size - count];
                        size2 = in.read(remaining);
                        System.arraycopy(remaining, 0, msg, count, size2); //copy remaining array into msg array at correct location
                        count = count + size2;
                    }
                }
                cmdProcessor.addMessage(msg);
            }
        }
        catch (Exception e)
        {
            //socket closed when trying to read from input stream.
            //occurs when sender disconnects. this.stop is set to true, but
            //we are still in the middle of the while loop.
            //LOGGER.log(Level.INFO, "Exception with socket. This is normal if the socket connection is closed", e);
            //LOGGER.log(Level.INFO, "GUI socket connection closed.");
        }
        finally
        {
            hasStopped = true;
        }
    }

        
    /**
     *  Stop this listener
     */
    public void stop()
    {
        this.stop = true;
    }
    
    
    /**
     * 
     * @return hasStopped
     */
    public boolean hasStopped()
   {
       return hasStopped;
   }
       
    
}
