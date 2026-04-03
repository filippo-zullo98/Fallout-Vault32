package frontend;

import org.json.JSONException; // importa l'eccezione JSON specifica
import org.json.JSONObject; // importa la classe JSONObject per la gestione dei dati JSON
import javax.ws.rs.ProcessingException; // Importa l'eccezione per errori di elaborazione
import javax.ws.rs.core.Response; // Importa la classe Response per gestire le risposte delle richieste

public class Parser {
    // Costante che rappresenta lo stato HTTP 200 OK, utilizzato per verificare l'accettazione della richiesta
    private final static int STATE_ACCEPTED = 200;

    /**
     * Metodo privato che converte la risposta HTTP in un oggetto JSONObject.
     * @param resp La risposta HTTP da convertire.
     * @return Un oggetto JSONObject se la conversione ha successo, altrimenti null.
     */
    private static JSONObject parseJsonString(Response resp) {
        try {
            // leggi l'entità della risposta come stringa e convertila in JSONObject
            return new JSONObject(resp.readEntity(String.class));
        } // fine del blocco try
        catch (JSONException e) {
            // Ritorna null se c'è un errore nella conversione della stringa in JSON
            return null;
        } // fine del blocco catch
    } // fine del metodo parseJsonString

    /**
     * Metodo pubblico che gestisce la risposta HTTP e la converte in un oggetto JSONObject.
     * @param resp La risposta HTTP da analizzare.
     * @return Un oggetto JSONObject se la risposta è accettata e la conversione ha successo, altrimenti null.
     */
    public static JSONObject parserResponse(Response resp){
        try {
            // Verifica se lo stato della risposta è 200
            if (resp.getStatus() == STATE_ACCEPTED) { // se getStatus restituisce uno stato accettante
                // Converte la risposta in JSONObject se lo stato è accettante
                return parseJsonString(resp);
            } // fine di if
        }// fine del blocco try
        catch (ProcessingException e) { // gestisci l'eccezione
            return null; // restituisci null al chiamante
        } // fine del blocco catch
        return null; // Ritorna null se lo stato della risposta non è 200 OK o se c'è un'eccezione
    } // fine del metodo parserResponse

} // fine della classe Parser