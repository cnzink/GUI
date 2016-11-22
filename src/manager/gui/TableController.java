package manager.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import manager.guiApp.NetworkEdge;
import manager.guiApp.NetworkNode;
import manager.guiApp.GUICmdProcessor;
import manager.guiApp.GUITopology;
import mesh.NodeUniqueID;
import mesh.util.Converter;

/**
 * This class extends the abstractTableModel to define the table model for the network statistic information
 * @author Song Han
 */
class StatsTableModel extends AbstractTableModel
{
    private GUITopology topology;
    private GUICmdProcessor cmdProc;
    // the number of rows and columns in the table
    private int rowNum;
    private int colNum;

    /**
     * Constructor function
     * @param topo the network topology
     */
    public StatsTableModel(GUITopology topo, GUICmdProcessor cmdProc)
    {
        this.cmdProc = cmdProc;
        topology = topo;
        // set the number of rows to be the number of vertices in the network
        rowNum = topo.GetGraph().getVertexCount();
        // set the number of columns to be default value 8
        colNum = 8;
    }

    public void setCmdProcessor(GUICmdProcessor cmdProc)
    {
        this.cmdProc = cmdProc;
    }
    
    public GUICmdProcessor getCmdProcessor()
    {
        return this.cmdProc;
    }
    
    /**
     * This function sets the network topology
     * @param topo network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        rowNum = topo.GetGraph().getVertexCount();
        colNum = 8;
    }
    
    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        rowNum = topology.GetGraph().getVertexCount();
        return rowNum;
    }

    /**
     * this function gets the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
        if(topology.GetGraph().getVertexCount() == 0)
            return "";

        NetworkNode [] nodeArray = new NetworkNode [topology.GetGraph().getVertexCount()];
        topology.GetGraph().getVertices().toArray(nodeArray);

        NetworkNode current = nodeArray [r];
        ArrayList<Integer> result = new ArrayList<Integer>();
       for(int i = 0;i<6;i++)
           result.add(0);
        /* try {
            result = this.getCmdProcessor().getTableInfo(current.getNodeNickname().GetNickName());
        } catch (InterruptedException ex) {
            Logger.getLogger(StatsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        switch (c)
        {
            case 0:
                return current.getID();
            case 1:
                return Converter.CharToHexString(current.getNodeNickname().GetNickName());
            case 2:
                return result.get(0);
            case 3:
                return result.get(1);
            case 4:
                return result.get(2);
            case 5:
                return result.get(3);
            case 6:
                return result.get(4);
            case 7:
                return result.get(5);
            
                
            default:
                return "";
        }
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "ID";
            case 1:
                return "name";
            case 2:
                return "Gen #";
            case 3:
                return "Ter #";
            case 4:
                return "MAC Fail";
            case 5:
                return "NWK Fail";
            case 6:
                return "CRC Fail";
            case 7:
                return "Nonce Fail";
            default:
                return "";
        }
    }
}

/**
 * This class extends the abstractTableModel to define the table model for the device list in the network
 * @author Song Han
 */
class DevListTableModel extends AbstractTableModel
{
    private GUITopology topology;
    // the number of rows and columns in the table
    private int rowNum;
    private int colNum;

    /**
     * Constructor function. Set the number of rows to be the larger one between the black list size and the write list size
     * @param topo the network topology
     */
    public DevListTableModel(GUITopology topo)
    {
        topology = topo;
        rowNum = (topo.GetWhiteList().size() > topo.GetBlackList().size()) ? topo.GetWhiteList().size() : topo.GetBlackList().size();
        colNum = 3;
    }

    /**
     * this function sets the network topology
     * @param topo the network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        rowNum = (topo.GetWhiteList().size() > topo.GetBlackList().size()) ? topo.GetWhiteList().size() : topo.GetBlackList().size();
        colNum = 3;
    }

    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        rowNum = (topology.GetWhiteList().size() > topology.GetBlackList().size()) ? topology.GetWhiteList().size() : topology.GetBlackList().size();
        return rowNum;
    }

    /**
     * this function returns the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
        ArrayList<NodeUniqueID> list = topology.GenActiveList();
        switch (c)
        {
            case 0:
                if(r < list.size())
                    return list.get(r).toString();
                else
                    return "";
            case 1:
                if(r < topology.GetWhiteList().size())
                    return topology.GetWhiteList().get(r).toString();
                else
                    return "";
            case 2:
                if(r < topology.GetBlackList().size())
                    return topology.GetBlackList().get(r).toString();
                else
                    return "";
            default:
                return "";
        }
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "Active Dev List";
            case 1:
                return "White Dev List";
            case 2:
                return "Black Dev List";
            default:
                return "";
        }
    }
}

/**
 * This class extends the abstractTableModel to define the table model for the network graphs
 * @author Song Han
 */
class GraphInfoTableModel extends AbstractTableModel
{
    private GUITopology topology;
    private int rowNum;
    private int colNum;

