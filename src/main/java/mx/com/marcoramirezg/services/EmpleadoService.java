package mx.com.marcoramirezg.services;

import mx.com.marcoramirezg.dto.EmpleadoDTO;
import mx.com.marcoramirezg.repositories.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public List<EmpleadoDTO> findAll() {
        return repository.findAllEmpleados();
    }

    public Optional<EmpleadoDTO> buscarPorNumero(Long empleadoId) {
        return repository.findEmpleadoById(empleadoId);
    }
}
