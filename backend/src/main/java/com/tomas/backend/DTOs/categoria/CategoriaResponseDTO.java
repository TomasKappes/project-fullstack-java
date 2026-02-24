package com.tomas.backend.DTOs.categoria;

public class CategoriaResponseDTO {
    private String nombre;
    private Long categoriaId;

    public CategoriaResponseDTO() {}

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }
    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
