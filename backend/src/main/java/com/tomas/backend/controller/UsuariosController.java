package com.tomas.backend.controller;
import com.tomas.backend.DTOs.usuarios.UsuarioCreateDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioRequestDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioUpdateDTO;
import com.tomas.backend.service.usuarios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UsuariosController {
    private final UsuarioService usuarioService;

    public UsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{idUsuario}")
    public UsuarioResponseDTO findById(@PathVariable Long idUsuario) {
        return usuarioService.obtenerUsuario(idUsuario);
    }

    @GetMapping
    public List<UsuarioResponseDTO> findAll() {
        return usuarioService.listaUsuarios();
    }


    @PutMapping("Update/{idUsuario}")
    public UsuarioResponseDTO update(@PathVariable Long idUsuario,@Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        return usuarioService.actualizarUsuario(idUsuario, usuarioUpdateDTO);
    }

    @DeleteMapping("Delete/{idUsuario}")
    public void delete(@PathVariable Long idUsuario) {
     usuarioService.eliminarUsuario(idUsuario);
    }


}
