package BlobManager;

import Global.Connection;
import com.microsoft.windowsazure.services.blob.client.CloudBlob;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.core.storage.StorageException;
import folderwatcher.FileFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlobManager {
    //add the username to this string instead of default

    private static String containerName = "default"; //User.getUsername();
    private static String url = "https://portalvhdsh8ghz0s9b7mx9.blob.core.windows.net/" + containerName + "/";

    //remember to set container name back to user if you have to change it for any reason
    public static void setContainerName(String newContainerName) {
        containerName = newContainerName;
    }

    public synchronized static void createContainter(String containerName) {
        containerName = containerName.toLowerCase();
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            container.createIfNotExist();
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static void uploadFileAsBlob(String fullPath) {
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            // System.out.println("The relative path is " + FileFunctions.getRelativePath(fullPath));
            CloudBlockBlob blob = container.getBlockBlobReference(FileFunctions.getRelativePath(fullPath));
            File source = new File(fullPath);
            FileInputStream fis = new FileInputStream(source);
            HashMap<String, String> meta = new HashMap<String, String>();
            meta.put("dateModified", FileFunctions.convertTimeToUTC(FileFunctions.convertTimestampToDate(source.lastModified())));            
            blob.setMetadata(meta);
            blob.upload(fis, source.length());
            fis.close();
        } catch (URISyntaxException | InvalidKeyException | StorageException | IOException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<String> getBlobsList() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            for (ListBlobItem blobItem : container.listBlobs("", true, null, null, null)) {
                System.out.println(blobItem.getUri().toString().substring(url.length()));
                list.add(blobItem.getUri().toString().substring(url.length()));
            }
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(url);
        return list;
    }

    public synchronized static void downloadAllBlobs() {
        String filePath = Connection.filePath;
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            for (ListBlobItem blobItem : container.listBlobs("", true, null, null, null)) {
                if (blobItem.getUri().toString().length() > 0) {
                    CloudBlob blob = (CloudBlob) blobItem;
                    blob.downloadAttributes();
                    // System.out.println(filePath + blob.getName());
                    File yourFile = new File(filePath + blob.getName());
                    if (!yourFile.exists()) {
                        yourFile.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(filePath + blob.getName());
                    HashMap<String, String> meta = new HashMap<String, String>(); 
                           meta =  blob.getMetadata();
                    
                    System.out.println("The size of teh meta is " + meta.size());
                    if(yourFile.exists()) {
                        System.out.println("File does exist");
                        if(FileFunctions.checkIfDownload(yourFile, new Date(meta.get("dateModified")))) {
                        blob.download(fos); 
                        }                       
                    }
                    else { 
                        System.out.println("File last modified in " +FileFunctions.convertTimestampToDate(yourFile.lastModified()));
                         blob.download(fos);
                    }
                    fos.close();
                }
            }
        } catch (URISyntaxException | InvalidKeyException | StorageException | IOException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void downloadBlob(String blobUri) {
        String filePath = Connection.filePath;
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            CloudBlob blob = container.getBlockBlobReference(blobUri);
            blob.downloadAttributes();
            File yourFile = new File(filePath + blob.getName());
            if (!yourFile.exists()) {
                yourFile.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(blob.getName());
            blob.download(fos);
            fos.close();
        } catch (URISyntaxException | InvalidKeyException | StorageException | IOException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static void deleteBlob(String fullPath) {
        String blobName = FileFunctions.getRelativePath(fullPath);
        blobName = blobName.replace("\\", "/");
        System.out.println("Blob is " + blobName);
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            for (ListBlobItem blobItem : container.listBlobs(blobName, true, null, null, null)) {

                CloudBlob blob = (CloudBlob) blobItem;
                System.out.println("Blob name is " + blob.getName());
                blob.delete();
            }
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteBlobContainer() {
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);

            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            container.delete();
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static void renameBlob(String newName, String oldName) {
        File file = new File(oldName);
        if (file.isDirectory()) {
            System.out.println("Blob is a directory");
            renameBlobDir(oldName, newName);
        }

        if (file.isFile()) {
            System.out.println("Blob is a file");
            renameSingleBlob(oldName, newName);
        }

    }

    private synchronized static void renameSingleBlob(String oldName, String newName) {
        oldName = FileFunctions.getRelativePath(oldName);
        System.out.println("The oldname is " + oldName);
        newName = FileFunctions.getRelativePath(newName);
        System.out.println("The new name is " + newName);
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            System.out.println("The old path is " + url + oldName + " and the new path is " + url + newName);
            CloudBlob oldBlob = blobClient.getBlockBlobReference(url + oldName);
            CloudBlob newBlob = container.getBlockBlobReference(url + newName);
            newBlob.copyFromBlob(oldBlob);
            oldBlob.delete();
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized static void renameBlobDir(String oldName, String newName) {
        oldName = FileFunctions.getRelativePath(oldName);
        System.out.println("The oldname is " + oldName);
        newName = FileFunctions.getRelativePath(newName);
        System.out.println("The new name is " + newName);
        try {
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(Connection.storageConnectionString);

            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            for (ListBlobItem blobItem : container.listBlobs(oldName, true, null, null, null)) {
                CloudBlob blob = (CloudBlob) blobItem;
                String oName = blob.getName();
                String nName = newName + oName.substring(oldName.length());
                System.out.println("New name is " + nName);
                CloudBlob newBlob = container.getBlockBlobReference(nName);
                CloudBlob oldBlob = container.getBlockBlobReference(oName);
                System.out.println("The blob names are " + blob.getName());
                newBlob.copyFromBlob(oldBlob);
                oldBlob.delete();
            }
        } catch (URISyntaxException | InvalidKeyException | StorageException ex) {
            Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        BlobManager.uploadFileAsBlob("C:\\Watcher\\hello.txt");
        //BlobManager.uploadFileAsBlob("C:\\Watcher\\myname2\\hithere.txt");
        //BlobManager.uploadFileAsBlob("C:\\Watcher\\myname2\\pp.txt");
        //BlobManager.getBlobsList();
        //BlobManager.downloadAllBlobs("default");
        //BlobManager.renameBlob("C:/Watcher/myname3", "C:/Watcher/myname2");

        //BlobManager.re
    }
}
