package manager.guiApp;

import manager.guiApp.NetworkNode;
import manager.guiApp.GUITopology;
import manager.guiApp.NetworkEdge;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import manager.gui.MyMouseMenus;
import manager.gui.PopupBarrierMenuMousePlugin;
import manager.gui.PopupVertexEdgeMenuMousePlugin;
import mesh.ScanPeriod;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;


/**
 * this class defines the network visualizer for display the network topology
 * @author Song Han
 */
public class NetworkVisualizer {

    // data structure
    private Layout<NetworkNode, NetworkEdge> layout;
    private VisualizationViewer<NetworkNode, NetworkEdge> vv;
    private EditingModalGraphMouse gm;
    private PickedState <NetworkNode> pickedState;    
    private GUITopology topology;
    private JFrame jFrame;
    private ScalingControl scaler;
    private int scaleNum;
    private GUICmdProcessor cmdProcessor;
    
    // boolean variables
    boolean isPruning;
    boolean isComplete;
    boolean isBroadcastGraph;
    boolean isFromGatewayGraph;
    boolean isUpLinkGraph;

    // decrators for vertex and edge
    private Transformer<NetworkEdge, Stroke> edgeStrokeTransformer;
    private Transformer<NetworkEdge, Stroke> bgStrokeTransformer;
    private Transformer<NetworkEdge, Stroke> ulgStrokeTransformer;
    private Transformer<NetworkEdge, Paint> edgePaintTransformer;
    private Transformer<NetworkEdge, Paint> bgPaintTransformer;
    private Transformer<NetworkEdge, Paint> ulgPaintTransformer;
    private Transformer<NetworkNode, Paint> vertexFillColorTransformer;

    private Transformer<NetworkEdge, Stroke> fromGatewayGraphStrokeTransformer;
    private Transformer<NetworkEdge, Paint> fromGatewayPaintTransformer;
    private int signalStrengthThreshold;

    // predicate for the vertex and edge
    Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkNode>> vertexPredicate;
    Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkEdge>> edgePredicate;

    // mouse plugins
    PopupBarrierMenuMousePlugin barrierPlugin;
    ChangeSignalStrengthPlugin signalStrengthPlugin;
    PopupVertexEdgeMenuMousePlugin myPlugin;

