package mx.com.marcoramirezg.repositories;

import mx.com.marcoramirezg.dto.EmpleadoDTO;
import mx.com.marcoramirezg.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    //Obtener todos los empleados
    @Query("""
        SELECT NEW mx.com.marcoramirezg.dto.EmpleadoDTO(
            e.empleadoid,
        	e.nombre,
        	e.apaterno,
        	e.apmaterno,
        	e.email,
        	e.fechaalta,
        	e.fechabaja,
        	CONCAT( j.nombre, ' ', j.apaterno, ' ', j.apmaterno),
        	t.descripcion,
        	d.descripcion
        )
        FROM Empleado e
        JOIN e.tituloid t
        JOIN e.departamento d
        LEFT JOIN e.jefe j
    """)
    List<EmpleadoDTO> findAllEmpleados();


    //Obtener un empleado por su ID
    @Query("""
    SELECT new mx.com.marcoramirezg.dto.EmpleadoDTO(
        e.empleadoid,
        e.nombre,
        e.apaterno,
        e.apmaterno,
        e.email,
        e.fechaalta,
        e.fechabaja,
        CONCAT(j.nombre, ' ', j.apaterno, ' ', j.apmaterno),
        t.descripcion,
        d.descripcion
    )
    FROM Empleado e
    JOIN e.tituloid t
    JOIN e.departamento d
    LEFT JOIN e.jefe j
    WHERE e.empleadoid = :id
    """)
    Optional<EmpleadoDTO> findEmpleadoById(@Param("id") Long id);

}
