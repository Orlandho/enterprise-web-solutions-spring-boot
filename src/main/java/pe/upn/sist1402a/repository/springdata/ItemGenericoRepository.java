package pe.upn.sist1402a.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.upn.sist1402a.model.ItemGenerico;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemGenericoRepository extends JpaRepository<ItemGenerico, Long> {

    Optional<ItemGenerico> findByCodigoIdentificador(String codigoIdentificador);

    List<ItemGenerico> findByDenominacionContainingIgnoreCase(String denominacion);

    @Query("SELECT i FROM ItemGenerico i WHERE i.valorNumericoPrincipal >= :valorMinimo ORDER BY i.valorNumericoPrincipal DESC")
    List<ItemGenerico> buscarPorValorMinimo(@Param("valorMinimo") Double valorMinimo);
}
