package pe.upn.sist1402a.repository.entitymanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio concreto que demuestra el acceso manual a datos con EntityManager (Semana 03).
 * Muestra el uso de la API estándar de JPA sin JpaRepository:
 * persist(), find(), merge(), remove() y createQuery().
 */
@Repository
@Transactional
public class ProductoEntityManagerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Producto guardar(Producto producto) {
        if (producto.getId() == null) {
            entityManager.persist(producto);
            return producto;
        } else {
            return entityManager.merge(producto);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        Producto producto = entityManager.find(Producto.class, id);
        return Optional.ofNullable(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return entityManager.createQuery("SELECT p FROM Producto p ORDER BY p.id ASC", Producto.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return entityManager.createQuery(
                "SELECT p FROM Producto p WHERE LOWER(p.categoria) = LOWER(:cat)", Producto.class)
                .setParameter("cat", categoria)
                .getResultList();
    }

    public void eliminar(Long id) {
        Producto producto = entityManager.find(Producto.class, id);
        if (producto != null) {
            entityManager.remove(producto);
        }
    }
}
