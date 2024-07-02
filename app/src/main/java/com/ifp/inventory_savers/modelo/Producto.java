package com.ifp.inventory_savers.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Producto {
    private String idProducto; // Puede ser manejado por Firestore automáticamente
    private String correoUsuarioOrg;
    private String nombre;
    private String descripcion;
    private double precioCosto;
    private int cantidad;
    private String fechaExpiracion;
    private String proveedor;
    private String codigoBarras;
    private int nivelReordenamiento;
    private String almacen;
    private Date fechaAgregado;
    //private String imagenUrl;

    // Constructor sin argumentos
    public Producto() {
    }

    // Constructor completo incluyendo el almacen
    public Producto( String correoUsuarioOrg,String nombre, String descripcion, double precioCosto, int cantidad,
                     String fechaExpiracion, String proveedor, String codigoBarras, int nivelReordenamiento,
                     String almacen, Date fechaAgregado/*,String imagenUrl*/) {
        this.correoUsuarioOrg=correoUsuarioOrg;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCosto = precioCosto;
        this.cantidad = cantidad;
        this.fechaExpiracion = fechaExpiracion;
        this.proveedor = proveedor;
        this.codigoBarras = codigoBarras;
        this.nivelReordenamiento = nivelReordenamiento;
        this.almacen = almacen;
        this.fechaAgregado = fechaAgregado != null ? fechaAgregado : new Date();
        //this.imagenUrl = imagenUrl;
    }
    // Constructor personalizado
    public Producto(String nombre, int cantidad, double precioCosto, String fechaExpiracion) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioCosto = precioCosto;
        this.fechaExpiracion = fechaExpiracion;

        // Valores por defecto para los demás atributos
        this.descripcion = "";
        this.proveedor = "";
        this.codigoBarras = "";
        this.nivelReordenamiento = 0;
        this.almacen = "";
        this.fechaAgregado = new Date();
        //this.imagenUrl = "";
    }
    // Getters y Setters para cada campo
/*
    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }


 */
    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getCorreoUsuarioOrg() {
        return correoUsuarioOrg;
    }

    public void setCorreoUsuarioOrg(String correoUsuarioOrg) {
        this.correoUsuarioOrg = correoUsuarioOrg;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getNivelReordenamiento() {
        return nivelReordenamiento;
    }

    public void setNivelReordenamiento(int nivelReordenamiento) {
        this.nivelReordenamiento = nivelReordenamiento;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public Date getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(Date fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public Date getFechaExpiracionDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formatter.parse(fechaExpiracion);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}