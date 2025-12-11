package mx.gob.jumapacelaya.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class FormularioDTO {

    private Long formularioid;
    private String descripcion;
    private String clase;
    private String parametros;
    private String estado;

    public FormularioDTO(Long formularioid, String descripcion, String clase, String parametros, String estado) {
        this.formularioid = formularioid;
        this.descripcion = descripcion;
        this.clase = clase;
        this.parametros = parametros;
        this.estado = estado;
    }
}
