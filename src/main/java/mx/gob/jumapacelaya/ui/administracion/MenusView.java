package mx.gob.jumapacelaya.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
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
import mx.gob.jumapacelaya.dto.MenuDetalleDTO;
import mx.gob.jumapacelaya.entity.Formulario;
import mx.gob.jumapacelaya.entity.MenuItem;
import mx.gob.jumapacelaya.repositories.FormularioRepository;
import mx.gob.jumapacelaya.repositories.MenuItemRepository;
import mx.gob.jumapacelaya.ui.MainLayout;
import mx.gob.jumapacelaya.ui.components.ToolbarComponent;
import org.aspectj.weaver.ast.Not;

import java.util.Locale;
import java.util.function.Consumer;

@PageTitle("Menús")
@Route(value = "menus", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class MenusView extends VerticalLayout {

    private final MenuItemRepository menuItemRepository;
    private final FormularioRepository formularioRepository;
    private final Grid<MenuDetalleDTO> grid = new Grid<>(MenuDetalleDTO.class, false);
    private ListDataProvider<MenuDetalleDTO> dataProvider;

    private final TextField descripcionFilter = new TextField();
    private final TextField estadoFilter = new TextField();
    private final TextField formularioFilter = new TextField();
    private final TextField padreFilter = new TextField();
    private final TextField tipoFilter = new TextField();
    private final Button btnNuevoMenu = new Button(VaadinIcon.PLUS.create());
    private final Dialog addMenuDialog = new Dialog();
    private TextField txtNomMenu = new TextField("Nombre:");
    private ComboBox<Formulario> cmbFormularios = new ComboBox<>("Formulario:");
    private ComboBox<MenuItem> cmbMenuPadre = new ComboBox<>("Padre:");
    private ComboBox<String> cmbTipoMenu = new ComboBox<>("Tipo:");
    private Button btnGuardarMenu = new Button("Guardar");
    private Checkbox checkbox = new Checkbox("Activo:");
    private TextField txtOrden = new TextField("Orden:");
    private Long editingMenu = null;

    public MenusView(MenuItemRepository menuItemRepository, FormularioRepository formularioRepository) {
        this.menuItemRepository = menuItemRepository;
        this.formularioRepository = formularioRepository;
        addClassName("menu-view");
        setSizeFull();
        setSpacing(true);
        setPadding(false);

        configureComboBoxes();
        conrfigureGrid();

        add(createToolbar(), grid);
        updateList();
    }

    private Component createToolbar() {
        /* --------- AGREGAR NUEVO MENÚ ---------- */
        txtNomMenu.setRequired(true);
        txtOrden.setRequired(true);
        txtOrden.setValue("0");
        txtOrden.setWidth("20%");
        cmbTipoMenu.setWidth("20%");
        checkbox.setValue(true);
        cmbFormularios.setWidthFull();
        cmbMenuPadre.setWidthFull();

        HorizontalLayout combosLyt = new HorizontalLayout();
        combosLyt.setWidthFull();
        combosLyt.add(cmbFormularios, cmbMenuPadre);
        HorizontalLayout btnsLyt = new HorizontalLayout();
        btnsLyt.setAlignItems(Alignment.BASELINE);
        btnsLyt.setJustifyContentMode(JustifyContentMode.START);
        btnsLyt.add(txtOrden, cmbTipoMenu, checkbox);
        HorizontalLayout btnGuardarMenuLyt = new HorizontalLayout(btnGuardarMenu);
        btnGuardarMenuLyt.setWidthFull();
        btnGuardarMenuLyt.setJustifyContentMode(JustifyContentMode.END);
        btnGuardarMenu.addClickListener(e -> saveMenu());
        btnGuardarMenu.setThemeName("primary");
        VerticalLayout layout = new VerticalLayout(txtNomMenu, combosLyt, btnsLyt, btnGuardarMenuLyt);
        layout.setSpacing(false);
        addMenuDialog.setHeaderTitle("Agregar nuevo menú");
        addMenuDialog.add(layout);


        btnNuevoMenu.setTooltipText("Agregar nuevo menu");
        btnNuevoMenu.addClickListener(e -> addMenuDialog.open());
        return new ToolbarComponent(btnNuevoMenu);
    }

    private void configureComboBoxes() {
        cmbFormularios.setItemLabelGenerator(Formulario::getDescripcion);
        cmbFormularios.setItems(formularioRepository.findAll());

        cmbMenuPadre.setItemLabelGenerator(MenuItem::getDescripcion);
        cmbMenuPadre.setItems(menuItemRepository.findAll());

        cmbTipoMenu.setItems("M", "S");
        cmbTipoMenu.setValue("M");
    }

    private void saveMenu() {
        if (txtNomMenu.isEmpty() || cmbTipoMenu.isEmpty()) {
            Notification.show("Por favor, llene los campos requeridos.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        MenuItem menu;
        if (this.editingMenu != null) {
            menu = menuItemRepository.findById(editingMenu).orElse(new MenuItem());
        } else {
            menu = new MenuItem();
        }

        try {
            menu.setDescripcion(txtNomMenu.getValue().toUpperCase());
            menu.setTipo(cmbTipoMenu.getValue());
            menu.setEstado(checkbox.getValue() ? "A" : "I");

            Long orden = txtOrden.getValue().isEmpty() ? 0L : Long.parseLong(txtOrden.getValue());
            menu.setOrdenmenu(orden);

            if (cmbFormularios.getValue() != null) {
                menu.setFormularioid(cmbFormularios.getValue().getFormularioid());
            } else {
                menu.setFormularioid(null);
            }

            if (cmbMenuPadre.getValue() != null) {
                menu.setPadreid(cmbMenuPadre.getValue().getMenuid());
            } else {
                menu.setPadreid(null);
            }

            menuItemRepository.save(menu);

            Notification.show("¡Menú guardado exitosamente!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            this.editingMenu = null;
            addMenuDialog.close();
            updateList();
            clearForm();
        } catch (NumberFormatException e) {
            Notification.show("El campo 'Orden' debe ser un número válido.");
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
        }
    }

    private void deleteMenu(MenuDetalleDTO dto) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Menú");
        dialog.setText("¿Está seguro que quiere eliminar el menú?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");

        dialog.setConfirmText("Eliminar");
        dialog.addConfirmListener(event -> {
            if (dto.getMenuid() == null) return;
            try {
                menuItemRepository.deleteById(dto.getMenuid().longValue());
                Notification.show("Menú eliminado exitosamente.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                updateList();
            } catch (Exception e) {
                Notification.show("No se puede eliminar: el registro está siendo usado.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        dialog.open();
    }

    private void editMenu(MenuDetalleDTO dto) {
        MenuItem menu = menuItemRepository.findById(dto.getMenuid().longValue()).orElse(null);

        if (menu != null) {
            this.editingMenu = menu.getMenuid();

            txtNomMenu.setValue(menu.getDescripcion());
            txtOrden.setValue(String.valueOf(menu.getOrdenmenu()));
            cmbTipoMenu.setValue(menu.getTipo());
            checkbox.setValue("A".equals(menu.getEstado()));

            if (menu.getFormularioid() != null) {
                formularioRepository.findById(menu.getFormularioid())
                        .ifPresent(cmbFormularios::setValue);
            } else {
                cmbFormularios.clear();
            }
            if (menu.getPadreid() != null) {
                menuItemRepository.findById(menu.getPadreid())
                        .ifPresent(cmbMenuPadre::setValue);
            } else {
                cmbMenuPadre.clear();
            }

            addMenuDialog.setHeaderTitle("Editar menú: " + menu.getDescripcion());
            addMenuDialog.open();
        }
    }

    private void clearForm() {
        txtNomMenu.clear();
        cmbFormularios.clear();
        cmbMenuPadre.clear();
        cmbTipoMenu.clear();
        checkbox.setValue(true);
        txtOrden.clear();
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
        editButton.addClickListener(e -> editMenu(menuDetalleDTO));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        //deleteButton.setThemeName("small");
        deleteButton.addClickListener(e -> deleteMenu(menuDetalleDTO));

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