    /**
     * constructor function. Create an instance of the network visualizer using the default settings.
     * @param topo the network topology
     * @param f the parent frame
     * @throws IOException I/O exception
     */
    public NetworkVisualizer(GUITopology topo, JFrame f, GUICmdProcessor cmdProc) throws IOException
    {
        signalStrengthThreshold = -20;
        isPruning = false;
        isComplete = true;
        isBroadcastGraph = false;
        this.cmdProcessor = cmdProc;
        
        topology = topo;
        jFrame = f;

        layout = new KKLayout(topology.g);
        layout.setSize(new Dimension(450,450));

        vv = new VisualizationViewer<NetworkNode, NetworkEdge>(layout);
        vv.setPreferredSize(new Dimension(450,450));
        pickedState = vv.getPickedVertexState();
        scaler = new LayoutScalingControl();

        
        
        // set up a vertex fill color transformer
        vertexFillColorTransformer = new Transformer<NetworkNode, Paint>()
        {
            public Paint transform(NetworkNode v)
            {
                if (pickedState.isPicked(v))
                    return Color.BLACK;
                else
                {
                    if(v.getType().equals("GATEWAY") == true)
                        return Color.RED;
                    else if(v.getType().equals("AP") == true)
                        return Color.ORANGE;
                    else if(v.getType().equals("DEVICE") == true)
                        return new Color(50, 200, 255); //light blue instead of : Color.BLUE;
                    else if(v.getType().equals("ROUTER") == true)
                        return Color.GREEN;
                    else if(v.getType().equals("HANDHELD") == true)
                        return Color.MAGENTA;
                    else
                        return Color.WHITE;
                }
            }
        };

        // Set up a new stroke Transformer for the edges
        edgeStrokeTransformer = new Transformer<NetworkEdge, Stroke>()
        {
            final Stroke basic = new BasicStroke(1);
            final Stroke heavy = new BasicStroke(2);
            final Stroke dotted = RenderContext.DOTTED;

            public Stroke transform(NetworkEdge e) {
                
                if(e.getSignalStrength() > -20)
                    return heavy;
                else
                    return dotted;
            }
        };
/*
        // Set up a new stroke Transformer for the fromGateway graph
        fromGatewayGraphStrokeTransformer = new Transformer<GUINetworkEdge, Stroke>()
        {
            final Stroke basic = new BasicStroke(1);
            final Stroke heavy = new BasicStroke(2);
            final Stroke dotted = RenderContext.DOTTED;
            final Stroke bgStroke = new BasicStroke(4);

            public Stroke transform(GUINetworkEdge e) {

                if(pickedState.getPicked().size() > 0)
                {
                    Collection <NetworkNode> nodeCollection = pickedState.getPicked();
                    Iterator<NetworkNode> itr = nodeCollection.iterator();

                    NetworkNode itrNode;
                    while(itr.hasNext() == true)
                    {
                        itrNode = itr.next();
                        if((itrNode.getFromGateWayGraph() != null) && (itrNode.getFromGateWayGraph().containsEdge(e)))
                            return bgStroke;
                    }
                }

                if(e.getSignalStrength() > -20)
                    return heavy;
                else
                    return dotted;
            }
        };

        // Set up a new Paint Transformer for the fromGateway graph
        fromGatewayPaintTransformer = new Transformer<GUINetworkEdge,Paint> (){

            public Paint transform(GUINetworkEdge e) {

                if(pickedState.getPicked().size() > 0)
                {
                    Collection <NetworkNode> nodeCollection = pickedState.getPicked();
                    Iterator<NetworkNode> itr = nodeCollection.iterator();

                    NetworkNode itrNode;
                    while(itr.hasNext() == true)
                    {
                        itrNode = itr.next();
                        if((itrNode.getFromGateWayGraph() != null) && (itrNode.getFromGateWayGraph().containsEdge(e)))
                            return Color.YELLOW;
                    }
                    return Color.BLACK;
                }
                else
                    return Color.BLACK;
            }
        };
*/
        // Set up a new stroke Transformer for the broadcast graph
        bgStrokeTransformer = new Transformer<NetworkEdge, Stroke>()
        {
            final Stroke basic = new BasicStroke(1);
            final Stroke heavy = new BasicStroke(2);
            final Stroke dotted = RenderContext.DOTTED;
            final Stroke bgStroke = new BasicStroke(4);

            public Stroke transform(NetworkEdge e) {

                if((topology.bg != null) && (topology.bg.containsEdge(e)))
                {
                    return bgStroke;
                }
                else
                {
                    if(e.getSignalStrength() > -20)
                        return heavy;
                    else
                        return dotted;
                }
            }
        };

        // Set up a new stroke Transformer for the uplink graph
        ulgStrokeTransformer = new Transformer<NetworkEdge, Stroke>()
        {
            final Stroke basic = new BasicStroke(1);
            final Stroke heavy = new BasicStroke(2);
            final Stroke dotted = RenderContext.DOTTED;
            final Stroke ulgStroke = new BasicStroke(4);

            public Stroke transform(NetworkEdge e) {

                if((topology.ulg != null) && (topology.ulg.containsEdge(e)))
                {
                    return ulgStroke;
                }
                else
                {
                    if(e.getSignalStrength() > -20)
                        return heavy;
                    else
                        return dotted;
                }
            }
        };

        // Set up a new Paint Transformer for the broadcast graph
        bgPaintTransformer = new Transformer<NetworkEdge,Paint> (){

            public Paint transform(NetworkEdge e) {
                
                if(topology.bg.containsEdge(e))
                    return new Color(0.0f, 0.0f, 1.0f, 0.5f);
                else
                    return Color.BLACK;
            }
        };

        // Set up a new Paint Transformer for the uplink graph
        ulgPaintTransformer = new Transformer<NetworkEdge,Paint> (){

            public Paint transform(NetworkEdge e) {

                if(topology.ulg.containsEdge(e))
                    return Color.RED;
                else
                    return Color.BLACK;
            }
        };

        // Set up a new Paint Transformer for the edges
        edgePaintTransformer = new Transformer<NetworkEdge,Paint> (){

            public Paint transform(NetworkEdge e) {
                    return Color.BLACK;
            }
        };

        // the predicate for the vertex. It decides which vertices will be shown in the visualizer
        vertexPredicate = new Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkNode>>()
        {
            public boolean evaluate(Context<Graph<NetworkNode, NetworkEdge>, NetworkNode> context)
            {
                Graph<NetworkNode, NetworkEdge> graph = context.graph;
                NetworkNode v = context.element;

                if(((topology.pruneOption.getPowerAllLowerBound() <= v.getPowerAll()) && (v.getPowerAll() <= topology.pruneOption.getPowerAllUpperBound()))
                  &&((topology.pruneOption.getPowerLeftLowerBound() <= v.getPowerLeft() && (v.getPowerLeft() <= topology.pruneOption.getPowerLeftUpperBound()))
                  && (IsDeviceTypeToBePruned(v) == false)
                  && (IsScanPeriodToBePruned(v) == false)))
                    return true;
                else
                    return false;
            }           
        };

        // the predicate for the edges. It decides which edges will be shown in the visualizer
        edgePredicate = new Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkEdge>>()
        {
            public boolean evaluate(Context<Graph<NetworkNode, NetworkEdge>, NetworkEdge> context)
            {
                NetworkEdge edge = context.element;

                if(edge.getSignalStrength() <= -99)
                    return false;

                if(((topology.pruneOption.getWeightLowerBound() <= edge.getWeight()) && (edge.getWeight() <= topology.pruneOption.getWeightUpperBound()))
                && ((topology.pruneOption.getCapacityLowerBound() <= edge.getCapacity()) && (edge.getCapacity() <= topology.pruneOption.getCapacityUpperBound()))
                && ((topology.pruneOption.getSignalStrengthLowerBound() <= edge.getSignalStrength()) && (edge.getSignalStrength() <= topology.pruneOption.getSignalStrengthUpperBound())))
                    return true;
                else
                    return false;
            }           
        };

        // configure the visualization viewer
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(vertexFillColorTransformer);
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

        // decide the location of the vertexLabelRenderer
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        // Create a graph mouse and add it to the visualization viewer
        gm = new EditingModalGraphMouse(vv.getRenderContext(), topology.vertexFactory, topology.edgeFactory);
        
        // Try out the popup menu mouse plugin
        myPlugin = new PopupVertexEdgeMenuMousePlugin();
        myPlugin.setGUICmdProcessor(cmdProcessor);
        
        // Add some popup menus for the edges and vertices to our mouse plugin.
        JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(jFrame, cmdProcessor);
        //if(cmdProcessor==null)
        //    System.out.println("network visualizer null");
        JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu(jFrame, cmdProcessor);
        
        myPlugin.setEdgePopup(edgeMenu);
        myPlugin.setVertexPopup(vertexMenu);
        gm.remove(gm.getPopupEditingPlugin()); 
        gm.add(myPlugin);

        signalStrengthPlugin = new ChangeSignalStrengthPlugin();
        gm.add(signalStrengthPlugin);

        barrierPlugin = new PopupBarrierMenuMousePlugin();
        //JPopupMenu barrierMenu = new MyMouseMenus.BarrierMenu(jFrame);
        //barrierPlugin.setBarrierPopup(barrierMenu);
        barrierPlugin.setTopology(topology);
        barrierPlugin.setNwkVisualizer(this);
        gm.add(barrierPlugin);

        vv.setGraphMouse(gm);
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.addKeyListener(gm.getModeKeyListener());
    }

