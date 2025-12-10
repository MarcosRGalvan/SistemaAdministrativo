package mx.gob.jumapacelaya.repositories;

import mx.gob.jumapacelaya.dto.MenuDetalleDTO;
import mx.gob.jumapacelaya.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // 1. OBTIENE TODOS LOS ELEMENTOS ORDENADOS POR EL CAMPO ORDENMENU
    List<MenuItem> findAllByOrderByOrdenmenuAsc();

    // 2. MÉTODO PARA OBTENER SOLO LOS MENUS PRINCIPALES
    List<MenuItem> findByPadreidIsNullOrderByOrdenmenuAsc();

    // 3. METODO PARA OBTENER LOS MENUS HIJOS DE UN PADRE ESPECIFICO
    List<MenuItem> findByPadreidOrderByOrdenmenuAsc(Long padreid);

    // 4. METODO PARA OBTENER LOS MENUS UNIENDO OTRAS TABLAS A LA CONSULTA
    @Query( value = """
        SELECT
            m.MENUID,
            m.DESCRIPCION,
            m.ESTADO,
            f.DESCRIPCION formulario,
            padre.DESCRIPCION padre,
            m.ORDENMENU,
            m.TIPO
        FROM
            ADMIN.MENU m
        LEFT JOIN ADMIN.MENU padre ON padre.MENUID = m.PADREID
        LEFT JOIN ADMIN.FORMULARIOS f ON f.FORMULARIOID = m.FORMULARIOID
        ORDER BY
            m.MENUID ASC
    """, nativeQuery = true)
    List<MenuDetalleDTO> findMenuDetails();
}
