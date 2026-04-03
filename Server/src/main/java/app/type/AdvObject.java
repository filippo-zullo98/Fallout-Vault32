
package app.type;

import app.Utils;
import org.json.JSONObject;
import java.util.Set;


public class AdvObject {

    private final int id;

    private String name;

    private String description;
    
    private Set<String> alias;


    public AdvObject(int id) {
        this.id = id;
    }

    public AdvObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AdvObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public AdvObject(int id, String name, String description, Set<String> alias) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AdvObject){
            return Utils.parserString(this.getName()).equals(Utils.parserString(((AdvObject) obj).getName()));

        }
        if (obj instanceof String){
            return Utils.parserString(this.getName()).equals(Utils.parserString((String) obj));
        }
        return false;
    }

    /**
     * Funzione con lo scopo di creare un json che descrive l'oggetto passato
     * @param advObject --> passa l'oggetto
     * @return --> ritorna la descrizione dell'oggetto
     */
    public static JSONObject describeAdvObject(AdvObject advObject){
        JSONObject jsonResp = new JSONObject();
        jsonResp.put("id", advObject.getId());
        jsonResp.put("name", advObject.getName());
        jsonResp.put("descrizione", advObject.getDescription());
        return jsonResp;
    }

}
