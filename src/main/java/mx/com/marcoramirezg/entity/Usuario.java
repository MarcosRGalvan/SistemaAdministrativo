package mx.com.marcoramirezg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
@Table(name = "USUARIOS", schema = "ADMIN")
public class Usuario {

    @Id
    @Column(name = "USUARIOID")
    private String usuarioid;

    @Column(name = "EMPLEADOID")
    private Long empleadoid;

    @Column(name = "FECHAALTA")
    private Date fechaalta;

    @Column(name = "FECHABAJA")
    private Date fechabaja;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ESTADO")
    private String estado;
}
