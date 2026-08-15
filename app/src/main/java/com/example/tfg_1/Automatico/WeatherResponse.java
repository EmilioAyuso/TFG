package com.example.tfg_1.Automatico;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("coord")
    private Coord cordenadas;
    @SerializedName("weather")
    private List<Weather> weatherList;
    @SerializedName("main")
    private InfoTemperature infoTemperature;

    public Coord getCoordenadas(){
        return cordenadas;
    }
    public List<Weather> getWeatherList(){
        return weatherList;
    }
    public InfoTemperature getInfoTemperature(){
        return infoTemperature;
    }
}
