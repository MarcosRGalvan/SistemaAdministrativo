package mx.com.marcoramirezg.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
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
import mx.com.marcoramirezg.dto.UsuarioDTO;
import mx.com.marcoramirezg.services.UsuarioService;
import mx.com.marcoramirezg.ui.MainLayout;
import mx.com.marcoramirezg.ui.components.ToolbarComponent;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@PageTitle("Usuarios")
@Route(value = "usuarios", layout = MainLayout.class)
@RolesAllowed( value = "ADMIN")
public class UsuarioView extends VerticalLayout {

    private Button btnBuscarUsr;
    private Button btnLimpiar;
    private NumberField txtNumEmpleado;
    private TextField txtNomEmpleado;
    private TextField txtUsuario;
    private DatePicker txtFechaAlta;
    private DatePicker txtFechaBaja;
    private H2 label;
    private Button btnActualizar;

    private static final DateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    private final UsuarioService usuarioService;

    public UsuarioView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        configureView();
        add(createToolbar(),createContent());
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
        btnBuscarUsr = new Button("Buscar", VaadinIcon.SEARCH.create());
        btnBuscarUsr.addClickListener(e -> buscarUsuario());

        btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());
        btnLimpiar.addClickListener(e -> limpiarFormulario());

        btnActualizar = new Button("Actualizar/Guardar", LineAwesomeIcon.SAVE.create());

        return new ToolbarComponent(btnBuscarUsr,btnLimpiar,btnActualizar);
    }

    /* ---------------- CONTENIDO ---------------- */
    private Component createContent() {
        VerticalLayout content = new VerticalLayout();
        //content.setPadding(true);
        //content.setMargin(true);
        //content.setSpacing(true);

        content.add(createForm());

        return content;
    }


    /* ---------------- FORMULARIO ---------------- */
    private Component createForm() {
        txtNumEmpleado = new NumberField("No. de empleado:");
        txtNumEmpleado.addKeyPressListener(Key.ENTER, e -> buscarUsuario());

        txtUsuario = new TextField("Usuario:");
        txtUsuario.addKeyPressListener(Key.ENTER, e -> buscarUsuario());

        txtNomEmpleado = new TextField("Nombre:");
        txtNomEmpleado.setWidth("60%");

        txtFechaAlta = new DatePicker("Fecha de alta:");

        txtFechaBaja = new DatePicker("Fecha baja:");


        HorizontalLayout numemplLyt = new HorizontalLayout(txtNumEmpleado,txtUsuario);

        HorizontalLayout nombreLyt = new HorizontalLayout(txtNomEmpleado);
        nombreLyt.setWidthFull();

        HorizontalLayout fechaLyt = new HorizontalLayout(txtFechaAlta, txtFechaBaja);

        VerticalLayout card = new VerticalLayout(
                label = new H2("Crear o Actualizar usuarios del sistema:"),
                numemplLyt,
                nombreLyt,
                fechaLyt
        );

        card.getStyle()
                .set("border-radius", "10px")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.2)");

        return card;
    }


    /* ---------------- LLAMAMOS AL SERVICIO USUARIOSERVICE ---------------- */
    private void buscarUsuario() {
        String usuarioInput = txtUsuario.getValue() != null ? txtUsuario.getValue().trim() : "";
        Double numEmpleadoInput = txtNumEmpleado.getValue();

        if (numEmpleadoInput == null && usuarioInput.isEmpty()) {
            mostrarNotificacion(
                    "Debe ingresar un numero de empleado o un nombre de usuario",
                    NotificationVariant.LUMO_WARNING,
                    VaadinIcon.WARNING
            );
            return;
        }

        if (numEmpleadoInput != null) {
            Long id = numEmpleadoInput.longValue();
            usuarioService.buscarPorEmpleadoId(id).ifPresentOrElse(
                    this::procesarExito,
                    () -> {
                        if (!usuarioInput.isEmpty()) {
                            buscarSoloPorUsuario(usuarioInput);
                        } else {
                            mostrarErrorBusqueda("ID Empleado: " + id);
                        }
                    }
            );
        }
        else {
            buscarSoloPorUsuario(usuarioInput);
        }
    }

    private void buscarSoloPorUsuario(String usr) {
        usuarioService.buscarPorUsuarioId(usr.toUpperCase()).ifPresentOrElse(
                this::procesarExito,
                () -> mostrarErrorBusqueda("Usuario: " + usr)
        );
    }

    private void procesarExito(UsuarioDTO u) {
        llenarFormulario(u);
        mostrarNotificacion("Usuario encontrado", NotificationVariant.LUMO_SUCCESS, VaadinIcon.CHECK);
    }

    private void mostrarErrorBusqueda(String detalles) {
        mostrarNotificacion("No se encotró información para " + detalles,
                NotificationVariant.LUMO_ERROR, VaadinIcon.CLOSE_CIRCLE);
    }

    private void llenarFormulario(UsuarioDTO u) {
        txtNumEmpleado.setReadOnly(true);

        txtNumEmpleado.setValue(Double.valueOf(u.getEmpleadoid()));
        txtUsuario.setValue(u.getUsuarioid());
        txtNomEmpleado.setValue(u.getNombre());
        txtFechaAlta.setValue(formatearFecha(u.getFechaalta()));
        txtFechaBaja.setValue(formatearFecha(u.getFechabaja()));
    }

    private LocalDate formatearFecha(Date fecha) {
        if (fecha == null) {
            return null;
        }

        return fecha.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
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

    private void limpiarFormulario() {
        txtNumEmpleado.clear();
        txtNumEmpleado.setReadOnly(false);
        txtUsuario.clear();
        txtNomEmpleado.clear();
        txtFechaAlta.clear();
        txtFechaBaja.clear();
    }
}