    /**
     * Constructor function
     * @param topo the network topology
     */
    public GraphInfoTableModel(GUITopology topo)
    {
        topology = topo;
        rowNum = topology.GetGraph().getVertexCount();
        colNum = 5;
    }

    /**
     * This function sets the network topology
     * @param topo network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        rowNum = topology.GetGraph().getVertexCount();
        colNum = 5;
    }

    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        rowNum = topology.GetGraph().getVertexCount();
        return rowNum;
    }

    /**
     * this function gets the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
        if(topology.GetGraph().getVertexCount() == 0)
            return "";

        NetworkNode [] nodeArray = new NetworkNode [topology.GetGraph().getVertexCount()];
        topology.GetGraph().getVertices().toArray(nodeArray);

        NetworkNode current = nodeArray [r];

        switch (c)
        {
            case 0:
                return current.getID();
            case 1:
                return Converter.CharToHexString(current.getNodeNickname().GetNickName());
            case 2:
                if((topology.GetUpLinkGraph() != null) && (topology.GetUpLinkGraph().getVertexCount() > 0))
                    return Converter.CharToHexString((char)topology.GetUpLinkGraphID());
                else
                    return "N/A";
            case 3:
                if((topology.GetBCastGraph() != null) && (topology.GetBCastGraph().getVertexCount() > 0))
                    return Converter.CharToHexString((char)topology.GetBCastGraphID());
                else
                    return "N/A";
            case 4:
                /* if((current.getFromGateWayGraph() != null) && (current.getFromGateWayGraph().getVertexCount() > 0))
                    return Converter.CharToHexString((char)current.getNetworkGraphFromGW().getGraphID());
                 * else */
                    return "N/A";
            default:
                return "";
        }
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "ID";
            case 1:
                return "Nick Name";
            case 2:
                return "UpLink Graph";
            case 3:
                return "BCast Graph";
            case 4:
                return "DownLink Graph";
            default:
                return "";
        }
    }
}

/**
 * This class extends the abstractTableModel to define the table model for the vertices
 * @author Song Han
 */
class VertexTableModel extends AbstractTableModel
{
    private GUITopology topology;
    private int rowNum;
    private int colNum;

    /**
     * Constructor function
     * @param topo the network topology
     */
    public VertexTableModel(GUITopology topo)
    {
        topology = topo;
        rowNum = topo.GetGraph().getVertexCount();
        colNum = 5;
    }

    /**
     * This function sets the network topology
     * @param topo network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        rowNum = topo.GetGraph().getVertexCount();
        colNum = 5;
    }

    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        rowNum = topology.GetGraph().getVertexCount();
        return rowNum;
    }

    /**
     * this function gets the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
        if(topology.GetGraph().getVertexCount() == 0)
            return "";

        NetworkNode [] nodeArray = new NetworkNode [topology.GetGraph().getVertexCount()];
        topology.GetGraph().getVertices().toArray(nodeArray);

        NetworkNode current = nodeArray [r];

        switch (c)
        {
            case 0:
                return current.getID();
            case 1:
                return Converter.CharToHexString(current.getNodeNickname().GetNickName());
            case 2:
                return current.getNodeUniqueID().toString();
            case 3:
                return current.getType();
            case 4:
                return current.getScanPeriod().toString();
            default:
                return "";
        }
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "ID";
            case 1:
                return "Nick Name";
            case 2:
                return "Unique ID";
            case 3:
                return "Type";
            case 4:
                return "Scan Period";
            default:
                return "";
        }
    }
}

/**
 * This class extends the abstractTableModel to define the table model for the edges in the network
 * @author Song Han
 */
class EdgeTableModel extends AbstractTableModel
{
    private GUITopology topology;
    private int rowNum;
    private int colNum;

    /**
     * Constructor function
     * @param topo the network topology
     */
    public EdgeTableModel(GUITopology topo)
    {
        topology = topo;
        rowNum = topo.GetGraph().getEdgeCount();
        colNum = 6;
    }

