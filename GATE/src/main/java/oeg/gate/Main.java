/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.gate;

import gate.Gate;
import gate.gui.MainFrame;
import gate.util.GateException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import javax.swing.SwingUtilities;

/**
 *
 * @author pcalleja
 */
public class Main {
    
    
    public static void main(String [] args) throws GateException, InterruptedException, InvocationTargetException, MalformedURLException{
    
        
         
            Gate.setGateHome(new File(  "GateHome"));
            Gate.setPluginsHome(new File(Gate.getGateHome()+File.separator+"Plugins"));
            Gate.setSiteConfigFile(new File(Gate.getGateHome() + File.separator + "gate.xml"));
            Gate.setUserConfigFile(new File(Gate.getGateHome()+ File.separator + "gate.xml"));
            Gate.setUserSessionFile(new File(Gate.getGateHome() + File.separator + "gate.session"));
            
          
            Gate.init();
    
             SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
            MainFrame.getInstance().setVisible(true) ;
                    }
            }) ;
            
            
            Gate.getCreoleRegister().registerDirectories(new File( Gate.getPluginsHome().getAbsolutePath() +File.separator+"ANNIE").toURI().toURL()); 
             
    
    
    }
    
}
