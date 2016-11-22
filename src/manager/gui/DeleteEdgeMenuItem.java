package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import manager.guiApp.NetworkEdge;


/**
 * The delete edge menu item on the pop up mouse menu when right click on the edge
 * @author Song Han
 * @param <GUINetworkEdge> the network edge class
 */
public class DeleteEdgeMenuItem<GUINetworkEdge> extends JMenuItem implements EdgeMenuListener<GUINetworkEdge> {

    private GUINetworkEdge edge;
    private VisualizationViewer visComp;
    
    /**
     * Constructor function
     */
    public DeleteEdgeMenuItem() {
        super("Delete Edge");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // de-select and remove the edge from the visualization viewer
            visComp.getPickedEdgeState().pick(edge, false);
                visComp.getGraphLayout().getGraph().removeEdge(edge);
                visComp.repaint();
            }
        });
    }

    /**
     * This function sets the edge, the visualization viewer
     * @param edge the selected network edge
     * @param visComp the visualization viewer
     */
    public void setEdgeAndView(GUINetworkEdge edge, VisualizationViewer visComp) {
        this.edge = edge;
        this.visComp = visComp;
        this.setText("Delete Edge");
    }
    
}