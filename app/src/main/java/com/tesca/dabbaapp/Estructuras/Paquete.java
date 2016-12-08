package com.tesca.dabbaapp.Estructuras;

import java.io.Serializable;
import java.util.List;

public class Paquete implements Serializable {

    String id;
    String name;
    String price;
    String image;
    List<Paquete_Recipe> pack_cartridges;

    public List<Paquete_Recipe> getPack_cartridges() {
        return pack_cartridges;
    }

    public void setPack_cartridges(List<Paquete_Recipe> pack_cartridges) {
        this.pack_cartridges = pack_cartridges;
    }

    public Paquete() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
