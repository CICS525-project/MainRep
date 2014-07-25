package Server;

import java.net.URISyntaxException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

public class ServerBlobManager {

	private static CloudStorageAccount storageAccountSource;
	private static CloudStorageAccount storageAccountDest;

	public static void copyBlob(String srcContainerName, String policyId,
			String destContainerName, String blobName, int server) {
		CloudBlobClient cloudBlobClients = null;
		CloudBlobClient cloudBlobClientd = null;
		try {
			storageAccountSource = new CloudStorageAccount(
					new StorageCredentialsAccountAndKey(
							"portalvhdsh8ghz0s9b7mx9",
							"ThVIUXcwpsYqcx08mtIhRf6+XxvEuimK35/M65X+XlkdVCQNl4ViUiB/+tz/nq+eeZAEZCNrmFVQwwN3QiykvA=="),
					true);
			storageAccountDest = new CloudStorageAccount(
					new StorageCredentialsAccountAndKey(
							"portalvhds1fyd5w3n1hnd6",
							"DPBb+Y6B2kJEG/yN3Sm3PUDdljXY7BYyIkmZD/NUZU0l3LnDu7qXMjnRRJJgQWAeZTFCUi/xJtiCTIPYKnvI+A=="),
					true);
			cloudBlobClients = storageAccountSource.createCloudBlobClient();
			cloudBlobClientd = storageAccountDest.createCloudBlobClient();

		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		CloudBlobContainer srcContainer = null;
		CloudBlobContainer destContainer = null;

		try {
			destContainer = cloudBlobClientd
					.getContainerReference(destContainerName);
			srcContainer = cloudBlobClients
					.getContainerReference(srcContainerName);

			openContainer(destContainer);
			openContainer(srcContainer);

		} catch (URISyntaxException | StorageException e1) {			
			e1.printStackTrace();
		}

		String blobToken = null;
		CloudBlob destBlob = null;
		CloudBlob sourceBlob = null;
		// get the SAS token to use for all blobs
		try {
			sourceBlob = srcContainer.getBlockBlobReference(blobName);			
			destBlob = destContainer.getBlockBlobReference(blobName);

			System.out.println(destBlob + " " + destBlob.getName());
			if (!destBlob.exists()) {
				System.out.println("Blob does not exist");
			}

			//System.out.println(sourceBlob.acquireLease(40, "ok", null, null, null));
			
			destBlob.startCopyFromBlob(sourceBlob);
			
			System.out.println(destBlob.getCopyState().getStatusDescription());

			closeContainer(srcContainer);
			closeContainer(destContainer);

		} catch (StorageException | URISyntaxException e) {			
			//e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void closeContainer(CloudBlobContainer e) {
		BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
		// Include public access in the permissions object.
		containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
		// Set the permissions on the container.
		try {
			e.uploadPermissions(containerPermissions);
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static void openContainer(CloudBlobContainer e) {
		BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
		// Include public access in the permissions object.
		containerPermissions
				.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
		// Set the permissions on the container.
		try {
			e.uploadPermissions(containerPermissions);
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) {
		copyBlob("democontainer", "Yanki", "democontainer", "group5.txt", 1);
		copyBlob("democontainer", "Yanki", "democontainer",
				"tinashe2/ChrisBrownFt.LilWayne&TooShort-Loyal.mp3", 1);
	}
}
