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

		// server DB update
		Connection con = establishConnection();
		// updateServer(con,getTimeStamp(today),"x.txt","localpath/path/x.txt","serverpath/path/x.txt");

		// local DB update
		// updateLocal("the.txt","path/local/",today,"its me");
		// DBLikeFileObject data = localDB.get(localDB.size()-1);
		// data.setServerUpdateDate(today);
		// System.out.println("The last server update was at "+data.getLastServerUpdate());

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
		// DatabaseMetaData md = null;
		// ResultSet resultSet = null; // For the result set, if applicable

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

	public void updateServer(Connection connection,java.sql.Timestamp time_stamp, String local_path) 
	{

		try {

			Statement stmt = connection.createStatement();

			if (inServerDB(connection, local_path))// already exists so should update
			{
				PreparedStatement ps = connection.prepareStatement("UPDATE db_like_files SET time_stamp =?, path_local=?, path_server=?, user_id=? WHERE path_local=?");
				ps.setTimestamp(1, time_stamp);
				ps.setString(2, local_path);
				ps.setString(3, local_path);
				ps.setInt(4, 3);
				ps.setString(5, local_path);
				ps.executeUpdate();
			} 
			else 
			{
				PreparedStatement ps = connection.prepareStatement("INSERT INTO db_like_files (time_stamp, path_local, path_server, user_id) VALUES (?, ?, ?, ?)");
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

	public void updateLocal(String file_name, String local_path, java.sql.Timestamp local_update, String user) 
	{
		DBLikeFileObject data = new DBLikeFileObject(local_path, local_update, user);

		if (inLocalDB(local_path) != -1) // has been previously added
		{
			localDB.set(inLocalDB(local_path), data);
			System.out.println("Added data locally");
		} 
		
		else // new entry
		{
			localDB.add(data);
			System.out.println("Added data locally");
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

	private static java.sql.Timestamp getTimeStamp(Date d) {
		return new java.sql.Timestamp(d.getTime());

	}

}
