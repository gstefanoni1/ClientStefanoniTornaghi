import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.rmi.NoSuchObjectException;

public class RisorsaClientImpl extends Thread implements RisorsaClient {
    RisorsaServer server = null;
    RisorsaClient remoteCli = null;
    int myId;
    boolean aspettaPrelievo = false;
    boolean aspettaAggiunta = false;
    int count = 0;
    public static int clientRunning = 0;
    public static int clientWaitingPrelievi;
    public static int clientWaitingAggiunte;
    int numeroClientTotali;

    RisorsaClientImpl(int i, RisorsaServer server, int numCl) {
        this.server = server;
        myId = i;
        numeroClientTotali = numCl;
        synchronized (RisorsaClientImpl.class) {
            clientRunning++;
        }
    }

    public void notificaAggiunta(Risorsa r) {
        System.out.println(myId + " Aggiunto: " + r.toString());
        aspettaAggiunta = false;
    }

    public void notificaPrelievo(Risorsa r) {
        System.out.println(myId + " Prelevato:" + r.toString());
        aspettaPrelievo = false;
    }

    private void aggiunta() {
        System.out.println(myId + " Voglio fare un aggiunta");
        System.out.println(myId + " posso fare l'aggiunta: " + !aspettaAggiunta);
        //Attesa
        if (aspettaAggiunta) synchronized (RisorsaClientImpl.class) {
            clientWaitingAggiunte++;
        }
        while (aspettaAggiunta) { }
        if (aspettaAggiunta) synchronized (RisorsaClientImpl.class) {
            clientWaitingAggiunte--;
        }
        //Qua ho completato l'aggiunta precedente
        try {
            //Generazione random
            Random rand = new Random();
            int nInfo = rand.nextInt(20);
            Data d = new Data("Info " + nInfo);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Risorsa r = new Risorsa(myId + " " + count++, d, timestamp.toString());
            //Lo inserisco nel server
            aspettaAggiunta = true;
            server.aggiungiRisorsa(r, remoteCli);
        } catch (RemoteException e) {
            System.out.println("ERRORE ID: " + myId);
            e.printStackTrace();
        }
    }

    public void prelievo() {
        System.out.println(myId + " Voglio fare un prelievo");
        System.out.println(myId + " posso fare il prelievo: " + !aspettaPrelievo);
        //Attesa
        if (aspettaPrelievo) synchronized (RisorsaClientImpl.class) {
            clientWaitingPrelievi++;
        }
        while (aspettaPrelievo) { }
        if (aspettaPrelievo) synchronized (RisorsaClientImpl.class) {
            clientWaitingPrelievi--;
        }
        //Fine attesa
        try {
            aspettaPrelievo = true;
            server.prelevaRisorsa(remoteCli);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            remoteCli = (RisorsaClient) UnicastRemoteObject.exportObject(this, 2033);
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            Random rand = new Random();
            if (rand.nextBoolean()) {
                boolean f = true;
                synchronized (RisorsaClientImpl.class) {
                    if (clientWaitingAggiunte == clientRunning - 1)
                        f = false;
                }
                if (f)
                    aggiunta();
                else
                    prelievo();
            } else {
                boolean f = true;
                synchronized (RisorsaClientImpl.class) {
                    if (clientWaitingPrelievi == clientRunning - 1)
                        f = false;
                }
                if (f)
                    prelievo();
                else
                    aggiunta();
            }
            //Decido se terminare o no
            if (!aspettaPrelievo && !aspettaAggiunta && rand.nextInt(4) == 1) {
                try {
                    UnicastRemoteObject.unexportObject(this, true);
                } catch (NoSuchObjectException e) {
                    e.printStackTrace();
                }
                System.out.println("Termina client: " + myId);
                synchronized (RisorsaClientImpl.class) {
                    clientRunning--;
                }
                return;
            }
        }
    }


    private synchronized void bannerWait(){
        if (clientRunning == (clientWaitingPrelievi + clientWaitingAggiunte)) {
            System.out.println("TUTTI I CLIENT SONO IN WAITING");
            System.exit(0);
        }
    }
}
