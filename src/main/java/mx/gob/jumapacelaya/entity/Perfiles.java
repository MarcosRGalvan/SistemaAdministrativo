package mx.gob.jumapacelaya.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "PERFILES", schema = "ADMIN")
public class Perfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long perfilid;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(columnDefinition = "CHAR")
    private String estado;
}
