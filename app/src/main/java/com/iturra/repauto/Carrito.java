package com.iturra.repauto;

public class Carrito {
    private String nombreProducto;
    private int precioProducto;
    private int CantidadProducto;
    private String imagenProducto;

    public Carrito() {
    }

    public Carrito(String nombreProducto, int precioProducto, int cantidadProducto, String imagenProducto) {
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.CantidadProducto = cantidadProducto;
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

    public int getCantidadProducto() {
        return CantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        CantidadProducto = cantidadProducto;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
}
