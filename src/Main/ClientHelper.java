package Main;

import BlobManager.BlobManager;
import Global.Connection;
import Global.TrayIconBasic;
import Global.User;
import Server.QueueManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHelper {

	public static void initializeClient() {
		// create default directory where the program would store info
		File dir = new File(Connection.filePath);
		dir.mkdir();
		new TrayIconBasic();
		User.setUsername("democontainer");
		User.setUserId("1");
		User.setDirectory(Connection.filePath);
		System.out.println("User container is " + User.getUsername());
		syncFilesAtStartUp(null);
		createQueue();
		// continuallySyncFiles(null);
	}

	public static User performAuthentication() {
		// check if this has been done before if not ask user for credentials

		// if new user create container
		return null;
	}

	public static int connectToServer(User u) {

		return 0;
	}

	public static void syncFilesAtStartUp(User u) {
		// run the file syncing in a thread

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
					Logger.getLogger(ClientHelper.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				while (true) {
					BlobManager.downloadAllBlobs();
					try {
						System.out.println("Continually sync thread started");
						Thread.sleep(600000);
					} catch (InterruptedException ex) {
						Logger.getLogger(ClientHelper.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}
			}
		});
		initClient.start();
		System.out.println("Continually sync thread started");
	}

	public static void maintainConnection() {
		// constantly tell the server you are around
		new Runnable() {
			public void run() {
				// add the socket code to communicate with server here

				Process p1 = null;
				try {
					p1 = java.lang.Runtime.getRuntime().exec(
							"ping www.google.com");
					System.out.println(p1.waitFor());
				} catch (IOException ex) {
					Logger.getLogger(ClientHelper.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (InterruptedException ex) {
					Logger.getLogger(ClientHelper.class.getName()).log(
							Level.SEVERE, null, ex);
				}

				// return code for p1 will be 0 if internet is connected, else
				// it will be 1
			}
		};
	}

	public static void createQueue() {
		User.setQueueName(User.getUsername() + new Date().getTime());
		QueueManager.createQueue(User.getQueueName());
	}

	/*
	 * used to check if the login frame should be displayed or not
	 */
	public static Map<String, String> loggedInBefore() throws IOException {

		Map<String, String> dbParams = new HashMap<String, String>();
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/Settings/settings.txt"));

		String line;
		String[] l = new String[2];

		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.length() > 4) {
				//l = line.
				//dbP
			}
		}
		reader.close();
		return dbParams;
	}

	public static void main(String[] args) {
		try {
			Map<String, String> params = ClientHelper.loggedInBefore();
			System.out.println(params.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
