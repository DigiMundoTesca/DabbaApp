package com.tesca.dabbaapp.Estructuras;

import java.io.Serializable;

/**
 * Created by drdr_ on 18/11/2016.
 */

public class Custom_Order implements Serializable {

    String id;
    Cartucho cartucho;
    Paquete paquete;
    String quantity;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Custom_Order() {
    }

    public Custom_Order(String id, Cartucho cartucho, Paquete paquete) {
        this.id = id;
        this.cartucho = cartucho;
        this.paquete = paquete;
    }

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

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }
}
