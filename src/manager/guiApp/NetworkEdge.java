package manager.guiApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * this class defines the network edge.
 * @author Song Han
 */
public class NetworkEdge implements java.io.Serializable
{

    // the network edge ID
    private int id;
    // the static edge ID
    private static int edgeID = 0;

    // properties and default values of the network edge
    private double weight;
    private double capacity;
    private boolean isSwapped;
    private double signalStrength;
    private double swapSignalStrength;

    private double defaultWeight = 1.0;
    private double defaultCapacity = 1.0;
    private double defaultSignalStrength = 1.0;
    final double defaultBestSignalStrength = 5;
    final double defaultWorstSignalStrength = -100;

    /**
     * Constructor function. It creates an instance of the network edge with the given parameters
     * @param edgeId the ID of the network edge
     * @param w the weight of the network edge
     * @param c the capacity of the network edge
     * @param s the signal strength of the network edge
     */
    public NetworkEdge(int edgeId, double w, double c, double s)
    {
        id = edgeId;
        weight = w;
        capacity = c;
        swapSignalStrength = signalStrength = s;
        isSwapped = false;
    }
    
    /**
     * this function checks if the signal strength is swapped or not
     * @return true if the signal strength is swapped. Otherwise return false.
     */
    public boolean IsSignalStrengthSwapped()
    {
        return this.isSwapped;
    }

    /**
     * this function sets the signal strength to be swapped
     */
    public void SetSignalStrengthSwapped()
    {
        this.isSwapped = true;
    }

    /**
     * this function sets the signal strength not swapped
     */
    public void SetSignalStrengthNotSwapped()
    {
        this.isSwapped = false;
    }

    /**
     * Constructor function. Creates an instance of the network edge using default values.
     */
    public NetworkEdge()
    {
        weight = defaultWeight;
        capacity = defaultCapacity;
        signalStrength = defaultSignalStrength;
        id = ++edgeID;
    }

    /**
     * Constructor function. Creates an instance of the network edge using given parameters
     * @param w the weight of the network edge
     * @param c the capacity of the network edge
     * @param s the signal strength of the network edge
     */
    public NetworkEdge(double w, double c, double s)
    {
        weight = w;
        capacity = c;
        signalStrength = s;
        id = ++edgeID;
    }

    /**
     * this function display the signal strength of the network edge in the string format
     * @return the signal strength of the network edge in the string format
     */
    @Override
    public String toString()
    {
        return "" + (int) signalStrength;
    }
    
    /**
     * this function display the weight of the network edge in the string format
     * @return the weight of the network edge in the string format
     */
    public String summary()
    {
        return "Edge " + id + ": " + weight;
    }

    /**
     * this function gets the weight of the network edge
     * @return the weight of the network edge
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * this function sets the weight of the network edge
     * @param w the weight of the network edge
     */
    public void setWeight(double w)
    {
        weight = w;
    }

    /**
     * this function gets the signal strength of the network edge
     * @return the signal strength of the network edge
     */
    public double getSignalStrength()
    {
        return signalStrength;
    }

    /**
     * this function gets the swapped signal strength of the network edge
     * @return the swapped signal strength of the network edge
     */
    public double getSwapSignalStrength()
    {
        return this.swapSignalStrength;
    }

    /**
     * this function calculate the link quality of the network edge
     * @return the link quality of the network edge
     */
    public double calcLinkQuality()
    {
        // use different model to calculate the relation between the link quality and signal strength
        if(signalStrength >= defaultBestSignalStrength)
            return 1.0;
        else if(signalStrength <= defaultWorstSignalStrength)
            return 0.0;
        else
        {
            return Math.min(1, 1 + (signalStrength / 100));
        }
    }

    /**
     * this function sets the signal strength of the network edge
     * @param s the signal strength of the network edge
     */
    public void setSignalStrength(double s)
    {
        signalStrength = s;
    }

    /**
     * this function sets the swapped signal strength of the network edge
     * @param s the swapped signal strength of the network edge
     */
    public void setSwapSignalStrength(double s)
    {
        this.swapSignalStrength = s;
    }

    /**
     * this function gets the capacity of the network edge
     * @return the capacity of the network edge
     */
    public double getCapacity()
    {
        return capacity;
    }

    /**
     * this function sets the capacity of the network edge
     * @param c the capacity of the network edge
     */
    public void setCapacity(double c)
    {
        capacity = c;
    }

    /**
     * this function gets the edge ID in the string format
     * @return the edge ID in the string format
     */
    public String getName()
    {
        return "Edge " + id;
    }

    /**
     * this function gets the edge ID value
     * @return the edge ID value
     */
    public int getEdgeId()
    {
        return id;
    }
    
    // Serializable interface: 
    private void writeObject(ObjectOutputStream o) throws IOException
    {
        o.writeObject(id);
        o.writeObject(signalStrength);
    }

    // Serializable interface: 
    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
    {
        id = (Integer) o.readObject();
        signalStrength = (Double) o.readObject();
    }
}
