package pe.upn.sist1402a.dto;

import jakarta.validation.constraints.*;

public class ItemGenericoDto {

    @NotBlank(message = "El código identificador es obligatorio")
    @Size(min = 2, max = 40, message = "El código debe tener entre 2 y 40 caracteres")
    private String codigoIdentificador;

    @NotBlank(message = "La denominación es obligatoria")
    @Size(min = 2, max = 120, message = "La denominación debe tener entre 2 y 120 caracteres")
    private String denominacion;

    @NotNull(message = "El valor numérico principal es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor numérico principal no puede ser negativo")
    private Double valorNumericoPrincipal;

    @NotNull(message = "La cantidad entera es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadEntera;

    public ItemGenericoDto() {
    }

    public ItemGenericoDto(String codigoIdentificador, String denominacion, Double valorNumericoPrincipal, Integer cantidadEntera) {
        this.codigoIdentificador = codigoIdentificador;
        this.denominacion = denominacion;
        this.valorNumericoPrincipal = valorNumericoPrincipal;
        this.cantidadEntera = cantidadEntera;
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
}
