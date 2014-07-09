package DB;

public class DBLikeFileObject {
	
	
    private String local_file_path="";
    private java.sql.Timestamp last_local_update=null;
	private java.sql.Timestamp last_server_update=null;
	private int user=0;
	

	
	public DBLikeFileObject(String l_f_p, java.sql.Timestamp l_l_u, int local_user)
	{
		local_file_path = l_f_p;
		last_server_update = null;
		last_local_update = l_l_u;
		user=local_user;
		
	}
	
	
	public void setServerUpdateDate(java.sql.Timestamp l_l_u)
	{
	  last_server_update=l_l_u;
	}
	
	public String getLocalFilePath()
	{
		return this.local_file_path;
	}
	
	public java.sql.Timestamp getLastLocalUpdate()
	{
		return this.last_local_update;
	}
	
	public java.sql.Timestamp getLastServerUpdate()
	{
		return this.last_server_update;
	}
	
	public int getUser()
	{
		return this.user;
	}

	
}
