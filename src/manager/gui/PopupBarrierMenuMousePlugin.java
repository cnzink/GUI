package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import manager.guiApp.NetworkVisualizer;
import manager.guiApp.GUITopology;

/**
 * This class extends the PickingGraphMousePlugin class to handle the event when click on the barrier object
 * @author Song Han
 */

public class PopupBarrierMenuMousePlugin extends PickingGraphMousePlugin
{
    private JPopupMenu barrierPopup;
    private StaticBarrier barrier;
    private GUITopology topology;
    private NetworkVisualizer nwkVisualizer;

    /**
     * This function returns the network visualizer
     * @return the network visualizer
     */
    public NetworkVisualizer getNwkVisualizer()
    {
        return nwkVisualizer;
    }
    
    /**
     * This function sets the network visualizer
     * @param visualizer the network visualizer
     */
    public void setNwkVisualizer(NetworkVisualizer visualizer)
    {
        nwkVisualizer = visualizer;
    }

    /**
     * This function gets the barrier JPopupMenu
     * @return the barrier JPopupMenu
     */
    public JPopupMenu getBarrierPopup() {
        return barrierPopup;
    }

    /**
     * This function sets the barrier JPopupMenu
     * @param barrierPopup the barrier JPopupMenu
     */
    public void setBarrierPopup(JPopupMenu barrierPopup) {
        this.barrierPopup = barrierPopup;
    }

    /**
     * This function returns the network topology
     * @return the network topology
     */
    public GUITopology getTopology()
    {
        return topology;
    }

    /**
     * This function sets the network topology
     * @param topo the network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
    }

    /**
     * This function updates the barrier menu
     * @param barrier the barrier instance
     * @param vv the visualization viewer
     */
    private void updateBarrierMenu(StaticBarrier barrier, VisualizationViewer vv)
    {
        if (barrierPopup == null) return;
        Component[] menuComps = barrierPopup.getComponents();
        for (Component comp: menuComps) {
            if (comp instanceof BarrierMenuListener) {
                ((BarrierMenuListener)comp).setBarrierAndView(barrier, vv, topology);
            }
        }
    }

    /**
     * this function finds which is the current barrier the mouse is pointting at
     * @param e the mouse event
     * @return the current barrier. null if there is no barrier there
     */
    StaticBarrier FindCurrentBarrier(MouseEvent e)
    {
        StaticBarrier bar = null;
        for(int i = 0; i < topology.GetBarriers().size(); i ++)
        {
            bar = topology.GetBarriers().get(i);

            // if the current mouse position is inside the bar
            if(bar.isInBarrier(e.getX(), e.getY()))
                return bar;
        }
        return null;
    }

    /**
     * this function defines the behavior when the mouse is pressed
     * @param e mouse event
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        // get the current barrier pointed by the mouse
        barrier = FindCurrentBarrier(e);

        // right mouse button, popup a config menu
        if(((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) && (barrier != null))
        {
            updateBarrierMenu(barrier, nwkVisualizer.getViewer());
            barrierPopup.show(nwkVisualizer.getViewer(), e.getX(), e.getY());
        }

        // left mouse button, set the values of the current barrier according to the mouse position
        if(((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) && (barrier != null))
        {
            barrier.setXBegin(e.getX());
            barrier.setYBegin(e.getY());
            barrier.setInBarrier(true);
        }
    }

    /**
     * this function defines the behavior when the mouse is released
     * @param the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(barrier != null)
        {
            barrier.setInBarrier(false);
            barrier = null;
        }
    }

    /**
     * this function defines the behavior when the mouse is dragged. It updates the location of the barrier
     * @param the mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(barrier != null)
        {
            int pic_x = barrier.getX();
            int pic_y = barrier.getY();
            int begin_x = barrier.getXBegin();
            int begin_y = barrier.getYBegin();

            pic_x =pic_x - (begin_x - e.getX());
            pic_y =pic_y - (begin_y - e.getY());

            begin_x = e.getX();
            begin_y = e.getY();

            barrier.setX(pic_x);
            barrier.setY(pic_y);
            barrier.setXBegin(begin_x);
            barrier.setYBegin(begin_y);
        }
    }
}
