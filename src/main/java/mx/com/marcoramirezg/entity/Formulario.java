package mx.com.marcoramirezg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "FORMULARIOS", schema = "ADMIN")
public class Formulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
