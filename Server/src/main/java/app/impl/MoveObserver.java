package app.impl;

import app.GameObserver;
import app.GameDescription;
import app.parser.ParserOutput;
import app.type.CommandType;
import app.type.Elevator;
import app.type.Room;

public class MoveObserver implements GameObserver {


    /**
     * questa funzione serve a controllare se il movimento è valido o meno, se il movimento non è valido
     * il personaggio resta nella stessa posizione altrimenti viene spostato nella direzione indicata dal comando
     *
     * @param description --> serve per ottenere la stanza corrente e settare la nuova stanza
     * @param parserOutput --> indica la direzione
     * @return
     */
    @Override
    public String update(GameDescription description, ParserOutput parserOutput) {
        Room currentRoom = description.getCurrentRoom();

        if (parserOutput.getCommand().getType() == CommandType.UP) {
            if (currentRoom.getUp() != null) {
                if (!currentRoom.getUp().isLook()){
                    description.setCurrentRoom(currentRoom.getUp());
                }else{
                    return currentRoom.getUp().getLock();
                }
            } else {
                return "Da quella parte non si può andare c'è un muro!";
            }
        } else if (parserOutput.getCommand().getType() == CommandType.RIGHT) {
            if (currentRoom.getRight() != null) {
                if (!currentRoom.getRight().isLook()){
                    description.setCurrentRoom(currentRoom.getRight());
                }else{
                    return currentRoom.getRight().getLock();
                }
            } else {
                return "Da quella parte non si può andare c'è un muro!";
            }
        } else if (parserOutput.getCommand().getType() == CommandType.LEFT) {
            if (currentRoom.getLeft() != null) {
                if (!currentRoom.getLeft().isLook()){
                    description.setCurrentRoom(currentRoom.getLeft());
                }else{
                    return currentRoom.getLeft().getLock();
                }
            } else {
                return "Da quella parte non si può andare c'è un muro!";
            }
        } else if (parserOutput.getCommand().getType() == CommandType.DOWN) {
            if (currentRoom.getDown() != null) {
                if (!currentRoom.getDown().isLook()){
                    description.setCurrentRoom(currentRoom.getDown());

                }else{
                    if (currentRoom.getDown() instanceof Elevator){
                        description.setCurrentRoom(currentRoom.getDown());
                    }

                    return currentRoom.getDown().getLock();
                }
            } else {
                return "Da quella parte non si può andare c'è un muro!";
            }
        } else if (parserOutput.getCommand().getType() == CommandType.SALI) {
            // si controlla se la stanza corrente è un istanza di ascensore,
            // altrimenti non ha senso il comando sali
            if (currentRoom instanceof Elevator){
                if (((Elevator) currentRoom).getSali() != null) {
                    if (!(currentRoom).isLook()){
                        description.setCurrentRoom(((Elevator) currentRoom).getSali());
                    }else{
                        return ((Elevator) currentRoom).getLock();

                    }
                }else{
                    return "Non puoi salire";
                }
            }

        }
        else if (parserOutput.getCommand().getType() == CommandType.SCENDI) {
            // si controlla se la stanza corrente è un istanza di ascensore,
            // altrimenti non ha senso il comando scendi
            if (currentRoom instanceof Elevator){
                if (((Elevator) currentRoom).getScendi() != null) {
                    description.setCurrentRoom(((Elevator) currentRoom).getScendi());
                }else{
                    return "Non puoi scendere";
                }
            }
        }
        return null;
    }

}
