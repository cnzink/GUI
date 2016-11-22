/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ConnectionSettingBox.java
 *
 * Created on Jan 24, 2013, 12:36:43 PM
 */
package manager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *
 * @author Hang Yu
 */
public class ConnectionSettingBox extends javax.swing.JDialog {

    private String IPAddress = "127.0.0.1";
    private String Port = "8900";
    //private static boolean isConnected = false;
    private MeshSimulator localMS;
    
    public MeshSimulator setMeshSimulator(MeshSimulator ms)
    {
        this.localMS = ms;
        return ms;
    }
    
    // do not save the input IP and port by default, save when the use click "connect"
    private boolean saved = true;
    
    /** Creates new form ConnectionSettingBox */
    public ConnectionSettingBox(java.awt.Frame parent, boolean moral) {
        super(parent);
        initComponents();
        
        // Center the Dialog over the GUI window
        pack();
        setLocationRelativeTo(parent);
        
        // Other items to initialize
        this.saveButton.setEnabled(true);
        
      //  jLabel1.setText("Network Manager IP Address Configuration");
      //  IPLabel.setText("IP Address");
      //  portLabel.setText("Port");
        
        Document doc=load("GUIConnection.xml", false);
        if (doc != null)
        {
            readXMLConfig(doc);
            IPTextField.setText(IPAddress);
            portTextField.setText(Port);
        }
        else
        {
            createConfigFile("GUIConnection.xml");
        }
        
        //add action listner to detect when user presses "Enter", when cursor is on the "IP address" jTextField
        IPTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToNM();
            }
        });
    }
        
    /**
     * Read the IP address and port from xml file
     * @param doc the xml document
     * @return true if xml was read correctly, otherwise false
     */
    private boolean readXMLConfig(Document doc)
    {
        if (doc == null)
            return false;
        Node root = doc.getDocumentElement();
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
                            IPAddress = nodes.item(i).getChildNodes().item(0).getNodeValue();
                        }
                        else
                            if (nodes.item(i).getNodeName().compareTo("Port") == 0)
                            {
                                Port = nodes.item(i).getChildNodes().item(0).getNodeValue();
                            }
                    }
                }
            }
        }
        return true;
    }

    public String getIpAddress()
    {
        return IPAddress;
    }

    public void showIpAddress(String ip)
    {
        IPAddress = ip;
        IPTextField.setText(ip);
    }

    public int getPort()
    {
        return Integer.valueOf(Port);
    }

    public void showPort(String port)
    {
        Port = port;
        portTextField.setText(port);
    }

    public boolean isSaved()
    {
        return this.saved;
    }

    /**
     *  When the User clicks the "connect" button on the window from File, Connect,
     * run this method. Upon successful connection, the connect option is disabled,
     * and the Disconnect option (File, Disconnect) is enabled.
     */
    private void connectToNM()
    {
        if (checkIPFormat(IPTextField.getText()) && checkPortValue(portTextField.getText()))
        {
            IPAddress = IPTextField.getText();
            Port = portTextField.getText();
            createConfigFile("GUIConnection.xml");
            this.saved = true;
            int port = Integer.parseInt(Port);

            boolean success;
            success = this.localMS.getTcpManager().connect(IPAddress, port);
            if (!success) //if connection error: 
            {
                JOptionPane.showMessageDialog(null, "Connection Failed!"); //show a dialog If the connection fails
                return;
            }

            //at this point, we have a successful connection
            this.localMS.setStateConnected(); //change GUI state (this disables the connect option)
            this.dispose(); //close this JDialog 
            //JOptionPane.showMessageDialog(null, "Connected!"); // don't really need this dialog? 
        }
        else
        {
            message.setText("IP Address or Port Format Error!");
        }
    }

    private boolean checkIPFormat(String ip)
    {
        boolean ret = true;
        String str = null;
        String[] add = null;
        str = ip.replace('.', ':');
        add = str.split(":");
        if (add.length != 4)
            ret = false;
        else
        {
            try
            {
                for (int i = 0; i < add.length; i++)
                    if (Integer.valueOf(add[i]) > 255 || Integer.valueOf(add[i]) < 0)
                    {
                        ret = false;
                        break;
                    }
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                ret = false;
            }
        }
        return ret;
    }
    
    private boolean checkPortValue(String port)
    {
        boolean ret = true;
        int p;
        try
        {
            p = Integer.valueOf(port);
            if (p < 0 || p > 65535)
                ret = false;
        }
        catch (NumberFormatException e)
        {
            ret = false;
        }
        return ret;
    }
    
    private void centerDialogOverParent(java.awt.Frame parent)
    {
        Point parentLocation = parent.getLocationOnScreen();
        Point myLocation = new Point();
        
        myLocation.x = parentLocation.x + 10;
        myLocation.y = parentLocation.y + 10;
        
        this.setLocation(myLocation);
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

        portLabel = new javax.swing.JLabel();
        IPTextField = new javax.swing.JTextField();
        portTextField = new javax.swing.JTextField();
        IPLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        message = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(manager.ManagerApp.class).getContext().getResourceMap(ConnectionSettingBox.class);
        portLabel.setText(resourceMap.getString("portLabel.text")); // NOI18N
        portLabel.setName("portLabel"); // NOI18N

        IPTextField.setText(resourceMap.getString("IPTextField.text")); // NOI18N
        IPTextField.setName("IPTextField"); // NOI18N

        portTextField.setText(resourceMap.getString("portTextField.text")); // NOI18N
        portTextField.setName("portTextField"); // NOI18N

        IPLabel.setText(resourceMap.getString("IPLabel.text")); // NOI18N
        IPLabel.setToolTipText(resourceMap.getString("IPLabel.toolTipText")); // NOI18N
        IPLabel.setName("IPLabel"); // NOI18N

        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                saveButtonMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                saveButtonMouseExited(evt);
            }
        });

        message.setText(resourceMap.getString("message.text")); // NOI18N
        message.setName("message"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setToolTipText(resourceMap.getString("cancelButton.toolTipText")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                cancelButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(IPLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(portLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portTextField)
                            .addComponent(IPTextField))
                        .addGap(89, 89, 89))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(saveButton)
                                .addGap(18, 18, 18)
                                .addComponent(cancelButton))
                            .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(IPLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(IPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(portLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(saveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(message, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void saveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseClicked
    // This action occurs when the "Connect" button is pressed.
    connectToNM();
}//GEN-LAST:event_saveButtonMouseClicked

private void saveButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseExited
// TODO add your handling code here:
    message.setText("");
}//GEN-LAST:event_saveButtonMouseExited

private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
    // TODO add your handling code here:
    this.saved = false;
    this.setVisible(false);
}//GEN-LAST:event_cancelButtonMouseClicked

private void createConfigFile(String filename)
{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try
        {
            db = dbf.newDocumentBuilder();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Document doc = db.newDocument();
        Element root = doc.createElement("guiconnection");
        doc.appendChild(root);
        Element IP = doc.createElement("IP");
        org.w3c.dom.Text ip = doc.createTextNode(IPAddress);
        IP.appendChild(ip);
        root.appendChild(IP);
        Element port = doc.createElement("Port");
        org.w3c.dom.Text p = doc.createTextNode(Port);
        port.appendChild(p);
        root.appendChild(port);
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            // ((XmlDocument) doc).write(osw, "GB2312");
            callWriteXmlFile(doc, osw, "UTF-8");
            osw.close();
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void callWriteXmlFile(Document doc, Writer w, String encoding)
    {
        try
        {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);
            // Prepare the output file
            Result result = new StreamResult(w);
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            xformer.transform(source, result);
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
    }
    
    private Document load(String filename, boolean warning)
    {
        Document document = null;
        File f;
        f = new File(filename);
        if (f.length() == 0)
        {
            if (!warning)
                return null;
            JOptionPane.showMessageDialog(null, "Config File not Exist!\n\n Use default.");
            return null;
        }
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
        return document;
    }
    
    /*
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConnectionSettingBox dialog = new ConnectionSettingBox(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    */
    
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel IPLabel;
    private javax.swing.JTextField IPTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel message;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField portTextField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