    public void setCmdProcessor(GUICmdProcessor cmdProc)
    {
            this.cmdProcessor = cmdProc;
    }
        
    /**
     * this function gets the graph mouse
     * @return the graph mouse
     */
    
    
    public EditingModalGraphMouse getMouse()
    {   return gm;  }

    /**
     * this function gets the visualization viewer
     * @return the visualization viewer
     */
    public VisualizationViewer<NetworkNode, NetworkEdge> getViewer()
    {   return vv;  }

    /**
     * this function gets the Scaler of the visualization viewer
     * @return the Scaler of the visualization viewer
     */
    public ScalingControl getScaler()
    {   return scaler;  }

    /**
     * this function increases the scale number by one
     */
    public void increaseScaleNum()
    {   scaleNum ++; }

    /**
     * this function decreases the scale number by one
     */
    public void decreaseScaleNum()
    {   scaleNum --; }

    /**
     * this function gets the current scale number
     * @return the current scale number
     */
    public int getScaleNum()
    {   return scaleNum; }

    /**
     * this function sets the scale number
     * @param num the scale number
     */
    public void setScaleNum(int num)
    {   scaleNum = num; }

    /**
     * this function gets the visualization viewer layout
     * @return the visualization viewer layout
     */
    public Layout<NetworkNode, NetworkEdge> getLayout()
    {   return layout;  }

