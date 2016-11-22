package manager.guiApp;

import mesh.*;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.commons.collections15.Factory;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import manager.AdditionalDevice;
import manager.ManagerConfig;
import manager.NetAccessEntry;
import manager.gui.GraphPruneOption;
import manager.gui.StaticBarrier;
import org.apache.commons.collections15.Transformer;

import mesh.security.SecurityManager;
import org.xml.sax.SAXException;

/**
 * this class defines the network topology
 * @author Song Han
 */
public class GUITopology {

    private final static Logger LOGGER = Logger.getLogger(GUITopology.class.getName());

    // graph root
    NetworkNode root;

    // a special broadcast node
    NetworkNode bCastNode;

    GUICmdProcessor cmdprocessor;


    // orignal graph and schedule
    public Graph<NetworkNode, NetworkEdge> g;
    int numOfChannel = 16;

    // broadcast graph
    public Graph<NetworkNode, NetworkEdge> bg;
    public Graph<NetworkNode, NetworkEdge> bgSnapShot;
    int bgID = GraphId.BROADCAST_GRAPH_ID;

    // uplink graph
    public Graph<NetworkNode, NetworkEdge> ulg;
    public Graph<NetworkNode, NetworkEdge> ulgSnapShot;
    int ulgID = GraphId.UPSTREAM_GRAPH_ID;

    // snapshot of the superframes and the nodeschedules

    // global downlink graph
    Graph<NetworkNode, NetworkEdge> dlg;

    int nodeCount, edgeCount;
    Factory <NetworkNode> vertexFactory;
    Factory <NetworkEdge> edgeFactory;

    GraphPruneOption pruneOption;
    public ArrayList<StaticBarrier> barriers;
    ArrayList<NodeUniqueID> whiteList;
    ArrayList<NodeUniqueID> blackList;

    // network layer info for the devices
    static int deviceID = 0;


     ArrayList<NetAccessEntry> netAccessList;

    // prunning parameters
    final double defaultBestSignalStrength = 5;
    final double defaultWorstSignalStrength = -100;


    /**
     * this function gets the network prune option
     * @return the network prune option
     */
    public GraphPruneOption GetPruneOption()
    {
        return this.pruneOption;
    }

    public void setCmdProcessor(GUICmdProcessor cmdProc)
    {
        this.cmdprocessor = cmdProc;
    }


    /**
     * this function gets a new device ID
     * @return a new device ID
     */
    public static int GetDevID()
    {
        int id = deviceID;
        deviceID ++;
        return id;
    }




    /**
     * this function increase the current device ID by one
     */
    public static void IncreaseDevID()
    {
        deviceID ++;
    }





    /**
     * this function gets the device white list in the network
     * @return the device white list in the network
     */
    public ArrayList<NodeUniqueID> GetWhiteList()
    {
        return this.whiteList;
    }

    /**
     * this function gets the device black list in the network
     * @return the device black list in the network
     */
    public ArrayList<NodeUniqueID> GetBlackList()
    {
        return this.blackList;
    }

    /**
     * this function gets the network topology. It is represented in a format of Graph<NetworkNode, NetworkEdge>
     * @return a representation of the network topology
     */
    public Graph<NetworkNode, NetworkEdge> GetGraph()
    {
        return g;
    }

    // Set the general graph to a new graph
    public void setGraph(Graph<NetworkNode, NetworkEdge> newGraph)
    {
        this.g = newGraph;
    }
    
    
    /**
     * this function gets the broadcast graph in the network
     * @return the broadcast graph in the network
     */
    public Graph<NetworkNode, NetworkEdge> GetBCastGraph()
    {
        return this.bg;
    }

    /**
     * this function sets the broadcast graph in the network
     * @param g the broadcast graph in the network
     */
    public void SetBCastGraph(Graph<NetworkNode, NetworkEdge> g)
    {
        this.bg = g;
    }

    /**
     * this function sets the uplink graph in the network
     * @param g the uplink graph in the network
     */
    public void SetUpLinkGraph(Graph<NetworkNode, NetworkEdge> g)
    {
        this.ulg = g;
    }

    /**
     * this function sets the global downlink graph in the network
     * @param g the glbal downlink graph in the network
     */
    public void SetDownLinkGraph(Graph<NetworkNode, NetworkEdge> g)
    {
        this.dlg = g;
    }

    /**
     * this function gets the broadcast graph ID
     * @return the broadcast graph ID
     */
    public int GetBCastGraphID()
    {
        return this.bgID;
    }

    /**
     * this function gets the uplink graph in the network
     * @return the uplink graph in the network
     */
    public Graph<NetworkNode, NetworkEdge> GetUpLinkGraph()
    {
        return this.ulg;
    }

    /**
     * this function gets the uplink graph ID
     * @return the uplink graph ID
     */
    public int GetUpLinkGraphID()
    {
        return this.ulgID;
    }

    /**
     * this function gets the global downlink graph
     * @return the global downlink graph
     */
    public Graph<NetworkNode, NetworkEdge> GetDownLinkGraph()
    {
        return this.dlg;
    }

    /**
     * this function gets the uplink graph snapshot
     * @return the uplink graph snapshot
     */
    public Graph<NetworkNode, NetworkEdge> GetULGSnapShot()
    {
        return this.ulgSnapShot;
    }

    /**
     * this function gets the broadcast graph snapshot
     * @return the broadcast graph snapshot
     */
    public Graph<NetworkNode, NetworkEdge> GetBGSnapShot()
    {
        return this.bgSnapShot;
    }

