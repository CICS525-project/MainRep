package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.microsoft.sqlserver.jdbc.*;

public class DBManager {

	public DBManager() {
		super();
	}

	public ArrayList<DBLikeFileObject> localDB = new ArrayList<DBLikeFileObject>();

	public static void main(String[] args) {

		Connection con = establishConnection();
		//serverModify(con,"localpath/path/jjj.txt",getTimeStamp(today));
		//serverInsert(con,"localpath/path/jjj.txt",getTimeStamp(today));
		//serverDelete(con, "localpath/path/jjj.txt");
		
		 //localInsert("localpath/path/jjj.txt",getTimeStamp(today),3);
		 //localModify("localpath/path/jjj.txt",getTimeStamp(today),3);
		 //localModifyLastUpdate("localpath/path/jjj.txt");
		 //localDelete("localpath/path/ttt.txt");
	}

	public static Connection establishConnection() {

		// Connection string for your SQL Database server.
		String connectionString = "jdbc:sqlserver://ah0sncq8yf.database.windows.net:1433"
				+ ";"
				+ "database=db_like"
				+ ";"
				+ "user=MySQLAdmin@ah0sncq8yf"
				+ ";" + "password=almeta%6y";

		Connection connection = null; // For making the connections

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}

		catch (ClassNotFoundException cnfe) {

			System.out.println("ClassNotFoundException " + cnfe.getMessage());
		}

		try {

			connection = DriverManager.getConnection(connectionString);

		}

		catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		return connection;
	}

	public void updateServer(Connection connection,
			java.sql.Timestamp time_stamp, String local_path) {

		try {

			Statement stmt = connection.createStatement();

			if (inServerDB(connection, local_path))// already exists so should
													// update
			{
				PreparedStatement ps = connection
						.prepareStatement("UPDATE db_like_files SET time_stamp =?, path_local=?, path_server=?, user_id=? WHERE path_local=?");
				ps.setTimestamp(1, time_stamp);
				ps.setString(2, local_path);
				ps.setString(3, local_path);
				ps.setInt(4, 3);
				ps.setString(5, local_path);
				ps.executeUpdate();
			} else {
				PreparedStatement ps = connection
						.prepareStatement("INSERT INTO db_like_files (time_stamp, path_local, path_server, user_id) VALUES (?, ?, ?, ?)");
				ps.setTimestamp(1, time_stamp);
				ps.setString(2, local_path);
				ps.setString(3, local_path);
				ps.setInt(4, 3);
				ps.executeUpdate();
			}

			connection.commit();
			stmt.close();
			// Provide a message when processing is complete.
			System.out.println("Inserted Values.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serverInsert(Connection connection, String local_path,
			java.sql.Timestamp time_stamp) {

		try {

			Statement stmt = connection.createStatement();
			if (!inServerDB(connection, local_path))// already exists so should
													// update
			{
				PreparedStatement ps = connection
						.prepareStatement("INSERT INTO db_like_files (time_stamp, path_local, path_server, user_id) VALUES (?, ?, ?, ?)");
				ps.setTimestamp(1, time_stamp);
				ps.setString(2, local_path);
				ps.setString(3, local_path);
				ps.setInt(4, 3);
				ps.executeUpdate();
			}

			connection.commit();
			stmt.close();
			// Provide a message when processing is complete.
			System.out.println("Inserted Values.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serverModify(Connection connection, String local_path,
			java.sql.Timestamp time_stamp) {
		try {

			Statement stmt = connection.createStatement();

			if (inServerDB(connection, local_path))// already exists so should
													// update
			{
				PreparedStatement ps = connection
						.prepareStatement("UPDATE db_like_files SET time_stamp =?, path_local=?, path_server=?, user_id=? WHERE path_local=?");
				ps.setTimestamp(1, time_stamp);
				ps.setString(2, local_path);
				ps.setString(3, local_path);
				ps.setInt(4, 3);
				ps.setString(5, local_path);
				ps.executeUpdate();

			}

			connection.commit();
			stmt.close();
			// Provide a message when processing is complete.
			System.out.println("Inserted Values.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serverDelete(Connection connection, String local_path) {
		try {

			Statement stmt = connection.createStatement();

			if (inServerDB(connection, local_path))// already exists so should
													// update
			{
				PreparedStatement ps = connection
						.prepareStatement("DELETE db_like_files WHERE path_local=?");
				ps.setString(1, local_path);
				ps.executeUpdate();

			}

			connection.commit();
			stmt.close();
			// Provide a message when processing is complete.
			System.out.println("Inserted Values.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void localInsert(String local_path, java.sql.Timestamp local_update,
			int user) {
		DBLikeFileObject data = new DBLikeFileObject(local_path, local_update,
				user);
		localDB.add(data);

	}

	public void localModify(String local_path, java.sql.Timestamp local_update,
			int user) {
		DBLikeFileObject data = new DBLikeFileObject(local_path, local_update,
				user);

		DBLikeFileObject search = null;
		Iterator<DBLikeFileObject> itr = localDB.iterator();
		for (int i = 0; i < localDB.size(); i++) {
			search = itr.next();
			if (search.getLocalFilePath().equals(local_path)) {
				localDB.set(i, data);
				break;
			}

		}
	}

	public void localModifyLastUpdate(String local_path) {

		DBLikeFileObject search = null;
		Iterator<DBLikeFileObject> itr = localDB.iterator();
		for (int i = 0; i < localDB.size(); i++) {
			search = itr.next();

			if (search.getLocalFilePath().equals(local_path)) {
				search.setServerUpdateDate(search.getLastLocalUpdate());
				break;
			}

		}
	}

	public void localDelete(String local_path) {

		DBLikeFileObject search = null;
		Iterator<DBLikeFileObject> itr = localDB.iterator();
		while (itr.hasNext()) {
			search = itr.next();
			if (search.getLocalFilePath().equals(local_path)) {
				localDB.remove(search);
				break;
			}

		}

	}

	public boolean inServerDB(Connection con, String file_path) {
		boolean result = false;
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT * FROM db_like_files WHERE path_local = ?");
			ps.setString(1, file_path);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public int inLocalDB(String file_path) {
		int index = -1;
		DBLikeFileObject element = null;
		Iterator<DBLikeFileObject> itr = localDB.iterator();
		for (int i = 0; i < localDB.size(); i++) {
			element = itr.next();
			if (element.getLocalFilePath().equals(file_path)) {
				index = i;
			}

		}
		return index;
	}

	public java.sql.Timestamp getTimeStamp(Date d) {

		// java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(d.getTime());

	}
}
