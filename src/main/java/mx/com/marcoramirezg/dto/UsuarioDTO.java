package mx.com.marcoramirezg.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class UsuarioDTO {

    private String usuarioid;
    private Long empleadoid;
    private Date fechaalta;
    private Date fechabaja;
    private String nombre;
    private String estado;

    public UsuarioDTO(String usuarioid, Long empleadoid, Date fechaalta, Date fechabaja, String nombre, String estado) {
        this.usuarioid = usuarioid;
        this.empleadoid = empleadoid;
        this.fechaalta = fechaalta;
        this.fechabaja = fechabaja;
        this.nombre = nombre;
        this.estado = estado;
    }
}
