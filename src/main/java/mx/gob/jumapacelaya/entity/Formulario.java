package mx.gob.jumapacelaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "FORMULARIOS", schema = "ADMIN")
public class Formulario {

    @Id
    @Column(name = "FORMULARIOID")
    private Long formularioid;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "CLASE")
    private String clase;

    @Column(name = "PARAMETROS")
    private String parametros;

    @Column(columnDefinition = "CHAR")
    private String estado;

}
