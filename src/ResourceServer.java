import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ResourceServer extends Remote {
    public void aggiungiRisorsa(Risorsa r, ResourceClient c) throws RemoteException;
    public void prelevaRisorsa(ResourceClient c) throws RemoteException;
}
