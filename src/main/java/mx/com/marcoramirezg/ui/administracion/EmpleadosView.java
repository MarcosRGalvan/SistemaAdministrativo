package mx.com.marcoramirezg.ui.administracion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import jakarta.annotation.security.RolesAllowed;
import mx.com.marcoramirezg.dto.EmpleadoDTO;
import mx.com.marcoramirezg.services.EmpleadoService;
import mx.com.marcoramirezg.ui.MainLayout;
import mx.com.marcoramirezg.ui.components.ToolbarComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextField txtEmail;
    private H2 label;
    private Image ftoEmpl;

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
        Button btnListaUsuarios = new Button("Lista empleados", VaadinIcon.USER.create());
        btnListaUsuarios.addClickListener(e -> mostrarListaEmpleados());

        Button btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());
        btnLimpiar.addClickListener(e -> limpiarFormulario());

        Button btnBuscar = new Button("Buscar", VaadinIcon.SEARCH.create());
        btnBuscar.addClickListener(e -> buscarEmpleado());

        Button btnSubir = new Button("Subir Foto", VaadinIcon.CAMERA.create());
        btnSubir.addClickListener(e -> abrirDialogoSubida());

        ftoEmpl = new Image();
        ftoEmpl.setVisible(false);
        ftoEmpl.setHeight("120px");
        ftoEmpl.setWidth("120px");

        ftoEmpl.getStyle()
                .set("top", "20px")
                .set("right", "20px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "8px")
                .set("z-index", "10")
                .set("position", "absolute");

        return new ToolbarComponent(btnBuscar, btnLimpiar, btnListaUsuarios, btnSubir);
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

        ftoEmpl.setHeight("150px");
        ftoEmpl.setWidth("150px");
        ftoEmpl.getStyle()
                .set("top", "30px")
                .set("right", "30px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "8px")
                .set("z-index", "10")
                .set("position", "absolute");


        HorizontalLayout numemplLyt = new HorizontalLayout(txtNoEmpleado);
        numemplLyt.setWidthFull();

        HorizontalLayout nombreLyt = new HorizontalLayout(txtNombre, txtTitulo);
        nombreLyt.setWidthFull();
        nombreLyt.setPadding(false);
        txtNombre.setMaxWidth("50%");

        HorizontalLayout apellidosLyt = new HorizontalLayout(txtApPaterno, txtApMaterno);
        apellidosLyt.setWidthFull();
        apellidosLyt.setFlexGrow(1, txtApPaterno, txtApMaterno);

        HorizontalLayout fechasLyt = new HorizontalLayout(txtFechaIngreso, txtFechaBaja, txtEmail);
        fechasLyt.setWidthFull();

        HorizontalLayout deptoLyt = new HorizontalLayout(txtDepartamento, txtJefe);
        deptoLyt.setWidthFull();

        VerticalLayout card = new VerticalLayout(
                label = new H2("Buscar empleados"),
                ftoEmpl,
                numemplLyt,
                nombreLyt,
                apellidosLyt,
                fechasLyt,
                deptoLyt
        );

        card.getStyle()
                .set("position", "relative")
                .set("border-radius", "10px")
                .set("padding-right", "200px")
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

        txtNombre.setValue(safeString(e.getNombre()));
        txtNombre.setReadOnly(true);

        txtApPaterno.setValue(safeString(e.getApaterno()));
        txtApPaterno.setReadOnly(true);

        txtApMaterno.setValue(safeString(e.getAmaterno()));
        txtApMaterno.setReadOnly(true);

        txtTitulo.setValue(e.getTitulo());
        txtTitulo.setReadOnly(true);

        txtFechaIngreso.setValue(formatearFecha(e.getFechaingreso()));
        txtFechaIngreso.setReadOnly(true);

        txtFechaBaja.setValue(formatearFecha(e.getFechabaja()));
        txtFechaBaja.setReadOnly(true);

        txtDepartamento.setValue(safeString(e.getDepto()));
        txtDepartamento.setReadOnly(true);

        txtEmail.setValue(safeString(e.getEmail()));
        txtEmail.setReadOnly(true);

        txtJefe.setValue(safeString(e.getJefe()));
        txtJefe.setReadOnly(true);

        System.out.println("DEBUG: Nombre de foto en DTO: " + e.getFotoNombre());

        boolean fotoCargada = false;

        if (e.getFotoNombre() != null && !e.getFotoNombre().isEmpty()) {
            File file = new File(empleadoService.getRutaFotos() + e.getFotoNombre());
            System.out.println("DEBUG: Buscando archivo en: " + file.getAbsolutePath());
            System.out.println("DEBUG: ¿El archivo existe?: " + file.exists());

            if (file.exists()) {
                DownloadHandler handler = DownloadHandler.forFile(file);
                StreamRegistration registration = VaadinSession.getCurrent()
                                .getResourceRegistry()
                                        .registerResource(handler);
                ftoEmpl.setSrc(registration.getResourceUri().toString());
                ftoEmpl.setVisible(true);
                fotoCargada = true;
                System.out.println("DEBUG: URI generada: " + registration.getResourceUri());
            }
        } else {
            ftoEmpl.setVisible(false);
        }
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
        ftoEmpl.setVisible(false);
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

        /*
        grid.addColumn(EmpleadoDTO::getFechaingreso)
                        .setHeader("Fecha Ingreso")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getFechabaja)
                        .setHeader("Fecha Baja")
                                .setAutoWidth(true);

        grid.addColumn(EmpleadoDTO::getTitulo)
                        .setHeader("Titulo")
                                .setAutoWidth(true);
        */

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

    private void abrirDialogoSubida() {
        if (txtNoEmpleado.getValue() == null) {
            mostrarNotificacion("Primero busca un empleado", NotificationVariant.LUMO_WARNING, VaadinIcon.WARNING);
            return;
        }

        Long id = txtNoEmpleado.getValue().longValue();
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Subir foto");

        // CORRECCIÓN: La lambda recibe un solo objeto (UploadHandler.UploadRequest)
        UploadHandler handler = request -> {
            try {
                // Extraemos los datos del objeto request
                String fileName = request.getFileName();
                InputStream inputStream = request.getInputStream();

                String extension = fileName.substring(fileName.lastIndexOf("."));
                String nuevoNombre = "emp_" + id + extension;

                Path directorio = Path.of(empleadoService.getRutaFotos());
                // Aseguramos que la carpeta exista
                if (!Files.exists(directorio)) {
                    Files.createDirectories(directorio);
                }

                Path destino = directorio.resolve(nuevoNombre);

                // Transferencia de flujo (Stream)
                try (OutputStream outputStream = new FileOutputStream(destino.toFile())) {
                    inputStream.transferTo(outputStream);
                }

                // Actualización segura de la UI
                getUI().ifPresent(ui -> ui.access(() -> {
                    empleadoService.guardarNombreFoto(id, nuevoNombre);
                    buscarEmpleado();
                    mostrarNotificacion("Foto Actualizada", NotificationVariant.LUMO_SUCCESS, VaadinIcon.CHECK);
                    dialog.close();
                }));

            } catch (Exception ex) {
                getUI().ifPresent(ui -> ui.access(() -> {
                    mostrarNotificacion("Error al guardar: " + ex.getMessage(), NotificationVariant.LUMO_ERROR, VaadinIcon.CLOSE);
                }));
            }
        };

        Upload upload = new Upload(handler);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(2 * 1024 * 1024); // 2MB

        dialog.add(new VerticalLayout(new Text("Seleccione una imagen para el empleado " + id), upload));

        // Asegúrate de usar un botón local si 'btnConfirmar' no existe fuera de aquí
        Button btnCerrar = new Button("Cancelar", i -> dialog.close());
        dialog.getFooter().add(btnCerrar);
        dialog.open();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}