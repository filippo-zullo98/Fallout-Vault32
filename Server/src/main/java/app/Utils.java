package app;

import app.impl.FalloutGame;
import app.type.AdvObject;
import app.type.Elevator;
import app.type.Furniture;
import app.type.Room;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utils {

    public static String parserString(String string){
        return string.toLowerCase().replace(" ", "");
    }

    public static List<String> parseString(String string) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");
        for (String t : split) {
            tokens.add(t);
        }
        return tokens;
    }

    // Metodo universale per ottenere un Reader dalle risorse (Classpath)
    private static Reader getResourceReader(String resourcePath) throws FileNotFoundException {
        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        InputStream is = Utils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Risorsa non trovata nel classpath: " + path);
        }
        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }

    public static void loadRoomsFromFiles(String resourcePath, FalloutGame game) throws IOException, ParseException {
        try (Reader reader = getResourceReader(resourcePath)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            Map<Integer, JSONObject> directionJson = new HashMap<>();

            for (Object objectRoom : jsonObject.keySet()) {
                Room room = null;
                String roomName = (String) objectRoom;
                JSONObject stanzaDettagli = (JSONObject) ((JSONArray) jsonObject.get(roomName)).get(0);

                int type = Integer.parseInt(stanzaDettagli.get("type").toString());
                int piano = Integer.parseInt(stanzaDettagli.get("piano").toString());

                if (type == 0) {
                    room = new Room();
                } else if (type == 1) {
                    room = new Elevator();
                }

                if (room != null) {
                    room.setName(roomName);
                    room.setDescription(stanzaDettagli.get("descrizione").toString());
                    room.setId(Integer.parseInt(stanzaDettagli.get("id").toString()));
                    room.setPiano(piano);

                    Object isLock = stanzaDettagli.get("isLock");
                    if (!(isLock instanceof Boolean)) {
                        JSONObject direction = (JSONObject) ((JSONArray) isLock).get(0);
                        room.setLock((String) direction.get("descriptionLock"), Integer.parseInt(direction.get("objectLock").toString()));
                    }

                    JSONArray directionsArray = (JSONArray) (stanzaDettagli.get("directions"));
                    directionJson.put(room.getId(), (JSONObject) directionsArray.get(0));

                    game.getRooms().add(room);
                }
            }

            for (Map.Entry<Integer, JSONObject> entry : directionJson.entrySet()) {
                Integer key = entry.getKey();
                JSONObject value = entry.getValue();
                Room room = game.getRoomById(key);

                for (Object s : value.keySet()) {
                    String directions = s.toString();
                    long id = (Long) value.get(directions);
                    switch (directions) {
                        case "left" -> room.setLeft(game.getRoomById((int) id));
                        case "right" -> room.setRight(game.getRoomById((int) id));
                        case "down" -> room.setDown(game.getRoomById((int) id));
                        case "up" -> room.setUp(game.getRoomById((int) id));
                        case "sali" -> {
                            if (room instanceof Elevator) {
                                ((Elevator) room).setSali(game.getRoomById((int) id));
                            }
                        }
                        case "scendi" -> {
                            if (room instanceof Elevator) {
                                ((Elevator) room).setScendi(game.getRoomById((int) id));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void loadObjectFromFiles(String resourcePath, FalloutGame game) throws IOException, ParseException {
        try (Reader reader = getResourceReader(resourcePath)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            for (Object object : jsonObject.keySet()) {
                String objectName = (String) object;
                JSONObject furnitureDettagli = (JSONObject)((JSONArray) jsonObject.get(objectName)).get(0);
                int idObject = Integer.parseInt(furnitureDettagli.get("id").toString());
                int idFurniture = Integer.parseInt(furnitureDettagli.get("furniture").toString());
                String descObject = furnitureDettagli.get("descrizione").toString();
                AdvObject advObject = new AdvObject(idObject, objectName, descObject);

                game.getFurnitureById(idFurniture).addObject(advObject);
                game.getObjects().add(advObject);
            }
        }
    }

    public static void loadFurnitureFromFiles(String resourcePath, FalloutGame game) throws IOException, ParseException {
        try (Reader reader = getResourceReader(resourcePath)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            for (Object furniture : jsonObject.keySet()) {
                String furnitureName = (String) furniture;
                JSONObject furnitureDettagli = (JSONObject)((JSONArray) jsonObject.get(furnitureName)).get(0);
                int idFurniture = Integer.parseInt(furnitureDettagli.get("id").toString());
                int idRoom = Integer.parseInt(furnitureDettagli.get("stanza").toString());
                String descFurniture = furnitureDettagli.get("descrizione").toString();
                Room room = game.getRoomById(idRoom);
                if (room != null) {
                    room.setFurniture(new Furniture(idFurniture, furnitureName, descFurniture));
                }
            }
        }
    }
}