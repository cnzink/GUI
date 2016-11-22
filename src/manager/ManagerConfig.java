package manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mesh.NetworkID;
import mesh.ScanPeriod;
import mesh.util.Converter;
import org.xml.sax.SAXException;

/**
 * Manager GUI's cached values from ManagerCore's Configuration
 * These values are read from the manager core. The default values here are not used.
 */
public class ManagerConfig {

    // a summary of the network manager configurations and their default values
    static private NetworkID networkID = new NetworkID(new byte[]{0x00, 0x00});
    static private char ChannelMap = 0x0000;
    static private byte[] joinKey = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    static private int maxHopLimit = 3;
    
    static private ArrayList<AdditionalDevice> DeviceList = new ArrayList<AdditionalDevice>();
    static final String XML_FileName = "NMConfig.xml";
    static final String XML_BakFileName = "NMConfig.xml.bak";
    static final int SF_ScanPeriod = ScanPeriod._2_sec;
    static final int maxNetworkSize = 40;
    static final int maxNeighborSize = 8;
    static final int maxNetworkDepth = 3;
    
    static private boolean isNetworkIDReady = false; //determines if the networkID is ready to display in Network Configuration window. We recv valid Id when we get the networkID from the Core
    static private boolean isChannelMapReady = false; //same concept as networkID
    static private boolean isJoinKeyReady = false; // same concept as networkID
    static private boolean isMaxHopLimitReady = false;
    
    // Unused (due to removal of device white list)
    static private ArrayList<AdditionalDevice> devicesPendingUpdate = new ArrayList<AdditionalDevice>(); //contains new or edited devices that need to be sent to ManagerCore to update ManagerCore's xml deviceList
    static private ArrayList<Integer> indexDevicesPendingUpdate = new ArrayList<Integer>(); //contains index in DeviceList for items in devicesPendingUpdate. e.g: "devicesPendingUpdate.get(i)" has index "indexDevicePendingUpdate.get(i)".
    static private ArrayList<Integer> actionDevicesPendingUpdate = new ArrayList<Integer>(); //contains actions for DeviceList items in devicesPendingUpdate, "0 = edit, 1 = add, 2 = delete"

    
    /**
     * Check if ManagerConfig has received networkID, channelMap, and joinKey from ManagerCore.
     * @return true if networkID, ChannelMap, and joinKey are ready to display on network configuration window
     */
    static public boolean isDataReady()
    {
        return isNetworkIDReady && isChannelMapReady && isJoinKeyReady && isMaxHopLimitReady;
    }
    
    /**
     * this function gets the network ID
     * @return the network ID
     */
    static public NetworkID getNetworkID()
    {
        return networkID;
    }
    
    /**
     * this function gets the join key (16 bytes)
     * @return the join key
     */
    static public byte[] getJoinKey()
    {
        return joinKey;
    }
    
    /**
     * this function gets the max network size
     * @return the max network size
     */
    static public int GetMaxNetworkSize()
    {
        return maxNetworkSize;
    }
    
    /**
     * this function gets the max neighbor size
     * @return the max neighbor size
     */
    static public int GetMaxNeighborSize()
    {
        return maxNeighborSize;
    }
    
    /**
     * this function gets the max network depth
     * @return the max network depth
     */
    static public int GetMaxNetworkDepth()
    {
        return maxNetworkDepth;
    }
    
    /**
     * this function gets the superframe scan period
     * @return the superframe scan period
     */
    static public int GetSFScanPeriod()
    {
        return SF_ScanPeriod;
    }

    /**
     * this function gets the channel map
     * @return the channel map
     */
    static public char GetChannelMap()
    {
        return ChannelMap;
    }

    /**
     * this function gets the device list
     * @return the device list
     */
    static public ArrayList<AdditionalDevice> GetDeviceList()
    {
        return DeviceList;
    }
    
    /**
     * This removes all previous devices in deviceList
     */
    static public void ClearDeviceList()
    {
        DeviceList.clear();
    }

