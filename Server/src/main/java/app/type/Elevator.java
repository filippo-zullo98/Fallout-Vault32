package app.type;

/**
 * estensione della classe Room con 2 attributi in più
 */
public class Elevator extends Room{
    private Room sali = null;
    private Room  scendi = null;

    public Room getScendi() {
        return scendi;
    }

    public void setScendi(Room scendi) {
        this.scendi = scendi;
    }

    public Room getSali() {
        return sali;
    }

    public void setSali(Room sali) {
        this.sali = sali;
    }

    public Elevator() {
        super();
    }

}
