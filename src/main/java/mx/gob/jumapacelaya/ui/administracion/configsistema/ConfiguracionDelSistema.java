package mx.gob.jumapacelaya.ui.administracion.configsistema;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import mx.gob.jumapacelaya.ui.MainLayout;

import java.util.List;

@PageTitle("Configuración del Sistema")
@Route(value = "configsistema", layout = MainLayout.class)
public class ConfiguracionDelSistema extends VerticalLayout {

    private static final String THEME_SESSION_KEY = "userTheme";
    private static final String THEME_LOCALSTORAGE_KEY = "appThemePreference";

    private final List<ColorOption> primaryTextColorOptions = List.of(
            new ColorOption("Color 1", "#A02142"),
            new ColorOption("Color 2", "#691B31"),
            new ColorOption("Color 3", "#BC955B"),
            new ColorOption("Color 4", "#DDC9A3"),
            new ColorOption("Color 5", "#6F7271")
    );

    private final List<ColorOption> primaryColorOptions = List.of(
            new ColorOption("Color 1", "#A02142"),
            new ColorOption("Color 2", "#691B31"),
            new ColorOption("Color 3", "#BC955B"),
            new ColorOption("Color 4", "#DDC9A3"),
            new ColorOption("Color 5", "#6F7271")
    );

