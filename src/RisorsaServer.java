import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RisorsaServer extends Remote {
    public void aggiungiRisorsa(Risorsa r, RisorsaClient c) throws RemoteException;
    public void prelevaRisorsa(RisorsaClient c) throws RemoteException;
}
