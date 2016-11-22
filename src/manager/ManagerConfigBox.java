package manager;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import manager.guiApp.GUICmdProcessor;
import mesh.util.Converter;
import org.jdesktop.application.Action;
import org.xml.sax.SAXException;

/**
 * This class defines the Network Manager configuration Box
 * The config box allows users to edit NetworkID, ChannelMap, and devices.
 * 
 * Created by the "ConfigListener" class in MeshSimulator.java
 * @author Song Han
 * 
 * 
 * edited by Hang Yu
 * 2012.6.21
 */
public class ManagerConfigBox extends javax.swing.JDialog {

    /**
     * Constructor function. It creates an instance of the ManagerConfigBox
     * @param parent the parent frame
     * @throws ParserConfigurationException the Parser exception
     * @throws SAXException sax exception
     * @throws IOException I/O exception
     */
    private GUICmdProcessor cmdProcessor;
    
    public ManagerConfigBox(java.awt.Frame parent)
    {
            super(parent);
            initComponents();
                getRootPane().setDefaultButton(btnClose);
        try {
            // load the network manager configuration information from ManagerConfig.java
            LoadManagerConfig();
        } catch (InterruptedException ex) {
            Logger.getLogger(ManagerConfigBox.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

    public void setCmdProcessor(GUICmdProcessor cmdProc)
    {
           this.cmdProcessor = cmdProc;
    }
    
    /**
     * this function load the network manager configuration information to the GUI
     */
    private void LoadManagerConfig() throws InterruptedException
    {
        // Load the network ID and Channel Map text field
        txtNetworkID.setText(ManagerConfig.getNetworkID().toString());
        
        String channelMap1 = ManagerConfig.ToXmlFormatHexString(ManagerConfig.GetChannelMap()); // get full channel map string, e.g. "0x1236"
        String channelMap2 = ManagerConfig.FormatHexString(channelMap1); // remove "0x" from "0x1236"
        txtChannelMap.setText(channelMap2);

        // Load the join key
        txtJoinKey.setText(Converter.ByteArrayToHexString(ManagerConfig.getJoinKey()));
        
        // Load max hop limit
        String hopLimitStr = Integer.toString(ManagerConfig.getMaxHopLimit());
        txtMaxHopLimit.setText(hopLimitStr);
        
        
        /* unused (due to removal of device whitelist)
        // load the device information to the GUI
        ArrayList<AdditionalDevice> deviceLst = ManagerConfig.GetDeviceList();
        Vector<Vector> Rows = new Vector<Vector>(deviceLst.size());
        for (int i = 0; i < deviceLst.size(); i++)
        {
            Vector<String> rowData = new Vector<String>(2);
            AdditionalDevice device = deviceLst.get(i);
            rowData.add(device.GetDeviceType().toString());
            rowData.add(ManagerConfig.ToXmlFormatHexString(device.GetDeviceID().GetUniqueID()));
            rowData.add(ManagerConfig.ToXmlFormatHexString(device.GetJoinKey().getKey()));
            rowData.add(ManagerConfig.ToXmlFormatHexString(device.GetDeviceTag().GetDeviceTag()));
            Rows.add(rowData);
        }
        Vector<String> Cols = new Vector<String>(2);
        Cols.add("Device Type");
        Cols.add("Deivce ID");
        Cols.add("Join Key");
        Cols.add("Device Tag");
        tableModel = new DefaultTableModel(Rows, Cols)
        {

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        }; 
         */
    }

    /**
     * this function saves the network manager configuration information from the updated GUI.
     * We only send networkID if user changed it (same for channel map and devices)
     * 
     * the changes are sent to the ManagerCore, the ManagerCore saves info into NMConfig.xml
     */
    private void SaveManagerConfig()
    {
        boolean restartRequired = false;
        boolean needSave = false; //determine if the ManagerCore needs to SaveToXML
        
        //Check if NetworkID changed; if yes, save NetworkID to ManagerCore
        String newID = txtNetworkID.getText();
        String prevID = ManagerConfig.getNetworkID().toString();
        if(newID.equals(prevID) == false)
        {
            ManagerConfig.setNetworkID(txtNetworkID.getText());
            cmdProcessor.setManagerCoreNetworkID(txtNetworkID.getText()); //sends new NetworkID to ManagerCore
            restartRequired = true;
            needSave = true;
        }
        
        // Check if Channel Map changed; if yes, save Channel Map to Core
        String newChanMap = txtChannelMap.getText();
        String prevChanMap = ManagerConfig.ToXmlFormatHexString(ManagerConfig.GetChannelMap());
        prevChanMap = ManagerConfig.FormatHexString(prevChanMap); // temporary fix b/c channel map is still a char instead of byte[]. remove "0x" from channel map.
        if(newChanMap.equals(prevChanMap) == false)
        {
            ManagerConfig.SetChannelMap(txtChannelMap.getText());
            cmdProcessor.setManagerCoreChannelMap(txtChannelMap.getText()); //sends new chan map to ManagerCore
            restartRequired = true;
            needSave = true;
        }
        
        // Check if Join Key changed; if yes, save Join Key to Core
        String newJoinKeyStr = ManagerConfig.FormatHexString(txtJoinKey.getText()); //remove possible "0x" from join key
        byte[] newJoinKey = Converter.HexStringToByte(newJoinKeyStr); // convert join key string into byte array
        byte[] prevJoinKey = ManagerConfig.getJoinKey();
        if (Arrays.equals(newJoinKey, prevJoinKey) == false)
        {
            ManagerConfig.setJoinKey(newJoinKey);
            cmdProcessor.setManagerCoreJoinKey(newJoinKey);
            restartRequired = true;
            needSave = true;
        }
        
        // Check if max hop limit changed; if yes, save max hop limit to Core
        try
        {
            int newMaxHopLimit = Integer.parseInt(txtMaxHopLimit.getText());
            if (newMaxHopLimit != ManagerConfig.getMaxHopLimit())
            {
                ManagerConfig.setMaxHopLimit(newMaxHopLimit);
                cmdProcessor.setManagerCoreMaxHopLimit(newMaxHopLimit);
                restartRequired = true;
                needSave = true;
            }
        
        }
        catch (Exception e)
        {
            // don't save max hop limit if it is invalid
        }

        //show a notification to User
        if(restartRequired == true)
            JOptionPane.showMessageDialog(null, "You must restart the Network Manager for changes to take effect", "Warning", JOptionPane.WARNING_MESSAGE);
    
        //determine if we need to tell ManagerCore to Save to XML
        if(needSave== true)
            cmdProcessor.saveToXML(); //this sends cmd64014, tells Core to save to xml.
    }



    /**
     * this function shows a string format message using the showMessageDialog
     * @param the message in the string format
     */
    private void ShowMessage(String msg)
    {
        JOptionPane.showMessageDialog(this, msg);
    }

    /**
    * This function validate the format of the network ID and the Channel map
    * @return true if the format of the network ID and the Channel map are valid
    */
    private boolean ValidateFormat()
    {
       if (!ManagerConfig.ValidateHexString(txtNetworkID.getText(), 1, 4)) {
            ShowMessage("Improper format for Network ID.");
            return false;
        }
       
        if (!ManagerConfig.ValidateHexString(txtChannelMap.getText(), 1, 4)) {
            ShowMessage("Improper format for Channel Map.");
            return false;
        }
        
        if (ManagerConfig.ValidateHexString(txtJoinKey.getText(), 1, 16)) {
            ShowMessage("Improper format for Join Key.");
            return false;
        }
        
        return true;
    }

    /**
     * this function closes the network manager configuration box
     */
    @Action public void closeConfigBox() {
        ManagerConfig.getDeviceUpdates().clear(); //clear out any user changes that were not saved.
        ManagerConfig.getIndexOfDeviceUpdates().clear(); //clear out any user changes that were not saved.
        ManagerConfig.getActionOfDeviceUpdates().clear(); //clear out any user changes that were not saved.
        dispose();
    }

    /**
     * this function save the configuration information to the Core
     * This is called when user clicks "save" button under N
     * 
     */
    @Action
    public void saveConfigBox()
    {
        if (ValidateFormat())
        {
            SaveManagerConfig();
            dispose();
        }
    }


    /**
     * this function convert a string in the hex format to upper case.
     * @param hex the input string in the hex format
     * @return  the output string in the hex format with upper case
     */
    private String HexToUpperCase(String hex)
    {
        hex = hex.toUpperCase();
        StringBuilder sb = new StringBuilder(hex.length());
        for (int i=0; i<hex.length(); i++)
        {
            if (hex.charAt(i) == 'X')
                sb.append('x');
            else
                sb.append(hex.charAt(i));
        }
        return sb.toString();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        txtNetworkID = new javax.swing.JTextField();
        javax.swing.JLabel lbChannelMap = new javax.swing.JLabel();
        txtChannelMap = new javax.swing.JTextField();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lbNetworkID = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtJoinKey = new javax.swing.JTextField();
        javax.swing.JLabel lblMaxHopLimit = new javax.swing.JLabel();
        txtMaxHopLimit = new javax.swing.JTextField();
        javax.swing.JLabel lbChannelMap1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(manager.ManagerApp.class).getContext().getResourceMap(ManagerConfigBox.class);
        setTitle(resourceMap.getString("aboutBox.title")); // NOI18N
        setModal(true);
        setName("aboutBox"); // NOI18N
        setResizable(false);

        txtNetworkID.setFont(resourceMap.getFont("txtNetworkID.font")); // NOI18N
        txtNetworkID.setText(resourceMap.getString("txtNetworkID.text")); // NOI18N
        txtNetworkID.setName("txtNetworkID"); // NOI18N

        lbChannelMap.setFont(lbChannelMap.getFont().deriveFont(lbChannelMap.getFont().getStyle() | java.awt.Font.BOLD, lbChannelMap.getFont().getSize()+4));
        lbChannelMap.setText(resourceMap.getString("lbChannelMap.text")); // NOI18N
        lbChannelMap.setName("lbChannelMap"); // NOI18N

        txtChannelMap.setFont(resourceMap.getFont("txtChannelMap.font")); // NOI18N
        txtChannelMap.setName("txtChannelMap"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(manager.ManagerApp.class).getContext().getActionMap(ManagerConfigBox.class, this);
        btnClose.setAction(actionMap.get("closeConfigBox")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N

        btnSave.setAction(actionMap.get("saveConfigBox")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N

        lbNetworkID.setFont(lbNetworkID.getFont().deriveFont(lbNetworkID.getFont().getStyle() | java.awt.Font.BOLD, lbNetworkID.getFont().getSize()+4));
        lbNetworkID.setText(resourceMap.getString("lbNetworkID.text")); // NOI18N
        lbNetworkID.setName("lbNetworkID"); // NOI18N

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+4));
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtJoinKey.setFont(resourceMap.getFont("txtJoinKey.font")); // NOI18N
        txtJoinKey.setText(resourceMap.getString("txtJoinKey.text")); // NOI18N
        txtJoinKey.setName("txtJoinKey"); // NOI18N

        lblMaxHopLimit.setFont(lblMaxHopLimit.getFont().deriveFont(lblMaxHopLimit.getFont().getStyle() | java.awt.Font.BOLD, lblMaxHopLimit.getFont().getSize()+4));
        lblMaxHopLimit.setName("lblMaxHopLimit"); // NOI18N

        txtMaxHopLimit.setFont(resourceMap.getFont("txtMaxHopLimit.font")); // NOI18N
        txtMaxHopLimit.setName("txtMaxHopLimit"); // NOI18N

        lbChannelMap1.setFont(lbChannelMap1.getFont().deriveFont(lbChannelMap1.getFont().getStyle() | java.awt.Font.BOLD, lbChannelMap1.getFont().getSize()+4));
        lbChannelMap1.setText(resourceMap.getString("lbChannelMap1.text")); // NOI18N
        lbChannelMap1.setName("lbChannelMap1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMaxHopLimit)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbNetworkID)
                    .addComponent(jLabel1)
                    .addComponent(lbChannelMap)
                    .addComponent(lbChannelMap1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNetworkID, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSave)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnClose))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtChannelMap, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtJoinKey, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaxHopLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 172, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClose)
                            .addComponent(btnSave))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNetworkID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbNetworkID))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtJoinKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbChannelMap)
                            .addComponent(txtChannelMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(lblMaxHopLimit))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbChannelMap1)
                                    .addComponent(txtMaxHopLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // this is called when user double clicks on a device in the table to edit it    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbNetworkID;
    private javax.swing.JTextField txtChannelMap;
    private javax.swing.JTextField txtJoinKey;
    private javax.swing.JTextField txtMaxHopLimit;
    private javax.swing.JTextField txtNetworkID;
    // End of variables declaration//GEN-END:variables

    private DefaultTableModel tableModel;
}
