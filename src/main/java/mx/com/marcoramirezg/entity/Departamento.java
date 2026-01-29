package mx.com.marcoramirezg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "DEPARTAMENTO", schema = "ADMIN")
public class Departamento {

    @Id
    @Column(name = "DEPARTAMENTOID", columnDefinition = "CHAR")
    private String departamentoid;

    @Column(name = "GERENCIAID")
    private String gerenciaid;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "ACTIVO")
    private String activo;

    @Column(name = "JEFEAREA")
    private Long jefearea;

    @Column(name = "DEPTO_ANT")
    private String deptoAnt;
}
