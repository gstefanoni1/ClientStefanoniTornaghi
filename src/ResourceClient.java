import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ResourceClient extends Remote {
    public void notificaAggiunta() throws RemoteException;
    public void notificaPrelievo(Risorsa r) throws RemoteException;
}

