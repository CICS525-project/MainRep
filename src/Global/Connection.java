package Global;

import Server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Connection {

	public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
			+ "AccountName=portalvhdsh8ghz0s9b7mx9;"
			+ "AccountKey=ThVIUXcwpsYqcx08mtIhRf6+XxvEuimK35/M65X+XlkdVCQNl4ViUiB/+tz/nq+eeZAEZCNrmFVQwwN3QiykvA==";

	public static String filePath = "C:/Watcher/"; //System.getProperty("user.home") + "/NSync/";

	public static boolean watchFolder = true;

	public static int serverPort = 9006;

	public static int serverId = getRandomServer();

	public static ServerInterface server;

	private static int getRandomServer() {
		Random rand = new Random();
		int n = rand.nextInt(3) + 1;
		return n;
	}

	public static ArrayList<Integer> getOtherServerIds() {
		ArrayList<Integer> serverIds = new ArrayList<Integer>();
		for (int i = 1; i <= 3; i++) {
			if (i != serverId)
				serverIds.add(i);
		}
		return serverIds;
	}

	public static Map<String, String> getServerConnectionParams(int serverId) {

		Map<String, String> connParams = new HashMap<String, String>();
		if (serverId == 1) {
			String storageConnectionString = "DefaultEndpointsProtocol=https;"
					+ "AccountName=portalvhdsh8ghz0s9b7mx9;"
					+ "AccountKey=ThVIUXcwpsYqcx08mtIhRf6+XxvEuimK35/M65X+XlkdVCQNl4ViUiB/+tz/nq+eeZAEZCNrmFVQwwN3QiykvA==";
			String dbConnectionString = "jdbc:sqlserver://ah0sncq8yf.database.windows.net:1433"
					+ ";"
					+ "database=db_like"
					+ ";"
					+ "user=MySQLAdmin@ah0sncq8yf" + ";" + "password=almeta%6y";
			connParams.put("storageConnectionString", storageConnectionString);
			connParams.put("dbConnectionString", dbConnectionString);
			connParams.put("serverIP", "138.91.113.97");
		}

		// other if statements for the other 2 servers

		return connParams;
	}

	public static boolean isServerUp() {
		Map<String, String> connParams = getServerConnectionParams(serverId);
		try {
			System.setProperty("java.rmi.server.hostname",
					connParams.get("serverIP"));
			Registry registry = LocateRegistry.getRegistry(
					connParams.get("serverIP"), serverPort);
			server = (ServerInterface) registry.lookup("ServerInterfaceImpl");
			return true;
		} catch (NotBoundException | RemoteException e) {
			// e.printStackTrace();
			serverId = getOtherServerIds().get(0);
			connParams = getServerConnectionParams(serverId);
			try {
				System.setProperty("java.rmi.server.hostname",
						connParams.get("serverIP"));
				Registry registry = LocateRegistry.getRegistry(
						connParams.get("serverIP"), serverPort);
				server = (ServerInterface) registry
						.lookup("ServerInterfaceImpl");
				return true;
			} catch (NotBoundException | RemoteException e2) {
				// e.printStackTrace();
				serverId = getOtherServerIds().get(1);
				connParams = getServerConnectionParams(serverId);
				try {
					System.setProperty("java.rmi.server.hostname",
							connParams.get("serverIP"));
					Registry registry = LocateRegistry.getRegistry(
							connParams.get("serverIP"), serverPort);
					server = (ServerInterface) registry
							.lookup("ServerInterfaceImpl");
					return true;
				} catch (NotBoundException | RemoteException e3) {
					return false;
				}
			}
		}
		// return false;
	}

}
