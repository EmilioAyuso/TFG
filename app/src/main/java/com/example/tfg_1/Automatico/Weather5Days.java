package com.example.tfg_1.Automatico;

import java.util.List;

public class Weather5Days {
    private InfoTemperature main;
    private List<Weather> weather;
    private String dt_txt;

    public InfoTemperature getMain() {
        return main;
    }

    public void setMain(InfoTemperature main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}
