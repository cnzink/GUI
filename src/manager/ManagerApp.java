package manager;

import java.awt.AWTException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 * @author Hang Yu
 */
public class ManagerApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        try {
            try {
                MeshSimulator mainFrame = new MeshSimulator(this);
                show(mainFrame);
            } catch (IOException ex) {
                Logger.getLogger(ManagerApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
        catch (AWTException ex) {
            Logger.getLogger(ManagerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ManagerApp
     */
    public static ManagerApp getApplication() {
        return Application.getInstance(ManagerApp.class);
    }

    /**
     * Main method launching the application.
     * @param args no arguments is needed
     */
    public static void main(String[] args) throws IOException {
        // start the GUI
        launch(ManagerApp.class, args);
    }
}