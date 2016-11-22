package manager.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * This is a thread for updating the tables periodically
 * @author Song Han
 */
public class TimerUpdater implements Runnable
{
    JFrame main;
    int updateTime;
    TableController nodeTable;
    TableController edgeTable;
    TableController msgTable;
    TableController statTable;
    TableController graphTable;
    TableController devListTable;

    /**
     * Constructor function. Create an instance of the TimerUpdater with given parameters
     * @param m the parent frame
     * @param ms update time
     * @param node the node table controller
     * @param edge the edge table controller
     * @param msg the message table controller
     * @param stat the statistic information table controller
     * @param graph the graph information table controller
     * @param dev the device information table controller
     */
    public TimerUpdater(JFrame m, int ms, TableController node, TableController edge, TableController msg, TableController stat, TableController graph, TableController dev)
    {
        main = m;
        updateTime = ms;
        nodeTable = node;
        edgeTable = edge;
        msgTable = msg;
        statTable = stat;
        graphTable = graph;
        devListTable = dev;
    }

    /**
     * This function periodically update the table controllers every updateTime miniseconds.
     */
    public void run()
    {
        while(true)
        {
            try {
                Thread.sleep(updateTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(TimerUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }

            nodeTable.LoadTable();
            edgeTable.LoadTable();
            msgTable.LoadTable();
            statTable.LoadTable();
            graphTable.LoadTable();
            devListTable.LoadTable();
            main.repaint();
        }
    }
}
