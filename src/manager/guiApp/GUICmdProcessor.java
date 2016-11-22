/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.guiApp;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import manager.AdditionalDevice;
import manager.ManagerConfig;
import manager.MeshSimulator;
import manager.gui.DevLocation;
import mesh.*;
import mesh.util.Converter;
import mesh.cmd.*;

/**
 * This class processes command sent from ManagerCore to ManagerGui
 * @author Hang Yu
 */
public class GUICmdProcessor implements Runnable{

    private ConcurrentLinkedQueue<byte[]> incomingBytes = new ConcurrentLinkedQueue<byte[]>();
    private final static Logger LOGGER = Logger.getLogger(GUICmdProcessor.class.getName());
    // repInfo is used to receive responsed information received from network manager
    private Hashtable<Character, GuiCmdInfo> rspInfo = new Hashtable<Character, GuiCmdInfo>();

    boolean isActive;

    GUICmdSender cmdSender;
    GUITopology topology;
    NetworkVisualizer visualizer;
    JFrame mainframe;
    MeshSimulator meshSimulator; // reference to the main GUI

    JTextArea sendTextArea;
    JTextArea recvTextArea;

    Vector<byte[]> sentMsgVector;
    Vector<byte[]> recvMsgVector;
    Vector<byte[]> allMsgVector;

    char sequenceNumber = 0x0000;

    boolean receivedCmd64013Rsp = false;

    public Vector<byte[]> getSendMsgVector()
    {
        return this.sentMsgVector;
    }

    public Vector<byte[]> getRecvMsgVector()
    {
        return this.recvMsgVector;
    }

    public Vector<byte[]> getAllMsgVector()
    {
        return this.allMsgVector;
    }

    public GUICmdProcessor()
    {
    }

    public GUICmdProcessor(MeshSimulator ms, JFrame mainframe, JTextArea sendBoard, JTextArea recvBoard, GUITopology topo, NetworkVisualizer vis)
    {
        this.meshSimulator = ms;
        this.isActive = true;
        this.topology = topo;
        this.visualizer = vis;
        this.mainframe = mainframe;
        this.sendTextArea = sendBoard;
        this.recvTextArea = recvBoard;
    }

    public void setCmdSender(GUICmdSender sender)
    {
        this.cmdSender = sender;
    }

    public ConcurrentLinkedQueue<byte[]> getIncomingQueue()
    {
        return this.incomingBytes;
    }

    public void addMessage(byte[]data)
    {
        incomingBytes.add(data);
    }

    public char getSeqNumber()
    {
        return this.sequenceNumber;
    }

    public void increaseSeqNumber()
    {
        sequenceNumber++;
    }


    public void stopCmdProcessor()
    {
        this.isActive = false;
    }

    public GUICmdSender getCmdSender()
    {
        return this.cmdSender;
    }

    public void setSendTextArea(JTextArea area)
    {
        this.sendTextArea = area;
    }

    public void setRecvTextArea(JTextArea area)
    {
        this.recvTextArea = area;
    }


    public void DisplayInSendMsgBoard(String s)
    {
        sendTextArea.append(s + "\n");
    }

    public void DisplayInRecvMsgBoard(String s)
    {
        recvTextArea.append(s + "\n");
    }

    /*
     * send data to the GUI TCP parts
     */
    /*
    public void sendCommand(byte[]ba) throws IOException
    {
         // append the message in the sendMsgTextArea
        for(int i = 0; i < ba.length; i ++)
        {
            sendTextArea.append(Converter.ByteToHexString(ba[i]));
            sendTextArea.append(" ");
        }
        sendTextArea.append("\n");

        GUITcpSendPort.sendToNM(ba);
    }
    */


