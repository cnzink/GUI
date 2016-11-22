package manager.gui;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.NetworkVisualizer;
import manager.guiApp.GUITopology;

/**
 * The dialog form for creating barriers in the network topology and show in the visualizer
 * @author Song Han
 */
public class BarrierCreateDialog extends javax.swing.JDialog
{
    GUITopology topology;
    StaticBarrier toAdd;
    NetworkVisualizer nwkVisualizer;
    java.awt.Frame pFrame;
    GUICmdProcessor cmdProcessor;

    /**
     * Constructor function for the barrier dialog form
     * @param parent the parent frame of the dialog
     * @param topo the network topology data structure
     * @param visualizer the visualizer of the network
     * @throws IOException I/O exception
     */
    public BarrierCreateDialog(java.awt.Frame parent, GUITopology topo, NetworkVisualizer visualizer, GUICmdProcessor cmdProc) throws IOException
    {
        super(parent, true);
        pFrame = parent;
        topology = topo;
        nwkVisualizer = visualizer;
        cmdProcessor = cmdProc;
        initComponents();
        setTitle("Create Barrier");
        this.xPositionFormattedTextField.setValue(30);
        this.yPositionFormattedTextField.setValue(30);
        this.widthFormattedTextField.setValue(50);
        this.heightFormattedTextField.setValue(50);

        toAdd = new StaticBarrier(nwkVisualizer);
    }

    /**
     * This function initializes the graphical components in the dialog
     */
    private void initComponents() throws IOException
    {
        // the labels for the x position, y position, height and width of the barrier
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        // the input textfield for the x position, y position, height and width of the barrier
        xPositionFormattedTextField = new javax.swing.JFormattedTextField();
        yPositionFormattedTextField = new javax.swing.JFormattedTextField();
        widthFormattedTextField = new javax.swing.JFormattedTextField();
        heightFormattedTextField = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vertex Properties");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });

        jLabel1.setText("X Postion");
        jLabel2.setText("Y Position");
        jLabel3.setText("Width");
        jLabel4.setText("Height");

        // use the grid layout for the dialog
        this.setLayout(new GridLayout(5,2));

        add(jLabel1);
        add(xPositionFormattedTextField);
        add(jLabel2);
        add(yPositionFormattedTextField);
        add(jLabel3);
        add(widthFormattedTextField);
        add(jLabel4);
        add(heightFormattedTextField);
        add(jButton1);
        pack();
    }

    /**
     * This function reads the input from the dialog into the barrier class and add it to the network topology and the visualier
     * @param evt action event
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt)
    {
        toAdd.setX((Integer) this.xPositionFormattedTextField.getValue());
        toAdd.setY((Integer) this.yPositionFormattedTextField.getValue());
        toAdd.setXBegin((Integer) this.xPositionFormattedTextField.getValue());
        toAdd.setYBegin((Integer) this.yPositionFormattedTextField.getValue());
        toAdd.setWidth((Integer) this.widthFormattedTextField.getValue());
        toAdd.setHeight((Integer) this.heightFormattedTextField.getValue());

        topology.addBarrier(toAdd);
        
        ArrayList<Integer> barrierInfo = new ArrayList<Integer>();
        
        barrierInfo.add(toAdd.getX());
        barrierInfo.add(toAdd.getY());
        barrierInfo.add(toAdd.getXBegin());
        barrierInfo.add(toAdd.getYBegin());
        barrierInfo.add(toAdd.getWidth());
        barrierInfo.add(toAdd.getHeight());
        try {
            this.cmdProcessor.setStaticBarrier(barrierInfo);
        } catch (InterruptedException ex) {
            Logger.getLogger(BarrierCreateDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        nwkVisualizer.getViewer().addPostRenderPaintable(toAdd);
        pFrame.repaint();

        dispose();
    }

    private javax.swing.JFormattedTextField xPositionFormattedTextField;
    private javax.swing.JFormattedTextField yPositionFormattedTextField;
    private javax.swing.JFormattedTextField widthFormattedTextField;
    private javax.swing.JFormattedTextField heightFormattedTextField;

    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
}

