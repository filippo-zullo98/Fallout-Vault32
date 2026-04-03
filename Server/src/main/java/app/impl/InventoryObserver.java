package app.impl;

import app.GameObserver;
import app.GameDescription;
import app.parser.ParserOutput;
import app.type.AdvObject;
import app.type.CommandType;
import org.json.JSONObject;


public class InventoryObserver implements GameObserver {

    /**
     *
     * @param description
     * @param parserOutput
     * @return
     */
    @Override
    public String update(GameDescription description, ParserOutput parserOutput) {
        StringBuilder msg = new StringBuilder();
        if (parserOutput.getCommand().getType() == CommandType.INVENTORY) {
            if (description.getInventory().isEmpty()) {
                System.out.println("Il tuo inventario è vuoto!");
            } else {
                System.out.println("Nel tuo inventario ci sono:");
                JSONObject objectList = new JSONObject();

                for (AdvObject o : description.getInventory()) {
                    System.out.println(o.getName());
                    objectList.put(String.valueOf(o.getId()),AdvObject.describeAdvObject(o));
                    msg.append(o.getName()).append(": ").append(o.getDescription()).append("\n");
                }
                return objectList.toString();
            }
        }
        return null;
    }

}
