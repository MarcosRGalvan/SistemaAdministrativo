package mx.com.marcoramirezg.services;

import mx.com.marcoramirezg.dto.UsuarioDTO;
import mx.com.marcoramirezg.entity.Usuario;
import mx.com.marcoramirezg.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    private UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<UsuarioDTO> buscarPorUsuarioId(String id) {
        return usuarioRepository.findByUsuarioid(id)
                .map(this::convertToDTO);
    }


    public Optional<UsuarioDTO> buscarPorEmpleadoId(Long id) {
        return usuarioRepository.findByEmpleadoid(id)
                .map(this::convertToDTO);
    }


    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getUsuarioid(),
                usuario.getEmpleadoid(),
                usuario.getFechaalta(),
                usuario.getFechabaja(),
                usuario.getNombre(),
                usuario.getEstado()
        );
    }
}
