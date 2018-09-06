package net.ddns.deathaura.miverificador;

import java.util.Date;

public class Registros {
    private Producto codigo;
    private Tienda tienda;
    private double precio;
    private Date fecha;

    public Registros() {
    }

    public Registros(Producto codigo, Tienda tienda, double precio, Date fecha) {
        this.codigo = codigo;
        this.tienda = tienda;
        this.precio = precio;
        this.fecha = fecha;
    }
}
