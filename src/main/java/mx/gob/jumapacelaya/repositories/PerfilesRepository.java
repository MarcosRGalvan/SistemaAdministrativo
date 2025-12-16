package mx.gob.jumapacelaya.repositories;

import mx.gob.jumapacelaya.dto.PerfilDTO;
import mx.gob.jumapacelaya.entity.Perfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfilesRepository extends JpaRepository<Perfiles, Long> {

    @Query("""
        SELECT NEW mx.gob.jumapacelaya.dto.PerfilDTO(
            p.perfilid,
            p.descripcion,
            p.estado
        )
        FROM Perfiles p
        order by p.perfilid asc
    """)
    List<PerfilDTO> findAllPerfiles();
}
