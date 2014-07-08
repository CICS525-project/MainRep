/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package folderwatcher;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Ali
 */
public class FolderWatcher {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final Path dir = Paths.get("C:\\Users\\Ali\\Documents\\NetBeansProjects\\FolderWatcher\\FolderWatched");
    
    public FolderWatcher() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.registerAllFolders(dir);
        this.eventHandler();
        
    }
    
    private void register(Path directory) throws IOException{
       WatchKey key = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
       keys.put(key, directory); 
    }
    
    private void registerAllFolders(Path directory) throws IOException {
         // register directory and sub-directories
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    private void eventHandler() throws IOException {
        while(true){
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }
            
            //getting the current timestamp
            java.util.Date date= new java.util.Date();
            java.sql.Timestamp currTimeStamp = new java.sql.Timestamp(date.getTime());
            System.out.println(currTimeStamp);
            
            
            Path directory = keys.get(key);
            
            if (dir == null) {
                System.err.println("WatchKey not recognized! Event will be ignored.");
                continue;
            }
            
            for(WatchEvent<?> event : key.pollEvents()) {
                //getting the event kind
                WatchEvent.Kind<?> kind = event.kind();
                
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileNamePath = ev.context();
                Path dirPath = directory.resolve(fileNamePath);
                
                if (kind == ENTRY_CREATE) {
                    
                    if(Files.isDirectory(dirPath, NOFOLLOW_LINKS)){
                        this.registerAllFolders(dirPath);
                    }
                }
                System.out.println("event type: " + kind.name());
                System.out.println("relative path: " + dirPath);
                System.out.println("element name: " + fileNamePath.getFileName() + "\n");
            }
            boolean valid = key.reset();
            if (!valid) {
            break;
            }
            
            
        }
    }
    
    public static void main(String[] args) {
        try{
        new FolderWatcher();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    
    
}