    /**
     * This function sets the network topology
     * @param topo network topology
     */
    public void setTopology(GUITopology topo)
    {
        topology = topo;
        rowNum = topo.GetGraph().getEdgeCount();
        colNum = 6;
    }

    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        rowNum = topology.GetGraph().getEdgeCount();
        return rowNum;
    }

    /**
     * this function gets the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
         if(topology.GetGraph().getEdgeCount() == 0)
            return "";

        NetworkEdge [] edgeArray = new NetworkEdge [topology.GetGraph().getEdgeCount()];
        topology.GetGraph().getEdges().toArray(edgeArray);

        NetworkEdge current = edgeArray [r];

        switch (c)
        {
            case 0:
                return current.getName();
            case 1:
                return topology.GetGraph().getSource(current).toString();
            case 2:
                return topology.GetGraph().getDest(current).toString();
            case 3:
                return current.getWeight();
            case 4:
                return current.getCapacity();
            case 5:
                return current.getSignalStrength();
            default:
                return "";
        }
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "NAME";
            case 1:
                return "SRC";
            case 2:
                return "DEST";
            case 3:
                return "WEIGHT";
            case 4:
                return "CAPACITY";
            case 5:
                return "SIGNAL";
            default:
                return "";
        }
    }
}

/**
 * This class extends the abstractTableModel to define the table model for the messages received in the network manager
 * @author Song Han
 */
class MsgTableModel extends AbstractTableModel
{
    private GUICmdProcessor cmdProcessor;
    private int rowNum;
    private int colNum;

    /**
     * Constructor function
     * @param p the command processor
     */
    public MsgTableModel(GUICmdProcessor cmdProc)
    {
        this.cmdProcessor = cmdProc;        
        colNum = 10;
        
        if(cmdProc!=null)
            rowNum = 0;
        else 
            rowNum = 0;
    }

    public void SetCmdProcessor(GUICmdProcessor cmdProc)
    {
        cmdProcessor = cmdProc;
    }

    /**
     * this function gets the number of rows in the table
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        if(cmdProcessor!=null)
        rowNum = 0;  // here need revise later
        else
        rowNum = 0;
        
        return rowNum;
    }

    /**
     * this function gets the number of columns in the table
     * @return the number of columns in the table
     */
    public int getColumnCount()
    {
        return colNum;
    }

    /**
     * this function gets the object at a given row and a given column
     * @param r the given row
     * @param c the given column
     * @return the object in the table at the given row and the given column
     */
    public Object getValueAt(int r, int c)
    {
        return "";
    }

    /**
     * This function gets the column name with the given number of column
     * @return the column name in the string format
     */
    @Override
    public String getColumnName(int c)
    {
        switch (c)
        {
            case 0:
                return "S/ R";
            case 1:
                return "IP Header";
            case 2:
                return "Ctr Byte";
            case 3:
                return "Graph ID";
            case 4:
                return "Dest";
            case 5:
                return "Src";
            case 6:
                return "Proxy";
            case 7:
                return "Src Routing";
            case 8:
                return "Security";
            case 9:
                return "Payload";
            default:
                return "";
        }
    }
}

/**
 * This class defines the table controller. It contains all the table models to be used in the network manager
 * @author Song Han
 * 
 * updated by Hang Yu
 * 2012.6.20
 */
public class TableController
{
    private GUITopology topology;
    
    // private CmdProcessor processor;

    private GUICmdProcessor cmdProcessor;
    
    private JTable table;
    VertexTableModel vertexModel;
    EdgeTableModel edgeModel;
    MsgTableModel msgModel;
    StatsTableModel statsModel;
    GraphInfoTableModel graphModel;
    DevListTableModel devListModel;

    // The type of the table
    TableType type;

