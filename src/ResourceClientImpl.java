import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.NoSuchObjectException;

public class ResourceClientImpl extends Thread implements ResourceClient {
    ResourceServer server = null;
    int myId;
    boolean aspettaPrelievo = false;
    boolean aspettaAggiunta = false;
    Random rand = new Random();

    ResourceClientImpl(int i, ResourceServer server){
        this.server = server;
        myId = i;
    }
    public void notificaAggiunta() {
        aspettaAggiunta = false;
    }
    public void notificaPrelievo(Risorsa r) {
        System.out.println("Prelevato:" + r.toString());
        aspettaPrelievo = false;
    }

    public void run(){
        ResourceClient remoteCli;

        while(true){
            if(!aspettaAggiunta){
                try {
                    remoteCli = (ResourceClient) UnicastRemoteObject.exportObject(this, 2033);
                    int nInfo = rand.nextInt(20);
                    Data d = Data("Info" +  );
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
