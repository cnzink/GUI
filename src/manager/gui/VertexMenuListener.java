package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import manager.guiApp.GUICmdProcessor;

/**
 * the interface for vertex popup menu listener
 * @author Song Han
 * @param <GUINeworkNode> the type is NetworkNode
 */
public interface VertexMenuListener<GUINetworkNode> {

    /**
     * this function sets the NetworkNode instance and the visualization viewer
     * @param v the network node instance
     * @param visView the visualization viewer
     */
    void setVertexAndView(GUINetworkNode v, VisualizationViewer visView);
}

