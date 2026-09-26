package pe.upn.sist1402a.model;

import jakarta.persistence.*;

/**
 * Entidad JPA para el Caso 2: Almacén e Inventario de Productos.
 * Incluye @NamedQuery para búsqueda por stock mínimo y @Transient para estadoStock.
 */
@Entity
@Table(name = "productos")
@NamedQuery(
    name = "Producto.buscarConStockMinimo",
    query = "SELECT p FROM Producto p WHERE p.stock >= :stockMinimo ORDER BY p.stock DESC"
)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 30, unique = true)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    /**
     * Campo dinámico calculado en memoria.
     * Si stock < 5 -> INSUFICIENTE, caso contrario -> OK.
     */
    @Transient
    private String estadoStock;

    public Producto() {
    }

    public Producto(Long id, String codigo, String nombre, Double precio, Integer stock, String categoria) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstadoStock() {
        if (this.stock == null) {
            return "DESCONOCIDO";
        }
        return this.stock < 5 ? "INSUFICIENTE" : "OK";
    }

    public void setEstadoStock(String estadoStock) {
        this.estadoStock = estadoStock;
    }
}
