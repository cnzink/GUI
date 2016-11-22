package manager.gui;

import java.awt.geom.Point2D;

/**
 * The interface for the menu point listener
 * @author Song Han
 */
public interface MenuPointListener {

    /**
     * This function sets the point
     * @param point the location in the visualization viewer
     */
    void   setPoint(Point2D point);
}