    /**
     * Constructor function
     * @param topo the network topology
     * @param proc the command processor
     * @param t the type of the table
     */
    public TableController(GUITopology topo, GUICmdProcessor cmdProc, TableType t)
    {
        super();
        topology = topo;
        type = t;
        this.cmdProcessor = cmdProc;
        // initialize the table according to different types of the tables
        if(type == TableType.Vertex)
        {
            vertexModel = new VertexTableModel(topology);
            table = new JTable(vertexModel);
            table.getColumnModel().getColumn(0).setPreferredWidth(15); // id
            table.getColumnModel().getColumn(1).setPreferredWidth(40); // nickname
            table.getColumnModel().getColumn(3).setPreferredWidth(40); // type
            table.setAutoCreateRowSorter(true);
        }
        else if(type == TableType.DeviceList)
        {
            devListModel = new DevListTableModel(topology);
            table = new JTable(devListModel);
            table.setAutoCreateRowSorter(true);
        }
        else if(type == TableType.Stat)
        {
            statsModel = new StatsTableModel(topology, cmdProcessor);
            table = new JTable(statsModel);
            table.getColumnModel().getColumn(0).setPreferredWidth(25); // id
            table.setAutoCreateRowSorter(true);
        }
        else if(type == TableType.Graph)
        {
            graphModel = new GraphInfoTableModel(topology);
            table = new JTable(graphModel);
            table.setAutoCreateRowSorter(true);
        }
        else if(type == TableType.Edge)
        {
            edgeModel = new EdgeTableModel(topology);
            table = new JTable(edgeModel);
            table.setAutoCreateRowSorter(true);
        }
        else if(type == TableType.Msg)
        {
            msgModel = new MsgTableModel(cmdProcessor);
            table = new JTable(msgModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRowHeight(70);
            table.getColumnModel().getColumn(0).setPreferredWidth(35); // s/r
            table.getColumnModel().getColumn(1).setPreferredWidth(140); // ip header
            table.getColumnModel().getColumn(2).setPreferredWidth(50); // ctrl byte
            table.getColumnModel().getColumn(3).setPreferredWidth(70); // graph id
            table.getColumnModel().getColumn(4).setPreferredWidth(120); // dest
            table.getColumnModel().getColumn(5).setPreferredWidth(120); // src
            table.getColumnModel().getColumn(6).setPreferredWidth(50); // proxy
            table.getColumnModel().getColumn(7).setPreferredWidth(150); // src routing
            table.getColumnModel().getColumn(8).setPreferredWidth(50); // secuirty
            table.getColumnModel().getColumn(9).setPreferredWidth(1000); // payload
            table.getColumnModel().getColumn(9).setCellRenderer(new TextAreaRenderer());
        }
    }

    /**
     * this function gets the table
     * @return the table
     */
    public JTable getTable()
    {
        return table;
    }

    /**
     * this function sets the network topology and the
     * @param topo the network topology
     * @param jta the text area to show the information. Not used in the function.
     */
    public void setTopology(GUITopology topo, JTextArea jta)
    {
        topology = topo;

        if(type == TableType.Vertex)
            vertexModel.setTopology(topo);
        else if(type == TableType.Edge)
            edgeModel.setTopology(topo);
        else if(type == TableType.DeviceList)
            devListModel.setTopology(topo);
        else if(type == TableType.Graph)
            graphModel.setTopology(topo);
        else if(type == TableType.Stat)
            statsModel.setTopology(topo);

        //table.revalidate(); //previously commented out TOM12, is this even needed?
        //table.updateUI(); TOM12
        table.repaint();
    }

    
    /**
     * this function sets the command processor
     * @param p the command processor
     */
    public void setCmdProcessor(GUICmdProcessor p)
    {
        this.cmdProcessor = p;

        // only need to set the command processor is the table type is message table
        if(type == TableType.Msg)
            msgModel.SetCmdProcessor(p);

        if(type == TableType.Stat)
            statsModel.setCmdProcessor(p);
        //table.revalidate(); //TOM12 is this even needed?
        //table.updateUI(); TOM12
        table.repaint();
    }

    /**
     * This function reload the table
     */
    public void LoadTable()
    {
        //table.revalidate(); //TOM12 this need to have updated table drawing code
        //table.updateUI(); TOM12
        ((AbstractTableModel) table.getModel()).fireTableDataChanged(); //indicate that there may be new data, will trigger repaint. assumes table structure does not change.
    }
 }

/**
 * this class extends the JTextArea and implements the getTableCellRendererComponent function. It is used to use smaller size font for the payload field in the message table.
 * @author Song Han
 */
class TextAreaRenderer extends JTextArea implements TableCellRenderer {

    /**
     * Constructor function
     */
    public TextAreaRenderer() {
        setLineWrap(false);
        setWrapStyleWord(true);
    }

    /**
     * This method sets the font to be 2 size smaller than the original one used in the cell.
     * @return the componnet used for drawing the cell.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        this.setFont(new Font("small", table.getFont().getStyle(), table.getFont().getSize()-2));
        setText((String)value);
        return this;
    }
}



