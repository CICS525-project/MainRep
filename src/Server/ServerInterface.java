package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import BlobManager.BlobProperties;
import Global.User;

/**
 *
 * @author welcome
 */
public interface ServerInterface extends Remote {
    
	public boolean authenticateUser(User u);
	
	public void blobAdded(BlobProperties b);
	
	public void blobDeleted(BlobProperties b);
	
	public void replicateBlobChange(String command, BlobProperties b);
	
	public double findScore(String name) throws RemoteException;    
   
    
    public String copyBlob() throws RemoteException;
    
}
