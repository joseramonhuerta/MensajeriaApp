package com.eletronica.mensajeriaapp;

import java.io.Serializable;

public class Pedido implements Serializable {
    private int id_pedido;
    private int id_usuario;
    private int id_usuario_mensajero;
    private String nombre;
    private String nombre_mensajero;
    private double calificacion;
    private double calificacion_mensajero;
    private String fecha;
    private String hora;
    private String origen;
    private String destino;
    private double importe;
    private String descripcion_origen;
    private int status;
    private String status_descripcion;
    private double origen_latitud;
    private double origen_longitud;
    private double destino_latitud;
    private double destino_longitud;
    private String celular;
    private String celular_mensajero;
    private String placas;
    private double ubicacion_latitud;
    private double ubicacion_longitud;

    public double getCalificacion_mensajero() {
        return calificacion_mensajero;
    }

    public void setCalificacion_mensajero(double calificacion_mensajero) {
        this.calificacion_mensajero = calificacion_mensajero;
    }

    public double getUbicacion_latitud() {
        return ubicacion_latitud;
    }

    public void setUbicacion_latitud(double ubicacion_latitud) {
        this.ubicacion_latitud = ubicacion_latitud;
    }

    public double getUbicacion_longitud() {
        return ubicacion_longitud;
    }

    public void setUbicacion_longitud(double ubicacion_longitud) {
        this.ubicacion_longitud = ubicacion_longitud;
    }



    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getId_usuario_mensajero() {
        return id_usuario_mensajero;
    }

    public void setId_usuario_mensajero(int id_usuario_mensajero) {
        this.id_usuario_mensajero = id_usuario_mensajero;
    }

    public String getNombre_mensajero() {
        return nombre_mensajero;
    }

    public void setNombre_mensajero(String nombre_mensajero) {
        this.nombre_mensajero = nombre_mensajero;
    }

    public String getCelular_mensajero() {
        return celular_mensajero;
    }

    public void setCelular_mensajero(String celular_mensajero) {
        this.celular_mensajero = celular_mensajero;
    }

    public String getFecha_header() {
        return fecha_header;
    }

    public void setFecha_header(String fecha_header) {
        this.fecha_header = fecha_header;
    }

    public Double getTotal_header() {
        return total_header;
    }

    public void setTotal_header(Double total_header) {
        this.total_header = total_header;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    private String fecha_header;
    private Double total_header;
    private int template;

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getDescripcion_origen() {
        return descripcion_origen;
    }

    public void setDescripcion_origen(String descripcion_origen) {
        this.descripcion_origen = descripcion_origen;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_descripcion() {
        return status_descripcion;
    }

    public void setStatus_descripcion(String status_descripcion) {
        this.status_descripcion = status_descripcion;
    }

    public double getOrigen_latitud() {
        return origen_latitud;
    }

    public void setOrigen_latitud(double origen_latitud) {
        this.origen_latitud = origen_latitud;
    }

    public double getOrigen_longitud() {
        return origen_longitud;
    }

    public void setOrigen_longitud(double origen_longitud) {
        this.origen_longitud = origen_longitud;
    }

    public double getDestino_latitud() {
        return destino_latitud;
    }

    public void setDestino_latitud(double destino_latitud) {
        this.destino_latitud = destino_latitud;
    }

    public double getDestino_longitud() {
        return destino_longitud;
    }

    public void setDestino_longitud(double destino_longitud) {
        this.destino_longitud = destino_longitud;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
