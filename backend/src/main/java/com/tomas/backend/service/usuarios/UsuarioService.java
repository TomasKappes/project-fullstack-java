package com.tomas.backend.service.usuarios;
import com.tomas.backend.DTOs.usuarios.UsuarioCreateDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioRequestDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioUpdateDTO;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.mappers.UsuarioMapper;
import com.tomas.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = usuarioMapper.toEntity(usuarioCreateDTO);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioGuardado);
    }

    public UsuarioResponseDTO obtenerUsuario(Long idUsuario) {
     Usuario optUsuario = usuarioRepository.findById(idUsuario)
             .orElseThrow( () ->new RuntimeException("Usuario no encontrado"));
     return usuarioMapper.toResponseDTO(optUsuario);
    }

    public List<UsuarioResponseDTO> listaUsuarios() {
       List<UsuarioResponseDTO> listaUsuarios = new ArrayList<>();
       for (Usuario usuario : usuarioRepository.findAll()) {
           listaUsuarios.add(usuarioMapper.toResponseDTO(usuario));
       }

       return listaUsuarios;
    }

    public UsuarioResponseDTO obtenerUsuarioPorEmail(String email) {
        Usuario optUsuario =  usuarioRepository.findByEmail(email)
                .orElseThrow( () ->new RuntimeException("Usuario con este email no encontrado"));

        return usuarioMapper.toResponseDTO(optUsuario);
    }

    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(idUsuario);
    }

    public UsuarioResponseDTO actualizarUsuario(Long idUsuario, UsuarioUpdateDTO usuarioUpdateDTO) {
        Usuario optUsuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        optUsuario.setEmail(usuarioUpdateDTO.getEmail());
        optUsuario.setNombre(usuarioUpdateDTO.getNombre());
        optUsuario.setPassword(usuarioUpdateDTO.getPassword());

        Usuario usuarioActualizado = usuarioRepository.save(optUsuario);

       return usuarioMapper.toResponseDTO(usuarioActualizado);
    }


    public UsuarioResponseDTO loginUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        Usuario optUsuario = usuarioRepository.findByEmail(usuarioRequestDTO.getEmail())
                .orElseThrow( () ->new RuntimeException("No existe un usuario registrado con este email"));

        if (!optUsuario.getPassword().equals(usuarioRequestDTO.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }


        return usuarioMapper.toResponseDTO(optUsuario);

    }

    public UsuarioResponseDTO registrarUsuario(UsuarioCreateDTO usuarioCreateDTO) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(usuarioCreateDTO.getEmail());

        if (usuarioOpt.isPresent()) {
            throw new RuntimeException("Este Usuario ya existe");
        }

        return crearUsuario(usuarioCreateDTO);
    }

}
