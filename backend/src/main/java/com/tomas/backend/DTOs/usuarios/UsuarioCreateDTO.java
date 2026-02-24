package com.tomas.backend.DTOs.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioCreateDTO {
    @NotBlank(message = "Debe ingresar un email, el campo esta vacio")
    @Email(message = "El formato del email no es valido")
    private String email;

    @NotBlank(message = "Debe ingresar una contraseña, el campo esta vacio")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "Debe ingresar un nombre, el campo esta vacio")
    @Size(min = 3 , max = 20)
    private String nombre;

    public UsuarioCreateDTO() {}


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
