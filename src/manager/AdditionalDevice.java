package manager;

import mesh.Key;
import mesh.NodeUniqueID;
import mesh.DeviceTag;
import mesh.util.Converter;
import mesh.nmDeviceType;

/**
 * This class defines the addtionalDevice to be used by the AdditionalDeviceBox class. For the details, please refer to the AdditionalDeviceBox class.
 * @author Song Han
 */

public class AdditionalDevice {
    private NodeUniqueID DeviceID;
    private Key JoinKey;
    private DeviceTag DeviceTag;
    private nmDeviceType DeviceType;

    /**
     * Constructor function. It creates an instance of the AdditionalDevice with the given parameters
     * @param deviceType the device type in the string format
     * @param id the device ID in the string format
     * @param key the join key in the string format
     * @param tag the device tag in the string format
     */
    public AdditionalDevice(String deviceType, String id, String key, String tag)
    {
        DeviceType = nmDeviceType.valueOf(deviceType);
        DeviceID = new NodeUniqueID(ManagerConfig.FormatHexString(id));
        JoinKey = new Key(Converter.HexStringToByte(ManagerConfig.FormatHexString(key)));
        DeviceTag = new DeviceTag(ManagerConfig.FormatHexString(tag));
    }

    /**
     * this function gets the device ID
     * @return the device ID
     */
    public NodeUniqueID GetDeviceID()
    {
        return DeviceID;
    }

    /**
     * this function gets the device join key
     * @return the device join key
     */
    public Key GetJoinKey()
    {
        return JoinKey;
    }

    /**
     * this function gets the device tag
     * @return the device tag
     */
    public DeviceTag GetDeviceTag()
    {
        return DeviceTag;
    }

    /**
     * this function gets the device type
     * @return the device type
     */
    public nmDeviceType GetDeviceType()
    {
        return DeviceType;
    }
}

