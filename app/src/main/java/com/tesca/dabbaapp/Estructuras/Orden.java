package com.tesca.dabbaapp.Estructuras;

import java.io.Serializable;
import java.util.List;

/**
 * Created by drdr_ on 18/11/2016.
 */

public class Orden implements Serializable{

    List<Cartucho> lista_de_cartuchos;
    List<Paquete> lista_de_paquetes;
    Custom_User custom_user;
    String id;
    String created_date;
    String delivery_date;
    String status;
    String price;
    String latitude;
    String longitude;

    public Custom_User getCustom_user() {
        return custom_user;
    }

    public void setCustom_user(Custom_User custom_user) {
        this.custom_user = custom_user;
    }

    public Orden() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<Cartucho> getLista_de_cartuchos() {
        return lista_de_cartuchos;
    }

    public void setLista_de_cartuchos(List<Cartucho> lista_de_cartuchos) {
        this.lista_de_cartuchos = lista_de_cartuchos;
    }

    public List<Paquete> getLista_de_paquetes() {
        return lista_de_paquetes;
    }

    public void setLista_de_paquetes(List<Paquete> lista_de_paquetes) {
        this.lista_de_paquetes = lista_de_paquetes;
    }
}
