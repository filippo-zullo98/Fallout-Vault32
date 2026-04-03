/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.parser;


import app.Utils;
import app.type.AdvObject;
import app.type.Command;
import app.type.CommandType;

import java.util.List;
import java.util.Set;


public class Parser {




    private int checkForCommand(String token, List<Command> commands) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(token) || commands.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

    private int checkForObject(String token, List<AdvObject> obejcts) {
        for (int i = 0; i < obejcts.size(); i++) {
            if (obejcts.get(i).getName().equals(token) || obejcts.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

    /* ATTENZIONE: il parser è implementato in modo abbastanza independete dalla lingua, ma riconosce solo 
    * frasi semplici del tipo <azione> <oggetto> <oggetto>. Eventuali articoli o preposizioni vengono semplicemente
    * rimossi.
     */

    /**
     *
     * @param command
     * @param commands
     * @return
     */

    public ParserOutput parse(String command, List<Command> commands) {
        List<String> tokens = Utils.parseString(command);
        if (!tokens.isEmpty()) {
            int ic = checkForCommand(tokens.get(0), commands);
            if (ic > -1) {
                if (commands.get(ic).getType() == CommandType.PICK_UP){
                    StringBuilder object = new StringBuilder();
                    for (int i = 1;i < tokens.size();i++) {
                        object.append(" ").append(tokens.get(i));
                    }
                    return new ParserOutput(commands.get(ic),object.toString());
                }
                if (commands.get(ic).getType() == CommandType.ESAMINA){
                    StringBuilder object = new StringBuilder();
                    for (int i = 1;i < tokens.size();i++) {
                        object.append(" ").append(tokens.get(i));
                    }
                    return new ParserOutput(commands.get(ic),object.toString());
                }
                if (commands.get(ic).getType() == CommandType.USE){
                    StringBuilder object = new StringBuilder();
                    for (int i = 1;i < tokens.size();i++) {
                        object.append(" ").append(tokens.get(i));
                    }
                    String str = object.toString();
                    String[] splitString = str.split(" su ");
                    if (splitString.length == 2){
                        return new ParserOutput(commands.get(ic), splitString[0],splitString[1]);
                    }if (splitString.length == 1){
                        return new ParserOutput(commands.get(ic), splitString[0]);

                    }
                }

                return new ParserOutput(commands.get(ic), null);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
