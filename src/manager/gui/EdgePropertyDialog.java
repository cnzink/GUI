package manager.gui;

import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.NetworkEdge;

/**
 * The dialog form for showing the property of the network edge
 * @author Song Han
 */
public class EdgePropertyDialog extends javax.swing.JDialog
{
    NetworkEdge edge;
    /**
     * Construtor function
     * @param parent the parent frame of the dialog
     * @param edge the reference to the network edge
     */
    public EdgePropertyDialog(java.awt.Frame parent, NetworkEdge edge, GUICmdProcessor cmdProc)
    {
        super(parent, true);
     // initialize the graphic components in the dialog
        initComponents(cmdProc);
        this.edge = edge;
        setTitle("Edge: " + edge.getEdgeId());

        // set the link capacity, the weight of the edge and the signal strength
        this.capFormattedTextField.setValue(edge.getCapacity());
        this.wtFormattedTextField.setValue(edge.getWeight());
        this.ssFormattedTextField.setValue(edge.getSignalStrength());
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

        capFormattedTextField = new javax.swing.JFormattedTextField();
        wtFormattedTextField = new javax.swing.JFormattedTextField();
        ssFormattedTextField = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edge Properties");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt, cmdProc);
            }
        });

        jLabel1.setText("Capacity:");
        jLabel2.setText("Weight:");
        jLabel3.setText("Signal Strength:");

        // use the grid layout for the dialog
        this.setLayout(new GridLayout(4,2));
        
        add(jLabel1);
        add(capFormattedTextField);
        add(jLabel2);
        add(wtFormattedTextField);
        add(jLabel3);
        add(ssFormattedTextField);
        add(jButton1);
        pack();
    }

    /**
     * This function reads in the new capacity and weight of the edge
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt, GUICmdProcessor cmdProc) 
    {
        edge.setCapacity((Double)this.capFormattedTextField.getValue());
        edge.setWeight((Double)this.wtFormattedTextField.getValue());
        int edgeID = edge.getEdgeId();
        try {
            cmdProc.setEdgeProperty(edgeID, (Double)this.capFormattedTextField.getValue(), (Double)this.wtFormattedTextField.getValue());
        } catch (InterruptedException ex) {
        Logger.getLogger(EdgePropertyDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dispose();
    }

    private javax.swing.JFormattedTextField capFormattedTextField;
    private javax.swing.JFormattedTextField wtFormattedTextField;
    private javax.swing.JFormattedTextField ssFormattedTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
}

