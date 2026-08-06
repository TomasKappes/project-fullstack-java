package com.tomas.backend.service.usuarios;

import com.tomas.backend.DTOs.auth.AuthResponse;
import com.tomas.backend.DTOs.auth.LoginRequest;
import com.tomas.backend.DTOs.auth.RegisterRequest;
import com.tomas.backend.DTOs.auth.RegisterResponse;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.Roles;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.UsuarioRepository;
import com.tomas.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void deberiaHacerLoginConCredencialesValidasYDevolverTokenYUsuarioId() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("tomas@mail.com");
        request.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Tomás");
        usuario.setEmail("tomas@mail.com");
        usuario.setPassword("hash123");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("token123");

        // Act
        AuthResponse authResponse = authService.login(request);

        // Assert
        assertEquals("token123", authResponse.getToken());
        assertEquals(1L, authResponse.getUsuarioId());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(usuario);
    }

    @Test
    void deberiaLanzarResourceNotFoundAlLoginCuandoUsuarioNoExiste() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@mail.com");
        request.setPassword("password123");

        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );

        assertEquals("Usuario no encontrado", excepcion.getMessage());
    }

    @Test
    void deberiaPropagarBadCredentialsCuandoLaAutenticacionFalla() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("tomas@mail.com");
        request.setPassword("passwordIncorrecta");

        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        // Act & Assert
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(usuarioRepository, never()).findByEmail(any());
    }

    @Test
    void deberiaRegistrarUsuarioCorrectamente() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Tomás");
        request.setEmail("tomas@mail.com");
        request.setPassword("password123");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashEncodeado");

        // Act
        RegisterResponse registerResponse = authService.register(request);

        // Assert
        assertEquals("Usuario registrado con exito", registerResponse.getMessage());

        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any());
    }

    @Test
    void deberiaLanzarConflictAlRegistrarEmailExistente() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Tomás");
        request.setEmail("tomas@mail.com");
        request.setPassword("password123");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(1L);
        usuarioExistente.setEmail("tomas@mail.com");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuarioExistente));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> authService.register(request)
        );

        assertEquals("El email ya está registrado", excepcion.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deberiaEncodearLaPasswordAntesDeGuardar() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Tomás");
        request.setEmail("tomas@mail.com");
        request.setPassword("password123");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashEncodeado");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals("hashEncodeado", captor.getValue().getPassword());
        assertEquals(Roles.USER, captor.getValue().getRol());
        assertEquals("Tomás", captor.getValue().getNombre());
        assertEquals("tomas@mail.com", captor.getValue().getEmail());
    }
}
