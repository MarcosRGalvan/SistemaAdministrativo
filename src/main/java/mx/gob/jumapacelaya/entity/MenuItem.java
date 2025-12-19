package mx.gob.jumapacelaya.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MENU", schema = "ADMIN")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuid;

    private String descripcion;
    private String estado;
    private Long formularioid;
    private Long padreid;
    private Long ordenmenu;
    @Column(columnDefinition = "CHAR")
    private String tipo;
    private String icono;

    public Long getMenuid() {
        return menuid;
    }

    public void setMenuid(Long menuid) {
        this.menuid = menuid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getFormularioid() {
        return formularioid;
    }

    public void setFormularioid(Long formularioid) {
        this.formularioid = formularioid;
    }

    public Long getPadreid() {
        return padreid;
    }

    public void setPadreid(Long padreid) {
        this.padreid = padreid;
    }

    public Long getOrdenmenu() {
        return ordenmenu;
    }

    public void setOrdenmenu(Long ordenmenu) {
        this.ordenmenu = ordenmenu;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}
