package mx.gob.jumapacelaya.services;

import mx.gob.jumapacelaya.entity.Formulario;
import mx.gob.jumapacelaya.entity.MenuItem;
import mx.gob.jumapacelaya.repositories.FormularioRepository;
import mx.gob.jumapacelaya.repositories.MenuItemRepository;
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
