package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.NetworkEdge;
import manager.guiApp.NetworkNode;
import manager.guiApp.GUITopology;

/**
 * This class defines the possible popup menus when right click the mouse on a graph components
 * @author Song Han
 */
public class MyMouseMenus {

    /**
     * This class defines the popup menu when right click the mouse on a barrier component
     */
    public static class BarrierMenu extends JPopupMenu
    {
        /**
         * Constructor function.
         * @param frame the parent frame on which the barrier menu reside
         */
        GUICmdProcessor cmdProcessor;
        public BarrierMenu(final JFrame frame, GUICmdProcessor cmdProc) {
            super("Barrier Menu");
            this.cmdProcessor = cmdProc;
            // add the delete barrier menu item
            this.add(new DeleteBarrierMenuItem<StaticBarrier>());
            this.addSeparator();
            // add the barrier property menu item
            this.add(new BarrierPropItem(frame, cmdProcessor));
        }
    }

    /**
     * This class defines the barrier property menu item
     */
    public static class BarrierPropItem extends JMenuItem implements BarrierMenuListener<StaticBarrier>
    {
        StaticBarrier barrier;
        VisualizationViewer visComp;
        GUITopology topology;
        GUICmdProcessor cmdProcessor;

        /**
         * This function set the barrier, visualization view and the network topology
         * @param bar the barrier instance
         * @param visComp the visualization view
         * @param topo the network topology
         */
        public void setBarrierAndView(StaticBarrier bar, VisualizationViewer visComp, GUITopology topo) {
            this.barrier = bar;
            this.visComp = visComp;
            this.topology = topo;
        }

