package mx.gob.jumapacelaya.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import mx.gob.jumapacelaya.entity.MenuItem;
import mx.gob.jumapacelaya.security.SecurityService;
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
    private SecurityService securityService;

    public MainLayout(MenuService menuService, SecurityService securityService) {
        this.securityService = securityService;
        this.menuService = menuService;
        this.menuTree = menuService.getMenuItems().stream()
                .collect(Collectors.groupingBy(item -> item.getPadreid() == null ? 0L : item.getPadreid()));


        addToNavbar(createHeader());

        SideNav nav = createPrimaryNavigation();
        addToDrawer(nav);
    }

    private Component createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("My App");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        HorizontalLayout userArea = new HorizontalLayout();
        userArea.setAlignItems(FlexComponent.Alignment.END);
        userArea.setSpacing(true);

        if (securityService.getAuthenticatedUser() != null) {
            String username = securityService.getAuthenticatedUser().getUsername();

            Avatar avatar = new Avatar();
            avatar.getStyle().set("cursor", "pointer");

            MenuBar menuBar = new MenuBar();
            menuBar.setThemeName("tertiary-inline");

            com.vaadin.flow.component.contextmenu.MenuItem userItem = menuBar.addItem(avatar);
            userItem.getSubMenu().addItem("Cerrar Sesión", e -> securityService.logout());

            userArea.add(menuBar);
        }

        HorizontalLayout header = new HorizontalLayout(toggle, title, userArea);
        header.expand(title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.getStyle().set("padding", "0 20px 0 10px");

        return header;
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
