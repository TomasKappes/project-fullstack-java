package com.tomas.backend.mappers;


import com.tomas.backend.DTOs.usuarios.UsuarioCreateDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioUpdateDTO;
import com.tomas.backend.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioMapper() {}

    public Usuario toEntity(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioCreateDTO.getEmail());
        usuario.setNombre(usuarioCreateDTO.getNombre());
        usuario.setPassword(usuarioCreateDTO.getPassword());

        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setEmail(usuario.getEmail());
        usuarioResponseDTO.setNombre(usuario.getNombre());


        return usuarioResponseDTO;
    }

    public Usuario toUpdateEntity(UsuarioUpdateDTO usuarioUpdateDTO) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioUpdateDTO.getEmail());
        usuario.setNombre(usuarioUpdateDTO.getNombre());
        usuario.setPassword(usuarioUpdateDTO.getPassword());
        return usuario;
    }

}
