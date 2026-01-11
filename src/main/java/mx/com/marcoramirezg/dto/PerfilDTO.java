package mx.com.marcoramirezg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PerfilDTO {

    private Long perfilid;
    private String descripcion;
    private String estado;

    public PerfilDTO(Long perfilid, String descripcion, String estado) {
        this.perfilid = perfilid;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
