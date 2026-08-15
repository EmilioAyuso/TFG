package com.example.tfg_1;

public class Usuario {

    String username,password,correo,nombreReal,nombreCiudad;
    int anio_nacimiento;
    public Usuario(String username,String password,String correo,String nombreReal,int anio_nacimiento,String nombreCiudad){
        this.username=username;
        this.password=password;
        this.correo=correo;
        this.nombreReal=nombreReal;
        this.anio_nacimiento=anio_nacimiento;
        this.nombreCiudad=nombreCiudad;

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public int getAnio_nacimiento() {
        return anio_nacimiento;
    }

    public void setAnio_nacimiento(int anio_nacimiento) {
        this.anio_nacimiento = anio_nacimiento;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }
}
