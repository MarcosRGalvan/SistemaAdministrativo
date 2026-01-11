package mx.com.marcoramirezg.ui.login;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Login")
@Route(value = "login", autoLayout = false)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private final String localUser = "admin";
    private final String localPassword = "admin";

    public LoginView() {

        addClassName("login");
        setSizeFull();
        setPadding(false);
        setMargin(false);

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setMargin(false);

        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form form = i18n.getForm();
        form.setTitle("Sistema Administrativo");
        form.setUsername("Usuario");
        form.setPassword("Contraseña");
        form.setSubmit("Iniciar sesión");
        i18n.setForm(form);

        LoginI18n.ErrorMessage i8nError = i18n.getErrorMessage();
        i8nError.setTitle("Credenciales incorrectas");
        i8nError.setMessage("Usuario o contraseña incorrectos. Por favor, verifica tus datos");
        i18n.setErrorMessage(i8nError);

        i18n.setAdditionalInformation("Version 1.0.0 (Beta) - © 2025 Marco A. Ramirez");

        login.setAction("login");
        login.setI18n(i18n);

        VerticalLayout loginFormContainer = new VerticalLayout();
        Image image = new Image("images/logo.png", "Tu logo");
        loginFormContainer.add(image);
        loginFormContainer.add(login);
        loginFormContainer.setSizeUndefined();
        loginFormContainer.setPadding(false);
        loginFormContainer.setMargin(false);
        loginFormContainer.setAlignItems(Alignment.CENTER);
        loginFormContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        loginFormContainer.getElement().getThemeList().add("dark");

        mainLayout.add(loginFormContainer);
        mainLayout.setFlexGrow(1, loginFormContainer);
        mainLayout.setFlexGrow(2);
        add(mainLayout);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
