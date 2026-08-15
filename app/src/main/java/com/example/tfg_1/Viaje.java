package com.example.tfg_1;

import java.util.ArrayList;

public class Viaje {
    int id_viaje;
    String nombre_viaje, localizacion, fecha_inicio, fecha_fin, fecha_maleta;
    ArrayList<Integer> ids_outfit;

    public Viaje(int id_viaje, String nombre_viaje, String localizacion, String fecha_inicio, String fecha_fin, String fecha_maleta, ArrayList<Integer> ids_outfit) {
        this.id_viaje = id_viaje;
        this.nombre_viaje = nombre_viaje;
        this.localizacion = localizacion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.fecha_maleta = fecha_maleta;
        this.ids_outfit = ids_outfit;
    }

    public int getId_viaje() {
        return id_viaje;
    }

    public void setId_viaje(int id_viaje) {
        this.id_viaje = id_viaje;
    }

    public String getNombre_viaje() {
        return nombre_viaje;
    }

    public void setNombre_viaje(String nombre_viaje) {
        this.nombre_viaje = nombre_viaje;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getFecha_maleta() {
        return fecha_maleta;
    }

    public void setFecha_maleta(String fecha_maleta) {
        this.fecha_maleta = fecha_maleta;
    }

    public ArrayList<Integer> getIds_outfit() {
        return ids_outfit;
    }

    public void setIds_outfit(ArrayList<Integer> ids_outfit) {
        this.ids_outfit = ids_outfit;
    }
}
