package manager.guiApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.logging.Logger;
import mesh.NodeNickname;
import mesh.NodeType;
import mesh.NodeUniqueID;
import mesh.ScanPeriod;
import mesh.cmd.Cmd0;
import mesh.nmDevice;

/**
 * this function defines the network node
 * @author Song Han
 */
public class NetworkNode implements java.io.Serializable
{
    private final static Logger LOGGER = Logger.getLogger(NetworkNode.class.getName());
    
    // a marker for algorithmic usage
    private boolean isMarked;
    private int markState; // general purpose marker used for various states in graph algorithms. 

    // power infomation
    private boolean isEnabled;
    private boolean isPowered;
    private double powerLeft;
    private double powerAll;
    private double defaultPower = 100;

    // id, nickname and uniqueID
    private int nodeID;
    private NodeNickname nodeNickname;
    private NodeUniqueID nodeUniqueID;

    // device information
    private NodeType nodeType;
    private ScanPeriod scanPeriod;
    private byte[] longTag = new byte[32]; //fixed length of 32 bytes
    Cmd0 cmd0; // save the device's cmd0 info
    int firstJoinTxLinkSlotNumber; // keep a reference to the slot number of this device's first join TX link on SF1 (downlink)
    nmDevice device;

    static int currentID = 0;
    private int ulgDepth = 0; // the number of hops between this node and GWNM. AP's hop level is 0.

    /**
     * this function finalizes the data structures used in network node
     */
    @Override
    protected void finalize()
    {
        device = null;
    }

    /**
     * this function gets the ULG depth
     * @return the ULG depth
     */
    public int GetUlgDepth()
    {
        return this.ulgDepth;
    }

    /**
     * this function sets the ULG depth
     * @param depth the ULG depth
     */
    public void SetUlgDepth(int depth)
    {
        this.ulgDepth = depth;
    }
    
    /**
     * this function gets the superframe ID list which will be used by the network node
     * @return the superframe ID list which will be used by the network node
     */
    public Vector<Integer> GetSFIDList()
    {
        return this.device.GetSFIDList();
    }

    /**
     * this function checks the superframe ID list to see if a given ID exists
     * @param id the superframe ID to check if it is in the network node's superframe ID list
     * @return true if the given SF ID exists in the network node's superframe ID list. Otherwise return false.
     */
    public boolean HasSFIDList(int id)
    {
        return this.device.HasSuperFrameID(id);
    }

    /**
     * constructor function. Create a network node instance with the given parameters
     * @param enabled is the device enabled in the network
     * @param powered is the device powered or based on battery
     * @param uniqueID the unique ID of the network node
     * @param name the nickname of the network node
     * @param id the node ID
     * @param type the node type
     * @param period the scan period of the network node
     */
    public NetworkNode(boolean enabled, boolean powered, NodeUniqueID uniqueID, NodeNickname name, int id, NodeType type, ScanPeriod period)
    {
        isMarked = false;
        isEnabled = enabled;
        isPowered = powered;
        nodeNickname = name;
        nodeUniqueID = uniqueID;
        nodeID = id;
        nodeType = type;
        scanPeriod = period;

        if(isPowered == true)
            powerAll = powerLeft = defaultPower;

        device = null;
    }

    /**
     * constructor function. Create a network node instance with the default settings
     */
    public NetworkNode()
    {
        isMarked = false;
        isEnabled = true;
        isPowered = true;
        nodeID = currentID++;
        nodeType = NodeType.ABSTRACT;
        char[] initialAddr = {0,0,0,0};
        nodeNickname = new NodeNickname(initialAddr[0]);
        nodeUniqueID = new NodeUniqueID(initialAddr);
        scanPeriod = new ScanPeriod(ScanPeriod._0_ms);
        device = null;
     
        if(isPowered == true)
            powerAll = powerLeft = defaultPower;            
    }

