package manager.gui;

import mesh.ScanPeriod;

/**
 * This class installs the graph prunning options
 * @author Song Han
 */

public class GraphPruneOption {

    // Prune options for the vertex
    boolean vPrunePower;
    
    double vPowerAllUpperBound;
    double vPowerLeftUpperBound;
    double vPowerAllLowerBound;
    double vPowerLeftLowerBound;

    boolean vDisplayGW;
    boolean vDisplayAP;
    boolean vDisplayDevice;
    boolean vDisplayRouter;
    boolean vDisplayHH;

    boolean vDisplayDisabledDevice;
    boolean vDisplayEnabledDevice;

    boolean vPruneScanPeriod;
    ScanPeriod scanUpperBound;
    ScanPeriod scanLowerBound;

    // Prune options for the edges
    boolean ePruneWeight;
    boolean ePrunCapacity;
    boolean ePrunSignalStrength;

    double capacityUpperBound;
    double capacityLowerBound;

    double weightUpperBound;
    double weightLowerBound;

    double signalStrengthUpperBound;
    double signalStrengthLowerBound;

    /**
     * Constructor function. Initialize the options with the default values
     */
    public GraphPruneOption()
    {
        // Default Prune options for the vertex
        vPrunePower = false;
        vPowerAllUpperBound = 100;
        vPowerLeftUpperBound = 100;

        vPowerAllLowerBound = 0;
        vPowerLeftLowerBound = 0;

        vDisplayGW = false;
        vDisplayAP = false;
        vDisplayDevice = false;
        vDisplayRouter = false;
        vDisplayHH = false;

        vDisplayDisabledDevice = false;
        vDisplayEnabledDevice = false;

        vPruneScanPeriod = false;
        scanUpperBound = new ScanPeriod(ScanPeriod._8_min_32_sec);
        scanLowerBound = new ScanPeriod(ScanPeriod._0_ms);

        // Default Prune options for the edges
        ePruneWeight = false;
        ePrunCapacity = false;
        ePrunSignalStrength = false;

        capacityUpperBound = 1.0;
        capacityLowerBound = 0;

        weightUpperBound = 1.0;
        weightLowerBound = 0;

        signalStrengthUpperBound = 2.0;
        signalStrengthLowerBound = -60;
    }

    /**
     * This function checks if the PowerPrune is enabled or not
     * @return true if the PowerPrune is enabled
     */
    public boolean isPowerPruned()
    {   return vPrunePower; }

    /**
     * This function enables the PowerPrune option
     * @param p true if we will enable the PowerPrune option. Otherwise, disable it.
     */
    public void setPowerPruned(boolean p)
    {   vPrunePower = p;   }

    /**
     * This function gets the total power upper bound
     * @return the total power upper bound
     */
    public double getPowerAllUpperBound()
    {
        return vPowerAllUpperBound;
    }

    /**
     * This function sets the total power upper bound
     * @param threshold the total power upper bound
     */
    public void setPowerAllUpperBound(double threshold)
    {
        vPowerAllUpperBound = threshold;
    }

    /**
     * This function gets the remaining power upper bound
     * @return the remaining power upper bound
     */
    public double getPowerLeftUpperBound()
    {
        return vPowerLeftUpperBound;
    }

    /**
     * This function sets the remaining power upper bound
     * @param threshold the remaining power upper bound
     */
    public void setPowerLeftUpperBound(double threshold)
    {
        vPowerLeftUpperBound = threshold;
    }

    /**
     * This function gets the total power lower bound
     * @return the total power lower bound
     */
    public double getPowerAllLowerBound()
    {
        return vPowerAllLowerBound;
    }

    /**
     * This function sets the total power lower bound
     * @param threshold the total power lower bound
     */
    public void setPowerAllLowerBound(double threshold)
    {
        vPowerAllLowerBound = threshold;
    }

    /**
     * This function gets the remaining power lower bound
     * @return the remainning power lower bound
     */
    public double getPowerLeftLowerBound()
    {
        return vPowerLeftLowerBound;
    }

