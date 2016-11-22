package manager.guiApp;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.commons.collections15.Transformer;

/**
 * this class defines a network graph. It is used for the downlink graph in the wirelesshart network
 * @author Hang Yu
 */

public class GUINetworkGraph {

    // data structure
    int graphId;
    // the source and destination nodes of the graph
    NetworkNode srcNode;
    NetworkNode destNode;
    Graph<NetworkNode, NetworkEdge> graph;
    Vector<NetworkNode> circleNodes;

    // the root of the graph
    GUIGraphNode root;
    // the root of the graph if the graph node, else is network node
    
    
    // the original network topology
    GUITopology topology;
    // the average hops from the source to the destination
    double avgLength;

    // graph generation restriction
    double minSignalLimit;
    int maxHops;

    /**
     * this function finalize the network graph
     */
    @Override
    protected void finalize()
    {
        graph = null;
        circleNodes = null;
    }

    /**
     * constructor function. It creates a network graph instance with the given parameters
     * @param id the graph ID
     * @param s the source node of the graph
     * @param d the destination node of the graph
     * @param topo the original complete network topology
     */
    public GUINetworkGraph(int id, NetworkNode s, NetworkNode d, GUITopology topo)
    {
        graphId = id;
        srcNode = s;
        destNode = d;
        topology = topo;
        root = new GUIGraphNode(id, s, null);
        graph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
        circleNodes = new Vector<NetworkNode>();

        // set the parameters with the default values
        this.avgLength = 0.0;
        this.minSignalLimit = -15;
        this.maxHops = 4;
    }

    /**
     * constructor function. It creates a network graph instance with the given parameters
      * @param id the graph ID
     * @param s the source node of the graph
     * @param d the destination node of the graph
     * @param topo the original complete network topology
     * @param signalLimit the restriction on the signal strength
     * @param hopLimit the restriction on the hop number
     */
    public GUINetworkGraph(int id, NetworkNode s, NetworkNode d, GUITopology topo, double signalLimit, int hopLimit)
    {
        graphId = id;
        srcNode = s;
        destNode = d;
        topology = topo;
        root = new GUIGraphNode(id, s, null);
        graph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();
        circleNodes = new Vector<NetworkNode>();

        this.avgLength = 0.0;
        this.minSignalLimit = signalLimit;
        this.maxHops = hopLimit;
    }

    /**
     * this function display the graph information in the string format
     * @return the graph information in the string format
     */
    @Override
    public String toString()
    {
        return graph.toString();
    }

    /**
     * this function gets the average hops from the source to the destination in the graph
     * @return the average hops from the source to the destination in the graph
     */
    public double getAvgLength()
    {
        return this.avgLength;
    }

    /**
     * this function sets the average hops from the source to the destination in the graph
     * @param l the average hops from the source to the destination in the graph
     */
    public void setAvgLength(double l)
    {
        this.avgLength = l;
    }

    /**
     * this function gets the graph ID
     * @return the graph ID
     */
    public int getGraphID()
    {
        return graphId;
    }

    /**
     * this function sets the graph ID
     * @param id the graph ID
     */
    public void setGraphID(int id)
    {
        graphId = id;
    }

    /**
     * this function gets the source node of the graph
     * @return the source node of the graph
     */
    public NetworkNode getSrc()
    {
        return srcNode;
    }

    /**
     * this function sets the source node of the graph
     * @param s the source node of the graph
     */
    public void setSrc(NetworkNode s)
    {
        srcNode = s;
    }

    /**
     * this function gets the destination node of the graph
     * @return the destination node of the graph
     */
    public NetworkNode getDest()
    {
        return destNode;
    }

    /**
     * this function sets the destination node of the graph
     * @param d the destination node of the graph
     */
    public void setDest(NetworkNode d)
    {
        destNode = d;
    }

    /**
     * this function gets the root of the network graph
     * @return the root of the network graph
     */
    public GUIGraphNode getRoot()
    {
        return root;
    }

