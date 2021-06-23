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
    public static int clientRunning;
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
        System.out.println("[" + myId + "] Aggiunto: " + r.toString());
        aspettaAggiunta = false;
    }

    public void notificaPrelievo(Risorsa r) {
        System.out.println("[" + myId + "] Prelevato:" + r.toString());
        aspettaPrelievo = false;
    }

    private void aggiunta() {
        //Attesa
        synchronized (RisorsaClientImpl.class) {
            clientWaitingAggiunte++;
        }
        int cont = 0;
        while (aspettaAggiunta) {
            attesa(cont, "aggiunta");
            cont++;
        }
        synchronized (RisorsaClientImpl.class) {
            clientWaitingAggiunte--;
        }
        //Qua ho completato l'aggiunta precedente(nel caso ci fosse)
        try {
            //Generazione random
            Random rand = new Random();
            int nInfo = rand.nextInt(20);
            Data d = new Data("Info " + nInfo);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Risorsa r = new Risorsa(myId + "" + count++, d, timestamp.toString());
            //Lo inserisco nel server
            aspettaAggiunta = true;
            server.aggiungiRisorsa(r, remoteCli);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void prelievo() {
        //Attesa
        synchronized (RisorsaClientImpl.class) {
            clientWaitingPrelievi++;
        }
        int cont = 0;
        while (aspettaPrelievo) {
            attesa(cont, "prelievo");
            cont++;
        }
        synchronized (RisorsaClientImpl.class) {
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
            //L'operazione da eseguire viene scelta a random
            if (rand.nextBoolean()) {
                //Permette di cambiare scelta per evitare di avere
                //tutti i client congestionati
                boolean flagScelta = true;
                synchronized (RisorsaClientImpl.class) {
                    if (clientWaitingAggiunte == clientRunning - 1)
                        flagScelta = false;
                }
                if (flagScelta)
                    aggiunta();
                else
                    prelievo();
            } else {
                boolean flagScelta = true;
                synchronized (RisorsaClientImpl.class) {
                    if (clientWaitingPrelievi == clientRunning - 1)
                        flagScelta = false;
                }
                if (flagScelta)
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
                System.out.println("[" + myId + "] Termina");
                synchronized (RisorsaClientImpl.class) {
                    clientRunning--;
                }
                return;
            }
        }
    }

    private void attesa(int cont, String motivo){
        System.out.println("[" + myId + "] In attesa per " + motivo);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (cont == 10 && clientWaitingAggiunte + clientWaitingPrelievi == clientRunning) {
            bannerWait();
        }
    }
    private synchronized void bannerWait() {
        if (clientRunning == (clientWaitingPrelievi + clientWaitingAggiunte)) {
            System.out.println("TUTTI I CLIENT SONO IN WAITING");
            System.out.println("IMPOSSIBILE COMPLETARE LE TRANSAZIONI");
            System.exit(0);
        }
    }
}
