package manager.gui;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import mesh.NodeUniqueID;

/**
 * This class records the locations of the devices in the network
 * @author Song Han
 * @update Hang Yu
 */
public class DevLocation {

    // the hash map of the device locations. Key is the netowrk node unique ID
    HashMap<NodeUniqueID, Point2D> devLocation;

    /**
     * This function adds a device location into the hashmap
     * @param id the unique ID of the device
     * @param p the location of the device in the visualization viewer
     */
    public void AddDevLocation(NodeUniqueID id, Point2D p)
    {
        devLocation.put(id, p);
    }

    /**
     * this function gets the device location
     * @param id the device unique ID
     * @return the location of the device in the visualization viewer
     */
    public Point2D GetDevLocation(NodeUniqueID id)
    {
        Set set = this.devLocation.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext())
        {
            Map.Entry entry = (Map.Entry)i.next();
            NodeUniqueID uID = (NodeUniqueID) entry.getKey();

            if(uID.equals(id))
                return (Point2D) entry.getValue();
        }
        return null;
    }

    /**
     * Constructor function. It pre-installs some devices' locations
     */
    public DevLocation()
    {
        devLocation = new HashMap<NodeUniqueID, Point2D>();

        // location of the access point
        NodeUniqueID id = new NodeUniqueID("001b1ee18b020321");
        Point2D point = new Point2D.Double(236.31825128491892, 149.03703548101737);
        devLocation.put(id, point);

        // location of the Gateway
        id = new NodeUniqueID("001b1ef981000002");
        point = new Point2D.Double(342.52156397423715, 59.65181990432029);
        devLocation.put(id, point);

        // location of the large demo
        id = new NodeUniqueID("001b1ee18b020330");
        point = new Point2D.Double(450.20778471561476, 155.17029018041615);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020331");
        point = new Point2D.Double(327.8909209704349, 231.0729911194578);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020332");
        point = new Point2D.Double(283.41395684473304, 369.92559955236726);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020333");
        point = new Point2D.Double(128.77340682341298, 418.92121383702084);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020334");
        point = new Point2D.Double(591.1264868547299, 247.11940984618946);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020335");
        point = new Point2D.Double(460.31316797185127, 369.89515770171926);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020336");
        point = new Point2D.Double(103.31327824940513, 247.9034776101518);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020337");
        point = new Point2D.Double(573.3108078252199, 407.5707834791731);
        devLocation.put(id, point);

        // location of the small demo
        id = new NodeUniqueID("001b1ee18b020322");
        point = new Point2D.Double(450.20778471561476, 155.17029018041615);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020325");
        point = new Point2D.Double(327.8909209704349, 231.0729911194578);
        devLocation.put(id, point);

        id = new NodeUniqueID("001b1ee18b020327");
        point = new Point2D.Double(283.41395684473304, 369.92559955236726);
        devLocation.put(id, point);
    }
}
