package mx.com.marcoramirezg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter @Setter
@Table(name = "EMPLEADOS", schema = "ADMIN")
public class Empleado {

    @Id
    @Column(name = "EMPLEADOID")
    private Long empleadoid;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "APATERNO")
    private String apaterno;

    @Column(name = "AMATERNO")
    private String apmaterno;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FECHALTA")
    private Date fechaalta;

    @Column(name = "FECHABAJA")
    private Date fechabaja;

    @ManyToOne
    @JoinColumn(name = "TITULOID")
    private Titulo tituloid;

    @ManyToOne
    @JoinColumn(name = "DEPARTAMENTOID", columnDefinition = "CHAR")
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "JEFEID")
    private Empleado jefe;
}
