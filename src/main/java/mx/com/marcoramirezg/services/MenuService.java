package mx.com.marcoramirezg.services;

import mx.com.marcoramirezg.entity.Formulario;
import mx.com.marcoramirezg.entity.MenuItem;
import mx.com.marcoramirezg.repositories.FormularioRepository;
import mx.com.marcoramirezg.repositories.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final FormularioRepository formularioRepository;

    public MenuService(MenuItemRepository menuItemRepository, FormularioRepository formularioRepository) {
        this.menuItemRepository = menuItemRepository;
        this.formularioRepository = formularioRepository;
    }

    public List<MenuItem> getMenuItems() {
        return menuItemRepository.findAllByOrderByOrdenmenuAsc();
    }

    public Optional<String> getClasePorFormularioId(Long formularioiId) {
        if (formularioiId == null || formularioiId.equals(0L)) {
            return Optional.empty();
        }

        return formularioRepository.findById(formularioiId)
                .filter(f -> "A".equalsIgnoreCase(f.getEstado()))
                .map(Formulario::getClase);
    }

}
