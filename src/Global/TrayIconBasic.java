package Global;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
 
public class TrayIconBasic {
    
    public TrayIconBasic() {
        if (!SystemTray.isSupported()) {
            // Go directory to the task;
            return;
        }
 
        //Image icon = Toolkit.getDefaultToolkit().getImage("");        
        Image icon = createIcon("/Images/fb.png", "NSYNC File Sharing");
        if (icon == null) {
            // Go directory to the task;
            return;
        }
 
        // create the trayIcon itself.
        final TrayIcon trayIcon = new TrayIcon(icon);
        //final TrayIcon trayIcon2 = new TrayIcon(icon, "Ok", new JPopupMenu());
 
        // access the system tray. If not supported 
        // or if notification area is not present (Ubuntu)  
        // a NotSupportedException exception is trown;
 
        final SystemTray tray = SystemTray.getSystemTray();
 
        // Create popup menu
        PopupMenu popup = new PopupMenu();
        MenuItem exit = new MenuItem("Exit");
 
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Do some cleanup
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
 
        popup.add(exit);
 
         
        // Add tooltip and menu to trayicon
        trayIcon.setToolTip("NSYNC");
        trayIcon.setPopupMenu(popup);
 
        // Add the trayIcon to system tray/notification
        // area
 
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Could not load tray icon !");
        }        
         
        // Just to show how to add an alert/error message
        trayIcon.displayMessage("Alert", "NSYNC Started!!!", 
            TrayIcon.MessageType.INFO);        
    }
 
    public static void main(String[] args) {
 
       new TrayIconBasic();
    }
 
 
    // A handy method to create an Image instance.
    protected static Image createIcon(String path, String description) {
        URL imageURL = TrayIconBasic.class.getResource(path);
        if (imageURL == null) {
            System.err.println(path + " not found");
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}