package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import manager.guiApp.GUITopology;

/**
 * The delete barrier menu item on the pop up mouse menu when right click the mouse on the barrier
 * @author Song Han
 * @param <StaticBarrier> the static barrier class
 */
public class DeleteBarrierMenuItem<StaticBarrier> extends JMenuItem implements BarrierMenuListener<StaticBarrier>
{
    private StaticBarrier barrier;
    private VisualizationViewer visComp;
    private GUITopology topology;

    /**
     * Constructor function
     */
    public DeleteBarrierMenuItem()
    {
        super("Delete Barrier");
        this.addActionListener(new ActionListener(){
            @SuppressWarnings("element-type-mismatch")
            public void actionPerformed(ActionEvent e) {

                topology.GetBarriers().remove(barrier);
                visComp.removePostRenderPaintable((Paintable) barrier);
                visComp.repaint();
            }
        });
    }

    /**
     * This function sets the barrier, the visualization viewer and the network topology
     * @param bar the static barrier reference
     * @param visComp the visualization viewer
     * @param topo the network topology
     */
    public void setBarrierAndView(StaticBarrier bar, VisualizationViewer visComp, GUITopology topo) {
        this.barrier = bar;
        this.visComp = visComp;
        this.topology = topo;
        this.setText("Delete Barrier");
    }
}