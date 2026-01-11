package mx.com.marcoramirezg.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import mx.com.marcoramirezg.dto.PerfilDTO;
import mx.com.marcoramirezg.entity.Perfiles;
import mx.com.marcoramirezg.repositories.PerfilesRepository;
import mx.com.marcoramirezg.ui.MainLayout;
import mx.com.marcoramirezg.ui.components.ToolbarComponent;

import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Perfiles")
@Route(value = "perfiles", layout = MainLayout.class)
@RolesAllowed( value = "ADMIN")
public class PerfilesView extends VerticalLayout {

    private final PerfilesRepository perfilesRepository;
    private final Grid<PerfilDTO> grid = new Grid<>(PerfilDTO.class);
    private ListDataProvider<PerfilDTO> dataProvider;

    // Filtros
    private final TextField descripcionFilter = new TextField();
    private final TextField estadoFilter = new TextField();
    private final TextField txtNomPerfil = new TextField("Nombre:");
    private final Checkbox chkEstadoPerfil = new Checkbox("ACTIVO");
    private final Button btnGuardarPerfil = new Button("Guardar");
    private final Dialog addPerfilDialog = new Dialog();
    private Long editingPerfil = null;

    public PerfilesView(PerfilesRepository perfilesRepository) {
        this.perfilesRepository = perfilesRepository;
        addClassName("perfiles-view");
        setSizeFull();
        setPadding(false);

        add(createToolbar());

        configureGrid();
        add(grid);
        updateList();
    }

    private Component createToolbar() {
        /* ------------- AGREGAR NUEVO PERFIL -------------- */
        txtNomPerfil.setRequired(true);
        chkEstadoPerfil.setValue(true);

        btnGuardarPerfil.setThemeName("primary");
        btnGuardarPerfil.addClickListener(e -> savePerfil());

        HorizontalLayout btnsLyt = new HorizontalLayout(txtNomPerfil, chkEstadoPerfil);
        btnsLyt.setAlignItems(Alignment.BASELINE);
        btnsLyt.setWidthFull();

        HorizontalLayout btnGuardarPerfilLyt = new HorizontalLayout(btnGuardarPerfil);
        btnGuardarPerfilLyt.setWidthFull();
        btnGuardarPerfilLyt.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(btnsLyt, btnGuardarPerfilLyt);
        layout.setSpacing(true);
        addPerfilDialog.setHeaderTitle("Agregar nuevo perfil");
        addPerfilDialog.add(layout);

        Button btnNuevoPerfil = new Button(VaadinIcon.PLUS.create());
        btnNuevoPerfil.setTooltipText("Agregar nuevo perfil");
        btnNuevoPerfil.addClickListener(e -> addPerfilDialog.open());
        return new ToolbarComponent(btnNuevoPerfil);
    }

    private void savePerfil() {
        if (txtNomPerfil.isEmpty()) {
            Notification.show("Debe ingresar un nombre para el perfil", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        Perfiles perfil;
        if (editingPerfil != null) {
            perfil = perfilesRepository.findById(editingPerfil).orElse(new Perfiles());
        } else {
            perfil = new Perfiles();
        }

        try {
            perfil.setDescripcion(txtNomPerfil.getValue().toUpperCase());
            perfil.setEstado(chkEstadoPerfil.getValue() ? "A" : "I");

            perfilesRepository.save(perfil);
            Notification.show("¡Perfil guardado exitosamente!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            this.editingPerfil = null;
            addPerfilDialog.close();
            updateList();
            txtNomPerfil.clear();
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage());
        }
    }

    private void deltePerfil(PerfilDTO perfilDTO) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Perfil");
        dialog.setText("¿Está seguro que quiere eliminar el perfil " + perfilDTO.getDescripcion() + "?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");

        dialog.setConfirmText("Eliminar");
        dialog.addConfirmListener(event -> {
            if (perfilDTO.getPerfilid() == null) return;
            try {
                perfilesRepository.deleteById(perfilDTO.getPerfilid().longValue());
                Notification.show("Perfil eliminado exitosamente.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                updateList();
            } catch (Exception e) {
                Notification.show("No se puede eliminar el perfil: el registro está siendo usado.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        dialog.open();
    }

    private void editarPerfil(PerfilDTO perfilDTO) {
        Perfiles perfil = perfilesRepository.findById(perfilDTO.getPerfilid().longValue()).orElse(null);

        if (perfil != null) {
            this.editingPerfil = perfil.getPerfilid();

            txtNomPerfil.setValue(perfil.getDescripcion());
            chkEstadoPerfil.setValue("A".equals(perfil.getEstado()));

            addPerfilDialog.setHeaderTitle("Editar perfil: " + perfil.getDescripcion());
            addPerfilDialog.open();
        }
    }

    private void updateList() {
        this.dataProvider = new ListDataProvider<>(perfilesRepository.findAllPerfiles());
        grid.setItems(this.dataProvider);
    }



    private void configureGrid() {
        grid.addClassNames("grid");
        grid.removeAllColumns();

        Grid.Column<PerfilDTO> perfilCol =
                grid.addColumn(PerfilDTO::getDescripcion).setHeader("Perfil").setAutoWidth(true);
        Grid.Column<PerfilDTO> estadoCol =
                grid.addColumn(PerfilDTO::getEstado).setHeader("Estado").setAutoWidth(false);

        Grid.Column<PerfilDTO> actionsCol =
                grid.addComponentColumn(this::createActionsBtn)
                        .setFlexGrow(0)
                        .setWidth("150px")
                        .setFrozenToEnd(true);

        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(perfilCol).setComponent(
                createFilterField(descripcionFilter, this::aplyFilter)
        );
        filterRow.getCell(estadoCol).setComponent(
                createFilterField(estadoFilter, this::aplyFilter)
        );
        filterRow.getCell(actionsCol).setText("");
    }

    private void aplyFilter(String ignore) {
        if (dataProvider == null) return;

        dataProvider.setFilter(perfilDTO -> {
            String filterDesc = descripcionFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterEstado = estadoFilter.getValue().trim().toLowerCase(Locale.ROOT);

            boolean matchesDesc = checkMath(perfilDTO.getDescripcion(), filterDesc);
            boolean matchesEst = checkMath(perfilDTO.getEstado(), filterEstado);

            return matchesDesc && matchesEst;
        });
    }

    private boolean checkMath(Object value, String filter) {
        if (value == null) {
            return filter.isEmpty();
        }

        return value.toString().toLowerCase(Locale.ROOT).contains(filter);
    }

    private Component createFilterField(TextField field, Consumer<String> filterConsumerChange) {
        field.setPlaceholder("Buscar...");
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.setClearButtonVisible(true);
        field.setWidthFull();
        field.addValueChangeListener(e -> filterConsumerChange.accept(e.getValue()));

        VerticalLayout layout = new VerticalLayout(field);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        return layout;
    }

    private Component createActionsBtn(PerfilDTO perfilDTO) {
        Button editButton = new Button(VaadinIcon.PENCIL.create());
        editButton.addClickListener(e -> editarPerfil(perfilDTO));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> deltePerfil(perfilDTO));

        HorizontalLayout layout = new HorizontalLayout(editButton, deleteButton);
        layout.setSpacing(true);
        return layout;
    }
}
