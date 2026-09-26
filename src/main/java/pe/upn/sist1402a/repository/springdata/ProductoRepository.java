package pe.upn.sist1402a.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.upn.sist1402a.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para Producto (Semanas 04 a 06).
 * Incluye consultas derivadas, @Query con rango de precio y enlace con @NamedQuery.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByStockLessThan(int limite);

    /**
     * Consulta JPQL con rango de precios y parámetros seguros :minimo y :maximo.
     */
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minimo AND :maximo ORDER BY p.precio ASC")
    List<Producto> buscarPorRangoPrecio(@Param("minimo") Double minimo, @Param("maximo") Double maximo);

    /**
     * Vinculación directa con la @NamedQuery declarada en la entidad Producto ("Producto.buscarConStockMinimo").
     */
    List<Producto> buscarConStockMinimo(@Param("stockMinimo") Integer stockMinimo);
}
