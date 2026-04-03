/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.impl;


import app.GameObserver;
import app.GameDescription;
import app.parser.ParserOutput;
import app.type.AdvObject;
import app.type.CommandType;
import app.type.Furniture;
import org.json.JSONObject;

import java.util.List;


public class UseObserver implements GameObserver {

    /**
     *
     * @param description
     * @param parserOutput
     * @return
     */
    @Override
    public String update(GameDescription description, ParserOutput parserOutput) {
        JSONObject jsonObjects = new JSONObject();
        StringBuilder msg = new StringBuilder();
        if (parserOutput.getCommand().getType() == CommandType.OBSERVE) {
            List<Furniture> furnitures = description.getCurrentRoom().getObjects();
            for (Furniture furniture : furnitures){
                msg.append(furniture.getName()).append(" ");
            }

            if (msg.length() == 0){
                return jsonObjects.put("descrizione", "Non vedo nulla di rilevante nella stanza" ).toString();
            } else {
                return jsonObjects.put("descrizione", msg.toString()).toString();
            }
        }
        return null;
    }

}
