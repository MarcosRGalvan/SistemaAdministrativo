package mx.com.marcoramirezg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "TITULOS", schema = "ADMIN")
public class Titulo {

    @Id
    @Column(name = "TITULOID")
    private String tituloid;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
