package com.example.lab5;

public class Habito {

    private String nombre;
//em
    private String categoria;
    private int frecuenciaHoras;
    private String fechaHoraInicio;

    public Habito(String nombre, String categoria, int frecuenciaHoras, String fechaHoraInicio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuenciaHoras = frecuenciaHoras;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getFrecuenciaHoras() {
        return frecuenciaHoras;
    }

    public String getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFrecuenciaHoras(int frecuenciaHoras) {
        this.frecuenciaHoras = frecuenciaHoras;
    }

    public void setFechaHoraInicio(String fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
}