package com.tomas.backend.DTOs.categoria;


import jakarta.validation.constraints.NotBlank;

public class CategoriaCreateDTO {
    @NotBlank
    private String nombre;

    public CategoriaCreateDTO() {}

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
