package com.tomas.backend.security;

import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.Roles;
import com.tomas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Tomás");
        usuario.setEmail("tomas@mail.com");
        usuario.setPassword("hash");
        usuario.setRol(Roles.USER);
        return usuario;
    }

    @Test
    void deberiaDevolverUserDetailsCuandoElUsuarioExiste() {
        // Arrange
        Usuario usuario = crearUsuario();
        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("tomas@mail.com");

        // Assert
        assertEquals("tomas@mail.com", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Roles.USER.name())));
    }

    @Test
    void deberiaLanzarUsernameNotFoundCuandoElUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException excepcion = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("noexiste@mail.com")
        );

        assertEquals("Usuario no encontrado", excepcion.getMessage());
    }
}
