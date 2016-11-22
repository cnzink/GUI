package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import manager.guiApp.NetworkNode;
/**
 * The delete vertex menu item on the pop up mouse menu when right click on the node
 * @author Song Han
 * @param <GUINetworkNode> the network node class
 */
public class DeleteVertexMenuItem<GUINetworkNode> extends JMenuItem implements VertexMenuListener<GUINetworkNode>
{
    private GUINetworkNode vertex;
    private VisualizationViewer visComp;
    /**
     * Constructor function
     */
    public DeleteVertexMenuItem()
    {
        super("Delete Vertex");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // de-select and remove the vertex from the visualization viewer
                visComp.getPickedVertexState().pick(vertex, false);
                visComp.getGraphLayout().getGraph().removeVertex(vertex);
                visComp.repaint();
            }
        });
    }

    /**
     * This function sets the vertex and the visualization viewer
     * @param v the vertex reference
     * @param visComp the visualization viewer
     */
    public void setVertexAndView(GUINetworkNode v, VisualizationViewer visComp)
    {
        this.vertex = v;
        this.visComp = visComp;
        this.setText("Delete Vertex");
    }
}