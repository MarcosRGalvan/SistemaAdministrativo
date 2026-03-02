package mx.com.marcoramirezg.ui.administracion.configsistema;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import mx.com.marcoramirezg.ui.MainLayout;

import java.util.List;

@PageTitle("Configuración del Sistema")
@Route(value = "configsistema", layout = MainLayout.class)
@RolesAllowed( value = "ADMIN")
public class ConfiguracionDelSistema extends VerticalLayout {

    private static final String THEME_LOCALSTORAGE_KEY = "appThemePreference";
    private boolean initializing = true;

    private final List<ColorOptions> primaryTextColorOptions = List.of(
            new ColorOptions("Big Dip O´Ruby", "#A02142"),
            new ColorOptions("Claret", "#691B31"),
            new ColorOptions("Aztec Gold", "#BC955B"),
            new ColorOptions("Lion", "#DDC9A3"),
            new ColorOptions("Nickel", "#6F7271")
    );

    private final List<ColorOptions> primaryColorOptions = List.of(
            new ColorOptions("Big Dip O´Ruby", "#A02142"),
            new ColorOptions("Claret", "#691B31"),
            new ColorOptions("Aztec Gold", "#BC955B"),
            new ColorOptions("Lion", "#DDC9A3"),
            new ColorOptions("Nickel", "#6F7271")
    );

    public ConfiguracionDelSistema() {
        setSpacing(true);
        add(new H2("⚙\uFE0F Configuración del Sistema"));

        loadCssVariablesFromLocalStorage();

        RadioButtonGroup<String> themeSelector = createThemeSelector();
        ComboBox<ColorOptions> primaryColor = createColorCombo(
                "Color primario",
                "--lumo-primary-color",
                primaryColorOptions
        );

        ComboBox<ColorOptions> primaryTextColor = createColorCombo(
                "Color de textos",
                "--lumo-primary-text-color",
                primaryTextColorOptions
        );

        HorizontalLayout row1 = new HorizontalLayout(themeSelector);
        HorizontalLayout row2 = new HorizontalLayout(primaryColor, primaryTextColor);

        VerticalLayout layoutPadre = new VerticalLayout(row1, row2);
        layoutPadre.getStyle()
                .set("border-radius", "10px")
                .set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");

        add(layoutPadre);

        UI.getCurrent().getPage().executeJs("return true;")
                .then(Boolean.class, ok -> initializing = false);
    }

    private RadioButtonGroup<String> createThemeSelector() {
        RadioButtonGroup<String> selector = new RadioButtonGroup<>("Modo de interfaz");
        selector.setItems("Claro", "Oscuro");

        UI.getCurrent().getPage().executeJs(
                "return localStorage.getItem($0)", THEME_LOCALSTORAGE_KEY
        ).then(String.class, theme ->
                selector.setValue("dark".equals(theme) ? "Oscuro" : "Claro")
        );

        selector.addValueChangeListener(e -> {
            if (initializing) return;

            boolean dark = "Oscuro".equals(e.getValue());
            UI.getCurrent().getPage().executeJs(
                    dark
                            ? "document.documentElement.setAttribute('theme','dark')"
                            : "document.documentElement.removeAttribute('theme')"
            );
            UI.getCurrent().getPage().executeJs(
                    "localStorage.setItem($0,$1)",
                    THEME_LOCALSTORAGE_KEY,
                    dark ? "dark" : "light"
            );
        });

        return selector;
    }

    private ComboBox<ColorOptions> createColorCombo(
            String label,
            String cssVariable,
            List<ColorOptions> options
    ) {
        ComboBox<ColorOptions> combo = new ComboBox<>(label);
        combo.setItems(options);
        combo.setWidth("250px");
        combo.setAllowCustomValue(false);

        UI.getCurrent().getPage().executeJs(
                "return localStorage.getItem($0)",
                "config:" + cssVariable
        ).then(String.class, saved -> {
            options.stream()
                    .filter(o -> o.getHexValue().equalsIgnoreCase(saved))
                    .findFirst()
                    .ifPresent(combo::setValue);
        });

        combo.addValueChangeListener(e -> {
            if (initializing || e.getValue() == null) return;

            applyCssVariable(cssVariable, e.getValue().getHexValue());
        });

        combo.setRenderer(colorRenderer());
        return combo;
    }

    private void applyCssVariable(String variable, String value) {
        UI.getCurrent().getPage().executeJs(
                """
                document.documentElement.style.setProperty($0,$1);
                localStorage.setItem($2,$1);
                """,
                variable,
                value,
                "config:" + variable
        );
    }

    private void loadCssVariablesFromLocalStorage() {
        UI.getCurrent().getPage().executeJs(
                """
                ['--lumo-primary-color','--lumo-primary-text-color'].forEach(v=>{
                    const val = localStorage.getItem('config:'+v);
                    if(val) document.documentElement.style.setProperty(v,val);
                });
                """
        );
    }

    private ComponentRenderer<Div, ColorOptions> colorRenderer() {
        return new ComponentRenderer<>(opt -> {
            Div wrapper = new Div();
            wrapper.getStyle().set("display", "flex").set("align-items", "center");

            Div dot = new Div();
            dot.getStyle()
                    .set("width", "14px")
                    .set("height", "14px")
                    .set("border-radius", "50%")
                    .set("margin-right", "8px")
                    .set("background-color", opt.getHexValue())
                    .set("border", "1px solid var(--lumo-border-color)");

            wrapper.add(dot);
            wrapper.add(opt.getName());
            return wrapper;
        });
    }

    public static class ColorOptions {
        private final String name;
        private final String hexValue;

        public ColorOptions(String name, String hexValue) {
            this.name = name;
            this.hexValue = hexValue;
        }

        public String getName() { return name; }
        public String getHexValue() { return hexValue; }

        @Override
        public String toString() {
            return name;
        }
    }
}
