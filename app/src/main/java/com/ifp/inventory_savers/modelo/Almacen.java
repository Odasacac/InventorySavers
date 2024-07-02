package com.ifp.inventory_savers.modelo;

import java.util.List;

public class Almacen {

    protected String nombre = "";

    protected String correoUsuario = "";
    private List<Producto> productos;

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public Almacen ()
    {}
    public Almacen(String nombre)
    {
        this.nombre=nombre;
    }

    public void setNombre (String nombre)
    {
        this.nombre=nombre;
    }

    public String getNombre()
    {
        return this.nombre;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }
    public String getCorreoUsuario() {
        return this.correoUsuario;
    }

}
