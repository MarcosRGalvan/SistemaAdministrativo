package mx.gob.jumapacelaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "PERFILES", schema = "ADMIN")
public class Perfiles {

    @Id
    @Column(name = "PERFILID")
    private Long perfilid;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(columnDefinition = "CHAR")
    private String estado;
}
