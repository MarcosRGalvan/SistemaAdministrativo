package mx.com.marcoramirezg.ui.administracion;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import mx.com.marcoramirezg.ui.MainLayout;

@PageTitle("Empleados")
@Route(value = "empleados", layout = MainLayout.class)
@RolesAllowed(value = "ADMIN")
public class EmpleadosView extends VerticalLayout {
}
