package Main;

import Global.User;
import folderwatcher.FolderWatcher;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {    
    public static void main(String [] args) {
        ClientHelper.initializeClient();
        try {
            Thread folderWatcher = new Thread(new FolderWatcher(Paths.get(User.getDirectory()),true));
            folderWatcher.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
