package mx.gob.jumapacelaya.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import mx.gob.jumapacelaya.entity.MenuItem;
import mx.gob.jumapacelaya.services.MenuService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

    private final MenuService menuService;
    private final Map<Long, List<MenuItem>> menuTree;

    public MainLayout(MenuService menuService) {
        this.menuService = menuService;
        this.menuTree = menuService.getMenuItems().stream()
                .collect(Collectors.groupingBy(item -> item.getPadreid() == null ? 0L : item.getPadreid()));

        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("MyAPP");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        addToNavbar(toggle, title);

        SideNav nav = createPrimaryNavigation();

        addToDrawer(nav);
    }

    private SideNav createPrimaryNavigation() {
        SideNav nav = new SideNav();

        List<MenuItem> topLevelItems = menuTree.getOrDefault(0L, List.of());

        for (MenuItem item : topLevelItems) {
            SideNavItem topItem = buildSideNav(item);
            nav.addItem(topItem);
        }

        return nav;
    }

    private SideNavItem buildSideNav(MenuItem currentItem) {

        SideNavItem item;

        Long formularioId = currentItem.getFormularioid();

        Optional<String> classNameOptionl = menuService.getClasePorFormularioId(formularioId);

        Class<?> targetClass = null;
        if (classNameOptionl.isPresent()) {
            try {
                targetClass = Class.forName(classNameOptionl.get());
            } catch (ClassNotFoundException e) {
                System.out.println("No se pudo cargar la clase: " + classNameOptionl.get());
            }
        }

        boolean isComponent = (targetClass != null &&
                Component.class.isAssignableFrom(targetClass));

        if (isComponent) {
            @SuppressWarnings("unchecked")
            Class<? extends Component> componentClass = (Class<? extends Component>) targetClass;
            item = new SideNavItem(currentItem.getDescripcion(), componentClass);
        } else {
            item = new SideNavItem(currentItem.getDescripcion());
        }

        String nombreIcono = currentItem.getIcono();
        if (nombreIcono != null && !nombreIcono.isEmpty()) {
            try {
                VaadinIcon vIcon = VaadinIcon.valueOf(nombreIcono.toUpperCase());
                item.setPrefixComponent(vIcon.create());
            } catch (IllegalArgumentException e) {
                item.setPrefixComponent(VaadinIcon.QUESTION_CIRCLE.create());
            }
        } else {
            item.setPrefixComponent(VaadinIcon.OPTION_A.create());
        }

        List<MenuItem> children = menuTree.get(currentItem.getMenuid());

        if (children != null && !children.isEmpty()) {
            for (MenuItem child : children) {
                item.addItem(buildSideNav(child));
            }
        }

        return item;
    }
}
