package com.tomas.backend.DTOs.productos;

import jakarta.validation.constraints.NotBlank;

public class ProductoRequestDTO {

    @NotBlank(message = "El producto debe tener un nombre")
    private String nombre;



   public ProductoRequestDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {

       this.nombre = nombre;
    }


}

