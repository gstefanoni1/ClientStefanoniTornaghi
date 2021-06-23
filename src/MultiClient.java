import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MultiClient {
    static final int CLIENT_TOT = 5;
    void exec() {
        Registry reg;
        try {
            reg = LocateRegistry.getRegistry();
            ResourceServer server = (ResourceServer) reg.lookup("RESOURCE");
            int i = 0;
            while (i < CLIENT_TOT) {
                new ResourceClientImpl(i++, server).start();
            }
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Connessione al server fallita");
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        MultiClient mc=new MultiClient();
        mc.exec();
    }
}