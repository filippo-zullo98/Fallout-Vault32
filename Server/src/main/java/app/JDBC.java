package app;

import app.type.AdvObject;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * questa classe si occupa della gestione del DB e delle query fatte ad esso
 */
public class JDBC {
    private static final String CREATE_TABELLA_UTENTE = "CREATE TABLE IF NOT EXISTS metch (IdPartita INT PRIMARY KEY, Nome VARCHAR(30), IdUltimaStanza  INT, NomeUltimaStanza VARCHAR(30))";
    private static final String CREATE_TABLE_INVENTARIO = "CREATE TABLE IF NOT EXISTS inventario (id INT AUTO_INCREMENT PRIMARY KEY, idpartita INT, idobject INT)";
    private static final String CREATE_TABLE_UNLOCKROOM = "CREATE TABLE IF NOT EXISTS unlockroom (id INT AUTO_INCREMENT PRIMARY KEY, idpartita INT, idroom INT)";

    private static final String DELETE_INVENTARIO = "DELETE FROM INVENTARIO WHERE IDPARTITA =?";
    private static final String INSERT_INVENTARIO = "INSERT INTO inventario (idpartita , idobject) VALUES (?, ?)";

    private static final String DELETE_UNLOCK = "DELETE FROM UNLOCKROOM WHERE IDPARTITA =?";
    private static final String INSERT_UNLOCK = "INSERT INTO UNLOCKROOM (idpartita , idroom) VALUES (?, ?)";

    private static final String UPDATE_METCH = "UPDATE METCH SET NomeUltimaStanza= ?, IDULTIMASTANZA = ?, NOME = ?    WHERE IDPARTITA  = ?";
    private static final String INSERT_METCH = "INSERT INTO METCH (NomeUltimaStanza,IDULTIMASTANZA , NOME, IDPARTITA) VALUES (?, ?, ?,?)";

    private final Connection connection;
    private Statement stm;

    /**
     * attributi utili per il collegamento con il server
     */
    private final String usernameDB = "ph1l";
    private final String passwordDB = "D3v1l98";
    private final String urlDB = "jdbc:h2:tcp://localhost/~/test";
    private static JDBC instanceJdbc = null;

    /**
     * Esso crea la connessione con il db e tenta di creare la tabelle se non sono già presenti
     */
    private JDBC() throws SQLException {
        Properties dbprops = new Properties();
        dbprops.setProperty("user", usernameDB);
        dbprops.setProperty("password", passwordDB);
        connection = DriverManager.getConnection(urlDB,dbprops);
        stm = connection.createStatement();
        createTableUser();  // tenta di creare la tabella se essa non esiste già
        createTableInventario();    // tenta di creare la tabella se essa non esiste già
        createTableUnlockroom();    // tenta di creare la tabella se essa non esiste già
    }

    /**
     * La classe JDBC è una singleton quindi questa funzione ha lo scopo di ritornare l'istanza del db
     * @return ritorna l'istanza univoca del database
     */
    public static JDBC getDbInstance(){
        if (instanceJdbc == null){
            try {
                instanceJdbc = new JDBC();
            }catch (Exception e){
                return null;
            }
        }
        return instanceJdbc;
    }

    /**
     *
     * @throws SQLException --> eccezione che può essere sollevata nel momento in cui si effettua una query
     */
    private void createTableUser() throws SQLException {
        stm.executeUpdate(CREATE_TABELLA_UTENTE);
    }
    private void createTableInventario() throws SQLException {
        stm.executeUpdate(CREATE_TABLE_INVENTARIO);
    }
    private void createTableUnlockroom() throws SQLException {
        stm.executeUpdate(CREATE_TABLE_UNLOCKROOM);
    }

    /**
     * essa si occupa di effettuare una query al server ed ottiene tutte le partite salvate e le inserisce in un json file
     *
     * @throws SQLException --> essa può essere generata durante la query select
     * @return String --> essa contiene un json in formato stringa contenente tutti i dati estratti
     *  dal database
     */
    public String getMatchSaved() throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM metch");
        ResultSet rs = pstm.executeQuery();

