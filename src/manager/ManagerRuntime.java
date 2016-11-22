/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This function is used to get system run time information
 * @author Hang Yu
 */
public class ManagerRuntime {
    
    /**
     * 
     * @param procName the thread name that we want to search for 
     */
    // get the thread id of appointed program
    public static ArrayList<Integer> getThreadID(String procName)
    {
        ArrayList<Integer> threadList = new ArrayList<Integer>();
        try{
              Process p = Runtime.getRuntime().exec("tasklist");
              InputStream in = p.getInputStream();
              InputStreamReader reader = new InputStreamReader(in, "UTF-8");
              BufferedReader br = new BufferedReader(reader);
              String inStr = null;
              
              while( (inStr = br.readLine()) != null )
              {
                  if(inStr.indexOf(procName)!=-1)
                  {
                      String[]ss = inStr.trim().split("\\s+");
                      int id = Integer.parseInt(ss[1]);
                          threadList.add(id);
                  }
              }
            }
        catch(Exception e)
            {
                e.printStackTrace();
            }
        return threadList;
    }
    
    
    /**
     * 
     */
    public static void closeThread(String procName)
    {
        ArrayList<Integer> threadList = getThreadID(procName);
        for(int i = 0; i<threadList.size();i++)
        {
            String cmd = "taskkill /f /pid " + Integer.toString(threadList.get(i));
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(ManagerRuntime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    public static void runProc(String procName)
    {
        try {
            Runtime.getRuntime().exec(procName);
        } catch (IOException ex) {
            Logger.getLogger(ManagerRuntime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
