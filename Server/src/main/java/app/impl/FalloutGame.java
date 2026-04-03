package app.impl;
import app.*;
import app.RESTconfig.FalloutApi;
import app.parser.Parser;
import app.type.*;
import app.parser.ParserOutput;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.swing.*;
import javax.ws.rs.core.UriBuilder;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.time.Duration;
import java.time.LocalTime;

import java.util.*;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.*;


public class FalloutGame extends GameDescription implements Runnable {
    public static final int INIT_DEFAULT_ROOM = 0;
    private static final int ROOM_START_TIME = 8;
    private static final int ID_COMPUTER = 6;
    private static final int ID_STIMAK = 4;
    private static final int ID_FUCILE = 5;
    private static final int ROOM_GUARDIANI = 18;
    private static final int ROOM_INFERMIERA = 8;
    private static final int ROOM_COMPUTER = 15;
    private static final int TIME_MORTE_INFERMIERA = 30;

    private int portUsed;

    LocalTime startTime = null;
    private HttpServer server;
    private String message;
    private final List<GameObserver> observer = new ArrayList<>();

    private boolean useComupter;
    private final List<AdvObject> inventario = new ArrayList<>();//contiene l'inventario del giocatore corrente

    /**
     * @param idRoom --> indica la room dalla quale far partire la partita
     * @param portUsed --> porta sulla quale sarà in ascolto il server
     */
    public FalloutGame(int idRoom, int portUsed, List<Integer> inventario, List<Integer> unLockRoom) throws IOException, ParseException {
        this.portUsed = portUsed;
        Utils.loadRoomsFromFiles("/configFile/rooms.json", this);
        Utils.loadFurnitureFromFiles("/configFile/furniture.json", this);
        Utils.loadObjectFromFiles("/configFile/object.json", this);
        init();
        setRoomInit(idRoom);
        setInventario(inventario);
        setInitUnLockRoom(unLockRoom);
    }

    /**
     * Funzione utile ad inizializzare i comandi di gioco
     */
    public void init(){

        //Commands
        Command nord = new Command(CommandType.UP, "up");
        nord.setAlias(new String[]{"avanti"});
        getCommands().add(nord);

        Command iventory = new Command(CommandType.DOWN, "down");
        iventory.setAlias(new String[]{"giù"});
        getCommands().add(iventory);

        Command sud = new Command(CommandType.RIGHT, "right");
        sud.setAlias(new String[]{"destra"});
        getCommands().add(sud);

        Command est = new Command(CommandType.LEFT, "left");
        est.setAlias(new String[]{"sinistra"});
        getCommands().add(est);

        Command sali = new Command(CommandType.SALI, "sali");
        sali.setAlias(new String[]{"sali"});
        getCommands().add(sali);

        Command scendi = new Command(CommandType.SCENDI, "scendi");
        scendi.setAlias(new String[]{"scendi"});
        getCommands().add(scendi);

        Command observe = new Command(CommandType.OBSERVE, "osserva");
        observe.setAlias(new String[]{"osserva"});
        getCommands().add(observe);

        Command inventario = new Command(CommandType.INVENTORY, "inventario");
        inventario.setAlias(new String[]{"inventario"});
        getCommands().add(inventario);

        Command prendi = new Command(CommandType.PICK_UP,"prendi");
        prendi.setAlias(new String[]{"prendi"});
        getCommands().add(prendi);

        Command usa = new Command(CommandType.USE,"usa");
        usa.setAlias(new String[]{"usa"});
        getCommands().add(usa);

        Command esamina = new Command(CommandType.ESAMINA,"esamina");
        esamina.setAlias(new String[]{"esamina"});
        getCommands().add(esamina);

        MoveObserver moveObserver = new MoveObserver();
        this.attach(moveObserver);

        UseObserver useObserver = new UseObserver();
        this.attach(useObserver);

        InventoryObserver inventoryObserver = new InventoryObserver();
        this.attach(inventoryObserver);
    }

    /**
     * serve a settare il punto di partenza della partita sulla stanza passata come parametro
     * @param idStanza --> id UNIVOCO della stanza
     */
    private void setRoomInit(int idStanza) {
        for (Room room : getRooms()) {
            if (room.getId()==idStanza) {
                setCurrentRoom(room);
                break;
            }
        }
    }

    /**
     * Passata una lista di oggetti, li aggiunge all'inventario
     * @param inventario --> lista di oggetti
     */
    private void setInventario(List<Integer> inventario) {
        for (Integer idObject : inventario){
            pickUpObject(idObject);
        }
    }

    /**
     * Passata una lista di id di stanze le sbloca
     * @param unLockRoom --> lista id stanze da sbliccare
     */
    private void setInitUnLockRoom(List<Integer> unLockRoom){
        for (Integer idRoomLock: unLockRoom){
            getRoomById(idRoomLock).setUnLockRoom();
        }
    }

