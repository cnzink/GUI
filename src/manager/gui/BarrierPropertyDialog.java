package manager.gui;

import java.awt.GridLayout;
import manager.guiApp.GUICmdProcessor;

/**
 * The dialog form for showing the property of the barrier
 * @author Song Han
 */
public class BarrierPropertyDialog extends javax.swing.JDialog
{
    StaticBarrier barrier;
    GUICmdProcessor cmdProcessor;
    /**
     * Constructor function
     * @param parent the parent frame of the dialog
     * @param bar the reference to the barrier
     */
    public BarrierPropertyDialog(java.awt.Frame parent, StaticBarrier bar, GUICmdProcessor cmdProc)
    {       
        super(parent, true);
        initComponents();
        this.barrier = bar;
        this.cmdProcessor = cmdProc;
        setTitle("Barrier");
        this.wFormattedTextField.setValue(barrier.getWidth());
        this.hFormattedTextField.setValue(barrier.getHeight());
        this.shapeBox.setSelectedIndex(0);
    }

    /**
     * This function initializes the graphical components in the dialog
     */
    private void initComponents()
    {
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        // the width and height of the barrier
        wFormattedTextField = new javax.swing.JFormattedTextField();
        hFormattedTextField = new javax.swing.JFormattedTextField();
        shapeBox = new javax.swing.JComboBox();

        // the shape of the barrier
        shapeBox.addItem("Rectangle");
        shapeBox.addItem("Circle");
        shapeBox.addItem("Ellipse");
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Barrier Properties");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });

        jLabel1.setText("Width:");
        jLabel2.setText("Height:");
        jLabel3.setText("Shape:");

        // use the grid layout for the dialog
        this.setLayout(new GridLayout(4,2));

        add(jLabel3);
        add(shapeBox);
        add(jLabel1);
        add(wFormattedTextField);
        add(jLabel2);
        add(hFormattedTextField);
        add(jButton1);
        pack();
    }

    /**
     * This function reads in the new width and height of the dialog and update the barrier class
     * @param evt action event
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt)
    {
        barrier.setWidth((Integer) this.wFormattedTextField.getValue());
        barrier.setHeight((Integer)this.hFormattedTextField.getValue());
        
        // need to change the shape in the future
        dispose();
    }

    private javax.swing.JFormattedTextField wFormattedTextField;
    private javax.swing.JFormattedTextField hFormattedTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox shapeBox;
}