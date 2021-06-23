import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.rmi.NoSuchObjectException;

public class ResourceClientImpl extends Thread implements ResourceClient {
    ResourceServer server = null;
    int myId;
    boolean aspettaPrelievo = false;
    boolean aspettaAggiunta = false;
    int count = 0;
    Random rand = new Random();

    ResourceClientImpl(int i, ResourceServer server){
        this.server = server;
        myId = i;
    }
    public void notificaAggiunta(Risorsa r) {
        System.out.println("Ricevo notifica aggiunta risorsa: " + r.toString());
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
                    Data d = new Data("Info " + nInfo );
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    Risorsa r = new Risorsa(myId + " " + count, d, timestamp.toString());
                    server.aggiungiRisorsa(r, remoteCli);
                    aspettaAggiunta = true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if(!aspettaPrelievo){
                try {
                    remoteCli = (ResourceClient) UnicastRemoteObject.exportObject(this, 2033);
                    server.prelevaRisorsa(remoteCli);
                    aspettaPrelievo = true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            if(!aspettaPrelievo && !aspettaAggiunta){
                if(rand.nextInt(4) == 1){
                    System.out.println("Termina client: " + myId);
                    return;
                }
            }

        }
    }
}
