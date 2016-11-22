package manager;

import mesh.Key;
import mesh.NodeUniqueID;
import mesh.nmDeviceType;
import mesh.DeviceTag;

/**
 * this class defines the wirelesshart network access entry
 * @author Song Han
 */
public class NetAccessEntry {

    // the join key of the device
    Key joinKey;
    // the unique ID of the device
    NodeUniqueID uid;
    // the type of the device
    nmDeviceType type;
    // the device tag of the device
    DeviceTag tag;

    /**
     * Constructor function. It creates an instance of the network access entry with the given parameters
     * @param id the unique ID of the device
     * @param t the type of the device
     * @param k the join key of the device
     * @param tg the device tag of the device
     */
    public NetAccessEntry(NodeUniqueID id, nmDeviceType t, Key k, DeviceTag tg)
    {
        joinKey = k;
        uid = id;
        type = t;
        tag = tg;
    }

    /**
     * this function gets the join key of the device
     * @return the join key of the device
     */
    public Key GetJoinKey()
    {
        return this.joinKey;
    }

    /**
     * this function gets the unique ID of the device
     * @return the unique ID of the device
     */
    public NodeUniqueID GetUID()
    {
        return this.uid;
    }

    /**
     * this function gets the device type
     * @return the device type
     */
    public nmDeviceType GetDevType()
    {
        return this.type;
    }

    /**
     * this function gets the device tag
     * @return the device tag
     */
    public DeviceTag GetDeviceTag()
    {
        return this.tag;
    }
}