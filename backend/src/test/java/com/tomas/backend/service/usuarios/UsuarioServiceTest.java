package com.tomas.backend.service.usuarios;

import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.UsuarioMapper;
import com.tomas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deberiaObtenerUsuarioPorId() {

        Long id = 1L;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre("Tomás");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(id);
        responseDTO.setNombre("Tomás");

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.obtenerUsuario(id);

        assertEquals(id, resultado.getId());
        assertEquals("Tomás", resultado.getNombre());

        verify(usuarioRepository).findById(id);

        verify(usuarioMapper).toResponseDTO(usuario);
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {

        Long id = 10L;

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuario(id)
        );

        verify(usuarioRepository).findById(id);
    }

}