    /**
     * this function gets the network toplogy
     * @return the network toplogy
     */
    public GUITopology getTopology()
    {   return topology;    }

    /**
     * this function sets the network topology
     * @param topo the network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        layout.setGraph(topology.g);
    }

    /**
     * this function update the barrier plugin with the network topology
     * @param topo the network topology
     */
    public void updateBarrierPlugin(GUITopology topo)
    {
        if(barrierPlugin != null)
        {
            barrierPlugin.setTopology(topo);
        }
    }

    /**
     * this function enable the edge label
     */
    public void enableEdgeLabel()
    {
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    }

    /**
     * this function disable the edge label
     */
    public void disableEdgeLabel()
    {
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<NetworkEdge,String>()
        {   public String transform(NetworkEdge e){return "";}});
    }

    /**
     * this fucntion enables the vertex predicate
     */
    public void enableVertexPredicate()
    {
        vv.getRenderContext().setVertexIncludePredicate(vertexPredicate);
    }

    /**
     * this function enables the edge predicate
     */
    public void enableEdgePredicate()
    {
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);
        vv.getRenderContext().setEdgeIncludePredicate(edgePredicate);
    }

    /**
     * this function enables the broadcast graph predicate
     */
    public void enableBroadcastGraphPredicate()
    {
        if(isBroadcastGraph == false)
        {
            // change shape and color
            vv.getRenderContext().setEdgeStrokeTransformer(bgStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(bgPaintTransformer);
            isBroadcastGraph = true;
        }
        else
        {
            // change shape and color back to normal
            vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);
            isBroadcastGraph = false;
        }
    }

    /**
     * this function enables the uplink graph predicate
     */
    public void enableUpLinkGraphPredicate()
    {
        if(isUpLinkGraph == false)
        {
            // change shape and color
            vv.getRenderContext().setEdgeStrokeTransformer(ulgStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(ulgPaintTransformer);
            isUpLinkGraph = true;
        }
        else
        {
            // change shape and color back to normal
            vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);
            isUpLinkGraph = false;
        }
    }

    /**
     * this function enables the FromGateway graph predicate
     */
    public void enableFromGatewayGraphPredicate()
    {
        if(isFromGatewayGraph == false)
        {
            // change shape and color
            vv.getRenderContext().setEdgeStrokeTransformer(fromGatewayGraphStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(fromGatewayPaintTransformer);
            isFromGatewayGraph = true;
        }
        else
        {
            // change shape and color back to normal
            vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
            vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);
            isFromGatewayGraph = false;
        }
    }

    /**
     * this function disable the vertex predicate
     */
    public void disableVertexPredicate()
    {
        vv.getRenderContext().setVertexIncludePredicate(new Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkNode>>()
        {
            public boolean evaluate(Context<Graph<NetworkNode, NetworkEdge>, NetworkNode> context) 
            {return true;}
        });
    }

    /**
     * this function disbles the edge predicate
     */
    public void disableEdgePredicate()
    {
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);
        vv.getRenderContext().setEdgeIncludePredicate(new Predicate<Context<Graph<NetworkNode,NetworkEdge>,NetworkEdge>>()
        {
            public boolean evaluate(Context<Graph<NetworkNode, NetworkEdge>, NetworkEdge> context)
            {return true;}
        });
    }

    /**
     * this class extends the PickingGraphMousePlugin and it should change the edge signal strength when the mouse is dragged
     */
    private final class ChangeSignalStrengthPlugin extends PickingGraphMousePlugin
    {
        @Override
        public void mouseDragged(MouseEvent e)
        {
            super.mouseDragged(e);
            //topology.reEvaluateAllSignalStrength(layout);
        }
    }

    /**
     * this function checks if the network node should be pruned according to its device type
     * @param v the given netowrk node
     * @return true if the network node should be pruned according to its device type
     */
    public boolean IsDeviceTypeToBePruned(NetworkNode v)
    {
        if(v.getType().equals("GATEWAY"))
        {
            if(topology.pruneOption.isGWDisplayed() == false)
                return true;
            else
                return false;
        }
        else if(v.getType().equals("AP"))
        {
            if(topology.pruneOption.isAPDisplayed() == false)
                return true;
            else
                return false;
        }
        else if(v.getType().equals("DEVICE"))
        {
            if(topology.pruneOption.isDeviceDisplayed() == false)
                return true;
            else
                return false;
        }
        else if(v.getType().equals("ROUTER"))
        {
            if(topology.pruneOption.isRouterDisplayed() == false)
                return true;
            else
                return false;
        }
        else if(v.getType().equals("HANDHELD"))
        {
            if(topology.pruneOption.isHHDisplayed() == false)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * this function checks if the network node should be pruned according to its scan period
     * @param v the given network node
     * @return true if the network node should be pruned according to its scan period
     */
    public boolean IsScanPeriodToBePruned(NetworkNode v)
    {
        if(v.getScanPeriod().toString().equals("_0_ms")== true)
        {
            if((ScanPeriod._0_ms >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._0_ms <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_250_ms") == true)
        {
            if((ScanPeriod._250_ms >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._250_ms <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_1_sec") == true)
        {
            if((ScanPeriod._1_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._1_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_2_sec") == true)
        {
            if((ScanPeriod._2_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._2_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_4_sec") == true)
        {
            if((ScanPeriod._4_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._4_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_8_sec") == true)
        {
            if((ScanPeriod._8_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._8_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_16_sec") == true)
        {
            if((ScanPeriod._16_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._16_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_32_sec") == true)
        {
            if((ScanPeriod._32_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._32_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_1_min_4_sec") == true)
        {
            if((ScanPeriod._1_min_4_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._1_min_4_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_2_min_8_sec") == true)
        {
            if((ScanPeriod._2_min_8_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._2_min_8_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_4_min_16_sec") == true)
        {
            if((ScanPeriod._4_min_16_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._4_min_16_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else if(v.getScanPeriod().toString().equals("_8_min_32_sec") == true)
        {
            if((ScanPeriod._8_min_32_sec >= topology.pruneOption.getScanPeriodLowerBound().getScanPeriod())
            && (ScanPeriod._8_min_32_sec <= topology.pruneOption.getScanPeriodUpperBound().getScanPeriod()))
                return false;
            else
                return true;
        }
        else
            return false;
    }
}


