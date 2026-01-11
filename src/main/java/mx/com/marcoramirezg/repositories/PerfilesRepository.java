package mx.com.marcoramirezg.repositories;

import mx.com.marcoramirezg.dto.PerfilDTO;
import mx.com.marcoramirezg.entity.Perfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfilesRepository extends JpaRepository<Perfiles, Long> {

    @Query("""
        SELECT NEW mx.com.marcoramirezg.dto.PerfilDTO(
            p.perfilid,
            p.descripcion,
            p.estado
        )
        FROM Perfiles p
        order by p.perfilid asc
    """)
    List<PerfilDTO> findAllPerfiles();
}
