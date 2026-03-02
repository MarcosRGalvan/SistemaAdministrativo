package mx.com.marcoramirezg.repositories;

import mx.com.marcoramirezg.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Buscar por usuarioID
    Optional<Usuario> findByUsuarioid(String usuarioid);

    // Buscar por empleadoid
    Optional<Usuario> findByEmpleadoid(Long empleadoid);

    // Buscar por empleadoid o usuarioid
    Optional<Usuario> findByUsuarioidOrEmpleadoid(String usuarioid, Long empleadoid);
}
