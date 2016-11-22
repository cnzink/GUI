package manager.gui;

import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import manager.guiApp.NetworkVisualizer;

/**
 * This class defines the static barrier
 * @author Song Han
 */

public class StaticBarrier implements Paintable {

    NetworkVisualizer visualizer;

    private int xPosition;
    private int yPosition;

    int xBegin;
    int yBegin;

    private int width;
    private int height;
    
    boolean inTheBarrier = false;

    /**
     * Constructor function. It creates an instance of the StaticBarrier with the default settings
     * @param vis the network visualizer.
     * @throws IOException the I/O exception.
     */
    public StaticBarrier(NetworkVisualizer vis) throws IOException
    {
        visualizer = vis;
        xBegin = xPosition = 30;
        yBegin = yPosition = 30;
        width = 50;
        height = 50;
    }

    /**
     * The constructor function. It creates an instance of the static barrier with the given parameters
     * @param vis the network visualizer
     * @param x the x position of the barrier
     * @param y the y position of the barrier
     * @param w the width of the barrier
     * @param h the height of the barrier
     * @throws IOException the I/O exception
     */
    public StaticBarrier(NetworkVisualizer vis, int x, int y, int w, int h) throws IOException
    {
        visualizer = vis;
        xBegin = xPosition = x;
        yBegin = yPosition = y;
        width = w;
        height = h;
    }
    
    /**
     * The overrided function from the Paintable interface.
     * @param g the graphics instance
     */
    @Override
    public void paint(Graphics g) {
        
        Color oldColor = g.getColor();
        g.setColor(Color.gray);
        g.fillRect(xPosition, yPosition, width, height);
        g.setColor(oldColor);
    }

    /**
     * This function checks if the tranform is used or not
     * @return true if the transform is used. Otherwise return false.
     */
    public boolean useTransform() {
        return false;
    }

    /**
     * This function checks if a point is in the barrier
     * @param x the x position
     * @param y the y position
     * @return true if the point is in the barrier. Otherwise return false.
     */
    public boolean isInBarrier(double x, double y)
    {
        if((x >= xPosition) && (x <= xPosition + width)
        && (y >= yPosition) && (y <= yPosition + height))
            return true;
        else
            return false;
    }

    /**
     * This function checks the value of the boolean varible inTheBarrier
     * @return true if the value of the boolean varible inTheBarrier is true. Otherwise, return false.
     */
    public boolean getInBarrier()
    {
        return inTheBarrier;
    }

    /**
     * This function sets the value of the inTheBarrier variable
     * @param i the value of the inTheBarrier variable
     */
    public void setInBarrier(boolean i)
    {
        inTheBarrier = i;
    }

    /**
     * This function sets the x position
     * @param x the x position
     */
    public void setX(int x)
    {
        xPosition = x;
    }

    /**
     * This function gets the x position
     * @return the x position
     */
    public int getX()
    {
        return xPosition;
    }

    /**
     * This function sets the original x position
     * @param x the original x position
     */
    public void setXBegin(int x)
    {
        xBegin = x;
    }

    /**
     * This function gets the original x position
     * @return the original x position
     */
    public int getXBegin()
    {
        return xBegin;
    }

    /**
     * This function sets the original y position
     * @param y the original y position
     */
    public void setYBegin(int y)
    {
        yBegin = y;
    }

    /**
     * This function gets the original y position
     * @return the original y position
     */
    public int getYBegin()
    {
        return yBegin;
    }
    
    /**
     * This function sets the current y position
     * @param y the current y position
     */
    public void setY(int y)
    {
        yPosition = y;
    }

    /**
     * This function gets the current y position
     * @return the current y position
     */
    public int getY()
    {
        return yPosition;
    }

    /**
     * This function sets the barrier width
     * @param w the barrier width
     */
    public void setWidth(int w)
    {
        width = w;
    }

    /**
     * This function gets the barrier width
     * @return the barrier width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * This function sets the barrier height
     * @param h the barrier height
     */
    public void setHeight(int h)
    {
        height = h;
    }

    /**
     * This function gets the barrier height
     * @return the barrier height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * This function checks if the barrier intersects with a given line
     * @param src the src point of the line
     * @param dest the dest point of the line
     * @return true if the barrier intersects with the given line. Otherwise return false.
     */
    public boolean IntersectWithLine(Point2D src, Point2D dest)
    {
        Line2D.Double line = new Line2D.Double(src, dest);
        Rectangle2D.Double rect = new Rectangle2D.Double(xPosition, yPosition, width, height);
        return line.intersects(rect);
    }

    /**
     * This function display the barrier information in a string format
     * @return the barrier information in a string format
     */
    @Override
    public String toString()
    {
        return "" + xPosition + " " + yPosition + " " + width + " " + height;
    }
}