    /**
     * this function gets the network graph structure
     * @return the network graph structure
     */
    public Graph<NetworkNode, NetworkEdge> getGraph()
    {
        return graph;
    }

    /**
     * this function searches and prunes the network topology and only leave the nodes and edges that are on a certain path from the source to the destination.
     */
    public void SearchingandPruningGraph()
    {
        root.BuildGraphTo(graphId, destNode, topology, this.minSignalLimit, this.maxHops);
        graph.addVertex(root.getNetworkNode());
        root.AddVertexEdgeIntoGraph(graph, topology);        
    }

    /**
     * this function builds the K shortest path from the source to the destination. K = 2
     * @param layout the layout of the graph
     */
    public void BuildKShortestPath(Layout<NetworkNode, NetworkEdge> layout)
    {
        // a new transformer to calculate the link's weight
        Transformer<NetworkEdge, Double> wtTransformer = new Transformer<NetworkEdge,Double>() {
            public Double transform(NetworkEdge e) {
                if(e.calcLinkQuality() == 0)
                    return 1000.0; // set a big value
                else
                    return (1 / e.calcLinkQuality());
            }
        };

        // use the DijkstraShortestPath algorithm to construct the shortest path from the source to the destination
        DijkstraShortestPath<NetworkNode,NetworkEdge> alg = new DijkstraShortestPath(topology.g, wtTransformer);
        List<NetworkEdge> l = alg.getPath(srcNode, destNode);

        // update graph according the result from the graph
        for(int i = 0; i < l.size(); i ++)
        {
            NetworkEdge e = l.get(i);
            NetworkNode src = topology.g.getSource(e);
            NetworkNode dest = topology.g.getDest(e);

            graph.addVertex(src);
            graph.addVertex(dest);
            graph.addEdge(e, src, dest);

            // manipulate the signal strength on the selected edge
            if((src.getType().equals("AP") && dest.getType().equals("GATEWAY"))
            || (src.getType().equals("GATEWAY") && dest.getType().equals("AP")))
                ;
            else
            {
                e.setSwapSignalStrength(e.getSignalStrength());
                e.setSignalStrength(-100);
                e.SetSignalStrengthSwapped();
            }
        }

        // run the DijkstraShortestPath algorithm again
        DijkstraShortestPath<NetworkNode,NetworkEdge> alg2 = new DijkstraShortestPath(topology.g, wtTransformer);
        List<NetworkEdge> l2 = alg2.getPath(srcNode, destNode);

        // update the network graph
        for(int j = 0; j < l2.size(); j ++)
        {
            NetworkEdge e = l2.get(j);
            NetworkNode src = topology.g.getSource(e);
            NetworkNode dest = topology.g.getDest(e);

            graph.addVertex(src);
            graph.addVertex(dest);
            graph.addEdge(e, src, dest);
        } 

        // restore all the signal strength back
        //topology.reEvaluateAllSignalStrength(layout);
        topology.reStoreAllSignalStrength();
    }

    
    /**
     * this function gets the shared nodes and edges from this graph and the downlink graph of a given network node
     * @param node1 the given node
     */
    /*
    public void BuildTreeGraph(NetworkNode node1)
    {
        Collection <NetworkNode> node1Collection = node1.getNetworkGraphFromGW().getGraph().getVertices();
        Iterator<NetworkNode> node1Itr = node1Collection.iterator();

        NetworkNode itr1Node = null;
        while(node1Itr.hasNext() == true)
        {
            itr1Node = node1Itr.next();
            if(graph.containsVertex(itr1Node) == false)
                graph.addVertex(itr1Node);
        }

        Collection <GUINetworkEdge> edge1Collection = node1.getNetworkGraphFromGW().getGraph().getEdges();
        Iterator<GUINetworkEdge> edge1Itr = edge1Collection.iterator();

        GUINetworkEdge itr1Edge = null;
        while(edge1Itr.hasNext() == true)
        {
            itr1Edge = edge1Itr.next();
            if(graph.containsEdge(itr1Edge) == false)
                graph.addEdge(itr1Edge, node1.getNetworkGraphFromGW().getGraph().getSource(itr1Edge), node1.getNetworkGraphFromGW().getGraph().getDest(itr1Edge), EdgeType.DIRECTED);
        }

        GUINetworkEdge e1 = topology.g.findEdge(node1, this.destNode);

        if(e1 != null)
            graph.addEdge(e1, node1, this.destNode, EdgeType.DIRECTED);
    }
*/
    /**
     * this function constructs the reliable downlink graph with the given two nodes as the parents.
     * For the details of this function, please refer to the algorithms in the following technical report:
     * Song Han, Xiuming Zhu, Deji Chen, Aloysius K. Mok, Mark Nixon,
     * "Reliable and Real-time Communication in Industrial Wireless Mesh Networks"
     * in proceedings of the 17th IEEE Real-Time and Embedded Technology and Applications Symposium (RTAS),
     * 3-12, Chicago, IL, 2011.
     * @param node1 the first parent node
     * @param node2 the second parent node
     */
    /*
    public void BuildReliableGraph(NetworkNode node1, NetworkNode node2)
    {
        Graph<NetworkNode, GUINetworkEdge> markedGraph = new DirectedSparseMultigraph<NetworkNode, GUINetworkEdge>();

        // First build the redundant graph
        Collection <NetworkNode> node1Collection = node1.getNetworkGraphFromGW().getGraph().getVertices();
        Iterator<NetworkNode> node1Itr = node1Collection.iterator();

        NetworkNode itr1Node = null;
        while(node1Itr.hasNext() == true)
        {
            itr1Node = node1Itr.next();
            if(graph.containsVertex(itr1Node) == false)
                graph.addVertex(itr1Node);
        }

        Collection <NetworkNode> node2Collection = node2.getNetworkGraphFromGW().getGraph().getVertices();
        Iterator<NetworkNode> node2Itr = node2Collection.iterator();

        NetworkNode itr2Node = null;
        while(node2Itr.hasNext() == true)
        {
            itr2Node = node2Itr.next();
            if(graph.containsVertex(itr2Node) == false)
                graph.addVertex(itr2Node);
        }

        Collection <GUINetworkEdge> edge1Collection = node1.getNetworkGraphFromGW().getGraph().getEdges();
        Iterator<GUINetworkEdge> edge1Itr = edge1Collection.iterator();

        GUINetworkEdge itr1Edge = null;
        while(edge1Itr.hasNext() == true)
        {
            itr1Edge = edge1Itr.next();
            if(graph.containsEdge(itr1Edge) == false)
                graph.addEdge(itr1Edge, node1.getNetworkGraphFromGW().getGraph().getSource(itr1Edge), node1.getNetworkGraphFromGW().getGraph().getDest(itr1Edge), EdgeType.DIRECTED);
        }

        Collection <GUINetworkEdge> edge2Collection = node2.getNetworkGraphFromGW().getGraph().getEdges();
        Iterator<GUINetworkEdge> edge2Itr = edge2Collection.iterator();

        GUINetworkEdge itr2Edge = null;
        while(edge2Itr.hasNext() == true)
        {
            itr2Edge = edge2Itr.next();
            if(graph.containsEdge(itr2Edge) == false)
                graph.addEdge(itr2Edge, node2.getNetworkGraphFromGW().getGraph().getSource(itr2Edge), node2.getNetworkGraphFromGW().getGraph().getDest(itr2Edge), EdgeType.DIRECTED);
        }

        GUINetworkEdge e1 = topology.g.findEdge(node1, this.destNode);
        GUINetworkEdge e2 = topology.g.findEdge(node2, this.destNode);

        if(e1 != null)
            graph.addEdge(e1, node1, this.destNode, EdgeType.DIRECTED);
        if(e2 != null)
            graph.addEdge(e2, node2, this.destNode, EdgeType.DIRECTED);

        GUINetworkEdge e3 = topology.g.findEdge(node1, node2);
        GUINetworkEdge e4 = topology.g.findEdge(node2, node1);

        if(e3 != null)
            graph.addEdge(e3, node1, node2, EdgeType.DIRECTED);
        if(e4 != null)
            graph.addEdge(e4, node2, node1, EdgeType.DIRECTED);

        // then prune this graph
        topology.ClearAllVertices();
        this.destNode.SetMark();
        node1.SetMark();
        node2.SetMark();
        markedGraph.addVertex(this.destNode);
        markedGraph.addVertex(node1);
        markedGraph.addVertex(node2);

        // add node1 and node2 into the circleNodes
        this.circleNodes.add(node1);
        this.circleNodes.add(node2);

        boolean graphGrow = false;
        do {
            graphGrow = false;
            Collection <NetworkNode> nodeOriginalCollection = graph.getVertices();
            Iterator<NetworkNode> originalItr = nodeOriginalCollection.iterator();

            NetworkNode itrNode = null;
            while(originalItr.hasNext() == true)
            {
                itrNode = originalItr.next();

                if((itrNode.getType().equals("GATEWAY") == false) && itrNode.getID() != this.destNode.getID())
                {
                    Collection <NetworkNode> nodeCollection = graph.getVertices();
                    Iterator<NetworkNode> itr = nodeCollection.iterator();

                    NetworkNode detectItrNode = null;
                    Vector <NetworkNode> nodeVector = new Vector<NetworkNode>();
                    int numSuccessor = 0;
                    int numMarkedSuccessor = 0;

                    while(itr.hasNext() == true)
                    {
                        detectItrNode = itr.next();
                        if(graph.isSuccessor(itrNode, detectItrNode))
                        {
                            numSuccessor ++;

                            if(detectItrNode.IsMarked())
                            {
                                nodeVector.add(0, detectItrNode);
                                numMarkedSuccessor ++;
                            }
                            else
                                nodeVector.add(detectItrNode);
                        }
                    }

                    if((numMarkedSuccessor < 2) && (itrNode.getType().equals("AP") == false))
                        continue;
                    else
                    {
                        for(int i = 2; i < nodeVector.size(); i ++)
                        {
                            NetworkNode tmpNode = nodeVector.elementAt(i);
                            GUINetworkEdge tmpEdge = graph.findEdge(itrNode, tmpNode);

                            if(tmpEdge != null)
                                graph.removeEdge(tmpEdge);

                            if(itrNode.IsMarked() == false)
                            {
                                markedGraph.addVertex(itrNode);
                                graphGrow = true;
                                itrNode.SetMark();
                            }
                        }
                    }
                }
                else
                    continue;
            }
        }
        while(graphGrow == true);
        topology.ClearAllVertices();

        List delList = new ArrayList();
        boolean finished = true;

        do {
            finished = true;
            Collection <NetworkNode> nodeOriginalCollection = graph.getVertices();
            Iterator<NetworkNode> originalItr = nodeOriginalCollection.iterator();


            NetworkNode itrNode = null;
            while(originalItr.hasNext() == true)
            {
                itrNode = originalItr.next();
                if((graph.getPredecessorCount(itrNode) == 0) && (itrNode.getType().equals("GATEWAY") == false))
                {
                    delList.add(itrNode);
                    finished = false;
                }
            }
        }
        while(finished == false);

        for(int i = 0; i < delList.size(); i ++)
        {
            graph.removeVertex((NetworkNode)delList.get(i));
        }
    }
     */

    /**
     * this function searches the graph node associated with the given network node in the GraphNode r's downlink graph
     * @param r the root of the downlink graph.
     * @param node the node who is associated with a graph node that is the search target
     * @return the graph node associated with the given network node. null if the search fails.
     */
    public GUIGraphNode FindGraphNode(GUIGraphNode r, NetworkNode node)
    {
        GUIGraphNode target = null;
        if(r.getNetworkNode().getID() == node.getID())
            return r;
        else
        {
            for(GUIGraphNode gn : r.getChildren())
            {
                if((target = FindGraphNode(gn, node)) != null)
                    return target;
            }
            return null;
        }
    }
}
