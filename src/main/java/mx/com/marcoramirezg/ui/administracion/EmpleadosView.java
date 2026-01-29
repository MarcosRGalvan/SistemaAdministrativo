package mx.com.marcoramirezg.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Dial;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import mx.com.marcoramirezg.dto.EmpleadoDTO;
import mx.com.marcoramirezg.services.EmpleadoService;
import mx.com.marcoramirezg.ui.MainLayout;
import mx.com.marcoramirezg.ui.components.ToolbarComponent;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@PageTitle("Empleados")
@Route(value = "empleados", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmpleadosView extends VerticalLayout {

    private NumberField txtNoEmpleado;
    private TextField txtNombre;
    private TextField txtApPaterno;
    private TextField txtApMaterno;
    private TextField txtFechaIngreso;
    private TextField txtFechaBaja;
    private TextField txtTitulo;
    private TextField txtDepartamento;
    private TextField txtJefe;
    private Button btnListaUsuarios;
    private Button btnLimpiar;
    private TextField txtEmail;

    private static final DateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    private final EmpleadoService empleadoService;

    public EmpleadosView(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
        configureView();
        add(createToolbar(), createContent());
    }

    /* ---------------- CONFIGURACIÓN GENERAL ---------------- */

    private void configureView() {
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        setSizeFull();
    }

    /* ---------------- TOOLBAR ---------------- */

    private Component createToolbar() {
        btnListaUsuarios = new Button("Buscar", VaadinIcon.USER.create());
        btnListaUsuarios.addClickListener(e -> mostrarListaEmpleados());

        btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());
        btnLimpiar.addClickListener(e -> limpiarFormulario());

        return new ToolbarComponent(btnListaUsuarios,btnLimpiar);
    }

    /* ---------------- CONTENIDO ---------------- */

    private Component createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setMargin(true);
        content.setSpacing(true);

        content.add(createForm());

        return content;
    }

    /* ---------------- FORMULARIO ---------------- */

    private Component createForm() {
        txtNoEmpleado = new NumberField("No. de empleado");
        txtNoEmpleado.addKeyPressListener(Key.ENTER, e -> buscarEmpleado());

        txtFechaIngreso = new TextField("Fecha de ingreso");
        txtFechaBaja = new TextField("Fecha de baja");

        txtNombre = new TextField("Nombre(s)");
        txtNombre.setWidth("60%");
        txtTitulo = new TextField("Título");

        txtApPaterno = new TextField("Apellido paterno");
        txtApMaterno = new TextField("Apellido materno");

        txtDepartamento = new TextField("Departamento");
        txtDepartamento.setWidth("60%");

        txtJefe = new TextField("Jefe");
        txtJefe.setWidth("40%");

        txtEmail = new TextField("Email");
        txtEmail.setWidthFull();

        HorizontalLayout numemplLyt = new HorizontalLayout(txtNoEmpleado);
        numemplLyt.setWidthFull();

        HorizontalLayout nombreLyt = new HorizontalLayout(txtNombre, txtTitulo);
        nombreLyt.setWidthFull();

        HorizontalLayout apellidosLyt = new HorizontalLayout(txtApPaterno, txtApMaterno);
        apellidosLyt.setWidthFull();
        apellidosLyt.setFlexGrow(1, txtApPaterno, txtApMaterno);

        HorizontalLayout fechasLyt = new HorizontalLayout(txtFechaIngreso, txtFechaBaja, txtEmail);
        fechasLyt.setWidthFull();

        HorizontalLayout deptoLyt = new HorizontalLayout(txtDepartamento, txtJefe);
        deptoLyt.setWidthFull();

        VerticalLayout card = new VerticalLayout(
                numemplLyt,
                nombreLyt,
                apellidosLyt,
                fechasLyt,
                deptoLyt
        );

        card.getStyle()
                .set("border-radius", "10px")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.2)");

        return card;
    }

    /* ---------------- LLAMAMOS AL SERVICIO EMPLEADOSERVICE ---------------- */
    private void buscarEmpleado() {
        if (txtNoEmpleado.getValue() == null) {
            mostrarNotificacion(
                    "Debe ingresar un numero de empleado",
                    NotificationVariant.LUMO_WARNING,
                    VaadinIcon.WARNING
            );
            return;
        }

        Long id = txtNoEmpleado.getValue().longValue();

        empleadoService.buscarPorNumero(id)
                .ifPresentOrElse(
                        e -> {
                            llenarFormulario(e);
                            mostrarNotificacion(
                                    "Empleado cargado",
                                    NotificationVariant.LUMO_SUCCESS,
                                    VaadinIcon.CHECK
                            );
                        },
                        () -> mostrarNotificacion(
                                "Empleado no encontrado",
                                NotificationVariant.LUMO_ERROR,
                                VaadinIcon.CLOSE_CIRCLE
                        )
                );
    }

    /* ---------------- LLENAMOS LOS CAMPOS DEL FORMULARIO ---------------- */
    private void llenarFormulario(EmpleadoDTO e) {
        txtNoEmpleado.setEnabled(false);

        txtNombre.setValue(e.getNombre());
        txtNombre.setReadOnly(true);

        txtApPaterno.setValue(e.getApaterno());
        txtApPaterno.setReadOnly(true);

        txtApMaterno.setValue(e.getAmaterno());
        txtApMaterno.setReadOnly(true);

        txtTitulo.setValue(e.getTitulo());
        txtTitulo.setReadOnly(true);

        txtFechaIngreso.setValue(formatearFecha(e.getFechaingreso()));
        txtFechaIngreso.setReadOnly(true);

        txtFechaBaja.setValue(formatearFecha(e.getFechabaja()));
        txtFechaBaja.setReadOnly(true);

        txtDepartamento.setValue(e.getDepto());
        txtDepartamento.setReadOnly(true);

        txtEmail.setValue(e.getEmail());
        txtEmail.setReadOnly(true);

        txtJefe.setValue(e.getJefe());
        txtJefe.setReadOnly(true);
    }


    private void mostrarNotificacion(
            String mensaje,
            NotificationVariant variante,
            VaadinIcon icono
    ) {
        Notification notificacion = new Notification();
        notificacion.setDuration(3000);
        notificacion.setPosition(Notification.Position.TOP_CENTER);
        notificacion.addThemeVariants(variante);

        Icon icon = icono.create();
        icon.getStyle().set("margin-right", "8px");

        HorizontalLayout layout = new HorizontalLayout(icon, new Text(mensaje));
        layout.setJustifyContentMode(JustifyContentMode.CENTER);

        notificacion.add(layout);
        notificacion.open();
    }

    private String formatearFecha(Date fecha) {
        return fecha != null ? FORMATO_FECHA.format(fecha) : "";
    }

    private void limpiarFormulario() {
        txtNoEmpleado.clear();
        txtNoEmpleado.setEnabled(true);
        txtNombre.clear();
        txtNombre.setReadOnly(false);
        txtApPaterno.clear();
        txtApPaterno.setReadOnly(false);
        txtApMaterno.clear();
        txtApMaterno.setReadOnly(false);
        txtTitulo.clear();
        txtTitulo.setReadOnly(false);
        txtFechaIngreso.clear();
        txtFechaIngreso.setReadOnly(false);
        txtFechaBaja.clear();
        txtFechaBaja.setReadOnly(false);
        txtDepartamento.clear();
        txtDepartamento.setReadOnly(false);
        txtJefe.clear();
        txtJefe.setReadOnly(false);
        txtEmail.clear();
        txtEmail.setReadOnly(false);
    }

    private void mostrarListaEmpleados() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Todos los empleados");

        dialog.setWidth("90%");
        dialog.setHeight("80%");

        Grid<EmpleadoDTO> grid = new Grid<>(EmpleadoDTO.class, false);
        grid.addColumn(EmpleadoDTO::getEmpleadoid)
                .setHeader("No. empleado")
                .setAutoWidth(true);

        grid.addColumn(e ->
                e.getNombre() + " " +  e.getApaterno() + " " + e.getAmaterno()
        ).setHeader("Nombre")
                        .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getDepto)
                        .setHeader("Departamento")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getJefe)
                        .setHeader("Jefe")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getFechaingreso)
                        .setHeader("Fecha Ingreso")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getFechabaja)
                        .setHeader("Fecha Baja")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getTitulo)
                        .setHeader("Titulo")
                                .setAutoWidth(true);

        grid.setSizeFull();
        grid.setItems(empleadoService.findAll());

        grid.addItemDoubleClickListener(e -> {
            llenarFormulario(e.getItem());
            dialog.close();
        });

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        dialog.getFooter().add(btnCerrar);

        dialog.add(grid);
        dialog.open();
    }
}