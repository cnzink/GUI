package manager.guiApp;

import edu.uci.ics.jung.graph.Graph;
import java.util.Collection;
import java.util.HashSet;

/**
 * this class defines the node structure in a graph
 * @author Hang Yu
 */
public class GUIGraphNode {

    // the graph ID
    int graphId;
    // the network node associated with this graph node
    NetworkNode node;
    // its parent node in the graph
    GUIGraphNode parent;
    // its children in the graph
    HashSet<GUIGraphNode> children;

    /**
     * constructor function. It creates an instance of the GraphNode with the given parameters
     * @param id the graph ID
     * @param v the network node associated with this graph node
     * @param p its parent in the graph
     */
    public GUIGraphNode(int id, NetworkNode v, GUIGraphNode p)
    {
        graphId = id;
        node = v;
        parent = p;
        children = new HashSet<GUIGraphNode>();
    }

    /**
     * this function adds the vertex and edges into the given graph in a recursive way
     * @param g the graph to add vertex and edges
     * @param topo the network topology which includes the complete network topology
     */
    public void AddVertexEdgeIntoGraph(Graph<NetworkNode, NetworkEdge> g, GUITopology topo)
    {
        for(GUIGraphNode gn : children)
        {
            NetworkNode v = gn.getNetworkNode();
            g.addVertex(v);
            NetworkEdge e = topo.g.findEdge(this.node, v);
            g.addEdge(e, this.node, v);

            // recursively add the vertex and edges for the children
            gn.AddVertexEdgeIntoGraph(g, topo);
        }     
    }

    /**
     * this function creates a graph from this graph node to the given destination based on the given restrictions on the number of hop limit and the singal strength limit
     * @param graphId the ID of the created graph
     * @param destNode the destination of the graph
     * @param topo the complete network topology
     * @param minSignalLimit the signal strength limit
     * @param maxHops the max hop limit
     * @return this graph node if the construction is successful. Otherwise false.
     */
    public GUIGraphNode BuildGraphTo(int graphId, NetworkNode destNode, GUITopology topo, double minSignalLimit, int maxHops) {

        if(maxHops < 0)
            return null;
        else if(this.node.getID() == destNode.getID())
            return this;

        Collection<NetworkNode> neighbors = topo.g.getSuccessors(node);

        if(neighbors == null)
            return null;
        else
        {
              for(NetworkNode v : neighbors)
              {
                  NetworkEdge e = topo.g.findEdge(this.node, v);

                  if((e == null) || (e.getSignalStrength() <= minSignalLimit))
                      continue;

                  if(AlreadyInPathFromGateway(v, this))
                      continue;

                  GUIGraphNode childNode = new GUIGraphNode(graphId , v, this);
                  GUIGraphNode childGraph = childNode.BuildGraphTo(graphId , destNode, topo, minSignalLimit , maxHops - 1);

                  if (childGraph != null)
                  {
                      children.add(childGraph);
                  }
              }

              if(children.size() > 0)
                  return this;
        }
        return null;        
    }

    /**
     * this function checks if the network node is already in the path from the gateway to the given graph node
     * @param node the network node to be checked
     * @param gn the destination graph node
     * @return true if the given network node is already in the path from the gateway to the given graph node
     */
    public boolean AlreadyInPathFromGateway(NetworkNode node, GUIGraphNode gn)
    {
        // check this property in the reversed order from the destination to the Gateway
        if(node.getID() == gn.node.getID())
            return true;
        else if(gn.getParentGraphNode() != null)
        {
            return AlreadyInPathFromGateway(node, gn.getParentGraphNode());
        }
        else
            return false;
    }

    /**
     * this function gets the graph id associated with this graph node
     * @return the graph id associated with this graph node
     */
    public int getGraphID()
    {
        return graphId;
    }

    /**
     * this function sets the graph id associated with this graph node
     * @param id the graph id associated with this graph node
     */
    public void setGraphID(int id)
    {
        graphId = id;
    }

    /**
     * this function gets the network node associated with this graph node
     * @return the network node associated with this graph node
     */
    public NetworkNode getNetworkNode()
    {
        return node;
    }

    /**
     * this function sets the network node associated with this graph node
     * @param v the network node associated with this graph node
     */
    public void setNetworkNode(NetworkNode v)
    {
        node = v;
    }

    /**
     * this function gets the parent graph node of this graph node
     * @return the parent graph node of this graph node
     */
    public GUIGraphNode getParentGraphNode()
    {
        return parent;
    }

    /**
     * this function sets the parent graph node for this graph node
     * @param p the parent graph node for this graph node
     */
    public void setParentGraphNode(GUIGraphNode p)
    {
        parent = p;
    }

    /**
     * this function gets the children of the graph node
     * @return the children of the graph node
     */
    public Collection<GUIGraphNode> getChildren()
    {
        return children;
    }
}