        /**
         * Constructor function
         * @param frame the parent frame on which the barrier property item reside
         */
        public  BarrierPropItem(final JFrame frame, GUICmdProcessor cmdProc) {
            super("Edit Barrier Properties...");
            this.cmdProcessor = cmdProc;
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // create an dialog to edit the barrier property
                    BarrierPropertyDialog dialog = new BarrierPropertyDialog(frame, barrier, cmdProcessor);
                    dialog.setLocation((int)barrier.getX(), (int)barrier.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
            });
        }
    }

    /**
     * this class defines the edge menu when right click the mouse on an edge
     */
    public static class EdgeMenu extends JPopupMenu
    {
        /**
         * Constructor function
         * @param frame the parent frame on which the EdgeMenu reside
         */
        public EdgeMenu(final JFrame frame, GUICmdProcessor cmdProc) {
            super("Edge Menu");
            // this.frame = frame;
            this.add(new DeleteEdgeMenuItem<NetworkEdge>());
            this.addSeparator();
            this.add(new WeightDisplay());
            this.add(new CapacityDisplay());
            this.add(new SignalDisplay());
            this.addSeparator();
            this.add(new EdgePropItem(frame), cmdProc);
        }
    }

    /**
     * This class defines the edge property menu item
     */
    public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<NetworkEdge>, MenuPointListener
    {
        NetworkEdge edge;
        VisualizationViewer visComp;
        Point2D point;
        GUICmdProcessor cmdProcessor;

        /**
         * this function sets the edge and the visualization viewer
         * @param edge the edge instance
         * @param visComp the visualization viewer
         */
        public void setEdgeAndView(NetworkEdge edge, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.edge = edge;
            this.visComp = visComp;
            this.cmdProcessor = cmdProc;
        }
        public void setEdgeAndView(NetworkEdge edge, VisualizationViewer visComp) {
            this.edge = edge;
            this.visComp = visComp;
        }
        /**
         * this function sets the location of the dialog for editing the edge property
         * @param point the location of the dialog for editing the edge property
         */
        public void setPoint(Point2D point) {
            this.point = point;
        }

        /**
         * Constructor function. It creates the dialog for editing the edge property
         * @param frame the parent frame on which the dialog will reside
         */
        public  EdgePropItem(final JFrame frame) {
            super("Edit Edge Properties...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // create the dialog for editing the edge property
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, edge, cmdProcessor);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
            });
        }
    }    

    /**
     * This class defines a JMenuItem for showing the edge weight in the EdgeMenu
     */
    public static class WeightDisplay extends JMenuItem implements EdgeMenuListener<NetworkEdge> {
        /**
         * this function sets the edge and the visualization viewer
         * @param e the edge instance
         * @param visComp the visualization viewer
         */
        public void setEdgeAndView(NetworkEdge e, VisualizationViewer visComp) {
            this.setText("Weight " + " = " + e.getWeight());
        }
    }

    /**
     * This class defines a JMenuItem for showing the edge capacity in the EdgeMenu
     */
    public static class CapacityDisplay extends JMenuItem implements EdgeMenuListener<NetworkEdge> {
        /**
         * this function sets the edge and the visualization viewer
         * @param e the edge instance
         * @param visComp the visualization viewer
         */
        public void setEdgeAndView(NetworkEdge e, VisualizationViewer visComp) {
            this.setText("Capacity " + " = " + e.getCapacity());
        }
    }

    /**
     * This class defines a JMenuItem for showing the edge signal strength in the EdgeMenu
     */
    public static class SignalDisplay extends JMenuItem implements EdgeMenuListener<NetworkEdge> {
        /**
         * this function sets the edge and the visualization viewer
         * @param e the edge instance
         * @param visComp the visualization viewer
         */
        public void setEdgeAndView(NetworkEdge e, VisualizationViewer visComp) {
            this.setText("Signal Strength " + " = " + e.getSignalStrength());
        }
    }

    /**
     * This class defines a JMenuItem for showing the remaining power of the device in the VertexMenu
     */
    public static class PowerLeftDisplay extends JMenuItem implements VertexMenuListener<NetworkNode> {
        /**
         * this function sets the node and the visualization viewer
         * @param e the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode e, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.setText("Power Left " + " = " + e.getPowerLeft());
        }

        public void setVertexAndView(NetworkNode e, VisualizationViewer visComp) {
            this.setText("Power Left " + " = " + e.getPowerLeft());
        }
    }

    /**
     * This class defines a JMenuItem for showing the complete power of the device in the VertexMenu
     */
    public static class PowerAllDisplay extends JMenuItem implements VertexMenuListener<NetworkNode> {
        /**
         * this function sets the node and the visualization viewer
         * @param e the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode e, VisualizationViewer visComp) {
            this.setText("Total Power " + " = " + e.getPowerAll());
        }
        
        public void setVertexAndView(NetworkNode e, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.setText("Total Power " + " = " + e.getPowerAll());
        }
    }

    /**
     * This class defines a JMenuItem for showing the scanperiod of the device in the VertexMenu
     */
    public static class ScanPeriodDisplay extends JMenuItem implements VertexMenuListener<NetworkNode> {
        /**
         * this function sets the node and the visualization viewer
         * @param e the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode e, VisualizationViewer visComp) {
            this.setText("Scan Period " + " = " + e.getScanPeriod().toString());
        }

        public void setVertexAndView(NetworkNode v, VisualizationViewer visView, GUICmdProcessor cmdProc) {
            this.setText("Scan Period " + " = " + v.getScanPeriod().toString());
        }
    }

    /**
     * this class defines the vertex menu when right click the mouse on a node
     */
    public static class VertexMenu extends JPopupMenu {
        /**
         * Constructor function
         * @param frame the parent frame on which the VertexMenu reside
         */
        public VertexMenu(final JFrame frame, GUICmdProcessor cmdProc) {
            super("Vertex Menu");
            this.add(new DeleteVertexMenuItem<NetworkNode>());
            this.addSeparator();
            this.add(new ScanPeriodDisplay());
            this.add(new PowerLeftDisplay());
            this.add(new PowerAllDisplay());
            this.addSeparator();
            this.add(new poweredCheckBox());
            this.add(new enabledCheckBox());
            this.addSeparator();
//            if(cmdProc==null)
//            {
//                System.out.println("Vertex menu null");
//            }
            this.add(new VertexPropItem(frame, cmdProc));
        }
    }

    /**
     * This class defines the vertex property menu item
     */
    public static class VertexPropItem extends JMenuItem implements VertexMenuListener<NetworkNode>, MenuPointListener
    {
        NetworkNode vertex;
        VisualizationViewer visComp;
        Point2D point;
        GUICmdProcessor cmdProcessor;
                  
        /**
         * this function sets the node and the visualization viewer
         * @param v the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp) {
            this.vertex = v;
            this.visComp = visComp;
        }

        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.vertex = v;
            this.visComp = visComp;
            this.cmdProcessor = cmdProc;
        }
        /**
         * this function sets the location of the dialog for editing the vertex property
         * @param point the location of the dialog for editing the vertex property
         */
        public void setPoint(Point2D point) {
            this.point = point;
        }

        /**
         * Constructor function. It creates the dialog for editing the vertex property
         * @param frame the parent frame on which the dialog will reside
         */
        public VertexPropItem(final JFrame frame, GUICmdProcessor cmdProc) {
            super("Edit Vetex Properties...");
            this.cmdProcessor = cmdProc;
//            if(cmdProcessor==null)
//                System.out.println("VertexPropItem is null");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    VertexPropertyDialog dialog = new VertexPropertyDialog(frame, vertex, cmdProcessor);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
            });
        }
    }

    /**
     * This class defines a JMenuItem for showing if the vertex is powered or not
     */
    public static class poweredCheckBox extends JCheckBoxMenuItem implements VertexMenuListener<NetworkNode> {
        NetworkNode v;

        /**
         * Constructor function
         */
        public poweredCheckBox() {
            super("Powered");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    v.setPowered(isSelected());
                }
            });
        }
        
        /**
         * this function sets the node and the visualization viewer
         * @param v the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp) {
            this.v = v;
            this.setSelected(v.isPowered());
        }
        
        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.v = v;
            this.setSelected(v.isPowered());
        }
    }

    /**
     * This class defines a JMenuItem for showing if the vertex is enabled or not
     */
    public static class enabledCheckBox extends JCheckBoxMenuItem implements VertexMenuListener<NetworkNode> {
        NetworkNode v;

        /**
         * Constructor function
         */
        public enabledCheckBox() {
            super("Enabled");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    v.setEnabled(isSelected());
                }
            });
        }

        /**
         * this function sets the node and the visualization viewer
         * @param v the node instance
         * @param visComp the visualization viewer
         */
        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp) {
            this.v = v;
            this.setSelected(v.isEnabled());
        }
        
        public void setVertexAndView(NetworkNode v, VisualizationViewer visComp, GUICmdProcessor cmdProc) {
            this.v = v;
            this.setSelected(v.isPowered());
        }
    }
}
