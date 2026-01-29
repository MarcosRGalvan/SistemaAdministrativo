package mx.com.marcoramirezg.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class EmpleadoDTO {

    private Long empleadoid;
    private String nombre;
    private String apaterno;
    private String amaterno;
    private String email;
    private Date fechaingreso;
    private Date fechabaja;
    private String jefe;
    private String titulo;
    private String depto;

    public EmpleadoDTO(Long empleadoid, String nombre,String apaterno, String amaterno, String email, Date fechaingreso, Date fechabaja, String jefe, String titulo, String depto) {
        this.empleadoid = empleadoid;
        this.nombre = nombre;
        this.apaterno = apaterno;
        this.amaterno = amaterno;
        this.email = email;
        this.fechaingreso = fechaingreso;
        this.fechabaja = fechabaja;
        this.jefe = jefe;
        this.titulo = titulo;
        this.depto = depto;
    }
}
