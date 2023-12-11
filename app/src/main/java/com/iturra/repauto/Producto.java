package com.iturra.repauto;

public class Producto {
    private String nombre;
    private int precio;
    private int stock;
    private String imageUrl;

    public Producto() {
    }

    public Producto(String nombre, int precio, int stock, String imageUrl) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}