    /**
     * constructor function. Create a network node instance with the default settings except the given nickname
     * @param c the nickname of the network node
     */
    public NetworkNode(char c)
    {
        isMarked = false;
        isEnabled = true;
        isPowered = true;

        nodeID = -1;

        nodeType = NodeType.DEVICE;
        nodeNickname = new NodeNickname(c);
        scanPeriod = new ScanPeriod(ScanPeriod._0_ms);
        device = null;

        if(isPowered == true)
            powerAll = powerLeft = defaultPower;
    }

    /**
     * constructor function. Create a network node instance with the default settings except the device type
     * @param s the device type in the string format
     */
    public NetworkNode(String s)
    {
        isMarked = false;
        isEnabled = true;
        isPowered = true;

        nodeID = ++currentID;
        
        if(s.equals("AP") == true)
            nodeType = NodeType.AP;
        else if(s.equals("DEVICE") == true)
            nodeType = NodeType.DEVICE;
        else if(s.equals("ROUTER") == true)
            nodeType = NodeType.ROUTER;
        else if(s.equals("HANDHELD") == true)
            nodeType = NodeType.HANDHELD;
        else if(s.equals("GATEWAY") == true)
            nodeType = NodeType.GATEWAY;

        char[] initialAddr = {0,0,0,0};
        nodeNickname = new NodeNickname(initialAddr[0]);
        nodeUniqueID = new NodeUniqueID(initialAddr);
        scanPeriod = new ScanPeriod(ScanPeriod._0_ms);
        device = null;

        if(isPowered == true)
            powerAll = powerLeft = defaultPower;
    }

    /**
     * this function sets the nmDevice structure for the network node
     * @param dev the nmDevice structure for the network node
     */
    public void SetnmDevice(nmDevice dev)
    {
        this.device = dev;
    }

    /**
     * this function gets the nmDevice structure for the network node
     * @return the nmDevice structure for the network node
     */
    public nmDevice GetnmDevice()
    {
        return this.device;
    }

    /**
     * this function sets the network node to be marked. Used in some algorithms to select the device
     */
    public void SetMark()
    {
        this.isMarked = true;
    }

    /**
     * this function sets the network node to be not marked. Used in some algorithms to select the device
     */
    public void ClearMark()
    {
        this.isMarked = false;
    }

    /**
     * this function checks if a network node is marked or not
     * @return true if the network node is marked. Otherwise return false.
     */
    public boolean IsMarked()
    {
        return this.isMarked;
    }

    /**
     * this function sets the network node to be powered instead of battery support
     * @param powered true if the network node to be powered. false if the network node is based on battery
     */
    public void setPowered(boolean powered)
    {
        isPowered = powered;
    }

    /**
     * this function checks if the network node is based on power or based on battery support
     * @return true if the network node is based on power. false if the network node is based on battery support
     */
    public boolean isPowered()
    {
        return isPowered;
    }

    /**
     * this function sets the network node to be enabled in the network
     * @param enabled true if the network node to be enabled in the network. false if it is disabled in the network
     */
    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    /**
     * this function checks if the network node is enabled in the network
     * @return true if the network node is enabled in the network. Otherwise return false.
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * this function gets the power left in the device
     * @return the power left in the device
     */
    public double getPowerLeft()
    {
        return powerLeft;
    }

    /**
     * this function sets the power left in the device
     * @param p the power left in the device
     */
    public void setPowerLeft(double p)
    {
        powerLeft = p;
    }

    /**
     * this function gets the total power in the device
     * @return the total power in the device
     */
    public double getPowerAll()
    {
        return powerAll;
    }

    /**
     * this function sets the total power in the device
     * @param p the total power in the device
     */
    public void setPowerAll(double p)
    {
        powerAll = p;
    }

    /**
     * this function display the network node ID in the string format
     * @return the network node ID in the string format
     */
    @Override
    public String toString()
    {
        return "" + nodeID;
    }

    /**
     * this function gets the network node ID
     * @return the network node ID
     */
    public int getID()
    {
        return nodeID;
    }

