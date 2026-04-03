package app.type;

import app.Utils;

import java.util.ArrayList;
import java.util.List;

public class Furniture {
    private final int id;
    private final String description;
    private final String name;
    private final List<AdvObject> listObject = new ArrayList<>();

    public Furniture(int id,String name, String description) {
        this.id = id;
        this.description = description;
        this.name = name;
    }
    public void addObject(AdvObject object){
        listObject.add(object);
    }
    public List<AdvObject> listObject(){
        return listObject;
    }
    public int getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        return Utils.parserString(this.getName()).equals(obj.toString());
    }

}
