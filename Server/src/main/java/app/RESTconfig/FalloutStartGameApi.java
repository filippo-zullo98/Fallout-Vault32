package app.RESTconfig;

import app.Engine;
import app.JDBC;
import app.impl.FalloutGame;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * questa classe serve a gestire tutte le REST api relative alla fase pre inizio di una nuova partita
 *
 */
@Path("startgame/")
public class FalloutStartGameApi {
    private final Engine engine;
    private final JDBC jdbc;

    public FalloutStartGameApi(Engine engine) throws Exception {
        this.engine = engine;
        this.jdbc = JDBC.getDbInstance();
        if (this.jdbc == null){
            throw new Exception("Database non disponibile");
        }
    }

    /**
     * la funzione serve ad avviare un nuovo thread che gestiraà un server REST con le api per gestire le richieste
     * sulla partita avviata
     *
     * @param idMatch --> indica l'id del metch dalla quale vogliamo iniziare nuovamente
     * @return Response.ok + json --> all'interno del json è resente la nuova porta sulla quale il client dovrà andare a fare le successive richieste
     */
    @GET
    @Produces("application/json")
    public Response initMatch(@DefaultValue("-1") @QueryParam("idmetch") int idMatch) throws SQLException {
        int idRoom = FalloutGame.INIT_DEFAULT_ROOM; //stanza dalla quale si parte nel caso in cui
        if (idMatch != -1){ //si vuole avviare una partita specifica
            /* se viene passato un idMatch, il client vuole avviare una partita salvata precedentemente e contrassegnata
            * con l'id univoco del metch
            */
            idRoom = this.jdbc.getLastRoomMatch(idMatch); // si ottiene la stanza in cui abbiamo effettuato il salvataggio per quel metch
            if (idRoom == -1){
                return Response.serverError().build();
            }
        }

        int port = engine.startNewGame(idRoom,idMatch);//ritorna la porta sulla quale sarà avviato il nuovo server
        if (port == -1){
            //se la port è -1 ci sono stati problemi nell'avvio della nuova partita
            // es. si è superato il num max di partite contemporanee
            return Response.serverError().build();
        }else{
            //si prepara un json contenente la porta sulla quale è stato avviato il nuovo server REST che risponderà ai comandi di gioco
            JSONObject jsonResp = new JSONObject().put("port", port);
            return Response.ok(jsonResp.toString(),MediaType.APPLICATION_JSON).build();
        }
    }


    /**
     * funzione che ha il compito di restituire una stringa in formato json contenente
     * tutte le info sulle partite salvate
     * @return Response.ok --> se non ci sono problemi
     * @throws SQLException --> eccezione sul db
     */
    @GET
    @Path("metchsaved/")
    @Produces("application/json")
    public Response getMatchSaved() throws SQLException {
        String res = this.jdbc.getMatchSaved();
        /*
         * res contiene idPartita, Nome, ultimaStanza --> questo per ogni partita salvata
         */
        return Response.ok(res,MediaType.APPLICATION_JSON).build();
    }
}