    /**
     * constructor function. Create an instance of the network toplogy with the given parameters
     * @param graph the representation of the network topology
     * @param sm the security manager
     */
    public GUITopology(Graph<NetworkNode, NetworkEdge> graph)
    {
        // create the vertex factory
        vertexFactory = new Factory<NetworkNode>() {
            public NetworkNode create() {

                nodeCount ++;
                return (new NetworkNode());
            }
        };

        // create the edge factory
        edgeFactory = new Factory<NetworkEdge>() {
            public NetworkEdge create() {

                edgeCount ++;
                return (new NetworkEdge());
            }
        };

        // initialize the data structures
        nodeCount = graph.getVertexCount();
        edgeCount = graph.getEdgeCount();
        g = graph;
        bg = bgSnapShot = null;
        ulg = ulgSnapShot = null;
        dlg = null;
        root = null;
        bCastNode = new NetworkNode((char)(0xFFFF));

        pruneOption = new GraphPruneOption();
        barriers = new ArrayList<StaticBarrier>();
        whiteList = new ArrayList<NodeUniqueID>();
        blackList = new ArrayList<NodeUniqueID>();
    }

    /**
     * this function initializes the network access list by reading the device information from the ManagerConfig
     */
    final public void InitNetworkAccessList()
    { /*
        ArrayList<String> devList = new ArrayList<String>();
        try {
            devList = ManagerConfigParser.getDevices();
        } catch (InterruptedException ex) {
            Logger.getLogger(GUITopology.class.getName()).log(Level.SEVERE, null, ex);
        }
        */

        ArrayList<AdditionalDevice> devLst = ManagerConfig.GetDeviceList();
        for(int i=0; i<devLst.size(); i++) {
            AdditionalDevice ad = devLst.get(i);
            netAccessList.add(new NetAccessEntry(ad.GetDeviceID(), ad.GetDeviceType(),
                    ad.GetJoinKey(), ad.GetDeviceTag()));
        }
    }

    /**
     * constructor function. Create an instance of the network topology
     * @param isRandom is it randomly generated?
     * @param node the number of nodes in the network
     * @param edge the number of edges in the network
     * @param sm the security manager
     */
    public GUITopology(boolean isRandom, int node, int edge) {

        // create a vertex factory
        vertexFactory = new Factory<NetworkNode>() {
            public NetworkNode create() {
              nodeCount ++;
                return (new NetworkNode());
            }
        };

        // create an edge factory
        edgeFactory = new Factory<NetworkEdge>() {
            public NetworkEdge create() {

                edgeCount ++;
                return (new NetworkEdge());
            }
        };

        // if the construction is random
        if(isRandom == true)
        {
            nodeCount = node;
            edgeCount = edge;

            // construct a new representation of the network topology
            g = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();

            // create network node and add them to the graph
             NetworkNode [] vertexSet = new NetworkNode [nodeCount];

            for(int j = 0; j < nodeCount; j ++)
            {
                vertexSet [j] = new NetworkNode();
                g.addVertex(vertexSet [j]);

                // add the device to the device white list
                this.AddItemToDeviceWhiteList(vertexSet[j].getNodeUniqueID());
            }

            // create network edges and add them to the graph
            NetworkEdge currentEdge;
            for(int i = 0; i < edgeCount; i ++)
            {
                int random1 = (int)(Math.random() * 1000) % nodeCount;
                int random2 = (int)(Math.random() * 1000) % nodeCount;

                currentEdge = new NetworkEdge();
                g.addEdge(currentEdge, vertexSet [random1], vertexSet [random2], EdgeType.DIRECTED);
            }
        }
        else
        {
            // if not randomly, construct an empty network topology
            g = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
            dlg = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
            ulg = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
            nodeCount = edgeCount = 0;
        }

        // initialize the data structures
        bg = null;
        bgSnapShot = null;
        ulgSnapShot = null;
        root = null;
        bCastNode = new NetworkNode((char)(0xFFFF));

        pruneOption = new GraphPruneOption();
        barriers = new ArrayList<StaticBarrier>();
        whiteList = new ArrayList<NodeUniqueID>();
        blackList = new ArrayList<NodeUniqueID>();
        netAccessList = new ArrayList<NetAccessEntry>();
    }

    /**
     * this function saves the network toplogy to the given file. Adding the nodes and edge information to the file according to the given format
     * @param ofile the given file to save the network topology
     * @param layout the layout of the visualization viewer
     */
    public void saveToFile(PrintWriter ofile, Layout<NetworkNode, NetworkEdge> layout)
    {
        Collection <NetworkNode> vertexCollection = g.getVertices();
        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkNode> itr = vertexCollection.iterator();
        Iterator <NetworkEdge> itr2 = edgeCollection.iterator();
        int vertexNum = g.getVertexCount();
        int edgeNum = g.getEdgeCount();

        NetworkNode itrNode;
        NetworkEdge itrEdge;

        ofile.println(vertexNum);
        while(itr.hasNext()  == true)
        {
            itrNode = itr.next();
            Point2D position = layout.transform(itrNode);
            ofile.println(itrNode.getID() + "\t" + position.getX() + "\t" + position.getY() + "\t" + itrNode.getNodeUniqueID().toString() + "\t" + itrNode.getNodeNickname().toString() + "\t" + itrNode.getType() + "\t" + itrNode.getScanPeriod() + "\t" + itrNode.isEnabled() + "\t" + itrNode.isPowered());
        }

        ofile.println("\n" + edgeNum);
        while(itr2.hasNext() == true)
        {
            itrEdge = itr2.next();
            NetworkNode srcNode = g.getSource(itrEdge);
            NetworkNode destNode = g.getDest(itrEdge);
            ofile.println(itrEdge.getEdgeId() + "\t" + srcNode.getID() + "\t" + destNode.getID() + "\t" + itrEdge.getWeight() + "\t" + itrEdge.getCapacity() + "\t" + itrEdge.getSignalStrength());
        }
    }

