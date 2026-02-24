package com.tomas.backend.DTOs.productos;


import java.math.BigDecimal;

public class ProductoResponseDTO {

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Long categoriaId;

    public ProductoResponseDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Long getCategoria() {
        return categoriaId;
    }

    public void setCategoria(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