    public ConfiguracionDelSistema() {
        setSpacing(true);
        add(new H2("⚙\uFE0F Configuración del Sistema"));

        loadAndAplyThemeOnStartup();
        loadCustomVariablesOnStartup();

        // Selector de modo claro u obscuro
        RadioButtonGroup<String> themeSelector = createThemeSelector();

        // Selector de colores de texto primarios
        ComboBox<ColorOption> primaryTextColorSelector = createPrimaryTextColorComboBox();
        primaryTextColorSelector.setWidth("250px");

        // Selector del color primario
        ComboBox<ColorOption> primaryColorSelector = createPrimaryColorComboBox();
        primaryColorSelector.setWidth("250px");

        HorizontalLayout layout1 = new HorizontalLayout(themeSelector);
        layout1.setWidthFull();

        HorizontalLayout layout2 = new HorizontalLayout(primaryColorSelector, primaryTextColorSelector);
        layout2.setWidthFull();

        VerticalLayout layoutPadre = new VerticalLayout(layout1,layout2);
        layoutPadre.getStyle()
                .set("border-radius", "10px")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.2)");
        add(layoutPadre);
    }

    private ComboBox<ColorOption> createPrimaryColorComboBox() {
        ComboBox<ColorOption> selector = new ComboBox<>();
        selector.setLabel("Color primario");
        selector.setItems(primaryColorOptions);
        selector.setClearButtonVisible(false);
        selector.setAllowCustomValue(false);

        final String cssVariable = "--lumo-primary-color";

        UI.getCurrent().getPage().executeJs(String.format("return localStorage.getItem('config:%s');", cssVariable))
                .then(String.class, savedHex -> {
                    ColorOption savedOption = primaryTextColorOptions.stream()
                            .filter(opt -> opt.getHexValue().equalsIgnoreCase(savedHex))
                            .findFirst()
                            .orElse(primaryTextColorOptions.get(0));

                    selector.setValue(savedOption);
                });

        selector.addValueChangeListener(event -> {
            ColorOption selectedOption = event.getValue();
            if (selectedOption != null) {
                applyCssVariable(cssVariable, selectedOption.getHexValue());
            }
        });

        selector.setRenderer(new ComponentRenderer<>(colorOption -> {
            Div div = new Div();
            div.setText(colorOption.getName());
            div.getStyle().set("display", "flex")
                    .set("align-items", "center");

            Div colorSwatch = new Div();
            colorSwatch.getStyle().set("background-color", colorOption.getHexValue())
                    .set("width","15px")
                    .set("height","15px")
                    .set("border-radius","50%")
                    .set("margin-right","10px")
                    .set("border","1px solid var(--lumo-border-color)");

            div.addComponentAsFirst(colorSwatch);
            return div;
        }));

        return selector;
    }

    private ComboBox<ColorOption> createPrimaryTextColorComboBox() {
        ComboBox<ColorOption> selector = new ComboBox<>();
        selector.setLabel("Color de los textos");
        selector.setItems(primaryTextColorOptions);
        selector.setClearButtonVisible(false);
        selector.setAllowCustomValue(false);

        final String cssVariable = "--lumo-primary-text-color";

        UI.getCurrent().getPage().executeJs(String.format("return localStorage.getItem('config:%s');", cssVariable))
                .then(String.class, savedHex -> {
                    ColorOption savedOption = primaryTextColorOptions.stream()
                            .filter(opt -> opt.getHexValue().equalsIgnoreCase(savedHex))
                            .findFirst()
                            .orElse(primaryTextColorOptions.get(0));

                    selector.setValue(savedOption);
                });

        selector.addValueChangeListener(event -> {
            ColorOption selectedOption = event.getValue();
            if (selectedOption != null) {
                applyCssVariable(cssVariable, selectedOption.getHexValue());
            }
        });

        selector.setRenderer(new ComponentRenderer<>(colorOption -> {
            Div div = new Div();
            div.setText(colorOption.getName());
            div.getStyle().set("display", "flex")
                    .set("align-items", "center");

            Div colorSwatch = new Div();
            colorSwatch.getStyle().set("background-color", colorOption.getHexValue())
                    .set("width","15px")
                    .set("height","15px")
                    .set("border-radius","50%")
                    .set("margin-right","10px")
                    .set("border","1px solid var(--lumo-border-color)");

            div.addComponentAsFirst(colorSwatch);
            return div;
        }));

        return selector;
    }

    private RadioButtonGroup<String> createThemeSelector() {
        RadioButtonGroup<String> themeSelector = new RadioButtonGroup<>();
        themeSelector.setLabel("Modo de Interfaz (Tema)");
        themeSelector.setItems("Claro (Predeterminado)", "Oscuro");

        String currentTheme = (String) VaadinSession.getCurrent().getAttribute(THEME_SESSION_KEY);
        if ("dark".equals(currentTheme)) {
            themeSelector.setValue("Oscuro");
        } else {
            themeSelector.setValue("Claro (Predeterminado)");
        }

        themeSelector.addValueChangeListener(event -> {
            String selection = event.getValue();
            String themeAttribute = selection.contains("Oscuro") ? "dark" : "";

            applyTheme(themeAttribute);
        });

        return themeSelector;
    }

    private void applyTheme(String themeAttribute) {
        String jsCommand = String.format("document.documentElement.setAttribute('theme', '%s');", themeAttribute);
        String localStorageCommand = String.format("localStorage.setItem('%s', '%s');", THEME_LOCALSTORAGE_KEY, themeAttribute);

        UI.getCurrent().getPage().executeJs(jsCommand + localStorageCommand);
        VaadinSession.getCurrent().setAttribute(THEME_SESSION_KEY, themeAttribute.isEmpty() ? "light" : "dark");
    }

    private void loadAndAplyThemeOnStartup() {
        String jsLoadCommand = String.format(
                "const savedTheme = localStorage.getItem('%s');" +
                        "if (savedTheme) { document.documentElement.setAttribute('theme', savedTheme); }" +
                        "return savedTheme;",
                THEME_LOCALSTORAGE_KEY
        );

        UI.getCurrent().getPage().executeJs(jsLoadCommand)
                .then(String.class, savedTheme -> {
                    if (savedTheme != null && !savedTheme.isEmpty()) {
                        VaadinSession.getCurrent().setAttribute(THEME_SESSION_KEY, "dark");
                    } else {
                        VaadinSession.getCurrent().setAttribute(THEME_SESSION_KEY, "light");
                    }
                });
    }

    public static class ColorOption {
        private final String name;
        private final String hexValue;

        public ColorOption(String name, String hexValue) {
            this.name = name;
            this.hexValue = hexValue;
        }

        public String getName() { return name; }
        public String getHexValue() { return hexValue; }

        @Override
        public String toString() { return name; }
    }


    private void applyCssVariable(String variableName, String colorValue) {
        String jsSetVar = String.format(
                "document.documentElement.style.setProperty('%s', '%s');",
                variableName,
                colorValue
        );

        String jsSetStorage = String.format(
                "localStorage.setItem('config:%s', '%s');",
                variableName,
                colorValue
        );

        UI.getCurrent().getPage().executeJs(jsSetVar + jsSetStorage);
        VaadinSession.getCurrent().setAttribute(variableName, colorValue);
    }

    private void loadCustomVariablesOnStartup() {
        final String[] customVariables = {
                "--lumo-primary-text-color",
                "--lumo-primary-color"
        };

        StringBuilder jsLoadScript = new StringBuilder();

        for (String cssVariable : customVariables) {
            String storageKey = "config:" + cssVariable;

            // Genera el código para leer y aplicar cada variable
            jsLoadScript.append(String.format(
                    "const saved_%1$s = localStorage.getItem('%2$s');" +
                            "if (saved_%1$s) { document.documentElement.style.setProperty('%3$s', saved_%1$s); }",
                    cssVariable.replace("-", "_"), // Crea un nombre de variable JS válido
                    storageKey,
                    cssVariable
            ));
        }

        UI.getCurrent().getPage().executeJs(jsLoadScript.toString());
    }
}