    /**
     * This function sets the remaining power lower bound
     * @param threshold the remainning power lower bound
     */
    public void setPowerLeftLowerBound(double threshold)
    {
        vPowerLeftLowerBound = threshold;
    }

    /**
     * This function checks if the GW will be displayed in the graph
     * @return true if the GW will be displayed in the graph
     */
    public boolean isGWDisplayed()
    {
        return vDisplayGW;
    }

    /**
     * This functions decides if the GW will be displayed in the graph
     * @param d true if the GW will be displayed in the graph. Otherwise not
     */
    public void setGWDisplayed(boolean d)
    {
        vDisplayGW = d;
    }

    /**
     * This function checks if the AP will be displayed in the graph
     * @return true if the AP will be displayed in the graph
     */
    public boolean isAPDisplayed()
    {
        return vDisplayAP;
    }

    /**
     * This functions decides if the AP will be displayed in the graph
     * @param d true if the AP will be displayed in the graph. Otherwise not
     */
    public void setAPDisplayed(boolean d)
    {
        vDisplayAP = d;
    }

    /**
     * This function checks if the Device will be displayed in the graph
     * @return true if the Device will be displayed in the graph
     */
    public boolean isDeviceDisplayed()
    {
        return vDisplayDevice;
    }

    /**
     * This functions decides if the Device will be displayed in the graph
     * @param d true if the Device will be displayed in the graph. Otherwise not
     */
    public void setDeviceDisplayed(boolean d)
    {
        vDisplayDevice = d;
    }

    /**
     * This function checks if the Router will be displayed in the graph
     * @return true if the Router will be displayed in the graph
     */
    public boolean isRouterDisplayed()
    {
        return vDisplayRouter;
    }

    /**
     * This functions decides if the Router will be displayed in the graph
     * @param d true if the Router will be displayed in the graph. Otherwise not
     */
    public void setRouterDisplayed(boolean d)
    {
        vDisplayRouter = d;
    }

    /**
     * This function checks if the Handheld device will be displayed in the graph
     * @return true if the Handheld device will be displayed in the graph
     */
    public boolean isHHDisplayed()
    {
        return vDisplayHH;
    }

    /**
     * This functions decides if the Handheld device will be displayed in the graph
     * @param d true if the Handheld device will be displayed in the graph. Otherwise not
     */
    public void setHHDisplayed(boolean d)
    {
        vDisplayHH = d;
    }

    /**
     * This function checks if the enabled devices will be displayed
     * @return true if the enabled devices will be displayed
     */
    public boolean isEnabledDeviceDisplayed()
    {
        return vDisplayEnabledDevice;
    }

    /**
     * This function decides if the enabled devices will be displayed
     * @param d true if the enabled devices will be displayed
     */
    public void setEnabledDeviceDisplayed(boolean d)
    {
        vDisplayEnabledDevice = d;
    }

    /**
     * This function checks if the disabled devices will be displayed
     * @return true if the disabled devices will be displayed
     */
    public boolean isDisabledDeviceDisplayed()
    {
        return vDisplayDisabledDevice;
    }

    /**
     * This function decides if the disabled devices will be displayed
     * @param d true if the disabled devices will be displayed
     */
    public void setDisabledDeviceDisplayed(boolean d)
    {
        vDisplayDisabledDevice = d;
    }

    /**
     * This function checks if the scanperiod is pruned
     * @return true i the scanperiod is pruned
     */
    public boolean isScanPeriodPruned()
    {
        return vPruneScanPeriod;
    }

    /**
     * This function sets if the scanperiod is pruned
     * @param p true if the scanperiod is pruned
     */
    public void setScanPeriodPruned(boolean p)
    {
        vPruneScanPeriod = p;
    }

    /**
     * This function gets the scanperiod upper bound
     * @return the scanperiod upper bound
     */
    public ScanPeriod getScanPeriodUpperBound()
    {
        return scanUpperBound;
    }

    /**
     * This function sets the scanperiod upper bound
     * @param sp the scanperiod upper bound
     */
    public void setScanPeriodUpperBound(ScanPeriod sp)
    {
        scanUpperBound = sp;
    }

