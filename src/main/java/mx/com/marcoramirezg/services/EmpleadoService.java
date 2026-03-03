package mx.com.marcoramirezg.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import mx.com.marcoramirezg.dto.EmpleadoDTO;
import mx.com.marcoramirezg.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    @Value("${app.fotos.path}")
    private String rutaFotos;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        File directorio = new File(rutaFotos);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    public List<EmpleadoDTO> findAll() {
        return repository.findAllEmpleados();
    }

    public Optional<EmpleadoDTO> buscarPorNumero(Long empleadoId) {
        return repository.findEmpleadoById(empleadoId);
    }

    @Transactional
    public void guardarNombreFoto(Long id, String nombreArchivo) {
        repository.findById(id).ifPresent(e -> {
            e.setFotoNombre(nombreArchivo);
            repository.save(e);
        });
    }

    public String getRutaFotos() {
        return rutaFotos.endsWith(File.separator) ? rutaFotos : rutaFotos + File.separator;
    }
}