    public void run()
    {
        while(isActive)
        {
            try
            {
                Thread.yield();
                Thread.sleep(5);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            byte[]nextMessage = incomingBytes.poll();
            if(nextMessage==null)
                continue;
            else
            {
                try
                {
                    Handler(nextMessage);
                }
                catch(Exception ex)
                {
                    Logger.getLogger(GUICmdProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }



    public void stop()
    {
        this.isActive = false;
    }


    void Handler(byte[]msg) throws IOException, Exception
    {
        GUICmdParser parser = new GUICmdParser();

        parser.addCmd(msg);
        Vector<GuiCmdInfo> cmdList = parser.getCmds();
        Vector<GuiCmdInfo> rspCmdList = new Vector<GuiCmdInfo>();
        GuiCmdInfo rspCmd = new GuiCmdInfo();
            for(int i = 0;i<cmdList.size();i++)
            {
                rspCmd = CmdProcessing(cmdList.get(i));
                if(!rspCmd.IsReq())
                rspCmdList.add(rspCmd);
            }
            if(rspCmdList.size()>0)
            cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(rspCmdList));
    }


    public GuiCmdInfo getRspInfo(char cmdId)
    {
        int waitPeriod = 0;
        while(true)
        {

            if(rspInfo.containsKey(cmdId))
                return rspInfo.get(cmdId);
            try
            {
                Thread.sleep(200);
                waitPeriod += 200;
                if(waitPeriod == 5000) // wait for 3 seconds and get no response
                    break;
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
        return null;
    }

    public GuiCmdInfo CmdProcessing(GuiCmdInfo cmd) throws InterruptedException
    {
        GuiCmdInfo rspCmd = new GuiCmdInfo();
        char cmdId = cmd.GetCmdID();

        switch(cmdId)
        {
            // cmd 64001: test network layer
            case 0xFA01:
                rspInfo.put(cmdId, cmd);
                break;

            case 0xFA06: //64006 get networkID from ManagerCore
                Cmd64006 cmd64006 = (Cmd64006)cmd;
                String networkID = cmd64006.getNetworkID();
                ManagerConfig.setNetworkID(networkID);
                break;

            case 0xFA07: //64007 get channel map from ManagerCore
                Cmd64007 cmd64007 = (Cmd64007)cmd;
                String channelMap = cmd64007.getChannelMap();
                ManagerConfig.SetChannelMap(channelMap);
                break;

            case 0xFA08: // 64008 set join key
                Cmd64008 cmd64008 = (Cmd64008) cmd;
                ManagerConfig.setJoinKey(cmd64008.getJoinKey());
                // do not send any rsp cmd back
                
                break;

            case 0xFA0C:
                rspInfo.put(cmdId, cmd);
                break;

            case 0xFA0F:
                Cmd64015 cmd64015 = (Cmd64015)cmd;
                String sendMsg = cmd64015.getSendMsg();
                sendTextArea.append(sendMsg);
                break;

            case 0xFA10:
                Cmd64016 cmd64016 = (Cmd64016)cmd;
                String recvMsg = cmd64016.getRecvMsg();
                recvTextArea.append(recvMsg);
                break;
            // 64017 mainframe repaint
            case 0xFA11:
                // This is not needed for GUI to update
                //mainframe.repaint();
                break;
                // 64018: get manager about box version information
            case 0xFA12:
                Cmd64018 cmd64018 = (Cmd64018) cmd;
                String version = cmd64018.getVersion();
                ManagerAboutBox.SetManagerCoreVersion(version);
                break;

             // 64019 show gateway version
            case 0xFA13:
                Cmd64019 cmd64019 = (Cmd64019) cmd;
                ManagerAboutBox.SetGatewayVersion(cmd64019.getVersion());
                break;
            // 64020 show AP version
            case 0xFA14:
                Cmd64020 cmd64020 = (Cmd64020) cmd;
                ManagerAboutBox.SetApVersion(cmd64020.getMajorVersion(), cmd64020.getMinorVersion(), cmd64020.getSvnVersion());
                break;
            // 64021 update visualizer
            case 0xFA15:
                // NOTE: This is not needed. ManagerCore has disabled this functionality
                Cmd64021 cmd64021 = (Cmd64021)cmd;
                char c_nickName = cmd64021.getNickName();
                char[]c_uid = cmd64021.getUid();
                char c_nodeType = cmd64021.getNodeType();
                NetworkNode toAdd = new NetworkNode();
                NodeNickname nickName = new NodeNickname(c_nickName);
                toAdd.setNodeNickname(nickName);
                NodeUniqueID uid = new NodeUniqueID(c_uid);
                toAdd.setNodeUniqueID(uid);
                if(c_nodeType==1)
                    toAdd.setType(NodeType.AP);
                else if(c_nodeType==2)
                    toAdd.setType(NodeType.DEVICE);
                else if(c_nodeType==5)
                    toAdd.setType(NodeType.GATEWAY);

                DevLocation devLoc = new DevLocation();
                Point2D point = devLoc.GetDevLocation(uid);
                if(point != null)
                {
                    point.toString();
                    visualizer.getLayout().setLocation(toAdd, point);
                }
            break;
            // 64022: update node scan period
            case 0xFA16:
                Cmd64022 cmd64022 = (Cmd64022)cmd;
                NetworkNode updateNode = this.topology.GetNetworkNode(cmd64022.getNode());
                if(updateNode!=null)
                updateNode.setScanPeriod(Converter.HexIntToScanPeriod(cmd64022.getPeriod()));
           break;

           // 64023: remove edge
            case 0xFA17:
                Cmd64023 cmd64023 = (Cmd64023)cmd;
                NetworkNode src = topology.GetNetworkNode(cmd64023.getSrc());
                NetworkNode dst = topology.GetNetworkNode(cmd64023.getDes());
                NetworkEdge edge = topology.GetGraph().findEdge(src, dst);
                if(edge != null)
                    topology.GetGraph().removeEdge(edge);
                break;

            case 0xFA18:  // 64024: add vertex
                Cmd64024 cmd64024 = (Cmd64024) cmd;
                NodeNickname nickName_64024 = new NodeNickname(cmd64024.getNickName());
                NodeUniqueID uid_64024 = new NodeUniqueID(cmd64024.getUid());
                NetworkNode toAdd_64024 = new NetworkNode(true, true, uid_64024, nickName_64024, (int) cmd64024.getID(), NodeType.ABSTRACT, new ScanPeriod(ScanPeriod._0_ms));

                if (cmd64024.getNodeType() == 1)
                    toAdd_64024.setType(NodeType.AP);
                else
                    if (cmd64024.getNodeType() == 2)
                        toAdd_64024.setType(NodeType.DEVICE);
                    else
                        if (cmd64024.getNodeType() == 5)
                            toAdd_64024.setType(NodeType.GATEWAY);

                if (cmd64024.isUpdate() == false)
                {
                    //Add a new Vertex
                    this.topology.GetGraph().addVertex(toAdd_64024);
                    this.topology.GetUpLinkGraph().addVertex(toAdd_64024);
                    // this.topology.GetBCastGraph().addVertex(toAdd_64024);
                    this.topology.GetDownLinkGraph().addVertex(toAdd_64024);

                    if (cmd64024.getNodeType() == 5) //set fixed location of GW
                    {
                        double xPos = 150.0 + Math.random() * 150.0;
                        double yPos = 50.0 + Math.random() * 100.0;
                        visualizer.getLayout().setLocation(toAdd_64024, new Point2D.Double(xPos, yPos));
                    }
                   if (cmd64024.getNodeType() == 1) //set fixed location of AP
                       {
                        double xPos = 150.0 + Math.random() * 150.0;
                        double yPos = 100.0 + Math.random() * 100.0;
                        visualizer.getLayout().setLocation(toAdd_64024, new Point2D.Double(xPos, yPos));
                    }
                }
                else
                {
                    //Update Vertex
                    this.topology.updateVertex(toAdd_64024);
                }
                break;

            case 0xFA19: // 64025 construct graph
                // Graphs are created in topology's constructor. This command is no longer needed
                /*
                topology.SetBCastGraph(new DirectedSparseMultigraph<NetworkNode, NetworkEdge>());
                topology.SetUpLinkGraph(new DirectedSparseMultigraph<NetworkNode, NetworkEdge>());
                topology.SetDownLinkGraph(new DirectedSparseMultigraph<NetworkNode, NetworkEdge>());
                */
                break;

            // 64026 add item to device white list
            case 0xFA1A:
                Cmd64026 cmd64026 = (Cmd64026) cmd;
                NodeUniqueID uid_64026 = new NodeUniqueID(cmd64026.getUid());
                this.topology.AddItemToDeviceWhiteList(uid_64026);
                break;

            case 0xFA1C: // 64028 add edge
                Cmd64028 cmd64028 = (Cmd64028) cmd;
                NetworkEdge edge_64028 = new NetworkEdge();
                edge_64028.setSignalStrength(cmd64028.getSignalStrength());
                NetworkNode src_64028 = this.topology.GetNetworkNode(cmd64028.getSrcNickName());
                NetworkNode dst_64028 = this.topology.GetNetworkNode(cmd64028.getDstNickName());
                char graphType_64028 = cmd64028.getGraphType();
                char edgeType_64028 = cmd64028.getEdgeType();

                if (cmd64028.isUpdate() == false) //check if this is an "Add" or "Update"
                {
                    //Add edge
                    if (graphType_64028 == 0)
                        this.topology.GetGraph().addEdge(edge_64028, src_64028, dst_64028, EdgeType.DIRECTED);
                    else if (graphType_64028 == 1)
                        this.topology.GetUpLinkGraph().addEdge(edge_64028, src_64028, dst_64028, EdgeType.DIRECTED);
                    else if (graphType_64028 == 2)
                        this.topology.GetBCastGraph().addEdge(edge_64028, src_64028, dst_64028, EdgeType.DIRECTED);
                    else if (graphType_64028 == 3)
                        this.topology.GetDownLinkGraph().addEdge(edge_64028, src_64028, dst_64028, EdgeType.DIRECTED);
                }
                else
                {
                    //Update edge
                    this.topology.updateEdge(graphType_64028, edge_64028, src_64028, dst_64028);
                }
                break;

            case 0xFA2D: // cmd64045 add edge to all
                Cmd64045 cmd64045 = (Cmd64045) cmd;
                NetworkEdge edge_64045 = new NetworkEdge();
                edge_64045.setSignalStrength(cmd64045.getSignalStrength());
                NetworkNode src_64045 = this.topology.GetNetworkNode(cmd64045.getSrcNickName()); //the src and dst is assumed not to be null b/c the user will first add the vertex before adding an edge
                NetworkNode dst_64045 = this.topology.GetNetworkNode(cmd64045.getDstNickName());
                char edgeType_64045 = cmd64045.getEdgeType();
                char direction = cmd64045.getDirection();
                this.topology.GetGraph().addEdge(edge_64045, src_64045, dst_64045, EdgeType.DIRECTED);
                if (direction == 0)
                    this.topology.GetUpLinkGraph().addEdge(edge_64045, src_64045, dst_64045, EdgeType.DIRECTED);
                if (direction == 1)
                    this.topology.GetDownLinkGraph().addEdge(edge_64045, src_64045, dst_64045, EdgeType.DIRECTED);
                break;

                // 64029 add edge from existing edge
            case 0xFA1D:
                Cmd64029 cmd64029 = (Cmd64029)cmd;
                NetworkNode src_64029 = this.topology.GetNetworkNode(cmd64029.getSrcNickName());
                NetworkNode dst_64029 = this.topology.GetNetworkNode(cmd64029.getDstNickName());
                NetworkEdge edge_64029 = this.topology.GetGraph().findEdge(src_64029, dst_64029);

                char graphType_64029 = cmd64029.getGraphType();
                char edgeType_64029 = cmd64029.getEdgeType();
                if(graphType_64029==0)
                    this.topology.GetGraph().addEdge(edge_64029, src_64029, dst_64029, EdgeType.DIRECTED); //TOM bug? isn't this case just adding the edge back to the same graph? the graph will throw an exception if this occurs..
                else if(graphType_64029==1)
                    this.topology.GetUpLinkGraph().addEdge(edge_64029, src_64029, dst_64029, EdgeType.DIRECTED);
                else if(graphType_64029==2)
                    this.topology.GetBCastGraph().addEdge(edge_64029, src_64029, dst_64029, EdgeType.DIRECTED);
                else if(graphType_64029==3)
                    this.topology.GetDownLinkGraph().addEdge(edge_64029, src_64029, dst_64029, EdgeType.DIRECTED);
                break;
                
    /*
            // 64030 build from gateway graph
            case 0xFA1E:
                Cmd64030 cmd64030 = (Cmd64030)cmd;
                NetworkNode node_64030 = this.topology.GetNetworkNode(cmd64030.getNodeNickName());
                NetworkNode nothing = null;
                node_64030.BuildFromGatewayGraph(cmd64030.getGraphID(), topology, nothing);
            break;

                // 64031 build from gateway graph (two nodes)
            case 0xFA1F:
                Cmd64031 cmd64031 = (Cmd64031)cmd;
                NetworkNode src_64031 = this.topology.GetNetworkNode(cmd64031.getSrcNickName());
                NetworkNode dst_64031 = this.topology.GetNetworkNode(cmd64031.getDstNickName());
                src_64031.BuildFromGatewayGraph(cmd64031.getGraphID(), topology, dst_64031);
                break;
    */
                
            case 0xFA20: //64032
                rspInfo.put(cmdId, cmd);
                break;

            case 0xFA21:
                Cmd64033 cmd64033 = (Cmd64033)cmd;
                char nickName_64033 = cmd64033.getNickName();
                NetworkNode node_64033 = this.topology.GetNetworkNode(nickName_64033);
                topology.GetGraph().removeVertex(node_64033);
                topology.GetUpLinkGraph().removeVertex(node_64033);
                topology.GetDownLinkGraph().removeVertex(node_64033);
                break;

            case 0xFA22:
                Cmd64034 cmd64034 = (Cmd64034)cmd;
                NodeUniqueID uid_64034 = new NodeUniqueID(cmd64034.getUid());
                this.topology.RmItemFromDeviceBlackList(uid_64034);
                break;

            case 0xFA27:
                Cmd64039 cmd64039 = (Cmd64039)cmd;
                NetworkNode node64039 = this.topology.GetNetworkNode(cmd64039.getNodeID());
                if(node64039!=null)
                {
                    int depth = node64039.GetUlgDepth() + 1;
                    node64039.SetUlgDepth(depth);
                }
                break;
                
            case 0xFA2E: //64046: ManagerCore sends graph info to GUI
                Cmd64046 cmd64046 = (Cmd64046) cmd;
                processCmd64046(cmd64046);
                break;
                
            case 0xFA2F: // 64047: Set max hop limit
                Cmd64047 cmd64047 = (Cmd64047) cmd;
                ManagerConfig.setMaxHopLimit(cmd64047.getMaxHopLimit());
                break;
                
            case 64613: // GWNM trial version has ended. Display notification to user.
                meshSimulator.displayTrialEndedNotification();
                break;

        }


        return rspCmd;
    }
    
    // move to bottom
    private void processCmd64046(Cmd64046 cmd)
    {
        // See Cmd64046 for full description of progressFlag
        int progressFlag = cmd.getProgressFlag();
        
        if (progressFlag == 2)
        {
            // ManagerCore has finished sending setup cmds.
            // Respond with progressFlag 3 to indicate GUI is ready to receive incremental updates
            Cmd64046 rspCmd = new Cmd64046(true);
            rspCmd.setProgressFlag((byte) 3);

            ArrayList<GuiCmdInfo> cmdList = new ArrayList<GuiCmdInfo>();
            cmdList.add(rspCmd);
            this.cmdSender.addMsg(CmdManipulation.GuiCmdListToByteArray(cmdList));
        }
    }

    public int testNetworkLayer() throws InterruptedException
    {
        Cmd64001 cmd64001 = new Cmd64001(true);
        char id = cmd64001.GetCmdID();
        Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
        cmdInfo.add(cmd64001);

        this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
        Cmd64001 cmd;
        int result = -1;
        if( (cmd = (Cmd64001)getRspInfo(id))!=null)
        {
            result = cmd.getNetworkLayerWorking();
            return result;
        }
        else
            return result;
    }


    public void closeGateway() throws InterruptedException
    {
        Cmd64203 cmd64203 = new Cmd64203(true);
        Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
        cmdInfo.add(cmd64203);
        this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
    }

    /**
     * ManagerGui sends a command to get ManagerCore's network ID
     */
    public void getManagerCoreNetworkID()
    {
        Cmd64006 cmd64006 = new Cmd64006(true);
        Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
        cmdInfo.add(cmd64006);
        this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

    /**
     * ManagerGui sends a command to get ManagerCore's channel map
     */
   public void getManagerCoreChannelMap()
    {
        Cmd64007 cmd64007 = new Cmd64007(true);
        Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
        cmdInfo.add(cmd64007);
        this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }
   /*
   public int getDeviceListSize()
   {
       Cmd64008 cmd64008 = new Cmd64008(true);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64008);

       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
        int deviceListSize = -1;
        Cmd64008 cmd;
        char id = cmd64008.GetCmdID();
        if( (cmd = (Cmd64008)getRspInfo(id))!=null)
        {
            deviceListSize = cmd.getDeviceListSize();
            return deviceListSize;
        }
        else
            return deviceListSize;
   }
   */

   /**
    * ManagerGui sends a command to get ManagerCore's device list
    */
   public void getManagerCoreDeviceList()
   {
       Cmd64009 cmd64009 = new Cmd64009(true);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64009);

       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }


    /**
     * Send a new network ID to the ManagerCore
     * @param nID the new network ID
     */
   public void setManagerCoreNetworkID(String nID)
   {
       Cmd64010 cmd64010 = new Cmd64010(true);
       cmd64010.setNetworkID(nID);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64010);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   /**
    * Send a new join key to the ManagerCore
    * @param joinKey the new join key
    */
   public void setManagerCoreJoinKey(byte[] joinKey)
   {
       Cmd64008 cmd64008 = new Cmd64008(true);
       cmd64008.setJoinKey(joinKey);
       
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64008);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }
   
   /**
    * Send a new channel map to the ManagerCore
    * @param cMap the new channel map
    */
   public void setManagerCoreChannelMap(String cMap)
   {
       Cmd64011 cmd64011 = new Cmd64011(true);
       cmd64011.setChannelMap(cMap);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64011);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }
   
   /**
    * Send a new max hop limit to the ManagerCore
    * @param hopLimit 
    */
   public void setManagerCoreMaxHopLimit(int hopLimit)
   {
       Cmd64047 cmd64047 = new Cmd64047(true);
       cmd64047.setMaxHopLimit(hopLimit);
       ArrayList<GuiCmdInfo> cmdList = new ArrayList<GuiCmdInfo>();
       cmdList.add(cmd64047);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdListToByteArray(cmdList));
   }

   /**
    * Send device updates (new / edited / deleted devices) to ManagerCore.
    * The list has the following string format (with no spaces):
    * "index # action # deviceType # deviceID # joinKey # deviceTag"
    * @return false no changes were made to device list, otherwise true
    */
   public boolean setManagerCoreDeviceUpdates()
   {
       Cmd64013 cmd64013 = new Cmd64013(true);
       ArrayList<AdditionalDevice> deviceUpdates = ManagerConfig.getDeviceUpdates();
       ArrayList<Integer> deviceIndex = ManagerConfig.getIndexOfDeviceUpdates();
       ArrayList<Integer> deviceAction = ManagerConfig.getActionOfDeviceUpdates();
       StringBuilder dev = new StringBuilder();

       if(deviceUpdates.isEmpty())
           return false; //don't need to send any cmd if there are no devices to update

       for (int i = 0; i < deviceUpdates.size(); i++)
       {
           int index = deviceIndex.get(i);
           int action = deviceAction.get(i);
           AdditionalDevice device = deviceUpdates.get(i);

           dev.append(Integer.toString(index)); //index In ManagerConfig where this device is
           dev.append("#");
           dev.append(Integer.toString(action)); //action to perform. 0 - edit, 1 - add, 2 - delete
           dev.append("#");
           dev.append(device.GetDeviceType().toString());
           dev.append("#");
           dev.append(ManagerConfig.ToXmlFormatHexString(device.GetDeviceID().GetUniqueID()));
           dev.append("#");
           dev.append(ManagerConfig.ToXmlFormatHexString(device.GetJoinKey().getKey()));
           dev.append("#");
           dev.append(ManagerConfig.ToXmlFormatHexString(device.GetDeviceTag().GetDeviceTag()));

           if (i < deviceUpdates.size() - 1)
               dev.append("#");
       }

       cmd64013.setDevList(dev.toString());
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64013);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
       waitForCmd64013Rsp(); //blocks until rsp cmd 64013 is received
       return true;
   }

   //we need to wait to recv cmd 64013 rsp before we let user continue using ManagerGui, so we can show any rejected devices
   public synchronized void waitForCmd64013Rsp()
   {
        while (receivedCmd64013Rsp == false)
        {
            try
            {
                wait(); //Maybe add a timeout mechanism, and display error saying save was incomplete
            }
            catch (InterruptedException ex)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        //reset variables
        receivedCmd64013Rsp = false;
    }

   //we have received cmd64013 rsp, notify anyone that is waiting.
   public synchronized void notifyCmd64013Rsp()
   {
       receivedCmd64013Rsp = true;
       notify();
   }

   //ManagerGui sends cmd to tell ManagerCore to save everything to XML.
   //Called after ManagerGui sends NetworkID / ChannelMap / updated devices
   public void saveToXML()
   {
       Cmd64014 cmd64014 = new Cmd64014(true);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64014);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   public boolean validateFormat(String s) throws InterruptedException
   {
       Cmd64012 cmd64012 = new Cmd64012(true);  // request
       cmd64012.setStrInfo(s);

       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64012);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));

        boolean isValidated = true;
        Cmd64012 cmd;
        char id = cmd64012.GetCmdID();
        if( (cmd = (Cmd64012)getRspInfo(id))!=null)
        {
            isValidated = cmd.getValidated();
            return isValidated;
        }
        else
            return isValidated;
   }


