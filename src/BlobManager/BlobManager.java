package BlobManager;

import Global.Connection;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobListingDetails;
import com.microsoft.azure.storage.blob.BlobType;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import folderwatcher.FileFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlobManager {
	// add the username to this string instead of default

	private static String containerName = "democontainer"; // User.getUsername();
	private static String url = "https://portalvhdsh8ghz0s9b7mx9.blob.core.windows.net/"
			+ containerName + "/";

	// remember to set container name back to user if you have to change it for
	// any reason
	public static void setContainerName(String newContainerName) {
		containerName = newContainerName;
	}

	public synchronized static void createContainter(String containerName) {
		containerName = containerName.toLowerCase();
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			container.createIfNotExists();
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void uploadFileAsBlob(String fullPath) {

		// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			// System.out.println("The relative path is " +
			// FileFunctions.getRelativePath(fullPath));
			CloudBlockBlob blob = container.getBlockBlobReference(FileFunctions
					.getRelativePath(fullPath));

			File source = new File(fullPath);
			if (source.exists()) {

				if (blob.exists()) {
					blob.downloadAttributes();

					if (new Date(blob.getMetadata().get("dateModified"))
							.after(new Date((FileFunctions
									.convertTimeToUTC(FileFunctions
											.convertTimestampToDate(source
													.lastModified())))))) {
						System.out.println(source.getName()
								+ " is up to date so it is not uploaded");
						return;
					}
				}

				fis = new FileInputStream(source);
				System.gc();
				HashMap<String, String> meta = new HashMap<String, String>();
				meta.put("dateModified", FileFunctions
						.convertTimeToUTC(FileFunctions
								.convertTimestampToDate(source.lastModified())));
				blob.setMetadata(meta);
				if (!source.isHidden()) {
					System.out.println(source.getName()
							+ " is not up to date so it is uploaded");
					blob.upload(fis, source.length());
				}
				fis.close();
			}
		} catch (URISyntaxException | InvalidKeyException | StorageException
				| IOException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static ArrayList<String> getBlobsList(String startsWith) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			EnumSet<BlobListingDetails> details = EnumSet
					.of(BlobListingDetails.METADATA);
			for (ListBlobItem blobItem : container.listBlobs(startsWith, true,
					details, null, null)) {
				System.out.println(blobItem.getUri().toString()
						.substring(url.length()));
				list.add(blobItem.getUri().toString().substring(url.length()));
			}
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		// System.out.println(url);
		return list;
	}

	public synchronized static void downloadAllBlobs() {
		Connection.watchFolder = false;
		String filePath = Connection.filePath;
		System.out.println("The filepath is " + filePath);
		FileOutputStream fos = null;
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			// System.out.println("Container blob size is " + );
			EnumSet<BlobListingDetails> details = EnumSet
					.of(BlobListingDetails.METADATA);
			for (ListBlobItem blobItem : container.listBlobs("", true, details,
					null, null)) {
				if (blobItem.getUri().toString().length() > 0) {
					CloudBlob blob = (CloudBlob) blobItem;
					blob.downloadAttributes();
					HashMap<String, String> meta = new HashMap<String, String>();
					meta = blob.getMetadata();
					// System.out.println(filePath + blob.getName());
					File yourFile = new File(filePath + blob.getName());
					if (!yourFile.exists()) {
						yourFile.getParentFile().mkdirs();
					}					

					// System.out.println("The size of the meta is " +
					// meta.size());
					if (yourFile.exists()) {
						System.out.println(yourFile.getName() + " does exist");
						if (FileFunctions.checkIfDownload(yourFile, new Date(
								meta.get("dateModified")))) {
							fos = new FileOutputStream(filePath
									+ blob.getName());
							System.gc();
							System.out.println(yourFile.getName()
									+ " has just been updated");
							blob.download(fos);
							fos.close();
						} else {
							System.out.println(yourFile.getName()
									+ " is up to date");
						}
					} else {
						System.out
								.println(yourFile.getName()
										+ "File does not exist. Downloading from server");
						fos = new FileOutputStream(filePath + blob.getName());
						System.gc();
						// System.out.println("File last modified in " +
						// FileFunctions.convertTimestampToDate(yourFile.lastModified()));
						blob.download(fos);
						fos.close();
					}
					if (yourFile.isFile())
						yourFile.setLastModified(FileFunctions
								.convertUTCToLocal(meta.get("dateModified"))
								.getTime());
				}
			}
		} catch (URISyntaxException | InvalidKeyException | StorageException
				| IOException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {			
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Connection.watchFolder = true;
	}

	public static void downloadBlob(String blobUri) {
		String filePath = Connection.filePath;
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			CloudBlob blob = container.getBlockBlobReference(blobUri);
			blob.downloadAttributes();
			File yourFile = new File(filePath + blob.getName());
			if (!yourFile.exists()) {
				yourFile.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(blob.getName());
			blob.download(fos);
			fos.close();
		} catch (URISyntaxException | InvalidKeyException | StorageException
				| IOException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public synchronized static void deleteBlob(String fullPath) {
		String blobName = FileFunctions.getRelativePath(fullPath);
		if (blobName.contains("\\")) {
			blobName = blobName.replace("\\", "/");
		}
		System.out.println("Blob is " + blobName);
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			EnumSet<BlobListingDetails> details = EnumSet
					.of(BlobListingDetails.METADATA);
			System.out.println("Blob name is " + blobName);
			for (ListBlobItem blobItem : container.listBlobs(blobName, true,
					details, null, null)) {

				CloudBlob blob = (CloudBlob) blobItem;
				System.out.println("Blob name found is " + blob.getName());
				blob.delete();
			}
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public synchronized static void deleteBlobContainer() {
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);

			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			container.delete();
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
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

	private static void renameSingleBlob(String oldName, String newName) {
		oldName = FileFunctions.getRelativePath(oldName);
		System.out.println("The oldname is " + oldName);
		newName = FileFunctions.getRelativePath(newName);
		System.out.println("The new name is " + newName);
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			System.out.println("The old path is " + url + oldName
					+ " and the new path is " + url + newName);
			CloudBlob oldBlob = container.getBlockBlobReference(url + oldName);
			CloudBlob newBlob = container.getBlockBlobReference(url + newName);
			newBlob.startCopyFromBlob(oldBlob);
			oldBlob.delete();
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private static void renameBlobDir(String oldName, String newName) {
		oldName = FileFunctions.getRelativePath(oldName);
		System.out.println("The oldname is " + oldName);
		newName = FileFunctions.getRelativePath(newName);
		System.out.println("The new name is " + newName);
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(Connection.storageConnectionString);

			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient
					.getContainerReference(containerName);
			EnumSet<BlobListingDetails> details = EnumSet
					.of(BlobListingDetails.METADATA);
			for (ListBlobItem blobItem : container.listBlobs(oldName, true,
					details, null, null)) {
				CloudBlob blob = (CloudBlob) blobItem;
				String oName = blob.getName();
				String nName = newName + oName.substring(oldName.length());
				System.out.println("New name is " + nName);
				CloudBlob newBlob = container.getBlockBlobReference(nName);
				CloudBlob oldBlob = container.getBlockBlobReference(oName);
				System.out.println("The blob names are " + blob.getName());
				newBlob.startCopyFromBlob(oldBlob);
				oldBlob.delete();
			}
		} catch (URISyntaxException | InvalidKeyException | StorageException ex) {
			Logger.getLogger(BlobManager.class.getName()).log(Level.SEVERE,
					null, ex);
			System.out.println("The message of the exception is "
					+ ex.getMessage());
		}
	}

	public static void main(String[] args) {
		// BlobManager.uploadFileAsBlob("C:\\Watcher\\hello.txt");
		// BlobManager.uploadFileAsBlob("C:\\Watcher\\myname2\\hithere.txt");
		// BlobManager.uploadFileAsBlob("C:\\Watcher\\myname2\\pp.txt");
		// BlobManager.getBlobsList();
		BlobManager.downloadAllBlobs();
		// BlobManager.renameBlob("C:/Watcher/myname3", "C:/Watcher/myname2");

		// BlobManager.deleteBlob("C:/Watcher/store2");
	}
}
