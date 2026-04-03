/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.parser.ParserOutput;
import app.type.AdvObject;
import app.type.Command;
import app.type.Furniture;
import app.type.Room;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

    private List<Integer> unLockRoom = new ArrayList<>();

    private List<AdvObject> objects = new ArrayList<>();

    private Room currentRoom;


    public abstract void init() throws Exception;

    public abstract void pickUpObject(String nameObject);

    public abstract JSONObject parseCommand(String p);

    public abstract boolean nextMove(ParserOutput p);

    public abstract LocalTime getStartTime();

    public abstract String notifyObservers(ParserOutput parserOutput);

    public abstract String getMessages();

    public abstract void endMatch();

    public abstract JSONObject updateDescribeRoom(JSONObject describe);

    public abstract List<AdvObject> getInventory();

    /**
     * pasata una stanza ritorna la lista delle stanze adiacenti
     */
    public boolean getAdjacentRoom(Room current, Room room) {
        List<Room> rooms = new ArrayList<>();
        if (current.getLeft() != null){
            rooms.add(current.getLeft());
        }
        if (current.getRight() != null){
            rooms.add(current.getRight());
        }
        if (current.getUp() != null){
            rooms.add(current.getUp());
        }
        if (current.getDown() != null){
            rooms.add(current.getDown());
        }
        if (current == room){
            rooms.add(room);
        }
        for (Room room1 : rooms){
            if (room1 == room){
                return true;
            }
        }
        return false;
    }

    public List<Integer> getUnLockRoom() {
        return unLockRoom;
    }
    public List<AdvObject> getObjects() {
        return objects;
    }
    public AdvObject getObjectByName(String name){
        for (AdvObject object : getObjects()){
            if (object.equals(name)){
                return object;
            }
        }
        return null;
    }

    public List<Room> getRooms() {
        return rooms;
    }
    public Room getRoomByName(String nameRoom) {
        for (Room room : getRooms()){
            if (room.equals(nameRoom)){
                return room;
            }
        }
        return null;
    }
    public Room getRoomById(int idRoom) {
        for (Room room : getRooms()){
            if (idRoom == room.getId()){
                return room;
            }
        }
        return null;
    }
    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    public Furniture getFurnitureById(int idFurniture){
        for (Room room : getRooms()){
            for (Furniture furniture : room.getFurniture()){
                if (furniture.getId() == idFurniture){
                    return furniture;
                }
            }
        }
        return null;
    }
    public Furniture getFurnitureByName(String Namefurniture){
        for (Room room : getRooms()){
            for (Furniture furniture : room.getFurniture()){
                if (furniture.getName().equals(Namefurniture)){
                    return furniture;
                }
            }
        }
        return null;
    }
    public List<Command> getCommands() {
        return commands;
    }

}
