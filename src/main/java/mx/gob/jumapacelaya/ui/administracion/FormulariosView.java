package mx.gob.jumapacelaya.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import mx.gob.jumapacelaya.dto.FormularioDTO;
import mx.gob.jumapacelaya.entity.Formulario;
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
    private final TextField txtNomForm = new TextField("Nombre:");
    private final TextField txtClaseForm = new TextField("Clase:");
    private final TextField txtParametrosForm = new TextField("Parametros:");
    private final Checkbox chkEstadoForm = new Checkbox("ACTIVO");
    private final Button btnGuardarForm = new Button("Guardar");
    private final Dialog addFormDialog = new Dialog();
    private Long editingForm = null;
    Button btnNuevoForm;

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
        HorizontalLayout paramLyt = new HorizontalLayout(txtParametrosForm, chkEstadoForm);
        paramLyt.setAlignItems(Alignment.BASELINE);
        chkEstadoForm.setValue(true);
        txtParametrosForm.setPlaceholder("Opcional");

        HorizontalLayout btnGuardarFormLyt = new HorizontalLayout(btnGuardarForm);
        btnGuardarForm.setThemeName("primary");
        btnGuardarForm.addClickListener(e -> saveForm());
        btnGuardarFormLyt.setWidthFull();
        btnGuardarFormLyt.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(txtNomForm, txtClaseForm, paramLyt, btnGuardarFormLyt);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.START);

        txtNomForm.setRequired(true);
        txtNomForm.setWidth("250px");
        txtClaseForm.setRequired(true);
        txtClaseForm.setWidth("450px");

        addFormDialog.setHeaderTitle("Agregar nuevo formulario");
        addFormDialog.add(layout);

        btnNuevoForm = new Button(VaadinIcon.PLUS.create());
        btnNuevoForm.setTooltipText("Agregar nuevo formulario");
        btnNuevoForm.addClickListener(e -> addFormDialog.open());
        return new ToolbarComponent(btnNuevoForm);
    }

    private void saveForm() {
        if (txtNomForm.isEmpty() || txtClaseForm.isEmpty()) {
            Notification.show("Por favor, llene los campos obligatorios.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        Formulario formulario;
        if (this.editingForm != null) {
            formulario = formularioRepository.findById(editingForm).orElse(new Formulario());
        } else {
            formulario = new Formulario();
        }

        try {
            formulario.setDescripcion(txtNomForm.getValue().toUpperCase());
            formulario.setClase(txtClaseForm.getValue());
            formulario.setParametros(txtParametrosForm.getValue());
            formulario.setEstado(chkEstadoForm.getValue() ? "A" : "I");

            formularioRepository.save(formulario);

            Notification.show("¡Formulario guardado exitosamente!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            this.editingForm = null;
            addFormDialog.close();
            updateList();
            txtNomForm.clear();
            txtClaseForm.clear();
            txtParametrosForm.clear();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Notification.show("Error al guardar: " + e.getMessage());
        }
    }

    private void deleteForm(FormularioDTO dto) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Formulario");
        dialog.setText("¿Está seguro que quiere eliminar el formulario?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");

        dialog.setConfirmText("Eliminar");
        dialog.addConfirmListener(event -> {
            if (dto.getFormularioid() == null) return;
            try {
                formularioRepository.deleteById(dto.getFormularioid().longValue());
                Notification.show("Formulario eliminado exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                updateList();
            } catch (Exception e) {
                Notification.show("No se puede eliminar: el registro esta siendo usado.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        dialog.open();
    }

    private void editForm(FormularioDTO dto) {
        Formulario formulario = formularioRepository.findById(dto.getFormularioid().longValue()).orElse(null);

        if (formulario != null) {
            this.editingForm = formulario.getFormularioid();

            txtNomForm.setValue(formulario.getDescripcion());
            txtClaseForm.setValue(formulario.getClase() != null ? formulario.getClase() : "");
            txtParametrosForm.setValue(formulario.getParametros() != null ? formulario.getParametros() : "");
            chkEstadoForm.setValue("A".equals(formulario.getEstado()));

            addFormDialog.setHeaderTitle("Editar formulario: " + formulario.getDescripcion());
            addFormDialog.open();
        }
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
        editButton.addClickListener(e -> editForm(formularioDTO));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> deleteForm(formularioDTO));

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
