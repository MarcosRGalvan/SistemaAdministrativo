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
import mx.gob.jumapacelaya.dto.MenuDetalleDTO;
import mx.gob.jumapacelaya.entity.MenuItem;
import mx.gob.jumapacelaya.repositories.MenuItemRepository;
import mx.gob.jumapacelaya.ui.MainLayout;
import mx.gob.jumapacelaya.ui.components.ToolbarComponent;

import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Menús")
@Route(value = "menus", layout = MainLayout.class)
public class MenusView extends VerticalLayout {

    private final MenuItemRepository menuItemRepository;
    private final Grid<MenuDetalleDTO> grid = new Grid<>(MenuDetalleDTO.class, false);
    private ListDataProvider<MenuDetalleDTO> dataProvider;

    private final TextField descripcionFilter = new TextField();
    private final TextField estadoFilter = new TextField();
    private final TextField formularioFilter = new TextField();
    private final TextField padreFilter = new TextField();
    private final TextField tipoFilter = new TextField();

    public MenusView(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
        addClassName("menu-view");
        setSizeFull();
        setSpacing(true);
        setPadding(false);

        add(createToolbar());

        conrfigureGrid();
        add(grid);
        updateList();
    }

    private Component createToolbar() {
        Button btnNuevoMenu = new Button(VaadinIcon.PLUS.create());
        btnNuevoMenu.setTooltipText("Agregar nuevo menu");


        return new ToolbarComponent(btnNuevoMenu);
    }

    private void updateList() {
        this.dataProvider = new ListDataProvider<>(menuItemRepository.findMenuDetails());
       grid.setItems(this.dataProvider);
    }

    private void conrfigureGrid() {
        grid.removeAllColumns();
        grid.addClassNames("grid");

        Grid.Column<MenuDetalleDTO> menuCol =
            grid.addColumn(MenuDetalleDTO::getDescripcion).setHeader("Menú").setAutoWidth(true);
        Grid.Column<MenuDetalleDTO> estadoCol =
            grid.addColumn(MenuDetalleDTO::getEstado).setHeader("Estado").setAutoWidth(false);
        Grid.Column<MenuDetalleDTO> formCol =
            grid.addColumn(MenuDetalleDTO::getNombreFormulario).setHeader("Formulario").setAutoWidth(true);
        Grid.Column<MenuDetalleDTO> padreCol =
            grid.addColumn(MenuDetalleDTO::getNombrePadre).setHeader("Padre").setAutoWidth(true);
        Grid.Column<MenuDetalleDTO> tipoCol =
            grid.addColumn(MenuDetalleDTO::getTipo).setHeader("Tipo").setAutoWidth(false);

        Grid.Column<MenuDetalleDTO> actionsCol =
                grid.addComponentColumn(this::createActionsBtn)
                        .setFlexGrow(0)
                        .setWidth("150px")
                        .setFrozenToEnd(true);

        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(menuCol).setComponent(
                createFilterField(descripcionFilter, this::aplyFilter)
        );
        filterRow.getCell(estadoCol).setComponent(
                createFilterField(estadoFilter, this::aplyFilter)
        );
        filterRow.getCell(formCol).setComponent(
                createFilterField(formularioFilter, this::aplyFilter)
        );
        filterRow.getCell(padreCol).setComponent(
                createFilterField(padreFilter, this::aplyFilter)
        );
        filterRow.getCell(tipoCol).setComponent(
                createFilterField(tipoFilter, this::aplyFilter)
        );
    }

    private void aplyFilter(String ignore) {
        if (dataProvider == null) return;

        dataProvider.setFilter(menuDetalleDTO -> {
            String filterDesc = descripcionFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterEstado = estadoFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterForm = formularioFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterPadre = padreFilter.getValue().trim().toLowerCase(Locale.ROOT);
            String filterTipo = tipoFilter.getValue().trim().toLowerCase(Locale.ROOT);

            boolean matchesDesc = checkMatch(menuDetalleDTO.getDescripcion(), filterDesc);
            boolean matchesEstado = checkMatch(menuDetalleDTO.getEstado(), filterEstado);
            boolean matchesForm = checkMatch(menuDetalleDTO.getNombreFormulario(), filterForm);
            boolean matchesPadre = checkMatch(menuDetalleDTO.getNombrePadre(), filterPadre);
            boolean matchesTipo = checkMatch(menuDetalleDTO.getTipo(), filterTipo);

            return matchesDesc && matchesEstado && matchesForm && matchesPadre && matchesTipo;
        });
    }

    private boolean checkMatch(Object value, String filter) {
        if (value == null) {
            return filter.isEmpty();
        }

        return value.toString().toLowerCase(Locale.ROOT).contains(filter);
    }

    private Component createActionsBtn(MenuDetalleDTO menuDetalleDTO) {
        Button editButton = new Button(VaadinIcon.PENCIL.create());
        //editButton.setThemeName("primary small");
        editButton.addClickListener(e -> Notification.show("Editar " + menuDetalleDTO.getDescripcion()));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        //deleteButton.setThemeName("small");
        deleteButton.addClickListener(e -> Notification.show("Eliminar " + menuDetalleDTO.getDescripcion()));

        HorizontalLayout layout = new HorizontalLayout(editButton,deleteButton);
        layout.setSpacing(true);
        return layout;
    }

    private static Component createFilterField(TextField filed, Consumer<String> filterConsumerChange) {
        filed.setPlaceholder("Buscar...");
        filed.setValueChangeMode(ValueChangeMode.EAGER);
        filed.setClearButtonVisible(true);
        filed.setWidthFull();
        filed.addValueChangeListener(event -> filterConsumerChange.accept(event.getValue()));

        VerticalLayout layout = new VerticalLayout(filed);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        return layout;
    }
}
