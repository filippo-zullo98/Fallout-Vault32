/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.type;

import app.Utils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierpaolo
 */
public class Room {
    private int id;
    private String name;
    private String description;

    //elementi utili per bloccare la stanza
    private String lock = null;
    private int lockObject;

    private final List<Furniture> furniture = new ArrayList<>();
    private Room right = null;
    private Room left = null;
    private Room up = null;
    private Room down = null;
    private int piano;


    public void setPiano(int piano){
        this.piano = piano;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setDown(Room down) {
        this.down = down;
    }

    public Room getDown(){
        return this.down;
    }

    public void setUp(Room up) {
        this.up = up;
    }

    public Room getUp(){
        return this.up;
    }

    public void setLeft(Room left) {
        this.left = left;
    }

    public Room getLeft(){
        return this.left;
    }

    public void setRight(Room right) {
        this.right = right;
    }

    public Room getRight() {
        return this.right;
    }

    public List<Furniture> getObjects() {
        return furniture;
    }

    public void setFurniture(Furniture furniture){
        this.furniture.add(furniture);
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getPiano(){
        return piano;
    }

    public List<AdvObject> getObjectFurniture(String furniture){
        for (Furniture f :getFurniture()){
            if (Utils.parserString(f.getName()).equals(Utils.parserString(furniture))){
                return f.listObject();
            }
        }
        return null;
    }

    public List<Furniture> getFurniture() {
        return this.furniture;
    }

    public String getLock(){
        return lock;
    }


    /**
     * Funzione utile a sbloccare una stanza, la funzione verifica che l'id dell'oggetto con la quale vogliamo sbloccare
     * la stanza sia uguale a quello atteso
     * @param idobject --> id oggetto con la quale vogliamo sbloccare la stanza
     */
    public boolean unlock(int idobject){
        if (idobject == this.lockObject){
            this.lock = null;
            return true;
        }else{
            return false;
        }
    }

    public boolean isLook(){
        return this.lock != null;
    }

    public int getLockObject(){
        return this.lockObject;
    }

    public void setUnLockRoom(){
        this.lock = null;
    }
    public void setLock(String lock,int lockObject) {
        this.lock = lock;
        this.lockObject = lockObject;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return Utils.parserString(this.getName()).equals(Utils.parserString(obj.toString()));
    }

    /**
     * funzione con lo scopo di creare un json che descrive la stanza passata in input
     * @param room --> stanza da descrivere
     */
    public static JSONObject describeRoom(Room room){
        JSONObject jsonResp = new JSONObject();
        jsonResp.put("id", room.getId());
        if (room instanceof Elevator){
            jsonResp.put("name", "Ascensore al livello " + room.getPiano());
        }else {
            jsonResp.put("name", room.getName());
        }
        jsonResp.put("descrizione", room.getDescription());
        if (room instanceof Elevator){
            jsonResp.put("type",1);
        }else{
            jsonResp.put("type",0);
        }
        return jsonResp;
    }
}
