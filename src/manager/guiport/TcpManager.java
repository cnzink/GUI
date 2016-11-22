/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.guiport;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.guiApp.GUICmdProcessor;
import mesh.cmd.Cmd64046;
import mesh.cmd.CmdManipulation;
import mesh.cmd.GuiCmdInfo;

/**
 *
 * @author Tom
 * 
 * The Tcp Manager creates a single tcp connection to the ManagerCore.
 * This socket does all the send and recv between this GUI and the Core.
 */
public class TcpManager
{
    private final static Logger LOGGER = Logger.getLogger(TcpManager.class.getName());
    private boolean isConnected = false;
    private Socket mySocket; //connection to ManagerCore
    private GUITcpRecvPort guiListener;
    private GUICmdProcessor cmdProcessor;
    private PrintStream out; //output stream that belongs to the socket

    /**
     *  Constructor
     * @param npduProcessor processes commands received from ManagerCore
     */
    public TcpManager(GUICmdProcessor npduProcessor)
    {
        this.mySocket = null;
        this.cmdProcessor = npduProcessor;
    }

    /**
     * Connect to the ManagerCore. 
     * After successfully connecting, setup a thread to listen to for incoming
     * messages from ManagerCore.
     * 
     * @param IPAddress 
     * @param port 
     * @return true if successfully connected, otherwise false
     */
    public boolean connect(String IPAddress, int port)
    {
        // Send a "connect" message to ManagerCore
        Cmd64046 cmd64046 = new Cmd64046(true); //request the ManagerCore to send graph info to ManagerGui
        cmd64046.setProgressFlag((byte) 1); // Indicates GUI wants to initiate a connection to ManagerCore
        
        ArrayList<GuiCmdInfo> cmdInfo = new ArrayList<GuiCmdInfo>();
        cmdInfo.add(cmd64046);
        byte[] connectInfo = CmdManipulation.GuiCmdListToByteArray(cmdInfo);
        
        //Connect to NM
        try
        {
            mySocket = new Socket();
            mySocket.connect(new InetSocketAddress(IPAddress, port), 5000); //attempt to connect to NM, with a specific timeout
            out = new PrintStream(mySocket.getOutputStream(), true);
            sendToManagerCore(connectInfo);
        }
        catch (Exception e)
        {
            return false;
        }
       
        //Setup and start TCP recv port
        try
        {
            this.guiListener = new GUITcpRecvPort(mySocket, this.cmdProcessor);
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Unable to start TCP recv port");
            return false;
        }
        Thread guiListenerThread = new Thread(guiListener);
        guiListenerThread.start();
        
        isConnected = true;
        return true;
    }
    
    /**
     * Closes the TCP socket connection with ManagerCore.
     * Called by DisconnectListener in MeshSimulator
     */
    public void disconnect()
    {
        if (isConnected == false)
        {
            return; // GUI is not connected, no need to disconnect
        }
        
        // Send a "disconnect" message to ManagerCore
        Cmd64046 cmd64046 = new Cmd64046(true);
        cmd64046.setProgressFlag((byte) 4); // Indicates GUI wants to disconnect from ManagerCore
        ArrayList<GuiCmdInfo> cmdInfo = new ArrayList<GuiCmdInfo>();
        cmdInfo.add(cmd64046);
        byte[] disconnectMsg = CmdManipulation.GuiCmdListToByteArray(cmdInfo);
        sendToManagerCore(disconnectMsg);
        isConnected = false;
        
        // Close the TCP socket
        guiListener.stop(); //stop Tcp recv port thread
        try
        {
            out.close();
            mySocket.close(); //close the socket
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Error closing TCP port", ex);
            // Logger.getLogger(TcpManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send bytes over the current socket connection
     * Only supports messages up to 32767 bytes in length
     * (-1 is reserved value from InputStream.read() )
     *  Protocol:
     * First send the length of the message (2 bytes)
     * Next send the actual message.
     * @param npduBytes 
     */
    public void sendToManagerCore(byte[] npduBytes)
    {
        try
        {
            if(npduBytes.length > 32767)
                throw new Exception("Trying to send " + npduBytes.length +", cannot send more than 32,767");
            byte[] lengthBytes = new byte[2];
            lengthBytes[0] = (byte)npduBytes.length; //lower byte
            lengthBytes[1] = (byte)(npduBytes.length >> 8); //upper byte
            out.write(lengthBytes);
            out.write(npduBytes);
            out.flush();
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.SEVERE, "Error sending bytes", ex);
        }
    }
} //end class TcpManager
