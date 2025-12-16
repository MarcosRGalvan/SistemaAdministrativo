package mx.gob.jumapacelaya.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import mx.gob.jumapacelaya.dto.FormularioDTO;
import mx.gob.jumapacelaya.dto.PerfilDTO;
import mx.gob.jumapacelaya.repositories.PerfilesRepository;
import mx.gob.jumapacelaya.ui.MainLayout;
import mx.gob.jumapacelaya.ui.components.ToolbarComponent;

import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Perfiles")
@Route(value = "perfiles", layout = MainLayout.class)
public class PerfilesView extends VerticalLayout {

    private final PerfilesRepository perfilesRepository;
    private final Grid<PerfilDTO> grid = new Grid<>(PerfilDTO.class);
    private ListDataProvider<PerfilDTO> dataProvider;


    // Filtros
    private final TextField descripcionFilter = new TextField();
    private final TextField estadoFilter = new TextField();

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
        Button btnNuevoPerfil = new Button(VaadinIcon.PLUS.create());
        btnNuevoPerfil.setTooltipText("Agregar nuevo perfil");

        return new ToolbarComponent(btnNuevoPerfil);
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
        editButton.addClickListener(e -> Notification.show("Editar " + perfilDTO.getDescripcion()));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> Notification.show("Eliminar " + perfilDTO.getDescripcion()));

        HorizontalLayout layout = new HorizontalLayout(editButton, deleteButton);
        layout.setSpacing(true);
        return layout;
    }
}