    /**
     * This function gets the scanperiod lower bound
     * @return the scanperiod lower bound
     */
    public ScanPeriod getScanPeriodLowerBound()
    {
        return scanLowerBound;
    }

    /**
     * This function sets the scanperiod lower bound
     * @param sp the scanperiod lower bound
     */
    public void setScanPeriodLowerBound(ScanPeriod sp)
    {
        scanLowerBound = sp;
    }
    
    /**
     * This function checks if the edge weight is pruned
     * @return true if the edge weight is pruned
     */
    public boolean isEdgeWeightPruned()
    {
        return ePruneWeight;
    }

    /**
     * This function sets the edge weight pruned
     * @param ewp true if the edge weight is to be pruned.
     */
    public void setEdgeWeightPruned(boolean ewp)
    {
        ePruneWeight = ewp;
    }

    /**
     * This function checks if the edge capacity is pruned
     * @return true if the edge capacity is pruned
     */
    public boolean isEdgeCapacityPruned()
    {
        return ePrunCapacity;
    }

    /**
     * This function sets the edge capacity pruned
     * @param ewp true if the edge capacity is to be pruned.
     */
    public void setEdgeCapacityPruned(boolean ecp)
    {
        ePrunCapacity = ecp;
    }

    /**
     * This function checks if the edge signal strength is pruned
     * @return true if the edge signal strength is pruned
     */
    public boolean isEdgeSignalStrengthPruned()
    {
        return ePrunSignalStrength;
    }

    /**
     * This function sets the edge signal strength pruned
     * @param ewp true if the edge signal strength is to be pruned.
     */
    public void setEdgeSignalStrengthPruned(boolean esp)
    {
        ePrunSignalStrength = esp;
    }

    /**
     * This function sets the edge capacity upper bound
     * @param cub the edge capacity upper bound
     */
    public void setCapacityUpperBound(double cub)
    {
        capacityUpperBound = cub;
    }

    /**
     * This function gets the edge capacity upper bound
     * @return the edge capacity upper bound
     */
    public double getCapacityUpperBound()
    {
        return capacityUpperBound;
    }

    /**
     * This function sets the edge capacity lower bound
     * @param clb the edge capacity lower bound
     */
    public void setCapacityLowerBound(double clb)
    {
        capacityLowerBound = clb;
    }

    /**
     * This function gets the edge capacity lower bound
     * @return the edge capacity lower bound
     */
    public double getCapacityLowerBound()
    {
        return capacityLowerBound;
    }

    /**
     * This function sets the edge weight upper bound
     * @param wub the edge weight upper bound
     */
    public void setWeightUpperBound(double wub)
    {
        weightUpperBound = wub;
    }

    /**
     * This function gets the edge weigth upper bound
     * @return the edge weight upper bound
     */
    public double getWeightUpperBound()
    {
        return weightUpperBound;
    }

    /**
     * This function sets the edge weight lower bound
     * @param wlb the edge weight lower bound
     */
    public void setWeightLowerBound(double wlb)
    {
        weightLowerBound = wlb;
    }

    /**
     * This function gets the edge weight lower bound
     * @return the edge weight lower bound
     */
    public double getWeightLowerBound()
    {
        return weightLowerBound;
    }

    /**
     * This function sets the edge signal strength upper bound
     * @param sub the edge signal strength upper bound
     */
    public void setSignalStrengthUpperBound(double sub)
    {
        signalStrengthUpperBound = sub;
    }

    /**
     * This function gets the signal strength upper bound
     * @return the signal strength upper bound
     */
    public double getSignalStrengthUpperBound()
    {
        return signalStrengthUpperBound;
    }

    /**
     * This function sets the edge signal strength lower bound
     * @param slb the edge signal strength lower bound
     */
    public void setSignalStrengthLowerBound(double slb)
    {
        signalStrengthLowerBound = slb;
    }

    /**
     * This function gets the edge signal strength lower bound
     * @return the edge signal strength lower bound
     */
    public double getSignalStrengthLowerBound()
    {
        return signalStrengthLowerBound;
    }
}