    /**
     * this function sets the network ID
     * @param value the network ID
     */
    static public void setNetworkID(String value)
    {
        String formattedInput = FormatHexString(value);
        byte[] inputBytes = Converter.HexStringToByte(formattedInput);
        networkID.setNetworkIdBytes(inputBytes);
        isNetworkIDReady = true;
    }

    /**
     * this function sets the channel map
     * @param value the channel map
     */
    static public void SetChannelMap(String value)
    {
        ChannelMap = Converter.HexStringToChar(FormatHexString(value))[0];
        isChannelMapReady = true;
    }

    /**
     * this function sets the join key (16 bytes)
     * @param key 
     */
    static public void setJoinKey(byte[] key)
    {
        System.arraycopy(key, 0, joinKey, 0, key.length);
        isJoinKeyReady = true;
    }
    
    /**
     * Get the max hop limit
     * @return 
     */
    static public int getMaxHopLimit()
    {
        return maxHopLimit;
    }
    
    /**
     * Set the max hop limit
     * @param hopLimit 
     */
    static public void setMaxHopLimit(int hopLimit)
    {
        maxHopLimit = hopLimit;
        isMaxHopLimitReady = true;
    }
    
    /**
     *  the supporting function to get the element value in the xml file
     * @param the given element
     * @return the element value in the string format
     */
    static private String GetElementValue(Element e)
    {
        NodeList subLst = e.getChildNodes();
        Node node = (Node) subLst.item(0);
        return node.getNodeValue();
    }

    /**
     * the supporting function to set the element value in the xml file
     * @param the given element
     * @param the value of the element in the string format
     */
    static private void SetElementValue(Element e, String value)
    {
        NodeList subLst = e.getChildNodes();
        Node node = (Node) subLst.item(0);
        node.setNodeValue(value);
    }

