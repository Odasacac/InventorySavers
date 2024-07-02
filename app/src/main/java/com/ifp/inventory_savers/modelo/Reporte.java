package com.ifp.inventory_savers.modelo;

import java.util.ArrayList;
import java.util.List;

public class Reporte {
    private List<Almacen> almacenes;
    private List<Producto> productos;

    public Reporte(Almacen almacen) {
        this.almacenes = new ArrayList<>();
        this.almacenes.add(almacen);
    }

    public List<Almacen> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(List<Almacen> almacenes) {
        this.almacenes = almacenes;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    public void addAlmacen(Almacen almacen) {
        if (almacenes == null) {
            almacenes = new ArrayList<>();
        }
        almacenes.add(almacen);
    }


}
