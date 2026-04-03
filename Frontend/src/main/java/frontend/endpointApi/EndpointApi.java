package frontend.endpointApi;

import frontend.Parser;
import org.json.JSONObject;

import javax.swing.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class EndpointApi {
    private final static String ENDPOINT_START_GAME = "http://localhost:4321/startgame";
    private final static String ENDPOINT_SAVED_METCH = "http://localhost:4321/startgame/metchsaved";
    private final static String PROTOCOL = "http://localhost:";
    private final static String PATH_DIRECTIONAL_BOTTON = "/api/direzione";
    private final static String PATH_PARSE_COMMAND = "/api/parsercommand?command=";
    private final static String PATH_GET_SAVES = "/startgame/metchsaved";
    private final static String PATH_GET_INVENTARIO = "/api/inventario/";


    private static Response makeRequest(String endpoint){
        try{
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(endpoint);
            return target.request().get();

        }
        catch (ProcessingException pe){
            // Eccezione di comunicazione con il server
            pe.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server\n" + pe.getMessage(), "Errore di Connessione" , JOptionPane.ERROR_MESSAGE);
            return null;
        }
        catch(IllegalArgumentException iae){
            // Eccezione di argomento non valido
            iae.printStackTrace();
            JOptionPane.showMessageDialog(null, "Argomento non valido\n" + iae.getMessage(), "Errore di Argomento" , JOptionPane.ERROR_MESSAGE);
            return null;
        }
        catch(Exception e){
            // Eccezione generica
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore");
            return null;
        }
    }

    public static int startNewGame(int idMetch){
        Response respJson = makeRequest(ENDPOINT_START_GAME +"?idmetch="+ idMetch);
        assert respJson != null;
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed.getInt("port");
    }

    public static int startNewGame(){
        Response respJson = makeRequest(ENDPOINT_START_GAME);
        assert respJson != null;
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed.getInt("port");
    }

    public static JSONObject getListMetchSaved(){
        Response respJson = makeRequest(ENDPOINT_SAVED_METCH);
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed;
    }

    public static JSONObject getCurrentRoom(int port){
        Response respJson = makeRequest(PROTOCOL + port + "/api/getcurrentroom");
        if (respJson == null){
            return null;
        }
        return Parser.parserResponse(respJson);
    }

    public static JSONObject sendParser(int port, String text){
        // Codifica il parametro
        String encodedCommand = URLEncoder.encode(text, StandardCharsets.UTF_8);
        Response respJson = makeRequest(PROTOCOL + port + PATH_PARSE_COMMAND + encodedCommand);
        assert respJson != null;
        return Parser.parserResponse(respJson);
    }

    public static JSONObject saveMetch(int port, int index,String nameMetch){
        Response respJson =  makeRequest(PROTOCOL + port + "/api/savemetch?idmetch=" + index + "&namemetch=" + nameMetch);
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed;
    }
    public static JSONObject sendRequestDirection(String directions, int port){
        Response respJson =   makeRequest(PROTOCOL + port + PATH_DIRECTIONAL_BOTTON + directions );
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed;
    }

    public static JSONObject getInventario(int port){
        Response respJson =   makeRequest(PROTOCOL + port + PATH_GET_INVENTARIO);
        assert respJson != null;
        JSONObject request_parsed = Parser.parserResponse(respJson);
        assert request_parsed != null;
        return request_parsed;
    }

}