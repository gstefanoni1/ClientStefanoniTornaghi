import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MultiClient {
    static final int CLIENT_TOT = 30;
    void exec() {
        Registry reg;
        try {
            reg = LocateRegistry.getRegistry();
            RisorsaServer server = (RisorsaServer) reg.lookup("DEPOSITO");
            int i = 0;
            RisorsaClientImpl.clientRunning = 0;
            RisorsaClientImpl.clientWaitingAggiunte = 0;
            RisorsaClientImpl.clientWaitingPrelievi = 0;
            while (i < CLIENT_TOT) {
                new RisorsaClientImpl(i++, server, CLIENT_TOT).start();
            }
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Connessione al server fallita");
        }
    }
    public static void main(String[] args) {
        MultiClient mc = new MultiClient();
        mc.exec();
    }
}