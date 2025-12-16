package mx.gob.jumapacelaya.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import mx.gob.jumapacelaya.repositories.FormularioRepository;
import mx.gob.jumapacelaya.ui.MainLayout;
import mx.gob.jumapacelaya.ui.components.ToolbarComponent;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Formularios")
@Route(value = "forms", layout = MainLayout.class)
public class FormulariosView extends VerticalLayout {

    private final FormularioRepository formularioRepository;
    private final Grid<FormularioDTO> grid = new Grid<>(FormularioDTO.class, false);
    private ListDataProvider<FormularioDTO> dataProvider;


    // Filtros
    private final TextField descripcionFilter = new TextField();
    private final TextField claseFilter = new TextField();
    private final TextField parametrosFilter = new TextField();
    private final TextField estadoFilter = new TextField();

    public FormulariosView(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
        addClassName("formularios-view");
        setSizeFull();
        setPadding(false);
        add(createToolbar());

        configureGrid();
        add(grid);
        updateList();
    }

    private Component createToolbar() {
        Button btnNuevoForm = new Button(VaadinIcon.PLUS.create());
        btnNuevoForm.setTooltipText("Agregar nuevo formulario");

        return new ToolbarComponent(btnNuevoForm);
    }

    private void updateList() {
        this.dataProvider = new ListDataProvider<>(formularioRepository.findAllFormularios());
        grid.setItems(this.dataProvider);
    }

    private void configureGrid() {
        grid.addClassNames("grid");
        grid.removeAllColumns();

        Grid.Column<FormularioDTO> formCol =
                grid.addColumn(FormularioDTO::getDescripcion).setHeader("Formulario").setAutoWidth(true);
        Grid.Column<FormularioDTO> claseCol =
                grid.addColumn(FormularioDTO::getClase).setHeader("Clase").setAutoWidth(true);
        Grid.Column<FormularioDTO> paramCol =
                grid.addColumn(FormularioDTO::getParametros).setHeader("Parámetros").setAutoWidth(true);
        Grid.Column<FormularioDTO> estadoCol =
                grid.addColumn(FormularioDTO::getEstado).setHeader("Estado").setAutoWidth(false);

        Grid.Column<FormularioDTO> actionsCol =
                grid.addComponentColumn(this::createActionsBtn)
                        .setFlexGrow(0)
                        .setWidth("150px")
                        .setFrozenToEnd(true);

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Configuración de los filtros en la cabecera
        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(formCol).setComponent(
                createFilterField(descripcionFilter, this::applyFilter)
        );
        filterRow.getCell(claseCol).setComponent(
                createFilterField(claseFilter, this::applyFilter)
        );
        filterRow.getCell(paramCol).setComponent(
                createFilterField(parametrosFilter, this::applyFilter)
        );
        filterRow.getCell(estadoCol).setComponent(
                createFilterField(estadoFilter, this::applyFilter)
        );
        filterRow.getCell(actionsCol).setText("");
    }

    private void applyFilter(String ignore) {
        if (dataProvider == null) return;

        dataProvider.setFilter(formDTO -> {
            String filterDesc = descripcionFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterClase = claseFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterParam = parametrosFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterEstado = estadoFilter.getValue().trim().toLowerCase(Locale.ROOT);

            boolean matchesDesc = checkMatch(formDTO.getDescripcion(), filterDesc);
            boolean matchesClase = checkMatch(formDTO.getClase(), filterClase);
            boolean matchesParam = checkMatch(formDTO.getParametros(), filterParam);
            boolean matchesEstado = checkMatch(formDTO.getEstado(), filterEstado);

            return matchesDesc && matchesClase && matchesParam && matchesEstado;
        });
    }

    private boolean checkMatch(Object value, String filter) {
        if (value == null) {
            return filter.isEmpty();
        }

        return value.toString().toLowerCase(Locale.ROOT).contains(filter);
    }

    private Component createActionsBtn(FormularioDTO formularioDTO) {
        Button editButton = new Button(VaadinIcon.PENCIL.create());
        editButton.addClickListener(e -> Notification.show("Editar " + formularioDTO.getDescripcion()));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> Notification.show("Eliminar " + formularioDTO.getDescripcion()));

        HorizontalLayout layout = new HorizontalLayout(editButton, deleteButton);
        layout.setSpacing(true);
        return layout;
    }


    private static Component createFilterField(TextField field, Consumer<String> filterConsumerChange) {
        field.setPlaceholder("Buscar...");
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.setClearButtonVisible(true);
        field.setWidthFull();
        field.addValueChangeListener(event -> filterConsumerChange.accept(event.getValue()));

        VerticalLayout layout = new VerticalLayout(field);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        return layout;
    }

}
