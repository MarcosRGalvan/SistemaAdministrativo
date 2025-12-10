package mx.gob.jumapacelaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FORMULARIOS", schema = "ADMIN")
public class Formulario {

    @Id
    @Setter @Getter
    private Long formularioid;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private String clase;
    @Getter @Setter
    private String parametros;
    @Getter @Setter
    @Column(columnDefinition = "CHAR")
    private String estado;

}
