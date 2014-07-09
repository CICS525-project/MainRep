package folderwatcher;

import BlobManager.BlobManager;
import DB.DBManager;
import java.io.File;
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
import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FolderWatcher implements Runnable {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final Path dir = Paths.get("C:\\Watcher\\");
    private static Queue<FolderWatcherQueue> changes;

    public static Queue<FolderWatcherQueue> getChanges() {
        return changes;
    }

    public FolderWatcher() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.registerAllFolders(dir);
        changes = new PriorityQueue<FolderWatcherQueue>();
    }

    private void register(Path directory) throws IOException {
        WatchKey key = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, directory);
    }

    private void registerAllFolders(Path directory) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void eventHandler() throws IOException {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }

            Path directory = keys.get(key);
            System.out.println("path:" + directory.toString());
            if (directory == null) {
                System.err.println("WatchKey not recognized! Event will be ignored.");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                //getting the event kind
                WatchEvent.Kind<?> kind = event.kind();

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileNamePath = ev.context();
                Path dirPath = directory.resolve(fileNamePath);

                if (kind == ENTRY_CREATE) {

                    if (Files.isDirectory(dirPath, NOFOLLOW_LINKS)) {
                        this.registerAllFolders(dirPath);
                    }
                }
                System.out.println("event type: " + kind.name());
                System.out.println("relative path: " + dirPath);
                System.out.println("element name: " + fileNamePath.getFileName() + "\n");
                FolderWatcherQueue fwq = new FolderWatcherQueue();
                Calendar cal = Calendar.getInstance();
                fwq.setDate(cal.getTime());
                fwq.setEventType(kind.name());
                fwq.setFullPath(dirPath.toString());
                changes.add(fwq);
                //System.out.println(fwq.getFullPath());
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public void handleRequest() {

        //serverModify(con,"localpath/path/jjj.txt",getTimeStamp(today));
        //serverInsert(con,"localpath/path/jjj.txt",getTimeStamp(today));
        //serverDelete(con, "localpath/path/jjj.txt");

        //localInsert("localpath/path/jjj.txt",getTimeStamp(today),3);
        //localModify("localpath/path/jjj.txt",getTimeStamp(today),3);
        //localModifyLastUpdate("localpath/path/jjj.txt");
        //localDelete("localpath/path/ttt.txt");

        Thread pollQueue = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    FolderWatcherQueue s = changes.poll();
                    //System.out.println("The size of the " + changes.size());
                    if (s != null) {
                        String eventType = s.getEventType();
                        if (eventType.equals("ENTRY_CREATE")) {
                            handleCreateFile(s);
                        } else if (eventType.equals("ENTRY_DELETE")) {
                            handleDeleteFile(s);
                        } else if (eventType.equals("ENTRY_MODIFY")) {
                            handleModifyFile(s);
                        } else {
                            continue;
                        }
                    }
                }
            }
        });
        pollQueue.start();
    }

    public void handleCreateFile(FolderWatcherQueue s) {
        Connection con = DBManager.establishConnection();
        DBManager db = new DBManager();
        db.serverInsert(con, FileFunctions.getRelativePath(s.getFullPath()), db.getTimeStamp(s.getDate()));
        if (FileFunctions.isDirectory(s.getFullPath())) {
            File[] files = new File(s.getFullPath()).listFiles();
            if (files.length > 0) {
                for (File f : files) {
                    System.out.println("The file path is " + f.getPath());
                    BlobManager.uploadFileAsBlob(f.getPath());
                }
            }
        } else {
            BlobManager.uploadFileAsBlob(s.getFullPath());
        }
    }

    public void handleDeleteFile(FolderWatcherQueue s) {
        Connection con = DBManager.establishConnection();
        DBManager db = new DBManager();
        db.serverDelete(con, FileFunctions.getRelativePath(s.getFullPath()));
        BlobManager.deleteBlob(s.getFullPath());
    }

    public void handleModifyFile(FolderWatcherQueue s) {
        Connection con = DBManager.establishConnection();
        DBManager db = new DBManager();
        db.serverModify(con, FileFunctions.getRelativePath(s.getFullPath()), db.getTimeStamp(s.getDate()));
        if (FileFunctions.isDirectory(s.getFullPath())) {
            File[] files = new File(s.getFullPath()).listFiles();
            if (files.length > 0) {
                for (File f : files) {
                    BlobManager.uploadFileAsBlob(f.getPath());
                }
            }
        } else {
            BlobManager.uploadFileAsBlob(s.getFullPath());
        }
    }

    public static void main(String[] args) {
        try {
            Thread t = new Thread(new FolderWatcher());
            t.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        try {
            this.handleRequest();
            this.eventHandler();
        } catch (IOException ex) {
            Logger.getLogger(FolderWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
