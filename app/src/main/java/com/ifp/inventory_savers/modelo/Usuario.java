package com.ifp.inventory_savers.modelo;

public class Usuario {

    protected String correoUsuario,nivelPermisos,nombre ;

    public Usuario() {
    }

    public Usuario(String correoUsuario, String nivelPermisos, String nombre) {
        this.correoUsuario = correoUsuario;
        this.nivelPermisos = nivelPermisos;
        this.nombre = nombre;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getNivelPermisos() {
        return nivelPermisos;
    }

    public void setNivelPermisos(String nivelPermisos) {
        this.nivelPermisos = nivelPermisos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