    /**
     * la funzione serve a prendere l'oggetto passato come parametro
     * la funzione controlla se l'oggetto si trova all'interno della stanza corrente
     */
    private void pickUpObject(int idObject){
        for (Furniture furniture : getCurrentRoom().getFurniture()) {
            Iterator<AdvObject> iterator = furniture.listObject().iterator();
            while (iterator.hasNext()) {
                AdvObject object = iterator.next();
                if (object.getId() == idObject) {
                    inventario.add(object);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * la funzione serve a prendere l'oggetto passato come parametro
     * la funzione controlla se l'oggetto si trova all'interno della stanza corrente
     *
     */
    public void pickUpObject(String nameObject){
        for (Furniture furniture : getCurrentRoom().getFurniture()) {
            Iterator<AdvObject> iterator = furniture.listObject().iterator();
            while (iterator.hasNext()) {
                AdvObject object = iterator.next();
                if (object.equals(nameObject)){
                    inventario.add(object);
                    iterator.remove();
                }
            }
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * funzione che ci consente di muovere il personaggio all'interno della mappa
     *
     * @param parserOutput --> contiene l'azione che si vuole effettuare, se null il parser non ha riconosciuto il comando
     * @return true --> movimento effettuato
     *         false --> movimento non consentito
     */
    public boolean nextMove(ParserOutput parserOutput) {
        boolean lastMove = false;
        if (parserOutput.getCommand() == null) {
            System.out.println("COMANDO NON COMPRESO");
        } else {
            Room cr = getCurrentRoom();
            notifyObservers(parserOutput);
            if (!cr.equals(getCurrentRoom().getName()) && getCurrentRoom() != null) lastMove = true;
            if (lastMove) {
                if (ROOM_START_TIME == getCurrentRoom().getId()){
                    if (startTime == null){ //entra nella stanza con la persona morta
                        startTime = LocalTime.now();
                    }
                }
                if (getCurrentRoom().getPiano() == 2){
                    getRoomById(13).unlock(-1);
                }
                if (getCurrentRoom().getId() == 17){
                    ImageIcon icon = new ImageIcon("Frontend/src/main/resources/images/finale.jpg");
                    Image originalImage = icon.getImage();
                    Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    // Mostra il messaggio di dialogo con l'immagine
                    JOptionPane.showMessageDialog(
                            null,
                            "Sei uscito dal vault, chiudi la schermata di gioco",
                            "Fine Partita",
                            JOptionPane.INFORMATION_MESSAGE,
                            scaledIcon
                    );
                    endMatch();
                    //return Response.ok(jsonResp.toString(),MediaType.APPLICATION_JSON).build();
                }
                System.out.println("MOVIMENTO EFFETTUATO " + portUsed);
                System.out.println(getCurrentRoom().getName());
            }
        }
        return lastMove;
    }

    private String curaInfermiera(AdvObject object, Furniture furniture){
        leavesObject(object);
        getStartTime();

        if (verificaTimer()>TIME_MORTE_INFERMIERA){
            return "Troppo tardi non sei riuscito a curare l'infermiera in tempo";
        } else {
            return furniture.getDescription();
        }
    }

    private long verificaTimer(){
        LocalTime startTime = getStartTime();
        LocalTime endTime = LocalTime.now();
        System.out.println("Orario di fine: " + endTime);
        Duration duration = Duration.between(startTime, endTime);
        return duration.getSeconds();
    }

    private void unLockRoom(Room room,AdvObject object, int unLock ){
        room.unlock(unLock);
        getUnLockRoom().add(room.getId());
        getInventory().remove(object);
    }

    public JSONObject updateDescribeRoom(JSONObject describe){
        if (getCurrentRoom().getId() == ROOM_INFERMIERA && getStartTime() != null && verificaTimer() >= 0 && verificaTimer() < TIME_MORTE_INFERMIERA){
            describe.put("time",TIME_MORTE_INFERMIERA);
        }
        return describe;
    }

    private JSONObject parseEsamina(ParserOutput command){
        JSONObject jsonResp = new JSONObject();

        if (command.getObject() != null){
            String msg;
            List<AdvObject> objects = getCurrentRoom().getObjectFurniture(command.getObject());
            boolean flag = false;
            if (!objects.isEmpty()){
                pickUpObject(objects.get(0).getName());
                flag = true;
            }
            if (flag){
                msg = "Oggetto aggiunto all'inventario";
            }else{
                msg =  "Nulla di rilevante";
            }
            return jsonResp.put("descrizione",msg);
        }
        return jsonResp.put("descrizione", "Non puoi esaminare ciò");
    }

    private JSONObject parseUsa(ParserOutput command){
        JSONObject jsonResp = new JSONObject();
        int codice_sblocco = 0;
        Furniture furniture = getFurnitureByName(command.getIdRoomUnLock());

        if (getCurrentRoom() instanceof Elevator){
            command.setIdRoomUnLock(command.getIdRoomUnLock() + getCurrentRoom().getPiano()) ;
        }
        AdvObject object = getObjectByName(command.getObject());
        if (command.getIdRoomUnLock() == null && command.getObject() != null){
            if (getObjectByName(command.getObject()).getId() == ID_COMPUTER){
                if (!useComupter){
                    useComupter=true;
                    return jsonResp.put("descrizione","Inserire la password per sbloccare la porta del vault");
                }
            }
        }

        if (command.getObject().replace(" ","").length() == 4){
            // se codice_sblocco viene avvalorato --> dopo usa è stato messo un codice
            try {
                codice_sblocco = Integer.parseUnsignedInt(command.getObject().replace(" ",""));
            }catch (Exception e){
                System.out.println("non è un codice");
            }
        }
        Room room = getRoomByName(command.getIdRoomUnLock());
        if (room != null){  //sblocco stanza
            if (object != null){ //sblocco con oggetto
                if(getInventory().contains(object)){ // controlla che l'oggetto che vogliamo usare ' all'interno dell'inventario
                    if (getAdjacentRoom(getCurrentRoom(), room)){ // controlla che ci troviamo nelle vicinanze della stanza per sbloccarla
                        unLockRoom(room,object,object.getId());
                        return jsonResp.put("descrizione","Stanza sbloccata ora puoi accedere");
                    }
                }
            }
            if (room.isLook()){//sblocco con codice
                if (room.getLockObject() == codice_sblocco && getAdjacentRoom(getCurrentRoom(), room)){
                    unLockRoom(room,object,codice_sblocco);
                    return jsonResp.put("descrizione","Codice inserito correttamente");
                }
            }
        } else {
            if (furniture != null && object != null){
                if (object.getId() == ID_STIMAK && furniture.equals("infermiera") && getCurrentRoom().getId() == ROOM_INFERMIERA){ //cura l'infermiera entro 15 secondi
                    return jsonResp.put("descrizione", curaInfermiera(object,furniture));
                }
                if (object.getId() == ID_FUCILE && furniture.getName().equals("guardiani") && getCurrentRoom().getId() == 14){ //spara i guardiani
                    getRoomById(ROOM_GUARDIANI).unlock(-1);
                    getUnLockRoom().add(ROOM_GUARDIANI);
                    return jsonResp.put("descrizione", "Hai neutralizzato i guardiani, adesso puoi accedere al piano 2");
                }
            }
        }
        return jsonResp.put("descrizione", "Comando non riconosciuto");
    }
    private void leavesObject(AdvObject object){
        inventario.remove(object);
    }
    public List<AdvObject> getInventory() {
        return this.inventario;
    }
    public JSONObject parseCommand(String p){
        JSONObject jsonResp = new JSONObject();
        ParserOutput command = new Parser().parse(p, getCommands());

        if (command != null){
            //comando non riconosciuto
            if (command.getCommand().getType() == CommandType.OBSERVE){
                jsonResp.put("descrizione",getCurrentRoom().getDescription());
                return jsonResp;
            }
            if (command.getCommand().getType() == CommandType.ESAMINA){
                return parseEsamina(command);
            }
            if (command.getCommand().getType() == CommandType.USE){
                return parseUsa(command);
            }
        } else {
            if (useComupter && getCurrentRoom().getId() == ROOM_COMPUTER){
                if(p.equals("password")){ //verifica il comando
                    useComupter = false;
                    getRoomById(17).unlock(-1);
                    return jsonResp.put("descrizione","Porta del vault sbloccata");
                }

                useComupter = false;
                return jsonResp.put("descrizione","Password errata");
            }
        }

        return jsonResp.put("descrizione", "Comando non riconosciuto");
    }

    @Override
    public String notifyObservers(ParserOutput parserOutput) {
        for (GameObserver o : observer) {
            String res = o.update(this, parserOutput);
            if (res != null){
                message = res;
                return message;
            }
        }
        return null;
    }

    public void attach(GameObserver o) {
        if (!observer.contains(o)) {
            observer.add(o);
        }
    }

    public String getMessages(){
        return message;
    }

    /**
     * Funzione utile per chiudere il server
     */
    public void endMatch(){
        server.shutdown();
    }

    /**
     * la funzione run è un override dell'interfaccia Runnable, essa viene chiamata quando viene avviato il nuovo thread.
     * La funzione avvia il server sulla porta indicata e usa come classe di configurazione FalloutApi. che contiene tutte le
     * REST api per la gestione della partita
     */
    @Override
    public void run() {
        try {
            URI baseUri = UriBuilder.fromUri(Engine.URL_SERVER).port(portUsed).build();
            FalloutApi falloutApi = new FalloutApi(this);

            ResourceConfig config = new ResourceConfig()
                    .register(falloutApi)
                    .packages("app");
            server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
            server.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
