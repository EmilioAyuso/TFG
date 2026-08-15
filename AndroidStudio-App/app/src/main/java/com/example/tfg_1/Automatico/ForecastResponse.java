package com.example.tfg_1.Automatico;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<Weather5Days> list;
    @SerializedName("city")
    private City city;
    public List<Weather5Days> getGeneralList(){
        return list;
    }
    public City getCiudad(){ return city;}
}
