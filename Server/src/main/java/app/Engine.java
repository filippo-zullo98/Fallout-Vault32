package app;
import app.RESTconfig.FalloutStartGameApi;
import app.impl.FalloutGame;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;

import org.glassfish.jersey.server.ResourceConfig;




public class Engine {

    public static final String URL_SERVER = "http://localhost/";
    private final int PORT_SERVER = 4321;
    public static final int NUM_MAX_GAME = 4;
    private HttpServer server;
    private JDBC db;

    /**
     * contiene la lista di tutti i thread avviati per gestire ogni singola istanza
     */
    private static ArrayList<Thread> games= new ArrayList<>();
    /**
     * contiene l'ultima porta usata da un server avviato, utile quando bisogna avviare un nuovo server
     */
    private static int lastPortUsed = 4321;



    public Engine(){
        this.db = JDBC.getDbInstance();
    }

    /**
     * questa funzione ha il compito di avviare un thread per gestire una nuova partita con un thread e server REST nuovo
     * se sono presenti più di numParallelGames la nuova partita non viene avviata, altrimenti la partita viene avviata
     * con punto di inizio sulla porta passata in input, il riferimento al thread viene salvato
     *
     * @param idRoom --> indica l'id della room dalla quale far partire il gioco
     *                  la room è dinamica perchè la partita può essere salvata e ricaricata
     *                  successivamente e di conseguenza non si riparte più dalla room iniziale
     * @return intero --> se -1 la nuova partita non può essere avviata
     *                  se != -1 contiene la porta sulla quale è stato avviato il nuovo server che è in attesa
     */
    public int startNewGame(int idRoom,int idMetch){
        int port = -1;
        if ( games.size() < NUM_MAX_GAME ){
            try {
                port =  lastPortUsed+1; //viene incrementata il numero dell'ultima porta usata
                Thread newGame = new Thread(new FalloutGame(idRoom, port, db.getInventario(idMetch), db.getUnLockRoom(idMetch)));
                games.add(newGame);
                newGame.start();    //avviato il nuovo thread
            }catch (Exception e){
                System.err.println(e.getMessage());
                return port;
            }
            lastPortUsed +=1;
        }else{
            System.err.println(games.size()+" numero max di partite in corso raggiunto");
        }
        return port;
    }

    /**
     * questa funzione ha il compito di avviare il server sulla porta specificata, usando come classe di configurazione del server
     * FalloutStartGameApi --> essa prende in input l'engine corrente che li sarò utile per il collegamento al server e avviare nuove partite
     * @throws IOException --> server.start() puà generare eccezioni di questo tipo
     */
    private void startServer() throws IOException {
        try {
            URI baseUri = UriBuilder.fromUri(URL_SERVER).port(PORT_SERVER).build();
            FalloutStartGameApi falloutStartGameApi = new FalloutStartGameApi(this);
            ResourceConfig config = new ResourceConfig()
                    .register(falloutStartGameApi)
                    .packages("app");
            server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
            server.start();
            System.in.read();
            server.shutdown();
        }catch (Exception e){
            System.out.println("Errore nell'avvio del server: " + e);
        }

    }


    /**
     * Questa funzione si occupa di creare un istanza di Engine e di avviare successivamente un
     * server sulla quale riceveremo SOLO LE RICHIESTE X INIZIARE UNA NUOVA PARTITA O CHIEDERE LE PARTITE SALVATE
     */
    public static void main(String[] args){
        Engine engine = new Engine(); //creazione di un istanza di Engine
        try {
            //avviamo il server sulla quale andremo a ricevere le richieste per avviare una nuova partita
            engine.startServer(); //avvio il server principale
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }


}

