package com.example.tfg_1;

import java.util.ArrayList;

public class Prenda {
    //Prenda(id, tipo, nombre_prenda, lavando, ArrayList Colores, ArrayList Usos)
    String tipo, nombre_prenda;
    int id,lavando;
    ArrayList<String> colores, usos;

    public Prenda(int id, String tipo, String nombre_prenda, int lavando, ArrayList<String> colores, ArrayList<String> usos){
        this.id=id;
        this.tipo=tipo;
        this.nombre_prenda=nombre_prenda;
        this.lavando=lavando;
        this.colores=colores;
        this.usos=usos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre_prenda() {
        return nombre_prenda;
    }

    public void setNombre_prenda(String nombre_prenda) {
        this.nombre_prenda = nombre_prenda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLavando() {
        return lavando;
    }

    public void setLavando(int lavando) {
        this.lavando = lavando;
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
