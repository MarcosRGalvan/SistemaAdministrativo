package mx.com.marcoramirezg.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class ToolbarComponent extends HorizontalLayout {

    public ToolbarComponent(Component... components) {
        setWidthFull();
        setSpacing(true);
        setJustifyContentMode(JustifyContentMode.START);

        addClassName("toolbar");
        add(components);
    }

    public ToolbarComponent(List<Component> components) {
        this(components.toArray(new Component[0]));
    }
}