   public ArrayList<Integer> getTableInfo(char nickName) throws InterruptedException
   {
       Cmd64032 cmd64032 = new Cmd64032(true);
       cmd64032.setNickName(nickName);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64032);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));

       ArrayList<Integer> result = new ArrayList<Integer>();
       Cmd64032 cmd = new Cmd64032(true);
       char id = cmd64032.GetCmdID();
       if( (cmd = (Cmd64032)getRspInfo(id))!=null)
       {
           result = cmd.getResult();
       }

       return result;
   }


   public void setStaticBarrier(ArrayList<Integer>barrierInfo) throws InterruptedException
   {
       Cmd64602 cmd64602 = new Cmd64602(true);
       cmd64602.setBarrierInfo(barrierInfo);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64602);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   public void setVertexProperty(char nickName, double powerLeft, double powerAll, String scanPeriod) throws InterruptedException
   {
       Cmd64035 cmd64035 = new Cmd64035(true);
       cmd64035.setVertexProperty(nickName, powerLeft, powerAll, scanPeriod);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64035);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   public void deleteVertex(char nickName) throws InterruptedException
   {
       Cmd64036 cmd64036 = new Cmd64036(true);
       cmd64036.setNickName(nickName);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64036);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   public void setEdgeProperty(int edgeID, double capacity, double weight) throws InterruptedException
   {
       Cmd64037 cmd64037 = new Cmd64037(true);
       cmd64037.setEdgeInfo(edgeID, capacity, weight);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64037);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }

   public void deleteEdge(int edgeID) throws InterruptedException
   {
       Cmd64038 cmd64038 = new Cmd64038(true);
       cmd64038.setEdgeID(edgeID);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64038);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }


   public void initConnection() throws InterruptedException
   {
       Cmd64046 cmd64046 = new Cmd64046(true);
       Vector<GuiCmdInfo> cmdInfo = new Vector<GuiCmdInfo>();
       cmdInfo.add(cmd64046);
       this.cmdSender.addMsg(CmdManipulation.GuiCmdVectorToByteArray(cmdInfo));
   }
}
