package com.rldevel.helpers;

import com.google.android.gms.maps.GoogleMap;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by root on 28/12/17.
 */

public class RouteDefinition {

    private UUID identificador;
    private String usuarioCreador;
    private Date fechaEjecucion;
    private Date fechaFinalizacion;
    private Date fechaCreacion;
    private String nombreRuta;
    private GoogleMap googleMap;
    private PolyLineHelper polyLineHelper = new PolyLineHelper();
    private CustomArrayList<CustomMarker> colleccionMarcadores = new CustomArrayList<>();

    public RouteDefinition(GoogleMap googleMap){ this.googleMap = googleMap;}

    public RouteDefinition(UUID identificador){
        this.identificador = identificador;
    }

    //region Getter and setters

    public PolyLineHelper getPolyLineHelper() {
        return polyLineHelper;
    }

    public void setPolyLineHelper(PolyLineHelper polyLineHelper) {
        this.polyLineHelper = polyLineHelper;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public UUID getIdentificador () {
        return identificador;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public CustomArrayList<CustomMarker> getColleccionMarcadores() {
        return colleccionMarcadores;
    }

    //endregion

}
