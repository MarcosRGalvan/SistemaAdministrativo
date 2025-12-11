package mx.gob.jumapacelaya.repositories;

import mx.gob.jumapacelaya.dto.FormularioDTO;
import mx.gob.jumapacelaya.entity.Formulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {

    @Query("""
        SELECT NEW mx.gob.jumapacelaya.dto.FormularioDTO(
            f.formularioid,
            f.descripcion,
            f.clase,
            f.parametros,
            f.estado
        )
        FROM Formulario f
        ORDER BY f.formularioid ASC
    """)
    List<FormularioDTO> findAllFormularios();
}
