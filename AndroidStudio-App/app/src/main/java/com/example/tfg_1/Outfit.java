package com.example.tfg_1;

import java.util.ArrayList;

public class Outfit {
    int id;
    String name;
    int idUp, idDown, idEntero, idCalzado;
    ArrayList<Integer> idsExtra;
    ArrayList<String> colores, usos;

    public Outfit(int id, String name, int idUp, int idDown, int idEntero, int idCalzado, ArrayList<Integer> idsExtra, ArrayList<String> colores, ArrayList<String> usos) {
        this.id = id;
        this.name = name;
        this.idUp = idUp;
        this.idDown = idDown;
        this.idEntero = idEntero;
        this.idCalzado = idCalzado;
        this.idsExtra = idsExtra;
        this.colores = colores;
        this.usos = usos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdUp() {
        return idUp;
    }

    public void setIdUp(int idUp) {
        this.idUp = idUp;
    }

    public int getIdDown() {
        return idDown;
    }

    public void setIdDown(int idDown) {
        this.idDown = idDown;
    }

    public int getIdEntero() {
        return idEntero;
    }

    public void setIdEntero(int idEntero) {
        this.idEntero = idEntero;
    }

    public int getIdCalzado() {
        return idCalzado;
    }

    public void setIdCalzado(int idCalzado) {
        this.idCalzado = idCalzado;
    }

    public ArrayList<Integer> getIdsExtra() {
        return idsExtra;
    }

    public void setIdsExtra(ArrayList<Integer> idsExtra) {
        this.idsExtra = idsExtra;
    }

    public ArrayList<String> getColores() {
        return colores;
    }

    public void setColores(ArrayList<String> colores) {
        this.colores = colores;
    }

    public ArrayList<String> getUsos() {
        return usos;
    }

    public void setUsos(ArrayList<String> usos) {
        this.usos = usos;
    }
}