    /**
     * this function converts a string in a given format to the hex format
     * @param hex the string in a given format
     * @return the content of the string in the hex format
     */
    static public String FormatHexString(String hex)
    {
        StringBuilder sb = new StringBuilder(hex.length());
        String[] list = hex.split(",");
        for(int i=0; i<list.length; i++) {
            String item = list[i].trim();
            if (item.length() > 2 && item.charAt(0) == '0' &&
                    (item.charAt(1) == 'x' || item.charAt(1) == 'X'))
                item = item.substring(2);
            sb.append(item);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * this function converts a char value to the hex string format with "0x" preappended
     * @param value the char value
     * @return the hex string format of the char value with "0x" preappended
     */
    static public String ToXmlFormatHexString(char value)
    {
        return "0x" + Converter.CharToHexString(value).toUpperCase();
    }

    /**
     * this function converts a byte value to the hex string format with "0x" preappended
     * @param value the byte value
     * @return the hex string format of the byte value with "0x" preappended
     */
    static public String ToXmlFormatHexString(byte value)
    {
        return "0x" + Converter.ByteToHexString(value).toUpperCase();
    }

    /**
     * this function converts a char array to the hex string format with "0x" preappended for each char value
     * @param value the char array
     * @return the hex string format with "0x" preappended for each char value
     */
    static public String ToXmlFormatHexString(char[] value)
    {
        StringBuilder sb = new StringBuilder(value.length * 8);
        for(int i=0; i<value.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(ToXmlFormatHexString(value[i]));
        }
        return sb.toString();
    }

    /**
     * this function converts a byte array to the hex string format with "0x" preappended for each byte value
     * @param value the byte array
     * @return the hex string format with "0x" preappended for each byte value
     */
    static public String ToXmlFormatHexString(byte[] value)
    {
        StringBuilder sb = new StringBuilder(value.length * 6);
        for(int i=0; i<value.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(ToXmlFormatHexString(value[i]));
        }
        return sb.toString();
    }

    /**
     * this function checks if the given char value is in a valid hex format
     * @param ch the given char value
     * @return true if the given char value is in a valid hex format
     */
    static public boolean ValiateHexChar(char ch)
    {
        return (ch >= '0' && ch <= '9') ||
                (ch >= 'a' && ch <= 'f') ||
                (ch >= 'A' && ch <= 'F');
    }

    /**
     * this function checks if the given string follows the required format
     * @param hex the given string
     * @param size the required number of units in the string
     * @param unit the size of the unit
     * @return true if the given string follows the required format
     */
    static public boolean ValidateHexString(String hex, int size, int unit)
    {
        String[] list = hex.split(",");
        if (list.length != size)
        {
            return false;
        }
        
        for (int i = 0; i < size; i++)
        {
            String s = list[i].trim();
            if (s.length() == unit + 2)
            {
                if (s.charAt(0) != '0' || !(s.charAt(1) == 'x' || s.charAt(1) == 'X'))
                {
                    return false;
                }
                s = s.substring(2);
            }
            
            if (s.length() != unit)
            {
                return false;
            }
            
            for (int j = 0; j < unit; j++)
            {
                if (!ValiateHexChar(s.charAt(j)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * this function gets the device in the device list by searching the device ID
     * @param excludeIndex the index of the device list at which the device will be excluded from the search
     * @param deviceID the device ID
     * @return the device in the device list whose ID equals to the required device ID. If not found, return null.
     */
    static public AdditionalDevice GetAdditionalDeviceByID(int excludeIndex, String deviceID)
    {
        deviceID = FormatHexString(deviceID);
        for (int i=0; i<DeviceList.size(); i++) {
             if (i != excludeIndex && deviceID.equals(DeviceList.get(i).GetDeviceID().toString().toUpperCase()))
                     return DeviceList.get(i);
        }
        return null;
    }

    /**
     * this function gets the device in the device list by searching the device ID
     * @param deviceID the device ID
     * @return the device in the device list whose ID equals to the required device ID. If not found, return null.
     */
    static public AdditionalDevice GetAdditionalDeviceByID(String deviceID)
    {
        return GetAdditionalDeviceByID(-1, deviceID);
    }
    
    /**
     * Adds one AdditionalDevice to the deviceList
     * @param type type of device
     * @param id unique ID of the device
     * @param joinKey the join key of the device
     * @param tag the device tag
     */
    static public void AddAdditionalDevice(String type, String id, String joinKey, String tag)
    {
        DeviceList.add(new AdditionalDevice(type, id, joinKey, tag));
    }
   
    /**
     * Whenever a user adds a new device or edits a current device, store the changes here, 
     * and send a command to Core to update the devices in the Core (including telling Core to save to xml)
     * @param deviceIndex the index of the updated device in ManagerConfig (for both ManagerGui and ManagerCore)
     * @param action the action to perform (0 - edit, 1 - add, 2 - delete)
     * @param dev  the device that has been updated by user
     */
    static public void addDeviceUpdate(int deviceIndex, int action, AdditionalDevice dev)
    {
         //if user made 2 edits to same device, do not keep both, only keep most recent change
        int indexInList = indexDevicesPendingUpdate.indexOf(deviceIndex);

        if (indexInList == -1) //this is a new update, not a duplicate, just add to list
        {
            devicesPendingUpdate.add(dev);
            indexDevicesPendingUpdate.add(deviceIndex);
            actionDevicesPendingUpdate.add(action);
        }
        else //device already has a previous update, overwrite previous changes, according to different cases:
        {
            int prevAction = actionDevicesPendingUpdate.get(indexInList);

            //Add, Edit - added a new device, then edited the new device, so just replace the old device with new device info, keep action as add.
            //Edit, Edit - just overwrite previous edit with new edits. indexInList does not change, action does not change
            if (prevAction != 2 && action == 0)
                devicesPendingUpdate.set(indexInList, dev);

            //Edit, Delete - since user deletes the device, just set the action to delete, it doens't matter what the actual device info is anymore. indexInList does not change.
            if (prevAction == 0 && action == 2)
                actionDevicesPendingUpdate.set(indexInList, action);

            //Add, Delete - these two cancel out, so remove any changes for this device out of the update list
            if (prevAction == 1 && action == 2) 
            {
                devicesPendingUpdate.remove(indexInList);
                actionDevicesPendingUpdate.remove(indexInList);
                indexDevicesPendingUpdate.remove(indexInList);
            }
                
            //Add, Add is impossible, no need to implement a case.
            //Edit, Add is also impossible
            //Delete, <any action> is also impossible
        }
    }

    /**
     * Set a new list of pending device updates
     * @param newList the new list of device pending updates
     */
    static public void setDeviceUpdates(ArrayList<AdditionalDevice> newList)
    {
        devicesPendingUpdate = newList;
    }

    /**
     * Get the current list of pending device updates
     * @return list of pending device updates
     */
    static public ArrayList<AdditionalDevice> getDeviceUpdates()
    {
        return devicesPendingUpdate;
    }

    /**
     * Set a new list of indices for the pending device updates
     * @param newIndexList the new list of indices for pending device updates
     */
    static public void setIndexOfDeviceUpdates(ArrayList<Integer> newIndexList)
    {
        indexDevicesPendingUpdate = newIndexList;
    }

    /**
     * Get the current list of indices for pending device updates
     * @return list of indices for pending device updates
     */
    static public ArrayList<Integer> getIndexOfDeviceUpdates()
    {
        return indexDevicesPendingUpdate;
    }
    
    /**
     * Set a new list of actions for the pending device updates
     * @param newActionList the new list of actions for the pending device updates
     */
    static public void setActionOfDeviceUpdates(ArrayList<Integer> newActionList)
    {
        actionDevicesPendingUpdate = newActionList;
    }
    
    /**
     * Get the current list of actions for the pending device updates
     * @return list of actions for the pending device updates
     */
    static public ArrayList<Integer> getActionOfDeviceUpdates()
    {
        return actionDevicesPendingUpdate;
    }

    /*
     * Add new and edited devices into ManagerConfig's DeviceList
     * Assumption: we have already removed rejected devices via rsp cmd64013
     * So everything can be applied safely.
     */
    static public void applyDeviceUpdates()
    {
        int index, action;
        
        //for each pending update, apply it (edit / add / delete)
        for (int i = 0; i < devicesPendingUpdate.size(); i++)
        {
            index = indexDevicesPendingUpdate.get(i); //the index of this device within DeviceList
            action = actionDevicesPendingUpdate.get(i);
            if (action == 0)
                DeviceList.set(index, devicesPendingUpdate.get(i)); //edited device
            if (action == 1)
                DeviceList.add(devicesPendingUpdate.get(i)); //just add the device    
            if (action == 2)
                DeviceList.set(index, null); //set to null, remove later. this prevents index from changing b/c we need the correct index to reference devices
        }

        //remove all the nulls in DeviceList
        for (int i = 0; i < DeviceList.size(); i++)
        {
            if (DeviceList.get(i) == null)
                DeviceList.remove(i--); //decrement i b/c we removed it from list
        }
        
        devicesPendingUpdate.clear(); //finished processing, clear out the list b/c they do not need to be processed again.
        indexDevicesPendingUpdate.clear();
        actionDevicesPendingUpdate.clear();
    }
    
    
    /**
     * This is currently unused
     * 
     * this function loads the network manager configuration from the xml file
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sax exception
     * @throws IOException the I/O exception
     */
    static public void LoadFromXML() throws ParserConfigurationException, SAXException, IOException
    {
        File file = new File(XML_FileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        // read in the network ID
        NodeList nodeLst = doc.getElementsByTagName("NetworkID");
        if (nodeLst.getLength() == 1) {
            Element subElmnt = (Element) nodeLst.item(0);
            networkID = new NetworkID(FormatHexString(GetElementValue(subElmnt)));
        }

        // read in the channel map
        nodeLst = doc.getElementsByTagName("ChannelMap");
        if (nodeLst.getLength() == 1) {
            Element subElmnt = (Element) nodeLst.item(0);
            ChannelMap = Converter.HexStringToChar(FormatHexString(GetElementValue(subElmnt)))[0];
        }

        // read in the device information from the xml file
        nodeLst = doc.getElementsByTagName("AdditionalDevice");
        DeviceList.clear();
        for (int i = 0; i < nodeLst.getLength(); i++) {
            Node node = nodeLst.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                Element deviceID_Elmnt = (Element) e.getElementsByTagName("DeviceID").item(0);
                Element joinKey_Elmnt = (Element) e.getElementsByTagName("JoinKey").item(0);
                Element deviceTag_Elmnt = (Element) e.getElementsByTagName("DeviceTag").item(0);
                Element deviceType_Elmnt = (Element) e.getElementsByTagName("DeviceType").item(0);
                AdditionalDevice device = new AdditionalDevice(
                        GetElementValue(deviceType_Elmnt),
                        GetElementValue(deviceID_Elmnt), 
                        GetElementValue(joinKey_Elmnt),
                        GetElementValue(deviceTag_Elmnt));
                DeviceList.add(device);
            }
        }
    }

    /**
     * this function appends a text node into the xml file
     * @param doc the xml Document
     * @param parent the parent node of the node to be added in the doc
     * @param nodeName the name of the node to be added in the doc
     * @param nodeValue the value of the node to be added in the doc
     */
    static private void AppendTextNode(Document doc, Node parent, String nodeName, String nodeValue) {
            Node newNode = doc.createElement(nodeName);
            newNode.appendChild(doc.createTextNode(nodeValue));
            parent.appendChild(newNode);
    }

    /**
     *  This is currently unused
     * 
     * this function saves the network manager configuration information to the xml file
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the sax exception
     * @throws IOException the I/O exception
     * @throws TransformerConfigurationException the transformer configuration exception
     * @throws TransformerException the transformer exception
     */
    static public void SaveToXML() throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException
    {
        File file = new File(XML_FileName);
        /*File bak = new File(XML_BakFileName);
        bak.delete();
        file.renameTo(bak);*/

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        // save the network ID information
        NodeList nodeLst = doc.getElementsByTagName("NetworkID");
        if (nodeLst.getLength() == 1) {
            Element subElmnt = (Element) nodeLst.item(0);
            SetElementValue(subElmnt, ToXmlFormatHexString(networkID.getNetworkIdBytes()));
        }

        // save the channel map information
        nodeLst = doc.getElementsByTagName("ChannelMap");
        if (nodeLst.getLength() == 1) {
            Element subElmnt = (Element) nodeLst.item(0);
            SetElementValue(subElmnt, ToXmlFormatHexString(ChannelMap));
        }

        // save the device information
        nodeLst = doc.getElementsByTagName("AdditionalDevice");
        for (int i = nodeLst.getLength()-1; i >= 0; i--) {
            Node node = nodeLst.item(i);
            doc.getFirstChild().removeChild(node);
        }

        for (int i = 0; i < DeviceList.size(); i++) {
            AdditionalDevice device = DeviceList.get(i);
            Node deviceNode = doc.createElement("AdditionalDevice");
            AppendTextNode(doc, deviceNode, "DeviceType", device.GetDeviceType().toString());
            AppendTextNode(doc, deviceNode, "DeviceID", ToXmlFormatHexString(device.GetDeviceID().GetUniqueID()));
            AppendTextNode(doc, deviceNode, "JoinKey", ToXmlFormatHexString(device.GetJoinKey().getKey()));
            AppendTextNode(doc, deviceNode, "DeviceTag", ToXmlFormatHexString(device.GetDeviceTag().GetDeviceTag()));
            doc.getFirstChild().appendChild(deviceNode);
        }

        Transformer tfer = TransformerFactory.newInstance().newTransformer();
        tfer.transform(new DOMSource(doc), new StreamResult(file));
    }
}
