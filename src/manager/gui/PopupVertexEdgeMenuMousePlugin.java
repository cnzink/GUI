package manager.gui;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.NetworkEdge;
import manager.guiApp.NetworkNode;

/**
 * A GraphMousePlugin that brings up distinct popup menus when an edge or vertex is
 * appropriately clicked in a graph.  If these menus contain components that implement
 * either the EdgeMenuListener or VertexMenuListener then the corresponding interface
 * methods will be called prior to the display of the menus (so that they can display
 * context sensitive information for the edge or vertex).
 * @author Dr. Greg M. Bernstein
 */

public class PopupVertexEdgeMenuMousePlugin<V, E> extends AbstractPopupGraphMousePlugin
{
    private JPopupMenu edgePopup, vertexPopup;
    
    private GUICmdProcessor cmdProcessor;
    
    public void setGUICmdProcessor(GUICmdProcessor cmdProc)
    {
        this.cmdProcessor = cmdProc;
    }
    
    /**
     * Constructor function. It creates a new instance of PopupVertexEdgeMenuMousePlugin
     */
    public PopupVertexEdgeMenuMousePlugin() {
        this(MouseEvent.BUTTON3_MASK);
    }

    /**
     * Constructor function. It creates a new instance of PopupVertexEdgeMenuMousePlugin
     * @param modifiers mouse event modifiers see the jung visualization Event class.
     */
    public PopupVertexEdgeMenuMousePlugin(int modifiers) {
        super(modifiers);
    }

    /**
     * Implementation of the AbstractPopupGraphMousePlugin method. This is where the
     * work gets done.
     * @param e  mouse event
     */
    protected void handlePopup(MouseEvent e) {
        
        final VisualizationViewer<NetworkNode, NetworkEdge> vv = (VisualizationViewer<NetworkNode, NetworkEdge>) e.getSource();
        Point2D p = e.getPoint();

        GraphElementAccessor<NetworkNode, NetworkEdge> pickSupport = vv.getPickSupport();

        if(pickSupport != null) {
            final NetworkNode v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            if(v != null) {
                updateVertexMenu(v, vv, p, cmdProcessor);
                vertexPopup.show(vv, e.getX(), e.getY());
            } else {
                final NetworkEdge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                if(edge != null) {
                    updateEdgeMenu(edge, vv, p);
                    edgePopup.show(vv, e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * This function updates the vertex menu.
     * @param v the network node
     * @param vv the visualization viewer
     * @param point the location
     */
    private void updateVertexMenu(NetworkNode v, VisualizationViewer vv, Point2D point, GUICmdProcessor cmdProc)
    {
        if (vertexPopup == null) return;
        Component[] menuComps = vertexPopup.getComponents();
        for (Component comp: menuComps) {
            if (comp instanceof VertexMenuListener) {
                ((VertexMenuListener)comp).setVertexAndView(v, vv);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        }
    }

    /**
     * This function gets the edge pop up menu
     * @return the edge pop up menu
     */
    public JPopupMenu getEdgePopup() {
        return edgePopup;
    }

    /**
     * This function sets the edge pop up menu
     * @param edgePopup the edge pop up menu
     */
    public void setEdgePopup(JPopupMenu edgePopup) {
        this.edgePopup = edgePopup;
    }

    /**
     * This function gets the vetex pop up menu
     * @return the vertex pop up menu
     */
    public JPopupMenu getVertexPopup() {
        return vertexPopup;
    }

    /**
     * This function sets the vertex pop up menu
     * @param vertexPopup the vertex pop up menu
     */
    public void setVertexPopup(JPopupMenu vertexPopup) {
        this.vertexPopup = vertexPopup;
    }

    /**
     * This function update the edge pop up menu
     * @param edge the network ege
     * @param vv the visualization viewer
     * @param point the location point
     */
    private void updateEdgeMenu(NetworkEdge edge, VisualizationViewer vv, Point2D point) {
        if (edgePopup == null) return;
        Component[] menuComps = edgePopup.getComponents();
        for (Component comp: menuComps) {
            if (comp instanceof EdgeMenuListener) {
                ((EdgeMenuListener)comp).setEdgeAndView(edge, vv);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        }
    }
}

