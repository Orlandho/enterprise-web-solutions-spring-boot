package pe.upn.sist1402a.model;

import jakarta.persistence.*;

/**
 * Entidad JPA para el Caso 3: Plantilla Comodín / Activos Tecnológicos ODS 9.
 * Diseñada para adaptarse en menos de 5 minutos a cualquier enunciado del examen.
 */
@Entity
@Table(name = "items_genericos")
public class ItemGenerico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_identificador", nullable = false, length = 40, unique = true)
    private String codigoIdentificador;

    @Column(name = "denominacion", nullable = false, length = 120)
    private String denominacion;

    @Column(name = "valor_numerico_principal", nullable = false)
    private Double valorNumericoPrincipal;

    @Column(name = "cantidad_entera", nullable = false)
    private Integer cantidadEntera;

    /**
     * Campo calculado en memoria: Clasificación dinámica.
     */
    @Transient
    private String clasificacionCalculada;

    public ItemGenerico() {
    }

    public ItemGenerico(Long id, String codigoIdentificador, String denominacion, Double valorNumericoPrincipal, Integer cantidadEntera) {
        this.id = id;
        this.codigoIdentificador = codigoIdentificador;
        this.denominacion = denominacion;
        this.valorNumericoPrincipal = valorNumericoPrincipal;
        this.cantidadEntera = cantidadEntera;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoIdentificador() {
        return codigoIdentificador;
    }

    public void setCodigoIdentificador(String codigoIdentificador) {
        this.codigoIdentificador = codigoIdentificador;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public Double getValorNumericoPrincipal() {
        return valorNumericoPrincipal;
    }

    public void setValorNumericoPrincipal(Double valorNumericoPrincipal) {
        this.valorNumericoPrincipal = valorNumericoPrincipal;
    }

    public Integer getCantidadEntera() {
        return cantidadEntera;
    }

    public void setCantidadEntera(Integer cantidadEntera) {
        this.cantidadEntera = cantidadEntera;
    }

    public String getClasificacionCalculada() {
        if (this.cantidadEntera == null) {
            return "SIN_REGISTRO";
        }
        return this.cantidadEntera < 5 ? "CRITICO" : "OPTIMO";
    }

    public void setClasificacionCalculada(String clasificacionCalculada) {
        this.clasificacionCalculada = clasificacionCalculada;
    }
}
