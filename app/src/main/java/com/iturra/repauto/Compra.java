package com.iturra.repauto;


import java.util.Date;

public class Compra {
    private int cantidadProducto;
    private String estadoProducto;
    private Date fechaCompra;
    private Date fechaRetiro;
    private String imagenProducto;
    private String nombreProducto;
    private int precioProducto;

    public Compra() {
    }

    public Compra(int cantidadProducto, String estadoProducto, Date fechaCompra, Date fechaRetiro, String imagenProducto, String nombreProducto, int precioProducto) {
        this.cantidadProducto = cantidadProducto;
        this.estadoProducto = estadoProducto;
        this.fechaCompra = fechaCompra;
        this.fechaRetiro = fechaRetiro;
        this.imagenProducto = imagenProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(int precioProducto) {
        this.precioProducto = precioProducto;
    }
}