    /**
     * this function gets the device type
     * @return the device type
     */
    public String getType()
    {
        return nodeType.toString();
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    /**
     * this function sets the device type
     * @param t the device type
     */
    public void setType(NodeType t)
    {
        this.nodeType = t;
    }

    /**
     * this function gets the device scan period
     * @return the device scan period
     */
    public ScanPeriod getScanPeriod()
    {
        return scanPeriod;
    }
    
    /**
     * this function gets the network node nickname
     * @return the network node nickname
     */
    public NodeNickname getNodeNickname()
    {
        return nodeNickname;
    }

    /**
     * this function gets the network node unique ID
     * @return the network node unique ID
     */
    public NodeUniqueID getNodeUniqueID()
    {
        return nodeUniqueID;
    }

    /**
     * this function gets the current node ID in the network
     * @return the current node ID in the network.
     */
    public static int getCurrentId()
    {
        return ++currentID;
    }

    /**
     * this function sets the currentID to be zero
     */
    public static void clearCurrentId()
    {
        currentID = 0;
    }

    /**
     * this function sets the currentID to the given ID number
     * @param id the current ID value
     */
    public static void setCurrentId(int id)
    {
        currentID = id;
    }

    /**
     * this function sets the network node nickname
     * @param name the network node nickname
     */
    public void setNodeNickname(NodeNickname name)
    {
        nodeNickname = name;
    }

    /**
     * this function sets the network node unique ID
     * @param id the network node unique ID
     */
    public void setNodeUniqueID(NodeUniqueID id)
    {
        nodeUniqueID = id;
    }

    /**
     * this function sets the network node scan period
     * @param p the scan period of the network node in the ScanPeriod format
     */
    public void setScanPeriod(ScanPeriod p)
    {
        this.scanPeriod = p;
    }
    
    /**
     * this function sets the network node scan period
     * @param s the scan period of the network node in the String format
     */
    public void setScanPeriod(String s)
    {
        if(s.equals("_0_ms") == true)
            scanPeriod.setScanPeriod(ScanPeriod._0_ms);
        else if(s.equals("_250_ms") == true)
            scanPeriod.setScanPeriod(ScanPeriod._250_ms);
        else if(s.equals("_500_ms") == true)
            scanPeriod.setScanPeriod(ScanPeriod._500_ms);
        else if(s.equals("_1_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._1_sec);
        else if(s.equals("_2_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._2_sec);
        else if(s.equals("_4_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._4_sec);
        else if(s.equals("_8_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._8_sec);
        else if(s.equals("_16_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._16_sec);
        else if(s.equals("_32_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._32_sec);
        else if(s.equals("_1_min_4_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._1_min_4_sec);
        else if(s.equals("_2_min_8_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._2_min_8_sec);
        else if(s.equals("_4_min_16_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._4_min_16_sec);
        else if(s.equals("_8_min_32_sec") == true)
            scanPeriod.setScanPeriod(ScanPeriod._8_min_32_sec);
    }

    /**
     * Get the Cmd0 rsp of a device
     * @return Cmd0
     */
    public Cmd0 getCmd0()
    {
        return this.cmd0;
    }
    
    /**
     * Set the Cmd0 rsp of a device (cache cmd0)
     * @param c 
     */
    public void setCmd0(Cmd0 c)
    {
        this.cmd0 = c;
    }

    /**
     * Get the long tag of this device
     * @return long tag
     */
    public byte[] getLongTag()
    {
        return this.longTag;
        }
    
    /**
     * Set the long tag of this device
     * @param tag 
     */
    public void setLongTag(byte[] tag)
        {
        System.arraycopy(tag, 0, this.longTag, 0, 32);
    }

    /**
     * Get the slot number of this device's first available join TX link
     * @return slot number
     */
    public int getFirstJoinTxLinkSlotNumber()
    {
        return this.firstJoinTxLinkSlotNumber;
    }

    /**
     * Set the slot number of this device first available join TX link
     * @param slotNumber 
     */
    public void setFirstJoinTxLinkSlotNumber(int slotNumber)
        {
        this.firstJoinTxLinkSlotNumber = slotNumber;
        }

    /**
     * Get the mark state of this node
     * @return mark state
     */
    public int getMarkState()
        {
        return markState;
    }

    /**
     * Set the mark state of this node
     * @param markState 
     */
    public void setMarkState(int markState)
    {
        this.markState = markState;
    }

    // UNUSED
    /**
     * this function updates the device downlink graph route entry in the network node
     */
    /*void UpdateDeviceDLGRouteEntry()
    {
        LOGGER.log(Level.INFO, "I am updating the device dlg route entry");
        int graphId = this.fromGatewayGraph.getGraphID();
        NodeNickname dest = this.getNodeNickname();
        
        // construct a new graph entry
        nmGraphEntry gEntry = new nmGraphEntry((short)graphId, dest);
        
        Collection<NetworkNode> nodeCollection = fromGatewayGraph.getGraph().getSuccessors(fromGatewayGraph.getRoot().getNetworkNode());
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();
        
        NetworkNode neighbor = null;
        while(nodeItr.hasNext())
        {
            neighbor = nodeItr.next();
            gEntry.AddNeighbor(neighbor.getNodeNickname());
        }
        
        Iterator<nmRouteEntry> routeItr = this.device.GetUnicastSession().GetRouteEntrySet().iterator();

        nmRouteEntry rEntry = null;
        while(routeItr.hasNext())
        {
            rEntry = routeItr.next();
            if(rEntry.GetGraph().GetGraphID() == gEntry.GetGraphID())
            {
                rEntry.SetGraph(gEntry);
                return;
            }
        }

        // if no existing route entry is found. Construct a new route entry
        rEntry = new nmRouteEntry(nmRouteEntry.GetCurrentRouteID(), dest, gEntry, null, nmRouteType.ROUTE_TYPE_PUBLISH);
        this.device.GetUnicastSession().AddRouteEntry(rEntry);
    }
*/




// UNUSED
    /**
     * this function gets the source route from the gateway by calculating the shortest path from the gateway to the network node
     * @return the source route from the gateway
     */
    /* public ArrayList GetSourceRouteFromGW()
    {
        ArrayList srcRouteList = new ArrayList();
        NetworkNode src = this.getNetworkGraphFromGW().getSrc();
        NetworkNode dest = this.getNetworkGraphFromGW().getDest();
        srcRouteList.add(this.getNetworkGraphFromGW().getSrc().getNodeNickname());

        // new a transformer to calculate the link weight
        Transformer<NetworkEdge, Double> wtTransformer = new Transformer<NetworkEdge,Double>() {
            public Double transform(NetworkEdge e) {
                if(e.calcLinkQuality() == 0)
                    return 1000.0; // set a big value
                else
                    return (1 / e.calcLinkQuality());
            }
        };

        // use the DijkstraShortestPath algorithm to get the shorest path from the gateway to the network node
        DijkstraShortestPath<NetworkNode,NetworkEdge> alg = new DijkstraShortestPath(this.getNetworkGraphFromGW().getGraph(), wtTransformer);
        List<NetworkEdge> l = alg.getPath(src, dest);

        for(int i = 0; i < l.size(); i ++)
        {
            NetworkEdge e = l.get(i);
            srcRouteList.add(this.getNetworkGraphFromGW().getGraph().getDest(e).getNodeNickname());
        }
        return srcRouteList;
    }
*/

    // Serializable interface: 
    private void writeObject(ObjectOutputStream o) throws IOException
    {
        o.writeObject(nodeID);
        o.writeObject(nodeNickname);
        o.writeObject(nodeUniqueID);
        
        o.writeObject(nodeType);
    }

    // Serializable interface: 
    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
    {
        nodeID = (Integer) o.readObject();
        nodeNickname = (NodeNickname) o.readObject();
        nodeUniqueID = (NodeUniqueID) o.readObject();
        
        nodeType = (NodeType) o.readObject();
    }
    
}
