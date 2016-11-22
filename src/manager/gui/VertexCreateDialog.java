package manager.gui;

import manager.guiApp.NetworkNode;
import manager.guiApp.GUITopology;
import manager.guiApp.NetworkEdge;
import edu.uci.ics.jung.algorithms.layout.Layout;
import java.awt.GridLayout;
import mesh.NodeNickname;
import mesh.NodeUniqueID;

/**
 * This dialog form creates a vertex in the network topology and show in the visualizer
 * @author Song Han
 */
public class VertexCreateDialog extends javax.swing.JDialog
{
    GUITopology topology;
    String deviceType;
    NetworkNode toAdd;
    Layout<NetworkNode, NetworkEdge> layout;
    java.awt.Frame pFrame;

    /**
     * Constructor function for the vertex dialog form
     * @param parent the parent frame of the dialog
     * @param name the name of the device type
     * @param topo the network topology
     * @param lo the layout used
     */
    public VertexCreateDialog(java.awt.Frame parent, String name, GUITopology topo, Layout<NetworkNode, NetworkEdge> lo)
    {
        super(parent, true);
        pFrame = parent;
        topology = topo;
        deviceType = name;
        layout = lo;
        initComponents();
        setTitle("Create " + name);
        this.idFormattedTextField.setValue(0);
        this.naFormattedTextField.setValue(0);
        this.paFormattedTextField.setValue(0.0);
        this.scanPeriodBox.setSelectedIndex(0);
        this.enabledCheckBox.setSelected(false);
        this.poweredCheckBox.setSelected(false);
    }

    /**
     * this function initializes the graphical components in the dialog
     */
    private void initComponents()
    {
        toAdd = new NetworkNode(deviceType);
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        paFormattedTextField = new javax.swing.JFormattedTextField();
        naFormattedTextField = new javax.swing.JFormattedTextField();
        idFormattedTextField = new javax.swing.JFormattedTextField();

        scanPeriodBox = new javax.swing.JComboBox();
        enabledCheckBox = new javax.swing.JCheckBox("Is Enabled");
        poweredCheckBox = new javax.swing.JCheckBox("Is Powered");

        scanPeriodBox.addItem("_0_ms");
        scanPeriodBox.addItem("_250_ms");
        scanPeriodBox.addItem("_1_sec");
        scanPeriodBox.addItem("_2_sec");
        scanPeriodBox.addItem("_4_sec");
        scanPeriodBox.addItem("_8_sec");
        scanPeriodBox.addItem("_16_sec");
        scanPeriodBox.addItem("_32_sec");
        scanPeriodBox.addItem("_1_min_4_sec");
        scanPeriodBox.addItem("_2_min_8_sec");
        scanPeriodBox.addItem("_4_min_16_sec");
        scanPeriodBox.addItem("_8_min_32_sec");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vertex Properties");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });

        jLabel1.setText("Total Power:");
        jLabel2.setText("Nick Name:");
        jLabel3.setText("Scan Period:");
        jLabel6.setText("Unique ID:");

        this.setLayout(new GridLayout(6,2));

        add(jLabel1);
        add(paFormattedTextField);
        add(jLabel2);
        add(naFormattedTextField);
        add(jLabel6);
        add(idFormattedTextField);
        add(jLabel3);
        add(scanPeriodBox);
        add(enabledCheckBox);
        add(jLabel4);
        add(poweredCheckBox);
        add(jLabel5);
        add(jButton1);
        pack();
    }

    /**
     * This function reads the input from the dialog into the vertex class and add it to the network topology and the visualier
     * @param evt action event
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt)
    {
        toAdd.setPowerAll((Double) this.paFormattedTextField.getValue());
        toAdd.setPowerLeft((Double) this.paFormattedTextField.getValue());
        toAdd.setScanPeriod(this.scanPeriodBox.getSelectedItem().toString());
        toAdd.setNodeNickname(new NodeNickname(this.naFormattedTextField.getText()));
        toAdd.setNodeUniqueID(new NodeUniqueID(this.idFormattedTextField.getText()));
        toAdd.setEnabled(enabledCheckBox.isSelected());
        toAdd.setPowered(poweredCheckBox.isSelected());

        // add the vertex into the graph
        topology.GetGraph().addVertex(toAdd);

        // add the device to the white device list
        topology.AddItemToDeviceWhiteList(toAdd.getNodeUniqueID());

        // add the edges to the existing nodes in the graph
        topology.addEdgesforNode(toAdd, layout);
        pFrame.repaint();

        dispose();
    }

    private javax.swing.JFormattedTextField idFormattedTextField;
    private javax.swing.JFormattedTextField naFormattedTextField;
    private javax.swing.JFormattedTextField paFormattedTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;

    private javax.swing.JComboBox scanPeriodBox;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JCheckBox poweredCheckBox;
}
