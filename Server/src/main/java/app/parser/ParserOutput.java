/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.parser;


import app.type.AdvObject;
import app.type.Command;

/**
 *
 * @author pierpaolo
 */
public class ParserOutput {

    private Command command;

    private String object;

    private String idRoomUnLock;

    /**
     *
     * @param command
     * @param object
     */
    public ParserOutput(Command command, String object) {
        this.command = command;
        this.object = object;
    }

    public ParserOutput(Command command, String object,String idRoomUnLock) {
        this.command = command;
        this.object = object;
        this.idRoomUnLock = idRoomUnLock;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getIdRoomUnLock() {
        return idRoomUnLock;
    }

    public void setIdRoomUnLock(String s) {
        idRoomUnLock = s;
    }
}
