package manager.gui;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import manager.guiApp.NetworkEdge;
import manager.guiApp.NetworkNode;
import manager.guiApp.GUITopology;

/**
 * this class defines the tree controller for the devices in the network
 * @author Song Han
 */
class TreeController
{
    private GUITopology topology;
    private DefaultMutableTreeNode root;
    private JTree tree;
    DefaultTreeModel model;

    /**
     * Constructor function. This function sets the network topology and initialize the controller using default values
     */
    public TreeController(GUITopology topo)
    {
        root = new DefaultMutableTreeNode("Devices in the Mesh Simulator");
        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true);
        topology = topo;
    }

    /**
     * this function gets the tree structure of the devices
     * @return the tree structure
     */
    public JTree getTree()
    {
        return tree;
    }

    /**
     * this function sets the network topology
     * @param topo the network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        LoadTree();
    }

    /**
     * this function reloads the tree structure
     */
    public void LoadTree()
    {
        root.removeAllChildren();

        Collection <NetworkNode> vertexCollection = topology.GetGraph().getVertices();
        Iterator<NetworkNode> itr = vertexCollection.iterator();
        Iterator <NetworkEdge> itr2;

        NetworkNode itrNode;
        NetworkEdge itrEdge;
        
        DefaultMutableTreeNode treeVertexNode;
        DefaultMutableTreeNode treeEdgeNode;

        while(itr.hasNext()  == true)
        {
            // add a tree node for each device
            itrNode = itr.next();
            treeVertexNode = new DefaultMutableTreeNode(itrNode.toString());
            root.add(treeVertexNode);

            // add all the incident edges of the node as the childrens of the device
            Collection <NetworkEdge> edgeCollection = topology.GetGraph().getIncidentEdges(itrNode);
            itr2 = edgeCollection.iterator();
            
            while(itr2.hasNext() == true)
            {
                itrEdge = itr2.next();
                treeEdgeNode = new DefaultMutableTreeNode(itrEdge.summary());
                treeVertexNode.add(treeEdgeNode);
            }
        }
        model.reload();
    }
}
