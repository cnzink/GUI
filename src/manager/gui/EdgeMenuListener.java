package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * the interface for edge popup menu listener
 * @author Song Han
 * @param <GUINetworkEdge> the type is NetworkEdge
 */
public interface EdgeMenuListener<GUINetworkEdge> {

    /**
     * This function sets the NetworkEdge instance and the visualization viewer
     * @param e the reference to the network edge
     * @param visView the visualization viewer
     */
    void setEdgeAndView(GUINetworkEdge e, VisualizationViewer visView);
}
