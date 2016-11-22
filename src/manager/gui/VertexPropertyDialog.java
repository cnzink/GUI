package manager.gui;

import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.NetworkNode;

/**
 * The dialog form for showing the property of the vertex
 * @author Song Han
 */
public class VertexPropertyDialog extends javax.swing.JDialog
{
    NetworkNode vertex;
    /**
     * Constructor function
     * @param parent the parent frame of the dialog
     * @param v the reference to the network node
     */
    public VertexPropertyDialog(java.awt.Frame parent, NetworkNode v, GUICmdProcessor cmdProc)
    {
        super(parent, true);
        initComponents(cmdProc);
        this.vertex = v;
        setTitle("Node: " + v.getID());
        this.plFormattedTextField.setValue(v.getPowerLeft());
        this.paFormattedTextField.setValue(v.getPowerAll());
        this.scanPeriodBox.setSelectedItem(v.getScanPeriod().getScanPeriod());
    }

    /**
     * This function initializes the graphical components in the dialog
     */
    private void initComponents(final GUICmdProcessor cmdProc)
    {
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        plFormattedTextField = new javax.swing.JFormattedTextField();
        paFormattedTextField = new javax.swing.JFormattedTextField();
        scanPeriodBox = new javax.swing.JComboBox();

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
                okButtonHandler(evt, cmdProc);
            }
        });

        jLabel1.setText("Remained Power:");
        jLabel2.setText("Total Power:");
        jLabel3.setText("Scan Period");

        this.setLayout(new GridLayout(4,2));

        add(jLabel1);
        add(plFormattedTextField);
        add(jLabel2);
        add(paFormattedTextField);
        add(jLabel3);
        add(scanPeriodBox);
        add(jButton1);
        pack();
    }

    /**
     * This function reads in the power and scan period information from the dialog and update the vertex
     * @param evt the action event
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt, GUICmdProcessor cmdProc)
    {
        vertex.setPowerLeft((Double)this.plFormattedTextField.getValue());
        vertex.setPowerAll((Double)this.paFormattedTextField.getValue());
        vertex.setScanPeriod(this.scanPeriodBox.getSelectedItem().toString());
        
        char nickName = vertex.getNodeNickname().GetNickName();
        double powerLeft = (Double)this.plFormattedTextField.getValue();
        double powerAll = (Double)this.paFormattedTextField.getValue();
        String scanPeriod = this.scanPeriodBox.getSelectedItem().toString();
        try {
            if(cmdProc==null)
                System.out.println("VertexPropertyDialog is null");
            cmdProc.setVertexProperty(nickName, powerLeft, powerAll, scanPeriod);
        } catch (InterruptedException ex) {
            Logger.getLogger(VertexPropertyDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dispose();
    }

    private javax.swing.JFormattedTextField plFormattedTextField;
    private javax.swing.JFormattedTextField paFormattedTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;

    private javax.swing.JComboBox scanPeriodBox;
}


