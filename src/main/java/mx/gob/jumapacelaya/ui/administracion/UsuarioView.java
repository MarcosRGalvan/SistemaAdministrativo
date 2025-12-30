package mx.gob.jumapacelaya.ui.administracion;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import mx.gob.jumapacelaya.ui.MainLayout;

@PageTitle("Usuarios")
@Route(value = "usuarios", layout = MainLayout.class)
@RolesAllowed( value = "ADMIN")
public class UsuarioView extends VerticalLayout {

}
