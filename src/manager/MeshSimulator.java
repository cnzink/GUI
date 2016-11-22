package manager;

import manager.guiport.GUITcpRecvPort;
import edu.uci.ics.jung.algorithms.layout.Layout;
import java.awt.AWTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;

import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import manager.gui.BarrierCreateDialog;
import manager.gui.GraphPruneDialog;
import manager.gui.StaticBarrier;
import manager.gui.TableType;
import manager.gui.TableController;
import manager.gui.TimerUpdater;
import manager.gui.VertexCreateDialog;
import manager.guiApp.*;
import manager.guiport.TcpManager;
import mesh.*;
import mesh.cmd.Cmd64046;
import mesh.cmd.CmdManipulation;
import mesh.cmd.GuiCmdInfo;
import mesh.security.RandomManager;
import mesh.security.SecurityManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is the application's main frame. It creates the main frame for the network manager
 */
public class MeshSimulator extends FrameView
{

    /**
     * this function initialzes the network manager data structure and GUI.
     */
    //public static String NM_IP = "127.0.0.1";
    //public static int NM_port = 8900;

    /*private boolean readNMInfo(String fileName)
    {
        Document document = null;
        File f;
        f = new File(fileName);
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(f);
            document.normalize();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (document == null)
        {
            return false;
        }
        Node root = document.getDocumentElement();
        if (root.hasChildNodes())
        {
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++)
            {
                NodeList list = nodes.item(i).getChildNodes();
                for (int k = 0; k < list.getLength(); k++)
                {
                    Node subnode = list.item(k);
                    if (subnode.getNodeType() == 3)
                    {
                        if (nodes.item(i).getNodeName().compareTo("IP") == 0)
                        {
                            this.NM_IP = nodes.item(i).getChildNodes().item(0).getNodeValue();
                        }
                        else
                        {
                            if (nodes.item(i).getNodeName().compareTo("Port") == 0)
                            {
                                this.NM_port = Integer.parseInt(nodes.item(i).getChildNodes().item(0).getNodeValue());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }*/

