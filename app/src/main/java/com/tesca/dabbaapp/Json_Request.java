package com.tesca.dabbaapp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by drdr_ on 22/11/2016.
 */

public class Json_Request implements Serializable {

    int count;
    String next;
    String previuos;
    List<Orden> resultados;

    public Json_Request() {
    }

    public int getCount() {

        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPreviuos() {
        return previuos;
    }

    public void setPreviuos(String previuos) {
        this.previuos = previuos;
    }

    public List<Orden> getResultados() {
        return resultados;
    }

    public void setResultados(List<Orden> resultados) {
        this.resultados = resultados;
    }
}
