package Main;

import BlobManager.BlobManager;
import Global.User;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHelper {

    public static void initializeClient() {
        //create default directory where the program would store info
        File dir = new File("C:/Watcher");
        dir.mkdir();
        User.setUsername("democontainer");
        User.setUserId("1");
        User.setDirectory("C:/Watcher");
        syncFilesAtStartUp(null);
        //continuallySyncFiles(null);
    }

    public static User performAuthentication() {
        //check if this has been done before if not ask user for credentials

        //if new user create container 
        return null;
    }

    public static void syncFilesAtStartUp(User u) {
        //run the file syncing in a thread

        Thread initClient = new Thread(new Runnable() {
            public void run() {
                BlobManager.setContainerName(User.getUsername());
                BlobManager.createContainter(User.getUsername());
                BlobManager.downloadAllBlobs();
            }
        });
        initClient.start();
    }

    public static void continuallySyncFiles(User u) {
        Thread initClient = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
                while (true) {
                    BlobManager.downloadAllBlobs();
                    try {
                        System.out.println("Continually sync thread started");
                        Thread.sleep(600000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        initClient.start();
        System.out.println("Continually sync thread started");
    }

    public static void maintainConnection() {
        //constantly tell the server you are around
        new Runnable() {
            public void run() {
                //add the socket code to comminicate with server here

                Process p1 = null;
                try {
                    p1 = java.lang.Runtime.getRuntime().exec("ping www.google.com");
                    System.out.println(p1.waitFor());
                } catch (IOException ex) {
                    Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
                }

                // return code for p1 will be 0 if internet is connected, else it will be 1
            }
        };
    }
}