        JSONObject jsonMathSaved = new JSONObject();
        while (rs.next()) {
            JSONObject jsonSingleMetch = new JSONObject();
            jsonSingleMetch.put("Nome", rs.getString("Nome"));
            jsonSingleMetch.put("IdUltimaStanza", rs.getString("IdUltimaStanza"));
            jsonSingleMetch.put("NomeUltimaStanza", rs.getString("NomeUltimaStanza"));

            jsonMathSaved.put(String.valueOf(rs.getInt("IdPartita")), jsonSingleMetch.toString());
        }
        return jsonMathSaved.toString();
    }

    /**
     * Essa ritorna l'id dell'ultima stanza di una specifica partita
     * @param idMatch --> indica il metch
     * @return int --> se == -1 il metch non è salvato all'interno del database
     *                  se != -1 viene indicato il numero che identifica l'id della stanza in cui è stata salvata
     *                  la partita
     * @throws SQLException --> eccezione che può essere sollevata nel momento in cui si effettua la SELECT
     */
    public int getLastRoomMatch(int idMatch) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT idultimastanza FROM metch WHERE idpartita=?");
        pstm.setInt(1, idMatch);
        ResultSet rs = pstm.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);

        }
        return -1;
    }

    /**
     * Lista degli elemeneti dell'inventario di un match salvato
     * @param idMatch --> indica il metch
     */
    public List<Integer> getInventario(int idMatch) throws SQLException {
        return getFromDb("SELECT idobject FROM inventario WHERE idpartita=?", idMatch);
    }

    /**
     * Lista delle stanze sbloccate di uno specifico match
     * @param idMatch --> indica il metch
     */
    public List<Integer> getUnLockRoom(int idMatch) throws SQLException {
        return getFromDb("SELECT idroom FROM UNLOCKROOM WHERE idpartita=?", idMatch);
    }

    /**
     * Funzione che ha lo scopo di effettuare le query
     * @param query --> stringa della query
     * @param idMatch --> id match
     */
    private List<Integer> getFromDb(String query, int idMatch) throws SQLException {
        List<Integer> listId = new ArrayList<>();
        PreparedStatement pstm = connection.prepareStatement(query);
        pstm.setInt(1, idMatch);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            listId.add(rs.getInt(1));
        }
        return listId;
    }

    /**
     * Funzione con lo scopo di aggiornare la lista delle stanze bloccate di un match
     * @param idMatch --> id match
     * @param unLockRoom --> lista delle stanza sbloccate
     */
    private void updateUnLockRoom(int idMatch, List<Integer> unLockRoom){
        try {
            executeDelete(DELETE_UNLOCK, idMatch); // elimina i dati dello specifico match
            executeUpdate(INSERT_UNLOCK, unLockRoom, idMatch); // inserisce i nuovi dati

        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    /**
     * Funzione con lo scopo di aggiornare la lista dell'inventario di un match
     * @param idMatch --> id match
     * @param inventario --> lista dell'inventario
     */
    private void updateInventario(int idMatch, List<AdvObject> inventario){
        try {
            executeDelete(DELETE_INVENTARIO, idMatch);
            executeUpdate(INSERT_INVENTARIO, inventario, idMatch);
        }catch (Exception e){
            System.out.println(e);
        }


    }

    /**
     * Esegue l'update con una lista di elementi
     * @param query --> query che effettua l'aggiornamento
     * @param lista --> lista degli elementi da inserire nel db
     * @param idMatch --> id del match da aggiornare
     */
    public <T> void executeUpdate(String query, List<T> lista, int idMatch) throws SQLException {
        for (Object object : lista){
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setInt(1, idMatch);
            if (object instanceof AdvObject)
                pstm.setInt(2,  ((AdvObject) object).getId());
            if (object instanceof Integer)
                pstm.setInt(2, (Integer) object);

            pstm.executeUpdate();
        }
    }

    /**
     * Elimina tutti gli elementi di un match con una specifica query
     * @param query --> query
     * @param idMatch --> id del match
     */
    public void executeDelete(String query, int idMatch) throws SQLException {
        PreparedStatement stantmentUpdate = connection.prepareStatement(query);
        stantmentUpdate.setInt(1, idMatch);
        stantmentUpdate.executeUpdate();
    }

    /**
     * Salva il match  con ultima posizione, inventario e stanze sbloccate
     * @param idMatch --> id match
     * @param nameMatch --> nome del match
     * @param idLastRoom --> id ultima stanza
     * @param nameLastRoom --> nome ultima stanza
     * @param inventario --> inventario del match
     * @param unLockRoom --> stanze sbloccate
     * @return --> ritorna l'esito
     */
    public boolean saveMatch(int idMatch, String nameMatch, int idLastRoom, String nameLastRoom, List<AdvObject> inventario, List<Integer> unLockRoom){
        if (Engine.NUM_MAX_GAME > 0 && Engine.NUM_MAX_GAME <= 4){
            try {
                //inserisce gli elementi nel db riguardanti il match, se va in eccezione è perchè è già presente un match con quell'id nella tabella
                prepareStatementSaveMatch(INSERT_METCH, idMatch, nameMatch, idLastRoom, nameLastRoom).executeUpdate();
            }catch (Exception e){
                try {
                    //aggiorno gli elementi nel db riguardanti il match
                    prepareStatementSaveMatch(UPDATE_METCH,idMatch,nameMatch,idLastRoom,nameLastRoom).executeUpdate();
                }catch (Exception exception){
                    return false;
                }
            }

            try {
                //aggiorna l'inventario e le stanze sbloccate
                updateInventario(idMatch,inventario);
                updateUnLockRoom(idMatch, unLockRoom);
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }

    /**
     * crea lo stantment per aggiornare i dati riguardanti un match
     */
    private PreparedStatement prepareStatementSaveMatch (String query, int idMatch, String nameMatch, int idLastRoom ,String nameLastRoom) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement(query);
        pstm.setInt(2, idLastRoom);
        pstm.setString(3, nameMatch);
        pstm.setInt(4, idMatch);
        pstm.setString(1, nameLastRoom);
        return pstm;
    }
}