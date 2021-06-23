import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RisorsaClient extends Remote {
    public void notificaAggiunta(Risorsa r) throws RemoteException;
    public void notificaPrelievo(Risorsa r) throws RemoteException;
}

