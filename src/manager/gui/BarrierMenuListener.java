package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import manager.guiApp.GUITopology;

/**
 * the interface for barrier popup menu listener
 * @author Song Han
 * @param <StaticBarrier> the type is StaticBarrier
 */
public interface BarrierMenuListener<StaticBarrier> {

    /**
     * this function sets the StaticBarrier instance, the visualization viewer and the network topology
     * @param e the static barrier
     * @param visView the visualization viewer
     * @param topo the network topology
     */
    void setBarrierAndView(StaticBarrier e, VisualizationViewer visView, GUITopology topo);
}
