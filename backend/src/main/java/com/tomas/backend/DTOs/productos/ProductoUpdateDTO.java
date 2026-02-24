package com.tomas.backend.DTOs.productos;

import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductoUpdateDTO {

    @Size (min = 2, max = 50)
    private String nombre;
    @Size(min = 1, max = 150)
    private String descripcion;
    @PositiveOrZero
    private Integer stock;
    @NotNull
    private Long categoriaId;
    @PositiveOrZero
    private BigDecimal precio;


    public ProductoUpdateDTO() {}


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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