    /**
     * this function adds edges for a given node according to its location to all the other nodes in the layout
     * @param v the given node to add edges
     * @param layout the layout of the network
     */
    public void addEdgesforNode(NetworkNode v, Layout<NetworkNode, NetworkEdge> layout)
    {
        Collection <NetworkNode> vertexCollection = g.getVertices();
        Iterator<NetworkNode> itr = vertexCollection.iterator();
        NetworkNode itrNode;
        Point2D vPosition = layout.transform(v);

        while(itr.hasNext()  == true)
        {
            itrNode = itr.next();
            Point2D position = layout.transform(itrNode);

            // if itrNode is not itself or the GW, add edges
            if(v.getType().equals("AP") == true)
            {
                if(itrNode.getType().equals("AP") != true)
                {
                    if(itrNode.getType().equals("GATEWAY") == true)
                    {
                        NetworkEdge edge = new NetworkEdge(1.0,1.0,defaultBestSignalStrength);
                        NetworkEdge edge2 = new NetworkEdge(1.0,1.0,defaultBestSignalStrength);
                        g.addEdge(edge, v, itrNode);
                        g.addEdge(edge2, itrNode, v);
                    }
                    else
                    {
                        NetworkEdge edge = new NetworkEdge(1.0,1.0,calcSignalStrength(vPosition, position));
                        NetworkEdge edge2 = new NetworkEdge(1.0,1.0,calcSignalStrength(vPosition, position));
                        g.addEdge(edge, v, itrNode);
                        g.addEdge(edge2, itrNode, v);
                    }
                }
            }
            else
            {
                if(itrNode.equals(v) != true && itrNode.getType().equals("GATEWAY") != true)
                {
                    NetworkEdge edge = new NetworkEdge(1.0,1.0,calcSignalStrength(vPosition, position));
                    NetworkEdge edge2 = new NetworkEdge(1.0,1.0,calcSignalStrength(vPosition, position));
                    g.addEdge(edge, v, itrNode);
                    g.addEdge(edge2, itrNode, v);
                }
            }
        }
    }

    /**
     * this function checks if a given line is blocked by the barriers in the network topology
     * @param src the source point of the given line
     * @param dest the destination point of the given line
     * @return true if the given line is blocked by the barriers in the network topology. Otherwise, false
     */
    public boolean BlockedByBarrier(Point2D src, Point2D dest)
    {
        for(int i = 0; i < barriers.size(); i ++)
        {
            if(barriers.get(i).IntersectWithLine(src, dest))
                return true;
        }
        return false;
    }

    /**
     * this function calculates the signal strength between the two given points
     * @param src the source point
     * @param dest the destination point
     * @return the calculated signal strength
     */
    public double calcSignalStrength(Point2D src, Point2D dest)
    {
        // if the two points are blocked by a barrier, return very low signal strength
        if(BlockedByBarrier(src, dest))
            return -100;
        else
        {
            // calculate the signal strength
            double PixelsPerMetre = 10;
            double DistanceForZeroDbSignalStrength = 5;
            double distance = src.distance(dest);
            distance += (0.5 - Math.random()) * 20;

            double distanceMetres = distance / PixelsPerMetre;
            double signalStrength = Math.log10(DistanceForZeroDbSignalStrength / distanceMetres) * 20;

            return signalStrength;
        }
    }

    /**
     * this function reevaluates the signal strength of the given edge
     * @param edge the given edge
     * @param layout the layout of the network
     */
    public void reEvaluateSignalStrength(NetworkEdge edge, Layout<NetworkNode, NetworkEdge> layout)
    {
        NetworkNode src = g.getSource(edge);
        NetworkNode dest = g.getDest(edge);

        edge.setSignalStrength(calcSignalStrength(layout.transform(src), layout.transform(dest)));
    }

    /**
     * this function reevaluates the signal strength for all the edges in the network
     * @param layout the layout of the network
     */
    public void reEvaluateAllSignalStrength(Layout<NetworkNode, NetworkEdge> layout)
    {
        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkEdge> itr = edgeCollection.iterator();

        NetworkEdge itrEdge;

        while(itr.hasNext() == true)
        {
            itrEdge = itr.next();

            NetworkNode src = g.getSource(itrEdge);
            NetworkNode dest = g.getDest(itrEdge);

            if((src.getType().equals("AP") == true && dest.getType().equals("GATEWAY") == true)
            || (src.getType().equals("GATEWAY") == true && dest.getType().equals("AP") == true))
                ;
            else
                reEvaluateSignalStrength(itrEdge, layout);
        }
    }

    /**
     * this function restore the signal strength for all the edges in the network
     */
    public void reStoreAllSignalStrength()
    {
        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkEdge> itr = edgeCollection.iterator();

        NetworkEdge itrEdge;

        while(itr.hasNext() == true)
        {
            itrEdge = itr.next();

            if(itrEdge.IsSignalStrengthSwapped())
            {
                // restore the signal strength of the ege using the backup value
                itrEdge.setSignalStrength(itrEdge.getSwapSignalStrength());
                itrEdge.SetSignalStrengthNotSwapped();
            }
        }
    }

    /**
     * this function adds a barrier into the network topology
     * @param bar the barrier to be added into the network topology
     */
    public void addBarrier(StaticBarrier bar)
    {
        barriers.add(bar);
    }

    /**
     * this function removes a barrier from the network topology
     * @param bar the barrier to be removed from the network topology
     */
    public void removeBarrier(StaticBarrier bar)
    {
        barriers.remove(bar);
    }

    /**
     * this function gets the list of barriers in the network
     * @return the list of barriers in the network
     */
    public ArrayList<StaticBarrier> GetBarriers()
    {
        return this.barriers;
    }

