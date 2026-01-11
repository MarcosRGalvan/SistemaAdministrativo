package mx.com.marcoramirezg.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class MenuDetalleDTO {

    private BigDecimal menuid;
    private String descripcion;
    private Character estado;
    private String nombreFormulario;
    private String nombrePadre;
    private BigDecimal ordenMenu;
    private Character tipo;

    public MenuDetalleDTO(BigDecimal menuid, String descripcion, Character estado, String nombreFomrulario,
                          String nombrePadre, BigDecimal ordenMenu, Character tipo) {

        this.menuid = menuid;
        this.descripcion = descripcion;
        this.estado = estado;
        this.nombreFormulario = (nombreFomrulario != null) ? nombreFomrulario : "Sin formulario asignado";
        this.nombrePadre = (nombrePadre != null) ? nombrePadre : "N/A";
        this.ordenMenu = ordenMenu;
        this.tipo = tipo;
    }

    public BigDecimal getMenuid() {
        return menuid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Character getEstado() {
        return estado;
    }

    public String getNombreFormulario() {
        return nombreFormulario;
    }

    public String getNombrePadre() {
        return nombrePadre;
    }

    public BigDecimal getOrdenMenu() {
        return ordenMenu;
    }

    public Character getTipo() {
        return tipo;
    }
}
