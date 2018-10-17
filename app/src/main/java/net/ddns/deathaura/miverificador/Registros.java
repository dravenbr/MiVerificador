package net.ddns.deathaura.miverificador;

public class Registros {
    private String producto;
    private String tienda;
    private double precio;
    private String fecha;

    public Registros() {
    }

    public Registros(String producto, String tienda, double precio, String fecha) {
        this.producto = producto;
        this.tienda = tienda;
        this.precio = precio;
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