    /**
     * this function removes all the barriers in the network visualizer
     * @param visualizer the network visualizer
     */
    public void clearUp(NetworkVisualizer visualizer)
    {
        // clear up all the barriers in the network visualizer
        for(int i = 0; i < barriers.size(); i ++)
            visualizer.getViewer().removePostRenderPaintable(barriers.get(i));
        visualizer.getViewer().repaint();
    }

    /**
     * this function gets the broadcast node
     * @return the broadcast node
     */
    public NetworkNode GetBCastNode()
    {
        return this.bCastNode;
    }

    /**
     * this function gets the root of the network topology which is the gateway of the wirelesshart network
     * @return the root of the network topology
     */
    public NetworkNode GetRoot()
    {
        if(root != null)
            return root;
        else
        {
            Collection <NetworkNode> nodeCollection = g.getVertices();
            Iterator<NetworkNode> itr = nodeCollection.iterator();

            NetworkNode itrNode;

            while(itr.hasNext() == true)
            {
                // return the gateway of the wirelesshart network
                itrNode = itr.next();
                if(itrNode.getType().equals("GATEWAY"))
                    return itrNode;
            }
            return null;
        }
    }

    /**
     * this function gets the network node with the given node ID
     * @param id the node ID
     * @return the the network node with the given node ID. null if the node is not found
     */
    public NetworkNode GetNetworkNode(int id)
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if(itrNode.getID() == id)
                return itrNode;
        }
        return null;
    }

    /**
     * this function gets the network node with the given node nickname
     * @param nickName the node nickname
     * @return the network node with the given node nickname. null if the node doesn't exist
     */
    public NetworkNode GetNetworkNode(char nickName)
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if(itrNode.getNodeNickname().GetNickName() == nickName)
                return itrNode;
        }
        return null;
    }

    /**
     * this function gets the network node with the given node unique ID
     * @param uid the node unique ID
     * @return the network node with the given node unique ID
     */
    public NetworkNode GetNetworkNode(NodeUniqueID uid)
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if(itrNode.getNodeUniqueID().equals(uid) == true)
                return itrNode;
        }
        return null;
    }

    /**
     * this function gets the maximum node ID in the network
     * @return the maximum node ID in the network
     */
    public int GetMaxNodeID()
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();
        int maxID = 0;

        NetworkNode itrNode;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if(itrNode.getID() > maxID)
                maxID = itrNode.getID();
        }
        return maxID;
    }

    /**
     * this function gets the set of nodes whose scan period is equal to the given one
     * @param sp the given scan period
     * @return the set of nodes whose scan period is equal to the given one
     */
    public Collection<NetworkNode> GetNodewithPeriod(ScanPeriod sp)
    {
        Collection <NetworkNode> selectedNodes = new HashSet <NetworkNode>();
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if(itrNode.getScanPeriod().getScanPeriod() == sp.getScanPeriod())
                selectedNodes.add(itrNode);
        }
        return selectedNodes;
    }

    /**
     * this function generates the broadcast graph based on the network topology
     */
    public void GenerateBroadcastGraph()
    {
        root = GetRoot();

        // construct a hashmap for the network edge and its corresponding edge weight
        HashMap<NetworkEdge, Double> map = new HashMap<NetworkEdge, Double>();

        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkEdge> itr = edgeCollection.iterator();

        NetworkEdge itrEdge;
        while(itr.hasNext() == true)
        {
            itrEdge = itr.next();
            map.put(itrEdge, (-1) * itrEdge.getSignalStrength());
        }

        // apply the MinimumSpanningForest algorithm to construct the broadcast graph
        MinimumSpanningForest<NetworkNode,NetworkEdge> prim =
        	new MinimumSpanningForest<NetworkNode,NetworkEdge>(g,
        		new DelegateForest<NetworkNode, NetworkEdge>(), root,
        		map);

        bg = prim.getForest().getTrees().iterator().next();
    }

    /**
     * this function generates the uplink graph.
     */
    public void GenerateUpLinkGraph()
    {
        root = GetRoot();

        // construct a hashmap for the network edge and its corresponding edge weight
        HashMap<NetworkEdge, Double> map = new HashMap<NetworkEdge, Double>();

        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkEdge> itr = edgeCollection.iterator();

        NetworkEdge itrEdge;
        while(itr.hasNext() == true)
        {
            itrEdge = itr.next();
            map.put(itrEdge, (-1) * itrEdge.getSignalStrength());
        }

        // apply the MinimumSpanningForest algorithm to construct the graph
        MinimumSpanningForest<NetworkNode,NetworkEdge> prim =
        	new MinimumSpanningForest<NetworkNode,NetworkEdge>(g,
        		new DelegateForest<NetworkNode, NetworkEdge>(), root,
        		map);

        ulg = prim.getForest().getTrees().iterator().next();

        // reverse the ulg to construct the broadcast graph for the network topology. Notice here we assume that the link is dual link
        Graph<NetworkNode, NetworkEdge> uplinkGraph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
        Collection <NetworkEdge> edgeUpCollection = ulg.getEdges();
        Iterator<NetworkEdge> itr2 = edgeUpCollection.iterator();

        NetworkEdge itrEdge2;
        while(itr2.hasNext() == true)
        {
            itrEdge2 = itr2.next();
            NetworkNode from = ulg.getSource(itrEdge2);
            NetworkNode to = ulg.getDest(itrEdge2);
            uplinkGraph.addVertex(to);
            uplinkGraph.addVertex(from);

            NetworkEdge opposite = g.findEdge(to, from);

            if(opposite != null)
                uplinkGraph.addEdge(opposite, to, from);
        }
        ulg = uplinkGraph;
    }

    /**
     * this function clears up the uplink graph. This function clears up the uplink graph with only the root and all the access points.
     */
    public void ClearUpLinkGraph()
    {
        root = GetRoot();
        Graph<NetworkNode, NetworkEdge> uplinkGraph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();

        // add the root into the new uplink graph
        uplinkGraph.addVertex(root);

        Collection <NetworkNode> nodeCollection = ulg.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        // add the APs and their links to the root into the new link graph
        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if(itrNode.getType().equals("AP") == true)
            {
                uplinkGraph.addVertex(itrNode);
                NetworkEdge e = ulg.findEdge(itrNode, root);

                if(e != null)
                    uplinkGraph.addEdge(e, itrNode, root, EdgeType.DIRECTED);
            }
        }
        ulg = uplinkGraph;
    }

    /**
     * this function updates the reliable uplink graph. Each node in the graph has two outgoing edges. This is a simple version of the algorithm. For more comprehenstive one, please read the technical report
     */
    public void UpdateUplinkGraph()
    {
        boolean graphGrow = false;

        do
        {
            graphGrow = false;
            Collection <NetworkNode> nodeOriginalCollection = g.getVertices();
            Iterator<NetworkNode> originalItr = nodeOriginalCollection.iterator();

            NetworkNode itrNode = null;
            while(originalItr.hasNext() == true)
            {
                itrNode = originalItr.next();

                if((itrNode.getType().equals("GATEWAY") == false) &&
                   (itrNode.getType().equals("AP") == false) && (ulg.containsVertex(itrNode) == false))
                {
                    Collection <NetworkNode> nodeUpLinkCollection = ulg.getVertices();
                    Iterator<NetworkNode> ulgItr = nodeUpLinkCollection.iterator();

                    NetworkNode ulgItrNode = null;
                    Vector <NetworkNode> nodeVector = new Vector<NetworkNode>();
                    int numPrecessor = 0;

                    while(ulgItr.hasNext() == true)
                    {
                        ulgItrNode = ulgItr.next();
                        if(g.isSuccessor(itrNode, ulgItrNode))
                        {
                            numPrecessor ++;
                            nodeVector.add(ulgItrNode);
                        }
                    }

                    // if there is no two outgoing edges from the node
                    if(numPrecessor < 2)
                        continue;
                    else
                    {
                        ulg.addVertex(itrNode);
                        graphGrow = true;

                        for(int i = 0; i < 2; i ++)
                        {
                            NetworkNode childrenNode = nodeVector.elementAt(i);
                            NetworkEdge edge = g.findEdge(itrNode, childrenNode);

                            if(edge != null)
                                ulg.addEdge(edge, itrNode, childrenNode, EdgeType.DIRECTED);
                        }
                    }
                }
                else
                    continue;
            }
        }
        while(graphGrow == true);
    }

    /**
     * this function clears the broadcast graph. This function clears the broadcast graph with only the root and all the access points.
     */
    public void ClearBroadcastGraph()
    {
        root = GetRoot();
        Graph<NetworkNode, NetworkEdge> broadcastGraph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
        broadcastGraph.addVertex(root);

        Collection <NetworkNode> nodeCollection = bg.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if(itrNode.getType().equals("AP") == true)
            {
                broadcastGraph.addVertex(itrNode);
                NetworkEdge e = bg.findEdge(root, itrNode);

                if(e != null)
                    broadcastGraph.addEdge(e, root, itrNode, EdgeType.DIRECTED);
            }
        }
        bg = broadcastGraph;
    }

    /**
     * this function updates the reliable broadcast graph. Each node in the graph has two incoming edges. This is a simple version of the algorithm. For more comprehenstive one, please read the technical report
     */
    public void UpdateBroadcastGraph()
    {
        boolean graphGrow = false;
        do {
            graphGrow = false;
            Collection <NetworkNode> nodeOriginalCollection = g.getVertices();
            Iterator<NetworkNode> originalItr = nodeOriginalCollection.iterator();

            NetworkNode itrNode = null;
            while(originalItr.hasNext() == true)
            {
                itrNode = originalItr.next();

                if((itrNode.getType().equals("GATEWAY") == false) &&
                   (itrNode.getType().equals("AP") == false) && (bg.containsVertex(itrNode) == false))
                {
                    Collection <NetworkNode> nodeBCastCollection = bg.getVertices();
                    Iterator<NetworkNode> bgItr = nodeBCastCollection.iterator();

                    NetworkNode bgItrNode = null;
                    Vector <NetworkNode> nodeVector = new Vector<NetworkNode>();
                    int numPrecessor = 0;

                    while(bgItr.hasNext() == true)
                    {
                        bgItrNode = bgItr.next();
                        if(g.isSuccessor(bgItrNode, itrNode))
                        {
                            numPrecessor ++;
                            nodeVector.add(bgItrNode);
                        }
                    }

                    // if there is no two incoming edges from the node
                    if(numPrecessor < 2)
                        continue;
                    else
                    {
                        bg.addVertex(itrNode);
                        graphGrow = true;

                        for(int i = 0; i < 2; i ++)
                        {
                            NetworkNode parentNode = nodeVector.elementAt(i);
                            NetworkEdge edge = g.findEdge(parentNode, itrNode);
                            if(edge != null)
                            {
                                bg.addEdge(edge, parentNode, itrNode, EdgeType.DIRECTED);
                            }
                        }
                    }
                }
                else
                    continue;
            }

        }while(graphGrow == true);
    }

    /**
     * this function clears the global downlink graph. Only leave the root and the access points in the graph
     */
    public void ClearDownLinkGraph()
    {
        root = GetRoot();
        Graph<NetworkNode, NetworkEdge> downLinkGraph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
        downLinkGraph.addVertex(root);

        Collection <NetworkNode> nodeCollection = dlg.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if(itrNode.getType().equals("AP") == true)
            {
                downLinkGraph.addVertex(itrNode);
                NetworkEdge e = dlg.findEdge(root, itrNode);

                if(e != null)
                    downLinkGraph.addEdge(e, root, itrNode, EdgeType.DIRECTED);
            }
        }
        dlg = downLinkGraph;
    }




    /**
     * this function marks all the vertices in the network.
     */
    public void MarkAllVertices()
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode = null;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            itrNode.SetMark();
        }
    }

    /**
     * this function clears the marks on all the vertices in the network.
     */
    public void ClearAllVertices()
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode = null;

        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            itrNode.ClearMark();
        }
    }

    /*
     * this function delete all the vertices
     */
    public void removeAllVertices()
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();
        NetworkNode itrNode = null;

        Vector<NetworkNode> v_node = new Vector<NetworkNode>();
        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            v_node.add((NetworkNode)itrNode);
        }
            NetworkNode n = null;

        Iterator<NetworkNode> itr_node = v_node.iterator();
        while(itr_node.hasNext())
        {
            itrNode = itr_node.next();
            g.removeVertex((NetworkNode)itrNode);
        }

        v_node.clear();

    }



    /**
     * this function colones a graph
     * @param g the graph to be coloned
     * @return the coloned graph
     */
    public Graph<NetworkNode, NetworkEdge> ColoneGraph(Graph<NetworkNode, NetworkEdge> g)
    {
        Graph<NetworkNode, NetworkEdge> coloneGraph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();

        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            coloneGraph.addVertex(itrNode);
        }

        Collection <NetworkEdge> edgeCollection = g.getEdges();
        Iterator<NetworkEdge> edgeItr = edgeCollection.iterator();

        NetworkEdge itrEdge = null;
        while(edgeItr.hasNext() == true)
        {
            itrEdge = edgeItr.next();
            coloneGraph.addEdge(itrEdge, g.getSource(itrEdge), g.getDest(itrEdge), EdgeType.DIRECTED);
        }
        return coloneGraph;
    }

    /**
     * this function snapshots the uplink graph
     */
    public void SnapShotUpLinkGraph()
    {
        this.ulgSnapShot = ColoneGraph(this.ulg);
        CompareUpLinkGraph();
    }

    /**
     * this function compares the current uplink graph and its snapshot in the network manager.
     * @return a summary of the comparison in the string format
     */
    public String CompareUpLinkGraph()
    {
        String resultString = "";
        resultString += "The graph information of the Uplink Graph \n\n";

        if(this.ulg != null)
            resultString += this.ulg.toString() + "\n\n";

        resultString += "The graph information of the Uplink Graph SnapShot \n\n";

        if(this.ulgSnapShot != null)
            resultString += this.ulgSnapShot.toString() + "\n\n";
        return resultString;
    }

    /**
     * this function keeps a snapshot of the broadcast graph
     */
    public void SnapShotBroadcastGraph()
    {
        this.bgSnapShot = ColoneGraph(this.bg);
        this.CompareBroadcastGraph();
    }

    /**
     * this function compares the current broadcast graph and its snapshot in the network manager.
     * @return a summary of the comparison in the string format
     */
    public String CompareBroadcastGraph()
    {
        String resultString = "";
        resultString += "The graph information of the Broadcast Graph \n\n";

        if(this.bg != null)
            resultString += this.bg.toString() + "\n\n";

        resultString += "The graph information of the Broadcast Graph SnapShot \n\n";

        if(this.bgSnapShot != null)
            resultString += this.bgSnapShot.toString() + "\n\n";
        return resultString;
    }

    /**
     * this function keeps a snapshot of the downlink graph for each node in the network topology
     */
    public void SnapShotDownLinkGraph()
    {
        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> itr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(itr.hasNext() == true)
        {
            itrNode = itr.next();
            if((itrNode.getType().equals("AP") == false) && (itrNode.getType().equals("GATEWAY") == false))
            {
        //        Graph<GUINetworkNode, GUINetworkEdge> snapShotGraph = ColoneGraph(itrNode.getNetworkGraphFromGW().getGraph());
         /**********************************************why here!!!!!!***************/
//                itrNode.SetDownLinkGraphSnapShot(snapShotGraph);
            }
        }
    }

    /**
     * this function compares the current downlink graph of the given network node with its downlink graph snapshot
     * @param nickName the nickname of the network node to compare
     * @return the summary of the comparison in the string format
     */
    
    /*
    public String CompareDownLinkGraph(char nickName)
    {
        String resultString = "";
        NetworkNode node = this.GetNetworkNode(nickName);

        if(node == null)
            return resultString;

        resultString += "The information of the Downlink Graph \n\n";

//        if(node.getNetworkGraphFromGW().getGraph() != null);;
//        {
//            resultString += node.getNetworkGraphFromGW().getGraph().toString();
//            resultString += "\n\n";
//        }


        resultString += "The graph information of the Downlink Graph SnapShot \n\n";

        if(node.GetDownLinkGraphSnapShot() != null)
        {
            resultString += node.GetDownLinkGraphSnapShot().toString();
            resultString += "\n\n";
        }

        return resultString;
    }
*/

    /**
     * this function generates the shortest path from the source node to the destination node
     * @param src the source node
     * @param dest the destination node
     * @return the shortest path from the source node to the destination node
     */
    public ArrayList GenShortestPath(NetworkNode src, NetworkNode dest)
    {
        ArrayList srcRouteList = new ArrayList();
        srcRouteList.add(src.getNodeNickname());

        Transformer<NetworkEdge, Double> wtTransformer = new Transformer<NetworkEdge,Double>() {
            public Double transform(NetworkEdge e) {
                if(e.calcLinkQuality() == 0)
                    return 1000.0; // set a big value
                else
                    return (1 / e.calcLinkQuality());
            }
        };

        // apply the DijkstraShortestPath algorithm to construct the shortest path
        DijkstraShortestPath<NetworkNode,NetworkEdge> alg = new DijkstraShortestPath(g, wtTransformer);
        List<NetworkEdge> l = alg.getPath(src, dest);

        for(int i = 0; i < l.size(); i ++)
        {
            NetworkEdge e = l.get(i);
            srcRouteList.add(g.getDest(e).getNodeNickname());
        }
        return srcRouteList;
    }

    /**
     * this function converts the source route list to the char array
     * @param srcRouteList the source route list
     * @return the source route list in the char array format
     */
    public char[] GenSrcRouteCharArray(ArrayList srcRouteList)
    {
        char [] srcRoute = new char[srcRouteList.size()];

        for(int i = 0; i < srcRouteList.size(); i ++)
            srcRoute [i] = ((NodeNickname) (srcRouteList.get(i))).GetNickName();

        return srcRoute;
    }

    /**
     * this function constructs a nmSourceRouteEntry instance according to the list of source route
     * @param srcRouteList the source route list
     * @return a nmSourceRouteEntry instance
     */
    public nmSourceRouteEntry GenSrcRouteEntry(ArrayList srcRouteList)
    {
        char [] srcRoute1 = null;
        char [] srcRoute2 = null;

        if(srcRouteList.size() <= 4)
        {
            srcRoute1 = new char [4];
            srcRoute2 = new char [4];
            srcRoute1 [0] = srcRoute1 [1] = srcRoute1 [2] = srcRoute1 [3] = 0xFFFF;
            srcRoute2 [0] = srcRoute2 [1] = srcRoute2 [2] = srcRoute2 [3] = 0xFFFF;

            for(int i = 0; i < srcRouteList.size(); i ++)
                srcRoute1 [i] = ((NodeNickname) (srcRouteList.get(i))).GetNickName();
        }
        else if((srcRouteList.size() > 4) && (srcRouteList.size() <= 8))
        {
            srcRoute1 = new char [4];
            srcRoute2 = new char [4];
            srcRoute1 [0] = srcRoute1 [1] = srcRoute1 [2] = srcRoute1 [3] = 0xFFFF;
            srcRoute2 [0] = srcRoute2 [1] = srcRoute2 [2] = srcRoute2 [3] = 0xFFFF;

            for(int i = 0; i < 4; i ++)
                srcRoute1 [i] = ((NodeNickname) (srcRouteList.get(i))).GetNickName();

            for(int i = 4; i < srcRouteList.size(); i ++)
                srcRoute2 [i-4] = ((NodeNickname) (srcRouteList.get(i))).GetNickName();
        }
        else
            return null;

        return new nmSourceRouteEntry(srcRoute1, srcRoute2);
    }

    /**
     * this function gets the number of devices in the network. Excludes the gateway and APs
     * @return the the number of devices in the network. Excludes the gateway and APs
     */
    public int GetNumOfDevices()
    {
        int numOfDev = 0;

        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if((itrNode.getType().equals("AP") == false) && (itrNode.getType().equals("GATEWAY") == false))
                numOfDev ++;
        }
        return numOfDev;
    }

    /**
     * this function gets the number of devices and APs in the network
     * @return the number of devices and APs in the network
     */
    public int GetNumOfDeviceAP()
    {
        int numOfDev = 0;

        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if((itrNode.getType().equals("GATEWAY") == false))
                numOfDev ++;
        }
        return numOfDev;
    }

    /**
     * this function gets the graph by the given graph ID
     * @param graphId the ID of the graph to look for
     * @return the graph with the given graph ID
     */
    public Graph<NetworkNode, NetworkEdge> GetGraphByID(char graphId)
    {
        Graph<NetworkNode, NetworkEdge> graph = null;

        // check if it is the uplink graph
        if((this.ulg != null) && (graphId == this.GetUpLinkGraphID()))
            graph = this.ulg;
        // check if it is the broadcast graph
        else if((this.bg != null) && (graphId == this.GetBCastGraphID()))
            graph = this.bg;
        else
        {
            // check the downlink graphs
            Collection <NetworkNode> nodeCollection = g.getVertices();
            Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

            NetworkNode itrNode = null;
            while(nodeItr.hasNext() == true)
            {
                itrNode = nodeItr.next();
/*
                if((itrNode.getFromGateWayGraph() != null) && (itrNode.getNetworkGraphFromGW().getGraphID() == (int)graphId))
                    graph = itrNode.getFromGateWayGraph();
                    * */
                /************************* aaaaaaaaaaaaaaaaaaaa *********/
            }
        }
        return graph;
    }

    /**
     * this function adds a NodeUniqueID to the white list.
     * @param uid the node unique ID
     */
    public void AddItemToDeviceWhiteList(NodeUniqueID uid)
    {
        for(int i = 0; i < this.whiteList.size(); i ++)
        {
            if(this.whiteList.get(i).equals(uid))
                return;
        }

        // if the NodeUniqueID is not in the device white list, add it.
        this.whiteList.add(uid);
    }

     /**
     * this function adds a NodeUniqueID to the device black list.
     * @param uid the node unique ID
     */
    public void AddItemToDeviceBlackList(NodeUniqueID uid)
    {
        for(int i = 0; i < this.blackList.size(); i ++)
        {
            if(this.blackList.get(i).equals(uid))
                return;
        }
        // if the NodeUniqueID is not in the device black list, add it.
        this.blackList.add(uid);
    }

    /**
     * this function removes a nodeunique ID from the device white list
     * @param uid the device node unique ID
     */
    public void RmItemFromDeviceWhiteList(NodeUniqueID uid)
    {
        for(int i = 0; i < this.whiteList.size(); i ++)
        {
            if(this.whiteList.get(i).equals(uid))
                this.whiteList.remove(i);
        }
    }

    /**
     * this function removes a nodeunique ID from the device black list
     * @param uid the device node unique ID
     */
    public void RmItemFromDeviceBlackList(NodeUniqueID uid)
    {
        for(int i = 0; i < this.blackList.size(); i ++)
        {
            if(this.blackList.get(i).equals(uid))
                this.blackList.remove(i);
        }
    }

    public void RmAllItemFromDeviceWhiteList()
    {
        this.whiteList.clear();
    }

    public void RmAllItemFromDeviceBlackList()
    {
        for(int i = 0; i < this.blackList.size(); i ++)
        {
               this.blackList.clear();
        }
    }

    /**
     * this function generates the active device list
     * @return the active device list
     */
    public ArrayList<NodeUniqueID> GenActiveList()
    {
        ArrayList<NodeUniqueID> list = new ArrayList<NodeUniqueID>();

        Collection <NetworkNode> nodeCollection = g.getVertices();
        Iterator<NetworkNode> nodeItr = nodeCollection.iterator();

        NetworkNode itrNode = null;
        while(nodeItr.hasNext() == true)
        {
            itrNode = nodeItr.next();

            if(itrNode.getType().equals("GATEWAY") == false)
                list.add(itrNode.getNodeUniqueID());
        }
        return list;
    }

    /**
     * Updates a vertex on all graphs (graph, uplink, downlink, bcast). Assume a vertex must exist on all graphs.
     * Assumes that the NodeUniqueID of the vertex does not change.
     * We get the vertex from the graph using the uniqueID, then copy all the new properties of the vertex.
     * @param newVertex
     */
    public void updateVertex(NetworkNode newVertex)
    {
        NetworkNode temp;
        Collection<NetworkNode> vertexList;
        Iterator<NetworkNode> itr;

        //Update the vertex in all graphs:
        //General graph
        if (g != null)
        {
            vertexList = g.getVertices();

            itr = vertexList.iterator();
            while (itr.hasNext() == true)
            {
                temp = itr.next();
                if (temp.getNodeUniqueID().equals(newVertex.getNodeUniqueID()))
                    copyVertexProperties(newVertex, temp);
            }
        }

        //Uplink graph
        if (ulg != null)
        {
            vertexList = ulg.getVertices();
            itr = vertexList.iterator();
            while (itr.hasNext() == true)
            {
                temp = itr.next();
                if (temp.getNodeUniqueID().equals(newVertex.getNodeUniqueID()))
                    copyVertexProperties(newVertex, temp);
            }
        }

        //Downlink graph
        if (dlg != null)
        {
            vertexList = dlg.getVertices();
            itr = vertexList.iterator();
            while (itr.hasNext() == true)
            {
                temp = itr.next();
                if (temp.getNodeUniqueID().equals(newVertex.getNodeUniqueID()))
                    copyVertexProperties(newVertex, temp);
            }
        }

        //Broadcast graph
        if (bg != null)
        {
            vertexList = bg.getVertices();
            itr = vertexList.iterator();
            while (itr.hasNext() == true)
            {
                temp = itr.next();
                if (temp.getNodeUniqueID().equals(newVertex.getNodeUniqueID()))
                    copyVertexProperties(newVertex, temp);
            }
        }
    } //end update vertex

    //copy properties from src into dst
    private void copyVertexProperties(NetworkNode srcVertex, NetworkNode dstVertex)
    {
        NodeType newType = null;
        String str = srcVertex.getType();

        //first get the correct NodeType:
        if (str.equals("ABSTRACT"))
            newType = NodeType.ABSTRACT;
        else if (str.equals("AP"))
            newType = NodeType.AP;
        else if (str.equals("DEVICE"))
            newType = NodeType.DEVICE;
        else if (str.equals("ROUTER"))
            newType = NodeType.ROUTER;
        else if (str.equals("ROUTER"))
            newType = NodeType.ROUTER;
        else if (str.equals("GATEWAY"))
            newType = NodeType.GATEWAY;

        //copy properties into the vertex
        dstVertex.setNodeNickname(srcVertex.getNodeNickname());
        dstVertex.setType(newType);
        //do we need to set ID? there is no "GUINetworkNode.getID()", so we would have to add this to GUINetworkNode first.
        dstVertex.setScanPeriod(srcVertex.getScanPeriod());
    }


     /**
     * updates an edge on a graph.
     * @param graphType 0 = general graph, 1 = uplink, 2 = bcast, 3 = downlink
     * @param newEdge the new edge data that will be copied into the graph's current edge
     * @param srcVertex source vertex of the edge
     * @param dstVertex destination vertex of edge
     */
    public void updateEdge(int graphType, NetworkEdge newEdge, NetworkNode srcVertex, NetworkNode dstVertex)
    {
        Graph myGraph = null;
        switch (graphType)
        {
            case 0:
                myGraph = g;  //general graph
                break;
            case 1:
                myGraph = ulg; //uplink graph
                break;
            case 2:
                myGraph = bg; //broadcast graph
                break;
            case 3:
                myGraph = dlg; //downlink graph
                break;
        }

        if (myGraph == null)
            System.out.println("DEBUG: GUIToplogy, updateEdge(), invalid graph type");

        NetworkEdge temp = (NetworkEdge) myGraph.findEdge(srcVertex, dstVertex);

        if (temp == null)
            System.out.println("DEBUG GUITopology, updateEdge, no edge found");

        //Otherwise we have a valid edge. copy all the properties of the new edge onto the current edge:
        temp.setSignalStrength(newEdge.getSignalStrength()); //set the signal strength
        //any other properties to set? currently add edge only uses signal strength.

    }//end update edge

} //end GUITopology class



