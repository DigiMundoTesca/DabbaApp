package com.tesca.dabbaapp.Estructuras;

import java.io.Serializable;

/**
 * Created by drdr_ on 22/11/2016.
 */

public class Paquete_Recipe implements Serializable {

    String id;
    Cartucho cartucho;
    String quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cartucho getCartucho() {
        return cartucho;
    }

    public void setCartucho(Cartucho cartucho) {
        this.cartucho = cartucho;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