    private void initMeshSimulator() throws IOException, AWTException
    {
        this.getFrame().setTitle(titleName);
        mainFrame = this.getFrame();

        // add the window Listener and override the action for window closing
        mainFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                tcpManager.disconnect();
                ManagerRuntime.closeThread("awiagateway.exe");
                System.exit(0);
            }
        });

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        // set frame width, height and let platform pick screen location
        mainFrame.setSize(screenWidth / 2, screenHeight / 2);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setResizable(true);

        // initialize the GUI components
        toolBar = new JToolBar("ToolBar");
        topologyLabel = new JLabel("Topology: ");

        saveButton = new JButton(new ImageIcon(getClass().getResource("resources/save.png")));
        newButton = new JButton(new ImageIcon(getClass().getResource("resources/new.png")));
        openButton = new JButton(new ImageIcon(getClass().getResource("resources/open.png")));
        randomButton = new JButton(new ImageIcon(getClass().getResource("resources/random.png")));
        refreshButton = new JButton(new ImageIcon(getClass().getResource("resources/refresh.png")));
        addLabel = new JLabel("Add: ");
        addAPButton = new JButton(new ImageIcon(getClass().getResource("resources/ap.png")));
        addRouterButton = new JButton(new ImageIcon(getClass().getResource("resources/router.png")));
        addDeviceButton = new JButton(new ImageIcon(getClass().getResource("resources/device.png")));
        addHHButton = new JButton(new ImageIcon(getClass().getResource("resources/hh.png")));
        addBarrierButton = new JButton(new ImageIcon(getClass().getResource("resources/barrier.png")));

        viewLabel = new JLabel("View: ");
        pruneViewButton = new JButton(new ImageIcon(getClass().getResource("resources/prune.png")));
        completeViewButton = new JButton(new ImageIcon(getClass().getResource("resources/complete.png")));
        upLinkViewButton = new JButton(new ImageIcon(getClass().getResource("resources/up.png")));
        downLinkViewButton = new JButton(new ImageIcon(getClass().getResource("resources/down.png")));
        broadcastLinkViewButton = new JButton(new ImageIcon(getClass().getResource("resources/broadcast.png")));
        snapShotGraphButton = new JButton(new ImageIcon(getClass().getResource("resources/clock.png")));
        compareGraphButton = new JButton(new ImageIcon(getClass().getResource("resources/compare.png")));
        signalEnableButton = new JButton(new ImageIcon(getClass().getResource("resources/signal_en.png")));
        signalDisableButton = new JButton(new ImageIcon(getClass().getResource("resources/signal_dis.png")));
        zoomInButton = new JButton(new ImageIcon(getClass().getResource("resources/zoom_in.png")));
        zoomOutButton = new JButton(new ImageIcon(getClass().getResource("resources/zoom_out.png")));
        homeButton = new JButton(new ImageIcon(getClass().getResource("resources/home.png")));
        animationButton = new JButton(new ImageIcon(getClass().getResource("resources/goon.png")));
        animationStopButton = new JButton(new ImageIcon(getClass().getResource("resources/stop.png")));
        scheduleButton = new JButton(new ImageIcon(getClass().getResource("resources/test.png")));
        snapShotScheduleButton = new JButton(new ImageIcon(getClass().getResource("resources/clock.png")));
        compareScheduleButton = new JButton(new ImageIcon(getClass().getResource("resources/compare.png")));
        scheduleMsgButton = new JButton(new ImageIcon(getClass().getResource("resources/goon.png")));
        connectButton = new JButton(new ImageIcon(getClass().getResource("resources/connect.png")));
        disConnectButton = new JButton(new ImageIcon(getClass().getResource("resources/disconnect.png")));
        testButton = new JButton(new ImageIcon(getClass().getResource("resources/favorite.png")));

        remainLabel = new JLabel("                                                                                                                        ");
        staText = new JTextArea(5, 40);
        staText.setLineWrap(true);
        staText.setEditable(false);
        debugText = new JTextArea(5, 40);
        debugText.setLineWrap(true);
        debugText.setEditable(false);

        topology = new GUITopology(false, 2, 1);

        nwkVisualizer = new NetworkVisualizer(topology, this.getFrame(), cmdProcessor);

        // initialize the message boards
        sendMsgBoard = new JTextArea(5, 40);
        recvMsgBoard = new JTextArea(5, 40);
        sendMsgBoard.setLineWrap(true);
        recvMsgBoard.setLineWrap(true);
        sendMsgBoard.setEditable(false);
        recvMsgBoard.setEditable(false);

        nodeTableControl = new TableController(topology, null, TableType.Vertex);
        edgeTableControl = new TableController(topology, null, TableType.Edge);
        msgTableControl = new TableController(topology, cmdProcessor, TableType.Msg);
        statsTableControl = new TableController(topology, cmdProcessor, TableType.Stat);
        graphTableControl = new TableController(topology, null, TableType.Graph);
        devListTableControl = new TableController(topology, null, TableType.DeviceList);

        // setup the file chooser
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Topology files", "sav");
        chooser.setFileFilter(filter);

        // initialize the listeners
        prune = new PruneTopoListener();
        complete = new CompleteTopoListener();
        refresh = new RefreshListener();
        newListener = new NewListener(staText);
        randomListener = new RandomListener(staText);
        saveListener = new FileSaveListener();
        openListener = new FileOpenListener();
        aboutListener = new AboutListener(staText);
        configListener = new ConfigListener(staText);
        guiConfigListener = new GUIConfigListener(this);
        //     connectionListener = new ConnectionListener(this);
        addNodeListener = new AddNodeListener();
        addBarrierListener = new AddBarrierListener();
        signalEnableListener = new SignalEnableListener();
        signalDisableListener = new SignalDisableListener();
        connectionSettingListener = new ConnectionSettingListener(this);
        disconnectListener = new DisconnectListener(this);
        //connectListener = new ConnectListener(this);

        // configure the toolbar
        toolBar.add(topologyLabel);
        /*    saveButton.setToolTipText("Save GUITopology");
        toolBar.add(saveButton);
        openButton.setToolTipText("Open GUITopology");
        toolBar.add(openButton);
        newButton.setToolTipText("New GUITopology");
        toolBar.add(newButton);
        randomButton.setToolTipText("Randomized GUITopology");
        toolBar.add(randomButton);
        refreshButton.setToolTipText("Refresh");
        toolBar.add(refreshButton);
        toolBar.addSeparator();
        toolBar.add(addLabel);
        addAPButton.setToolTipText("Add Access Point");
        toolBar.add(addAPButton);
        addDeviceButton.setToolTipText("Add Device");
        toolBar.add(addDeviceButton);
        addRouterButton.setToolTipText("Add Router");
        toolBar.add(addRouterButton);
        addHHButton.setToolTipText("Add HandHeld Device");
        toolBar.add(addHHButton);
        addBarrierButton.setToolTipText("Add Barrier");
        toolBar.add(addBarrierButton);
        toolBar.addSeparator();
        toolBar.add(viewLabel);
        pruneViewButton.setToolTipText("Prune Graph");
        toolBar.add(pruneViewButton);
         */


        completeViewButton.setToolTipText("Complete Graph");
        toolBar.add(completeViewButton);
        upLinkViewButton.setToolTipText("View Up Link");
        toolBar.add(upLinkViewButton);
        downLinkViewButton.setToolTipText("View Down Link");
        toolBar.add(downLinkViewButton);
        broadcastLinkViewButton.setToolTipText("View Broadcast Link");
        toolBar.add(broadcastLinkViewButton);
        /*
        addBarrierButton.setToolTipText("Add Barrier");
        toolBar.add(addBarrierButton);

        toolBar.add(animationButton);
        toolBar.add(animationStopButton);
         */

        /*
        snapShotGraphButton.setToolTipText("Create Communication Graph Snapshots");
        toolBar.add(snapShotGraphButton);
        compareGraphButton.setToolTipText("Compare the Current Graph with its Snapshots");
        toolBar.add(compareGraphButton);

        toolBar.addSeparator();
        toolBar.add(signalEnableButton);
        toolBar.add(signalDisableButton);
        toolBar.addSeparator();
        toolBar.add(zoomInButton);
        toolBar.add(zoomOutButton);
        toolBar.add(homeButton);
        toolBar.addSeparator();

        //toolBar.add(animationButton);
        //toolBar.add(animationStopButton);
        //toolBar.addSeparator();

        toolBar.add(scheduleButton);
        snapShotScheduleButton.setToolTipText("Create Communication Schedule Snapshots");
        toolBar.add(snapShotScheduleButton);
        compareScheduleButton.setToolTipText("Compare the Current Schedule with its Snapshots");
        toolBar.add(compareScheduleButton);
        toolBar.add(scheduleMsgButton);
        toolBar.addSeparator();
        toolBar.add(connectButton);
        toolBar.add(disConnectButton);
        toolBar.addSeparator();
        testButton.setToolTipText("Test the network layer library");
        toolBar.add(testButton);
        toolBar.add(remainLabel);
         */

        // configure the buttons and their action listeners
        saveButton.setEnabled(false);
        newButton.setEnabled(false);
        openButton.setEnabled(false);
        randomButton.setEnabled(false);
        refreshButton.setEnabled(false);
        addAPButton.setEnabled(false);
        addRouterButton.setEnabled(false);
        addDeviceButton.setEnabled(false);
        addHHButton.setEnabled(false);
        addBarrierButton.setEnabled(true);

        zoomInButton.setEnabled(false);
        zoomOutButton.setEnabled(false);
        homeButton.setEnabled(false);
        animationButton.setEnabled(true);
        animationStopButton.setEnabled(true);
        scheduleMsgButton.setEnabled(false);
        disConnectButton.setEnabled(false);

        pruneViewButton.addActionListener(prune);
        completeViewButton.addActionListener(complete);
        refreshButton.addActionListener(refresh);
        newButton.addActionListener(newListener);
        randomButton.addActionListener(randomListener);
        saveButton.addActionListener(saveListener);
        openButton.addActionListener(openListener);
        addAPButton.addActionListener(addNodeListener);
        addDeviceButton.addActionListener(addNodeListener);
        addRouterButton.addActionListener(addNodeListener);
        addHHButton.addActionListener(addNodeListener);
        addBarrierButton.addActionListener(addBarrierListener);

        signalEnableButton.addActionListener(signalEnableListener);
        signalDisableButton.addActionListener(signalDisableListener);

        // add the action listener for the zoom out button
        zoomOutButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                nwkVisualizer.getScaler().scale(nwkVisualizer.getViewer(), 1 / 1.1f, nwkVisualizer.getViewer().getCenter());
                nwkVisualizer.decreaseScaleNum();
            }
        });

        // add the action listener for the zoom in button
        zoomInButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                nwkVisualizer.getScaler().scale(nwkVisualizer.getViewer(), 1.1f, nwkVisualizer.getViewer().getCenter());
                nwkVisualizer.increaseScaleNum();
            }
        });

        // add the action listener for the home button, return the scale size to be the original one
        homeButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {

                if (nwkVisualizer.getScaleNum() < 0)
                {
                    for (int i = 0; i < (-nwkVisualizer.getScaleNum()); i++)
                    {
                        nwkVisualizer.getScaler().scale(nwkVisualizer.getViewer(), 1.1f, nwkVisualizer.getViewer().getCenter());
                    }
                }
                else
                {
                    if (nwkVisualizer.getScaleNum() > 0)
                    {
                        for (int j = 0; j < (nwkVisualizer.getScaleNum()); j++)
                        {
                            nwkVisualizer.getScaler().scale(nwkVisualizer.getViewer(), 1 / 1.1f, nwkVisualizer.getViewer().getCenter());
                        }
                    }
                }
                nwkVisualizer.setScaleNum(0);
            }
        });

        // add the action listener for the animation button. Do the animation
        animationButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                AnimationFunction();
            }
        });

        // add the action listener for the animation button
        animationStopButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                AnimationStopFunction();
            }
        });

        // add the action listener for the test button. Testing function can be put here
        testButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                int isWorking = 0;
                try
                {
                    isWorking = cmdProcessor.testNetworkLayer();
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (isWorking == 1)
                {
                    // IOException
                }
                else
                {
                    if (isWorking == 2)
                    {
                        // InterruptedException
                    }
                }
            }
        });

        // add the action listener for the schedule button. It generates the schedule for the network based on the constructed routing graphs
        scheduleButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
            }
        });

        // add the action listener for the schedule message button. To be implemented
        scheduleMsgButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
            }
        });

        // add the action listener for the connect button.
        connectButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {

                // initialize the command processor, the update timer and the command sender
                updater = new TimerUpdater(mainFrame, 2000, nodeTableControl, edgeTableControl, msgTableControl, statsTableControl, graphTableControl, devListTableControl);

                msgTableControl.setCmdProcessor(cmdProcessor);

                refreshThread = new Thread(updater);
                refreshThread.start();
            }
        });

        // add the action listener for the disconnect button
        disConnectButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                // stops the command processor
                cmdProcessor.stopCmdProcessor();;
                /*******************************************/
                // note: this is different from the original one
                // the former one is disconnect from the gateway, here is disconnect with network manager
            }
        });

        // add the action listener for broadcast link view button
        broadcastLinkViewButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {

                // update the network broadcast graph
                topology.ClearBroadcastGraph();
                topology.UpdateBroadcastGraph();
                // enable the broadcast graph predicate
                nwkVisualizer.enableBroadcastGraphPredicate();
                // update the network visualizer to show the broadcast links
                nwkVisualizer.getViewer().repaint();
            }
        });

        // add the action listener for the downlink graph view button
        downLinkViewButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                nwkVisualizer.enableFromGatewayGraphPredicate();
                nwkVisualizer.getViewer().repaint();
            }
        });

        // add the action listener for the uplink view button
        upLinkViewButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                nwkVisualizer.enableUpLinkGraphPredicate();
                nwkVisualizer.getViewer().repaint();
            }
        });

        // add the action listener for the snapshot graph button
        snapShotGraphButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {

                // keep a snapshot for the uplink graph, the broadcast graph and the downlink graph for each device
                topology.SnapShotUpLinkGraph();
                topology.SnapShotBroadcastGraph();
                topology.SnapShotDownLinkGraph();
            }
        });

        // add the action listener for the snapshot schedule button
        snapShotScheduleButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                // keep a snapshot for the superframes and the node schedule.
                /*    topology.SnapShotSuperframes();
                topology.SnapShotNodeSchedules();
                 */
            }
        });

        // add the action listener for the compare schedule button
        compareScheduleButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                // compare the network schedule and the previous snapshot
                //GUIScheduleComparison compare = new GUIScheduleComparison(topology, cmdProcessor);
                // display the comparison of the two network schedules
                // compare.Display();
            }
        });

        // initialize the remaining GUI components
        jMenuNew.addActionListener(newListener);
        jMenuSave.addActionListener(saveListener);
        jMenuOpen.addActionListener(openListener);
        jMenuSaveAs.addActionListener(saveListener);
        jMenuAbout.addActionListener(aboutListener);
        jMenuConfig.addActionListener(configListener);
        jMenuGUIConfig.addActionListener(guiConfigListener);
        jMenuConnectionSetting.addActionListener(connectionSettingListener);
        jMenuDisconnect.addActionListener(disconnectListener);
        //jMenuConnect.addActionListener(connectListener);


        // jMenuConnection.addActionListener(connectionListener);

        GraphZoomScrollPane viewScrollPane = new GraphZoomScrollPane(nwkVisualizer.getViewer());
        JScrollPane statScrollPane = new JScrollPane(staText);
        JScrollPane debugScrollPane = new JScrollPane(debugText);
        JScrollPane sendMsgPane = new JScrollPane(sendMsgBoard);
        JScrollPane recvMsgPane = new JScrollPane(recvMsgBoard);

        JScrollPane tableScrollPane = new JScrollPane(nodeTableControl.getTable());
        JScrollPane tableScrollPane2 = new JScrollPane(edgeTableControl.getTable());
        JScrollPane tableScrollPane3 = new JScrollPane(msgTableControl.getTable());
        JScrollPane tableScrollPane4 = new JScrollPane(statsTableControl.getTable());
        JScrollPane tableScrollPane5 = new JScrollPane(graphTableControl.getTable());
        JScrollPane tableScrollPane6 = new JScrollPane(devListTableControl.getTable());

        JTabbedPane tabbedTablePane = new JTabbedPane();
        tabbedTablePane.addTab("Vertex", tableScrollPane);
        tabbedTablePane.addTab("Edge", tableScrollPane2);
        tabbedTablePane.addTab("Statistics", tableScrollPane4);
        tabbedTablePane.addTab("Graph Info", tableScrollPane5);
        tabbedTablePane.addTab("Device List", tableScrollPane6);

        JTabbedPane tabbedOutputPane = new JTabbedPane();
        tabbedOutputPane.addTab("Debug", debugScrollPane);
        tabbedOutputPane.addTab("Output", statScrollPane);
        tabbedOutputPane.addTab("Send", sendMsgPane);
        tabbedOutputPane.addTab("Recv", recvMsgPane);

        JTabbedPane tabbedViewPane = new JTabbedPane();
        tabbedViewPane.addTab("Visualizer", viewScrollPane);
        tabbedViewPane.addTab("Message", tableScrollPane3);

        JPanel treeTablePanel = new JPanel();
        treeTablePanel.setLayout(new GridLayout(1, 1));
        treeTablePanel.add(tabbedTablePane);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(tabbedViewPane, BorderLayout.CENTER);
        
        // ----- Add the toolbar to show "Topology": General / Uplink / Downlink buttons -----
        // Do not show the buttons to select uplink/downlink graph; these are currently not working due to:
            // 1. (new link scheduling means GUI does not know of any changes to uplink/downlink)
            // 2. when GUI connects, ManagerCore only sends general graph, currently does not send uplink/downlink graph info
        // mainFrame.add(toolBar, BorderLayout.NORTH); 
        
        // mainFrame.add(tabbedOutputPane, BorderLayout.SOUTH); // do not show the debug/output/send/recv text areas / tabs
        mainFrame.add(treeTablePanel, BorderLayout.EAST);
        mainFrame.pack();

        // put the icon to the system tray when the minimized button on the frame is pressed
        systemTray = SystemTray.getSystemTray();
        /////////////////////////////////////////////////
        // here to change the icon next to the title

        getFrame().setIconImage(new ImageIcon(getClass().getResource("resources/nm.png")).getImage());
        icon = new ImageIcon(getClass().getResource("resources/nm.png")).getImage();
        //trayIcon = new TrayIcon(icon, "Network Manager");
        trayIcon = new TrayIcon(icon, "Gateway");

        this.getFrame().addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowIconified(WindowEvent e)
            {
                if (isTaskBar == false)
                {

                    try
                    {
                        // add the system tray icon
                        systemTray.remove(trayIcon);
                        systemTray.add(trayIcon);
                    }
                    catch (AWTException ex)
                    {
                        Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    mainFrame.dispose();
                }
                else
                {
                    systemTray.remove(trayIcon);
                }
            }
        });

        // double click on the trayIcon and the main frame of the network manager will return.
        trayIcon.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    mainFrame.setExtendedState(Frame.NORMAL);
                }
                mainFrame.setVisible(true);
            }
        });
        
    } // end initMeshSimulator()

    /**
     * this function initializes the logger
     */
    private void initLogger() throws IOException
    {
        Logger nmLogger = Logger.getLogger("");
        nmLogger.setLevel(Level.INFO);
        nmLogFile = new FileHandler("nmlog.txt");
        nmLogFile.setFormatter(new SimpleFormatter());
        nmLogger.addHandler(nmLogFile);
    }

    /*
     * this function initializes the connection between the gateway and the network manager
     */
    private void initConnection()
    {
        // initialize the command processor and the command sender
        // public GUICmdProcessor(JFrame mainframe, JTextArea sendBoard, JTextArea recvBoard, GUITopology topo, NetworkVisualizer vis)

        cmdProcessor = new GUICmdProcessor(this, mainFrame, sendMsgBoard, recvMsgBoard, topology, nwkVisualizer);
        cmdProcessorThread = new Thread(cmdProcessor);
        cmdProcessorThread.start();

        cmdSender = new GUICmdSender(this, topology);
        cmdSenderThread = new Thread(cmdSender);
        cmdSenderThread.start();
        cmdProcessor.setCmdSender(cmdSender);


        nwkVisualizer.setCmdProcessor(cmdProcessor);
        msgTableControl.setCmdProcessor(cmdProcessor);
        statsTableControl.setCmdProcessor(cmdProcessor);


        updater = new TimerUpdater(mainFrame, 2000, nodeTableControl, edgeTableControl, msgTableControl, statsTableControl, graphTableControl, devListTableControl);

        refreshThread = new Thread(updater);
        refreshThread.start();

        ////////////////////////

        this.tcpManager = new TcpManager(this.cmdProcessor);
    }

    /**
     * this function closes the gateway by sending the customized command to the Gateway
     * @throws InterruptedException interrupted exception
     */
    public void CloseGateway() throws InterruptedException
    {
        cmdProcessor.closeGateway();
    }

    /**
     * this disables the "Connect" item under "File" once we successfully connect to ManagerCore
     * and enables the "Disconnect" item.
     */
    public void setStateConnected()
    {
        jMenuConnectionSetting.setEnabled(false);
        jMenuDisconnect.setEnabled(true);
    }

    /**
     * this enables the "Connect" item under "File" once we successfully connect to ManagerCore
     * and disables the "Disconnect" item.
     */
    public void setStateDisonnected()
    {
        jMenuConnectionSetting.setEnabled(true);
        jMenuDisconnect.setEnabled(false);
    }

    /**
     *  This is used to get a reference to TcpManager, mostly so we can
     * call TcpManager.sendToNM().
     * @return reference to TcpManager
     */
    public TcpManager getTcpManager()
    {
        return this.tcpManager;
    }

    /**
     * the constructor function.
     * @param app the single frame application
     * @throws IOException the I/O exception
     * @throws AWTException the AWT exception
     */
    public MeshSimulator(SingleFrameApplication app) throws IOException, AWTException
    {
        super(app);
        /*try
        {
            ManagerConfig.LoadFromXML(); /disable Loading from XML, it is not used in any other location.
        }
        catch (ParserConfigurationException ex)
        {
            Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        // initialize the GUI components
        initComponents();
        // initialize the main frame
        initMeshSimulator();
        // initialize the connection between the network manager and the gateway
        initConnection();
        // initialize the logger
        initLogger();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        //int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        int messageTimeout = 5000;
        messageTimer = new Timer(messageTimeout, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        //int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        int busyAnimationRate = 30;
        for (int i = 0; i < busyIcons.length; i++)
        {
            //busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
            busyIcons[i] = new ImageIcon(getClass().
                    getResource("resources/busyicons/busy-icon" + i + ".png"));
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {

            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                }
                else
                {
                    if ("done".equals(propertyName))
                    {
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                    }
                    else
                    {
                        if ("message".equals(propertyName))
                        {
                            String text = (String) (evt.getNewValue());
                            statusMessageLabel.setText((text == null) ? "" : text);
                            messageTimer.restart();
                        }
                        else
                        {
                            if ("progress".equals(propertyName))
                            {
                                int value = (Integer) (evt.getNewValue());
                                progressBar.setVisible(true);
                                progressBar.setIndeterminate(false);
                                progressBar.setValue(value);
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * the complete network topology listener
     */
    private class CompleteTopoListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            // display the complete network topology
            nwkVisualizer.disableVertexPredicate();
            nwkVisualizer.disableEdgePredicate();
            mainFrame.repaint();
        }
    }

    /**
     * the prunning network topology listener
     */
    private class PruneTopoListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {

            // create the graphprunedialog
            GraphPruneDialog dialog = new GraphPruneDialog(mainFrame, topology, nwkVisualizer);
            dialog.setVisible(true);
        }
    }

    /**
     * the enable signal strength in the network topology listener
     */
    private class SignalEnableListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            // enable the signal strength in the network topology
            nwkVisualizer.enableEdgeLabel();
            mainFrame.repaint();
        }
    }

    /**
     * the disable signal strength in the network topology listener
     */
    private class SignalDisableListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            // disable the signal strength in the network topology
            nwkVisualizer.disableEdgeLabel();
            mainFrame.repaint();
        }
    }

    /**
     * the add node in the network topology listener
     */
    private class AddNodeListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            // create the add node dialog according to the pressed button
            if (event.getSource() == addAPButton)
            {
                VertexCreateDialog dialog = new VertexCreateDialog(mainFrame, "AP", topology, nwkVisualizer.getLayout());
                dialog.setVisible(true);
            }

            if (event.getSource() == addDeviceButton)
            {
                VertexCreateDialog dialog = new VertexCreateDialog(mainFrame, "DEVICE", topology, nwkVisualizer.getLayout());
                dialog.setVisible(true);
            }

            if (event.getSource() == addRouterButton)
            {
                VertexCreateDialog dialog = new VertexCreateDialog(mainFrame, "ROUTER", topology, nwkVisualizer.getLayout());
                dialog.setVisible(true);
            }

            if (event.getSource() == addHHButton)
            {
                VertexCreateDialog dialog = new VertexCreateDialog(mainFrame, "HANDHELD", topology, nwkVisualizer.getLayout());
                dialog.setVisible(true);
            }
        }
    }

    /**
     * the add barrier listener
     */
    private class AddBarrierListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            // create the dialog for adding barrier in the network topology
            BarrierCreateDialog dialog = null;
            try
            {
                dialog = new BarrierCreateDialog(mainFrame, topology, nwkVisualizer, cmdProcessor);
            }
            catch (IOException ex)
            {
                Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            dialog.setVisible(true);
        }
    }

    /**
     * the refresh listener
     */
    private class RefreshListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {

            // refresh all the tables in the network manager
            int numVertex = topology.g.getVertexCount();
            staText.append("The number of Vertex is " + numVertex + "\n\n");
            //treeControl.LoadTree();
            nodeTableControl.LoadTable();
            edgeTableControl.LoadTable();
            msgTableControl.LoadTable();
            statsTableControl.LoadTable();
            graphTableControl.LoadTable();
            devListTableControl.LoadTable();

            //topology.reEvaluateAllSignalStrength(nwkVisualizer.getLayout());
            mainFrame.repaint();
        }
    }

    /**
     * the new network topology listener
     */
    private class NewListener implements ActionListener
    {

        public NewListener(JTextArea jta)
        {
            statTextArea = jta;
        }

        public void actionPerformed(ActionEvent event)
        {

            // create a new network topology
            NetworkNode.clearCurrentId();
            topology.clearUp(nwkVisualizer);
            sManager = new SecurityManager();
            topology = new GUITopology(false, 2, 1);

            // update the network visualizer
            nwkVisualizer.setTopology(topology);
            nwkVisualizer.updateBarrierPlugin(topology);
            nwkVisualizer.disableEdgePredicate();
            nwkVisualizer.disableVertexPredicate();

            // update the tables in the network manager
            nodeTableControl.setTopology(topology, staText);
            edgeTableControl.setTopology(topology, staText);
            statsTableControl.setTopology(topology, staText);
            graphTableControl.setTopology(topology, staText);
            devListTableControl.setTopology(topology, staText);
            nwkVisualizer.getViewer().repaint();
        }
        private JTextArea statTextArea;
    }

    /**
     * the about listener for the network manager
     */
    private class AboutListener implements ActionListener
    {

        public AboutListener(JTextArea jta)
        {
            statTextArea = jta;
        }

        public void actionPerformed(ActionEvent event)
        {
            if (aboutBox == null)
            {
                // create a new ManagerAboutBox for the network manager
                JFrame mainframe = ManagerApp.getApplication().getMainFrame();
                aboutBox = new ManagerAboutBox(mainframe);
                aboutBox.setLocationRelativeTo(mainframe);
            }
            ManagerApp.getApplication().show(aboutBox);
        }
        private JTextArea statTextArea;
    }

    /**
     * the network manager configuration listener
     * This opens the configuration window accessed by "Option > Network Configuration"
     */
    private class ConfigListener implements ActionListener
    {

        public ConfigListener(JTextArea jta)
        {
            statTextArea = jta;
        }

        public void actionPerformed(ActionEvent event)
        {
            //If we are not connected to manager, don't show the window
            if(ManagerConfig.isDataReady() == false)
            {
                JOptionPane.showMessageDialog(null, "Please connect to the Manager Service or wait for the connection to complete and try again.");
                return;
            }

            JFrame mainframe = ManagerApp.getApplication().getMainFrame();
            // create a manager configuration box
            ManagerConfigBox configBox = new ManagerConfigBox(mainframe);
            configBox.setCmdProcessor(cmdProcessor);
            configBox.setLocationRelativeTo(mainframe);
            ManagerApp.getApplication().show(configBox);
        }
        private JTextArea statTextArea;
    }

    /**
     * the network manager GUI configuration listener
     */
    private class GUIConfigListener implements ActionListener
    {

        private MeshSimulator localMS;

        public GUIConfigListener(MeshSimulator ms)
        {
            this.localMS = ms;
        }

        public void actionPerformed(ActionEvent event)
        {
            // create the network manager GUI configuration box
            ManagerGUIConfigBox guiConfigBox = new ManagerGUIConfigBox(this.localMS);
        }
    }

    //This is the action for "Connect" jMenuItem under "File"
    private class ConnectionSettingListener implements ActionListener
    {

        private MeshSimulator localMS;

        public ConnectionSettingListener(MeshSimulator ms)
        {
            this.localMS = ms;
        }

        public void actionPerformed(ActionEvent e)
        {
            JFrame mainframe = ManagerApp.getApplication().getMainFrame();
            // create a manager configuration box
            ConnectionSettingBox connectionsettingBox = new ConnectionSettingBox(mainframe, true);
            connectionsettingBox.setMeshSimulator(localMS);
            connectionsettingBox.setVisible(true);

        }
    }

    //This is the action for "Disconnect" jMenuItem under "File"
    private class DisconnectListener implements ActionListener
    {

        private MeshSimulator localMS;

        public DisconnectListener(MeshSimulator ms)
        {
            this.localMS = ms;
        }

        public void actionPerformed(ActionEvent e)
        {
            this.localMS.getTopology().removeAllVertices();
            this.localMS.getTopology().RmAllItemFromDeviceWhiteList();
            this.localMS.getTopology().RmAllItemFromDeviceBlackList();
            this.localMS.setStateDisonnected();
            this.localMS.getTcpManager().disconnect();
        }
    }

    /**
     * the random network topology construction listener
     */
    private class RandomListener implements ActionListener
    {

        public RandomListener(JTextArea jta)
        {
            statTextArea = jta;
        }

        public void actionPerformed(ActionEvent event)
        {

            // update the network topology, the network visualizer, the tables
            NetworkNode.clearCurrentId();
            topology.clearUp(nwkVisualizer);
            sManager = new SecurityManager();
            topology = new GUITopology(false, 2, 1);

            nwkVisualizer.setTopology(topology);
            nwkVisualizer.updateBarrierPlugin(topology);
            nwkVisualizer.disableEdgePredicate();
            nwkVisualizer.disableVertexPredicate();

            //treeControl.setTopology(topology);
            nodeTableControl.setTopology(topology, staText);
            edgeTableControl.setTopology(topology, staText);
            statsTableControl.setTopology(topology, staText);
            graphTableControl.setTopology(topology, staText);
            devListTableControl.setTopology(topology, staText);
            statTextArea.append("\nI am in the newListener : " + topology.g.getVertexCount());

            // add two APs into the topology. The nicknames and uniqueIDs are randomly generated by the security manager.
            char apAddress = 0xF981;
            char[] apUniqueID =
            {
                0x001B, 0x1E00, 0x0000, 0x0001
            };
            NetworkNode ap = new NetworkNode(true, true, new NodeUniqueID(apUniqueID), new NodeNickname(apAddress), NetworkNode.getCurrentId(), NodeType.AP, (new ScanPeriod(ScanPeriod._0_ms)));
            topology.g.addVertex(ap);

            // add to the device white list
            topology.AddItemToDeviceWhiteList(new NodeUniqueID(apUniqueID));

            char ap2Address = 0xF982;
            char[] ap2UniqueID =
            {
                0x001B, 0x1E00, 0x0000, 0x0002
            };
            NetworkNode ap2 = new NetworkNode(true, true, new NodeUniqueID(ap2UniqueID), new NodeNickname(ap2Address), NetworkNode.getCurrentId(), NodeType.AP, (new ScanPeriod(ScanPeriod._0_ms)));
            topology.g.addVertex(ap2);

            // add to the device white list
            topology.AddItemToDeviceWhiteList(new NodeUniqueID(ap2UniqueID));

            Layout<NetworkNode, NetworkEdge> lo = nwkVisualizer.getViewer().getGraphLayout();
            topology.addEdgesforNode(ap, lo);
            topology.addEdgesforNode(ap2, lo);

            // add ten devices into the network topology with randomly generated location
            for (int i = 0; i < 10; i++)
            {
                NetworkNode device = new NetworkNode(true, true, RandomManager.GenRanUniqueID(), RandomManager.GenRanNickname(), NetworkNode.getCurrentId(), NodeType.DEVICE, RandomManager.GenRanScanPeriod());
                topology.g.addVertex(device);

                // add the device to the white device list
                topology.AddItemToDeviceWhiteList(device.getNodeUniqueID());

                // randomly set the location of the device
                double xPosition = (Math.random() * 450);
                double yPosition = (Math.random() * 450);

                Point2D.Double location = new Point2D.Double(xPosition, yPosition);
                nwkVisualizer.getLayout().setLocation(device, location);
                topology.addEdgesforNode(device, lo);
            }
            nwkVisualizer.getViewer().repaint();
        }
        private JTextArea statTextArea;
    }

    /**
     * the save the network topology to the file listener
     */
    private class FileSaveListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            chooser.setCurrentDirectory(new File(".\\src\\mesh\\topology"));
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showSaveDialog(mainPanel);

            if (result == JFileChooser.APPROVE_OPTION)
            {
                String name = chooser.getSelectedFile().getPath();

                try
                {
                    PrintWriter out = new PrintWriter(name + ".sav");
                    // call the saveToFile function in the topology class to save the netwwork topology into the given file
                    topology.saveToFile(out, nwkVisualizer.getLayout());
                    out.close();

                }
                catch (FileNotFoundException ex)
                {
                    Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * the open the network topology from the file listener
     */
    private class FileOpenListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            chooser.setCurrentDirectory(new File(".\\src\\mesh\\topology"));
            int result = chooser.showOpenDialog(mainPanel);

            if (result == JFileChooser.APPROVE_OPTION)
            {
                String fileName = chooser.getSelectedFile().getPath();
                // new the network graph
                DirectedSparseMultigraph graph = new DirectedSparseMultigraph<NetworkNode, NetworkEdge>();

                try
                {
                    //read in the node information and the edge information.
                    Scanner in = new Scanner(new FileReader(fileName));
                    int nVertex = in.nextInt();
                    in.nextLine();

                    NetworkNode[] vertexNode = new NetworkNode[nVertex];
                    Point2D[] vertexPoints = new Point2D[nVertex];

                    for (int i = 0; i < nVertex; i++)
                    {
                        String line = in.nextLine();
                        String[] tokens = line.split("\t");
                        int nodeId = Integer.parseInt(tokens[0]);
                        double xPosition = Double.parseDouble(tokens[1]);
                        double yPosition = Double.parseDouble(tokens[2]);
                        String nodeUniqueID = tokens[3];
                        String nodeNickName = tokens[4];

                        String type = tokens[5];
                        String scanPeriod = tokens[6];
                        String enabled = tokens[7];
                        String powered = tokens[8];

                        vertexPoints[i] = new Point2D.Double(xPosition, yPosition);
                        NodeUniqueID uniqueID = new NodeUniqueID(nodeUniqueID);
                        NodeNickname nickName = new NodeNickname(nodeNickName);

                        NodeType nodeType = NodeType.valueOf(type);
                        ScanPeriod period = ScanPeriod.stringToScanPeriod(scanPeriod);
                        boolean isEnabled = true;
                        boolean isPowered = true;

                        if (enabled.equals("false"))
                        {
                            isEnabled = false;
                        }

                        if (powered.equals("false"))
                        {
                            isPowered = false;
                        }

                        vertexNode[i] = new NetworkNode(isEnabled, isPowered, uniqueID, nickName, nodeId, nodeType, period);
                        graph.addVertex(vertexNode[i]);

                        // add the device to the white device list
                        topology.AddItemToDeviceWhiteList(vertexNode[i].getNodeUniqueID());
                    }

                    in.nextLine();
                    int nEdge = in.nextInt();
                    in.nextLine();

                    NetworkEdge[] edgeNode = new NetworkEdge[nEdge];
                    for (int j = 0; j < nEdge; j++)
                    {
                        String line = in.nextLine();
                        String[] tokens = line.split("\t");
                        int edgeId = Integer.parseInt(tokens[0]);
                        int srcNodeId = Integer.parseInt(tokens[1]);
                        int destNodeId = Integer.parseInt(tokens[2]);
                        double weight = Double.parseDouble(tokens[3]);
                        double capacity = Double.parseDouble(tokens[4]);
                        double signal = Double.parseDouble(tokens[5]);

                        edgeNode[j] = new NetworkEdge(edgeId, weight, capacity, signal);
                        NetworkNode src = null;
                        NetworkNode dest = null;

                        for (int k = 0; k < nVertex; k++)
                        {
                            if (vertexNode[k].getID() == srcNodeId)
                            {
                                src = vertexNode[k];
                            }

                            if (vertexNode[k].getID() == destNodeId)
                            {
                                dest = vertexNode[k];
                            }

                            if (dest != null && src != null)
                            {
                                break;
                            }
                        }

                        graph.addEdge(edgeNode[j], src, dest, EdgeType.DIRECTED);
                    }

                    // update the data structures in the network manager
                    topology.clearUp(nwkVisualizer);
                    sManager = new SecurityManager();
                    topology = new GUITopology(graph);
                    NetworkNode.setCurrentId(graph.getVertexCount() + 1);

                    nwkVisualizer.updateBarrierPlugin(topology);
                    nwkVisualizer.disableEdgePredicate();
                    nwkVisualizer.disableVertexPredicate();

                    nwkVisualizer.setTopology(topology);

                    // update the tables in the network manager
                    nodeTableControl.setTopology(topology, staText);
                    edgeTableControl.setTopology(topology, staText);
                    statsTableControl.setTopology(topology, staText);
                    graphTableControl.setTopology(topology, staText);
                    devListTableControl.setTopology(topology, staText);

                    // change the location for all the nodes
                    for (int t = 0; t < nVertex; t++)
                    {
                        nwkVisualizer.getLayout().setLocation(vertexNode[t], vertexPoints[t]);
                    }

                    nwkVisualizer.getViewer().repaint();

                }
                catch (FileNotFoundException ex)
                {
                    Logger.getLogger(MeshSimulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * this function sets the network manager frame to be minimized to the system tray
     */
    public void SetToTaskBar()
    {
        this.isTaskBar = true;
    }

    /**
     * this function sets the network manager frame to not be able to be minimized to the system tray
     */
    public void SetToSystemTray()
    {
        this.isTaskBar = false;
    }

    /**
     * this function checks if the network manager frame can be minimized to the system tray
     * @return true if the network manager frame can be minimized to the system tray
     */
    public boolean GetIsTaskBar()
    {
        return this.isTaskBar;
    }

    /**
     * this function is the animation testing funciton
     */
    void AnimationFunction()
    {
        timer = new java.util.Timer();
        BarrierAnimation animation = new BarrierAnimation();
        int delay = 500;
        int period = 500;
        timer.schedule(animation, delay, period);
    }

    /*
     * this function stops the animation function
     */
    void AnimationStopFunction()
    {
        if (timer != null)
        {
            timer.cancel();
        }
    }
    
    public void displayTrialEndedNotification()
    {
        JOptionPane.showMessageDialog(this.getFrame(), 
                "Thank you for using the trial version of AwiaTech's WirelessHART network.\n"
                        + "To continue using the network, please restart the network and all wireless devices.\n"
                        + "Visit www.AwiaTech.com for more information.",
                "AwiaTech WirelessHART network",
                JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * this class defines the behavior of the barrier animation task
     * @author Song Han
     */
    class BarrierAnimation extends TimerTask
    {

        public void run()
        {

            if (topology.barriers != null)
            {
                StaticBarrier bar = null;
                for (int i = 0; i < topology.barriers.size(); i++)
                {
                    // change the location of each barrier in the network periodically
                    bar = topology.barriers.get(i);

                    int x = (int) (Math.random() * 400);
                    int y = (int) (Math.random() * 400);

                    int currentX = bar.getX();
                    int currentY = bar.getY();

                    x = ((int) (currentX * 0.9 + x * 0.1)) % 400;
                    y = ((int) (currentY * 0.9 + x * 0.1)) % 400;

                    bar.setX(x);
                    bar.setY(y);

                    nwkVisualizer.getViewer().repaint();
                }
            }
        }
    }

    public JFrame getMainFrame()
    {
        return this.mainFrame;
    }

    public NetworkVisualizer getVisualizer()
    {
        return this.nwkVisualizer;
    }

    public GUICmdProcessor getCmdProcessor()
    {
        return this.cmdProcessor;
    }

    public GUITopology getTopology()
    {
        return this.topology;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        optionMenu = new javax.swing.JMenu();
        jMenuNew = new javax.swing.JMenuItem();
        jMenuOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenuItem();
        jMenuConfig = new javax.swing.JMenuItem();
        jMenuGUIConfig = new javax.swing.JMenuItem();
        jMenuConnectionSetting = new javax.swing.JMenuItem();
        jMenuDisconnect = new javax.swing.JMenuItem();
        jMenuConnect = new javax.swing.JMenuItem();

        //   jMenuConnection = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tree.setName("tree"); // NOI18N
        jScrollPane1.setViewportView(tree);

        mainPanel.add(jScrollPane1, java.awt.BorderLayout.LINE_END);

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        mainPanel.add(statusPanelSeparator, java.awt.BorderLayout.PAGE_END);

        menuBar.setName("menuBar");

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(manager.ManagerApp.class).getContext().getResourceMap(MeshSimulator.class);
        fileMenu.setText("File");
        fileMenu.setName("fileMenu");
        menuBar.add(fileMenu);

        jMenuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuNew.setText("New");
        jMenuNew.setName("jMenuNew");
        //fileMenu.add(jMenuNew);

        jMenuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuOpen.setText("Open");
        jMenuOpen.setName("jMenuOpen");
        //fileMenu.add(jMenuOpen);

        jMenuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuSave.setText("Save");
        jMenuSave.setName("jMenuSave");
        //fileMenu.add(jMenuSave);

        jMenuSaveAs.setText("SaveAs");
        jMenuSaveAs.setName("jMenuSaveAs");
        //fileMenu.add(jMenuSaveAs);

        jMenuConnectionSetting.setText("Connect");
        jMenuConnectionSetting.setName("Setting");
        fileMenu.add(jMenuConnectionSetting);

        jMenuDisconnect.setText("Disconnect");
        jMenuDisconnect.setName("Setting");
        jMenuDisconnect.setEnabled(false);
        fileMenu.add(jMenuDisconnect);

        jMenuConnect.setText("Connect");
        jMenuConnect.setName("Connect");
        //  fileMenu.add(jMenuConnect);

        /*********************************************************/
        // this is the option part
        jMenuConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuConfig.setText("Network Configuration");
        jMenuConfig.setName("jMenuConfig");
        optionMenu.add(jMenuConfig);

        jMenuGUIConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuGUIConfig.setText("Preferences");
        jMenuGUIConfig.setName("jMenuGUIConfig");
        optionMenu.add(jMenuGUIConfig);

        /*
        jMenuConnection.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuConnection.setText("Connect to network manager");
        jMenuConnection.setName("jMenuConnection");
        optionMenu.add(jMenuConnection);
         */

        optionMenu.setText("Option");
        optionMenu.setName("optionMenu");
        menuBar.add(optionMenu);

        jMenuAbout.setText("About");
        jMenuAbout.setName("jMenuAbout");
        helpMenu.add(jMenuAbout);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(manager.ManagerApp.class).getContext().getActionMap(MeshSimulator.class, this);
        exitMenuItem.setAction(actionMap.get("quit"));
        exitMenuItem.setName("exitMenuItem");
        fileMenu.add(exitMenuItem);

        helpMenu.setText("Help");
        helpMenu.setName("helpMenu");
        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addContainerGap().addComponent(statusMessageLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE).addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(statusAnimationLabel).addContainerGap()));
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(statusMessageLabel).addComponent(statusAnimationLabel).addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(3, 3, 3)));

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMenuNew;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSaveAs;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuItem jMenuConfig;
    private javax.swing.JMenuItem jMenuGUIConfig;
    private javax.swing.JMenuItem jMenuConnectionSetting;
    private javax.swing.JMenuItem jMenuConnect; //TODO jMenuConnect is not used, it has been replaced by jMenuConnectionSetting
    private javax.swing.JMenuItem jMenuDisconnect;
    //  private javax.swing.JMenuItem jMenuConnection;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu optionMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JToolBar toolBar;
    private JButton saveButton;
    private JButton newButton;
    private JButton openButton;
    private JButton randomButton;
    private JButton refreshButton;
    private JButton addAPButton;
    private JButton addRouterButton;
    private JButton addDeviceButton;
    private JButton addHHButton;
    private JButton addBarrierButton;
    private JButton completeViewButton;
    private JButton pruneViewButton;
    private JButton upLinkViewButton;
    private JButton downLinkViewButton;
    private JButton broadcastLinkViewButton;
    private JButton snapShotGraphButton;
    private JButton compareGraphButton;
    private JButton snapShotScheduleButton;
    private JButton compareScheduleButton;
    private JButton signalEnableButton;
    private JButton signalDisableButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton homeButton;
    private JButton animationButton;
    private JButton animationStopButton;
    private JButton scheduleButton;
    private JButton scheduleMsgButton;
    private JButton connectButton;
    private JButton disConnectButton;
    private JButton testButton;
    private JLabel topologyLabel;
    private JLabel addLabel;
    private JLabel remainLabel;
    private JLabel viewLabel;
    private JTextArea staText;
    private JTextArea debugText;
    private JFileChooser chooser;

    /* ActionListeners */
    private PruneTopoListener prune;
    private CompleteTopoListener complete;
    private RefreshListener refresh;
    private NewListener newListener;
    private RandomListener randomListener;
    private FileSaveListener saveListener;
    private FileOpenListener openListener;
    private AboutListener aboutListener;
    private ConfigListener configListener;
    private GUIConfigListener guiConfigListener;
    //   private ConnectionListener connectionListener;
    private AddNodeListener addNodeListener;
    private SignalEnableListener signalEnableListener;
    private SignalDisableListener signalDisableListener;
    private ConnectionSettingListener connectionSettingListener;
    //private ConnectListener connectListener;
    private DisconnectListener disconnectListener;
    private AddBarrierListener addBarrierListener;

    /* Data Structures */
    private String titleName = "Manager Client"; //title of ManagerGUI's main window
    public GUITopology topology;
    private SecurityManager sManager;
    private NetworkVisualizer nwkVisualizer;
    private JTextArea sendMsgBoard;
    private JTextArea recvMsgBoard;
    public TableController nodeTableControl;
    public TableController edgeTableControl;
    public TableController msgTableControl;
    public TableController statsTableControl;
    public TableController graphTableControl;
    public TableController devListTableControl;
    private JFrame mainFrame;
    java.util.Timer timer;
    private JDialog aboutBox;
    // for minimize to system tray or task bar
    private boolean isTaskBar = true;
    private Image icon;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    public GUICmdProcessor cmdProcessor;
    Thread cmdProcessorThread;
    GUICmdSender cmdSender;
    Thread cmdSenderThread;
    TcpManager tcpManager;
    GUITcpRecvPort guiListener;
    // RandomManager randManager;
    public TimerUpdater updater;
    public Thread refreshThread;
    static private FileHandler nmLogFile;
}